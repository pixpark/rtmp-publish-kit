package com.spark.live.sdk.network.rtmp.handshake;

import com.spark.live.sdk.network.rtmp.IBinary;
import com.spark.live.sdk.util.BinaryUtil;
import com.spark.live.sdk.util.LogUtil;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Random;

/**
 * C1消息结构
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |                         time (4 bytes)                        |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |                        zero (4 bytes)                         |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |                        random bytes                           |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |                        random bytes                           |
 * |                            (cont)                             |
 * |                            ....                               |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 *
 * Created by devzhaoyou on 8/15/16.
 */
public class CS1 implements IBinary {

    public static final int REQUEST_LENGTH = 1536;
    public static final int RANDOM_LENGTH = 1528;

    private int time;

    private int zero = 0x00;

    private byte[] randomData;

    public CS1() {
        createRandom();
        time = (int) (System.currentTimeMillis() / 1000);
    }

    public CS1(byte[] binary) throws ErrorBinaryDataException {
        if (binary != null && binary.length == REQUEST_LENGTH) {
            ByteBuffer buffer = ByteBuffer.wrap(binary);
            buffer.rewind();
            time = buffer.getInt();
            zero = buffer.getInt();
            randomData = new byte[RANDOM_LENGTH];
            buffer.get(randomData);
        } else {
            LogUtil.e("Error, can not create CS1 from the binary array. The array may be null or bad length!");
            throw new ErrorBinaryDataException("Error, can not create CS1 from the binary array. The array may be null or bad length!");
        }

    }

    private void createRandom() {
        randomData = new byte[RANDOM_LENGTH];
        Random random = new Random();
        random.nextBytes(randomData);
    }

    public int getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = (int) time;
    }


    public byte[] getRandomData() {
        return randomData;
    }

    @Override
    public byte[] toBinary() {

        byte[] result = new byte[REQUEST_LENGTH];
        byte[] timeArray = BinaryUtil.getBytesFromIntValue(time, 4);
        byte[] zeroArray = BinaryUtil.getBytesFromIntValue(0x00, 4);
        System.arraycopy(timeArray, 0, result, 0, timeArray.length);
        System.arraycopy(zeroArray, 0, result, timeArray.length, zeroArray.length);
        System.arraycopy(randomData, 0, result, timeArray.length + zeroArray.length, randomData.length);
        return result;
    }

    @Override
    public String toString() {
        return "CS1{" +
                "time=" + time +
                ", zero=" + zero +
                ", randomData=" + Arrays.toString(randomData) +
                '}';
    }
}
