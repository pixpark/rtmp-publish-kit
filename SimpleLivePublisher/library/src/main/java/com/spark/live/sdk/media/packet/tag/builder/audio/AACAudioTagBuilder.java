package com.spark.live.sdk.media.packet.tag.builder.audio;

import com.spark.live.sdk.media.packet.tag.audio.AudioTagHeader;
import com.spark.live.sdk.media.packet.tag.audio.aac.AACAudioTag;
import com.spark.live.sdk.media.packet.tag.audio.aac.AACTagData;
import com.spark.live.sdk.media.packet.tag.audio.aac.AACTagHeader;
import com.spark.live.sdk.media.packet.tag.builder.DataValueObject;
import com.spark.live.sdk.media.packet.tag.builder.IFlvTagBuilder;
import com.spark.live.sdk.media.packet.tag.common.FLVTag;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * Created by devzhaoyou on 8/9/16.
 */
public class AACAudioTagBuilder implements IFlvTagBuilder {

    private AACTagData aacData = null;
    private AACTagHeader aacHeader = null;
    private Map<String, Object> params = null;
    private List<FLVTag> tags = new ArrayList<>();
    private boolean isSequenceOK = false;

    public AACAudioTagBuilder(Map<String, Object> params) {
        this.params = params;
    }


    @Override
    public void buildTagHeader(DataValueObject dataVo) {
        if (aacHeader == null) {
            aacHeader = new AACTagHeader();
            aacHeader.setSoundFormat((Integer) params.get(AudioTagHeader.KEY_SOUND_FORMAT));
            aacHeader.setSoundRate((Integer) params.get(AudioTagHeader.KEY_SOUND_RATE));
            aacHeader.setSoundSize((Integer) params.get(AudioTagHeader.KEY_SOUND_SIZE));
            aacHeader.setSoundType((Integer) params.get(AudioTagHeader.KEY_SOUND_TYPE));
        }
    }

    @Override
    public void buildTagData(DataValueObject dataVo) {

        if (isSequenceOK) {
            aacData = new AACTagData(AACAudioTag.AACConstant.AAC_PACKET_TYPE_AAC_RAW_DATA);
            byte[] raw = new byte[dataVo.bufferInfo.size];
            dataVo.data.get(raw);
            aacData.setRawData(raw);
        } else {
            aacData = new AACTagData(AACAudioTag.AACConstant.AAC_PACKET_TYPE_AAC_SEQUENCE_HEADER);
            aacData.setAudioObjectType(AACAudioTag.AACConstant.AUDIO_OBJECT_TYPE_AAC_LC);
            aacData.setChannelConfiguration(AACAudioTag.AACConstant.CHANNEL_CONFIG_1_CHANNEL);
            aacData.setSamplingFrequencyIndex(AACAudioTag.AACConstant.SAMPLE_FREQUENCY_44100);
            isSequenceOK = true;
        }

    }

    @Override
    public List<FLVTag> buildTag() {
        tags.clear();
        AACAudioTag aacAudioTag = new AACAudioTag();
        aacAudioTag.setTagHeader(aacHeader);
        aacAudioTag.setTagData(aacData);
        tags.add(aacAudioTag);
        aacData = null;
        return tags;
    }
}
