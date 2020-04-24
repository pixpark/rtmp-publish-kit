package com.spark.live.sdk.network.rtmp.message.commands;

import com.spark.live.sdk.network.rtmp.amf.AMFNull;
import com.spark.live.sdk.network.rtmp.amf.AMFNumber;
import com.spark.live.sdk.network.rtmp.amf.AMFString;

/**
 *
 * Created by devzhaoyou on 9/12/16.
 */

public class CreateStream extends Command {

    public CreateStream(double transactionId) {
        setCommandField(new AMFString(NAME_CREATE_STREAM));
        setCommandField(new AMFNumber(transactionId));
        setCommandField(new AMFNull());
    }
}
