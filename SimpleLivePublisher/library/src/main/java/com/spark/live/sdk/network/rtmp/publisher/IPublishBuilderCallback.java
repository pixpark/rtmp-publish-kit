package com.spark.live.sdk.network.rtmp.publisher;

/**
 * PublisherKnight 建造者回调接口
 * Created by devzhaoyou on 9/18/16.
 */

public interface IPublishBuilderCallback {

    /**
     * 建造成功回调
     * @param publisher rtmp publish接口
     */
    void onBuildPublisher(IPublisher publisher);

    /**
     * 错误
     * @param error 错误信息
     */
    void onBuildError(String error);
}
