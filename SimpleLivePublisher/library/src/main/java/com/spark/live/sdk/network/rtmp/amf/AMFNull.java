package com.spark.live.sdk.network.rtmp.amf;

import com.spark.live.sdk.network.rtmp.ICreator;
import com.spark.live.sdk.util.LogUtil;

import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 *
 * Created by devzhaoyou on 8/16/16.
 */

public class AMFNull extends AMFData {

    public AMFNull() {
        super(AMFData.NULL_MARKER);
    }

    @Override
    public byte[] toBinary() {
        if (binaryData == null) {
            binaryData = new byte[1];
            binaryData[0] = typeMarker;
        }

        return binaryData;
    }

    @Override
    public String toString() {
        return "AMFNull{null}";
    }

    public static final ICreator<AMFNull> Creator = new ICreator.Stub<AMFNull>() {
        @Override
        public AMFNull create(InputStream in) {

            if (in != null) {
                try {
                    int marker = in.read();
                    if (marker == NULL_MARKER) {
                        return new AMFNull();
                    } else {
                        LogUtil.e("AMFNull Creator Error: Bad marker type for AMFNull!");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else {
                LogUtil.e("AMFNull Creator Error: The input stream is null!!");
                return null;
            }

            return super.create(in);
        }

        @Override
        public AMFNull create(ByteBuffer buffer) {
            return new AMFNull();
        }
    };


}
