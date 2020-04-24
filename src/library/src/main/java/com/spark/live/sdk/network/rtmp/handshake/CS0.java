package com.spark.live.sdk.network.rtmp.handshake;

import com.spark.live.sdk.network.rtmp.IBinary;
import com.spark.live.sdk.util.BinaryUtil;
import com.spark.live.sdk.util.LogUtil;

/**
 *
 * Created by devzhaoyou on 8/15/16.
 */

public class CS0 implements IBinary {

    public static final byte DEFAULT_VERSION = (byte) 0x03;

    private byte version = (byte) 0x03;

    public CS0() {
        this(DEFAULT_VERSION);
    }

    public CS0(byte version) {
        this.version = version;
    }

    public byte getVersion() {
        return version;
    }

    public void setVersion(byte version) {
        this.version = version;
    }

    @Override
    public byte[] toBinary() {
        LogUtil.i("The C0 version is : " + version);
        return BinaryUtil.getBytesFromIntValue(version, 1);
    }

    @Override
    public String toString() {
        return "CS0{" +
                "version=" + version +
                '}';
    }
}
