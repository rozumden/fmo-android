# CMAKE generated file: DO NOT EDIT!
# Generated by "Unix Makefiles" Generator, CMake Version 3.6

# Delete rule output on recipe failure.
.DELETE_ON_ERROR:


#=============================================================================
# Special targets provided by cmake.

# Disable implicit rules so canonical targets will work.
.SUFFIXES:


# Remove some rules from gmake that .SUFFIXES does not remove.
SUFFIXES =

.SUFFIXES: .hpux_make_needs_suffix_list


# Suppress display of executed commands.
$(VERBOSE).SILENT:


# A target that is always out of date.
cmake_force:

.PHONY : cmake_force

#=============================================================================
# Set environment variables for the build.

# The shell in which to execute make rules.
SHELL = /bin/sh

# The CMake executable.
CMAKE_COMMAND = /usr/local/bin/cmake

# The command to remove a file.
RM = /usr/local/bin/cmake -E remove -f

# Escaping for special characters.
EQUALS = =

# The top-level source directory on which CMake was run.
CMAKE_SOURCE_DIR = /home/rozumden/src/fmo-android-denys/fmo-cpp

# The top-level build directory on which CMake was run.
CMAKE_BINARY_DIR = /home/rozumden/src/fmo-android-denys/fmo-cpp/build

# Include any dependencies generated for this target.
include tests/CMakeFiles/fmo-benchmark.dir/depend.make

# Include the progress variables for this target.
include tests/CMakeFiles/fmo-benchmark.dir/progress.make

# Include the compile flags for this target's objects.
include tests/CMakeFiles/fmo-benchmark.dir/flags.make

tests/CMakeFiles/fmo-benchmark.dir/benchmark-main.cpp.o: tests/CMakeFiles/fmo-benchmark.dir/flags.make
tests/CMakeFiles/fmo-benchmark.dir/benchmark-main.cpp.o: ../tests/benchmark-main.cpp
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green --progress-dir=/home/rozumden/src/fmo-android-denys/fmo-cpp/build/CMakeFiles --progress-num=$(CMAKE_PROGRESS_1) "Building CXX object tests/CMakeFiles/fmo-benchmark.dir/benchmark-main.cpp.o"
	cd /home/rozumden/src/fmo-android-denys/fmo-cpp/build/tests && /usr/bin/c++   $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -o CMakeFiles/fmo-benchmark.dir/benchmark-main.cpp.o -c /home/rozumden/src/fmo-android-denys/fmo-cpp/tests/benchmark-main.cpp

tests/CMakeFiles/fmo-benchmark.dir/benchmark-main.cpp.i: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Preprocessing CXX source to CMakeFiles/fmo-benchmark.dir/benchmark-main.cpp.i"
	cd /home/rozumden/src/fmo-android-denys/fmo-cpp/build/tests && /usr/bin/c++  $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -E /home/rozumden/src/fmo-android-denys/fmo-cpp/tests/benchmark-main.cpp > CMakeFiles/fmo-benchmark.dir/benchmark-main.cpp.i

tests/CMakeFiles/fmo-benchmark.dir/benchmark-main.cpp.s: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Compiling CXX source to assembly CMakeFiles/fmo-benchmark.dir/benchmark-main.cpp.s"
	cd /home/rozumden/src/fmo-android-denys/fmo-cpp/build/tests && /usr/bin/c++  $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -S /home/rozumden/src/fmo-android-denys/fmo-cpp/tests/benchmark-main.cpp -o CMakeFiles/fmo-benchmark.dir/benchmark-main.cpp.s

tests/CMakeFiles/fmo-benchmark.dir/benchmark-main.cpp.o.requires:

.PHONY : tests/CMakeFiles/fmo-benchmark.dir/benchmark-main.cpp.o.requires

tests/CMakeFiles/fmo-benchmark.dir/benchmark-main.cpp.o.provides: tests/CMakeFiles/fmo-benchmark.dir/benchmark-main.cpp.o.requires
	$(MAKE) -f tests/CMakeFiles/fmo-benchmark.dir/build.make tests/CMakeFiles/fmo-benchmark.dir/benchmark-main.cpp.o.provides.build
.PHONY : tests/CMakeFiles/fmo-benchmark.dir/benchmark-main.cpp.o.provides

tests/CMakeFiles/fmo-benchmark.dir/benchmark-main.cpp.o.provides.build: tests/CMakeFiles/fmo-benchmark.dir/benchmark-main.cpp.o


# Object files for target fmo-benchmark
fmo__benchmark_OBJECTS = \
"CMakeFiles/fmo-benchmark.dir/benchmark-main.cpp.o"

# External object files for target fmo-benchmark
fmo__benchmark_EXTERNAL_OBJECTS =

