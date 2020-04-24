package com.spark.live.sdk.media.device;

/**
 *
 * Created by devzhaoyou on 8/3/16.
 */
public interface OnAVDataCallback {

    void onVideoData(byte[] frame, Object... args);

    void onAudioData(byte[] data, Object... args);

    void onAudioData(short[] data, Object... args);

    abstract class Stub implements OnAVDataCallback{
        @Override
        public void onVideoData(byte[] frame, Object... args) {

        }

        @Override
        public void onAudioData(byte[] data, Object... args) {

        }

        @Override
        public void onAudioData(short[] data, Object... args) {

        }
    }
}
