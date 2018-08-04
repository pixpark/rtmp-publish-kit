package com.spark.live.sdk.network.rtmp.amf;

/**
 *
 * Created by devzhaoyou on 8/16/16.
 */

public class AMFXMLDocument extends AMFData {


    private AMFLongString value;

    public AMFXMLDocument() {
        super(AMFData.XML_DOCUMENT_MARKER);
    }

    public void setValue(String value) {
        if (this.value == null) {
            this.value = new AMFLongString();
        }
        this.value.setValue(value);
    }

    @Override
    public byte[] toBinary() {

        if (binaryData == null) {
            byte[] valueArray = value.toBinary();
            binaryData = new byte[valueArray.length];
            binaryData[0] = typeMarker;
            System.arraycopy(valueArray, 1, binaryData, 1, valueArray.length - 1);
        }

        return binaryData;
    }
}
