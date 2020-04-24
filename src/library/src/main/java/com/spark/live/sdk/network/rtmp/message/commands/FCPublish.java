package com.spark.live.sdk.network.rtmp.message.commands;

import com.spark.live.sdk.network.rtmp.amf.AMFNull;
import com.spark.live.sdk.network.rtmp.amf.AMFNumber;
import com.spark.live.sdk.network.rtmp.amf.AMFString;

/**
 *
 * Created by devzhaoyou on 9/12/16.
 */

public class FCPublish extends Command {

    public FCPublish(double transactionId, String stream) {
        setCommandField(new AMFString(NAME_FC_PUBLISH));
        setCommandField(new AMFNumber(transactionId));
        setCommandField(new AMFNull());
        setCommandField(new AMFString(stream));
    }
}
