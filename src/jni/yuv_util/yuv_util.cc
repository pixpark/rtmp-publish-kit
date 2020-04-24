//#include "yuv_util.h"

#include <jni.h>
#include <libyuv.h>
#include <android/log.h>
#include <string.h>

#define LIBENC_LOGD(...) ((void)__android_log_print(ANDROID_LOG_DEBUG, "libenc", __VA_ARGS__))
#define LIBENC_LOGI(...) ((void)__android_log_print(ANDROID_LOG_INFO , "libenc", __VA_ARGS__))
#define LIBENC_LOGW(...) ((void)__android_log_print(ANDROID_LOG_WARN , "libenc", __VA_ARGS__))
#define LIBENC_LOGE(...) ((void)__android_log_print(ANDROID_LOG_ERROR, "libenc", __VA_ARGS__))
#define LIBENC_ARRAY_ELEMS(a)  (sizeof(a) / sizeof(a[0]))

using namespace libyuv;

static JavaVM *jvm;
static JNIEnv *jenv;


struct I420Frame {
    int width;
    int height;
    uint8_t *data;
    uint8_t *y;
    uint8_t *u;
    uint8_t *v;
};

struct NV12Frame {
    int width;
    int height;
    uint8_t *data;
    uint8_t *y;
    uint8_t *uv;
};



static struct I420Frame i420_rotated_frame;
static struct I420Frame i420_scaled_frame;
static struct NV12Frame nv12_det_frame;

static bool convert_to_i420(uint8_t *src_frame, jint src_width, jint src_height,
                            jboolean need_flip, jint rotate_degree, int format) {
    int y_size = src_width * src_height;


    if (rotate_degree % 180 == 0) {
        if (i420_rotated_frame.width != src_width || i420_rotated_frame.height != src_height) {
            free(i420_rotated_frame.data);
            i420_rotated_frame.width = src_width;
            i420_rotated_frame.height = src_height;
            i420_rotated_frame.data = (uint8_t *) malloc(y_size * 4 / 2);
            i420_rotated_frame.y = i420_rotated_frame.data;
            i420_rotated_frame.u = i420_rotated_frame.y + y_size;
            i420_rotated_frame.v = i420_rotated_frame.u + y_size / 4;
        }
    } else {
        if (i420_rotated_frame.width != src_height || i420_rotated_frame.height != src_width) {
            free(i420_rotated_frame.data);
            i420_rotated_frame.width = src_height;
            i420_rotated_frame.height = src_width;
            i420_rotated_frame.data = (uint8_t *) malloc(y_size * 4 / 2);
            i420_rotated_frame.y = i420_rotated_frame.data;
            i420_rotated_frame.u = i420_rotated_frame.y + y_size;
            i420_rotated_frame.v = i420_rotated_frame.u + y_size / 4;
        }
    }


    jint ret = ConvertToI420(src_frame, y_size,
                             i420_rotated_frame.y, i420_rotated_frame.width,
                             i420_rotated_frame.u, i420_rotated_frame.width / 2,
                             i420_rotated_frame.v, i420_rotated_frame.width / 2,
                             0, 0,
                             src_width, src_height,
                             src_width, src_height,
                             (RotationMode) rotate_degree, format);
    if (ret < 0) {
        LIBENC_LOGE("ConvertToI420 failure");
        return false;
    }

    ret = I420Scale(i420_rotated_frame.y, i420_rotated_frame.width,
                    i420_rotated_frame.u, i420_rotated_frame.width / 2,
                    i420_rotated_frame.v, i420_rotated_frame.width / 2,
                    need_flip ? -i420_rotated_frame.width : i420_rotated_frame.width,
                    i420_rotated_frame.height,
                    i420_scaled_frame.y, i420_scaled_frame.width,
                    i420_scaled_frame.u, i420_scaled_frame.width / 2,
                    i420_scaled_frame.v, i420_scaled_frame.width / 2,
                    i420_scaled_frame.width, i420_scaled_frame.height,
                    kFilterNone);

    if (ret < 0) {
        LIBENC_LOGE("I420Scale failure");
        return false;
    }

    return true;
}

