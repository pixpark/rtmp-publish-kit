package com.spark.live.sdk.util;

import android.os.Environment;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.Flushable;
import java.io.IOException;

/**
 *
 * Created by devzhaoyou on 9/5/16.
 */

public class FileUtil {

    public static FileOutputStream openFileInRoot(String fileName) {
        FileOutputStream fos = null;
        File dir = Environment.getExternalStorageDirectory();
        File file = new File(dir, fileName);
        try {
            if (!file.exists()) {
                String log = file.createNewFile() ? "Create a new file: " + fileName : "can not create file :" + fileName;
                LogUtil.w(log);
            }
            fos = new FileOutputStream(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fos;
    }

    public static void closeStream(Closeable stream) {
        if (stream != null) {
            try {
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void flushBuffer(Flushable stream) {
        if (stream != null) {
            try {
                stream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
