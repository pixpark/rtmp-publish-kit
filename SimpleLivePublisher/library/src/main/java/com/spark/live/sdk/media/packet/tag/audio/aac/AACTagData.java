package com.spark.live.sdk.media.packet.tag.audio.aac;

import com.spark.live.sdk.media.packet.tag.audio.AudioTagData;
import com.spark.live.sdk.util.BinaryUtil;
import com.spark.live.sdk.util.LogUtil;

import java.nio.ByteBuffer;

/**
 * AAC Tag 数据部分封装
 * Created by devzhaoyou on 8/12/16.
 */
public class AACTagData extends AudioTagData{

    public static final String KEY_AUDIO_OBJECT_TYPE = "AUDIO_OBJECT_TYPE";
    public static final String KEY_SAMPLING_FREQUENCY_INDEX = "SAMPLING_FREQUENCY_INDEX";
    public static final String KEY_CHANNEL_CONFIGURATION = "CHANNEL_CONFIGURATION";


    private int packetType;

    /**
     * Size in bits 5 bits 编码结构类型 AAC-LC为2
     */
    private int audioObjectType;

    /**
     * Size in bits 4 bits  音频采样索引值 44100 对应的值为4
     */
    private int samplingFrequencyIndex;

    /**
     * Size in bits 4 bits 音频输出声道 2
     */
    private int channelConfiguration;

    private int audioConfig;

    private byte[] rawData;
    private ByteBuffer aacBinaryData;

    public AACTagData(int packetType) {
        if (packetType < AACAudioTag.AACConstant.AAC_PACKET_TYPE_MIN_VALUE ||
                packetType > AACAudioTag.AACConstant.AAC_PACKET_TYPE_MAX_VALUE) {
            LogUtil.e("The packet type must be between AAC_PACKET_TYPE_MIN_VALUE " +
                    "and AAC_PACKET_TYPE_MAX_VALUE");
        }
        this.packetType = packetType;

    }

    public void setRawData(byte[] rawData) {
        this.rawData = rawData;
    }

    public AACTagData setPacketType(int packetType) {
        if (packetType < AACAudioTag.AACConstant.AAC_PACKET_TYPE_MIN_VALUE ||
                packetType > AACAudioTag.AACConstant.AAC_PACKET_TYPE_MAX_VALUE) {
            LogUtil.e("The packet type must be between AAC_PACKET_TYPE_MIN_VALUE " +
                    "and AAC_PACKET_TYPE_MAX_VALUE");
            return null;
        }
        this.packetType = packetType;
        return this;
    }

    public AACTagData setAudioObjectType(int audioObjectType) {
        if (audioObjectType < AACAudioTag.AACConstant.AUDIO_OBJECT_TYPE_MIN_VALUE ||
                audioObjectType > AACAudioTag.AACConstant.AUDIO_OBJECT_TYPE_MAX_VALUE) {
            LogUtil.e("The audioObjectType must be between AUDIO_OBJECT_TYPE_MIN_VALUE" +
                    "and AUDIO_OBJECT_TYPE_MAX_VALUE");
            return null;
        }

        this.audioObjectType = audioObjectType;
        return this;
    }

    public AACTagData setSamplingFrequencyIndex(int samplingFrequencyIndex) {
        if (samplingFrequencyIndex < AACAudioTag.AACConstant.SAMPLE_FREQUENCY_MIN_VALUE ||
                samplingFrequencyIndex > AACAudioTag.AACConstant.SAMPLE_FREQUENCY_MAX_VALUE) {
            LogUtil.e("The samplingFrequencyIndex must be between SAMPLE_FREQUENCY_MIN_VALUE" +
                    "and SAMPLE_FREQUENCY_MAX_VALUE");
            return null;
        }

        this.samplingFrequencyIndex = samplingFrequencyIndex;
        return this;
    }

    public AACTagData setChannelConfiguration(int channelConfiguration) {
        if (channelConfiguration < AACAudioTag.AACConstant.CHANNEL_CONFIG_MIN_VALUE ||
                channelConfiguration > AACAudioTag.AACConstant.CHANNEL_CONFIG_MAX_VALUE) {
            LogUtil.e("The channelConfiguration must be between CHANNEL_CONFIG_MIN_VALUE and " +
                    "SAMPLE_FREQUENCY_MAX_VALUE");
            return null;
        }

        this.channelConfiguration = channelConfiguration;
        return this;
    }

    @Override
    public ByteBuffer toBinaryData() {
        super.toBinaryData();
        byte[] result = packetType == 0 ? createAACSequencePacket() : createAACDataPacket();
        aacBinaryData = ByteBuffer.wrap(result).asReadOnlyBuffer();
        return aacBinaryData;
    }

    /**
     * 创建音频同步包
     * @return 音频同步包字节数组
     */
    private byte[] createAACSequencePacket() {

        //TODO...暂时无需变化所以声明为局部变量此处默认值均为0
        int frameLengthFlag = 0;
        int dependsOnCoreCoder = 0;
        int extensionFlag = 0;

        audioConfig |= (AACAudioTag.AAC_PACKET_TYPE_MASK & packetType << 16);
        audioConfig |= (AACAudioTag.AUDIO_OBJECT_TYPE_MASK & audioObjectType << 11);
        audioConfig |= (AACAudioTag.SAMPLE_RATE_INDEX_MASK & samplingFrequencyIndex << 7);
        audioConfig |= (AACAudioTag.CHANNEL_CONFIG_MASK & channelConfiguration << 3);
        audioConfig |= (AACAudioTag.FRAME_LENGTH_FLAG_MASK & frameLengthFlag << 2);
        audioConfig |= (AACAudioTag.DEPEND_ON_CORE_CODER_MASK & dependsOnCoreCoder << 1);
        audioConfig |= (AACAudioTag.EXTENSION_FLAG_MASK & extensionFlag);

        return BinaryUtil.getBytesFromIntValue(audioConfig, 3);
    }

    /**
     * 创建普通数据包
     * @return 普通包字节数组
     */
    private byte[] createAACDataPacket() {
        byte[] result = new byte[rawData.length + 1];
        result[0] = (byte) packetType;
        System.arraycopy(rawData, 0, result, 1, rawData.length);
        return result;
    }

    @Override
    public String toString() {
        return "AACTagData{" +
                "packetType=" + packetType +
                ", audioObjectType=" + audioObjectType +
                ", samplingFrequencyIndex=" + samplingFrequencyIndex +
                ", channelConfiguration=" + channelConfiguration +
                ", audioConfig=" + audioConfig +
                ", rawData=" + rawData +
                ", aacBinaryData=" + aacBinaryData +
                '}';
    }
}
