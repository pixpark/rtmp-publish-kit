package com.spark.live.sdk.network.rtmp.amf;

import com.spark.live.sdk.network.rtmp.ICreator;
import com.spark.live.sdk.util.LogUtil;

import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 *
 * Created by devzhaoyou on 8/16/16.
 */

public class AMFUndefined extends AMFData {

    public AMFUndefined() {
        super(AMFData.UNDEFINED_MARKER);
    }

    @Override
    public byte[] toBinary() {
        if (binaryData == null) {
            binaryData = new byte[1];
            binaryData[0] = typeMarker;
        }
        return binaryData;
    }

    public static final ICreator<AMFUndefined> Creator = new ICreator.Stub<AMFUndefined>() {
        @Override
        public AMFUndefined create(InputStream in) {

            if (in != null) {
                try {
                    int marker = in.read();
                    if (marker == NULL_MARKER) {
                        return new AMFUndefined();
                    }else if (marker == UNDEFINED_MARKER) {
                        return new AMFUndefined();
                    } else {
                        LogUtil.e("AMFUndefined Creator Error: Bad marker type for AMFUndefined!");
                        return null;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }

            } else {
                LogUtil.e("AMFUndefined Creator Error: The input stream is null!!");
                return null;
            }
        }

        @Override
        public AMFUndefined create(ByteBuffer buffer) {
            return new AMFUndefined();
        }
    };
}
