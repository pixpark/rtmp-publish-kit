package com.spark.live.sdk.media.packet.tag.builder.video;
import com.spark.live.sdk.media.packet.tag.builder.IFlvTagBuilder;


import java.util.HashMap;

/**
 *
 * Created by devzhaoyou on 8/11/16.
 */
public abstract class VideoTagBuilder implements IFlvTagBuilder{

    /**Type of video frame*/
    protected int frameType4Bits;

    /**Codec Identifier*/
    private int codecID4Bits = 0x07;

    protected HashMap<String, Object> params;

    /*protected VideoTagHeader header;
    protected VideoTagData data;*/

    public VideoTagBuilder(HashMap<String, Object> params) {
        this.params = params;
    }





}
