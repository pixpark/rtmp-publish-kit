package com.spark.live.sdk.network.rtmp.message.commands;

import com.spark.live.sdk.network.rtmp.IBinary;
import com.spark.live.sdk.network.rtmp.amf.AMFBoolean;
import com.spark.live.sdk.network.rtmp.amf.AMFData;
import com.spark.live.sdk.network.rtmp.amf.AMFNumber;
import com.spark.live.sdk.network.rtmp.amf.AMFObject;
import com.spark.live.sdk.network.rtmp.amf.AMFString;

/**
 * A semi-command implementer
 * Created by devzhaoyou on 8/16/16.
 */

public class Connect extends Command implements IBinary {
    private String app;

    private String tcUrl;

    private AMFString name;

    private AMFNumber transactionId;

    private AMFObject properties;

    private AMFObject optionalArgs;


    public Connect(double transId, String app, String tcUrl) {
        this.app = app;
        this.tcUrl = tcUrl;

        name = new AMFString(NAME_CONNECT);
        transactionId = new AMFNumber(transId);
        properties = new AMFObject();
        optionalArgs = new AMFObject();
        setCommandField(name);
        setCommandField(transactionId);
        setCommandField(properties);
        initProperties();
//        setCommandField(optionalArgs);
    }

    public Connect setName(String name) {
        this.name.setValue(name);
        return this;
    }

    public Connect setTransactionId(Double transactionId) {
        this.transactionId.setValue(transactionId);
        return this;
    }

    public Connect setCommands(String key, AMFData value) {
        properties.setProperty(new AMFString(key), value);
        return this;
    }

    public Connect setOptionalCommands(String key, AMFData value) {
        optionalArgs.setProperty(new AMFString(key), value);
        return this;
    }

    private void initProperties() {
        setCommands(Connect.CommandKeys.KEY_APP_STRING, new AMFString(app))
                .setCommands(Connect.CommandKeys.KEY_FLASHVER_STRING, new AMFString("WIN 15,0,0,239"))
                .setCommands(Connect.CommandKeys.KEY_SWF_URL_STRING, new AMFString(""))
                .setCommands(Connect.CommandKeys.KEY_TC_URL_STRING, new AMFString(tcUrl))
                .setCommands(Connect.CommandKeys.KEY_FPAD_BOOLEAN, new AMFBoolean(AMFBoolean.FALSE))
                .setCommands(Connect.CommandKeys.KEY_CAPABILITIES_NUMBER, new AMFNumber(239d))
                .setCommands(Connect.CommandKeys.KEY_AUDIO_CODECS_NUMBER, new AMFNumber(1024d))
                .setCommands(Connect.CommandKeys.KEY_VIDEO_CODECS_NUMBER, new AMFNumber(128d))
                .setCommands(Connect.CommandKeys.KEY_VIDEO_FUNCTION_NUMBER, new AMFNumber(1d))
                .setCommands(Connect.CommandKeys.KEY_PAGE_URL_STRING, new AMFString(""))
                .setCommands(Connect.CommandKeys.KEY_OBJECT_ENCODING_NUMBER, new AMFNumber(0d));
    }


    public void clear() {
        name = null;
        transactionId = null;
        properties = null;
        binaryData = null;
    }

    @Override
    public String toString() {
        return "ConnectCMD{" +
                "name=" + name.toString() +
                ", transactionId=" + transactionId.toString() +
                ", properties=" + properties.toString() +
                ", optionalArgs=" + optionalArgs.toString() +
                '}';
    }

    /*@Override
    public byte[] toBinary() {
        if (binaryData == null) {
            byte[] nameArray = name.toBinary();
            byte[] transactionArray = transactionId.toBinary();
            byte[] objectArray = properties.toBinary();
            byte[] optionals = optionalArgs.toBinary();
            ByteBuffer buffer = ByteBuffer.allocate(nameArray.length + transactionArray.length
                    + objectArray.length + optionals.length);
            buffer.put(nameArray).put(transactionArray).put(objectArray).put(optionals);
            binaryData = buffer.array();
        }

        return binaryData;
    }*/




    public static final double TRANSACTION_ID_CONNECT = 1d;
    public static final double TRANSACTION_ID_CREATE_STREAM = 2d;

