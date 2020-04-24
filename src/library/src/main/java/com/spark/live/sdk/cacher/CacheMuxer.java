package com.spark.live.sdk.cacher;

import com.spark.live.sdk.media.packet.tag.common.FLVTag;
import com.spark.live.sdk.network.rtmp.message.MessageHeader;
import com.spark.live.sdk.network.rtmp.message.RtmpMessage;
import com.spark.live.sdk.network.rtmp.publisher.IPublishBuilderCallback;
import com.spark.live.sdk.network.rtmp.publisher.IPublisher;
import com.spark.live.sdk.network.rtmp.publisher.PublisherBuilder;
import com.spark.live.sdk.util.LogUtil;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.LinkedBlockingQueue;

/**
 *
 * Created by devzhaoyou on 9/8/16.
 */

public class CacheMuxer extends Thread implements IMuxer {

    private static final Object lock = new Object();

    private static CacheMuxer mInstance = null;

    private IPublisher publisher;
    private String rtmpUrl;
    private LinkedBlockingQueue<CacheFrame> cacheQueue;
    private ArrayList<CacheFrame> cachePool;
    private int nb_videos;
    private int nb_audios;

    private IMuxerCallback callback;

    private boolean isRunning = false;


    public static IMuxer getInstance(IMuxerCallback callback) {
        if (mInstance == null) {
            mInstance = new CacheMuxer("Cache", callback);
        }
        return mInstance;
    }

    private CacheMuxer(String name, IMuxerCallback callback) {
        super(name);
        this.callback = callback;
        nb_audios = 0;
        nb_videos = 0;
        cachePool = new ArrayList<>();
        cacheQueue = new LinkedBlockingQueue<>();
    }

    @Override
    public void startMuxer(String url) {
        this.rtmpUrl = url;
        isRunning = true;
        start();
    }

    @Override
    public void sendFrame(final CacheFrame frame) {
        if (frame.isVideoKeyFrame() && cacheQueue.size() > 60) {
            cacheQueue.clear();
        }
        cacheQueue.add(frame);
    }

    @Override
    public void closeMuxer() {
        synchronized (lock) {
            if (isAlive() && isRunning) {
                isRunning = false;
                this.interrupt();
                try {
                    this.join(5);
                } catch (InterruptedException e) {
                    LogUtil.e("CacheMuxer: " + e.getMessage());
                }
            }
            if (publisher != null) {
                publisher.close();
            }
        }
        publisher = null;
        mInstance = null;
    }

    private void cacheFrame(CacheFrame frame) {
        if (frame == null) {
            return;
        }
        switch (frame.getFrameType()) {
            case CacheFrame.VIDEO_FRAME:
                nb_videos++;
                break;
            case CacheFrame.AUDIO_FRAME:
                nb_audios++;
                break;
        }
        cachePool.add(frame);
        if (nb_videos > 1 && nb_audios > 1) {
            flushCache();
        }
    }

    private void flushCache() {

        Collections.sort(cachePool, new Comparator<CacheFrame>() {
            @Override
            public int compare(CacheFrame lhs, CacheFrame rhs) {
                return lhs.getPts() - rhs.getPts();
            }
        });
        while (nb_videos > 1 && nb_audios > 1) {
            CacheFrame cache = cachePool.remove(0);
            FLVTag tag = cache.getFrame();
            RtmpMessage message = new RtmpMessage();
            MessageHeader header = new MessageHeader();
            ByteBuffer tagBuffer = tag.toBinaryData();
            byte[] binaryData = new byte[tagBuffer.remaining()];
            tagBuffer.get(binaryData);
            header.setTimeStamp(tag.getTagHeader().getTimeStamp())
                    .setPayloadLength(binaryData.length);
            message.setPayload(binaryData);
            switch (cache.getFrameType()) {
                case CacheFrame.VIDEO_FRAME:
                    nb_videos--;
                    header.setChunkStreamId(5);
                    header.setMessageTypeId(MessageHeader.TYPE_IDS.TYPE_ID_VIDEO_MESSAGE);
                    break;
                case CacheFrame.AUDIO_FRAME:
                    nb_audios--;
                    header.setChunkStreamId(15);
                    header.setMessageTypeId(MessageHeader.TYPE_IDS.TYPE_ID_AUDIO_MESSAGE);
                    break;
            }
            message.setHeader(header);
            try {
                publisher.sendMessage(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void run() {

        new PublisherBuilder(rtmpUrl, new IPublishBuilderCallback() {
            @Override
            public void onBuildPublisher(IPublisher publisher) {
                CacheMuxer.this.publisher = publisher;
                callback.onMuxerResume();
            }

            @Override
            public void onBuildError(String error) {
                callback.onError(error);
                closeMuxer();
            }
        }).build();

        while (isRunning) {
            try {
                CacheFrame frame = cacheQueue.take();
                cacheFrame(frame);
            } catch (InterruptedException e) {
                break;
            }
        }
    }
}
