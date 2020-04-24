package com.spark.live.sdk.media.codec;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.media.MediaFormat;
import android.os.Build;

import com.spark.live.sdk.util.LogUtil;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Date;

/**
 *
 * Created by devzhaoyou on 9/14/16.
 */
public class CodecManager {

    private MediaCodec mediaCodec = null;
    private MediaCodecInfo mediaCodecInfo = null;
    private MediaCodec.BufferInfo mediaCodecBufferInfo = null;
    private CodecState state = CodecState.IDLE;
    private ICodecCallback callback = null;
    private long presentationTimeUs;

    public enum CodecState {
        IDLE, CREATE, CONFIGURATION, START, ERROR
    }

    public void setCodecCallback(ICodecCallback callback) {
        this.callback = callback;
    }

    public synchronized void createCodec(String mimeType) throws IOException {
        if (state == CodecState.IDLE) {
            mediaCodecInfo = selectCodecInfoByMimeType(mimeType);
            if (mediaCodecInfo != null) {
                mediaCodec = MediaCodec.createByCodecName(mediaCodecInfo.getName());
                mediaCodecBufferInfo = new MediaCodec.BufferInfo();
                CodecState tmp = state;
                state = CodecState.CREATE;
                if (callback != null) {
                    callback.onStatusChanged(tmp, state);
                }
            } else {
                LogUtil.e("CodecManager createCodec: The mime type: " + mimeType + " is not supported!");
            }
        } else {
            LogUtil.e("CodecManager createCodec: The sate must be IDLE current is : " + state);
            if (callback != null) {
                callback.onError(state, "CodecManager createCodec: The sate must be IDLE current is : " + state);
            }
        }
    }

    public synchronized void initCodec(boolean isVideo) {
        if (state == CodecState.IDLE) {
            LogUtil.e("CodecManager initCodec: The state can not be IDLE current is: IDLE");
            if (callback != null) {
                callback.onError(state, "CodecManager initCodec: The state can not be IDLE current is: IDLE");
            }
            return;
        }
        MediaFormat format;
        if (isVideo) {
            VideoConfiguration.getInstance().findColorFormat(mediaCodecInfo);
            format = VideoConfiguration.getInstance().getMediaFormat();
        } else {
            format = AudioConfiguration.getInstance().getMediaFormat();
        }
        mediaCodec.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        CodecState tmp = state;
        state = CodecState.CONFIGURATION;
        if (callback != null) {
            callback.onStatusChanged(tmp, state);
        }

    }

    public synchronized void startCodec() {
        if (state != CodecState.CONFIGURATION) {
            LogUtil.e("CodecManager startCodec: The state must be CONFIGURATION current is : " + state);
            if (callback != null) {
                callback.onError(state, "CodecManager startCodec: The state must be CONFIGURATION current is : " + state);
            }
            return;
        }
        mediaCodec.start();
        presentationTimeUs = new Date().getTime() * 1000;
        CodecState tmp = state;
        state = CodecState.START;
        if (callback != null) {
            callback.onStatusChanged(tmp, state);
        }
    }

    public synchronized void handleVideoData(byte[] data, int... args) {
        if (state != CodecState.START) {
            LogUtil.e("CodecManager handleVideoData: The state must be START current is: " + state);
            if (callback != null) {
                callback.onError(state, "CodecManager handleVideoData: The state must be START current is: " + state);
            }
            return;
        }
        if(data == null) {
            LogUtil.w("CodecManager handleVideoData: Got a empty video frame!");
            return;
        }
        ByteBuffer[] inBuffers = mediaCodec.getInputBuffers();
        ByteBuffer[] outBuffers = mediaCodec.getOutputBuffers();
        int inBufferIndex = mediaCodec.dequeueInputBuffer(-1);

        if (inBufferIndex >= 0) {
            ByteBuffer bb = inBuffers[inBufferIndex];
            bb.clear();
            bb.put(data, 0, data.length);
            long pts = new Date().getTime() * 1000 - presentationTimeUs;
            mediaCodec.queueInputBuffer(inBufferIndex, 0, data.length, pts, 0);
        }

        for (; ; ) {
            int outBufferIndex = mediaCodec.dequeueOutputBuffer(mediaCodecBufferInfo, 0);
            if (outBufferIndex >= 0) {
                ByteBuffer bb = outBuffers[outBufferIndex];
                if (callback != null) {
                    callback.onEncodeVideo(bb, mediaCodecBufferInfo);
                }
                mediaCodec.releaseOutputBuffer(outBufferIndex, false);
            }

            if (outBufferIndex < 0) {
                break;
            }
        }
    }

