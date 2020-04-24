package com.spark.live.sdk.media.packet.tag.video.avc;

import com.spark.live.sdk.util.BinaryUtil;
import com.spark.live.sdk.util.LogUtil;

/**
 *
 * Created by devzhaoyou on 8/11/16.
 */
public class NalUnit {

    private static final int NALU_HEADER_MASK = 0x1F;
    public static final int NALU_DIVIDER = 0x00000001;
    public static final int NALU_DIVIDER_LITTLE_END = 0x01000000;

    private int type = -1;
    private byte[] rawData;
    private int size;

    public NalUnit() {
        this(null, 0);
    }

    public NalUnit(byte[] rawData, int size) {
        this.rawData = rawData;
        this.size = size;
    }

    public void setNalRawData(byte[] rawData) {
        this.rawData = rawData;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getType() {
        if (rawData != null) {
            if (type < 0) {
                byte header = rawData[0];
                type = header & NALU_HEADER_MASK;
            }
            return type;
        } else {
//            LogUtil.e("NalUnit: Can not get the NALU type, the nalData is null!");
            return -1;
        }

    }

    public boolean isPPSNalUnit() {
        return getType() == NALUConstant.NALU_TYPE_PPS;
    }

    public boolean isSPSNalUnit() {
        return getType() == NALUConstant.NALU_TYPE_SPS;
    }

    public boolean isAUDNalUnit() {
        return getType() == NALUConstant.NALU_TYPE_AUD;
    }

    public boolean isIDRNalUnit() {
        return getType() == NALUConstant.NALU_TYPE_IDR;
    }

    public byte[] getRawDataArray() {
        return rawData;
    }

    /**
     * 获取普通AVC数据
     * Data = H264 NALU Size（4字节） + NALU Raw Data
     * @return 数据字节数组
     */
    public byte[] getNalData() {
        if (rawData != null) {
            byte[] data = new byte[size + 4];
            byte[] nalSize = BinaryUtil.getBytesFromIntValue(size, 4);
            System.arraycopy(nalSize, 0, data, 0, nalSize.length);
            System.arraycopy(rawData, 0, data, 4, size);
            return data;
        } else {
            LogUtil.e("NalUnit: Can not get the NALU rawData, the nalData is null!");
            return null;
        }
    }

    public int getRawSize() {
        return size;
    }

    public int getNalDataSize() {
        return getRawSize() + 4;
    }


    @Override
    public String toString() {
        byte[] data = getNalData();
        return "NalUnit{" +
                "type=" + getType() +
                ", nalData=" + (data == null ? "" : BinaryUtil.printByteToHex(data)) +
                ", size=" + getRawSize() +
                '}';
    }

    public static class NALUConstant {

        public static final int NALU_TYPE_MIN_VALUE = 1;
        public static final int NALU_TYPE_SLICE = 1;
        public static final int NALU_TYPE_DPA = 2;
        public static final int NALU_TYPE_DPB = 3;
        public static final int NALU_TYPE_DPC = 4;
        public static final int NALU_TYPE_IDR = 5;
        public static final int NALU_TYPE_SEI = 6;
        public static final int NALU_TYPE_SPS = 7;
        public static final int NALU_TYPE_PPS = 8;
        public static final int NALU_TYPE_AUD = 9;
        public static final int NALU_TYPE_EOSEQ = 10;
        public static final int NALU_TYPE_EOSTREAM = 11;
        public static final int NALU_TYPE_FILL = 12;
        public static final int NALU_TYPE_MAX_VALUE = 12;
    }
}
