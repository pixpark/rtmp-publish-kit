package com.spark.live.sdk.media.packet.tag.video;

import com.spark.live.sdk.media.packet.tag.common.FLVTag;

import java.nio.ByteBuffer;

/**
 *
 * Created by devzhaoyou on 8/9/16.
 */
public class VideoTag extends FLVTag {

    public static final int FRAME_TYPE_MASK = 0xf0;
    public static final int CODEC_ID_MASK = 0x0f;


    @Override
    public ByteBuffer toBinaryData() {

        VideoTagHeader videoTagHeader = (VideoTagHeader) tagHeader;
        VideoTagData videoTagData = (VideoTagData) tagData;

        byte[] headerArray = videoTagHeader.toBinaryData().array();
        byte[] dataArray = videoTagData.toBinaryData().array();

        binaryTag = ByteBuffer.allocate(headerArray.length + dataArray.length);
        binaryTag.put(headerArray);
        binaryTag.put(dataArray);
        binaryTag.flip();
        return binaryTag;
    }


    @Override
    public VideoTagData getTagData() {
        return (VideoTagData)super.getTagData();
    }

    @Override
    public VideoTagHeader getTagHeader() {
        return (VideoTagHeader)super.getTagHeader();
    }

    public static class VideoTagConstant {

        public static final int FRAME_TYPE_MIN_VALUE = 1;
        public static final int KEY_FRAME_AVC_SEEKABLE = 1;
        public static final int FRAME_NO_USE = -1;
        public static final int INTER_FRAME_AVC_NON_SEEKABLE = 2;
        public static final int DISPOSABLE_INTER_FRAME_H263_ONLY = 3;
        public static final int GENERATED_KEY_FRAME_RESERVED_FOR_SERVER = 4;
        public static final int VIDEO_INFO_COMMAND_FRAME = 5;
        public static final int FRAME_TYPE_MAX_VALUE = 5;


        public static final int CODECID_MIN_VALUE = 2;
        public static final int SORENSON_H263 = 2;
        public static final int SCREEN_VIDEO = 3;
        public static final int ON2_VP6 = 4;
        public static final int ON2_VP6_WITH_ALPHA_CHANNEL = 5;
        public static final int SCREEN_VIDOE_VERSION_2 = 6;
        public static final int AVC = 7;
        public static final int CODECID_MAX_VALUE = 7;
    }
}
