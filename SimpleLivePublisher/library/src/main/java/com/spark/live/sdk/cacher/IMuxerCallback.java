package com.spark.live.sdk.cacher;

/**
 *
 * Created by devzhaoyou on 9/13/16.
 */

public interface IMuxerCallback {

    void onMuxerResume();

    void onError(String error);
}
