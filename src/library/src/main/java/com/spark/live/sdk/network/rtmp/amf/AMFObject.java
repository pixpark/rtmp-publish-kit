package com.spark.live.sdk.network.rtmp.amf;

import com.spark.live.sdk.network.rtmp.IBinary;
import com.spark.live.sdk.network.rtmp.ICreator;
import com.spark.live.sdk.util.LogUtil;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.UnsupportedCharsetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * Created by devzhaoyou on 8/16/16.
 */

public class AMFObject extends AMFData {
    protected List<ObjectProperty> properties;
    protected AMFObjectEnd endFlag = new AMFObjectEnd();
    protected int size = 4;

    public AMFObject() {
        super(AMFData.OBJECT_MARKER);
        properties = new ArrayList<>();
    }

    public void setProperty(AMFString key, AMFData value) {
        final int len = key.toBinary().length - 1 + value.toBinary().length;
        size += len;
        ObjectProperty property = new ObjectProperty(value.typeMarker, key, value, len);
        properties.add(property);
    }

    public void clearProperties() {
        binaryData = null;
        properties.clear();
        size = 4;
    }

    @Override
    public byte[] toBinary() {

        if (binaryData == null) {
            if (properties.isEmpty()) {
                binaryData = new AMFNull().toBinary();
            } else {
                try {
                    binaryData = new byte[size];
                    binaryData[0] = typeMarker;
                    int dstStart = 1;
                    for (ObjectProperty property : properties) {
                        byte[] propertyData = property.toBinary();
                        System.arraycopy(propertyData, 0, binaryData, dstStart, propertyData.length);
                        dstStart += propertyData.length;
                    }
                    byte[] endArray = endFlag.toBinary();
                    System.arraycopy(endArray, 0, binaryData, dstStart, endArray.length);
                } catch (UnsupportedCharsetException e) {
                    e.printStackTrace();
                }
            }

        }

        return binaryData;
    }

    public static final ICreator<AMFObject> Creator = new ICreator.Stub<AMFObject>() {

        @Override
        public AMFObject create(InputStream in) {

            if (in != null) {
                try {
                    int marker = in.read();
                    if (marker == NULL_MARKER) {
                        AMFObject object = new AMFObject();
                        return object;
                    } else if (marker == OBJECT_MARKER) {
                        AMFObject object = new AMFObject();
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
                        LogUtil.e("AMFObject Creator Error: Bad marker type for AMFObject!");
                        return null;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }

            } else {
                LogUtil.e("AMFObject Creator Error: The input stream is null!!");
                return null;
            }
        }

        @Override
        public AMFObject create(ByteBuffer buffer) {
            AMFObject object = new AMFObject();
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
                try {
                    AMFString key;
                    key = new AMFString(new String(content, "UTF-8"));
                    byte valueMarker = buffer.get();
                    object.setProperty(key, propertyParser(buffer, valueMarker));
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
                buffer.get(tmpArray);
            }

            return object;
        }
    };

    protected class ObjectProperty implements IBinary {

        byte propertyType;
        AMFString key;
        AMFData value;
        int binarySize;

        byte[] propertyData;

        public ObjectProperty(byte propertyType, AMFString key, AMFData value, int binarySize) {
            this.propertyType = propertyType;
            this.key = key;
            this.value = value;
            this.binarySize = binarySize;
        }

        @Override
        public byte[] toBinary() {

            if (propertyData == null) {

                byte[] keyArray = key.toBinary();
                byte[] valueArray = value.toBinary();
                propertyData = new byte[keyArray.length + valueArray.length - 1];
                System.arraycopy(keyArray, 1, propertyData, 0, keyArray.length - 1);
                System.arraycopy(valueArray, 0, propertyData, keyArray.length - 1, valueArray.length);
            }

            return propertyData;
        }

        @Override
        public String toString() {
            return "ObjectProperty{" +
                    "propertyType=" + propertyType +
                    ", key=" + key +
                    ", value=" + value.toString() +
                    ", binarySize=" + binarySize +
                    ", propertyData=" + Arrays.toString(propertyData) +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "AMFObject{" +
                "properties=" + properties.toString() +
                ", endFlag=" + endFlag +
                ", size=" + size +
                '}';
    }
}
