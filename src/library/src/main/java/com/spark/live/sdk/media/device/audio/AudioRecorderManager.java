package com.spark.live.sdk.media.device.audio;

import android.media.AudioRecord;

import com.spark.live.sdk.media.codec.AudioConfiguration;
import com.spark.live.sdk.media.device.OnAVDataCallback;
import com.spark.live.sdk.util.LogUtil;

/**
 *
 * Created by devzhaoyou on 9/7/16.
 */

public class AudioRecorderManager extends Thread implements IAudioDevice {

    private static final Object lock = new Object();
    private static IAudioDevice mInstance;

    public static IAudioDevice getInstance() {
        synchronized (lock) {
            if (mInstance == null) {
                mInstance = new AudioRecorderManager();
            }
            return mInstance;
        }
    }

    private OnAVDataCallback callback;
    private AudioRecord recorder = null;
    private byte[] buffer = null;

    private boolean isRecording;
    private boolean isPause;

    private AudioRecorderManager() {
    }

    @Override
    public void setAVDataCallback(OnAVDataCallback callback) {
        synchronized (lock) {
            this.callback = callback;
        }
    }

    @Override
    public void startRecorder() {
        start();
    }

    @Override
    public void resumeRecorder() {
        synchronized (lock) {
            isPause = false;
        }
    }

    @Override
    public void pauseRecorder() {
        synchronized (lock) {
            isPause = true;
        }
    }

    @Override
    public void stopRecorder() {
        synchronized (lock) {
            if (isRecording) {
                stopSafely();
            }
            if (recorder != null) {
                recorder.stop();
                recorder.release();
                recorder = null;
            }
            buffer = null;
            callback = null;
            mInstance = null;
        }
    }

    private void stopSafely() {
        try {
            if (isRecording && isAlive()) {
                isRecording = false;
                this.interrupt();
                this.join(5);
                recorder = null;
                LogUtil.i("AudioRecorder worker thread quiet safely!!");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private void initRecorder() {
        synchronized (lock) {
            if (AudioConfiguration.getInstance().getSampleRateInHZ() > 0) {
                isRecording = createRecorder(AudioConfiguration.getInstance().getSampleRateInHZ());
            } else {
                int[] optional = AudioConfiguration.getInstance().getOptionalSampleRates();
                for (int sampleRate : optional) {
                    if (createRecorder(optional[sampleRate])) {
                        isRecording = true;
                        break;
                    }
                }
            }
            if (!isRecording) {
                LogUtil.e("AudioManager: Could not open AudioRecorder!");
            } else {
                recorder.startRecording();
                LogUtil.i("AudioManager: Open " + AudioConfiguration.getInstance().toString());
            }
        }
    }

    private boolean createRecorder(int sampleRateInHz) {

        AudioConfiguration configManger = AudioConfiguration.getInstance();
        int bufferSizeInByte = AudioRecord.getMinBufferSize(
                sampleRateInHz, configManger.getChannelConfig(),
                configManger.getAudioFormat());
        bufferSizeInByte = Math.min(4096, bufferSizeInByte);
        configManger.setBufferSizeInByte(bufferSizeInByte);

        recorder = new AudioRecord(configManger.getAudioSource(),
                configManger.getSampleRateInHZ(),
                configManger.getChannelConfig(),
                configManger.getAudioFormat(),
                bufferSizeInByte);
        if (recorder.getState() == AudioRecord.STATE_INITIALIZED) {
            buffer = new byte[bufferSizeInByte];
            configManger.setSampleRateInHZ(sampleRateInHz);
            return true;
        }

        return false;
    }

    @Override
    public void run() {
        initRecorder();
        while (isRecording) {
            if (!isPause) {
                if (recorder != null) {
                    int size = recorder.read(buffer, 0, buffer.length);
                    if (size <= 0) {
                        LogUtil.i("audio ignore, no payload to read.");
                        continue;
                    }
                    if (callback != null) {
                        callback.onAudioData(buffer);
                    }
                } else {
                    LogUtil.e("The Audio Record is null!");
                }
            } else {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    LogUtil.w("AudioRecorderManager: worker thread interrupted!");
                    break;
                }
            }

        }
    }
}
