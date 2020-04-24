package com.spark.live.sdk.network.rtmp.amf;

import android.text.TextUtils;

import com.spark.live.sdk.network.rtmp.ICreator;
import com.spark.live.sdk.util.BinaryUtil;
import com.spark.live.sdk.util.LogUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

/**
 * AMFString
 * Created by devzhaoyou on 8/16/16.
 */

public class AMFString extends AMFData {

    private String value;

    public AMFString(String value) {
        super(AMFData.STRING_MARKER);
        this.value = value;
    }

    public void setValue(String value) {
        this.value = value;
        binaryData = null;
    }

    public String getValue() {
        return value;
    }

    @Override
    public byte[] toBinary() {

        if (binaryData == null) {
            try {
                if (TextUtils.isEmpty(value)) {
                    binaryData = new AMFNull().toBinary();
                } else {
                    byte[] data = value.getBytes("UTF-8");
                    byte[] headerArray = BinaryUtil.getBytesFromIntValue(data.length, 2);
                    binaryData = new byte[data.length + 3];
                    binaryData[0] = typeMarker;
                    System.arraycopy(headerArray, 0, binaryData, 1, headerArray.length);
                    System.arraycopy(data, 0, binaryData, headerArray.length + 1, data.length);
                }

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        return binaryData;
    }

    @Override
    public String toString() {
        return "AMFString{" +
                "value='" + value + '\'' +
                '}';
    }

    public static final ICreator<AMFString> Creator = new ICreator.Stub<AMFString>() {
        @Override
        public AMFString create(InputStream in) {
            if (in != null) {
                try {
                    int marker = in.read();
                    if (marker == NULL_MARKER) {
                        AMFString object = new AMFString(null);
                        return object;
                    } else if (marker == STRING_MARKER) {
                        byte[] header = new byte[2];
                        read(in, header, 0, 2);
                        int len = 0;
                        len |= (header[0] & 0x000000FF) << 8;
                        len |= (header[1] & 0x000000FF);
                        byte[] content = new byte[len];
                        read(in, content, 0, len);
                        String value = new String(content, "UTF-8");
                        return new AMFString(value);
                    } else {
                        LogUtil.e("AMFString Creator Error: bad type marker for AMFString!!");
                        return null;
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }

            } else {
                LogUtil.e("ConnectResponse Create Error!! The input stream is null!!");
                return null;
            }
        }

        @Override
        public AMFString create(ByteBuffer buffer) {
            byte[] header = new byte[2];
            try {
                buffer.get(header);
                int len = 0;
                len |= (header[0] & 0x000000FF) << 8;
                len |= (header[1] & 0x000000FF);
                byte[] content = new byte[len];
                buffer.get(content);
                String value = new String(content, "UTF-8");
                return new AMFString(value);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }

        }
    };
}
