package com.spark.live.sdk.util;

import android.util.Log;

import com.spark.live.sdk.BuildConfig;

/**
 * Log output wrapper util
 * Created by devzhaoyou on 7/21/16.
 */
public class LogUtil {

    /**
     * 冗余信息输出
     * @param verboseInfo 冗余信息
     */
    public static void v(String verboseInfo) {
        if (BuildConfig.LOG_FLAG) {
            Log.v(BuildConfig.TAG, verboseInfo);
        }
    }

    /**
     * debug消息输出
     * @param debugInfo debug 消息
     */
    public static void d(String debugInfo) {
        if (BuildConfig.LOG_FLAG) {
            Log.d(BuildConfig.TAG, debugInfo);
        }
    }

    /**
     * 程序信息输出
     * @param iInfo 信息内容
     */
    public static void i(String iInfo) {
        if (BuildConfig.LOG_FLAG) {
            Log.i(BuildConfig.TAG, iInfo);
        }
    }

    /**
     * 警告输出
     * @param warnInfo 警告消息
     */
    public static void w(String warnInfo) {
        if (BuildConfig.LOG_FLAG) {
            Log.w(BuildConfig.TAG, warnInfo);
        }
    }

    /**
     * 错误输出
     * @param errorInfo 错误消息
     */
    public static void e(String errorInfo) {
        if (BuildConfig.LOG_FLAG) {
            Log.e(BuildConfig.TAG, errorInfo);
        }
    }

    /**
     * java原始流输出
     * @param priority 日志级别
     * @param msg 日志内容
     */
    public static void println(int priority, String msg) {
        if (BuildConfig.LOG_FLAG) {
            Log.println(priority, BuildConfig.TAG, msg);
        }
    }
}
