package com.spark.live.sdk.media.packet.tag.builder.video;

import com.spark.live.sdk.media.packet.tag.builder.DataValueObject;
import com.spark.live.sdk.media.packet.tag.builder.IFlvTagBuilder;
import com.spark.live.sdk.media.packet.tag.common.FLVTag;
import com.spark.live.sdk.media.packet.tag.video.VideoTag;
import com.spark.live.sdk.media.packet.tag.video.avc.AVCVideoTag;
import com.spark.live.sdk.media.packet.tag.video.avc.AVCTagData;
import com.spark.live.sdk.media.packet.tag.video.avc.AVCTagHeader;
import com.spark.live.sdk.media.packet.tag.video.avc.NalUnit;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * AVCTag的建造者负责AVCTag对象的具体创建
 * Created by devzhaoyou on 8/11/16.
 */
public class AVCVideoTagBuilder implements IFlvTagBuilder {

    private AVCTagHeader header = null;
    private AVCTagData sequenceData = null;
    private AVCTagData tagData;
    private List<FLVTag> tags = null;
    private Map<String, Object> params = null;

    private boolean isSequenceOK = false;
    private boolean isSPSChanged = false;
    private boolean isPPSChanged = false;

    public AVCVideoTagBuilder(Map<String, Object> params) {
        this.params = params;
        sequenceData = new AVCTagData(AVCVideoTag.AVCConstant.AVC_SEQUENCE_HEADER);
        tags = new ArrayList<>();
    }

    @Override
    public void buildTagHeader(DataValueObject dataVo) {
        header = new AVCTagHeader();
        header.setCodecID4Bits(VideoTag.VideoTagConstant.AVC);
        header.setTagType(FLVTag.FLVTagConfigConstant.FLV_TAG_TAG_TYPE_VIDEO);
        header.setFrameType4Bits(VideoTag.VideoTagConstant.KEY_FRAME_AVC_SEEKABLE);
        header.setCompositionTime(0);
    }

    @Override
    public void buildTagData(DataValueObject dataVo) {
        ArrayList<NalUnit> nalUnits = findNALUData(dataVo);
        if (nalUnits != null) {
            int dataSize = 0;

            tagData = new AVCTagData(AVCVideoTag.AVCConstant.AVC_NALU);
            tagData.clearData();
            for (NalUnit nalUnit : nalUnits) {
                if (nalUnit.isAUDNalUnit()) {
                    continue;
                }

                if (nalUnit.isSPSNalUnit()) {
                    isSPSChanged = sequenceData.setNalu_sps(nalUnit);
                    continue;
                }

                if (nalUnit.isPPSNalUnit()) {
                    isPPSChanged = sequenceData.setNalu_pps(nalUnit);
                    continue;
                }

                dataSize += nalUnit.getNalDataSize();
                tagData.addNalUnits(nalUnit);

            }
            if (dataSize <= 0) {
                tagData = null;
            } else {
                tagData.setDataSize(dataSize);
            }
        }

    }

    @Override
    public List<FLVTag> buildTag() {
        tags.clear();
        buildSequenceTag(tags);
        buildDataTag(tags);
        return tags;
    }

    private void buildSequenceTag(List<FLVTag> tags) {
        if (isSequenceOK && !isPPSChanged && !isSPSChanged) {
            return;
        }
        AVCVideoTag tag = new AVCVideoTag();
        header.setFrameType4Bits(VideoTag.VideoTagConstant.KEY_FRAME_AVC_SEEKABLE);
        header.setAvcPacketType(AVCVideoTag.AVCConstant.AVC_SEQUENCE_HEADER);
        tag.setTagHeader(header);
        tag.setTagData(sequenceData);
        isSequenceOK = true;
        isSPSChanged = false;
        isPPSChanged = false;
        tags.add(tag);
    }

    private void buildDataTag(List<FLVTag> tags) {
        if (!isSequenceOK || tagData == null) {
            return;
        }
        AVCVideoTag tag = new AVCVideoTag();
        tag.setTagData(tagData);
        header.setFrameType4Bits(tagData.getFrameType());
        header.setAvcPacketType(tagData.getAvcPacketType());
        tag.setTagHeader(header);
        tagData = null;
        tags.add(tag);
    }


    /**
     * 找出Buffer中的所有NALU （一个编码后生成的buffer中可能同时包含多个NALU）
     * buffer中NALU是用0x00000001来分隔的此处NALU_DIVIDER_LITTLE_END的值为0x01000000
     *
     * @param dataVo 包含buffer和info的数据对象
     * @return 找到的NALU 列表
     */
    private ArrayList<NalUnit> findNALUData(DataValueObject dataVo) {
        ArrayList<NalUnit> nalList = new ArrayList<>();
        /*配置buffer*/
        ByteBuffer data = dataVo.data;
        final int bufferSize = dataVo.bufferInfo.size;
        data.limit(bufferSize);
        data.position(data.position() + dataVo.bufferInfo.offset);

        /*初始化数据*/
        int startPos = -1;
        NalUnit nalUnit = null;
        final int limitPos = bufferSize - 4;
        ByteBuffer child = null;

        /*搜索nal单元*/
        while (data.position() < limitPos) {
            final int divider = data.getInt();
            final int curPos = data.position();

            if (divider == NalUnit.NALU_DIVIDER_LITTLE_END) {
                if (startPos != -1 && !nalUnit.isAUDNalUnit() && child != null) {
                    final int size = curPos - startPos - 4;
                    if (size > 0) {
                        /*保存对象*/
                        byte[] dataNal = new byte[size];
                        child.get(dataNal);
                        nalUnit.setNalRawData(dataNal);
                        nalUnit.setSize(size);
                        nalList.add(nalUnit);
                    }
                }
                /*记录当前位置并创建新对象*/
                startPos = curPos;
                child = data.slice();
                nalUnit = new NalUnit();
            } else {
                /*保持步长为1 因为每次获取int前进四个字节所以需要回退3*/
                data.position(curPos - 3);
            }

        }
        /*处理结尾余下的数据*/
        if (startPos > 0 && startPos < bufferSize && child != null) {
            final int size = bufferSize - startPos;
            if (size > 0) {
                byte[] dataNal = new byte[size];
                child.get(dataNal);
                nalUnit.setNalRawData(dataNal);
                nalUnit.setSize(size);
                nalList.add(nalUnit);
            }

        }
        return nalList;
    }


}
