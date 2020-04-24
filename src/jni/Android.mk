JNI_PATH := $(call my-dir)
include $(JNI_PATH)/libyuv/Android.mk

include $(CLEAR_VARS)
LOCAL_ARM_MODE := arm
LOCAL_MODULE := libyuv_util
LOCAL_MODULE_TAGS := optional

LOCAL_SRC_FILES := $(JNI_PATH)/yuv_util/yuv_util.cc \



LOCAL_C_INCLUDES += $(JNI_PATH)/libyuv/include \

LOCAL_SHARED_LIBRARIES := \
					libyuv


include $(BUILD_SHARED_LIBRARY)
