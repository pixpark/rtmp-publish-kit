package com.spark.live.sdk.util;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * 二进制操作工具类
 *
 * Note：数组中的字节序是高位字节优先即高字节排在前面以int为例：data = 0x7fac67ea
 * 如果取出一个字节结果为 array[0] = 0xea, 如果取出两个字节结果为 array[0] = 0x67,
 * array[1] = 0xea ...依次类推
 *
 * Created by devzhaoyou on 8/8/16.
 */
public class BinaryUtil {

    /**
     * 从一个整型变量中取出指定数量的字节
     * @param value 整型变量
     * @param count 字节数量
     * @return {@code count}长度字节数组
     */
    public static byte[] getBytesFromIntValue(int value, int count) {

        if (count <= 0 || count > 4) {
            LogUtil.e("The count arg must between 1 and 4");
            return null;
        }
        return getBytesFromLongValue(value, count);
    }

    /**
     * 从一个整型变量中取出指定数量的字节
     * @param value double 变量
     * @param count 要取的字节数量
     * @return {@code count}长度字节数组
     */
    public static byte[] getBytesFromDoubleValue(double value, int count) {
        long lvalue = Double.doubleToRawLongBits(value);
        return getBytesFromLongValue(lvalue, count);
    }

    /**
     * 从一个整型变量中取出指定数量的字节
     * @param value long 变量
     * @param count 要取的字节数量
     * @return {@code count}长度字节数组
     */
    public static byte[] getBytesFromLongValue(long value, int count) {

        if (count <= 0 || count > 8) {
            LogUtil.e("The count arg must between 1 and 8");
            return null;
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream(8);
        DataOutputStream dos = new DataOutputStream(bos);
        byte[] result = null;
        try {
            dos.writeLong(value);
            byte[] array = bos.toByteArray();
            final int len = array.length;
            result = Arrays.copyOfRange(array, len - count, len);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                dos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return result;

    }

    /**
     *
     * @param data int value
     */
    public static String printBits(int data) {
        StringBuilder buffer = new StringBuilder();
        for (int i=0; i<32; i++) {

            int result = (data & 0x80000000 >>> i ) >>> 31 - i;
            buffer.append(result).append(" ");
            if ((i + 1) % 8 == 0) {
                buffer.append("--");
            }

        }
        buffer.append(" ");
        return buffer.toString();
    }

    /**
     *
     * @param data byte value
     */
    public static String printBits(byte data) {
        StringBuilder buffer = new StringBuilder();
        for (int i=0; i<8; i++) {

            int result = (data & 0x80 >>> i ) >>> 7 - i;
            buffer.append(result).append(" ");
        }
        return buffer.toString();
    }

    /**
     *
     * @param value int value
     * @return hex string
     */
    public static String printIntToHex(int value) {
        StringBuilder buffer = new StringBuilder();
        int low = value & 0xff;
        int llow = (value >> 8) & 0xff;
        int lhigh = (value >> 16) & 0xff;
        int high = (value >> 24) & 0xff;

        buffer.append(printByteToHex((byte)high))
        .append(printByteToHex((byte)lhigh))
        .append(printByteToHex((byte)llow))
        .append(printByteToHex((byte)low)).append(" ");
        return buffer.toString();
    }

    /**
     *
     * @param value byte value
     * @return hex string
     */
    public static String printByteToHex(byte value) {

        StringBuilder buffer = new StringBuilder();
        int low = value & 0x0f;
        int high = (value >> 4) & 0x0f;
        buffer.append(" ").append(hexHelper(high)).append(hexHelper(low));
        return buffer.toString();
    }

    /**
     *
     * @param buffer byte buffer
     * @return hex string
     */
    public static String printIntToHex(ByteBuffer buffer) {
        StringBuilder sb = new StringBuilder();
        int flag = 0;
        buffer.mark();
        while (flag < buffer.limit()) {
            sb.append(printIntToHex(buffer.getInt()));
            flag++;
            if (flag % 10 == 0) {
                sb.append("\n");
            }
        }
        buffer.reset();
        return sb.toString();
    }

    /**
     *
     * @param buffer byte buffer
     * @return hex string
     */
    public static String printByteToHex(ByteBuffer buffer) {
        int flag = 0;
        StringBuilder sb = new StringBuilder();
        buffer.mark();
        while (buffer.hasRemaining()) {
            sb.append(printByteToHex(buffer.get()));
            flag++;
            if (flag % 10 == 0) {
                sb.append("\n");
            }
        }
        buffer.reset();
        return sb.toString();
    }

    /**
     *
     * @param value byte array
     * @return hex string
     */
    public static String printByteToHex(byte[] value) {
        StringBuilder sb = new StringBuilder();
        for (int i=0; i<value.length; i++) {
            sb.append(printByteToHex(value[i]));
            if (i !=0 && i % 10 ==0) {
                sb.append("\n");
            }
        }
        return sb.toString();
    }

    /**
     *
     * @param value int value 10 - 15
     * @return hex value
     */
    private static String hexHelper(int value) {
        switch (value) {
            case 10:
                return "A";
            case 11:
                return "B";
            case 12:
                return "C";
            case 13:
                return "D";
            case 14:
                return "E";
            case 15:
                return "F";
            default:
                return String.valueOf(value);

        }
    }
}
