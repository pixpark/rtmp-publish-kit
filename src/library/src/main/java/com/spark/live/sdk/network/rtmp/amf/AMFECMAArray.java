package com.spark.live.sdk.network.rtmp.amf;

import com.spark.live.sdk.network.rtmp.ICreator;
import com.spark.live.sdk.util.LogUtil;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 *
 * Created by devzhaoyou on 8/16/16.
 */

public class AMFECMAArray extends AMFObject {
    private int count;


    public AMFECMAArray() {
        typeMarker = ECMA_ARRAY_MARKER;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getCount() {
        return count;
    }

    @Override
    public byte[] toBinary() {
        return new byte[0];
    }


    public static final ICreator<AMFECMAArray> Creator = new ICreator.Stub<AMFECMAArray>() {
        @Override
        public AMFECMAArray create(InputStream in) {

            if (in != null) {
                try {
                    int marker = in.read();
                    if (marker == NULL_MARKER) {
                        return new AMFECMAArray();
                    } else if (marker == ECMA_ARRAY_MARKER) {
                        AMFECMAArray object = new AMFECMAArray();
                        byte[] countArray = new byte[4];
                        read(in, countArray, 0, 4);
                        int count = 0;
                        count |= (countArray[0] & 0x000000FF) << 24;
                        count |= (countArray[1] & 0x000000FF) << 16;
                        count |= (countArray[2] & 0x000000FF) << 8;
                        count |= (countArray[3] & 0x000000FF);
                        object.setCount(count);

                        AMFObjectEnd objectEnd = new AMFObjectEnd();
                        byte[] endArray = objectEnd.toBinary();
                        byte[] tmpArray = new byte[3];
                        read(in, tmpArray, 0, 3);
                        while (!Arrays.equals(endArray, tmpArray)) {
                            int len = 0;
                            len |= (tmpArray[0] & 0x000000FF) << 8;
                            len |= (tmpArray[1] & 0x000000FF);
                            byte[] content = new byte[len];
                            content[0] = tmpArray[2];
                            read(in, content, 1, len - 1);
                            AMFString key = new AMFString(new String(content, "UTF-8"));
                            int valueMarker = in.read();
                            object.setProperty(key, propertyParser(in, valueMarker));
                            read(in, tmpArray, 0, 3);
                        }

                        return object;
                    } else {
                        LogUtil.e("AMFECMAArray Creator Error: Bad marker type for AMFECMAArray!");
                        return null;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }

            } else {
                LogUtil.e("AMFECMAArray Creator Error: The input stream is null!!");
                return null;
            }
        }

        @Override
        public AMFECMAArray create(ByteBuffer buffer) {

            AMFECMAArray object = new AMFECMAArray();
            byte[] countArray = new byte[4];
            buffer.get(countArray);
            int count = 0;
            count |= (countArray[0] & 0x000000FF) << 24;
            count |= (countArray[1] & 0x000000FF) << 16;
            count |= (countArray[2] & 0x000000FF) << 8;
            count |= (countArray[3] & 0x000000FF);
            object.setCount(count);

            AMFObjectEnd objectEnd = new AMFObjectEnd();
            byte[] endArray = objectEnd.toBinary();
            byte[] tmpArray = new byte[3];
            buffer.get(tmpArray);
            while (!Arrays.equals(endArray, tmpArray)) {
                int len = 0;
                len |= (tmpArray[0] & 0x000000FF) << 8;
                len |= (tmpArray[1] & 0x000000FF);
                byte[] content = new byte[len];
                content[0] = tmpArray[2];
                buffer.get(content, 1, len - 1);
                AMFString key;
                try {
                    key = new AMFString(new String(content, "UTF-8"));
                    byte valueMarker = buffer.get();
                    object.setProperty(key, propertyParser(buffer, valueMarker));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                buffer.get(tmpArray);
            }
            return object;
        }
    };
}