static bool convert_to_i420_with_crop_scale(uint8_t *src_frame, jint src_width, jint src_height,
                                            jint crop_x, jint crop_y, jint crop_width,
                                            jint crop_height,
                                            jboolean need_flip, jint rotate_degree, int format) {
    int y_size = src_width * src_height;

    if (rotate_degree % 180 == 0) {
        if (i420_rotated_frame.width != src_width || i420_rotated_frame.height != src_height) {
            free(i420_rotated_frame.data);
            i420_rotated_frame.data = (uint8_t *) malloc(y_size * 3 / 2);
            i420_rotated_frame.y = i420_rotated_frame.data;
            i420_rotated_frame.u = i420_rotated_frame.y + y_size;
            i420_rotated_frame.v = i420_rotated_frame.u + y_size / 4;
        }

        i420_rotated_frame.width = crop_width;
        i420_rotated_frame.height = crop_height;

    } else {
        if (i420_rotated_frame.width != src_height || i420_rotated_frame.height != src_width) {
            free(i420_rotated_frame.data);
            i420_rotated_frame.data = (uint8_t *) malloc(y_size * 3 / 2);
            i420_rotated_frame.y = i420_rotated_frame.data;
            i420_rotated_frame.u = i420_rotated_frame.y + y_size;
            i420_rotated_frame.v = i420_rotated_frame.u + y_size / 4;
        }

        i420_rotated_frame.width = crop_height;
        i420_rotated_frame.height = crop_width;
    }

    jint ret = ConvertToI420(src_frame, y_size,
                             i420_rotated_frame.y, i420_rotated_frame.width,
                             i420_rotated_frame.u, i420_rotated_frame.width / 2,
                             i420_rotated_frame.v, i420_rotated_frame.width / 2,
                             crop_x, crop_y,
                             src_width, need_flip ? -src_height : src_height,
                             crop_width, crop_height,
                             (RotationMode) rotate_degree, format);

    if (ret < 0) {
        LIBENC_LOGE("ConvertToI420 failure");
        return false;
    }

    return true;
}



