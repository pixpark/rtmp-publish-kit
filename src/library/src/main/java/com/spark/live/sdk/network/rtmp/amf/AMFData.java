package com.spark.live.sdk.network.rtmp.amf;

import com.spark.live.sdk.network.rtmp.IBinary;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 *
 * Created by devzhaoyou on 8/16/16.
 */

public abstract class AMFData implements IBinary {

    public static final byte NUMBER_MARKER = (byte) 0x00;
    public static final byte BOOLEAN_MARKER = (byte) 0x01;
    public static final byte STRING_MARKER = (byte) 0x02;
    public static final byte OBJECT_MARKER = (byte) 0x03;
    public static final byte MOVIEECLIP_MARKER = (byte) 0x04;
    public static final byte NULL_MARKER = (byte) 0x05;
    public static final byte UNDEFINED_MARKER = (byte) 0x06;
    public static final byte REFERENCE_MARKER = (byte) 0x07;
    public static final byte ECMA_ARRAY_MARKER = (byte) 0x08;
    public static final byte OBJECT_END_MARKER = (byte) 0x09;
    public static final byte STRICT_ARRAY_MARKER = (byte) 0x0A;
    public static final byte DATE_MARKER = (byte) 0x0B;
    public static final byte LONG_STRING_MARKER = (byte) 0x0C;
    public static final byte UNSUPPORTED_MARKER = (byte) 0x0D;
    public static final byte RECORDSET_MARKER = (byte) 0x0E;
    public static final byte XML_DOCUMENT_MARKER = (byte) 0x0F;
    public static final byte TYPED_OBJECT_MARKER = (byte) 0x10;
    public static final byte AVMPLUS_OBJECT_MARKER = (byte) 0x11;

    protected byte typeMarker;
    protected byte[] binaryData;

    public AMFData(byte typeMarker) {
        this.typeMarker = typeMarker;
    }

    static AMFData propertyParser(InputStream in, int marker) throws IOException {
        switch (marker) {
            case NUMBER_MARKER:
                return AMFNumber.Creator.createAfterMarker(in);
            case BOOLEAN_MARKER:
                return AMFBoolean.Creator.createAfterMarker(in);
            case STRING_MARKER:
                return AMFString.Creator.createAfterMarker(in);
            case OBJECT_MARKER:
                return AMFObject.Creator.createAfterMarker(in);
            case UNDEFINED_MARKER:
                return AMFUndefined.Creator.createAfterMarker(in);
            case REFERENCE_MARKER:
                return AMFReference.Creator.createAfterMarker(in);
            case ECMA_ARRAY_MARKER:
                return AMFECMAArray.Creator.createAfterMarker(in);
            case STRICT_ARRAY_MARKER:
                return AMFStrictArray.Creator.createAfterMarker(in);
            case NULL_MARKER:
                return AMFNull.Creator.createAfterMarker(in);
            case DATE_MARKER:
                return AMFDate.Creator.createAfterMarker(in);
            case LONG_STRING_MARKER:
                return AMFLongString.Creator.createAfterMarker(in);
            case TYPED_OBJECT_MARKER:
                return AMFTypedObject.Creator.createAfterMarker(in);
            default:
                return null;
        }
    }

    static AMFData propertyParser(ByteBuffer buffer, byte marker) throws IOException {
        switch (marker) {
            case NUMBER_MARKER:
                return AMFNumber.Creator.create(buffer);
            case BOOLEAN_MARKER:
                return AMFBoolean.Creator.create(buffer);
            case STRING_MARKER:
                return AMFString.Creator.create(buffer);
            case OBJECT_MARKER:
                return AMFObject.Creator.create(buffer);
            case UNDEFINED_MARKER:
                return AMFUndefined.Creator.create(buffer);
            case REFERENCE_MARKER:
                return AMFReference.Creator.create(buffer);
            case ECMA_ARRAY_MARKER:
                return AMFECMAArray.Creator.create(buffer);
            case STRICT_ARRAY_MARKER:
                return AMFStrictArray.Creator.create(buffer);
            case NULL_MARKER:
                return AMFNull.Creator.create(buffer);
            case DATE_MARKER:
                return AMFDate.Creator.create(buffer);
            case LONG_STRING_MARKER:
                return AMFLongString.Creator.create(buffer);
            case TYPED_OBJECT_MARKER:
                return AMFTypedObject.Creator.create(buffer);
            default:
                return null;
        }
    }
}
