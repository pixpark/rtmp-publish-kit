APP_ABI :=  armeabi-v7a arm64-v8a
APP_OPTIM        := release
APP_CFLAGS       += -O3
APP_STL := c++_static
NDK_TOOLCHAIN_VERSION = clang
APP_CPPFLAGS :=  -std=c++11 -fexceptions
APP_PLATFORM := android-21