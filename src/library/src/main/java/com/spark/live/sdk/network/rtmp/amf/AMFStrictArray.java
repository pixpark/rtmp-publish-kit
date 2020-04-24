package com.spark.live.sdk.network.rtmp.amf;

import com.spark.live.sdk.network.rtmp.ICreator;
import com.spark.live.sdk.util.BinaryUtil;
import com.spark.live.sdk.util.LogUtil;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;

/**
 *
 * Created by devzhaoyou on 8/16/16.
 */

public class AMFStrictArray extends AMFData {

    private int count;
    private ArrayList<AMFData> valueList;
    private int binarySize = 5;

    public AMFStrictArray() {
        super(AMFData.STRICT_ARRAY_MARKER);
        valueList = new ArrayList<>();
    }

    public void setElement(AMFData element) {
        valueList.add(element);
        binarySize += element.toBinary().length;
        count++;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getCount() {
        return count;
    }

    public void clear() {
        count = 0;
        binarySize = 4;
        valueList.clear();
        binaryData = null;
    }

    @Override
    public String toString() {
        return "AMFStrictArray{" +
                "count=" + count +
                ", valueList=" + valueList.toString() +
                ", binarySize=" + binarySize +
                '}';
    }

    @Override
    public byte[] toBinary() {

        if (binaryData == null) {
            if (count == 0 && valueList.isEmpty()) {
                binaryData = new AMFNull().toBinary();
            } else {
                binaryData = new byte[binarySize];
                binaryData[0] = typeMarker;
                byte[] countArray = BinaryUtil.getBytesFromIntValue(count, 4);
                System.arraycopy(countArray, 0, binaryData, 1, countArray.length);
                int dstStart = 5;
                for (AMFData value : valueList) {
                    byte[] valueArray = value.toBinary();
                    System.arraycopy(valueArray, 0, binaryData, dstStart, valueArray.length);
                    dstStart += valueArray.length;
                }
            }
        }

        return binaryData;
    }

    public static final ICreator<AMFStrictArray> Creator = new ICreator.Stub<AMFStrictArray>() {
        @Override
        public AMFStrictArray create(InputStream in) {
            if (in != null) {
                try {
                    int marker = in.read();
                    if (marker == NULL_MARKER) {
                        AMFStrictArray object = new AMFStrictArray();
                        return object;
                    } else if (marker == STRICT_ARRAY_MARKER) {
                        AMFStrictArray strictArray = new AMFStrictArray();
                        byte[] countArray = new byte[4];
                        read(in, countArray, 0, 4);
                        int count = 0;
                        count |= (countArray[0] & 0x000000FF) << 24;
                        count |= (countArray[1] & 0x000000FF) << 16;
                        count |= (countArray[2] & 0x000000FF) << 8;
                        count |= (countArray[3] & 0x000000FF);
                        while (count > 0) {
                            marker = in.read();
                            strictArray.setElement(propertyParser(in, marker));
                            count--;
                        }
                        return strictArray;
                    } else {
                        LogUtil.e("AMFStrictArray Creator Error: Bad marker type for AMFStrictArray!");
                        return null;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }

            } else {
                LogUtil.e("AMFStrictArray Creator Error: The input stream is null!!");
                return null;
            }
        }

        @Override
        public AMFStrictArray create(ByteBuffer buffer) {
            AMFStrictArray strictArray = new AMFStrictArray();
            byte[] countArray = new byte[4];
            buffer.get(countArray);
            int count = 0;
            count |= (countArray[0] & 0x000000FF) << 24;
            count |= (countArray[1] & 0x000000FF) << 16;
            count |= (countArray[2] & 0x000000FF) << 8;
            count |= (countArray[3] & 0x000000FF);
            while (count > 0) {
                try {
                    byte marker = buffer.get();
                    strictArray.setElement(propertyParser(buffer, marker));
                    count--;
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }
            return strictArray;
        }
    };
}
