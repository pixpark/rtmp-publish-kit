package com.spark.live.sdk.network.rtmp;

/**
 *
 * Created by devzhaoyou on 8/29/16.
 */

public class RTMPException extends Exception {
    public RTMPException() {
        super();
    }

    public RTMPException(String detailMessage) {
        super(detailMessage);
    }

    public RTMPException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public RTMPException(Throwable throwable) {
        super(throwable);
    }
}
