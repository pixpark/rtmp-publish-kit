package com.spark.live.sdk.media.device.camera;

/**
 *
 * Created by devzhaoyou on 9/7/16.
 */

public interface ICameraEvent {
    void onCameraOpen(CameraKeeper camera);

    void onErrorEvent(String error);
}
