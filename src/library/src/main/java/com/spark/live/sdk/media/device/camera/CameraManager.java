package com.spark.live.sdk.media.device.camera;

import android.content.Context;
import android.hardware.Camera;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.view.SurfaceHolder;

import com.spark.live.sdk.media.device.OnAVDataCallback;
import com.spark.live.sdk.util.LogUtil;

import java.io.IOException;

/**
 * Camera管理类负责操作摄像头
 * Created by devzhaoyou on 9/7/16.
 */
public class CameraManager extends HandlerThread implements ICameraDevice {

    public static final int CAMERA_OPEN_CAMERA_BACK = 0;
    public static final int CAMERA_OPEN_CAMERA_FRONT = 1;
    private static final int CAMERA_HANDLE_FRAME = 4;

    private static final Object lock = new Object();

    private Handler handler = null;
    private SurfaceHolder holder = null;
    private CameraKeeper openedCamera = null;
    private ICameraEvent callback = null;
    private OnAVDataCallback frameCallback = null;
    private boolean previewing;

    private static ICameraDevice mInstance = null;

    public static ICameraDevice getInstance(String name) {
        synchronized (lock) {
            if (mInstance == null) {
                mInstance = new CameraManager(name);
            }
            return mInstance;
        }
    }

    private CameraManager(String name) {
        super(name);
        start();
        handler = new Handler(getLooper(), new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                switch (msg.what) {
                    case CAMERA_OPEN_CAMERA_BACK:
                        holder = (SurfaceHolder) msg.obj;
                        openCamera(CAMERA_OPEN_CAMERA_BACK, holder);
                        break;
                    case CAMERA_OPEN_CAMERA_FRONT:
                        holder = (SurfaceHolder) msg.obj;
                        openCamera(CAMERA_OPEN_CAMERA_FRONT, holder);
                        break;
                    case CAMERA_HANDLE_FRAME:
                        byte[] data = (byte[]) msg.obj;
                        handleFrameBuffer(data);
                        break;
                }
                return false;
            }
        });
    }

    public void openCamera(int index, SurfaceHolder holder) {
        synchronized (lock) {
            if (callback != null) {
                if (safeCameraOpen(index)) {
                    try {
                        openedCamera.getCamera().setPreviewDisplay(holder);
                        callback.onCameraOpen(openedCamera);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    callback.onErrorEvent("");
                }

            } else {
                LogUtil.e("CameraManager openCamera: The Event callback is null!");
            }
        }

    }


    public void setAVDataCallback(OnAVDataCallback callback) {
        frameCallback = callback;
    }


    @Override
    public void startPreview() {
        synchronized (lock) {
            CameraKeeper theCamera = openedCamera;
            if (theCamera != null && !previewing) {
                theCamera.getCamera().startPreview();
                theCamera.getCamera().setPreviewCallbackWithBuffer(previewCallback);
                theCamera.getCamera().addCallbackBuffer(CameraConfigManager.getInstance().getBuffer());
                previewing = true;
            }
        }

    }

    @Override
    public void rotateCamera(Context context) {
        if (openedCamera != null) {
            CameraConfigManager.getInstance().rotateCamera(openedCamera, context);
        }
    }

    @Override
    public void switchCamera() {
        if (openedCamera != null) {
            int index = openedCamera.getFacing().getIndex();
            if (index == CAMERA_OPEN_CAMERA_BACK) {
                index = CAMERA_OPEN_CAMERA_FRONT;
            } else if (index == CAMERA_OPEN_CAMERA_FRONT) {
                index = CAMERA_OPEN_CAMERA_BACK;
            }
            releaseCameraAndPreview();
            openCamera(index, holder);
        }

    }

    @Override
    public void stopPreview() {
        synchronized (lock) {
            CameraKeeper theCamera = openedCamera;
            if (theCamera != null && previewing) {
                theCamera.getCamera().setPreviewCallbackWithBuffer(null);
                theCamera.getCamera().stopPreview();
                previewing = false;

            }
        }
    }

    @Override
    public void setCameraEventCallback(ICameraEvent callback) {
        synchronized (lock) {
            this.callback = callback;
        }
    }

    @Override
    public void exit() {
        releaseCameraAndPreview();
        this.previewCallback = null;
        this.frameCallback = null;
        this.callback = null;
        mInstance = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            quitSafely();
        } else {
            quit();
        }

    }

    @Override
    public Handler getHandler() {
        return handler;
    }

    private boolean safeCameraOpen(int id) {
        synchronized (lock) {
            boolean qOpened = false;
            try {
                releaseCameraAndPreview();
                openedCamera = OpenCameraHelper.open(id);
                qOpened = (openedCamera != null);
            } catch (Exception e) {
                LogUtil.e("CameraManager safeCameraOpen: failed to open Camera");
                e.printStackTrace();
            }

            return qOpened;
        }
    }

    private void releaseCameraAndPreview() {
        stopPreview();
        synchronized (lock) {
            if (openedCamera != null) {
                openedCamera.getCamera().release();
                openedCamera = null;
            }
        }
    }

    private void handleFrameBuffer(byte[] data) {
        if (frameCallback != null && openedCamera != null && previewing) {
            frameCallback.onVideoData(data, openedCamera.getFacing().getIndex());
        } else {
            LogUtil.w("CameraManager handleFrameBuffer: Can not handle buffer!");
        }

    }

    @SuppressWarnings("deprecation")
    private Camera.PreviewCallback previewCallback = new Camera.PreviewCallback() {

        @Override
        public void onPreviewFrame(byte[] data, Camera camera) {
//            Message.obtain(handler, CAMERA_HANDLE_FRAME, data).sendToTarget();

            handleFrameBuffer(data);
            openedCamera.getCamera().addCallbackBuffer(data);
        }
    };

}
