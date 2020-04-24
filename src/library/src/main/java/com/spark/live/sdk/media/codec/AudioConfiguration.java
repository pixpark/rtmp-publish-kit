package com.spark.live.sdk.media.codec;

import android.media.AudioFormat;
import android.media.MediaFormat;


/**
 *
 * Created by devzhaoyou on 9/7/16.
 */

public class AudioConfiguration {

    public static final int SAMPLE_RATE_44100 = 44100;
    public static final int SAMPLE_RATE_22050 = 22050;
    public static final int SAMPLE_RATE_16000 = 16000;
    public static final int SAMPLE_RATE_11025 = 11025;

    private static AudioConfiguration mInstance = null;
    private static final Object lock = new Object();
    public static AudioConfiguration getInstance() {
        synchronized (lock) {
            if (mInstance == null) {
                mInstance = new AudioConfiguration();
            }
            return mInstance;
        }
    }

    private int[] optionalSampleRates = {44100, 22050, 16000, 11025};
    private MediaFormat mediaFormat;
    /**
     * source
     */
    private int audioSource;

    /**
     * the sample rate expressed in Hertz. 44100Hz is currently the only rate
     * that is guaranteed to work on all devices, but other rates such as 22050,
     * 16000, and 11025 may work on some devices.
     */
    private int sampleRateInHZ;

    /**
     * describes the configuration of the audio channels.
     * See CHANNEL_IN_MONO and CHANNEL_IN_STEREO.
     * CHANNEL_IN_MONO is guaranteed to work on all devices.
     */
    private int channelConfig;

    /**
     * the format in which the audio data is to be returned.
     * See ENCODING_PCM_8BIT, ENCODING_PCM_16BIT, and ENCODING_PCM_FLOAT.
     */
    private int audioFormat;

    /**
     * the total size (in bytes) of the buffer where audio data is written to during the recording.
     * New audio data can be read from this buffer in smaller chunks than this size.
     * See getMinBufferSize(int, int, int) to determine the minimum required buffer size
     * for the successful creation of an AudioRecord instance.
     * Using values smaller than getMinBufferSize() will result in an initialization failure.
     */
    private int bufferSizeInByte;

    private AudioConfiguration() {
        mediaFormat = new MediaFormat();
        mediaFormat.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, 0);
        mediaFormat.setInteger(MediaFormat.KEY_AAC_PROFILE, 2);
    }

    public AudioConfiguration setAudioSource(int audioSource) {
        this.audioSource = audioSource;
        return this;
    }

    public AudioConfiguration setSampleRateInHZ(int sampleRateInHZ) {
        this.sampleRateInHZ = sampleRateInHZ;
        mediaFormat.setInteger(MediaFormat.KEY_SAMPLE_RATE, sampleRateInHZ);
        return this;
    }

    public AudioConfiguration setChannelConfig(int channelConfig) {
        this.channelConfig = channelConfig;
        if (channelConfig == AudioFormat.CHANNEL_IN_MONO) {
            mediaFormat.setInteger(MediaFormat.KEY_CHANNEL_COUNT, 1);
        } else if (channelConfig == AudioFormat.CHANNEL_IN_STEREO) {
            mediaFormat.setInteger(MediaFormat.KEY_CHANNEL_COUNT, 2);
        }
        return this;
    }

    public AudioConfiguration setAudioFormat(int audioFormat) {
        this.audioFormat = audioFormat;
        return this;
    }

    public AudioConfiguration setBufferSizeInByte(int bufferSizeInByte) {
        this.bufferSizeInByte = bufferSizeInByte;
        return this;
    }

    public int getAudioSource() {
        return audioSource;
    }

    public int getSampleRateInHZ() {
        return sampleRateInHZ;
    }

    public int getChannelConfig() {
        return channelConfig;
    }

    public int getAudioFormat() {
        return audioFormat;
    }

    public int getBufferSizeInByte() {
        return bufferSizeInByte;
    }

    public int[] getOptionalSampleRates() {
        return optionalSampleRates;
    }

    public MediaFormat getMediaFormat() {
        return mediaFormat;
    }

    public String getMimeType() {
        return mediaFormat.getString(MediaFormat.KEY_MIME);
    }

    public AudioConfiguration setMimeType(String mimeType) {
        mediaFormat.setString(MediaFormat.KEY_MIME, mimeType);
        return this;
    }

    public AudioConfiguration setBitrate(int mBitrate) {
        mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, mBitrate * 1000);
        return this;
    }

    @Override
    public String toString() {
        return super.toString();
    }

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
