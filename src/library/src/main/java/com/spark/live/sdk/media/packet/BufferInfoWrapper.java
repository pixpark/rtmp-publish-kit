package com.spark.live.sdk.media.packet;

import android.media.MediaCodec;

/**
 *
 * Created by devzhaoyou on 8/8/16.
 */
public class BufferInfoWrapper {


    public BufferInfoWrapper(MediaCodec.BufferInfo bufferInfo) {
        this.offset = bufferInfo.offset;
        this.presentationTimeUs = bufferInfo.presentationTimeUs;
        this.size = bufferInfo.size;
        this.flags = bufferInfo.flags;
    }

    /**
     * The start-offset of the data in the buffer.
     */
    public int offset;

    /**
     * The amount of data (in bytes) in the buffer.  If this is {@code 0},
     * the buffer has no data in it and can be discarded.  The only
     * use of a 0-size buffer is to carry the end-of-stream marker.
     */
    public int size;

    /**
     * The presentation timestamp in microseconds for the buffer.
     * This is derived from the presentation timestamp passed in
     * with the corresponding input buffer.  This should be ignored for
     * a 0-sized buffer.
     */
    public long presentationTimeUs;

    /**
     * Buffer flags associated with the buffer.  A combination of
     * BUFFER_FLAG_KEY_FRAME and BUFFER_FLAG_END_OF_STREAM.
     *
     * <p>Encoded buffers that are key frames are marked with
     * BUFFER_FLAG_KEY_FRAME.
     *
     * <p>The last output buffer corresponding to the input buffer
     * marked with BUFFER_FLAG_END_OF_STREAM will also be marked
     * with BUFFER_FLAG_END_OF_STREAM. In some cases this could
     * be an empty buffer, whose sole purpose is to carry the end-of-stream
     * marker.
     */
    public int flags;

}
