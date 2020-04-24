package com.spark.live.sdk.engine;

import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.Point;
import android.hardware.Camera;
import android.media.AudioFormat;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Message;
import android.view.SurfaceHolder;

import com.spark.live.sdk.cacher.CacheFrame;
import com.spark.live.sdk.cacher.CacheMuxer;
import com.spark.live.sdk.cacher.IMuxer;
import com.spark.live.sdk.cacher.IMuxerCallback;
import com.spark.live.sdk.media.codec.CodecManager;
import com.spark.live.sdk.media.codec.ICodecCallback;
import com.spark.live.sdk.media.codec.VideoConfiguration;
import com.spark.live.sdk.media.device.OnAVDataCallback;
import com.spark.live.sdk.media.codec.AudioConfiguration;
import com.spark.live.sdk.media.device.audio.AudioRecorderManager;
import com.spark.live.sdk.media.device.audio.IAudioDevice;
import com.spark.live.sdk.media.device.camera.CameraConfigManager;
import com.spark.live.sdk.media.device.camera.CameraManager;
import com.spark.live.sdk.media.device.camera.CameraKeeper;
import com.spark.live.sdk.media.device.camera.ICameraDevice;
import com.spark.live.sdk.media.device.camera.ICameraEvent;
import com.spark.live.sdk.media.packet.BufferInfoWrapper;
import com.spark.live.sdk.media.packet.tag.audio.AudioTagHeader;
import com.spark.live.sdk.media.packet.tag.builder.BuildCommander;
import com.spark.live.sdk.media.packet.tag.builder.DataValueObject;
import com.spark.live.sdk.media.packet.tag.builder.audio.AACAudioTagBuilder;
import com.spark.live.sdk.media.packet.tag.builder.video.AVCVideoTagBuilder;
import com.spark.live.sdk.media.packet.tag.common.FLVTag;
import com.spark.live.sdk.util.JniYuvUtil;
import com.spark.live.sdk.util.LogUtil;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * SDK 对外接口
 * Created by devzhaoyou on 8/30/16.
 */

public class SimpleLivePulisherEngine extends ISimpleLiveEngine.Stub {

    public class State {
        // rtmp network state
        public static final int NetConnectSuccess = 100;
        public static final int NetConnectFailed = 101;
//        public static final int PublishMediaSuccess = 102;
        public static final int PublishMediaFailed = 103;
//        public static final int Publishing = 104;
        public static final int PushingDown = 105;
        public static final int Unknown = 106;


        //device state
        public static final int CameraOpenFaild = 200;
        public static final int CameraOpenSuccess = 201;

    }

    public class VIDEO_QUALITY {
        public static final int STANDARD = 0;
        public static final int HIGH = 1;
        public static final int SUPER  = 2;

    }

    private CodecManager videoCodec, audioCodec;
    private BuildCommander audioTagFactory, videoTagFactory;

    private ICameraDevice cameraManager;
    private IAudioDevice audioManager;

    private IMuxer cacheMuxer;

    private Context context;
    private String rtmpUrl;
    private int cameraIndex;
    private Handler handler;

    ISimpleLiveEngineEventCallback stateCallback;
    private boolean isRTMPConnected = false;
    private boolean isCaptureDeviceWorking = false;

    private static ISimpleLiveEngine mInstance = null;
    public static ISimpleLiveEngine getInstance() {
        if (mInstance == null) {
            mInstance = new SimpleLivePulisherEngine();
        }
        return mInstance;
    }

    private SimpleLivePulisherEngine() {
        handler = new Handler();
    }

