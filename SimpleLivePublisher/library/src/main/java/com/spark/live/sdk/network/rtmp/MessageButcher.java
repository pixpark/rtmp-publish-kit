package com.spark.live.sdk.network.rtmp;

import com.spark.live.sdk.network.rtmp.chunk.BasicHeader;
import com.spark.live.sdk.network.rtmp.chunk.ChunkHeader;
import com.spark.live.sdk.network.rtmp.chunk.RtmpChunk;
import com.spark.live.sdk.network.rtmp.message.RtmpMessage;
import com.spark.live.sdk.network.rtmp.message.MessageHeader;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;


/**
 * 消息切分
 * Created by devzhaoyou on 8/18/16.
 */

public class MessageButcher implements IKnightSword {

    private static final int DEFAULT_CHUNK_SIZE = 128;

    private static int maxChunkSize = DEFAULT_CHUNK_SIZE;
    private boolean isReset = true;

    private MessageHeader register;

    @Override
    public List<RtmpChunk> hackMessage(RtmpMessage message) {
        List<RtmpChunk> chunks = new ArrayList<>();
        MessageHeader header = message.getHeader();
        /*final int firstChunkType = isReset ? BasicHeader.FMT_TYPE_0 : getFirstChunkType(header, register);
        register = header;*/
        isReset = false;

        byte[] payload = message.toBinary();
        final int length = header.getPayloadLength();


        if (length > maxChunkSize) {
            byte[] chunkPayload = new byte[maxChunkSize];
            ByteBuffer buffer = ByteBuffer.wrap(payload);
            buffer.get(chunkPayload);
            RtmpChunk chunk1 = makeChunk(BasicHeader.FMT_TYPE_0, header, chunkPayload);
            chunks.add(chunk1);
            while (buffer.remaining() > maxChunkSize) {
                chunkPayload = new byte[maxChunkSize];
                buffer.get(chunkPayload);
                RtmpChunk chunk2 = makeChunk(BasicHeader.FMT_TYPE_3, header, chunkPayload);
                chunks.add(chunk2);
            }
            byte[] remained = new byte[buffer.remaining()];
            buffer.get(remained);
            RtmpChunk chunk3 = makeChunk(BasicHeader.FMT_TYPE_3, header, remained);
            chunks.add(chunk3);

        } else {
            RtmpChunk single = makeChunk(BasicHeader.FMT_TYPE_0, header, payload);
            chunks.add(single);
        }

        return chunks;
    }

    /**
     * 生成RtmpChunk
     * @param chunkType chunk类型 FMT_TYPE_0 FMT_TYPE_1 FMT_TYPE_2 FMT_TYPE_3之一
     * @param header 消息头
     * @param payload chunk要负载的数据
     * @return chunk 对象
     */
    private RtmpChunk makeChunk(int chunkType, MessageHeader header, byte[] payload) {
        RtmpChunk chunk;
        /**设置chunk头信息*/
        BasicHeader basicHeader = new BasicHeader(chunkType);
        ChunkHeader chunkHeader = new ChunkHeader(basicHeader);
        basicHeader.setChunkStreamId(header.getChunkStreamId());
        switch (chunkType) {
            case BasicHeader.FMT_TYPE_0:
                chunkHeader.setMessageStreamId(header.getStreamId());
                chunkHeader.setMessageLength(header.getPayloadLength());
                chunkHeader.setMessageTypeId(header.getTypeId());
                chunkHeader.setTimestamp(TimeStampHelper.getInstance().createAbsoluteTimestamp());
                break;
            case BasicHeader.FMT_TYPE_1:
                chunkHeader.setMessageLength(header.getPayloadLength());
                chunkHeader.setMessageTypeId(header.getTypeId());
            case BasicHeader.FMT_TYPE_2:
                chunkHeader.setTimestamp(TimeStampHelper.getInstance().createTimestampDelta());
                break;
            case BasicHeader.FMT_TYPE_3:
                break;
        }
        chunk = new RtmpChunk(chunkHeader, payload);
        return chunk;
    }


    private int getFirstChunkType(MessageHeader current, MessageHeader previous) {
        if (previous != null) {
            if (previous.getStreamId() == current.getStreamId()) {

                if (previous.getPayloadLength() == current.getPayloadLength() &&
                        previous.getTypeId() == current.getTypeId()) {
                    if (previous.getTimestamp() == current.getTimestamp()) {
                        return BasicHeader.FMT_TYPE_3;
                    }
                    return BasicHeader.FMT_TYPE_2;
                }

                return BasicHeader.FMT_TYPE_1;
            }
            return BasicHeader.FMT_TYPE_0;
        }

        return BasicHeader.FMT_TYPE_0;
    }

    /**
     * 返回当前chunk最大容量
     * @return chunk最大容量
     */
    public static int getMaxChunkSize() {
        return maxChunkSize;
    }

    /**
     * 设定当前chunk最大容量
     * @param maxChunkSize chunk最大容量
     */
    public static void setMaxChunkSize(int maxChunkSize) {
       MessageButcher.maxChunkSize = maxChunkSize;
    }


    @Override
    public void setReset() {
        this.isReset = true;
    }
}
