package com.spark.live.sdk.network.rtmp;

import com.spark.live.sdk.network.rtmp.chunk.RtmpChunk;
import com.spark.live.sdk.network.rtmp.message.RtmpMessage;

import java.util.List;

/**
 *
 * Created by devzhaoyou on 8/18/16.
 */

public interface IKnightSword {

    List<RtmpChunk> hackMessage(RtmpMessage message);

    void setReset();

}
