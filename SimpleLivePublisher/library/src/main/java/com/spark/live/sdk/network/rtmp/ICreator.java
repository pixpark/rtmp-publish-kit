package com.spark.live.sdk.network.rtmp;

import com.spark.live.sdk.util.LogUtil;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 *
 * Created by devzhaoyou on 8/23/16.
 */

public interface ICreator<T> {

    T create(byte[] binary);

    T create(InputStream in);

    T createAfterMarker(InputStream in);

    T create(ByteBuffer buffer);

    abstract class Stub<T> implements ICreator<T> {

        @Override
        public T create(byte[] binary) {
            return null;
        }

        @Override
        public T create(InputStream in) {
            return null;
        }

        @Override
        public T createAfterMarker(InputStream in) {
            return null;
        }

        @Override
        public T create(ByteBuffer buffer) {
            return null;
        }

        /**
         * read bytes into a byte array
         * @param in        InputStream
         * @param dst       target array
         * @param dstOffset array offset
         * @param remained  remained data
         */
        public static void read(InputStream in, byte[] dst, int dstOffset, int remained) throws IOException {
            if (dst != null && dstOffset >= 0 && remained > 0) {
                try {
                    int actual;
                    do {
                        actual = in.read(dst, dstOffset, remained);
                        dstOffset += actual;
                        remained -= actual;
                    } while (actual > 0 && remained > 0);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                LogUtil.e("Error: Creator read method bad parameters!!");
            }
        }
    }
}
