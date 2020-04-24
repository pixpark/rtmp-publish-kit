//
// Created by 葛昭友 on 2018/7/12.
//


#include <jni.h>

#include "yuv_util.h"




#undef JNIEXPORT
#define JNIEXPORT __attribute__((visibility("default")))

#include "./yuv_util.h"


namespace spark_live_pusher_jni {
    static JavaVM *jvm;
    static JNIEnv *jenv;



    static JNINativeMethod yuv_util_methods[] = {
            {"RGBAToI420",           "([BIIZI)[B",            (void *) YuvUtil_RGBAToI420},
            {"ARGBToI420Scaled",     "([IIIZIIIII)[B",        (void *) YuvUtil_ARGBToI420Scaled},
            {"ARGBToI420",           "([IIIZI)[B",            (void *) YuvUtil_ARGBToI420},
            {"NV21ToI420Scaled",     "([BIIZIIIII)[B",        (void *) YuvUtil_NV21ToI420Scaled},
    };



    extern "C" jint JNIEXPORT JNI_OnLoad(JavaVM *vm, void *reserved) {
        jvm = vm;

        if (jvm->GetEnv((void **) &jenv, JNI_VERSION_1_6) != JNI_OK) {
            LIBENC_LOGE("Env not got");
            return JNI_ERR;
        }

        jclass clz = jenv->FindClass("com/spark/live/sdk/util/JniYuvUtil");
        if (clz == NULL) {
            LIBENC_LOGE("Class \"com/spark/live/sdk/util/JniYuvUtil\" not found");
            return JNI_ERR;
        }

        if (jenv->RegisterNatives(clz, yuv_util_methods, LIBENC_ARRAY_ELEMS(yuv_util_methods))) {
            LIBENC_LOGE("methods not registered");
            return JNI_ERR;
        }
        return JNI_VERSION_1_6;
    }



}  // namespace webrtc_jni

