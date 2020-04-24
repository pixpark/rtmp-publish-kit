package com.spark.live.sdk.network.rtmp.amf;

import com.spark.live.sdk.network.rtmp.ICreator;
import com.spark.live.sdk.util.BinaryUtil;
import com.spark.live.sdk.util.LogUtil;

import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * AMF Number 类型
 * Created by devzhaoyou on 8/16/16.
 */

public class AMFNumber extends AMFData {

    private Double value;

    public AMFNumber(Double value) {
        super(AMFData.NUMBER_MARKER);
        this.value = value;
    }

    public void setValue(double value) {
        this.value = value;
        binaryData = null;
    }

    public double getValue() {
        return value;
    }

    @Override
    public byte[] toBinary() {
        if (binaryData == null) {
            if (value == null) {
                binaryData = new AMFNull().toBinary();
            } else {
                binaryData = new byte[9];
                binaryData[0] = typeMarker;
                byte[] valueArray = BinaryUtil.getBytesFromDoubleValue(value, 8);
                System.arraycopy(valueArray, 0, binaryData, 1, valueArray.length);
            }
        }

        return binaryData;
    }

    @Override
    public String toString() {
        return "AMFNumber{" +
                "value=" + value +
                '}';
    }

    public static final ICreator<AMFNumber> Creator = new ICreator.Stub<AMFNumber>() {
        public static final long LONG_BYTE_MASK = 0x000000FFL;

        @Override
        public AMFNumber create(InputStream in) {

            if (in != null) {
                try {
                    int marker = in.read();
                    if (marker == NULL_MARKER) {
                        AMFNumber object = new AMFNumber(null);
                        return object;
                    } else if (marker == NUMBER_MARKER) {
                        byte[] values = new byte[8];
                        read(in, values, 0, 8);
                        long result = 0;
                        result |= (values[0] & LONG_BYTE_MASK) << 56;
                        result |= (values[1] & LONG_BYTE_MASK) << 48;
                        result |= (values[2] & LONG_BYTE_MASK) << 40;
                        result |= (values[3] & LONG_BYTE_MASK) << 32;
                        result |= (values[4] & LONG_BYTE_MASK) << 24;
                        result |= (values[5] & LONG_BYTE_MASK) << 16;
                        result |= (values[6] & LONG_BYTE_MASK) << 8;
                        result |= (values[7] & LONG_BYTE_MASK);
                        return new AMFNumber(Double.longBitsToDouble(result));
                    } else {
                        LogUtil.e("AMFNumber Creator Error: Bad marker type for AMFNumber!");
                        throw new AMFBadMarkerException("AMFNumber Creator Error: Bad marker type for AMFNumber!");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            } else {
                LogUtil.e("AMFNumber Creator Error: The input stream is null!!");
                return null;
            }

        }

        @Override
        public AMFNumber create(ByteBuffer buffer) {
            byte[] values = new byte[8];
            buffer.get(values);
            long result = 0;
            result |= (values[0] & LONG_BYTE_MASK) << 56;
            result |= (values[1] & LONG_BYTE_MASK) << 48;
            result |= (values[2] & LONG_BYTE_MASK) << 40;
            result |= (values[3] & LONG_BYTE_MASK) << 32;
            result |= (values[4] & LONG_BYTE_MASK) << 24;
            result |= (values[5] & LONG_BYTE_MASK) << 16;
            result |= (values[6] & LONG_BYTE_MASK) << 8;
            result |= (values[7] & LONG_BYTE_MASK);
            return new AMFNumber(Double.longBitsToDouble(result));
        }
    };
}


