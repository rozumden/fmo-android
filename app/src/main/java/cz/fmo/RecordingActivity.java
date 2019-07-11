package cz.fmo;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.lang.ref.WeakReference;

import cz.fmo.camera.CameraThread;
import cz.fmo.camera.PreviewCameraTarget;
import cz.fmo.camera.RecordingCameraTarget;
import cz.fmo.data.Assets;
import cz.fmo.data.TrackSet;
import cz.fmo.recording.AutomaticRecordingTask;
import cz.fmo.recording.CyclicBuffer;
import cz.fmo.recording.EncodeThread;
import cz.fmo.recording.ManualRecordingTask;
import cz.fmo.recording.SaveThread;
import cz.fmo.util.Config;
import cz.fmo.util.FileManager;

/**
 * The main activity, facilitating video preview, encoding and saving.
 */
public final class RecordingActivity extends Activity {
    private static final String LOG_TAG = "FMO";
    private static final float BUFFER_SECONDS = 8;
    private static final float AUTOMATIC_MARGIN = 2;
    private static final int PREVIEW_SLOWDOWN_FRAMES = 59;
    private static final String FILENAME = "video.mp4";
    private final Handler mHandler = new Handler(this);
    private final GUI mGUI = new GUI();
    private final FileManager mFileMan = new FileManager(this);
    private Config mConfig;
    private Status mStatus = Status.STOPPED;
    private CameraThread mCamera;
    private EncodeThread mEncode;
    private SaveThread mSaveMovie;
    private SaveThread.Task mSaveTask;
    private RecordingCameraTarget mEncodeTarget;
    private PreviewCameraTarget mPreviewTarget;

    @Override
    protected void onCreate(android.os.Bundle savedBundle) {
        super.onCreate(savedBundle);
        mGUI.init();
        mGUI.update(GUIUpdate.ALL);
    }

    /**
     * Responsible for querying and acquiring camera permissions. Whatever the response will be,
     * the permission request could result in the application being paused and resumed. For that
     * reason, requesting permissions at any later point, including in onResume(), might cause an
     * infinite loop.
     */
    @Override
    protected void onStart() {
        super.onStart();
        if (isCameraPermissionDenied() || isStoragePermissionDenied()) {
            String[] perms = new String[]{Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE};
            ActivityCompat.requestPermissions(this, perms, 0);
        }
    }