    @Override
    public void Init(Context context) {
        this.context = context;
        this.cameraIndex = CameraManager.CAMERA_OPEN_CAMERA_FRONT;

        AVDataCallback dataCallback = new AVDataCallback();
        cameraManager = CameraManager.getInstance("Camera");
        cameraManager.setAVDataCallback(dataCallback);
        cameraManager.setCameraEventCallback(new CameraEventCallback());


        CameraConfigManager.getInstance()
                .setDesiredPreviewSize(GetResolution(VIDEO_QUALITY.STANDARD))
                .setFlashMode(false)
                .setFocusMode(true)
                .setFpsRange(new int[]{20, 30})
                .setImageFormat(ImageFormat.NV21)
                .setSceneMode(Camera.Parameters.SCENE_MODE_LANDSCAPE)
                .setWhiteBalance(Camera.Parameters.WHITE_BALANCE_AUTO);

        audioManager = AudioRecorderManager.getInstance();
        audioManager.setAVDataCallback(dataCallback);
        AudioConfiguration.getInstance()
                .setSampleRateInHZ(AudioConfiguration.SAMPLE_RATE_44100)
                .setAudioSource(MediaRecorder.AudioSource.MIC)
                .setAudioFormat(AudioFormat.ENCODING_PCM_16BIT)
                .setChannelConfig(AudioFormat.CHANNEL_IN_MONO)
                .setMimeType(AudioConfiguration.AUDIO_MP4A_LATM)
                .setBitrate(24);
        Map<String, Object> params = new HashMap<>();
        params.put(AudioTagHeader.KEY_SOUND_FORMAT, AudioTagHeader.SOUND_FORMAT_AAC);
        params.put(AudioTagHeader.KEY_SOUND_RATE, AudioTagHeader.SAMPLE_RATE_44K);
        params.put(AudioTagHeader.KEY_SOUND_SIZE, AudioTagHeader.SOUND_SIZE_SND_16_BIT);
        params.put(AudioTagHeader.KEY_SOUND_TYPE, AudioTagHeader.SOUND_TYPE_SND_MONO);
        audioTagFactory = new BuildCommander(new AACAudioTagBuilder(params));

        OnEncodeCallback onCodecEncodeCallback = new OnEncodeCallback();
        videoCodec = new CodecManager();
        videoCodec.setCodecCallback(onCodecEncodeCallback);
        audioCodec = new CodecManager();
        audioCodec.setCodecCallback(onCodecEncodeCallback);

        cacheMuxer = CacheMuxer.getInstance(muxerCallback);
    }

    public void SetVideoQuality(int quality) {
        CameraConfigManager.getInstance().setDesiredPreviewSize(GetResolution(quality));
    }


    private void SetMirror(boolean enalbe) {

    }

    private void SetWatermark() {

    }


    // not avilable
    private void setBeautyFilter(boolean enalbe) {

    }

    // 硬编开关
    private void setHardwareEncoder(boolean enalbe) {

    }


    private void setCameraIndex(int id) {
        this.cameraIndex = id;
    }

    @Override
    public void Start(String rtmpUrl) {

        if(!isCaptureDeviceWorking) {
            cacheMuxer.startMuxer(rtmpUrl);
        }
    }

    @Override
    public void Pause() {
        cameraManager.stopPreview();
        audioManager.pauseRecorder();
    }

    @Override
    public void Resume() {
        cameraManager.startPreview();
        audioManager.resumeRecorder();
    }

    @Override
    public void SwitchCamera() {
        cameraManager.switchCamera();
        cameraManager.startPreview();
    }

