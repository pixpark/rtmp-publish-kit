package com.spark.live.sdk.media.device.camera;

import android.content.Context;
import android.graphics.Point;
import android.hardware.Camera;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;

import com.spark.live.sdk.util.CameraConfigurationUtils;
import com.spark.live.sdk.util.ImageUtil;
import com.spark.live.sdk.util.LogUtil;

/**
 * Singleton Instance
 * Created by devzhaoyou on 9/7/16.
 */

public class CameraConfigManager {
    public final static int DEGREES_0 = 0;
    public final static int DEGREES_90 = 90;
    public final static int DEGREES_180 = 180;
    public final static int DEGREES_270 = 270;

    private static final Object lock = new Object();

    private Point desiredPreviewSize, finalPreviewSize;
    private int imageFormat;
    private boolean isTorch;
    private boolean isAutoFocus;
    private String whiteBalance;
    private String sceneMode;
    private int[] fpsRange;
    private int rotateDegree;
    private byte[] buffer;

    private static CameraConfigManager instance = null;

    public static CameraConfigManager getInstance() {
        synchronized (lock) {
            if (instance == null) {
                instance = new CameraConfigManager();
            }
            return instance;
        }
    }

    private CameraConfigManager() {}

    public byte[] getBuffer() {
        if (buffer == null) {
            if (finalPreviewSize != null) {
                buffer = new byte[ImageUtil.getYuvBufferSize(finalPreviewSize.x, finalPreviewSize.y)];
            } else {
                LogUtil.e("The preview size is null! Could not create frame buffer!");
            }
        }
        return buffer;
    }

    public CameraConfigManager setDesiredPreviewSize(Point desiredPreviewSize) {
        this.desiredPreviewSize = desiredPreviewSize;
        return this;
    }



    public CameraConfigManager setImageFormat(int imageFormat) {
        this.imageFormat = imageFormat;
        return this;
    }

    public CameraConfigManager setFlashMode(boolean isTorch) {
        this.isTorch = isTorch;
        return this;
    }

    public CameraConfigManager setWhiteBalance(String whiteBalance) {
        this.whiteBalance = whiteBalance;
        return this;
    }

    public CameraConfigManager setSceneMode(String sceneMode) {
        this.sceneMode = sceneMode;
        return this;
    }

    public CameraConfigManager setFocusMode(boolean isAutoFocus) {
        this.isAutoFocus = isAutoFocus;
        return this;
    }

    public CameraConfigManager setFpsRange(int[] fpsRange) {
        this.fpsRange = fpsRange;
        return this;
    }

    public Point getFinalPreviewSize() {
        return finalPreviewSize;
    }

    public int getImageFormat() {
        return imageFormat;
    }

    public int[] getFpsRange() {
        return fpsRange;
    }

    public int getRotateDegree() {
        return rotateDegree;
    }

    /**
     *
     * @return camera rotate degree
     */
    public int getScreenRotateDegree() {
        return rotateDegree;
    }

    /**
     * release the instance
     */
    public void release() {
        instance = null;
    }

    /**
     *
     * @param theCamera instance
     * @param context context
     */
    @SuppressWarnings("deprecation")
    public void setCamera(CameraKeeper theCamera, Context context) {
        if (theCamera != null) {
            Camera instance = theCamera.getCamera();
            Camera.Parameters parameters = instance.getParameters();

            CameraConfigurationUtils.setPreviewFormat(parameters, imageFormat);
            CameraConfigurationUtils.setTorch(parameters, isTorch);
            CameraConfigurationUtils.setBestPreviewFPS(parameters, fpsRange[0], fpsRange[1]);
            CameraConfigurationUtils.setSceneMode(parameters, sceneMode);
            CameraConfigurationUtils.setFocus(parameters, isAutoFocus, false, false);
            CameraConfigurationUtils.setVideoStabilization(parameters);
            CameraConfigurationUtils.setWhiteBalance(parameters, whiteBalance);

            finalPreviewSize = CameraConfigurationUtils.findBestPreviewSizeValue(parameters, desiredPreviewSize);
            LogUtil.i("CameraManager: Preview width: " + finalPreviewSize.x + " Preview height: " + finalPreviewSize.y);
            parameters.setPreviewSize(finalPreviewSize.x, finalPreviewSize.y);
            buffer = new byte[ImageUtil.getYuvBufferSize(finalPreviewSize.x, finalPreviewSize.y)];

            instance.setParameters(parameters);
            rotateCamera(theCamera, context);
        } else {
            LogUtil.e("CameraConfigManager set Camera: The camera instance is null!");
        }
    }

    /**
     *
     * @param theCamera instance
     * @param context context
     */
    public void rotateCamera(CameraKeeper theCamera, Context context) {
        int screenRotateDegree = getScreenRotateDegree(context);
        int result;
        if (theCamera.getFacing() == CameraFacing.FRONT) {
            result = (theCamera.getOrientation() + screenRotateDegree) % 360;
            result = (360 - result) % 360;
        } else {
            result = (theCamera.getOrientation() - screenRotateDegree + 360) % 360;
        }
        this.rotateDegree = result;
        LogUtil.i("The camera will rotate : " + result + "degrees!");
        theCamera.getCamera().setDisplayOrientation(result);
    }

    /**
     *
     * @param context context
     * @return screen rotate degree
     */
    private int getScreenRotateDegree(Context context) {
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        int rotation = display.getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = DEGREES_0;
                break;
            case Surface.ROTATION_90:
                degrees = DEGREES_90;
                break;
            case Surface.ROTATION_180:
                degrees = DEGREES_180;
                break;
            case Surface.ROTATION_270:
                degrees = DEGREES_270;
                break;
        }
        return degrees;
    }
}