    /**
     * Queries the camera permission status.
     */
    private boolean isCameraPermissionDenied() {
        int permissionStatus = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        return permissionStatus != PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Queries the storage permission status.
     */
    private boolean isStoragePermissionDenied() {
        int permissionStatus = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return permissionStatus != PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Called when a decision has been made regarding the camera permission. Whatever the response
     * is, the initialization procedure continues. If the permission is denied, the init() method
     * will display a proper error message on the screen.
     */
    @Override
    public void onRequestPermissionsResult(int requestID, @NonNull String[] permissionList,
                                           @NonNull int[] grantedList) {
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        init();
    }

    /**
     * The main initialization step. There are multiple callers of this method, but mechanisms are
     * put into place so that the actual initialization happens exactly once. There is no need for
     * a mutex, assuming that all entry points are run by the main thread.
     * <p>
     * Called by:
     * - onResume()
     * - GUI.surfaceCreated(), when GUI preview surface has just been created
     * - onRequestPermissionsResult(), when camera permissions have just been granted
     */
    private void init() {
        // reality check: don't initialize twice
        if (mStatus == Status.RUNNING) return;

        // stop if the preview surface has not been created yet
        if (!mGUI.isPreviewReady()) return;

        // stop if permissions haven't been granted
        if (isCameraPermissionDenied()) {
            mStatus = Status.CAMERA_PERMISSION_ERROR;
            mGUI.update(GUIUpdate.LABELS);
            return;
        }

        if (isStoragePermissionDenied()) {
            mStatus = Status.STORAGE_PERMISSION_ERROR;
            mGUI.update(GUIUpdate.LABELS);
            return;
        }

        // get configuration settings
        mConfig = new Config(this);

        // set up assets
        Assets.getInstance().load(this);

        // set up track set
        TrackSet.getInstance().setConfig(mConfig);

        // create a dedicated camera input thread
        mCamera = new CameraThread(mHandler, mConfig);

        // add preview as camera target
        mPreviewTarget = new PreviewCameraTarget(mGUI.getPreviewSurface(),
                mGUI.getPreviewWidth(), mGUI.getPreviewHeight());
        mCamera.addTarget(mPreviewTarget);

        if (mConfig.slowPreview) {
            mPreviewTarget.setSlowdown(PREVIEW_SLOWDOWN_FRAMES);
        }

        if (mConfig.recordMode != Config.RecordMode.OFF) {
            // make a suitably-sized cyclic buffer
            CyclicBuffer buffer = new CyclicBuffer(mCamera.getBitRate(), mCamera.getFrameRate(),
                    BUFFER_SECONDS);

            // create dedicated encoding and video saving threads
            mEncode = new EncodeThread(mCamera.getMediaFormat(), buffer, mHandler);
            mSaveMovie = new SaveThread(buffer, mHandler);

            // add encoder as camera target
            mEncodeTarget = new RecordingCameraTarget(mEncode.getInputSurface(),
                    mCamera.getWidth(), mCamera.getHeight());
            mCamera.addTarget(mEncodeTarget);

            // only allow encoding in automatic mode; manual mode starts encoding once the recording
            // button is pressed
            setEncodingEnabled(mConfig.recordMode == Config.RecordMode.AUTOMATIC);
        }

        if (!mConfig.disableDetection) {
            // C++ initialization
            Lib.detectionStart(mCamera.getWidth(), mCamera.getHeight(), mConfig.procRes,
                    mConfig.gray, mHandler);
        }

        // refresh GUI
        mStatus = Status.RUNNING;
        mGUI.update(GUIUpdate.ALL);

        // start threads
        if (mEncode != null) mEncode.start();
        if (mSaveMovie != null) mSaveMovie.start();
        mCamera.start();
    }

    /**
     * Perform cleanup after the activity has been paused.
     */
    @Override
    protected void onPause() {
        super.onPause();

        Lib.detectionStop();

        if (mCamera != null) {
            mCamera.getHandler().sendKill();
            try {
                mCamera.join();
            } catch (InterruptedException ie) {
                throw new RuntimeException("Interrupted when closing CameraThread");
            }
            mCamera = null;
        }

        stopSaving();

        if (mSaveMovie != null) {
            mSaveMovie.getHandler().sendKill();
            try {
                mSaveMovie.join();
            } catch (InterruptedException ie) {
                throw new RuntimeException("Interrupted when closing SaveThread");
            }
            mSaveMovie = null;
        }

        if (mEncode != null) {
            mEncode.getHandler().sendKill();
            try {
                mEncode.join();
            } catch (InterruptedException ie) {
                throw new RuntimeException("Interrupted when closing EncodeThread");
            }
            mEncode = null;
        }

        TrackSet.getInstance().clear();

        mStatus = Status.STOPPED;
    }

    private void triggerAutomaticRecording() {
        if (mStatus != Status.RUNNING) return;
        if (mSaveMovie == null) return;
        if (mConfig.recordMode != Config.RecordMode.AUTOMATIC) return;

        boolean extended = (mSaveTask != null) && mSaveTask.extend();

        if (!extended) {
            File outFile = mFileMan.open(FILENAME);
            mSaveTask = new AutomaticRecordingTask(AUTOMATIC_MARGIN, outFile, mSaveMovie);
        }

        mGUI.update(GUIUpdate.BUTTONS); // GUI thread only
    }

    public void onForceAutomaticRecording(@SuppressWarnings("UnusedParameters") View view) {
        triggerAutomaticRecording();
    }

    public void onStartManualRecording(@SuppressWarnings("UnusedParameters") View view) {
        if (mSaveMovie == null) return;
        if (mStatus != Status.RUNNING) return;
        if (mSaveTask != null) return;
        setEncodingEnabled(true);
        File outFile = mFileMan.open(FILENAME);
        mSaveTask = new ManualRecordingTask(outFile, mSaveMovie);
        mGUI.update(GUIUpdate.BUTTONS);
    }

    public void onStopManualRecording(@SuppressWarnings("UnusedParameters") View view) {
        if (mSaveMovie == null) return;
        mSaveTask.terminate();
        setEncodingEnabled(false);
        mSaveTask = null;
        mGUI.update(GUIUpdate.BUTTONS);
    }

    private void onSaveCompleted(File file, boolean success) {
        stopSaving();

        if (success) {
            mFileMan.newMedia(file);
        }

        mSaveTask = null;
        mGUI.update(GUIUpdate.BUTTONS);
    }

    public void onClearDetections(View toggle) {
        TrackSet.getInstance().clear();
    }

    public void onOpenMenu(View toggle) {
        startActivity(new Intent(this, SettingsActivity.class));
    }

    /**
     * Ceases any saving operation, scheduled or in progress.
     */
    private void stopSaving() {
        if (mSaveTask != null) {
            mSaveTask.terminate();
            mSaveTask = null;
        }
    }

    /**
     * Enabled or disables encoding, along with any possibility of recording. In idle state,
     * encoding is disabled to save battery; while recording (both manually or automatically),
     * encoding is enabled.
     */
    private void setEncodingEnabled(boolean enabled) {
        stopSaving();

        if (mEncode != null) {
            mEncode.clearBuffer();
        }

        if (mEncodeTarget != null) {
            mEncodeTarget.setEnabled(enabled);
        }
    }

    private enum Status {
        STOPPED, RUNNING, CAMERA_ERROR, CAMERA_PERMISSION_ERROR, STORAGE_PERMISSION_ERROR
    }

    /**
     * Available types of GUI updates.
     */
    private enum GUIUpdate {
        ALL,
        LABELS,
        BUTTONS,
    }

    /**
     * A subclass that receives all relevant messages on an arbitrary thread and reacts to them,
     * typically by forwarding them to the main (GUI) thread.
     */
    private static class Handler extends android.os.Handler implements Lib.Callback,
            EncodeThread.Callback, SaveThread.Callback, CameraThread.Callback {
        private static final int CAMERA_ERROR = 2;
        private static final int TRIGGER_AUTO_RECORD = 3;
        private static final int SAVE_COMPLETED = 4;
        private static final int UPDATE_GUI = 5;
        private final WeakReference<RecordingActivity> mActivity;

        Handler(RecordingActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void log(String message) {
            Log.i(LOG_TAG, message);
        }

        @Override
        public void onObjectsDetected(Lib.Detection[] detections) {
            RecordingActivity activity = mActivity.get();
            if (activity == null) return;
            CameraThread cam = activity.mCamera;
            if (cam == null) return;
            TrackSet.getInstance().addDetections(detections, cam.getWidth(), cam.getHeight());
            sendMessage(obtainMessage(TRIGGER_AUTO_RECORD));
        }

        @Override
        public void flushCompleted(EncodeThread thread) {
        }

        @Override
        public void saveCompleted(File file, boolean success) {
            sendMessage(obtainMessage(SAVE_COMPLETED, success ? 1 : 0, 0, file));
        }

        @Override
        public void onCameraRender() {
            // send flush command to encoder thread
            RecordingActivity activity = mActivity.get();
            if (activity == null) return;
            if (activity.mEncode == null) return;
            activity.mEncode.getHandler().sendFlush();
        }

        @Override
        public void onCameraFrame(byte[] dataYUV420SP) {
            Lib.detectionFrame(dataYUV420SP);
        }

        @Override
        public void onCameraError() {
            if (hasMessages(CAMERA_ERROR)) return;
            sendMessage(obtainMessage(CAMERA_ERROR));
        }

        @Override
        public void handleMessage(android.os.Message msg) {
            RecordingActivity activity = mActivity.get();
            if (activity == null) return;

            switch (msg.what) {
                case CAMERA_ERROR:
                    activity.mStatus = Status.CAMERA_ERROR;
                    activity.mGUI.update(GUIUpdate.ALL);
                    break;
                case TRIGGER_AUTO_RECORD:
                    activity.triggerAutomaticRecording();
                    break;
                case SAVE_COMPLETED:
                    activity.onSaveCompleted((File) msg.obj, msg.arg1 == 1);
                    break;
                case UPDATE_GUI:
                    activity.mGUI.update((GUIUpdate) msg.obj);
                    break;
            }
        }
    }

    /**
     * A subclass that handles visual elements -- buttons, labels, and suchlike.
     */
    private class GUI implements SurfaceHolder.Callback {
        private SurfaceView mPreview;
        private boolean mPreviewReady = false;

        private TextView mStatusText;
        private String mStatusTextLast;

        private String mErrorCamera;
        private String mErrorPermissionCamera;
        private String mErrorPermissionStorage;

        private Button mManualStoppedButton;
        private Button mManualRunningButton;
        private Button mAutomaticStoppedButton;
        private Button mAutomaticRunningButton;

        /**
         * Prepares all static UI elements.
         */
        void init() {
            setContentView(R.layout.activity_recording);
            mPreview = (SurfaceView) findViewById(R.id.recording_preview);
            mPreview.getHolder().addCallback(this);

            mStatusText = (TextView) findViewById(R.id.recording_status);
            mStatusTextLast = null;

            mErrorCamera = getString(R.string.errorCamera);
            mErrorPermissionCamera = getString(R.string.errorPermissionCamera);
            mErrorPermissionStorage = getString(R.string.errorPermissionStorage);

            mManualStoppedButton = (Button) findViewById(R.id.recording_manual_stopped);
            mManualRunningButton = (Button) findViewById(R.id.recording_manual_running);
            mAutomaticStoppedButton = (Button) findViewById(R.id.recording_automatic_stopped);
            mAutomaticRunningButton = (Button) findViewById(R.id.recording_automatic_running);
        }

        /**
         * Updates all dynamic UI elements, such as labels and buttons.
         */
        void update(GUIUpdate u) {
            if (mStatus == Status.STOPPED) return;
            if (u == GUIUpdate.ALL || u == GUIUpdate.BUTTONS) updateRecordingButtons();
            if (u == GUIUpdate.ALL || u == GUIUpdate.LABELS) updateStatusString();
        }

        private void updateStatusString() {
            String text;
            if (mStatus == Status.CAMERA_ERROR) {
                text = mErrorCamera;
            } else if (mStatus == Status.CAMERA_PERMISSION_ERROR) {
                text = mErrorPermissionCamera;
            } else if (mStatus == Status.STORAGE_PERMISSION_ERROR) {
                text = mErrorPermissionStorage;
            } else {
                text = "";
            }

            //noinspection StringEquality
            if (mStatusTextLast != text) {
                mStatusText.setText(text);
                mStatusTextLast = text;
            }
        }

        private void updateRecordingButtons() {
            {
                boolean relevant = mConfig.recordMode == Config.RecordMode.MANUAL;
                boolean stopped = relevant && mSaveTask == null;
                boolean running = relevant && mSaveTask != null;
                mManualStoppedButton.setVisibility(stopped ? View.VISIBLE : View.GONE);
                mManualRunningButton.setVisibility(running ? View.VISIBLE : View.GONE);
            }

            {
                boolean relevant = mConfig.recordMode == Config.RecordMode.AUTOMATIC;
                boolean stopped = relevant && mSaveTask == null;
                boolean running = relevant && mSaveTask != null;
                mAutomaticStoppedButton.setVisibility(stopped ? View.VISIBLE : View.GONE);
                mAutomaticRunningButton.setVisibility(running ? View.VISIBLE : View.GONE);
            }
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            mPreviewReady = true;
            RecordingActivity.this.init();
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            mPreviewReady = false;
        }

        boolean isPreviewReady() {
            return mPreviewReady;
        }

        Surface getPreviewSurface() {
            return mPreview.getHolder().getSurface();
        }

        int getPreviewWidth() {
            return mPreview.getWidth();
        }

        int getPreviewHeight() {
            return mPreview.getHeight();
        }
    }
}