static bool nv21_convert_to_nv12_with_crop_scale(uint8_t *src_frame, jint src_width, jint src_height,
                                            jint crop_x, jint crop_y, jint crop_width,
                                            jint crop_height,
                                            jboolean need_flip, jint rotate_degree, int format) {
    int y_size = src_width * src_height;

    if (rotate_degree % 180 == 0) {
        if (i420_rotated_frame.width != src_width || i420_rotated_frame.height != src_height) {
            free(i420_rotated_frame.data);
            i420_rotated_frame.data = (uint8_t *) malloc(y_size * 3 / 2);
            i420_rotated_frame.y = i420_rotated_frame.data;
            i420_rotated_frame.u = i420_rotated_frame.y + y_size;
            i420_rotated_frame.v = i420_rotated_frame.u + y_size / 4;
        }

        i420_rotated_frame.width = crop_width;
        i420_rotated_frame.height = crop_height;

    } else {
        if (i420_rotated_frame.width != src_height || i420_rotated_frame.height != src_width) {
            free(i420_rotated_frame.data);
            i420_rotated_frame.data = (uint8_t *) malloc(y_size * 3 / 2);
            i420_rotated_frame.y = i420_rotated_frame.data;
            i420_rotated_frame.u = i420_rotated_frame.y + y_size;
            i420_rotated_frame.v = i420_rotated_frame.u + y_size / 4;
        }

        i420_rotated_frame.width = crop_height;
        i420_rotated_frame.height = crop_width;
    }

    jint ret = ConvertToI420(src_frame, y_size,
                             i420_rotated_frame.y, i420_rotated_frame.width,
                             i420_rotated_frame.u, i420_rotated_frame.width / 2,
                             i420_rotated_frame.v, i420_rotated_frame.width / 2,
                             crop_x, crop_y,
                             src_width, need_flip ? -src_height : src_height,
                             crop_width, crop_height,
                             (RotationMode) rotate_degree, format);

    if (ret < 0) {
        LIBENC_LOGE("ConvertToI420 failure");
        return false;
    }

    if (rotate_degree % 180 == 0) {
        if (src_width != nv12_det_frame.width || src_height != nv12_det_frame.height) {
            free(nv12_det_frame.data);
            nv12_det_frame.data = (uint8_t *) malloc(y_size * 3 / 2);
            nv12_det_frame.y = nv12_det_frame.data;
            nv12_det_frame.uv = nv12_det_frame.y + y_size;
        }

        nv12_det_frame.width = crop_width;
        nv12_det_frame.height = crop_height;
    } else {
        if (src_width != nv12_det_frame.height || src_height != nv12_det_frame.width) {
            free(nv12_det_frame.data);
            nv12_det_frame.data = (uint8_t *) malloc(y_size * 3 / 2);
            nv12_det_frame.y = nv12_det_frame.data;
            nv12_det_frame.uv = nv12_det_frame.y + y_size;
        }
        nv12_det_frame.width = crop_height;
        nv12_det_frame.height = crop_width;
    }

    ret = I420ToNV12(i420_rotated_frame.y, i420_rotated_frame.width,
                i420_rotated_frame.u, i420_rotated_frame.width / 2,
                i420_rotated_frame.v, i420_rotated_frame.width / 2,
                nv12_det_frame.y, nv12_det_frame.width,
                nv12_det_frame.uv, nv12_det_frame.width, nv12_det_frame.width, nv12_det_frame.height);


    if (ret < 0) {
        LIBENC_LOGE("I420Scale failure");
        return false;
    }

    return true;
}


// For COLOR_FormatYUV420Planar
static jbyteArray YuvUtil_RGBAToI420(JNIEnv *env, jobject thiz, jbyteArray frame, jint src_width,
                                     jint src_height, jboolean need_flip, jint rotate_degree) {
    jbyte *rgba_frame = env->GetByteArrayElements(frame, NULL);

    if (!convert_to_i420((uint8_t *) rgba_frame, src_width, src_height, need_flip, rotate_degree,
                         FOURCC_RGBA)) {
        return NULL;
    }

    int y_size = i420_scaled_frame.width * i420_scaled_frame.height;
    jbyteArray i420Frame = env->NewByteArray(y_size * 3 / 2);
    env->SetByteArrayRegion(i420Frame, 0, y_size * 3 / 2, (jbyte *) i420_scaled_frame.data);

    env->ReleaseByteArrayElements(frame, rgba_frame, JNI_ABORT);
    return i420Frame;
}

static jbyteArray
YuvUtil_NV21ToI420Scaled(JNIEnv *env, jobject thiz, jbyteArray frame, jint src_width,
                         jint src_height, jboolean need_flip, jint rotate_degree,
                         jint crop_x, jint crop_y, jint crop_width, jint crop_height) {
    jbyte *argb_frame = env->GetByteArrayElements(frame, NULL);
    if (!convert_to_i420_with_crop_scale((uint8_t *) argb_frame, src_width, src_height,
                                         crop_x, crop_y, crop_width, crop_height,
                                         need_flip, rotate_degree, FOURCC_NV21)) {
        return NULL;
    }

    int y_size = i420_rotated_frame.width * i420_rotated_frame.height;
    jbyteArray i420Frame = env->NewByteArray(y_size * 3 / 2);
    env->SetByteArrayRegion(i420Frame, 0, y_size * 3 / 2, (jbyte *) i420_rotated_frame.data);

    env->ReleaseByteArrayElements(frame, argb_frame, JNI_ABORT);
    return i420Frame;
}

