package com.spark.live.sdk.media.packet;

import java.nio.ByteBuffer;

/**
 *
 * Created by devzhaoyou on 8/8/16.
 */
public interface IPack {

    int VIDEO_TRACK = -100;
    int AUDIO_TRACK = -200;

    void setTrack(MediaFormatWrapper mediaFormatWrapper, int avFlag);

    void createFileHeader(byte version, boolean audioFlag, boolean videoFlag, int lengthInByte );

    void packAudioData(ByteBuffer audioBuffer, BufferInfoWrapper bufferInfoWrapper);

    void packVideoData(ByteBuffer videoBuffer, BufferInfoWrapper bufferInfoWrapper);
    
    
    abstract class Stub implements IPack {
        @Override
        public void setTrack(MediaFormatWrapper mediaFormatWrapper, int avFlag) {
            
        }

        @Override
        public void createFileHeader(byte version, boolean audioFlag, boolean videoFlag, int lengthInByte) {

        }

        @Override
        public void packAudioData(ByteBuffer audioBuffer, BufferInfoWrapper bufferInfoWrapper) {

        }

        @Override
        public void packVideoData(ByteBuffer videoBuffer, BufferInfoWrapper bufferInfoWrapper) {

        }
    }
}
