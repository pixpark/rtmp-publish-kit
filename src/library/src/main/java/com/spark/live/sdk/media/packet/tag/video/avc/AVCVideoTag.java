package com.spark.live.sdk.media.packet.tag.video.avc;

import com.spark.live.sdk.media.packet.tag.video.VideoTag;

import java.nio.ByteBuffer;


/**
 *
 * Created by devzhaoyou on 8/10/16.
 */
public class AVCVideoTag extends VideoTag {

    public static final int CONFIGURATION_VERSION_MASK = 0xFF000000;
    public static final int AVC_PROFILE_INDICATION_MASK = 0x00FF0000;
    public static final int PROFILE_COMPATIBILITY_MASK = 0x0000FF00;
    public static final int AVC_PACKET_TYPE_MASK = 0xFF000000;
    public static final int COMPOSITION_TYPE_MASK = 0x00FFFFFF;

    public static final String KEY_AVC_PACKET_TYPE_INTEGER_VALUE = "KEY_AVC_PACKET_TYPE";
    public static final String KEY_COMPOSITION_TIME_INTEGER_VALUE = "KEY_COMPOSITION_TIME";

    @Override
    public AVCTagData getTagData() {
        return (AVCTagData)super.getTagData();
    }

    @Override
    public AVCTagHeader getTagHeader() {
        return (AVCTagHeader)super.getTagHeader();
    }

    @Override
    public ByteBuffer toBinaryData() {
        AVCTagHeader avcTagHeader = (AVCTagHeader) tagHeader;
        AVCTagData avcTagData = (AVCTagData) tagData;

        ByteBuffer headerBuffer = avcTagHeader.toBinaryData();
        ByteBuffer dataBuffer = avcTagData.toBinaryData();
        byte[] headerArray = new byte[headerBuffer.remaining()];
        byte[] dataArray = new byte[dataBuffer.remaining()];
        headerBuffer.get(headerArray);
        dataBuffer.get(dataArray);
        /**如果需要完整的FLV TAG 还需要将其他头打包进去*/
        binaryTag = ByteBuffer.allocate(headerArray.length + dataArray.length).put(headerArray).put(dataArray);
        binaryTag.flip();
        binaryTag = binaryTag.asReadOnlyBuffer();
        return binaryTag;
    }

    public static class AVCConstant {

        public static final int AVC_PACKET_TYPE_MIN_VALUE = 0;
        public static final int AVC_SEQUENCE_HEADER = 0;
        public static final int AVC_NALU = 1;
        public static final int AVC_END_OF_SEQUENCE = 2;
        public static final int AVC_PACKET_TYPE_MAX_VALUE = 2;


    }
}
