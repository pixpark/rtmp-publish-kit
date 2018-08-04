package com.spark.live.sdk.network.rtmp;

import com.spark.live.sdk.network.rtmp.message.RtmpMessage;
import com.spark.live.sdk.network.rtmp.publisher.exception.RTMPConnectException;
import com.spark.live.sdk.network.rtmp.publisher.exception.RTMPCreateStreamException;

/**
 *
 * Created by devzhaoyou on 8/17/16.
 */

public interface IRtmpKing {

    void createRtmp() throws RTMPConnectException, RTMPCreateStreamException;

    void destroyRtmp();

    void sendMessage(RtmpMessage message);
}

