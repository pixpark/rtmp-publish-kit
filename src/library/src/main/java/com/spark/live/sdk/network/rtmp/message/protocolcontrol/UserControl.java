package com.spark.live.sdk.network.rtmp.message.protocolcontrol;

import com.spark.live.sdk.network.rtmp.message.MessageHeader;
import com.spark.live.sdk.network.rtmp.message.RtmpMessage;
import com.spark.live.sdk.util.BinaryUtil;

import java.util.Arrays;

/**
 *
 * Created by devzhaoyou on 8/16/16.
 */

public class UserControl extends RtmpMessage {

    private short eventType;
    private byte[] eventData;


    public UserControl(MessageHeader header) {
        this.header = header;
    }

    @Override
    public byte[] toBinary() {

        byte[] result = new byte[eventData.length + 2];
        byte[] typeArray = BinaryUtil.getBytesFromIntValue(eventType, 2);
        System.arraycopy(typeArray, 0, result, 0, typeArray.length);
        System.arraycopy(eventData, 0, result, typeArray.length, eventData.length);
        return result;
    }

    public void setEventData(byte[] eventData) {
        this.eventData = eventData;
    }

    public void setEventType(short eventType) {
        this.eventType = eventType;
    }

    public byte[] getEventData() {
        return eventData;
    }

    public short getEventType() {
        return eventType;
    }

    @Override
    public String toString() {
        return "UserControl{" +
                "header=" + header.toString() +
                "eventType=" + eventType +
                ", eventData=" + Arrays.toString(eventData) +
                '}';
    }

    public static class EventType {


        /**
         * The server sends this event to notify the client
         * that a stream has become functional and can be
         * used for communication. By default, this event
         * is sent on ID 0 after the application connect
         * command is successfully received from the
         * client. The event data is 4-byte and represents
         * the stream ID of the stream that became
         * functional.
         */
        public static final int EVENT_TYPE_STREAM_BEGIN = 0;

        /**
         * The server sends this event to notify the client
         * that the playback of data is over as requested
         * on this stream. No more data is sent without
         * issuing additional commands. The client discards
         * the messages received for the stream. The
         * 4 bytes of event data represent the ID of the
         * stream on which playback has ended.
         */
        public static final int EVENT_TYPE_STREAN_EOF = 1;

        /**
         * The server sends this event to notify the client
         * that there is no more data on the stream. If the
         * server does not detect any message for a time
         * period, it can notify the subscribed clients
         * that the stream is dry. The 4 bytes of event
         * data represent the stream ID of the dry stream.
         */
        public static final int EVENT_TYPE_STREAM_DRY = 2;

        /**
         * The client sends this event to inform the server
         * of the buffer size (in milliseconds) that is
         * used to buffer any data coming over a stream.
         * This event is sent before the server starts
         * processing the stream. The first 4 bytes of the
         * event data represent the stream ID and the next
         * 4 bytes represent the buffer length, in
         * milliseconds.
         */
        public static final int EVENT_TYPE_SET_BUFFER_LENGTH = 3;

        /**
         * The server sends this event to notify the client
         * that the stream is a recorded stream. The
         * 4 bytes event data represent the stream ID of
         * the recorded stream.
         */
        public static final int EVENT_TYPE_STREAMS_RECORDED = 4;

        /**
         * The server sends this event to test whether the
         * client is reachable. Event data is a 4-byte
         * timestamp, representing the local server time
         * when the server dispatched the command. The
         * client responds with kMsgPingResponse on
         * receiving kMsgPingRequest.
         */
        public static final int EVENT_TYPE_PING_REQUEST = 6;

        /**
         * The client sends this event to the server in
         * response to the ping request. The event data is
         * a 4-byte timestamp, which was received with the
         * kMsgPingRequest request.
         */
        public static final int EVENT_TYPE_RESPONSE = 7;
    }
}
