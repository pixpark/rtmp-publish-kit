package com.spark.live.sdk.network.rtmp.message.protocolcontrol;

import com.spark.live.sdk.network.rtmp.message.RtmpMessage;
import com.spark.live.sdk.network.rtmp.message.MessageHeader;
import com.spark.live.sdk.util.BinaryUtil;

/**
 *
 * Created by devzhaoyou on 8/25/16.
 */

public class WindowAckSize extends RtmpMessage{

    private int windowAckSize = 0;

    public WindowAckSize(MessageHeader header) {
        this.header = header;
    }

    public int getWindowAckSize() {
        return windowAckSize;
    }

    public void setWindowAckSize(int windowAckSize) {
        this.windowAckSize = windowAckSize;
    }

    @Override
    public byte[] toBinary() {
        payload = BinaryUtil.getBytesFromIntValue(windowAckSize, 4);
        return payload;
    }

    @Override
    public String toString() {
        return "WindowAckSize{" +
                "header=" + header.toString() +
                ", windowAckSize =" + windowAckSize +
                '}';
    }
}
