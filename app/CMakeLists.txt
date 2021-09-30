# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.10.2)

if(${FLAVOR} STREQUAL "GITHUB")
    add_subdirectory(src/github/cpp)
elseif(${FLAVOR} STREQUAL "DEV")
    add_subdirectory(src/dev/cpp)
elseif(${FLAVOR} STREQUAL "COMPILE")
    add_subdirectory(src/compile/cpp)
endif()