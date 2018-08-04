package com.spark.live.sdk.cacher;

import android.os.Handler;

import com.spark.live.sdk.media.packet.tag.common.FLVTag;
import com.spark.live.sdk.media.packet.tag.common.FLVTagHeader;
import com.spark.live.sdk.media.packet.tag.video.VideoTag;
import com.spark.live.sdk.media.packet.tag.video.avc.AVCTagData;
import com.spark.live.sdk.media.packet.tag.video.avc.AVCTagHeader;
import com.spark.live.sdk.media.packet.tag.video.avc.AVCVideoTag;

/**
 *
 * Created by devzhaoyou on 9/8/16.
 */

public class CacheFrame {
    public static final int VIDEO_SEQUENCE = 100;
    public static final int AUDIO_SEQUENCE = 200;
    public static final int VIDEO_FRAME = 300;
    public static final int AUDIO_FRAME = 400;

    private int pts;
    private FLVTag frame;
    private int frameType;

    public CacheFrame(int pts, FLVTag frame) {
        this.pts = pts;
        this.frame = frame;
    }

    public int getPts() {
        return pts;
    }

    public FLVTag getFrame() {
        return frame;
    }

    public void setPts(int pts) {
        this.pts = pts;
    }

    public void setFrame(FLVTag frame) {
        this.frame = frame;
    }

    public int getFrameType() {
        return frameType;
    }

    public void setFrameType(int frameType) {
        this.frameType = frameType;
    }

    public boolean isVideoKeyFrame() {
        if (frameType == VIDEO_FRAME) {
            FLVTagHeader header = frame.getTagHeader();
            if (header instanceof AVCTagHeader) {
                AVCTagHeader avcTagHeader = (AVCTagHeader) header;
                int type = avcTagHeader.getFrameType4Bits();
                if (type == AVCVideoTag.VideoTagConstant.KEY_FRAME_AVC_SEEKABLE) {
                    return true;
                }
            }
        }
        return false;
    }
}
