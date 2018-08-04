package com.spark.live.sdk.network.rtmp.publisher.exception;

import com.spark.live.sdk.network.rtmp.RTMPException;

/**
 *
 * Created by devzhaoyou on 8/29/16.
 */

public class RTMPCreateStreamException extends RTMPException {
    public RTMPCreateStreamException() {
        super();
    }

    public RTMPCreateStreamException(String detailMessage) {
        super(detailMessage);
    }

    public RTMPCreateStreamException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public RTMPCreateStreamException(Throwable throwable) {
        super(throwable);
    }
}
