package com.spark.live.sdk.network.rtmp.chunk;

import com.spark.live.sdk.network.rtmp.IBinary;
import com.spark.live.sdk.network.rtmp.ICreator;
import com.spark.live.sdk.util.BinaryUtil;
import com.spark.live.sdk.util.LogUtil;

import java.io.IOException;
import java.io.InputStream;

/**
 * BasicHeader 结构
 * 0 1 2 3 4 5 6 7
 * +-+-+-+-+-+-+-+-+
 * |fmt|    cs id  |
 * +-+-+-+-+-+-+-+-+
 * <p>
 * 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |fmt|     0     |    cs id - 64 |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * <p>
 * 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |fmt|      1    |        cs id - 64             |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * Created by devzhaoyou on 8/15/16.
 */

public class BasicHeader implements IBinary {

    public static final int FMT_TYPE_MIN_VALUE = 0;
    public static final int FMT_TYPE_0 = 0;
    public static final int FMT_TYPE_1 = 1;
    public static final int FMT_TYPE_2 = 2;
    public static final int FMT_TYPE_3 = 3;
    public static final int FMT_TYPE_MAX_VALUE = 3;

    public static final int STREAM_ID_MIN_VALUE = 0;
    public static final int STREAM_ID_3 = 3;
    public static final int STREAM_ID_4 = 4;
    public static final int STREAM_ID_5 = 5;
    public static final int STREAM_ID_MAX_VALUE = 65599;

    public static final int REAL_LENGTH_MIN_VALUE = 1;
    public static final int REAL_LENGTH_1_BYTE = 1;
    public static final int REAL_LENGTH_2_BYTES = 2;
    public static final int REAL_LENGTH_3_BYTES = 3;
    public static final int REAL_LENGTH_MAX_VALUE = 3;

    public static final int FMT_ONE_BYTE_MASK = 0x000000C0;
    public static final int FMT_TWO_BYTES_MASK = 0x0000C000;
    public static final int FMT_THREE_BYTES_MASK = 0x00C00000;

    public static final int STREAM_ID_ONE_BYTE_MASK = 0x0000003F;
    public static final int STREAM_ID_TWO_BYTES_MASK = 0x000000FF;
    public static final int STREAM_ID_THREE_BYTES_MASK = 0x003FFFFF;

    public static final int STREAM_ID_TYPE_0 = 0x00000000;
    public static final int STREAM_ID_TYPE_1 = 0x0000003F;
    public static final int STREAM_ID_TYPE_2 = 0x00000002;

    public static final int CONTROL_CHUNK_STREAM_ID = 2;

    private int fmt;
    private int chunkStreamId;

    private int realLengthInBytes;

    private byte[] binaryData;

    public BasicHeader(int fmt) {
        this.fmt = fmt;
        setChunkStreamId(STREAM_ID_3);
    }

    public BasicHeader(int fmt, int chunkStreamId) {
        this.fmt = fmt;
        setChunkStreamId(chunkStreamId);
    }

    public BasicHeader() {
    }

    public void setFmt(int fmt) {
        if (fmt < FMT_TYPE_MIN_VALUE ||
                fmt > FMT_TYPE_MAX_VALUE) {
            LogUtil.e("The fmt must be between FMT_TYPE_MIN_VALUE and FMT_TYPE_MAX_VALUE");
            return;
        }
        this.fmt = fmt;
    }

    public int getFmt() {
        return fmt;
    }

    public void setChunkStreamId(int chunkStreamId) {

        if (chunkStreamId < STREAM_ID_MIN_VALUE ||
                chunkStreamId > STREAM_ID_MAX_VALUE) {
            LogUtil.e("The chunkStreamId must be between STREAM_ID_MIN_VALUE and STREAM_ID_MAX_VALUE");
            return;
        }

        if (chunkStreamId <= 63) {
            realLengthInBytes = REAL_LENGTH_1_BYTE;
            this.chunkStreamId = chunkStreamId & STREAM_ID_ONE_BYTE_MASK;
        } else if (chunkStreamId < 319) {
            realLengthInBytes = REAL_LENGTH_2_BYTES;
            this.chunkStreamId = chunkStreamId & STREAM_ID_TWO_BYTES_MASK;
        } else if (chunkStreamId < 65599) {
            realLengthInBytes = REAL_LENGTH_3_BYTES;
            this.chunkStreamId = chunkStreamId | 0x003F0000 & STREAM_ID_THREE_BYTES_MASK;
        }
    }

