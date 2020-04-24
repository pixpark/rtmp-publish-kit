package com.spark.live.sdk.network.rtmp.message.commands;

import com.spark.live.sdk.network.rtmp.amf.AMFNull;
import com.spark.live.sdk.network.rtmp.amf.AMFNumber;
import com.spark.live.sdk.network.rtmp.amf.AMFString;

/**
 *
 * Created by devzhaoyou on 9/12/16.
 */

public class Publish extends Command {
    private AMFString name;
    private AMFNumber transaction;
    private AMFString publishName;
    private AMFString type;


    public Publish(double transtactionId, String stream, String type) {
        name = new AMFString(Command.NAME_PUBLISH);
        transaction = new AMFNumber(transtactionId);
        publishName = new AMFString(stream);
        this.type = new AMFString(type);
        setCommandField(name);
        setCommandField(transaction);
        setCommandField(new AMFNull());
        setCommandField(publishName);
        setCommandField(this.type);
    }
}
