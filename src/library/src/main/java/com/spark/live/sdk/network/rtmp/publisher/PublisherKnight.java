package com.spark.live.sdk.network.rtmp.publisher;

import com.spark.live.sdk.network.rtmp.ChunkEngineer;
import com.spark.live.sdk.network.rtmp.IAssembleLine;
import com.spark.live.sdk.network.rtmp.MessageButcher;
import com.spark.live.sdk.network.rtmp.RTMPKnight;
import com.spark.live.sdk.network.rtmp.TimeStampHelper;
import com.spark.live.sdk.network.rtmp.amf.AMFData;
import com.spark.live.sdk.network.rtmp.amf.AMFNumber;
import com.spark.live.sdk.network.rtmp.amf.AMFString;
import com.spark.live.sdk.network.rtmp.cache.AVCache;
import com.spark.live.sdk.network.rtmp.chunk.RtmpChunk;
import com.spark.live.sdk.network.rtmp.message.CommandMessage;
import com.spark.live.sdk.network.rtmp.message.MessageHeader;
import com.spark.live.sdk.network.rtmp.message.RtmpMessage;
import com.spark.live.sdk.network.rtmp.message.commands.Command;
import com.spark.live.sdk.network.rtmp.message.commands.Connect;
import com.spark.live.sdk.network.rtmp.message.commands.CreateStream;
import com.spark.live.sdk.network.rtmp.message.commands.Publish;
import com.spark.live.sdk.util.LogUtil;

import java.io.IOException;
import java.util.List;

/**
 * Stream Publisher
 * Created by devzhaoyou on 8/17/16.
 */

public class PublisherKnight extends RTMPKnight implements IPublisher {

    private static int CONNECT_COMMAND = -1;
    private static int CREATE_STREAM_COMMAND = -2;
    private static int PUBLISH_COMMAND = -3;
    private static int RELEASE_STREAM_COMMAND = -4;
    private static int FC_PUBLISH_COMMAND = -5;

    private IAssembleLine assembleWorker;
    private int transactionId = 1;
    private int serverReturnStreamId = 0;
    private AVCache cache;
    private IPublisherCallback callback;

    PublisherKnight(String rtmpURL, IPublisherCallback callback) {
        super(rtmpURL, callback);
        this.callback = callback;
        cache = new AVCache();
    }

    @Override
    protected boolean init() {
        dragonSword = new MessageButcher();
        assembleWorker = new ChunkEngineer(envoyIn, 128);
        return true;
    }

    @Override
    public boolean rtmpConnect() throws IOException {
        /**初始化建立连接命令消息*/
        String tcUrl = "rtmp://" + host + "/" + app;
        CONNECT_COMMAND = transactionId++;
        Connect connect = new Connect(CONNECT_COMMAND, app, tcUrl);
        MessageHeader header = new MessageHeader();
        header.setStreamId(0);
        header.setMessageTypeId(MessageHeader.TYPE_IDS.TYPE_ID_COMMAND_MESSAGE_AMF0);
        header.setPayloadLength(connect.toBinary().length);
        header.setTimeStamp(0);
        header.setChunkStreamId(17);
        CommandMessage connectMsg = new CommandMessage(header, connect);

        /**发送连接消息*/
        sendMessage(connectMsg);
        LogUtil.i("send Connect to server!!");
        boolean result = waitResult(5000, CONNECT_COMMAND);
        if (result) {
            LogUtil.i("PublisherKnight: RTMP connect success!");
            if (callback != null) {
                callback.onConnect();
            }
        } else {
            LogUtil.e("PublisherKnight: RTMP connect failed!");
            if (callback != null) {
                callback.onError("PublisherKnight: RTMP connect failed!");
            }
        }
        return result;
    }