tests/fmo-benchmark: tests/CMakeFiles/fmo-benchmark.dir/benchmark-main.cpp.o
tests/fmo-benchmark: tests/CMakeFiles/fmo-benchmark.dir/build.make
tests/fmo-benchmark: fmo/libfmo-core.a
tests/fmo-benchmark: fmo/explorer-v1/libfmo-explorer-v1.a
tests/fmo-benchmark: fmo/explorer-v2/libfmo-explorer-v2.a
tests/fmo-benchmark: fmo/explorer-v3/libfmo-explorer-v3.a
tests/fmo-benchmark: fmo/median-v1/libfmo-median-v1.a
tests/fmo-benchmark: fmo/median-v2/libfmo-median-v2.a
tests/fmo-benchmark: fmo/taxonomy-v1/libfmo-taxonomy-v1.a
tests/fmo-benchmark: fmo/libfmo-core.a
tests/fmo-benchmark: /usr/local/lib/libopencv_dnn.so.3.3.1
tests/fmo-benchmark: /usr/local/lib/libopencv_ml.so.3.3.1
tests/fmo-benchmark: /usr/local/lib/libopencv_objdetect.so.3.3.1
tests/fmo-benchmark: /usr/local/lib/libopencv_shape.so.3.3.1
tests/fmo-benchmark: /usr/local/lib/libopencv_stitching.so.3.3.1
tests/fmo-benchmark: /usr/local/lib/libopencv_superres.so.3.3.1
tests/fmo-benchmark: /usr/local/lib/libopencv_videostab.so.3.3.1
tests/fmo-benchmark: /usr/local/lib/libopencv_calib3d.so.3.3.1
tests/fmo-benchmark: /usr/local/lib/libopencv_features2d.so.3.3.1
tests/fmo-benchmark: /usr/local/lib/libopencv_flann.so.3.3.1
tests/fmo-benchmark: /usr/local/lib/libopencv_highgui.so.3.3.1
tests/fmo-benchmark: /usr/local/lib/libopencv_photo.so.3.3.1
tests/fmo-benchmark: /usr/local/lib/libopencv_video.so.3.3.1
tests/fmo-benchmark: /usr/local/lib/libopencv_videoio.so.3.3.1
tests/fmo-benchmark: /usr/local/lib/libopencv_imgcodecs.so.3.3.1
tests/fmo-benchmark: /usr/local/lib/libopencv_imgproc.so.3.3.1
tests/fmo-benchmark: /usr/local/lib/libopencv_core.so.3.3.1
tests/fmo-benchmark: tests/CMakeFiles/fmo-benchmark.dir/link.txt
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green --bold --progress-dir=/home/rozumden/src/fmo-android-denys/fmo-cpp/build/CMakeFiles --progress-num=$(CMAKE_PROGRESS_2) "Linking CXX executable fmo-benchmark"
	cd /home/rozumden/src/fmo-android-denys/fmo-cpp/build/tests && $(CMAKE_COMMAND) -E cmake_link_script CMakeFiles/fmo-benchmark.dir/link.txt --verbose=$(VERBOSE)

# Rule to build all files generated by this target.
tests/CMakeFiles/fmo-benchmark.dir/build: tests/fmo-benchmark

.PHONY : tests/CMakeFiles/fmo-benchmark.dir/build

tests/CMakeFiles/fmo-benchmark.dir/requires: tests/CMakeFiles/fmo-benchmark.dir/benchmark-main.cpp.o.requires

.PHONY : tests/CMakeFiles/fmo-benchmark.dir/requires

tests/CMakeFiles/fmo-benchmark.dir/clean:
	cd /home/rozumden/src/fmo-android-denys/fmo-cpp/build/tests && $(CMAKE_COMMAND) -P CMakeFiles/fmo-benchmark.dir/cmake_clean.cmake
.PHONY : tests/CMakeFiles/fmo-benchmark.dir/clean

tests/CMakeFiles/fmo-benchmark.dir/depend:
	cd /home/rozumden/src/fmo-android-denys/fmo-cpp/build && $(CMAKE_COMMAND) -E cmake_depends "Unix Makefiles" /home/rozumden/src/fmo-android-denys/fmo-cpp /home/rozumden/src/fmo-android-denys/fmo-cpp/tests /home/rozumden/src/fmo-android-denys/fmo-cpp/build /home/rozumden/src/fmo-android-denys/fmo-cpp/build/tests /home/rozumden/src/fmo-android-denys/fmo-cpp/build/tests/CMakeFiles/fmo-benchmark.dir/DependInfo.cmake --color=$(COLOR)
.PHONY : tests/CMakeFiles/fmo-benchmark.dir/depend
