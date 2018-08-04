package com.spark.live.sdk.network.rtmp.message;

import com.spark.live.sdk.network.rtmp.IBinary;
import com.spark.live.sdk.util.BinaryUtil;


/**
 * RTMP 消息头结构
 * 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * | Message Type|      Payload length                           |
 * |   (1 byte)  |      (3 bytes)                                |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |                    Timestamp                                |
 * |                    (4 bytes)                                |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |                    Stream ID                  |
 * |                     (3 bytes)                 |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * Created by devzhaoyou on 8/15/16.
 */

public class MessageHeader implements IBinary {

    private int messageTypeId1Byte;
    private int payloadLength3Bytes;
    private int timeStamp;
    private int streamId3Bytes;

    private int chunkStreamId = 3;

    public MessageHeader(int messageTypeId1Byte) {
        this.messageTypeId1Byte = messageTypeId1Byte;
    }

    public MessageHeader() {}

    @Override
    public byte[] toBinary() {
        byte[] typeArray = BinaryUtil.getBytesFromIntValue(messageTypeId1Byte, 1);
        byte[] lengthArray = BinaryUtil.getBytesFromIntValue(payloadLength3Bytes, 3);
        byte[] timeArray = BinaryUtil.getBytesFromIntValue(timeStamp, 4);
        byte[] streamArray = BinaryUtil.getBytesFromIntValue(streamId3Bytes, 3);
        final int typeLen = typeArray.length;
        final int payLen = lengthArray.length;
        final int timeLen = timeArray.length;
        final int streamLen = streamArray.length;
        byte[] result = new byte[typeLen + payLen + timeLen + streamLen];
        System.arraycopy(typeArray, 0, result, 0, typeLen);
        System.arraycopy(lengthArray, 0, result, typeLen, payLen);
        System.arraycopy(timeArray, 0, result, typeLen + payLen, timeLen);
        System.arraycopy(streamArray, 0, result, typeLen + payLen + timeLen, streamLen);
        return result;
    }

    public int getTypeId() {
        return messageTypeId1Byte;
    }

    public int getPayloadLength() {
        return payloadLength3Bytes;
    }

    public int getTimestamp() {
        return timeStamp;
    }

    public int getStreamId() {
        return streamId3Bytes;
    }

    public MessageHeader setMessageTypeId(int messageTypeId1Byte) {
        this.messageTypeId1Byte = messageTypeId1Byte;
        return this;
    }

    public MessageHeader setPayloadLength(int payloadLength3Bytes) {
        this.payloadLength3Bytes = payloadLength3Bytes;
        return this;
    }

    public MessageHeader setTimeStamp(int timeStamp) {
        this.timeStamp = timeStamp;
        return this;
    }

    public MessageHeader setStreamId(int streamId3Bytes) {
        this.streamId3Bytes = streamId3Bytes;
        return this;
    }

    public void setChunkStreamId(int chunkStreamId) {
        this.chunkStreamId = chunkStreamId;
    }

    public int getChunkStreamId() {
        return chunkStreamId;
    }

    @Override
    public String toString() {
        return "MessageHeader{" +
                "messageTypeId1Byte=" + messageTypeId1Byte +
                ", payloadLength3Bytes=" + payloadLength3Bytes +
                ", timeStamp=" + timeStamp +
                ", streamId3Bytes=" + streamId3Bytes +
                '}';
    }

    public static class TYPE_IDS {

        /**PROTOCOL Control Message*/
        public static final int TYPE_ID_SET_CHUNK_SIZE = 1;
        public static final int TYPE_ID_ABORT_MESSAGE = 2;
        public static final int TYPE_ID_ACKNOWLEDGEMENT = 3;
        public static final int TYPE_ID_USER_CONTROL_MESSAGE = 4;
        public static final int TYPE_ID_WINDOW_ACKNOWLEDGEMENT_SIZE = 5;
        public static final int TYPE_ID_SET_PEER_BANDWIDTH = 6;

        /**for server use*/
        public static final int TYPE_ID_EDGE_ORIGIN_SERVER = 7;

        /**AV Message*/
        public static final int TYPE_ID_AUDIO_MESSAGE = 8;
        public static final int TYPE_ID_VIDEO_MESSAGE = 9;

        /**OTHER*/
        public static final int TYPE_ID_DATA_MESSAGE_AMF3 = 15;
        public static final int TYPE_ID_SHARED_MESSAGE_AMF3 = 16;
        public static final int TYPE_ID_COMMAND_MESSAGE_AMF3 = 17;
        public static final int TYPE_ID_DATA_MESSAGE_AMF0 = 18;
        public static final int TYPE_ID_SHARED_MESSAGE_AMF0 = 19;
        public static final int TYPE_ID_COMMAND_MESSAGE_AMF0 = 20;
        public static final int TYPE_ID_AGGREGATE_MESSAGE = 22;

    }
}
