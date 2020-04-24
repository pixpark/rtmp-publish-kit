package com.spark.live.sdk.network.rtmp;

import com.spark.live.sdk.network.rtmp.message.RtmpMessage;

/**
 * Interface for assemble chunk to message
 * Created by devzhaoyou on 8/25/16.
 */

public interface IAssembleLine {

    /**
     * Assemble chunks to Message
     * @return message
     */
    RtmpMessage assembleChunk();

}
