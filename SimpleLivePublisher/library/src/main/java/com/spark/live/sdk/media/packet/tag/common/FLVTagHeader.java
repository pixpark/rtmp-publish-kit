package com.spark.live.sdk.media.packet.tag.common;

import com.spark.live.sdk.util.BinaryUtil;
import com.spark.live.sdk.util.LogUtil;

import java.nio.ByteBuffer;

/**
 * FLV Tag 通用头
 * Created by devzhaoyou on 8/12/16.
 */
public abstract class FLVTagHeader implements ISuperTagOperation {

    /**
     * Reserved Filter Tag Type Data size 一起占四个字节
     */
    protected int RESERVED_FILTER_TAG_TYPE_DATA_SIZE;
    /**
     * Timestamp Timestamp extended 一起占四个字节
     */
    protected int TIMESTAMP_TIMESTAMP_EXTENDED;
    /**
     * StreamID 占四个字节中的低三个字节
     */
    protected int STREAM_ID;

    /**
     * UB [2] Reserved for FMS, should be 0
     */
    protected int reserved;

    /**
     * UB [1] Indicates if packets are filtered.
     * 0 = No pre-processing required.
     * 1 = Pre-processing (such as decryption) of the packet is
     * required before it can be rendered.
     * Shall be 0 in unencrypted files, and 1 for encrypted tags.
     * &See Annex F. FLV Encryption for the use of filters.
     */
    protected int filter;

    /**
     * UB [5] Type of contents in this tag. The following types are
     * defined:
     * 8 = audio
     * 9 = video
     * 18 = script data
     */
    protected int tagType;

    /**
     * UI24 Length of the message. Number of bytes after StreamID to
     * end of tag (Equal to length of the tag – 11)
     */
    protected int dataSize;

    /**
     * UI24 Time in milliseconds at which the data in this tag applies.
     * This value is relative to the first tag in the FLV file, which
     * always has a timestamp of 0.
     */
    protected int timeStamp;

    /**
     * Extension of the Timestamp field to form a SI32 value. This
     * field represents the upper 8 bits, while the previous
     * Timestamp field represents the lower 24 bits of the time in
     * milliseconds.
     */
    protected int timeStampExtended;

    /**
     * Always 0
     */
    protected int streamId;

    protected ByteBuffer flvBinaryHeader;


    public FLVTagHeader setFilter(int filter) {

        if (filter < FLVTag.FLVTagConfigConstant.FLV_TAG_FILTER_MIN_VALUE ||
                filter > FLVTag.FLVTagConfigConstant.FLV_TAG_FILTER_MAX_VALUE) {
            LogUtil.e("The filter must be between FLV_TAG_FILTER_MIN_VALUE" +
                    "and FLV_TAG_FILTER_MAX_VALUE");
            return null;
        }

        this.filter = filter;
        return this;
    }

    public FLVTagHeader setTagType(int tagType) {
        if (tagType < FLVTag.FLVTagConfigConstant.TAG_TYPE_MIN_VALUE ||
                tagType > FLVTag.FLVTagConfigConstant.TAG_TYPE_MAX_VALUE) {
            LogUtil.e("The tagType must be between TAG_TYPE_MIN_VALUE" +
                    "and TAG_TYPE_MAX_VALUE");
            return null;
        }

        this.tagType = tagType;
        return this;
    }

    public FLVTagHeader setTimeStamp(int timeStamp) {


        this.timeStamp = timeStamp;

        return this;
    }

    public FLVTagHeader setTimeStampExtended(int timeStampExtended) {
        this.timeStampExtended = timeStampExtended;

        return this;
    }

    public int getTimeStamp() {
        return timeStamp;
    }

    public int getTimeStampExtended() {
        return timeStampExtended;
    }

    public ByteBuffer getFlvBinaryHeader() {
        return flvBinaryHeader;
    }

    @Override
    public ByteBuffer toBinaryData() {
        /**第一个四字节*/
        RESERVED_FILTER_TAG_TYPE_DATA_SIZE |= (FLVTag.FLV_TAG_RESERVED_MASK & reserved << 30);
        RESERVED_FILTER_TAG_TYPE_DATA_SIZE |= (FLVTag.FLV_TAG_FILTER_MASK & filter << 29);
        RESERVED_FILTER_TAG_TYPE_DATA_SIZE |= (FLVTag.FLV_TAG_TAG_TYPE_MASK & tagType << 24);
        RESERVED_FILTER_TAG_TYPE_DATA_SIZE |= (FLVTag.FLV_TAG_DATA_SIZE_MASK & dataSize);

        /**第二个四字节*/
        TIMESTAMP_TIMESTAMP_EXTENDED |= (FLVTag.FLV_TAG_TIMESTAMP_MASK & timeStamp << 8);
        TIMESTAMP_TIMESTAMP_EXTENDED |= (FLVTag.FLV_TAG_TIMESTAMP_EXTENDED_MASK & timeStampExtended);

        /**低三字节*/
        STREAM_ID |= (FLVTag.FLV_TAG_STREAM_ID_MASK & streamId);

        byte[] result = new byte[11];
        byte[] array_4_size = BinaryUtil.getBytesFromIntValue(RESERVED_FILTER_TAG_TYPE_DATA_SIZE, 4);
        byte[] array_4_size_ = BinaryUtil.getBytesFromIntValue(TIMESTAMP_TIMESTAMP_EXTENDED, 4);
        byte[] array_3_size = BinaryUtil.getBytesFromIntValue(STREAM_ID, 3);

        final int len1 = array_4_size.length;
        final int len2 = array_4_size_.length;
        final int len3 = array_3_size.length;

        System.arraycopy(array_4_size, 0, result, 0, len1);
        System.arraycopy(array_4_size_, 0, result, len1, len2);
        System.arraycopy(array_3_size, 0, result, len1 + len2, len3);

        flvBinaryHeader = ByteBuffer.wrap(result).asReadOnlyBuffer();
        return flvBinaryHeader;
    }

    @Override
    public String toString() {
        return "FLVTagHeader{" +
                "RESERVED_FILTER_TAG_TYPE_DATA_SIZE=" + RESERVED_FILTER_TAG_TYPE_DATA_SIZE +
                ", TIMESTAMP_TIMESTAMP_EXTENDED=" + TIMESTAMP_TIMESTAMP_EXTENDED +
                ", STREAM_ID=" + STREAM_ID +
                ", reserved=" + reserved +
                ", filter=" + filter +
                ", tagType=" + tagType +
                ", dataSize=" + dataSize +
                ", timeStamp=" + timeStamp +
                ", timeStampExtended=" + timeStampExtended +
                ", streamId=" + streamId +
                ", flvBinaryHeader=" + flvBinaryHeader +
                '}';
    }
}
