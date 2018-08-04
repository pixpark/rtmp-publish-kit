package com.spark.live.sdk.network.rtmp;

/**
 *
 * Created by devzhaoyou on 9/18/16.
 */

public interface IRTMPCallback {

    void onCreateSocket();

    void onHandshake();

    void onError(String error);
}