static jbyteArray
YuvUtil_NV21ToNV12Scaled(JNIEnv *env, jobject thiz, jbyteArray frame, jint src_width,
                         jint src_height, jboolean need_flip, jint rotate_degree,
                         jint crop_x, jint crop_y, jint crop_width, jint crop_height) {
    jbyte *argb_frame = env->GetByteArrayElements(frame, NULL);
    if (!nv21_convert_to_nv12_with_crop_scale((uint8_t *) argb_frame, src_width, src_height,
                                         crop_x, crop_y, crop_width, crop_height,
                                         need_flip, rotate_degree, FOURCC_NV21)) {
        return NULL;
    }

    int y_size = nv12_det_frame.width * nv12_det_frame.height;
    jbyteArray i420Frame = env->NewByteArray(y_size * 3 / 2);
    env->SetByteArrayRegion(i420Frame, 0, y_size * 3 / 2, (jbyte *) nv12_det_frame.data);

    env->ReleaseByteArrayElements(frame, argb_frame, JNI_ABORT);
    return i420Frame;
}

// For Bitmap.getPixels() ARGB_8888
static jbyteArray YuvUtil_ARGBToI420(JNIEnv *env, jobject thiz, jintArray frame, jint src_width,
                                     jint src_height, jboolean need_flip, jint rotate_degree) {
    jint *argb_frame = env->GetIntArrayElements(frame, NULL);

    if (!convert_to_i420((uint8_t *) argb_frame, src_width, src_height, need_flip, rotate_degree,
                         FOURCC_ARGB)) {
        return NULL;
    }

    int y_size = i420_scaled_frame.width * i420_scaled_frame.height;
    jbyteArray i420Frame = env->NewByteArray(y_size * 3 / 2);
    env->SetByteArrayRegion(i420Frame, 0, y_size * 3 / 2, (jbyte *) i420_scaled_frame.data);

    env->ReleaseIntArrayElements(frame, argb_frame, JNI_ABORT);
    return i420Frame;
}

// For Bitmap.getPixels() ARGB_8888
static jbyteArray
YuvUtil_ARGBToI420Scaled(JNIEnv *env, jobject thiz, jintArray frame, jint src_width,
                         jint src_height, jboolean need_flip, jint rotate_degree,
                         jint crop_x, jint crop_y, jint crop_width, jint crop_height) {
    jint *argb_frame = env->GetIntArrayElements(frame, NULL);

    if (!convert_to_i420_with_crop_scale((uint8_t *) argb_frame, src_width, src_height,
                                         crop_x, crop_y, crop_width, crop_height,
                                         need_flip, rotate_degree, FOURCC_ARGB)) {
        return NULL;
    }

    int y_size = i420_scaled_frame.width * i420_scaled_frame.height;
    jbyteArray i420Frame = env->NewByteArray(y_size * 3 / 2);
    env->SetByteArrayRegion(i420Frame, 0, y_size * 3 / 2, (jbyte *) i420_scaled_frame.data);

    env->ReleaseIntArrayElements(frame, argb_frame, JNI_ABORT);
    return i420Frame;
}

static JNINativeMethod yuv_util_methods[] = {
        {"RGBAToI420",           "([BIIZI)[B",            (void *) YuvUtil_RGBAToI420},
        {"ARGBToI420Scaled",     "([IIIZIIIII)[B",        (void *) YuvUtil_ARGBToI420Scaled},
        {"ARGBToI420",           "([IIIZI)[B",            (void *) YuvUtil_ARGBToI420},
        {"NV21ToI420Scaled",     "([BIIZIIIII)[B",        (void *) YuvUtil_NV21ToI420Scaled},
        {"NV21ToNV12Scaled",     "([BIIZIIIII)[B",        (void *) YuvUtil_NV21ToNV12Scaled}
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
