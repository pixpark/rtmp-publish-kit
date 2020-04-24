package com.spark.live.sdk.network.rtmp.amf;

/**
 *
 * Created by devzhaoyou on 8/29/16.
 */

public class AMFBadMarkerException extends Exception{

    public AMFBadMarkerException() {
        super();
    }

    public AMFBadMarkerException(String detailMessage) {
        super(detailMessage);
    }

    public AMFBadMarkerException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public AMFBadMarkerException(Throwable throwable) {
        super(throwable);
    }
}
