package com.spark.live.sdk.network.rtmp.publisher;

import com.spark.live.sdk.network.rtmp.IRTMPCallback;

/**
 *
 * Created by devzhaoyou on 9/18/16.
 */

public interface IPublisherCallback extends IRTMPCallback{

    void onConnect();

    void onCreateStream();

    void onSendPublish();
}
