package com.spark.live.sdk.network.rtmp.chunk;

import com.spark.live.sdk.network.rtmp.IBinary;
import com.spark.live.sdk.network.rtmp.ICreator;
import com.spark.live.sdk.util.BinaryUtil;
import com.spark.live.sdk.util.LogUtil;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * ChunkHeader 结构
 * 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |                  timestamp                    |message length |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |       message length (cont)   |message type id| msg stream id |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |          message stream id (cont)             |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * Created by devzhaoyou on 8/15/16.
 */

public class ChunkHeader implements IBinary {

    public static final int TIMESTAMP_MASK = 0xFFFFFF00;
    public static final int MESSAGE_LENGTH_MASK = 0xFFFFFF00;
    public static final int MESSAGE_TYPE_ID_MASK = 0x000000FF;
    public static final int MESSAGE_STREAM_ID_MASK = 0xFFFFFFFF;
    public static final int EXTENDS_TIME_FLAG = 0x00FFFFFF;

    public static final int TIMESTAMP_MIN_VALUE = 0;
    public static final int TIMESTAMP_MAX_VALUE = 16777215;
    public static final int MESSAGE_LENGTH_MIN_VALUE = 0;
    public static final int MESSAGE_LENGTH_MAX_VALUE = 16777215;
    public static final int MESSAGE_TYPE_ID_MIN_VALUE = 0;
    public static final int MESSAGE_TYPE_ID_MAX_VALUE = 255;

    private BasicHeader basicHeader;

    /**时间戳 3字节*/
    private int timestamp3Bytes;
    /**消息长度 3字节*/
    private int messageLength3Bytes;
    /**消息类型 1字节*/
    private int messageTypeId1Byte;
    /**MessageStreamId 4字节*/
    private int messageStreamId4Bytes;

    private int extendedTimestamp;

    private byte[] binaryData;

    public ChunkHeader(int timeStamp3Bytes, int messageLength3Bytes,
                       int messageTypeId1Byte, int messageStreamId4Bytes) {
        setTimestamp(timeStamp3Bytes);
        setMessageLength(messageLength3Bytes);
        setMessageTypeId(messageTypeId1Byte);
        setMessageStreamId(messageStreamId4Bytes);
    }

    public ChunkHeader(int timeStamp3Bytes, int messageLength3Bytes,
                       int messageTypeId1Byte) {
        this(timeStamp3Bytes, messageLength3Bytes, messageTypeId1Byte, 0);
    }

    public ChunkHeader(int timeStamp3Bytes) {
        this(timeStamp3Bytes, 0, 0);
    }


    public ChunkHeader(BasicHeader basicHeader) {
        this.basicHeader = basicHeader;
    }

    public ChunkHeader() {

    }


