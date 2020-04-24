package com.spark.live.sdk.network.rtmp.amf;

import com.spark.live.sdk.network.rtmp.ICreator;
import com.spark.live.sdk.util.LogUtil;

import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 *
 *
 * Created by devzhaoyou on 8/16/16.
 */

public class AMFBoolean extends AMFData {

    public static final byte FALSE = (byte)0x00;
    public static final byte TRUE = (byte)0x01;
    public static final byte NULL = (byte)0x02;

    private byte value;

    public AMFBoolean(byte value) {
        super(AMFData.BOOLEAN_MARKER);
        this.value = (byte) Math.abs(value);
    }

    public void setValue(byte value) {
        this.value = (byte) Math.abs(value);
        binaryData = null;
    }

    public byte getValue() {
        return value;
    }

    @Override
    public byte[] toBinary() {
        if (binaryData == null) {
            if (value == NULL) {
                binaryData = new AMFNull().toBinary();
            } else {
                binaryData = new byte[2];
                binaryData[0] = typeMarker;
                binaryData[1] = value;
            }

        }
        return binaryData;
    }

    public static final ICreator<AMFBoolean> Creator = new ICreator.Stub<AMFBoolean>() {
        @Override
        public AMFBoolean create(InputStream in) {

            if (in != null) {
                try {
                    int marker = in.read();
                    if (marker == NULL_MARKER) {
                        return new AMFBoolean(NULL);
                    } else if (marker == AMFData.BOOLEAN_MARKER) {
                        int value = in.read();
                        return value == 0 ? new AMFBoolean(FALSE) : new AMFBoolean(TRUE);
                    } else {
                        LogUtil.e("AMFBoolean Creator Error: Bad marker type for AMFBoolean!");
                        throw new AMFBadMarkerException("AMFBoolean Creator Error: Bad marker type for AMFBoolean");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }

            } else {
                LogUtil.e("AMFBoolean Creator Error: The input stream is null!!");
                return null;
            }
        }

        @Override
        public AMFBoolean create(ByteBuffer buffer) {
            byte value = buffer.get();
            return value == 0 ? new AMFBoolean(FALSE) : new AMFBoolean(TRUE);
        }
    };
}
