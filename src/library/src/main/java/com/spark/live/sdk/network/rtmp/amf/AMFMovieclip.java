package com.spark.live.sdk.network.rtmp.amf;

/**
 * This type is not supported and is reserved for future use.
 * <p>
 * The Movieclip type is not supported for serialization; their markers are
 * retained with a reserved status for future use.
 * </p>
 * Created by devzhaoyou on 8/16/16.
 */

public class AMFMovieclip extends AMFData {

    public AMFMovieclip() {
        super(AMFData.MOVIEECLIP_MARKER);
    }

    @Override
    public byte[] toBinary() {
        return new byte[0];
    }
}
