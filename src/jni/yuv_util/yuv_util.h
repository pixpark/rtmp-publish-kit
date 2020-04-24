//
// Created by 葛昭友 on 2018/7/12.
//

#ifndef NDK_LIBS_YUV_UTIL_H
#define NDK_LIBS_YUV_UTIL_H

#endif //NDK_LIBS_YUV_UTIL_H

#include <jni.h>
#include <libyuv.h>
#include <android/log.h>

#include <string.h>

#define LIBENC_LOGD(...) ((void)__android_log_print(ANDROID_LOG_DEBUG, "libenc", __VA_ARGS__))
#define LIBENC_LOGI(...) ((void)__android_log_print(ANDROID_LOG_INFO , "libenc", __VA_ARGS__))
#define LIBENC_LOGW(...) ((void)__android_log_print(ANDROID_LOG_WARN , "libenc", __VA_ARGS__))
#define LIBENC_LOGE(...) ((void)__android_log_print(ANDROID_LOG_ERROR, "libenc", __VA_ARGS__))
#define LIBENC_ARRAY_ELEMS(a)  (sizeof(a) / sizeof(a[0]))


static jbyteArray
YuvUtil_ARGBToI420Scaled(JNIEnv *env, jobject thiz, jintArray frame, jint src_width,
                         jint src_height, jboolean need_flip, jint rotate_degree,
                         jint crop_x, jint crop_y, jint crop_width, jint crop_height) ;




static jbyteArray YuvUtil_ARGBToI420(JNIEnv *env, jobject thiz, jintArray frame, jint src_width,
                                     jint src_height, jboolean need_flip, jint rotate_degree) ;




static jbyteArray
YuvUtil_NV21ToI420Scaled(JNIEnv *env, jobject thiz, jbyteArray frame, jint src_width,
                         jint src_height, jboolean need_flip, jint rotate_degree,
                         jint crop_x, jint crop_y, jint crop_width, jint crop_height);


static jbyteArray YuvUtil_RGBAToI420(JNIEnv *env, jobject thiz, jbyteArray frame, jint src_width,
                                     jint src_height, jboolean need_flip, jint rotate_degree);




// private method
static bool convert_to_i420_with_crop_scale(uint8_t *src_frame, jint src_width, jint src_height,
                                            jint crop_x, jint crop_y, jint crop_width,
                                            jint crop_height,
                                            jboolean need_flip, jint rotate_degree, int format);


static bool convert_to_i420(uint8_t *src_frame, jint src_width, jint src_height,
                            jboolean need_flip, jint rotate_degree, int format);