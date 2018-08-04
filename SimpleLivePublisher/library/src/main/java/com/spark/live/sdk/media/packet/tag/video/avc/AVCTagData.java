package com.spark.live.sdk.media.packet.tag.video.avc;

import com.spark.live.sdk.media.packet.tag.video.VideoTag;
import com.spark.live.sdk.media.packet.tag.video.VideoTagData;
import com.spark.live.sdk.util.BinaryUtil;
import com.spark.live.sdk.util.LogUtil;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * Created by devzhaoyou on 8/12/16.
 */
public class AVCTagData extends VideoTagData{

    private int avcPacketType;

    private NalUnit nalu_sps;
    private NalUnit nalu_pps;

    private int dataSize;

    private List<NalUnit> nalUnits = null;

    public AVCTagData(int avcPacketType) {
        setAvcPacketType(avcPacketType);
        nalUnits = new ArrayList<>();
    }


    public AVCTagData setAvcPacketType(int avcPacketType) {
        if (avcPacketType < AVCVideoTag.AVCConstant.AVC_PACKET_TYPE_MIN_VALUE ||
                avcPacketType > AVCVideoTag.AVCConstant.AVC_PACKET_TYPE_MAX_VALUE) {
            LogUtil.e("The avcPacketType must be between AVC_PACKET_TYPE_MIN_VALUE and " +
                    "AVC_PACKET_TYPE_MAX_VALUE");
            return null;
        }
        this.avcPacketType = avcPacketType;
        return this;
    }

    public boolean setNalu_sps(NalUnit nalu_sps) {
        if (this.nalu_sps == null) {
            this.nalu_sps = nalu_sps;
            return true;
        } else {
            if (Arrays.equals(nalu_sps.getRawDataArray(), this.nalu_sps.getRawDataArray())) {
                return false;
            } else {
                this.nalu_sps = nalu_sps;
                return true;
            }
        }

    }

    public void clearData() {
        dataSize = 0;
        nalUnits.clear();
    }

    public int getDataSize() {
        return dataSize;
    }

    public boolean setNalu_pps(NalUnit nalu_pps) {
        if (this.nalu_pps == null) {
            this.nalu_pps = nalu_pps;
            return true;
        } else {
            if (Arrays.equals(nalu_pps.getRawDataArray(), this.nalu_pps.getRawDataArray())) {
                return false;
            } else {
                this.nalu_pps = nalu_pps;
                return true;
            }
        }
    }

    public int getAvcPacketType() {
        return avcPacketType;
    }

    public void addNalUnits(NalUnit nalUnit) {
        nalUnits.add(nalUnit);
    }

    public void setDataSize(int dataSize) {
        this.dataSize = dataSize;
    }

    public int getFrameType() {
        final int type = nalUnits.get(0).getType();
        int frameType = VideoTag.VideoTagConstant.INTER_FRAME_AVC_NON_SEEKABLE;
        switch (type) {
            case NalUnit.NALUConstant.NALU_TYPE_IDR:
                frameType = VideoTag.VideoTagConstant.KEY_FRAME_AVC_SEEKABLE;
                break;
            case NalUnit.NALUConstant.NALU_TYPE_SLICE:
                frameType = VideoTag.VideoTagConstant.INTER_FRAME_AVC_NON_SEEKABLE;
                break;
            case NalUnit.NALUConstant.NALU_TYPE_AUD:
                frameType = VideoTag.VideoTagConstant.FRAME_NO_USE;
                break;
        }
        return frameType;
    }

    @Override
    public ByteBuffer toBinaryData() {

        byte[] result = avcPacketType == 0 ?
                getAVCDecoderConfigurationRecord() : getRealFrameData();

        return ByteBuffer.wrap(result);
    }


    private byte[] getRealFrameData() {
        if (nalUnits != null) {
            ByteBuffer buffer = ByteBuffer.wrap(new byte[dataSize]);
            for (NalUnit nalUnit : nalUnits) {
                buffer.put(nalUnit.getNalData());
            }
            buffer.flip();
            return buffer.array();
        }
        return new byte[0];
    }
    /**
     * 获取AVC同步数据
     * @return 同步数据数组
     */
    private byte[] getAVCDecoderConfigurationRecord() {

        final int totalLen = nalu_pps.getRawSize() + nalu_sps.getRawSize() + 11;
        ByteBuffer buffer = ByteBuffer.allocate(totalLen);
        byte[] spsData = nalu_sps.getRawDataArray();
        byte[] ppsData = nalu_pps.getRawDataArray();
        /**
         * buffer[0]ConfigurationVersion length: 8bits value: 0x01
         * buffer[1]AVCProfileIndication length: 8bits value: nalu_sps[1]
         * buffer[2]Profile_Compatibility length: 8bits value: nalu_sps[2]
         * buffer[3]AVCLevelIndication length: 8bits value: nalu_sps[3]
         * buffer[4]Reserved length: 6bits value:111111
         * buffer[4]LengthSizeMinusOne length: 2bits value: lengthSizeMinusOne, or NAL_unit_length,
         * always use 4bytes size,so we always set it to 0x03.
         */
        buffer.put((byte) 0X01);
        buffer.put(spsData[1]);
        buffer.put(spsData[2]);
        buffer.put(spsData[3]);
        buffer.put((byte) 0x03);

        /**
         * buffer[5]NumOfSequenceParameterSets length: 8bits value: 0x01
         * buffer[6]SequenceParameterSetLength length: 16bits
         * buffer[7]SequenceParameterSetLength length: 16bits
         */
        buffer.put((byte) 0x01);
        buffer.put(BinaryUtil.getBytesFromIntValue(nalu_sps.getRawSize(), 2));
        buffer.put(spsData);

        buffer.put((byte) 0x01);
        buffer.put(BinaryUtil.getBytesFromIntValue(nalu_pps.getRawSize(), 2));
        buffer.put(ppsData);
        buffer.flip();

        return buffer.array();
    }

    @Override
    public String toString() {
        return "AVCTagData{" +
                "avcPacketType=" + avcPacketType +
                ", nalu_sps=" + (nalu_sps == null ? "" : nalu_sps.toString()) +
                ", nalu_pps=" + (nalu_pps == null ? "" : nalu_pps.toString()) +
                '}';
    }
}
