package com.spark.live.sdk.util;

import android.hardware.Camera;


/**
 * 图片处理工具类
 * Created by devzhaoyou on 7/21/16.
 */
public class ImageUtil {


    // for the vbuffer for YV12(android YUV), @see below:
    // https://developer.android.com/reference/android/hardware/Camera.Parameters.html#setPreviewFormat(int)
    // https://developer.android.com/reference/android/graphics/ImageFormat.html#YV12
    public static int getYuvBufferSize(int width, int height) {
        // stride = ALIGN(width, 16)
        int stride = (int) Math.ceil(width / 16.0) * 16;
        // y_size = stride * height
        int y_size = stride * height;
        // c_stride = ALIGN(stride/2, 16)
        int c_stride = (int) Math.ceil(width / 32.0) * 16;
        // c_size = c_stride * height/2
        int c_size = c_stride * height / 2;
        // size = y_size + c_size * 2
        return y_size + c_size * 2;
    }

    // the color transform, @see http://stackoverflow.com/questions/15739684/mediacodec-and-camera-color-space-incorrect
    public static byte[] YV12toYUV420PackedSemiPlanar(final byte[] input, final byte[] output, final int width, final int height) {
        /*
         * COLOR_TI_FormatYUV420PackedSemiPlanar is NV12
         * We convert by putting the corresponding U and V bytes together (interleaved).
         */

        final int frameSize = width * height;
        final int qFrameSize = frameSize / 4;

        System.arraycopy(input, 0, output, 0, frameSize); // Y

        for (int i = 0; i < qFrameSize; i++) {
            output[frameSize + i * 2] = input[frameSize + i + qFrameSize]; // Cb (U)
            output[frameSize + i * 2 + 1] = input[frameSize + i]; // Cr (V)
        }

        return output;
    }

    public static byte[] YUV420PackedSemiPlanartoYV12(final byte[] input, final byte[] output, final int width, final int height) {
        final int frameSize = width * height;
        final int qFrameSize = frameSize / 4;

        System.arraycopy(input, 0, output, 0, frameSize); // Y

        for (int i = 0; i < qFrameSize; i++) {
            output[frameSize + i + qFrameSize] = input[frameSize + i * 2]; // Cb (U)
            output[frameSize + i] = input[frameSize + i * 2 + 1]; // Cr (V)
        }

        return output;
    }

    public static byte[] YV12toYUV420Planar(byte[] input, byte[] output, int width, int height) {
        /*
         * COLOR_FormatYUV420Planar is I420 which is like YV12, but with U and V reversed.
         * So we just have to reverse U and V.
         */
        final int frameSize = width * height;
        final int qFrameSize = frameSize / 4;

        System.arraycopy(input, 0, output, 0, frameSize); // Y
        System.arraycopy(input, frameSize, output, frameSize + qFrameSize, qFrameSize); // Cr (V)
        System.arraycopy(input, frameSize + qFrameSize, output, frameSize, qFrameSize); // Cb (U)

        return output;
    }



    public void hFlipYV12(byte[] input, byte[] output, int imageWidth, int imageHeight) {

        int nIndex=0;
        int i = 0;
        /* First flip the Y plane */
        for (int y = 0; y < imageHeight; y++) {
            nIndex += imageWidth;
            for (int x = 0; x < imageWidth; x++) {
                output[i++] = input[--nIndex];
            }
            nIndex += imageWidth;
        }

        /* Now flip the V plane */
        for (int y = 0; y < imageHeight  / 2; y++) {
            nIndex += imageWidth / 2;
            for (int x = 0; x < imageWidth / 2; x++) {
                output[i++] = input[--nIndex];
            }
            nIndex += imageWidth / 2;
        }

        /* Last flip the U plane */
        for (int y = 0; y < imageHeight / 2; y++) {
            nIndex += imageWidth / 2;
            for (int x = 0; x < imageWidth / 2; x++) {
                output[i++] = input[--nIndex];
            }
            nIndex += imageWidth / 2;
        }
    }

    public static void vFlipYV12(byte[] input, byte[] output, int imageWidth, int imageHeight) {
        final int frameSize = imageWidth * imageHeight;
        final int qFrameSize = frameSize / 4;

        int nIndex=0;
        for (int y = 0; y < imageHeight; y++) {
            System.arraycopy(input, nIndex, output , frameSize -nIndex - imageWidth, imageWidth);
            nIndex += imageWidth;
        }

        nIndex = 0;
        for (int y = 0; y < imageHeight/2; y++) {
            System.arraycopy(input, frameSize + nIndex, output , frameSize + qFrameSize - nIndex - imageWidth/2, imageWidth/2);
            nIndex += imageWidth/2;
        }

        nIndex = 0;
        for (int y = 0; y < imageHeight/2; y++) {
            System.arraycopy(input, frameSize + qFrameSize + nIndex,  output , frameSize + 2*qFrameSize - nIndex - imageWidth/2, imageWidth/2);
            nIndex += imageWidth/2;
        }

        return;
    }

