package com.spark.live.sdk.media.codec;

import android.media.MediaCodec;

import com.spark.live.sdk.util.LogUtil;

import java.nio.ByteBuffer;

/**
 *
 * Created by devzhaoyou on 9/17/16.
 */

public interface ICodecCallback {
    void onStatusChanged(CodecManager.CodecState from, CodecManager.CodecState to);
    void onEncodeAudio(ByteBuffer buffer, MediaCodec.BufferInfo bufferInfo);
    void onEncodeVideo(ByteBuffer buffer, MediaCodec.BufferInfo bufferInfo);
    void onError(CodecManager.CodecState state, String error);

    abstract class stub implements ICodecCallback{
        @Override
        public void onStatusChanged(CodecManager.CodecState from, CodecManager.CodecState to) {
            LogUtil.i("The state has changed from: " + from + " to: " + to);
        }
    }
}
