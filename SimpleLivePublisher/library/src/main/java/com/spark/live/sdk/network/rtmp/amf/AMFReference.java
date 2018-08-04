package com.spark.live.sdk.network.rtmp.amf;

import com.spark.live.sdk.network.rtmp.ICreator;
import com.spark.live.sdk.util.BinaryUtil;
import com.spark.live.sdk.util.LogUtil;

import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 *
 * Created by devzhaoyou on 8/16/16.
 */

public class AMFReference extends AMFData {

    private short value;


    public AMFReference(short value) {
        super(AMFData.REFERENCE_MARKER);
        setValue(value);
    }

    public void setValue(short value) {
        this.value = (short) Math.abs(value);
        binaryData = null;
    }

    @Override
    public byte[] toBinary() {
        if (binaryData == null) {
            binaryData = new byte[3];
            binaryData[0] = typeMarker;
            byte[] valueArray = BinaryUtil.getBytesFromIntValue(value, 2);
            System.arraycopy(valueArray, 0, binaryData, 1, valueArray.length);
        }

        return binaryData;
    }

    @Override
    public String toString() {
        return "AMFReference{" +
                "value=" + value +
                '}';
    }

    public static final ICreator<AMFReference> Creator = new ICreator.Stub<AMFReference>() {
        @Override
        public AMFReference create(InputStream in) {
            if (in != null) {
                try {
                    int marker = in.read();
                    if (marker == NULL_MARKER) {
                        AMFReference object = new AMFReference((short) 0);
                        return object;
                    } if (marker == REFERENCE_MARKER) {
                        byte[] valueArray = new byte[2];
                        read(in, valueArray, 0, 2);
                        short shift = 0;
                        shift |= (valueArray[0] & 0x000000FF) << 8;
                        shift |= (valueArray[1] & 0x000000FF);
                        return new AMFReference(shift);
                    } else {
                        LogUtil.e("AMFReference Creator Error: Bad marker type for AMFReference!");
                        return null;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }

            } else {
                LogUtil.e("AMFReference Creator Error: The input stream is null!!");
                return null;
            }
        }

        @Override
        public AMFReference create(ByteBuffer buffer) {
            byte[] valueArray = new byte[2];
            buffer.get(valueArray);
            short shift = 0;
            shift |= (valueArray[0] & 0x000000FF) << 8;
            shift |= (valueArray[1] & 0x000000FF);
            return new AMFReference(shift);
        }
    };
}
