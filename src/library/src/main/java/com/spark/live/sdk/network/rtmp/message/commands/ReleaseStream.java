package com.spark.live.sdk.network.rtmp.message.commands;

import com.spark.live.sdk.network.rtmp.amf.AMFNull;
import com.spark.live.sdk.network.rtmp.amf.AMFNumber;
import com.spark.live.sdk.network.rtmp.amf.AMFString;

/**
 *
 * Created by devzhaoyou on 9/12/16.
 */

public class ReleaseStream extends Command {
    private AMFString name;
    private AMFNumber transactionId;


    public ReleaseStream(double transactionId, String stream) {
        this.name = new AMFString(NAME_RELEASE_STREAM);
        this.transactionId = new AMFNumber(transactionId);
        setCommandField(this.name);
        setCommandField(this.transactionId);
        setCommandField(new AMFNull());
        setCommandField(new AMFString(stream));
    }
}
