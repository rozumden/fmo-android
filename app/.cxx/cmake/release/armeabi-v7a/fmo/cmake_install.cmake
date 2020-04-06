# Install script for directory: /home/rozumden/src/fmo-android-denys/fmo-cpp/fmo

# Set the install prefix
if(NOT DEFINED CMAKE_INSTALL_PREFIX)
  set(CMAKE_INSTALL_PREFIX "/usr/local")
endif()
string(REGEX REPLACE "/$" "" CMAKE_INSTALL_PREFIX "${CMAKE_INSTALL_PREFIX}")

# Set the install configuration name.
if(NOT DEFINED CMAKE_INSTALL_CONFIG_NAME)
  if(BUILD_TYPE)
    string(REGEX REPLACE "^[^A-Za-z0-9_]+" ""
           CMAKE_INSTALL_CONFIG_NAME "${BUILD_TYPE}")
  else()
    set(CMAKE_INSTALL_CONFIG_NAME "Release")
  endif()
  message(STATUS "Install configuration: \"${CMAKE_INSTALL_CONFIG_NAME}\"")
endif()

# Set the component getting installed.
if(NOT CMAKE_INSTALL_COMPONENT)
  if(COMPONENT)
    message(STATUS "Install component: \"${COMPONENT}\"")
    set(CMAKE_INSTALL_COMPONENT "${COMPONENT}")
  else()
    set(CMAKE_INSTALL_COMPONENT)
  endif()
endif()

# Install shared libraries without execute permission?
if(NOT DEFINED CMAKE_INSTALL_SO_NO_EXE)
  set(CMAKE_INSTALL_SO_NO_EXE "1")
endif()

# Is this installation the result of a crosscompile?
if(NOT DEFINED CMAKE_CROSSCOMPILING)
  set(CMAKE_CROSSCOMPILING "TRUE")
endif()

if(NOT CMAKE_INSTALL_LOCAL_ONLY)
  # Include the install script for each subdirectory.
  include("/home/rozumden/src/fmo-android-denys/app/.cxx/cmake/release/armeabi-v7a/fmo/explorer-v1/cmake_install.cmake")
  include("/home/rozumden/src/fmo-android-denys/app/.cxx/cmake/release/armeabi-v7a/fmo/explorer-v2/cmake_install.cmake")
  include("/home/rozumden/src/fmo-android-denys/app/.cxx/cmake/release/armeabi-v7a/fmo/explorer-v3/cmake_install.cmake")
  include("/home/rozumden/src/fmo-android-denys/app/.cxx/cmake/release/armeabi-v7a/fmo/median-v1/cmake_install.cmake")
  include("/home/rozumden/src/fmo-android-denys/app/.cxx/cmake/release/armeabi-v7a/fmo/median-v2/cmake_install.cmake")
  include("/home/rozumden/src/fmo-android-denys/app/.cxx/cmake/release/armeabi-v7a/fmo/taxonomy-v1/cmake_install.cmake")

endif()

