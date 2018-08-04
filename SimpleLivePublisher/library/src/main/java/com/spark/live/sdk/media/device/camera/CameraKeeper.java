package com.spark.live.sdk.media.device.camera;

import android.hardware.Camera;

import com.spark.live.sdk.media.device.camera.CameraFacing;

/**
 * Represents an open {@link Camera} and its metadata, like facing direction and orientation.
 */
@SuppressWarnings("ALL")
public final class CameraKeeper {

    private final int index;
    private final Camera camera;
    private final CameraFacing facing;
    private final int orientation;

    public CameraKeeper(int index, Camera camera, CameraFacing facing, int orientation) {
        this.index = index;
        this.camera = camera;
        this.facing = facing;
        this.orientation = orientation;
    }

    public Camera getCamera() {
        return camera;
    }

    public CameraFacing getFacing() {
        return facing;
    }

    public int getOrientation() {
        return orientation;
    }

    @Override
    public String toString() {
        return "Camera #" + index + " : " + facing + ',' + orientation;
    }

}
