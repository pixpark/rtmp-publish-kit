package com.spark.live.sdk.network.rtmp.cache;

import com.spark.live.sdk.network.rtmp.message.MessageHeader;
import com.spark.live.sdk.network.rtmp.message.RtmpMessage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.LinkedBlockingQueue;

/**
 *
 * Created by devzhaoyou on 9/13/16.
 */

public class AVCache {
    private static final Object lock = new Object();
    private LinkedBlockingQueue<RtmpMessage> cacheQueue;
    private ArrayList<RtmpMessage> cachePool;
    private int nb_videos;
    private int nb_audios;

    public AVCache() {

        cacheQueue = new LinkedBlockingQueue<>();
        cachePool = new ArrayList<>();
        nb_audios = 0;
        nb_videos = 0;
    }

    public void add(RtmpMessage message, boolean clear) {
        if (cacheQueue != null) {
            if (clear && cacheQueue.size() > 60) {
                cacheQueue.clear();
            }
            cacheQueue.add(message);
        }
    }

    public void flushPool() throws InterruptedException {
        if (cacheQueue != null && cachePool != null) {
            RtmpMessage message = cacheQueue.take();
            MessageHeader header = message.getHeader();
            switch (header.getTypeId()) {
                case MessageHeader.TYPE_IDS.TYPE_ID_AUDIO_MESSAGE:
                    nb_audios++;
                    break;
                case MessageHeader.TYPE_IDS.TYPE_ID_VIDEO_MESSAGE:
                    nb_videos++;
                    break;
            }
            cachePool.add(message);
            Collections.sort(cachePool, new Comparator<RtmpMessage>() {
                @Override
                public int compare(RtmpMessage lhs, RtmpMessage rhs) {
                    return lhs.getHeader().getTimestamp() - rhs.getHeader().getTimestamp();
                }
            });
            handleMessage();
        }
    }

    private void handleMessage() {
        while (nb_videos > 1 && nb_audios > 1) {
            RtmpMessage cachedMsg = cachePool.remove(0);
            switch (cachedMsg.getHeader().getTypeId()) {
                case MessageHeader.TYPE_IDS.TYPE_ID_AUDIO_MESSAGE:
                    nb_audios--;
                    break;
                case MessageHeader.TYPE_IDS.TYPE_ID_VIDEO_MESSAGE:
                    nb_videos--;
                    break;
            }
        }
    }
}
