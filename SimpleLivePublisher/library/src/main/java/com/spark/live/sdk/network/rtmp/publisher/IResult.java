package com.spark.live.sdk.network.rtmp.publisher;

/**
 *
 * Created by devzhaoyou on 9/12/16.
 */

interface IResult {

    boolean onConnectResult(int transaction, long timeout);

    boolean onCreateStreamResult(int transaction, long timeout);
}