    @Override
    public void Destroy() {
        if (audioManager != null) {
            audioManager.stopRecorder();
            audioManager = null;
        }
        if (cameraManager != null) {
            cameraManager.exit();
            cameraManager = null;
        }
        if (videoCodec != null) {
            videoCodec.stopCodec();
            videoCodec.releaseCodec();
        }

        if (audioCodec != null) {
            audioCodec.stopCodec();
            audioCodec.releaseCodec();
        }

        if (cacheMuxer != null) {
            cacheMuxer.closeMuxer();
            cacheMuxer = null;
        }
        mInstance = null;
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if(!isCaptureDeviceWorking) {
            Message.obtain(cameraManager.getHandler(), CameraManager.CAMERA_OPEN_CAMERA_FRONT, holder).sendToTarget();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Point size = CameraConfigManager.getInstance().getFinalPreviewSize();
        if ((size != null) && (videoCodec.getCodecState() == CodecManager.CodecState.START)) {
            cameraManager.rotateCamera(context);

            videoCodec.stopCodec();
            VideoConfiguration.getInstance().setFrameSize(
                    CameraConfigManager.getInstance().getFinalPreviewSize(),
                    CameraConfigManager.getInstance().getRotateDegree());
            videoCodec.initCodec(true);
            videoCodec.startCodec();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                cameraManager.stopPreview();
            }
        });
    }

    @Override
    public void setStateCallback(ISimpleLiveEngineEventCallback stateCallback) {
        this.stateCallback = stateCallback;
    }

    private void callbackState(int state, String desp) {
        if(stateCallback != null) {
            stateCallback.onGotECliveEngineEvent(state, desp);
        }
    }

    private IMuxerCallback muxerCallback = new IMuxerCallback() {
        @Override
        public void onMuxerResume() {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    isRTMPConnected = true;
                    callbackState(State.NetConnectSuccess, "connect to rtmp server: " + rtmpUrl + " success, start push video and audio!");
                }
            });
        }

        @Override
        public void onError(String error) {
            LogUtil.e(error);
            callbackState(State.NetConnectFailed, "connect to rtmp server:" + rtmpUrl +" failed, please check again!");
        }
    };

    /**
     * 音视频数据捕获回调接口
     */
    private class AVDataCallback extends OnAVDataCallback.Stub {
        @Override @SuppressWarnings("deprecation")
        public void onVideoData(byte[] data, Object... args) {
            if(!isRTMPConnected) {
                return;
            }
            final int facing = (Integer) args[0];
            Point size = CameraConfigManager.getInstance().getFinalPreviewSize();
            int rotate = CameraConfigManager.getInstance().getRotateDegree();
            if (facing == Camera.CameraInfo.CAMERA_FACING_FRONT && rotate == 90) {
                rotate = 270;
            }
            byte[] frame =  null;
            switch (VideoConfiguration.getInstance().getVideoColorFormat()) {
                case MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Planar: // I420
                    frame = JniYuvUtil.NV21ToI420Scaled(data, size.x, size.y, false, rotate, 0, 0, size.x, size.y);
                    break;
                case MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420SemiPlanar: //NV12
                    frame = JniYuvUtil.NV21ToNV12Scaled(data, size.x, size.y, false, rotate, 0, 0, size.x, size.y);
                    break;
                default:
                    videoCodec.handleVideoData(frame);
                    return;
            }
            videoCodec.handleVideoData(frame);
        }

        @Override
        public void onAudioData(byte[] data, Object... args) {
            if(!isRTMPConnected) {
                return;
            }
            audioCodec.handleAudioData(data);
        }
    }

    private class OnEncodeCallback extends ICodecCallback.stub {
        @Override
        public void onEncodeAudio(ByteBuffer buffer, MediaCodec.BufferInfo bufferInfo) {
            DataValueObject vo = new DataValueObject();
            vo.data = buffer;
            vo.bufferInfo = new BufferInfoWrapper(bufferInfo);
            List<FLVTag> tags = audioTagFactory.build(vo);
            for (FLVTag tag : tags) {
                int pst = (int) (bufferInfo.presentationTimeUs / 1000);
                CacheFrame cacheFrame = new CacheFrame(pst, tag);
                cacheFrame.setFrameType(CacheFrame.AUDIO_FRAME);
                cacheMuxer.sendFrame(cacheFrame);
            }
        }

        @Override
        public void onEncodeVideo(ByteBuffer buffer, MediaCodec.BufferInfo bufferInfo) {
            DataValueObject vo = new DataValueObject();
            vo.data = buffer;
            vo.bufferInfo = new BufferInfoWrapper(bufferInfo);
            List<FLVTag> tags = videoTagFactory.build(vo);
            for (FLVTag tag : tags) {
                ByteBuffer byteBuffer = tag.toBinaryData();
                byte[] data = new byte[byteBuffer.remaining()];
                byteBuffer.get(data);
                byteBuffer.rewind();
                int pst = (int) (bufferInfo.presentationTimeUs / 1000);
                CacheFrame cacheFrame = new CacheFrame(pst, tag);
                cacheFrame.setFrameType(CacheFrame.VIDEO_FRAME);
                cacheMuxer.sendFrame(cacheFrame);
            }
        }

        @Override
        public void onError(CodecManager.CodecState state, String error) {
            callbackState(State.PublishMediaFailed, error);
        }
    }

    /**
     * 摄像头事件回调接口
     */
    private class CameraEventCallback implements ICameraEvent {
        @Override
        public void onCameraOpen(CameraKeeper camera) {
            callbackState(State.CameraOpenSuccess, "camera open success, start camera preview!");

            CameraConfigManager.getInstance().setCamera(camera, context);
            VideoConfiguration.getInstance()
                    .setBitrate(800)
                    .setFps(20)
                    .setGop(5)
                    .setMimeType(VideoConfiguration.VIDEO_AVC)
                    .setFrameSize(CameraConfigManager.getInstance().getFinalPreviewSize(),
                            CameraConfigManager.getInstance().getRotateDegree());

            if(!isCaptureDeviceWorking) {
                isCaptureDeviceWorking = true;
                try {
                    videoCodec.createCodec(VideoConfiguration.VIDEO_AVC);
                    videoCodec.initCodec(true);
                    videoCodec.startCodec();
                    audioCodec.createCodec(AudioConfiguration.AUDIO_MP4A_LATM);
                    audioCodec.initCodec(false);
                    audioCodec.startCodec();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                cameraManager.startPreview();
                audioManager.startRecorder();
            }
            if (videoTagFactory == null) {
                videoTagFactory = new BuildCommander(new AVCVideoTagBuilder(null));
            }
        }

        @Override
        public void onErrorEvent(String error) {
            LogUtil.i(error);
            callbackState(State.CameraOpenFaild, "camera open faild, please check your camera permission or camera index!");
        }
    }


    private Point GetResolution(int quality) {
        switch (quality) {
            case VIDEO_QUALITY.STANDARD :
                return new Point(640, 360);
            case VIDEO_QUALITY.HIGH:
                return new Point(960, 540);
            case VIDEO_QUALITY.SUPER:
                return new Point(1280, 720);
        }
        return new Point(640, 360);
    }
}
