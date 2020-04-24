package com.spark.live.sdk.network.rtmp.amf;

import android.text.TextUtils;

import com.spark.live.sdk.network.rtmp.ICreator;
import com.spark.live.sdk.util.BinaryUtil;
import com.spark.live.sdk.util.LogUtil;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

/**
 *
 * Created by devzhaoyou on 8/16/16.
 */

public class AMFLongString extends AMFData {

    private String value;

    public AMFLongString() {
        super(AMFData.LONG_STRING_MARKER);
    }

    public AMFLongString(String value) {
        super(AMFData.LONG_STRING_MARKER);
        this.value = value;
    }

    public void setValue(String value) {
        this.value = value;
        binaryData = null;
    }

    @Override
    public byte[] toBinary() {

        if (binaryData == null) {
            if (TextUtils.isEmpty(value)) {
                binaryData = new AMFNull().toBinary();
            } else {
                try {
                    byte[] data = value.getBytes("UTF-8");
                    byte[] headerArray = BinaryUtil.getBytesFromIntValue(data.length, 4);
                    binaryData = new byte[data.length + 5];
                    binaryData[0] = typeMarker;
                    System.arraycopy(headerArray, 0, binaryData, 1, headerArray.length);
                    System.arraycopy(data, 0, binaryData, headerArray.length + 1, data.length);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }

        return binaryData;
    }

    public static final ICreator<AMFLongString> Creator = new ICreator.Stub<AMFLongString>() {
        @Override
        public AMFLongString create(InputStream in) {
            if (in != null) {
                try {
                    int marker = in.read();
                    if (marker == NULL_MARKER) {
                        return new AMFLongString();
                    } else if (marker == LONG_STRING_MARKER) {
                        byte[] headerArray = new byte[4];
                        read(in, headerArray, 0, 4);
                        int len = 0;
                        len |= (headerArray[0] & 0x000000FF) << 24;
                        len |= (headerArray[1] & 0x000000FF) << 16;
                        len |= (headerArray[2] & 0x000000FF) << 8;
                        len |= (headerArray[3] & 0x000000FF);
                        byte[] utf8 = new byte[len];
                        read(in, utf8, 0, len);
                        return new AMFLongString(new String(utf8, "UTF-8"));
                    } else {
                        LogUtil.e("AMFLongString Creator Error: Bad marker type for AMFLongString!");
                        return null;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }

            } else {
                LogUtil.e("AMFLongString Creator Error: The input stream is null!!");
                return null;
            }
        }

        @Override
        public AMFLongString create(ByteBuffer buffer) {
            byte[] headerArray = new byte[4];
            buffer.get(headerArray);
            int len = 0;
            len |= (headerArray[0] & 0x000000FF) << 24;
            len |= (headerArray[1] & 0x000000FF) << 16;
            len |= (headerArray[2] & 0x000000FF) << 8;
            len |= (headerArray[3] & 0x000000FF);
            byte[] utf8 = new byte[len];
            buffer.get(utf8);
            try {
                return new AMFLongString(new String(utf8, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                return null;
            }
        }
    };

    @Override
    public String toString() {
        return "AMFLongString{" +
                "value='" + value + '\'' +
                '}';
    }
}
