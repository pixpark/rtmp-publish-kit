package com.spark.live.sdk.network.rtmp;

/**
 *
 * Created by devzhaoyou on 9/2/16.
 */

public class TimeStampHelper {

    private long baseRealTime;
    private long lastRealTime;
    private static TimeStampHelper timeStampCreator;

    private TimeStampHelper() {
        baseRealTime = System.currentTimeMillis();
        lastRealTime = baseRealTime;
    }

    public static TimeStampHelper getInstance() {
        if (timeStampCreator == null) {
            timeStampCreator = new TimeStampHelper();
        }
        return timeStampCreator;
    }

    public int createAbsoluteTimestamp() {
        lastRealTime = System.currentTimeMillis();
        int timestamp = (int)(lastRealTime - baseRealTime);
        return timestamp;
    }

    public int createTimestampDelta() {
        long currentRealTime = System.currentTimeMillis();
        long timestampDelta = currentRealTime - lastRealTime;
        lastRealTime = currentRealTime;
        return (int)timestampDelta;
    }

    public static void release() {
        timeStampCreator = null;
    }
}
