package com.spark.live.sdk.network.rtmp.amf;

/**
 * amf-packet = version header-count *(header-type) message-count *(message-type)
 * Created by devzhaoyou on 8/16/16.
 */

public class AMFPacket {

    /**
     * The first two bytes of an AMF packet specify the version of AMF used to encode value
     * types. The general structure of an AMF packet is always formatted in AMF 0; however,
     * header values and message body values may be encoded in another AMF version, such as
     * AMF 3.
     */
    private short version;

    private short headerCount;

    private short messageCount;


    private class HeaderType {

        /**
         * A header's name typically identifies a remote operation or method to be invoked by this
         * context header. If a method is specified, it should conform to URI formatting styles using
         * a forward slash '/' to delimit object and/or directory paths. When the header is bound for
         * the Flash Player, it should target a well known method name on the NetConnection
         * instance's client.
         * header-name = UTF-8
         */
        AMFString headerName;
        short mustUnderstand;
        int headerLength;

    }

    //TODO...
}
