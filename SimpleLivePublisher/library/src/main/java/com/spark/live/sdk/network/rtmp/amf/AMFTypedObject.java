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

public class AMFTypedObject extends AMFObject {

    private AMFString className;

    public AMFTypedObject() {
        typeMarker = TYPED_OBJECT_MARKER;
    }

    @Override
    public byte[] toBinary() {
        if (binaryData == null) {
            if (properties.isEmpty()) {
                binaryData =  new AMFNull().toBinary();
            } else {
                byte[] nameArray = className.toBinary();
                final int nameLen = nameArray.length - 1;
                binaryData = new byte[size + nameLen];
                binaryData[0] = typeMarker;
                System.arraycopy(nameArray, 1, binaryData, 1, nameLen);
                int dstStart = nameLen + 1;
                for (ObjectProperty property : properties) {
                    byte[] propertyData = property.toBinary();
                    System.arraycopy(propertyData, 0, binaryData, dstStart, propertyData.length);
                    dstStart += propertyData.length;
                }
                byte[] endArray = endFlag.toBinary();
                System.arraycopy(endArray, 0, binaryData, dstStart, endArray.length);
            }
        }

        return binaryData;
    }

    public void setClassName(AMFString className) {
        this.className = className;
    }

    public AMFString getClassName() {
        return className;
    }


    public static final ICreator<AMFTypedObject> Creator = new ICreator.Stub<AMFTypedObject>() {
        @Override
        public AMFTypedObject create(InputStream in) {
            if (in != null) {
                try {
                    int marker = in.read();
                    if (marker == NULL_MARKER) {
                        AMFTypedObject object = new AMFTypedObject();
                        return object;
                    } else if (marker == TYPED_OBJECT_MARKER) {
                        AMFTypedObject object = new AMFTypedObject();
                        AMFString className = AMFString.Creator.createAfterMarker(in);
                        object.setClassName(className);
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
                        LogUtil.e("AFMTypedObject Creator Error: Bad marker type for AFMTypedObject!");
                        return null;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }

            } else {
                LogUtil.e("AFMTypedObject Creator Error: The input stream is null!!");
                return null;
            }
        }

        @Override
        public AMFTypedObject create(ByteBuffer buffer) {
            try {
                AMFTypedObject object = new AMFTypedObject();
                AMFString className = AMFString.Creator.create(buffer);
                object.setClassName(className);
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
                    AMFString key = new AMFString(new String(content, "UTF-8"));
                    byte valueMarker = buffer.get();
                    object.setProperty(key, propertyParser(buffer, valueMarker));
                    buffer.get(tmpArray);
                }
                return object;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
    };

    @Override
    public String toString() {
        return "AMFTypedObject{" +
                "className=" + className +
                "properties=" + properties.toString() +
                '}';
    }
}
