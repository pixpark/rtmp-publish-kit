package com.spark.live.sdk.media.packet.tag.audio.aac;

import com.spark.live.sdk.media.packet.tag.audio.AudioTag;

import java.nio.ByteBuffer;


/**
 *
 * Created by devzhaoyou on 8/9/16.
 */
public class AACAudioTag extends AudioTag {

    public static final int AUDIO_OBJECT_TYPE_MASK = 0x0000F800;
    public static final int SAMPLE_RATE_INDEX_MASK = 0X00000780;
    public static final int CHANNEL_CONFIG_MASK = 0x00000078;
    public static final int FRAME_LENGTH_FLAG_MASK = 0x00000004;
    public static final int DEPEND_ON_CORE_CODER_MASK = 0x00000002;
    public static final int EXTENSION_FLAG_MASK = 0x00000001;
    public static final int AAC_PACKET_TYPE_MASK = 0x00FF0000;




    @Override
    public ByteBuffer toBinaryData() {

        AACTagHeader aacTagHeader = (AACTagHeader) tagHeader;
        AACTagData aacTagData = (AACTagData) tagData;

        ByteBuffer headerBuffer = aacTagHeader.toBinaryData();
        ByteBuffer dataBuffer = aacTagData.toBinaryData();
        byte[] headerArray = new byte[headerBuffer.remaining()];
        byte[] dataArray = new byte[dataBuffer.remaining()];
        headerBuffer.get(headerArray);
        dataBuffer.get(dataArray);

        binaryTag = ByteBuffer.allocate(headerArray.length + dataArray.length);
        binaryTag.put(headerArray);
        binaryTag.put(dataArray);
        binaryTag.flip();
        binaryTag = binaryTag.asReadOnlyBuffer();
        return binaryTag;
    }

    public static class AACConstant {

        public static final int AUDIO_OBJECT_TYPE_MIN_VALUE = 0x00;
        public static final int AUDIO_OBJECT_TYPE_NULL = 0x00;
        public static final int AUDIO_OBJECT_TYPE_AAC_MAIN = 0x01;
        public static final int AUDIO_OBJECT_TYPE_AAC_LC = 0x02;
        public static final int AUDIO_OBJECT_TYPE_AAC_SSR = 0x03;
        public static final int AUDIO_OBJECT_TYPE_AAC_LPT = 0X04;
        public static final int AUDIO_OBJECT_TYPE_SBR = 0x05;
        public static final int AUDIO_OBJECT_TYPE_AAC_SCALABLE = 0x06;
        public static final int AUDIO_OBJECT_TYPE_MAX_VALUE = 0x06;

        public static final int SAMPLE_FREQUENCY_MIN_VALUE = 0x00;
        public static final int SAMPLE_FREQUENCY_96000 = 0x00;
        public static final int SAMPLE_FREQUENCY_88200 = 0x01;
        public static final int SAMPLE_FREQUENCY_64000 = 0x02;
        public static final int SAMPLE_FREQUENCY_48000 = 0x03;
        public static final int SAMPLE_FREQUENCY_44100 = 0x04;
        public static final int SAMPLE_FREQUENCY_32000 = 0x05;
        public static final int SAMPLE_FREQUENCY_24000 = 0x06;
        public static final int SAMPLE_FREQUENCY_22050 = 0x07;
        public static final int SAMPLE_FREQUENCY_16000 = 0x08;
        public static final int SAMPLE_FREQUENCY_2000 = 0x09;
        public static final int SAMPLE_FREQUENCY_11025 = 0x0a;
        public static final int SAMPLE_FREQUENCY_8000 = 0x0b;
        public static final int SAMPLE_FREQUENCY_MAX_VALUE = 0x0b;

        public static final int CHANNEL_CONFIG_MIN_VALUE = 0x01;
        public static final int CHANNEL_CONFIG_1_CHANNEL = 0x01;
        public static final int CHANNEL_CONFIG_2_CHANNEL = 0x02;
        public static final int CHANNEL_CONFIG_3_CHANNEL = 0x03;
        public static final int CHANNEL_CONFIG_4_CHANNEL = 0x03;
        public static final int CHANNEL_CONFIG_MAX_VALUE = 0x03;

        public static final int AAC_PACKET_TYPE_MIN_VALUE = 0;
        public static final int AAC_PACKET_TYPE_AAC_SEQUENCE_HEADER = 0;
        public static final int AAC_PACKET_TYPE_AAC_RAW_DATA = 1;
        public static final int AAC_PACKET_TYPE_MAX_VALUE = 1;

    }
}
