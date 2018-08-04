package com.spark.live.sdk.media.packet.tag.audio;

import com.spark.live.sdk.media.packet.tag.common.FLVTag;
import com.spark.live.sdk.media.packet.tag.common.FLVTagHeader;
import com.spark.live.sdk.util.BinaryUtil;
import com.spark.live.sdk.util.LogUtil;

import java.nio.ByteBuffer;

/**
 *
 * Created by devzhaoyou on 8/12/16.
 */
public class AudioTagHeader extends FLVTagHeader{

    public static final String KEY_SOUND_FORMAT = "SOUND_FORMAT";
    public static final String KEY_SOUND_RATE = "SOUND_RATE";
    public static final String KEY_SOUND_SIZE = "SOUND_SIZE";
    public static final String KEY_SOUND_TYPE = "SOUND_TYPE";

    public static final int SAMPLE_RATE_44K = 3;
    public static final int SAMPLE_RATE_22K = 2;
    public static final int SAMPLE_RATE_11K = 1;
    public static final int SAMPLE_RATE_5_point_5K = 0;

    public static final int SOUND_FORMAT_MP3 = 2;
    public static final int SOUND_FORMAT_AAC = 10;
    public static final int SOUND_FORMAT_SPEEX = 11;

    public static final int SOUND_SIZE_SND_8_BIT = 0;
    public static final int SOUND_SIZE_SND_16_BIT = 1;

    public static final int SOUND_TYPE_SND_MONO = 0;
    public static final int SOUND_TYPE_SND_STEREO = 1;

    /**
     * Size in bits UB [4]
     * Format of SoundData. The following values are defined:
     * 0 = Linear PCM, platform endian
     * 1 = ADPCM
     * 2 = MP3
     * 3 = Linear PCM, little endian
     * 4 = Nellymoser 16 kHz mono
     * 5 = Nellymoser 8 kHz mono
     * 6 = Nellymoser
     * 7 = G.711 A-law logarithmic PCM
     * 8 = G.711 mu-law logarithmic PCM
     * 9 = reserved
     * 10 = AAC
     * 11 = Speex
     * 14 = MP3 8 kHz
     * 15 = Device-specific sound
     * Formats 7, 8, 14, and 15 are reserved.
     * AAC is supported in Flash Player 9,0,115,0 and higher.
     * Speex is supported in Flash Player 10 and higher.
     */
    protected int soundFormat;

    /**
     * Size in bits UB [2]
     * Sampling rate. The following values are defined:
     * 0 = 5.5 kHz
     * 1 = 11 kHz
     * 2 = 22 kHz
     * 3 = 44 kHz
     */
    protected int soundRate;

    /**
     * Size in bits UB [1]
     * Size of each audio sample. This parameter only pertains to
     * uncompressed formats. Compressed formats always decode
     * to 16 bits internally.
     * 0 = 8-bit samples
     * 1 = 16-bit samples
     */
    protected int soundSize;

    /**
     * Size in bits UB [1]
     * Mono or stereo sound
     * 0 = Mono sound
     * 1 = Stereo sound
     */
    protected int soundType;

    private int binaryResult;
    protected ByteBuffer audioBinaryHeader;


    public AudioTagHeader() {
        this.tagType = FLVTag.FLVTagConfigConstant.FLV_TAG_TAG_TYPE_AUDIO;
    }

    public AudioTagHeader setSoundFormat(int soundFormat) {
        if (soundFormat < AudioTag.HeaderConstant.SOUND_FORMAT_MINI_VALUE ||
                soundFormat > AudioTag.HeaderConstant.SOUND_FORMAT_MAX_VALUE) {
            LogUtil.e("The soundFormat must be between SOUND_FORMAT_MINI_VALUE" +
                    "and SOUND_FORMAT_MAX_VALUE");
            return null;
        }
        this.soundFormat = soundFormat;
        return this;
    }

    public AudioTagHeader setSoundRate(int soundRate) {
        if (soundRate < AudioTag.HeaderConstant.SOUND_RATE_MINI_VALUE ||
                soundRate > AudioTag.HeaderConstant.SOUND_FORMAT_MAX_VALUE) {
            LogUtil.e("The soundRate must be between SOUND_RATE_MINI_VALUE" +
                    "and SOUND_FORMAT_MAX_VALUE");
            return null;
        }
        this.soundRate = soundRate;
        return this;
    }

    public AudioTagHeader setSoundSize(int soundSize) {

        if (soundSize < AudioTag.HeaderConstant.SOUND_SIZE_MINI_VALUE ||
                soundSize > AudioTag.HeaderConstant.SOUND_SIZE_MAX_VALUE) {
            LogUtil.e("The soundSize must be between SOUND_SIZE_MINI_VALUE" +
                    "and SOUND_SIZE_MAX_VALUE");
            return null;
        }

        this.soundSize = soundSize;
        return this;
    }

    public AudioTagHeader setSoundType(int soundType) {

        if (soundType < AudioTag.HeaderConstant.SOUND_TYPE_MINI_VALUE ||
                soundType > AudioTag.HeaderConstant.SOUND_TYPE_MAX_VALUE) {
            LogUtil.e("The soundType must be between SOUND_TYPE_MINI_VALUE" +
                    "and SOUND_TYPE_MAX_VALUE");
            return null;
        }
        this.soundType = soundType;
        return this;
    }

    @Override
    public ByteBuffer toBinaryData() {
        super.toBinaryData();

        binaryResult |= (AudioTag.SOUND_FORMAT_MASK & soundFormat << 4);
        binaryResult |= (AudioTag.SOUND_RATE_MASK & soundRate << 2);
        binaryResult |= (AudioTag.SOUND_SIZE_MASK & soundSize << 1);
        binaryResult |= (AudioTag.SOUND_TYPE_MASK & soundType);

        audioBinaryHeader = ByteBuffer.wrap(BinaryUtil.getBytesFromIntValue(binaryResult, 1)).asReadOnlyBuffer();
        return audioBinaryHeader;
    }

    @Override
    public String toString() {
        return "AudioTagHeader{" +
                "soundFormat=" + soundFormat +
                ", soundRate=" + soundRate +
                ", soundSize=" + soundSize +
                ", soundType=" + soundType +
                ", binaryResult=" + binaryResult +
                ", audioBinaryHeader=" + audioBinaryHeader +
                '}';
    }
}