    public static void rotateYV12(byte[] input, byte[] output, int imageWidth, int imageHeight, int rotation, int cameraType){
        if (rotation == 0) {
            System.arraycopy(input, 0, output, 0, input.length);
            return;
        }

        if (cameraType == Camera.CameraInfo.CAMERA_FACING_BACK) {
            if (rotation == 90) {
                rotateYV12Degree90(input, output, imageWidth, imageHeight);
            } else if (rotation == 180) {
                rotateYV12Degree180(input, output, imageWidth, imageHeight);
            }
        }

        if (cameraType == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            if (rotation == 90) {
                rotateYV12Degree270(input, output, imageWidth, imageHeight);
            } else if (rotation ==180) {
                rotateYV12Degree180(input, output, imageWidth, imageHeight);
            }
        }
    }

    public static void rotateYV12Degree90(byte[] input, byte[] output, int imageWidth, int imageHeight) {
        final int frameSize = imageWidth * imageHeight;
        final int qFrameSize = frameSize / 4;

        // Rotate the Y luma
        int i = 0;
        for (int x = 0;x < imageWidth;x++) {
            for(int y = imageHeight-1; y >= 0; y--)
            {
                output[i] = input[y*imageWidth+x];
                i++;
            }
        }

        //Rotate the V and U color components
        for (int x = 0; x < imageWidth/2; x++) {
            for(int y = imageHeight/4 - 1; y >= 0; y--) {
                //Rotate the V color components
                output[i] = input[frameSize + y * imageWidth + imageWidth/2 + x];
                //Rotate the U color components
                output[qFrameSize + i] = input[frameSize + qFrameSize + y * imageWidth + imageWidth/2 + x];
                i++;

                //Rotate the V color components
                output[i] = input[frameSize + y * imageWidth + x];
                //Rotate the U color components
                output[qFrameSize + i] = input[frameSize + qFrameSize + y * imageWidth + x];
                i++;
            }
        }
    }

    public static void rotateYV12Degree180(byte[] input, byte[] output, int imageWidth, int imageHeight) {
        final int frameSize = imageWidth * imageHeight;
        final int qFrameSize = frameSize / 4;

        // Rotate the Y luma
        int i = 0;
        for (int y = imageHeight - 1; y >= 0; y--) {
            for (int x = imageWidth - 1; x >= 0; x --) {
                output[i] = input[y*imageWidth + x];
                i++;
            }
        }

        //Rotate the V and U color components
        for (int y = imageHeight/2 - 1; y >= 0; y--) {
            for (int x = imageWidth/2 - 1; x >= 0; x--) {
                //Rotate the V color components
                output[i] = input[frameSize + y * imageWidth/2 + x];
                //Rotate the U color components
                output[qFrameSize + i] = input[frameSize + qFrameSize + y * imageWidth/2 + x];
                i++;
            }
        }
    }

    public static void rotateYV12Degree270(byte[] input, byte[] output, int imageWidth, int imageHeight) {
        final int frameSize = imageWidth * imageHeight;
        final int qFrameSize = frameSize / 4;

        //Rotate the Y luma
        int i = 0;
        for (int x = 1; x <= imageWidth; x++) {
            for (int y = 1; y <= imageHeight; y++) {
                output[i++] = input[y * imageWidth - x];
            }
        }

        //Rotate the V and U color components
        for (int x = 1; x <= imageWidth/2; x++) {
            for (int y = 1; y <= imageHeight/2; y++) {
                output[i] = input[frameSize + y * imageWidth/2 - x];
                output[qFrameSize + i] = input[frameSize + qFrameSize + y * imageWidth/2 - x];
                i++;
            }
        }
    }

    //rotate degrees of sensor.
    //@see http://www.wordsaretoys.com/2013/10/25/roll-that-camera-zombie-rotation-and-coversion-from-yv12-to-yuv420planar/
    //@see http://stackoverflow.com/questions/23107057/rotate-yuv420-nv21-image-in-android
    public static void rotateYV12Deform(byte[] input, byte[] output, int width, int height, int rotation, int cameraType) {
        if (rotation == 0 && cameraType == Camera.CameraInfo.CAMERA_FACING_BACK) {
            System.arraycopy(input, 0, output, 0, input.length);
            return;
        }
        boolean swap = (rotation == 90 || rotation == 270);
        boolean yflip = (rotation == 90 || rotation == 180);
        boolean xflip = (rotation == 0 || (rotation == 90 && cameraType == Camera.CameraInfo.CAMERA_FACING_FRONT));
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int xo = x, yo = y;
                int w = width, h = height;
                int xi = xo, yi = yo;
                if (swap) {
                    xi = w * yo / h;
                    yi = h * xo / w;
                }
                if (yflip) {
                    yi = h - yi - 1;
                }
                if (xflip) {
                    xi = w - xi - 1;
                }
                output[w * yo + xo] = input[w * yi + xi];
                int fs = w * h;
                int qs = (fs >> 2);
                xi = (xi >> 1);
                yi = (yi >> 1);
                xo = (xo >> 1);
                yo = (yo >> 1);
                w = (w >> 1);
                int ui = fs + w * yi + xi;
                int uo = fs + w * yo + xo;
                int vi = qs + ui;
                int vo = qs + uo;
                output[uo] = input[ui];
                output[vo] = input[vi];
            }
        }
    }
}
