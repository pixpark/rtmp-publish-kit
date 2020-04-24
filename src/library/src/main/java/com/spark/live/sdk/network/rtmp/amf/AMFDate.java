package com.spark.live.sdk.network.rtmp.amf;

import com.spark.live.sdk.network.rtmp.ICreator;
import com.spark.live.sdk.util.BinaryUtil;
import com.spark.live.sdk.util.LogUtil;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 *
 * Created by devzhaoyou on 8/16/16.
 */

public class AMFDate extends AMFData {

    private short timeZone = (short) 0x0000;
    private Double value;


    public AMFDate() {
        super(AMFData.DATE_MARKER);
    }

    public AMFDate(short timeZone, Double value) {
        super(AMFData.DATE_MARKER);
        this.timeZone = timeZone;
        this.value = value;
    }

    @Override
    public byte[] toBinary() {

        if (binaryData == null) {
            if (value == null) {
                binaryData = new AMFNull().toBinary();
            } else {
                binaryData = new byte[11];
                binaryData[0] = typeMarker;
                byte[] valueArray = BinaryUtil.getBytesFromDoubleValue(value, 8);
                System.arraycopy(valueArray, 0, binaryData, 1, valueArray.length);
                byte[] timeZoneArray = BinaryUtil.getBytesFromIntValue(timeZone, 2);
                System.arraycopy(timeZoneArray, 0, binaryData, valueArray.length + 1, timeZoneArray.length);
            }
        }

        return binaryData;
    }

    public static final ICreator<AMFDate> Creator = new ICreator.Stub<AMFDate>() {

        public static final long LONG_BYTE_MASK = 0x000000FFL;

        @Override
        public AMFDate create(InputStream in) {


            if (in != null) {
                try {
                    int marker = in.read();
                    if (marker == NULL_MARKER) {
                        return new AMFDate();
                    } else if (marker == DATE_MARKER) {
                        return createAfterMarker(in);
                    } else {
                        LogUtil.e("AMFDate Creator Error: Bad marker type for AMFDate!");
                        return null;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }

            } else {
                LogUtil.e("AMFDate Creator Error: The input stream is null!!");
                return null;
            }
        }

        @Override
        public AMFDate createAfterMarker(InputStream in) {
            byte[] values = new byte[8];
            try {
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
                double value = Double.longBitsToDouble(result);
                byte[] zoneArray = new byte[2];
                read(in, zoneArray, 0, 2);
                int timeZone = 0;
                timeZone |= zoneArray[0] << 8;
                timeZone |= (zoneArray[1] & 0x000000FF);
                return new AMFDate((short) timeZone, value);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public AMFDate create(ByteBuffer buffer) {
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
            double value = Double.longBitsToDouble(result);
            byte[] zoneArray = new byte[2];
            buffer.get(zoneArray);
            int timeZone = 0;
            timeZone |= zoneArray[0] << 8;
            timeZone |= (zoneArray[1] & 0x000000FF);
            return new AMFDate((short) timeZone, value);
        }
    };

    @Override
    public String toString() {
        return "AMFDate{" +
                "timeZone=" + timeZone +
                ", value=" + value +
                '}';
    }
}
