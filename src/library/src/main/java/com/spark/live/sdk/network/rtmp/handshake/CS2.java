package com.spark.live.sdk.network.rtmp.handshake;

import com.spark.live.sdk.network.rtmp.IBinary;
import com.spark.live.sdk.util.BinaryUtil;
import com.spark.live.sdk.util.LogUtil;

import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * C2消息结构
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |                        time (4 bytes)                         |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |                        time2 (4 bytes)                        |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |                        random bytes                           |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |                        random bytes                           |
 * |                            (cont)                             |
 * |                            ....                               |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * Created by devzhaoyou on 8/15/16.
 */

public class CS2 implements IBinary {

    public static final int REQUEST_LENGTH = 1536;
    public static final int RANDOM_LENGTH = 1528;

    /** This field MUST contain the timestamp sent by the peer in S1*/
    private int time;

    /** This field MUST contain the timestamp at which the previous
     * packet S1 sent by the peer was read. */
    private int time2;

    /**
     * This field MUST contain the random data field sent by the peer in
     * S1 (for C2) or S2 (for C1).Either peer can use the time and time2
     * fields together with the current timestamp as a quick estimate of
     * the bandwidth and/or latency of the connection,
     * but this is unlikely to be useful.
     */
    private byte[] randomEcho;

    public CS2() {
    }

    public CS2(byte[] binary) throws ErrorBinaryDataException {
        if (binary != null && binary.length == REQUEST_LENGTH) {
            ByteBuffer buffer = ByteBuffer.wrap(binary);
            buffer.rewind();
            time = buffer.getInt();
            time2 = buffer.getInt();
            randomEcho = new byte[RANDOM_LENGTH];
            buffer.get(randomEcho);
        } else {
            LogUtil.e("Error, can not create CS1 from the binary array. The array may be null or bad length!");
            throw new ErrorBinaryDataException("Error, can not create CS1 from the binary array. The array may be null or bad length!");
        }
    }

    public void setTime(int time) {
        this.time = time;
    }

    public void setTime2(int time2) {
        this.time2 = time2;
    }

    public void setRandomEcho(byte[] randomEcho) {
        this.randomEcho = randomEcho;
    }

    public int getTime() {
        return time;
    }

    public int getTime2() {
        return time2;
    }

    public byte[] getRandomEcho() {
        return randomEcho;
    }

    @Override
    public byte[] toBinary() {

        byte[] result = new byte[REQUEST_LENGTH];
        byte[] timeArray = BinaryUtil.getBytesFromIntValue(time, 4);
        byte[] time2Array = BinaryUtil.getBytesFromIntValue(time2, 4);
        System.arraycopy(timeArray, 0, result, 0, timeArray.length);
        System.arraycopy(time2Array, 0, result, timeArray.length, time2Array.length);
        System.arraycopy(randomEcho, 0, result, timeArray.length + time2Array.length, randomEcho.length);
        return result;
    }

    @Override
    public String toString() {
        return "CS2{" +
                "time=" + time +
                ", time2=" + time2 +
                ", randomEcho=" + Arrays.toString(randomEcho) +
                '}';
    }
}
