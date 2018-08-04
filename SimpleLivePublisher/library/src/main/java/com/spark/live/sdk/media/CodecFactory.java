package com.spark.live.sdk.media;

import com.spark.live.sdk.media.codec.ICodecManager;
import com.spark.live.sdk.media.codec.ICodecManagerCallback;

/**
 *
 * Created by devzhaoyou on 7/27/16.
 */
public class CodecFactory {

    public static final int MEDIA_CODEC = 100;
    public static final int FFMEPG_CODEC = 200;


    private static ICodecManager mInstance = null;


    public static ICodecManager createCodecManager(int codecType, ICodecManagerCallback callback) {
        switch (codecType) {
            case MEDIA_CODEC:
                mInstance = null;
                break;
            case FFMEPG_CODEC:
                mInstance = null;
                break;
        }

        return mInstance;
    }

}
