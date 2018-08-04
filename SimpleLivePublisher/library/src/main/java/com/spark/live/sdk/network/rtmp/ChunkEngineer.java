package com.spark.live.sdk.network.rtmp;

import com.spark.live.sdk.network.rtmp.amf.AMFBoolean;
import com.spark.live.sdk.network.rtmp.amf.AMFData;
import com.spark.live.sdk.network.rtmp.amf.AMFDate;
import com.spark.live.sdk.network.rtmp.amf.AMFECMAArray;
import com.spark.live.sdk.network.rtmp.amf.AMFLongString;
import com.spark.live.sdk.network.rtmp.amf.AMFNull;
import com.spark.live.sdk.network.rtmp.amf.AMFNumber;
import com.spark.live.sdk.network.rtmp.amf.AMFObject;
import com.spark.live.sdk.network.rtmp.amf.AMFReference;
import com.spark.live.sdk.network.rtmp.amf.AMFStrictArray;
import com.spark.live.sdk.network.rtmp.amf.AMFString;
import com.spark.live.sdk.network.rtmp.amf.AMFTypedObject;
import com.spark.live.sdk.network.rtmp.amf.AMFUndefined;
import com.spark.live.sdk.network.rtmp.chunk.BasicHeader;
import com.spark.live.sdk.network.rtmp.chunk.ChunkHeader;
import com.spark.live.sdk.network.rtmp.message.RtmpMessage;
import com.spark.live.sdk.network.rtmp.message.CommandMessage;
import com.spark.live.sdk.network.rtmp.message.MessageHeader;
import com.spark.live.sdk.network.rtmp.message.commands.Command;
import com.spark.live.sdk.network.rtmp.message.protocolcontrol.AbortMessage;
import com.spark.live.sdk.network.rtmp.message.protocolcontrol.Acknowledgement;
import com.spark.live.sdk.network.rtmp.message.protocolcontrol.SetChunkSize;
import com.spark.live.sdk.network.rtmp.message.protocolcontrol.SetPeerBandwidth;
import com.spark.live.sdk.network.rtmp.message.protocolcontrol.UserControl;
import com.spark.live.sdk.network.rtmp.message.protocolcontrol.WindowAckSize;
import com.spark.live.sdk.util.LogUtil;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * RtmpMessage组装生产线
 * Created by devzhaoyou on 8/25/16.
 */

public class ChunkEngineer implements IAssembleLine {

    private static int chunkSize;
    private InputStream in;


    public ChunkEngineer(InputStream in, int defaultChunkSize) {
        this.in = in;
        chunkSize = defaultChunkSize;
    }