    public static class CommandKeys {

        public static final String KEY_APP_STRING = "app";
        public static final String KEY_FLASHVER_STRING = "flashver";
        public static final String KEY_SWF_URL_STRING = "swfUrl";
        public static final String KEY_TC_URL_STRING = "tcUrl";
        public static final String KEY_FPAD_BOOLEAN = "fpad";
        public static final String KEY_CAPABILITIES_NUMBER = "capabilities";
        public static final String KEY_AUDIO_CODECS_NUMBER = "audioCodecs";
        public static final String KEY_VIDEO_CODECS_NUMBER = "videoCodecs";
        public static final String KEY_VIDEO_FUNCTION_NUMBER = "videoFunction";
        public static final String KEY_PAGE_URL_STRING = "pageUrl";
        public static final String KEY_OBJECT_ENCODING_NUMBER = "objectEncoding";
    }

    public static class OptioalValues {
        /**
         * Raw sound, no compression
         */
        public static final int VALUE_AUDIO_SUPPORT_SND_NONE = 0x00001;
        /**
         * ADPCM compression
         */
        public static final int VALUE_AUDIO_SUPPORT_SND_ADPCM = 0x00002;
        /**
         * mp3 compression
         */
        public static final int VALUE_AUDIO_SUPPORT_SND_MP3 = 0x00004;
        /**
         * Not used
         */
        public static final int VALUE_AUDIO_SUPPORT_SND_INTEL = 0x00008;
        /**
         * Not used
         */
        public static final int VALUE_AUDIO_SUPPORT_SND_UNUSED = 0x00010;
        /**
         * NellyMoser at 8-kHzcompression
         */
        public static final int VALUE_AUDIO_SUPPORT_SND_NELLY8 = 0x00020;
        /**
         * NellyMoser compression (5, 11, 22, and 44 kHz)
         */
        public static final int VALUE_AUDIO_SUPPORT_SND_NELLY = 0x00040;
        /**
         * G711A sound compression(Flash Media Server only)
         */
        public static final int VALUE_AUDIO_SUPPORT_SND_G711A = 0x00080;
        /**
         * G711U sound compression (Flash Media Server only)
         */
        public static final int VALUE_AUDIO_SUPPORT_SND_G711U = 0x0100;
        /**
         * NellyMouser at 16-kHz compression
         */
        public static final int VALUE_AUDIO_SUPPORT_SND_NELLY16 = 0x0200;
        /**
         * Advanced audio coding (AAC) codec
         */
        public static final int VALUE_AUDIO_SUPPORT_SND_AAC = 0x0400;
        /**
         * Speex Audio
         */
        public static final int VALUE_AUDIO_SUPPORT_SND_SPEEX = 0x0800;
        /**
         * All RTMP-supported audio codecs
         */
        public static final int VALUE_AUDIO_SUPPORT_SND_ALL = 0x0FFF;


        public static final int VALUE_VIDEO_SUPPORT_VID_UNUSED = 0x0001;
        public static final int VALUE_VIDEO_SUPPORT_VID_JPEG = 0x0002;
        public static final int VALUE_VIDEO_SUPPORT_VID_SORENSON = 0x0004;
        public static final int VALUE_VIDEO_SUPPORT_VID_HOMEBREW = 0x0008;
        public static final int VALUE_VIDEO_SUPPORT_VID_VP6_ON2 = 0x0010;
        public static final int VALUE_VIDEO_SUPPORT_VID_VP6ALPHA = 0x0020;
        public static final int VALUE_VIDEO_SUPPORT_VID_HOMEBREWV = 0x0040;
        public static final int VALUE_VIDEO_SUPPORT_VID_H264 = 0x0080;
        public static final int VALUE_VIDEO_SUPPORT_VID_ALL = 0x00FF;

        public static final int VALUE_VIDEO_SUPPORT_VID_CLIENT_SEEK = 1;

        public static final int VALUE_OBJECT_ENCODING_PROPERTY_KAMF0 = 0;
        public static final int VALUE_OBJECT_ENCODING_PROPERTY_KAMF3 = 3;

        public static final int TRANSACTION_ID_CONNECT = 1;
    }

}
