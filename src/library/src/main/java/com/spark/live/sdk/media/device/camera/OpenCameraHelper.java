package com.spark.live.sdk.media.device.camera;
import android.hardware.Camera;

import com.spark.live.sdk.util.LogUtil;

/**
 * Abstraction over the {@link Camera} API that helps open them and return their metadata.
 */
@SuppressWarnings("deprecation")
public final class OpenCameraHelper {


    private OpenCameraHelper() {
    }


    /**
     * Opens the requested camera with {@link Camera#open(int)}, if one exists.
     *
     * @param cameraId camera ID of the camera to use. A negative value
     *                  means "no preference", in which case a rear-facing
     *                 camera is returned if possible or else any camera
     * @return handle to {@link CameraKeeper} that was opened
     */
    public static CameraKeeper open(int cameraId) {

        int numCameras = Camera.getNumberOfCameras();
        if (numCameras == 0) {
            LogUtil.e("The device has no cameras!");
            return null;
        }

        boolean explicitRequest = cameraId >= 0;

        Camera.CameraInfo selectedCameraInfo = null;
        int index;
        if (explicitRequest) {
            index = cameraId;
            selectedCameraInfo = new Camera.CameraInfo();
            Camera.getCameraInfo(index, selectedCameraInfo);
        } else {
            index = 0;
            while (index < numCameras) {
                Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
                Camera.getCameraInfo(index, cameraInfo);
                CameraFacing reportedFacing = CameraFacing.values()[cameraInfo.facing];
                if (reportedFacing == CameraFacing.BACK) {
                    selectedCameraInfo = cameraInfo;
                    break;
                }
                index++;
            }
        }

        Camera camera;
        if (index < numCameras) {
            LogUtil.i("Opening camera #" + index);
            camera = Camera.open(index);
        } else {
            if (explicitRequest) {
                LogUtil.w("Requested camera does not exist: " + cameraId);
                camera = null;
            } else {
                LogUtil.i("No camera facing " + CameraFacing.BACK + "; returning camera #0");
                camera = Camera.open(0);
                selectedCameraInfo = new Camera.CameraInfo();
                Camera.getCameraInfo(0, selectedCameraInfo);
            }
        }

        if (camera == null) {
            return null;
        }
        return new CameraKeeper(index,
                camera,
                CameraFacing.values()[selectedCameraInfo.facing],
                selectedCameraInfo.orientation);
    }

}
