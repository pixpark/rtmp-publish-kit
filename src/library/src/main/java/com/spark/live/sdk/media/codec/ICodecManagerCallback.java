package com.spark.live.sdk.media.codec;

/**
 *
 * Created by devzhaoyou on 7/28/16.
 */
public interface ICodecManagerCallback {

    void onInit(ICodecManager.Configuration configuration);

    void onStart();

    void onPause();

    void onStop();

    void onRelease();


    abstract class Stub implements ICodecManagerCallback{

        @Override
        public void onInit(ICodecManager.Configuration configuration) {

        }

        @Override
        public void onStart() {

        }

        @Override
        public void onPause() {

        }

        @Override
        public void onStop() {

        }

        @Override
        public void onRelease() {

        }
    }
}
