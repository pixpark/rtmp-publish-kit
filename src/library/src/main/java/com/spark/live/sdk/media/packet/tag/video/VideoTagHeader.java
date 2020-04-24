package com.spark.live.sdk.media.packet.tag.video;

import com.spark.live.sdk.media.packet.tag.common.FLVTagHeader;
import com.spark.live.sdk.util.LogUtil;

import java.nio.ByteBuffer;

/**
 * Created by devzhaoyou on 8/12/16.
 */
public class VideoTagHeader extends FLVTagHeader {

    /**
     * Type of video frame
     */
    protected int frameType4Bits;

    /**
     * Codec Identifier
     */
    protected int codecID4Bits;

    protected ByteBuffer videoBinaryHeader;

    public VideoTagHeader setFrameType4Bits(int frameType4Bits) {
        if (frameType4Bits < VideoTag.VideoTagConstant.FRAME_TYPE_MIN_VALUE ||
                frameType4Bits > VideoTag.VideoTagConstant.FRAME_TYPE_MAX_VALUE) {
            LogUtil.e("The frameType must be between FRAME_TYPE_MIN_VALUE" +
                    "and FRAME_TYPE_MAX_VALUE");
            return null;
        }

        this.frameType4Bits = frameType4Bits;
        return this;
    }

    public VideoTagHeader setCodecID4Bits(int codecID4Bits) {
        if (codecID4Bits < VideoTag.VideoTagConstant.CODECID_MIN_VALUE ||
                codecID4Bits > VideoTag.VideoTagConstant.CODECID_MAX_VALUE) {
            LogUtil.e("The codecID must be between CODECID_MIN_VALUE and" +
                    "CODECID_MAX_VALUE");
            return null;
        }

        this.codecID4Bits = codecID4Bits;
        return this;
    }


    public int getFrameType4Bits() {
        return frameType4Bits;
    }

    public int getCodecID4Bits() {
        return codecID4Bits;
    }

    public ByteBuffer getVideoBinaryHeader() {
        return videoBinaryHeader;
    }

    public ByteBuffer toBinaryData() {
        int result = 0;
        result |= (VideoTag.FRAME_TYPE_MASK & frameType4Bits << 4);
        result |= (VideoTag.CODEC_ID_MASK & codecID4Bits);
        videoBinaryHeader = ByteBuffer.wrap(new byte[]{(byte) result});
        videoBinaryHeader = videoBinaryHeader.asReadOnlyBuffer();
        return videoBinaryHeader;
    }

    @Override
    public String toString() {
        return "VideoTagHeader{" +
                "frameType4Bits=" + frameType4Bits +
                ", codecID4Bits=" + codecID4Bits +
                ", videoBinaryHeader=" + videoBinaryHeader +
                '}';
    }
}
