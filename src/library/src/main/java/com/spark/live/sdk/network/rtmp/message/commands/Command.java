package com.spark.live.sdk.network.rtmp.message.commands;

import com.spark.live.sdk.network.rtmp.IBinary;
import com.spark.live.sdk.network.rtmp.amf.AMFData;
import com.spark.live.sdk.network.rtmp.amf.AMFNull;
import com.spark.live.sdk.util.LogUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * RTMP command
 * Created by devzhaoyou on 8/26/16.
 */

public class Command implements IBinary {

    private List<AMFData> fields = null;
    protected byte[] binaryData = null;

    public void setCommandField(AMFData field) {
        if (fields == null) {
            fields = new ArrayList<>();
        }

        if (field != null) {
            fields.add(field);
        } else {
            LogUtil.e("CommandMessage setCommandField Error: The field can not be null!");
        }

    }

    public AMFData getField(int index) {
        if (fields != null && index >= 0 && index < fields.size()) {
            return fields.get(index);
        }
        return null;
    }

    @Override
    public String toString() {
        return "Command{" +
                "fields=" + fields.toString() +
                '}';
    }

    @Override
    public byte[] toBinary() {
        if (binaryData == null) {
            if (fields.isEmpty()) {
                binaryData = new AMFNull().toBinary();
            } else {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                for (AMFData data : fields) {
                    try {
                        bos.write(data.toBinary());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                binaryData = bos.toByteArray();
            }

        }

        return binaryData;
    }

    public static final String _RESULT = "_result";
    public static final String _ERROR = "_error";


    public static final String NAME_CONNECT = "connect";
    public static final String NAME_CREATE_STREAM = "createStream";
    public static final String NAME_PLAY = "play";
    public static final String NAME_PLAY2 = "play2";
    public static final String NAME_RELEASE_STREAM = "releaseStream";
    public static final String NAME_DELETE_STREAM = "deleteStream";
    public static final String NAME_RECEIVE_AUDIO = "receiveAudio";
    public static final String NAME_RECEIVE_VIDEO = "receiveVideo";
    public static final String NAME_PUBLISH = "publish";
    public static final String NAME_FC_PUBLISH = "FCPublish";
    public static final String NAME_SEEK = "seek";
    public static final String NAME_PAUSE = "pause";
}
