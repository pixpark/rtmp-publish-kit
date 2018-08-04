package com.spark.live.sdk.network.rtmp.publisher;


import com.spark.live.sdk.network.rtmp.message.RtmpMessage;

import java.io.IOException;

/**
 *
 * Created by devzhaoyou on 8/17/16.
 */

public interface IPublisher {

    String PUBLISH_TYPE_LIVE = "live";
    String PUBLISH_TYPE_RECORD = "record";
    String PUBLISH_TYPE_APPEND = "append";

    /**
     * 建立rtmp连接
     * @return true 连接成功 false 失败
     */
    boolean rtmpConnect() throws IOException;

    /**
     * 创建传输流
     * @return true 创建成功 false 失败
     */
    boolean createStream() throws IOException;

    /**
     * 发送开始推流消息
     * @param type 推流类型 live/record/append
     * @return true 发送成功 false 发送失败
     */
    boolean publish(String type) throws IOException;

    /**
     * 将message切分为chunk并发送
     * @param message rtmp 消息
     * @throws IOException
     */
    void sendMessage(RtmpMessage message) throws IOException;

    /**
     * 关闭所有
     */
    void close();

}
