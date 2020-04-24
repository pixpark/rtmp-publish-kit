package com.spark.live.sdk.network.rtmp.message;


import com.spark.live.sdk.network.rtmp.IBinary;
import com.spark.live.sdk.util.BinaryUtil;

import java.util.Arrays;

/**
 *
 * Created by devzhaoyou on 8/15/16.
 */

public class RtmpMessage implements IBinary {

    protected MessageHeader header;

    protected byte[] payload;


    public RtmpMessage() {

    }

    @Override
    public byte[] toBinary() {
        return payload;
    }

    public MessageHeader getHeader() {
        return header;
    }

    public void setHeader(MessageHeader header) {
        this.header = header;
    }

    public byte[] getPayload() {
        return payload;
    }

    public void setPayload(byte[] payload) {
        this.payload = payload;
    }

    @Override
    public String toString() {
        return "RtmpMessage{" +
                "header=" + (header == null ? "" : header.toString()) +
                ", payload=" + (payload == null ? "" : BinaryUtil.printByteToHex(payload)) +
                '}';
    }
}
