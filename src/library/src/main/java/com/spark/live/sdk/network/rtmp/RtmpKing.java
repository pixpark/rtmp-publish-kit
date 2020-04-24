package com.spark.live.sdk.network.rtmp;

import com.spark.live.sdk.network.rtmp.message.RtmpMessage;
import com.spark.live.sdk.network.rtmp.publisher.PublisherKnight;
import com.spark.live.sdk.network.rtmp.publisher.exception.RTMPConnectException;
import com.spark.live.sdk.network.rtmp.publisher.exception.RTMPCreateStreamException;
import com.spark.live.sdk.util.LogUtil;


/**
 *
 * Created by devzhaoyou on 8/30/16.
 */

public class RtmpKing implements IRtmpKing {

    public static final int PUBLISH_KNIGHT = 100;

    private PublisherKnight knight;

    public RtmpKing(int type, String rtmpUrl) {
        switch (type) {
            case PUBLISH_KNIGHT:
                break;
        }
    }

    @Override
    public void createRtmp() throws RTMPConnectException, RTMPCreateStreamException {
    }

    @Override
    public void sendMessage(RtmpMessage message) {
    }

    @Override
    public void destroyRtmp() {
        LogUtil.i("RtmpKing: switch to destroy...");
    }
}
