package com.spark.live.sdk.media.packet.tag.audio;

import com.spark.live.sdk.media.packet.tag.common.FLVTag;

import java.nio.ByteBuffer;


/**
 *
 * Created by devzhaoyou on 8/9/16.
 */
public class AudioTag extends FLVTag {

    public static final int SOUND_FORMAT_MASK = 0xF0;
    public static final int SOUND_RATE_MASK = 0x0C;
    public static final int SOUND_SIZE_MASK = 0x02;
    public static final int SOUND_TYPE_MASK = 0x01;

    protected AudioTagHeader audioTagHeader;
    protected AudioTagData audioTagData;

    public AudioTag() {
        if (tagHeader instanceof AudioTagHeader) {
            audioTagHeader = (AudioTagHeader) tagHeader;
        }

        if (tagData instanceof AudioTagData) {
            audioTagData = (AudioTagData) tagData;
        }

    }

    @Override
    public ByteBuffer toBinaryData() {
        return null;
    }

    @Override
    public String toString() {
        return "AudioTag{" +
                "audioTagHeader=" + (audioTagHeader == null ? "" : audioTagHeader.toString()) +
                ", audioTagData=" + (audioTagData == null ? "" : audioTagData.toString()) +
                '}';
    }

    public static class HeaderConstant {

        public static final int SOUND_FORMAT_MINI_VALUE = 0;
        public static final int SOUND_FORMAT_LINEAR_PCM_PLATFORM_ENDIAN = 0;
        public static final int SOUND_FORMAT_ADPCM = 1;
        public static final int SOUND_FORMAT_MP3 = 2;
        public static final int SOUND_FORMAT_LINEAR_PCM_LITTLE_ENDIAN = 3;
        public static final int SOUND_FORMAT_NELLYMOSER_16_KHZ_MONO = 4;
        public static final int SOUND_FORMAT_NELLYMOSER_8_KHZ_MONO = 5;
        public static final int SOUND_FORMAT_NELLYMOSER = 6;
        public static final int SOUND_FORMAT_G711_A_LAW_LOGARITHMIC_PCM = 7;
        public static final int SOUND_FORMAT_G711_MU_LAW_LOGARITHMIC_PCM = 8;
        public static final int SOUND_FORMAT_RESERVED = 9;
        public static final int SOUND_FORMAT_AAC = 10;
        public static final int SOUND_FORMAT_SPEEX = 11;
        public static final int SOUND_FORMAT_MP3_8_KHZ = 14;
        public static final int SOUND_FORMAT_DEVICE_SPECIFIC_SOUND = 15;
        public static final int SOUND_FORMAT_MAX_VALUE = 15;

        public static final int SOUND_RATE_MINI_VALUE = 0;
        public static final int SOUND_RATE_5_POINT_5_KHZ = 0;
        public static final int SOUND_RATE_11_KHZ = 1;
        public static final int SOUND_RATE_22_KHZ = 2;
        public static final int SOUND_RATE_44_KHZ = 3;
        public static final int SOUND_RATE_MAX_VALUE = 3;

        public static final int SOUND_SIZE_MINI_VALUE = 0;
        public static final int SOUND_SIZE_8_BITS_SAMPLES = 0;
        public static final int SOUND_SIZE_16_BITS_SAMPLES = 1;
        public static final int SOUND_SIZE_MAX_VALUE = 1;

        public static final int SOUND_TYPE_MINI_VALUE = 0;
        public static final int SOUND_TYPE_MONO_SOUND = 0;
        public static final int SOUND_TYPE_STEREO_SOUND = 1;
        public static final int SOUND_TYPE_MAX_VALUE = 1;

    }

}
