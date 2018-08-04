package com.spark.live.sdk.media.codec;

import android.media.MediaCodec;


import java.nio.ByteBuffer;

/**
 *
 * Created by devzhaoyou on 8/30/16.
 */

public interface IOnEncodeAVData {

    void onEncodeVideo(ByteBuffer buffer, MediaCodec.BufferInfo info);

    void onEncodeAudio(ByteBuffer buffer, MediaCodec.BufferInfo info);
}
