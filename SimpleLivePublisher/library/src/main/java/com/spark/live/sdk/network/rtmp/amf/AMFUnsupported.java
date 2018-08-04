package com.spark.live.sdk.network.rtmp.amf;

/**
 * If a type cannot be serialized a special unsupported marker can be used in place of the
 * type. Some endpoints may throw an error on encountering this type marker. No further
 * information is encoded for this type.
 * Created by devzhaoyou on 8/16/16.
 */

public class AMFUnsupported extends AMFData {

    public AMFUnsupported() {
        super(AMFData.UNSUPPORTED_MARKER);
    }

    @Override
    public byte[] toBinary() {
        return new byte[0];
    }
}