    public void setRealLengthInBytes(int realLengthInBytes) {
        if (realLengthInBytes < REAL_LENGTH_MIN_VALUE || realLengthInBytes > REAL_LENGTH_MAX_VALUE) {
            LogUtil.e("BasicHeader: realLengthInBytes must be between REAL_LENGTH_MIN_VALUE " +
                    "and REAL_LENGTH_MAX_VALUE");
            return;
        }
        this.realLengthInBytes = realLengthInBytes;
    }

    public int getRealLengthInBytes() {
        return realLengthInBytes;
    }

    public int getChunkStreamId() {
        return chunkStreamId;
    }

    @Override
    public byte[] toBinary() {
        if (binaryData == null) {
            int shiftResult = 0;
            switch (realLengthInBytes) {
                case REAL_LENGTH_1_BYTE:
                    shiftResult |= (FMT_ONE_BYTE_MASK & fmt << 6);
                    shiftResult |= (STREAM_ID_ONE_BYTE_MASK & chunkStreamId);
                    binaryData = BinaryUtil.getBytesFromIntValue(shiftResult, 1);
                    break;
                case REAL_LENGTH_2_BYTES:
                    shiftResult |= (FMT_TWO_BYTES_MASK & fmt << 14);
                    shiftResult |= (STREAM_ID_TWO_BYTES_MASK & chunkStreamId);
                    binaryData = BinaryUtil.getBytesFromIntValue(shiftResult, 2);
                    break;
                case REAL_LENGTH_3_BYTES:
                    shiftResult |= (FMT_THREE_BYTES_MASK & fmt << 22);
                    shiftResult |= (STREAM_ID_THREE_BYTES_MASK & chunkStreamId);
                    binaryData = BinaryUtil.getBytesFromIntValue(shiftResult, 3);
                    break;
            }


        }

        return binaryData;
    }

    public static final ICreator<BasicHeader> Creator = new ICreator.Stub<BasicHeader>() {
        @Override
        public BasicHeader create(InputStream in) {
            BasicHeader basicHeader = new BasicHeader();
            if (in != null) {
                try {
                    byte[] result = new byte[3];
                    read(in, result, 0, 1);
                    byte first = result[0];
                    int streamType = first & STREAM_ID_ONE_BYTE_MASK;
                    basicHeader.setFmt((first & FMT_ONE_BYTE_MASK)>>>6);
                    switch (streamType) {
                        case STREAM_ID_TYPE_0:
                            read(in, result, 1, 1);
                            basicHeader.setChunkStreamId((result[1] & 0x000000FF) + 64);
                            basicHeader.setRealLengthInBytes(REAL_LENGTH_2_BYTES);
                            break;
                        case STREAM_ID_TYPE_1:
                            read(in, result, 1, 2);
                            basicHeader.setChunkStreamId((result[1] & 0x000000FF) + 64
                                    + (result[2] & 0x000000FF) * 256);
                            basicHeader.setRealLengthInBytes(REAL_LENGTH_3_BYTES);
                            break;
                        default:
                            basicHeader.setRealLengthInBytes(REAL_LENGTH_1_BYTE);
                            basicHeader.setChunkStreamId(streamType);
                            break;
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else {
                LogUtil.e("Error the input stream is null!!");
                return null;
            }

            return basicHeader;
        }


    };

    @Override
    public String toString() {
        return "BasicHeader{" +
                "fmt=" + fmt +
                ", chunkStreamId=" + chunkStreamId +
                ", realLengthInBytes=" + realLengthInBytes +
                ", binaryData=" + BinaryUtil.printByteToHex(toBinary()) +
                '}';
    }


}