    public static final ICreator<ChunkHeader> Creator = new ICreator.Stub<ChunkHeader>() {
        @Override
        public ChunkHeader create(InputStream in) {

            if (in != null) {
                try {
                    ChunkHeader chunkHeader = new ChunkHeader();
                    BasicHeader basicHeader = BasicHeader.Creator.create(in);
                    chunkHeader.setBasicHeader(basicHeader);
                    byte[] result = new byte[11];
                    ByteBuffer buffer;
                    switch (basicHeader.getFmt()) {
                        case BasicHeader.FMT_TYPE_0:
                            read(in, result, 0, 11);
                            buffer = ByteBuffer.wrap(result);
                            buffer.limit(11);
                            createType0(buffer, chunkHeader);
                            break;
                        case BasicHeader.FMT_TYPE_1:
                            read(in, result, 0, 7);
                            buffer = ByteBuffer.wrap(result);
                            buffer.limit(7);
                            createType1(buffer, chunkHeader);
                            break;
                        case BasicHeader.FMT_TYPE_2:
                            read(in, result, 0, 3);
                            buffer = ByteBuffer.wrap(result);
                            buffer.limit(3);
                            buffer.rewind();
                            createType2(buffer, chunkHeader);
                            break;
                        case BasicHeader.FMT_TYPE_3:
                            break;
                    }

                    if (chunkHeader.getTimestamp() == ChunkHeader.EXTENDS_TIME_FLAG) {
                        byte[] extendsTimeArray = new byte[4];
                        read(in, extendsTimeArray, 0, 4);
                        buffer = ByteBuffer.wrap(extendsTimeArray);
                        chunkHeader.setTimestamp(buffer.getInt());
                    }
                    return chunkHeader;

                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else {
                LogUtil.e("Error: ChunkHeader Creator input stream is null!");
                return null;
            }
            return null;
        }

        private void createType0(ByteBuffer buffer, ChunkHeader chunkHeader) {
            createType1(buffer, chunkHeader);
            int streamID = buffer.getInt();
            chunkHeader.setMessageStreamId(streamID);
        }

        private void createType1(ByteBuffer buffer, ChunkHeader chunkHeader) {
            createType2(buffer, chunkHeader);
            byte[] lenArray = new byte[3];
            buffer.get(lenArray);
            int length = 0;
            length |= (lenArray[0] & 0x000000FF) << 16;
            length |= (lenArray[1] & 0x000000FF) << 8;
            length |= (lenArray[2] & 0x000000FF);
            chunkHeader.setMessageLength(length);

            byte type = buffer.get();
            chunkHeader.setMessageTypeId(type);


        }

        private void createType2(ByteBuffer buffer, ChunkHeader chunkHeader) {
            byte[] timeArray = new byte[3];
            buffer.get(timeArray);
            int time = 0;
            time |= (timeArray[0] & 0x000000FF) << 16;
            time |= (timeArray[1] & 0x000000FF) << 8;
            time |= timeArray[2] & 0x000000FF;
            if (time != 0x00FFFFFF) {
                chunkHeader.setTimestamp(time);
            }
        }

    };

    @Override
    public String toString() {
        return "ChunkHeader{" +
                "basicHeader=" + basicHeader.toString() +
                ", timestamp3Bytes=" + timestamp3Bytes +
                ", messageLength3Bytes=" + messageLength3Bytes +
                ", messageTypeId1Byte=" + messageTypeId1Byte +
                ", messageStreamId4Bytes=" + messageStreamId4Bytes +
                ", extendedTimestamp=" + extendedTimestamp +
                ", binaryData=" + (BinaryUtil.printByteToHex(toBinary())) +
                '}';
    }

    @Override
    public byte[] toBinary() {


        if (binaryData == null) {
            byte[] result = null;
            byte[] timeArray;
            byte[] extendsArray = null;
            byte[] lenMsgTypeIdArray;
            int shiftResult = 0;
            switch (basicHeader.getFmt()) {
                case BasicHeader.FMT_TYPE_0:
                    result = new byte[11];
                    if (timestamp3Bytes == EXTENDS_TIME_FLAG) {
                        extendsArray = BinaryUtil.getBytesFromIntValue(extendedTimestamp, 4);
                    }
                    timeArray = BinaryUtil.getBytesFromIntValue(timestamp3Bytes, 3);
                    shiftResult |= (MESSAGE_LENGTH_MASK & messageLength3Bytes << 8);
                    shiftResult |= (MESSAGE_TYPE_ID_MASK & messageTypeId1Byte);
                    lenMsgTypeIdArray = BinaryUtil.getBytesFromIntValue(shiftResult, 4);
                    byte[] msgStreamIdArray = BinaryUtil.getBytesFromIntValue(Integer.reverseBytes(messageStreamId4Bytes), 4);
                    System.arraycopy(timeArray, 0, result, 0, timeArray.length);
                    System.arraycopy(lenMsgTypeIdArray, 0, result, timeArray.length, lenMsgTypeIdArray.length);
                    System.arraycopy(msgStreamIdArray, 0, result, timeArray.length + lenMsgTypeIdArray.length, msgStreamIdArray.length);
                    break;
                case BasicHeader.FMT_TYPE_1:
                    result = new byte[7];
                    if (timestamp3Bytes == EXTENDS_TIME_FLAG) {
                        extendsArray = BinaryUtil.getBytesFromIntValue(extendedTimestamp, 4);
                    }
                    timeArray = BinaryUtil.getBytesFromIntValue(timestamp3Bytes, 3);
                    shiftResult |= (MESSAGE_LENGTH_MASK & messageLength3Bytes << 8);
                    shiftResult |= (MESSAGE_TYPE_ID_MASK & messageTypeId1Byte);
                    lenMsgTypeIdArray = BinaryUtil.getBytesFromIntValue(shiftResult, 4);
                    System.arraycopy(timeArray, 0, result, 0, timeArray.length);
                    System.arraycopy(lenMsgTypeIdArray, 0, result, timeArray.length, lenMsgTypeIdArray.length);
                    break;
                case BasicHeader.FMT_TYPE_2:
                    if (timestamp3Bytes == EXTENDS_TIME_FLAG) {
                        extendsArray = BinaryUtil.getBytesFromIntValue(extendedTimestamp, 4);
                    }
                    result = BinaryUtil.getBytesFromIntValue(timestamp3Bytes, 3);
                    break;
                case BasicHeader.FMT_TYPE_3:
                    result = null;
                    break;
            }
            byte[] basicArray = basicHeader.toBinary();
            if (result != null) {
                if (extendsArray != null) {
                    binaryData = new byte[basicArray.length + result.length + 4];
                    System.arraycopy(basicArray, 0, binaryData, 0, basicArray.length);
                    System.arraycopy(result, 0, binaryData, basicArray.length, result.length);
                    System.arraycopy(extendsArray, 0, binaryData, basicArray.length + result.length, 4);
                } else {
                    binaryData = new byte[basicArray.length + result.length];
                    System.arraycopy(basicArray, 0, binaryData, 0, basicArray.length);
                    System.arraycopy(result, 0, binaryData, basicArray.length, result.length);
                }
            } else {
                binaryData = basicArray;
            }
        }

        return binaryData;
    }


    public void setTimestamp(int timestamp) {
        if (timestamp < TIMESTAMP_MIN_VALUE) {
            LogUtil.e("ChunkHeader: The timeStamp3Bytes must be greater than TIMESTAMP_MIN_VALUE");
            return;
        }

        if (timestamp > TIMESTAMP_MAX_VALUE) {
            extendedTimestamp = timestamp;
            timestamp3Bytes = EXTENDS_TIME_FLAG;
        } else {
            this.timestamp3Bytes = timestamp;
        }

    }

    public void setMessageLength(int messageLength3Bytes) {
        if (messageLength3Bytes < MESSAGE_LENGTH_MIN_VALUE ||
                messageLength3Bytes > MESSAGE_LENGTH_MAX_VALUE) {
            LogUtil.e("ChunkHeader: The messageLength3Bytes must be between MESSAGE_LENGTH_MIN_VALUE " +
                    "and MESSAGE_LENGTH_MAX_VALUE current is: " + messageLength3Bytes);
            return;
        }
        this.messageLength3Bytes = messageLength3Bytes;
    }

    public void setMessageTypeId(int messageTypeId1Byte) {
        if (messageTypeId1Byte < MESSAGE_TYPE_ID_MIN_VALUE ||
                messageTypeId1Byte > MESSAGE_TYPE_ID_MAX_VALUE) {
            LogUtil.e("ChunkHeader: The messageTypeId1Byte must be between MESSAGE_TYPE_ID_MIN_VALUE and MESSAGE_TYPE_ID_MAX_VALUE");
            return;
        }
        this.messageTypeId1Byte = messageTypeId1Byte;
    }

    public void setMessageStreamId(int messageStreamId4Bytes) {
        this.messageStreamId4Bytes = messageStreamId4Bytes;
    }

    public void setBasicHeader(BasicHeader basicHeader) {
        this.basicHeader = basicHeader;
    }

    public BasicHeader getBasicHeader() {
        return basicHeader;
    }

    public int getTimestamp() {
        return timestamp3Bytes;
    }

    public int getMessageLength() {
        return messageLength3Bytes;
    }

    public int getMessageTypeId() {
        return messageTypeId1Byte;
    }

    public int getMessageStreamId() {
        return messageStreamId4Bytes;
    }


}
