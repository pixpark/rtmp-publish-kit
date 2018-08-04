package com.spark.live.sdk.util;

public class JniYuvUtil {



    public static native byte[] RGBAToI420(byte[] frame, int width, int height, boolean flip, int rotate);
    public static native byte[] ARGBToI420Scaled(int[] frame, int width, int height, boolean flip, int rotate, int crop_x, int crop_y,int crop_width, int crop_height);
    public static native byte[] ARGBToI420(int[] frame, int width, int height, boolean flip, int rotate);
    public static native byte[] NV21ToI420Scaled(byte[] frame, int width, int height, boolean flip, int rotate, int crop_x, int crop_y,int crop_width, int crop_height);
    public static native byte[] NV21ToNV12Scaled(byte[] frame, int width, int height, boolean flip, int rotate, int crop_x, int crop_y,int crop_width, int crop_height);

    static {
        System.loadLibrary("yuv_util");
    }
}
