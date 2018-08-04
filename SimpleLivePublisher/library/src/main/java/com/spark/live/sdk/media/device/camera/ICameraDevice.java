package com.spark.live.sdk.media.device.camera;

import android.content.Context;
import android.os.Handler;
import android.view.SurfaceHolder;

import com.spark.live.sdk.media.device.OnAVDataCallback;

/**
 * 摄像头操作接口 按生命周期进行调用避免内存泄露
 * Created by devzhaoyou on 9/7/16.
 */

public interface ICameraDevice {

    Handler getHandler();

    /**
     * 设置数据回调接口
     * @param callback 数据回调接口
     */
    void setAVDataCallback(OnAVDataCallback callback);

    /**
     * 设置摄像头事件处理回调接口
     * @param callback 事件接口
     */
    void setCameraEventCallback(ICameraEvent callback);

    /**
     * 打开索引指定的摄像头 并设置预览句柄
     * @param index 摄像头索引
     * @param holder 预览句柄
     */
    void openCamera(int index, SurfaceHolder holder);

    /**
     * 旋转摄像头
     * @param context 全局上下文
     */
    void rotateCamera(Context context);

    /**
     * 切换摄像头
     */
    void switchCamera();

    /**
     * 开始预览
     */
    void startPreview();

    /**
     * 停止预览
     */
    void stopPreview();

    /**
     * 退出
     */
    void exit();
}
