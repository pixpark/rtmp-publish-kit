package com.spark.live.sdk.network.rtmp.message.protocolcontrol;

import com.spark.live.sdk.network.rtmp.message.RtmpMessage;
import com.spark.live.sdk.network.rtmp.message.MessageHeader;

/**
 *
 * Created by devzhaoyou on 8/25/16.
 */

public class SetChunkSize extends RtmpMessage {

    private int chunkSize = 0;

    public SetChunkSize(MessageHeader header) {
        this.header = header;
    }

    public int getChunkSize() {
        return chunkSize;
    }

    public void setChunkSize(int chunkSize) {
        this.chunkSize = chunkSize;
    }

    @Override
    public String toString() {
        return "SetChunkSize{" +
                "header=" + header.toString() +
                ", setChunkSize=" + chunkSize +
                '}';
    }
}
