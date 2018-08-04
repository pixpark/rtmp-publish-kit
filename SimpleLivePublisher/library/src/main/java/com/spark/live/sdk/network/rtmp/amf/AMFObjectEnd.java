package com.spark.live.sdk.network.rtmp.amf;

/**
 * <p>
 * The object-end-type should only appear to mark the end of a set of properties of an
 * object-type or typed-object-type or to signal the end of an associative section of an
 * ECMA Array.
 * </p>
 * Created by devzhaoyou on 8/16/16.
 */

public class AMFObjectEnd extends AMFData{


    public AMFObjectEnd() {
        super(AMFData.OBJECT_END_MARKER);
    }

    @Override
    public byte[] toBinary() {
        if (binaryData == null) {
            binaryData = new byte[3];
            binaryData[0] = 0x00;
            binaryData[1] = 0x00;
            binaryData[2] = typeMarker;
        }
        return binaryData;
    }

    @Override
    public String toString() {
        return "AMFObjectEnd{}";
    }
}