    public synchronized void handleAudioData(byte[] data, int... args) {

        if (state != CodecState.START) {
            LogUtil.e("CodecManager handleAudioData: The state must be START current is: " + state);
            if (callback != null) {
                callback.onError(state, "CodecManager handleAudioData: The state must be START current is: " + state);
            }
            return;
        }

        ByteBuffer[] inBuffers = mediaCodec.getInputBuffers();
        ByteBuffer[] outBuffers = mediaCodec.getOutputBuffers();

        int inBufferIndex = mediaCodec.dequeueInputBuffer(-1);
        if (inBufferIndex >= 0) {
            ByteBuffer bb = inBuffers[inBufferIndex];
            bb.clear();
            bb.put(data, 0, data.length);
            long pts = new Date().getTime() * 1000 - presentationTimeUs;
            mediaCodec.queueInputBuffer(inBufferIndex, 0, data.length, pts, 0);
        }

        for (; ; ) {
            int outBufferIndex = mediaCodec.dequeueOutputBuffer(mediaCodecBufferInfo, 0);
            if (outBufferIndex >= 0) {
                ByteBuffer bb = outBuffers[outBufferIndex];
                if (callback != null) {
                    callback.onEncodeAudio(bb, mediaCodecBufferInfo);
                }
                mediaCodec.releaseOutputBuffer(outBufferIndex, false);
            } else {
                break;
            }
        }
    }

    public synchronized void stopCodec() {
        if (Build.VERSION.SDK_INT >= 21) {
            if (state == CodecState.IDLE) {
                LogUtil.e("CodecManager stopCodec: The state can not be IDLE!");
                if (callback != null) {
                    callback.onError(state, "CodecManager stopCodec: The state can not be IDLE!");
                }
                return;
            }
            mediaCodec.reset();
            presentationTimeUs = 0L;
            CodecState tmp = state;
            state = CodecState.CREATE;
            if (callback != null) {
                callback.onStatusChanged(tmp, state);
            }
        } else {
            if (state == CodecState.START) {
                mediaCodec.stop();
                presentationTimeUs = 0L;
                CodecState tmp = state;
                state = CodecState.CREATE;
                if (callback != null) {
                    callback.onStatusChanged(tmp, state);
                }
            } else {
                LogUtil.e("CodecManager stopCodec: The state must be START current is: " + state);
                if (callback != null) {
                    callback.onError(state, "CodecManager stopCodec: The state must be START current is: " + state);
                }
            }
        }
    }

    public synchronized void releaseCodec() {
        if (state == CodecState.IDLE) {
            LogUtil.e("CodecManager releaseCodec: The state can not be IDLE!");
            if (callback != null) {
                callback.onError(state, "CodecManager releaseCodec: The state can not be IDLE!");
            }
            return;
        }
        mediaCodec.release();
        mediaCodec = null;
        mediaCodecBufferInfo = null;
        mediaCodecInfo = null;
        presentationTimeUs = 0L;
        CodecState tmp = state;
        state = CodecState.IDLE;
        if (callback != null) {
            callback.onStatusChanged(tmp, state);
        }
        callback = null;
    }

    public CodecState getCodecState() {
        return state;
    }


    private MediaCodecInfo selectCodecInfoByMimeType(String mimeType) {
        int count = MediaCodecList.getCodecCount();
        for (int i = 0; i < count; i++) {
            MediaCodecInfo codecInfo = MediaCodecList.getCodecInfoAt(i);

            if (!codecInfo.isEncoder()) {
                continue;
            }
            String[] types = codecInfo.getSupportedTypes();
            for (String type : types) {
                if (type.equalsIgnoreCase(mimeType)) {
                    return codecInfo;
                }
            }
        }
        return null;
    }

}
