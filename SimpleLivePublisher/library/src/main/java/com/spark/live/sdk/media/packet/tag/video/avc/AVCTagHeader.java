package com.spark.live.sdk.media.packet.tag.video.avc;

import com.spark.live.sdk.media.packet.tag.video.VideoTag;
import com.spark.live.sdk.media.packet.tag.video.VideoTagHeader;
import com.spark.live.sdk.util.BinaryUtil;
import com.spark.live.sdk.util.LogUtil;

import java.nio.ByteBuffer;

/**
 *
 * Created by devzhaoyou on 8/12/16.
 */
public class AVCTagHeader extends VideoTagHeader {

    private int avcPacketType;

    private int compositionTime;


    private ByteBuffer avcBinaryHeader;


    public AVCTagHeader() {
        setCodecID4Bits(VideoTag.VideoTagConstant.AVC);
    }


    public AVCTagHeader setAvcPacketType(int avcPacketType) {
        if (avcPacketType < AVCVideoTag.AVCConstant.AVC_PACKET_TYPE_MIN_VALUE ||
                avcPacketType > AVCVideoTag.AVCConstant.AVC_PACKET_TYPE_MAX_VALUE) {

            LogUtil.e("The avcPacketType must be between AVC_PACKET_TYPE_MIN_VALUE" +
                    "and AVC_PACKET_TYPE_MAX_VALUE");
            return null;
        }

        this.avcPacketType = avcPacketType;
        return this;
    }

    public void setCompositionTime(int compositionTime) {
        this.compositionTime = compositionTime;
    }

    public int getAvcPacketType() {
        return avcPacketType;
    }

    public ByteBuffer getAvcBinaryHeader() {
        return avcBinaryHeader;
    }

    @Override
    public ByteBuffer toBinaryData() {
        super.toBinaryData();
        int resultInt = 0;
        resultInt |= (AVCVideoTag.AVC_PACKET_TYPE_MASK & avcPacketType << 24);
        resultInt |= (AVCVideoTag.COMPOSITION_TYPE_MASK & compositionTime);
        byte[] resultBytes = BinaryUtil.getBytesFromIntValue(resultInt, 4);
        byte[] superBytes = new byte[videoBinaryHeader.remaining()];
        byte[] resultArray = new byte[resultBytes.length + superBytes.length];
        videoBinaryHeader.get(superBytes);

        avcBinaryHeader = ByteBuffer.wrap(resultArray);
        avcBinaryHeader.put(superBytes).put(resultBytes).flip();
        avcBinaryHeader = avcBinaryHeader.asReadOnlyBuffer();
        return avcBinaryHeader;
    }

    @Override
    public String toString() {
        return "AVCTagHeader{" +
                "avcPacketType=" + avcPacketType +
                ", compositionTime=" + compositionTime +
                ", avcBinaryHeader=" + avcBinaryHeader +
                '}';
    }
}
