# FMO detection (demo version)

The demonstrator displays the detection and processing of fast moving objects in real-time.
Various properties of the objects can be estimated, like velocity, radius or trajectory.
Users can toss various objects such as balls or darts, “shoot” any objects in front of a webcam
at speeds that the objects will appear as long streaks. Properties of these blurred steaks allow
us to describe movement and shape of the object. The detected objects and their trajectories
are shown in real-time. If the real object size of the object in world units is known, then object
velocity can be estimated. By default we assume a floorball, thus for objects of other sizes the
estimation will be corrupted, but these settings can be easily changed (see Parameters section). The user should
understand that velocity is found solely from video frames, whereas current methods (for
example in tennis tournaments) usually use radars for this task.
Additionally, the total number of detections is shown. This may be particularly interesting when a
lot of small objects, which for example are not from the same class, are thrown in front of the
camera.
Applications of the methods like removal of fast moving objects can be shown in real-time.
Attendees will be able to play with the system, testing its limits (small objects, very fast throws,
shaking the camera).

## Prerequisites
```
opencv 
boost
```

## To compile
```
mkdir build
cd build
cmake ..
make
sudo make install
```

Compiled version for Windows: http://cmp.felk.cvut.cz/fmo/files/fmo-demo_windows_compiled.zip 

## To run real-time on a camera 
Integer after `--camera` for camera index.

> fmo-desktop --camera 0 --utiademo

While running: space to stop, enter to make a step, "n" to enter name to score table, "1" to hide score table, "2" to show score table, "r" to start or stop recording, "h" for help subwindow, "a" and "m" to switch between automatic and manual mode, esc to exit.

> fmo-desktop --camera 0 --demo

Minimalistic FMO detection.

> fmo-desktop --camera 0 --tutdemo

Warning: starts recording by default, `--no-record` to fix it.

While running different modes switched by:

0 - default with recorded videos

1 - all detection for every frame

2 - detection steps: input frame, difference image, distance transform and local maxima, detections

3 - shows only the last frame where FMOs occurred and current frame in the corner

4 - shows frame which had the largest amount of FMOs during last 10 seconds

> fmo-desktop --camera 0 --removal

FMO removal.

> fmo-desktop --camera 0 --debug

While running: "d" for difference image, "i" for thresholded, "t" for distance transform, "o" for original image, etc.
 
  
## Parameters

`--exposure <float> `

Set exposure value. Should be between 0 and 1. Usually between 0.03 and 0.1.

`--fps <int>`

Set number of frames per second.

`--p2cm <float>`

Set how many cm are in one pixel on object. Used for speedm estimation. More dominant than --radius.
            
`--dfactor <float>`

Differential image threshold factor. Default 1.0.

`--radius  <float> `

Set object radius in cm. Used for speed estimation. Used if --p2cm is not specified. By default used for tennis/floorball: 3.6 cm.


## To run for a specific video
> fmo-desktop --input <path> --demo
  
## Help
> fmo-desktop --help
