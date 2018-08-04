package com.spark.live.sdk.cacher;

/**
 *
 * Created by devzhaoyou on 9/8/16.
 */

public interface IMuxer {

    void startMuxer(String url);

    void sendFrame(CacheFrame frame);

    void closeMuxer();
}
