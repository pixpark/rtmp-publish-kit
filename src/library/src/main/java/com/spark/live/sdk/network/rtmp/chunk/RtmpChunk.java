package com.spark.live.sdk.network.rtmp.chunk;

import com.spark.live.sdk.network.rtmp.IBinary;
import com.spark.live.sdk.network.rtmp.ICreator;
import com.spark.live.sdk.util.BinaryUtil;
import com.spark.live.sdk.util.LogUtil;

import java.io.InputStream;

/**
 * Chunk Format
 * +-------------+----------------+-------------------+--------------+
 * | Basic header|Chunk Msg Header|Extended Time Stamp|  Chunk Data  |
 * +-------------+----------------+-------------------+--------------+
 * Created by devzhaoyou on 8/15/16.
 */

public class RtmpChunk implements IBinary{

    protected ChunkHeader header;

    protected byte[] payload;

    private byte[] binaryData;

    public RtmpChunk() {

    }

    public RtmpChunk(ChunkHeader header, byte[] payload) {
        this.header = header;
        this.payload = payload;
    }

    public ChunkHeader getHeader() {
        return header;
    }

    public void setHeader(ChunkHeader header) {
        this.header = header;
    }

    public void setPayload(byte[] payload) {
        this.payload = payload;
    }

    public byte[] getPayload() {
        return payload;
    }

    @Override
    public byte[] toBinary() {
        if (binaryData == null) {
            byte[] headerArray = header.toBinary();
            binaryData = new byte[headerArray.length + payload.length];
            System.arraycopy(headerArray, 0, binaryData, 0, headerArray.length);
            System.arraycopy(payload, 0, binaryData, headerArray.length, payload.length);
        }

        return binaryData;
    }

    public static final ICreator<RtmpChunk> Creator = new ICreator.Stub<RtmpChunk>() {
        @Override
        public RtmpChunk create(InputStream in) {

            if (in != null) {
                RtmpChunk chunk = new RtmpChunk();
                ChunkHeader chunkHeader = ChunkHeader.Creator.create(in);
                chunk.setHeader(chunkHeader);
            } else {
                LogUtil.e("RtmpChunk Error: The input stream is null!!");
                return null;
            }
            return super.create(in);
        }
    };


    @Override
    public String toString() {
        return "RtmpChunk{" +
                "header=" + header.toString() +
                ", payload=" + (payload == null ? "" : BinaryUtil.printByteToHex(payload)) +
                ", binaryData=" + (BinaryUtil.printByteToHex(toBinary())) +
                '}';
    }
}
