package com.spark.live.sdk.network.rtmp.publisher.exception;

import com.spark.live.sdk.network.rtmp.RTMPException;

/**
 *
 * Created by devzhaoyou on 8/29/16.
 */

public class RTMPConnectException extends RTMPException {


    public RTMPConnectException() {
        super();
    }

    public RTMPConnectException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public RTMPConnectException(String detailMessage) {
        super(detailMessage);
    }

    public RTMPConnectException(Throwable throwable) {
        super(throwable);
    }
}
