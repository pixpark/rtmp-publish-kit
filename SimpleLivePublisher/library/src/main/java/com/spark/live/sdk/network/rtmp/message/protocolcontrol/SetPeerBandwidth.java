package com.spark.live.sdk.network.rtmp.message.protocolcontrol;

import com.spark.live.sdk.network.rtmp.message.RtmpMessage;
import com.spark.live.sdk.network.rtmp.message.MessageHeader;
import com.spark.live.sdk.util.LogUtil;


/**
 *
 * Created by devzhaoyou on 8/25/16.
 */

public class SetPeerBandwidth extends RtmpMessage {

    public static final int LIMIT_TYPE_MIN_VALUE = 0;
    public static final int HARD = 0;
    public static final int SOFT = 1;
    public static final int DYNAMIC = 2;
    public static final int LIMIT_TYPE_MAX_VALUE = 2;

    private byte limitType;
    private int peerBandwidth;

    public SetPeerBandwidth(MessageHeader header) {
        this.header = header;
    }

    public void setLimitType(byte limitType) {
        if (limitType < LIMIT_TYPE_MIN_VALUE ||
                limitType > LIMIT_TYPE_MAX_VALUE) {
            LogUtil.e("The limitType must be between LIMIT_TYPE_MIN_VALUE and LIMIT_TYPE_MAX_VALUE");
            return;
        }
        this.limitType = limitType;
    }

    public byte getLimitType() {
        return limitType;
    }

    public void setPeerBandwidth(int peerBandwidth) {
        this.peerBandwidth = peerBandwidth;
    }

    public int getPeerBandwidth() {
        return peerBandwidth;
    }

    @Override
    public String toString() {
        return "SetPeerBandwidth{" +
                "header=" + header.toString() +
                ", peerBandwidth =" + peerBandwidth +
                ", limitType= " + limitType +
                '}';
    }
}
