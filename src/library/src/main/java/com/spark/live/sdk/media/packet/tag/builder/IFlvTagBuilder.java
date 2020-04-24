package com.spark.live.sdk.media.packet.tag.builder;

import com.spark.live.sdk.media.packet.tag.common.FLVTag;

import java.util.List;

/**
 *
 * Created by devzhaoyou on 8/9/16.
 */
public interface IFlvTagBuilder {

    void buildTagHeader(DataValueObject dataVo);

    void buildTagData(DataValueObject dataVo);

    List<FLVTag> buildTag();
}
