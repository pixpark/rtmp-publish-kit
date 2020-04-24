package com.spark.live.sdk.media.codec;

import android.graphics.Point;
import android.media.Image;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.media.MediaFormat;
import android.os.Build;

/**
 *
 * Created by devzhaoyou on 9/14/16.
 */

public class VideoConfiguration {

    private static Object lock = new Object();

    private static VideoConfiguration mInstance = null;

    public static VideoConfiguration getInstance() {
        synchronized (lock) {
            if (mInstance == null) {
                mInstance = new VideoConfiguration();
            }
            return mInstance;
        }
    }
    private MediaFormat mediaFormat;

    private int rotateDegree;

    private VideoConfiguration() {
        mediaFormat = new MediaFormat();
        mediaFormat.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, 0);
    }

    public void findColorFormat(MediaCodecInfo mediaCodecInfo) {
        String mime = mediaFormat.getString(MediaFormat.KEY_MIME);
        MediaCodecInfo.CodecCapabilities capabilities = mediaCodecInfo
                .getCapabilitiesForType(mime);
        int colorFormats[] = capabilities.colorFormats;
        int matchedColorFormat = 0;
        for (int colorFormat : colorFormats) {
            /**优先选择 COLOR_FormatYUV420Planar=19 COLOR_FormatYUV420PackedPlanar=20
             * COLOR_FormatYUV420SemiPlanar=21 三者中的较大者   PS: 具体原因我也不清楚
             * choose YUV for h.264, prefer the bigger one.*/
            if ((colorFormat >= MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Planar &&
                    colorFormat <= MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420SemiPlanar)) {

                if (colorFormat > matchedColorFormat) {
                    matchedColorFormat = colorFormat;
                }
            }
        }
        mediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, matchedColorFormat);
    }

    public MediaFormat getMediaFormat() {
        return mediaFormat;
    }

    private boolean chenckSdkVersion(MediaCodecInfo.CodecCapabilities capabilities) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            /**
             * Flexible 12 bits per pixel, subsampled YUV color format with 8-bit chroma and luma
             * components.
             * <p>
             * Chroma planes are subsampled by 2 both horizontally and vertically.
             * Use this format with {@link Image}.
             * This format corresponds to {@link android.graphics.ImageFormat#YUV_420_888},
             * and can represent the {@link #COLOR_FormatYUV411Planar},
             * {@link #COLOR_FormatYUV411PackedPlanar}, {@link #COLOR_FormatYUV420Planar},
             * {@link #COLOR_FormatYUV420PackedPlanar}, {@link #COLOR_FormatYUV420SemiPlanar}
             * and {@link #COLOR_FormatYUV420PackedSemiPlanar} formats.
             *
             * @see Image#getFormat
             */
            mediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Flexible);
            return capabilities.isFormatSupported(mediaFormat);
        } else {
            return capabilities == null;
        }


    }

    public int getFps() {
        return mediaFormat.getInteger(MediaFormat.KEY_FRAME_RATE);
    }

    public int getGop() {
        return mediaFormat.getInteger(MediaFormat.KEY_I_FRAME_INTERVAL);
    }

    public int getVideoWidth() {
        return mediaFormat.getInteger(MediaFormat.KEY_WIDTH);
    }

    public int getVideoHeight() {
        return mediaFormat.getInteger(MediaFormat.KEY_HEIGHT);
    }

    public int getVideoColorFormat() {
        return mediaFormat.getInteger(MediaFormat.KEY_COLOR_FORMAT);
    }

    public Point getFrameSize() {
        int x = mediaFormat.getInteger(MediaFormat.KEY_WIDTH);
        int y = mediaFormat.getInteger(MediaFormat.KEY_HEIGHT);
        return new Point(x, y);
    }

    public int getRotateDegree() {
        return rotateDegree;
    }

    public int getBitrate() {
        return mediaFormat.getInteger(MediaFormat.KEY_BIT_RATE);
    }

    public String getMimeType() {
        return mediaFormat.getString(MediaFormat.KEY_MIME);
    }


    public VideoConfiguration setFps(int fps) {
        mediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, fps);
        return this;
    }

    public VideoConfiguration setGop(int gop) {
        mediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, gop);
        return this;
    }

    public VideoConfiguration setVideoColorFormat(int videoColorFormat) {
        mediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, videoColorFormat);
        return this;
    }

    public VideoConfiguration setFrameSize(Point frameSize, int rotateDegree) {
        int realW, realH;
        this.rotateDegree = rotateDegree;
        if (rotateDegree == 90 || rotateDegree == 270) {
            realW = frameSize.y;
            realH = frameSize.x;
        } else {
            realW = frameSize.x;
            realH = frameSize.y;
        }
        mediaFormat.setInteger(MediaFormat.KEY_WIDTH, realW);
        mediaFormat.setInteger(MediaFormat.KEY_HEIGHT, realH);
        return this;
    }

    public VideoConfiguration setRotateDegree(int rotateDegree) {
        this.rotateDegree = rotateDegree;
        return this;
    }

    public VideoConfiguration setBitrate(int mBitrate) {
        mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, mBitrate * 1000);
        return this;
    }

    public VideoConfiguration setMimeType(String mimeType) {
        mediaFormat.setString(MediaFormat.KEY_MIME, mimeType);
        return this;
    }

    /**VP8 video (i.e. video in .webm)*/
    public static final String VIDEO_X_VND_ON2_VP8 = "video/x-vnd.on2.vp8";

    /**VP9 video (i.e. video in .webm)*/
    public static final String VIDEO_X_VND_ON2_VP9 = "video/x-vnd.on2.vp9";

    /**H.264/AVC video*/
    public static final String VIDEO_AVC = "video/avc";

    /**H.265/HEVC video*/
    public static final String VIDEO_HEVC = "video/hevc";

    /**MPEG4 video*/
    public static final String VIDEO_MP4V_ES = "video/mp4v-es";
    /**H.263 video*/
    public static final String VIDEO_3GPP = "video/3gpp";
}
