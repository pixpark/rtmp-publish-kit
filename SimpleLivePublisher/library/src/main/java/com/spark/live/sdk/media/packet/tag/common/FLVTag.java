package com.spark.live.sdk.media.packet.tag.common;


import java.nio.ByteBuffer;

/**
 * Flv Tag 实体
 * Created by devzhaoyou on 8/9/16.
 */
public abstract class FLVTag implements ISuperTagOperation{

    public static final int FLV_TAG_RESERVED_MASK = 0xC0000000;
    public static final int FLV_TAG_FILTER_MASK = 0x20000000;
    public static final int FLV_TAG_TAG_TYPE_MASK = 0x1F000000;
    public static final int FLV_TAG_DATA_SIZE_MASK = 0x00FFFFFF;

    public static final int FLV_TAG_TIMESTAMP_MASK = 0xFFFFFF00;
    public static final int FLV_TAG_TIMESTAMP_EXTENDED_MASK = 0x000000FF;

    public static final int FLV_TAG_STREAM_ID_MASK = 0x00FFFFFF;


    protected FLVTagHeader tagHeader;
    protected FLVTagData tagData;

    protected ByteBuffer binaryTag;


    public void setTagHeader(FLVTagHeader tagHeader) {
        this.tagHeader = tagHeader;
    }

    public void setTagData(FLVTagData tagData) {
        this.tagData = tagData;
    }

    public FLVTagHeader getTagHeader() {
        return tagHeader;
    }

    public FLVTagData getTagData() {
        return tagData;
    }

    public ByteBuffer getBinaryTag() {
        return binaryTag;
    }


    @Override
    public String toString() {
        return "FLVTag{" +
                "tagHeader=" + tagHeader.toString() +
                ", tagData=" + tagData.toString() +
                '}';
    }

    public static class FLVTagConfigConstant {

        public static final int FLV_TAG_FILTER_MIN_VALUE = 0;
        public static final int FLV_TAG_FILTER_NO_PRE_PROCESSING = 0;
        public static final int FLV_TAG_FILTER_PRE_PROCESSING = 1;
        public static final int FLV_TAG_FILTER_MAX_VALUE = 1;

        public static final int TAG_TYPE_MIN_VALUE = 8;
        public static final int FLV_TAG_TAG_TYPE_AUDIO = 8;
        public static final int FLV_TAG_TAG_TYPE_VIDEO = 9;
        public static final int FLV_TAG_TAG_TYPE_SCRIPT = 18;
        public static final int TAG_TYPE_MAX_VALUE = 18;

        public static final int FLV_TAG_STREAM_ID_ZERO = 0;

    }
}
