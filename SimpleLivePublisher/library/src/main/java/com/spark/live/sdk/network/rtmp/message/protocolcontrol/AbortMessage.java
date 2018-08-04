package com.spark.live.sdk.network.rtmp.message.protocolcontrol;

import com.spark.live.sdk.network.rtmp.message.MessageHeader;
import com.spark.live.sdk.network.rtmp.message.RtmpMessage;

/**
 *
 * Created by devzhaoyou on 8/26/16.
 */

public class AbortMessage extends RtmpMessage{

    private int chunkStreamId = -1;

    public AbortMessage(MessageHeader header) {
        this.header = header;
    }

    public int getChunkStreamId() {
        return chunkStreamId;
    }

    public void setChunkStreamId(int chunkStreamId) {
        this.chunkStreamId = chunkStreamId;
    }

    @Override
    public String toString() {
        return "AbortMessage{" +
                "header=" + header.toString() +
                "chunkStreamId=" + chunkStreamId +
                '}';
    }
}
