package com.spark.live.sdk.media.packet.tag.builder;

import com.spark.live.sdk.media.packet.tag.common.FLVTag;

import java.util.List;

/**
 *
 * Created by devzhaoyou on 8/9/16.
 */
public class BuildCommander {

    private IFlvTagBuilder builder = null;

    public BuildCommander(IFlvTagBuilder builder) {
        this.builder = builder;
    }

    public List<FLVTag> build(DataValueObject dataVo) {
        builder.buildTagHeader(dataVo);
        builder.buildTagData(dataVo);
        return builder.buildTag();
    }


}
