package com.spark.live.sdk.media.device.audio;

import com.spark.live.sdk.media.device.OnAVDataCallback;

/**
 *
 * Created by devzhaoyou on 9/7/16.
 */

public interface IAudioDevice {

    void setAVDataCallback(OnAVDataCallback callback);

    void startRecorder();

    void resumeRecorder();

    void pauseRecorder();

    void stopRecorder();
}