    @Override
    public boolean createStream() throws IOException {

        /**初始化创建流命令消息*/
        CREATE_STREAM_COMMAND = transactionId++;
        Command createStream = new CreateStream(CREATE_STREAM_COMMAND);
        MessageHeader header = new MessageHeader();
        header.setStreamId(0).setTimeStamp(0)
                .setPayloadLength(createStream.toBinary().length)
                .setMessageTypeId(MessageHeader.TYPE_IDS.TYPE_ID_COMMAND_MESSAGE_AMF0)
                .setChunkStreamId(7);
        CommandMessage createMsg = new CommandMessage(header, createStream);
        /**发送创建流消息*/
        sendMessage(createMsg);
        LogUtil.i("Send CreateStream message to sever...");
        boolean result = waitResult(5000L, CREATE_STREAM_COMMAND);
        if (result) {
            LogUtil.i("PublisherKnight: Rtmp createStream success!");
            if (callback != null) {
                callback.onCreateStream();
            }
        } else {
            LogUtil.e("PublisherKnight: Rtmp createStream failed!");
            if (callback != null) {
                callback.onError("PublisherKnight: Rtmp createStream failed!");
            }
        }
        return result;
    }

    @Override
    public boolean publish(String type) throws IOException {
        PUBLISH_COMMAND = transactionId++;
        Command publish = new Publish(PUBLISH_COMMAND, stream, type);
        MessageHeader header = new MessageHeader();
        header.setMessageTypeId(MessageHeader.TYPE_IDS.TYPE_ID_COMMAND_MESSAGE_AMF0);
        header.setPayloadLength(publish.toBinary().length);
        header.setTimeStamp(TimeStampHelper.getInstance().createAbsoluteTimestamp());
        header.setStreamId(0);
        header.setChunkStreamId(9);
        CommandMessage publishMsg = new CommandMessage(header, publish);

        /**发送开始推流消息*/
        sendMessage(publishMsg);
        LogUtil.i("PublisherKnight: Send Publish message to sever...");
        dragonSword.setReset();
        LogUtil.i("PublisherKnight: Send Publish message to sever success!");
        if (callback != null) {
            callback.onSendPublish();
        }
        return true;
    }

    @Override
    public void sendMessage(RtmpMessage message) throws IOException {
        message.getHeader().setStreamId(serverReturnStreamId);
        List<RtmpChunk> chunks = dragonSword.hackMessage(message);
        for (RtmpChunk chunk : chunks) {
            envoyOut.write(chunk.toBinary());
        }
    }

    @Override
    public void close() {
        if (envoy != null && envoy.isConnected()) {
            try {
                envoyOut.flush();
                envoyIn.close();
                envoyOut.close();
                envoy.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                envoy = null;
                envoyIn = null;
                envoyOut = null;
                LogUtil.i("PublisherKnight: Killed envoy!");
            }
        }
    }

    /**
     * 等待命令消息的返回结果
     *
     * @param timeout 超时时间
     * @return true 如果返回 _result false 如果返回 _error 或超时
     */
    private boolean waitResult(long timeout, int transactionId) {
        long start = System.currentTimeMillis();
        do {
            RtmpMessage result = assembleWorker.assembleChunk();
            LogUtil.i("PublisherKnight: Receive: " + (result == null ? "" : result.toString()));
            if (result instanceof CommandMessage) {
                Command command = ((CommandMessage) result).getCommand();
                AMFData field = command.getField(0);
                if (field != null && field instanceof AMFString) {
                    String resp = ((AMFString) field).getValue();
                    if (resp.equals(Connect._RESULT)) {
                        if (transactionId == CREATE_STREAM_COMMAND) {
                            AMFData streamId = command.getField(3);
                            if (streamId != null && streamId instanceof AMFNumber) {
                                AMFNumber streamId_= (AMFNumber)streamId;
                                serverReturnStreamId = (int) streamId_.getValue();
                                LogUtil.i("PublisherKnight: Get server response stream id: " + serverReturnStreamId);
                                return true;
                            } else {
                                return false;
                            }
                        }
                        LogUtil.i("PublisherKnight: Received _result from server...");
                        return true;
                    } else if (resp.equals(Connect._ERROR)) {
                        LogUtil.i("PublisherKnight: Received _error from server...");
                        return false;
                    }
                }
            }
        } while (System.currentTimeMillis() - start < timeout);
        return false;
    }
}
