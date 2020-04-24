package com.spark.live.sdk.media.codec;

import android.graphics.Point;
import android.media.MediaFormat;

import com.spark.live.sdk.util.LogUtil;
import com.spark.live.sdk.util.StringUtil;

/**
 *
 * Created by devzhaoyou on 7/26/16.
 */
public interface ICodecManager {

    /**
     *
     */
    void init(CodecInitType codeInitType);

    /**
     *
     */
    void start();

    void setOnEncodeAVDataCallback(IOnEncodeAVData callback);
    /**
     *
     * @param data 视频数据
     * @param args 额外参数
     */
    void handleVideoData(byte[] data, int... args);

    /**
     *
     * @param data 音频数据
     * @param args 额外参数
     */
    void handleAudioData(byte[] data, int... args);

    /**
     *
     * @param data 音频数据
     * @param args 额外参数
     */
    void handleAudioData(short[] data, int... args);

    /**
     *
     */
    void pause();

    /**
     *
     */
    void stop();

    /**
     *
     */
    void release();


    void reconfig();

    MediaFormat getMediaFormat();
    enum CodecState {
        IDLE, INIT, RESUME, PAUSE, STOP, RELEASE, ERROR
    }

    /**
     *
     */
    abstract class Stub implements ICodecManager{
        @Override
        public void init(CodecInitType codeInitType) {}

        @Override
        public void start() {}

        @Override
        public void setOnEncodeAVDataCallback(IOnEncodeAVData callback) {

        }

        @Override
        public void handleVideoData(byte[] data, int... args) {}

        @Override
        public void handleAudioData(byte[] data, int... args) {}

        @Override
        public void handleAudioData(short[] data, int... args) {}

        @Override
        public void pause() {}

        @Override
        public void stop() {}

        @Override
        public void release() {}

        @Override
        public void reconfig() {

        }
    }

    /**
     *
     */
    abstract class Configuration {

        /**Audio Params*/
        protected int sampleRate;
        protected int channel;
        protected int audioFormat;


        /**Video Params*/
        protected int fps;
        protected int gop;
        protected int videoWidth;
        protected int videoHeight;
        protected int videoColorFormat;
        protected Point mPreviewSize;
        protected int rotateDegree;

        /**Common Params*/
        protected int mTrack;
        protected int mBitrate;
        protected String mimeType;



        public void setMimeType(String mimeType) {
            this.mimeType = mimeType;
        }



        public Configuration setBitrate(int mBitrate) {
            this.mBitrate = mBitrate;
            LogUtil.i(StringUtil.format("The bitrate is set to : %d", mBitrate));
            return this;
        }

        public Configuration setSampleRate(int mSampleRate) {
            this.sampleRate = mSampleRate;
            LogUtil.i(StringUtil.format("The sample rate is set to : %d", mSampleRate));
            return this;
        }

        public Configuration setChannel(int mChannel) {
            this.channel = mChannel;
            LogUtil.i(StringUtil.format("The audio chanel is set to: %d", mChannel));
            return this;
        }

        public Configuration setAudioFormat(int mAudioFormat) {
            this.audioFormat = mAudioFormat;
            LogUtil.i(StringUtil.format("The audio sample format is set to : %d", mAudioFormat));
            return this;
        }

        public Configuration setFPS(int mFPS) {
            this.fps = mFPS;
            LogUtil.i(StringUtil.format("The video fps is set to : %d", mFPS));
            return this;
        }

        public Configuration setGOP(int mGOP) {
            this.gop = mGOP;
            LogUtil.i(StringUtil.format("The video gop is set to : %d", mGOP));
            return this;
        }

        public Configuration setVideoWidth(int mVideoWidth) {
            this.videoWidth = mVideoWidth;
            LogUtil.i(StringUtil.format("The video width is set to : %d", mVideoWidth));
            return this;
        }

        public Configuration setVideoHeight(int mVideoHeight) {
            this.videoHeight = mVideoHeight;
            LogUtil.i(StringUtil.format("The video height is set to : %d", mVideoHeight));
            return this;
        }

        public Configuration setVideoColorFormat(int mVideoColorFormat) {
            this.videoColorFormat = mVideoColorFormat;
            LogUtil.i(StringUtil.format("The video color format is set to : %d", mVideoColorFormat));
            return this;
        }

        public Configuration setPreviewSize(Point mPreviewSize) {
            this.mPreviewSize = mPreviewSize;
            LogUtil.i(StringUtil.format("The video preview size is : %s", mPreviewSize.toString()));
            return this;
        }

        public Configuration setTrack(int mTrack) {
            this.mTrack = mTrack;
            LogUtil.i(StringUtil.format("The track is : %d", mTrack));
            return this;
        }

        public int getSampleRate() {
            return sampleRate;
        }

        public int getChannel() {
            return channel;
        }

        public int getAudioFormat() {
            return audioFormat;
        }

        public int getFps() {
            return fps;
        }

        public int getGop() {
            return gop;
        }

        public int getVideoWidth() {
            return videoWidth;
        }

        public int getVideoHeight() {
            return videoHeight;
        }

        public int getVideoColorFormat() {
            return videoColorFormat;
        }

        public Point getmPreviewSize() {
            return mPreviewSize;
        }

        public int getRotateDegree() {
            return rotateDegree;
        }

        public int getmTrack() {
            return mTrack;
        }

        public int getmBitrate() {
            return mBitrate;
        }

        public String getMimeType() {
            return mimeType;
        }

        public void setRotateDegree(int rotateDegree) {
            this.rotateDegree = rotateDegree;
        }

        public abstract void config();
    }


    enum CodecInitType{
        ENCODE_AUDIO, ENCODE_VIDEO, DECODE_AUDIO, DECODE_VIDEO, ENCODE_AV, DECODE_AV
    }


    class Constant {

        /*public static final int ENCODE_AUDIO = 100;
        public static final int ENCODE_VIDEO = 200;
        public static final int DECODE_AUDIO = 300;
        public static final int DECODE_VIDEO = 400;
        public static final int ENCODE_AV = 500;
        public static final int DECODE_AV = 600;*/

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


        /**AMR narrowband audio*/
        public static final String AUDIO_3GPP = "audio/3gpp";

        /**AMR wideband audio*/
        public static final String AUDIO_AMR_WB = "audio/amr-wb";

        /**MPEG1/2 audio layer III*/
        public static final String AUDIO_MPEG = "audio/mpeg";

        /**AAC audio (note, this is raw AAC packets, not packaged in LATM!)*/
        public static final String AUDIO_MP4A_LATM = "audio/mp4a-latm";
        /**vorbis audio*/
        public static final String AUDIO_VORBIS = "audio/vorbis";

        /**G.711 alaw audio*/
        public static final String AUDIO_G711_ALAW = "audio/g711-alaw";

        /**G.711 ulaw audio*/
        public static final String AUDIO_G711MLAW = "audio/g711-mlaw";
    }

}