    @Override
    public RtmpMessage assembleChunk() {
        ChunkHeader chunkHeader = ChunkHeader.Creator.create(in);
        RtmpMessage message = createMessage(chunkHeader);
        byte[] binary = message.getPayload();
        final int binarySize = binary.length;
        try {
            if (binarySize < chunkSize) {
                ICreator.Stub.read(in, binary, 0, binarySize);
            } else {
                ChunkHeader register = chunkHeader;
                int remained = binarySize;
                int dstOffset = 0;
                while (remained > 0) {
                    int needRead = remained > chunkSize ? chunkSize : remained;
                    ICreator.Stub.read(in, binary, dstOffset, needRead);
                    remained -= needRead;
                    dstOffset += needRead;
                    if (remained > 0) {
                        chunkHeader = ChunkHeader.Creator.create(in);
                        LogUtil.i("register: " + register.toString());
                        LogUtil.i("current: " + chunkHeader.toString());
                        if (!messageChunksCheck(chunkHeader, register)) {
                            LogUtil.e("ChunkEngineer assemble message Error: The same message's chunk header mismatch!!");
                            return null;
                        }
                        register = chunkHeader;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return parseMessage(message);
    }

    /**
     * 根据chunk头创建一个空消息
     * @param chunkHeader chunk 头
     * @return RtmpMessage
     */
    private RtmpMessage createMessage(ChunkHeader chunkHeader) {

        MessageHeader msgHeader = new MessageHeader();
        msgHeader.setTimeStamp(chunkHeader.getTimestamp());
        msgHeader.setPayloadLength(chunkHeader.getMessageLength());
        msgHeader.setMessageTypeId(chunkHeader.getMessageTypeId());
        msgHeader.setStreamId(chunkHeader.getMessageStreamId());
        RtmpMessage message = new RtmpMessage();
        message.setHeader(msgHeader);
        final int binarySize = chunkHeader.getMessageLength();
        byte[] binary = new byte[binarySize];
        message.setPayload(binary);
        return message;
    }

    /**
     * Parse a RtmpMessage to a specific message type
     * @param source src message
     * @return specific type message
     */
    private RtmpMessage parseMessage(RtmpMessage source) {

        MessageHeader header = source.getHeader();
        byte[] payload = source.getPayload();
        final int msgTypeId = header.getTypeId();
        switch (msgTypeId) {
            case MessageHeader.TYPE_IDS.TYPE_ID_COMMAND_MESSAGE_AMF0:
                try {
                    Command command = new Command();
                    ByteBuffer buffer = ByteBuffer.wrap(payload);
                    while (buffer.hasRemaining()) {
                        byte marker = buffer.get();
                        command.setCommandField(propertyParser(buffer, marker));
                    }
                    return new CommandMessage(header, command);
                } catch (IOException e) {
                    return null;
                }
            case MessageHeader.TYPE_IDS.TYPE_ID_SHARED_MESSAGE_AMF0:

                break;
            case MessageHeader.TYPE_IDS.TYPE_ID_DATA_MESSAGE_AMF0:

                break;
            case MessageHeader.TYPE_IDS.TYPE_ID_AUDIO_MESSAGE:

                break;
            case MessageHeader.TYPE_IDS.TYPE_ID_VIDEO_MESSAGE:

                break;
            case MessageHeader.TYPE_IDS.TYPE_ID_AGGREGATE_MESSAGE:

                break;
            case MessageHeader.TYPE_IDS.TYPE_ID_USER_CONTROL_MESSAGE:
                UserControl userControl = new UserControl(header);
                ByteBuffer buffer1 = ByteBuffer.wrap(payload);
                userControl.setEventType(buffer1.getShort());
                byte[] data = new byte[payload.length - 2];
                buffer1.get(data);
                userControl.setEventData(data);
                userControl.setPayload(payload);
                return userControl;
            case MessageHeader.TYPE_IDS.TYPE_ID_ACKNOWLEDGEMENT:
                Acknowledgement acknowledgement = new Acknowledgement(header);
                acknowledgement.setPayload(payload);
                acknowledgement.setCurrentNumber(ByteBuffer.wrap(payload).getInt());
                return acknowledgement;
            case MessageHeader.TYPE_IDS.TYPE_ID_SET_PEER_BANDWIDTH:
                SetPeerBandwidth setPeerBandwidth = new SetPeerBandwidth(header);
                ByteBuffer buffer2 = ByteBuffer.wrap(payload);
                setPeerBandwidth.setPeerBandwidth(buffer2.getInt());
                setPeerBandwidth.setLimitType(buffer2.get());
                setPeerBandwidth.setPayload(payload);
                return setPeerBandwidth;
            case MessageHeader.TYPE_IDS.TYPE_ID_SET_CHUNK_SIZE:
                chunkSize = ByteBuffer.wrap(payload).getInt();
                SetChunkSize setChunkSize = new SetChunkSize(header);
                setChunkSize.setPayload(payload);
                setChunkSize.setChunkSize(chunkSize);
                return setChunkSize;
            case MessageHeader.TYPE_IDS.TYPE_ID_WINDOW_ACKNOWLEDGEMENT_SIZE:
                WindowAckSize windowAckSize = new WindowAckSize(header);
                windowAckSize.setPayload(payload);
                windowAckSize.setWindowAckSize(ByteBuffer.wrap(payload).getInt());
                return windowAckSize;
            case MessageHeader.TYPE_IDS.TYPE_ID_ABORT_MESSAGE:
                AbortMessage abortMessage = new AbortMessage(header);
                abortMessage.setPayload(payload);
                abortMessage.setChunkStreamId(ByteBuffer.wrap(payload).getInt());
                return abortMessage;
        }

        return null;
    }

    /**
     * Message's chunk check
     * to show weather the two chunk belongs to a same message
     * @param cur current chunk
     * @param pre previous chunk
     * @return true if belongs false otherwise
     */
    private boolean messageChunksCheck(ChunkHeader cur, ChunkHeader pre) {
        BasicHeader curBasic = cur.getBasicHeader();
        boolean isBelongSameMessage = false;
        switch (curBasic.getFmt()) {
            case BasicHeader.FMT_TYPE_0:
                if (cur.getMessageStreamId() !=
                        pre.getMessageStreamId()) {
                    break;
                }
            case BasicHeader.FMT_TYPE_1:
                if (cur.getMessageLength() !=
                        pre.getMessageLength() ||
                        cur.getMessageTypeId() ==
                        pre.getMessageTypeId()) {
                    break;
                }
            case BasicHeader.FMT_TYPE_2:
                if (cur.getTimestamp() !=
                        pre.getTimestamp()) {
                    break;
                }
            case BasicHeader.FMT_TYPE_3:
                isBelongSameMessage = true;
                break;
        }
        return isBelongSameMessage;
    }

    /**
     * parse a AMF Data according the marker
     * @param buffer data
     * @param marker type marker
     * @return AMFData instance
     * @throws IOException
     */
    private AMFData propertyParser(ByteBuffer buffer, int marker) throws IOException {
        switch (marker) {
            case AMFData.NUMBER_MARKER:
                return AMFNumber.Creator.create(buffer);
            case AMFData.BOOLEAN_MARKER:
                return AMFBoolean.Creator.create(buffer);
            case AMFData.STRING_MARKER:
                return AMFString.Creator.create(buffer);
            case AMFData.OBJECT_MARKER:
                return AMFObject.Creator.create(buffer);
            case AMFData.UNDEFINED_MARKER:
                return AMFUndefined.Creator.create(buffer);
            case AMFData.REFERENCE_MARKER:
                return AMFReference.Creator.create(buffer);
            case AMFData.ECMA_ARRAY_MARKER:
                return AMFECMAArray.Creator.create(buffer);
            case AMFData.STRICT_ARRAY_MARKER:
                return AMFStrictArray.Creator.create(buffer);
            case AMFData.NULL_MARKER:
                return AMFNull.Creator.create(buffer);
            case AMFData.DATE_MARKER:
                return AMFDate.Creator.create(buffer);
            case AMFData.LONG_STRING_MARKER:
                return AMFLongString.Creator.create(buffer);
            case AMFData.TYPED_OBJECT_MARKER:
                return AMFTypedObject.Creator.create(buffer);
            default:
                return null;
        }
    }

}
