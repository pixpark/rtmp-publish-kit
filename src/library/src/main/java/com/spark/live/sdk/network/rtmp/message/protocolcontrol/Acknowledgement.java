package com.spark.live.sdk.network.rtmp.message.protocolcontrol;

import com.spark.live.sdk.network.rtmp.message.MessageHeader;
import com.spark.live.sdk.network.rtmp.message.RtmpMessage;

/**
 *
 * Created by devzhaoyou on 8/26/16.
 */

public class Acknowledgement extends RtmpMessage {

    private int currentNumber = 0;

    public Acknowledgement(MessageHeader header) {
        this.header = header;
    }

    public int getCurrentNumber() {
        return currentNumber;
    }

    public void setCurrentNumber(int currentNumber) {
        this.currentNumber = currentNumber;
    }

    @Override
    public String toString() {
        return "Acknowledgement{" +
                "header=" + header.toString() +
                "currentNumber=" + currentNumber +
                '}';
    }
}
