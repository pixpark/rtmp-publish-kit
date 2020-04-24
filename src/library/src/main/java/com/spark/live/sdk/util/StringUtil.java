package com.spark.live.sdk.util;

import java.util.Locale;

/**
 * Created by devzhaoyou on 7/28/16.
 */
public class StringUtil {

    public static String format(String format, Object ...args) {
        return String.format(Locale.getDefault(), format, args) ;
    }
}
