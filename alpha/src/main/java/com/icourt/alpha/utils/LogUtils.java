package com.icourt.alpha.utils;

import android.text.TextUtils;
import android.util.Log;

import com.bugtags.library.Bugtags;
import com.icourt.alpha.BuildConfig;

/**
 * @author xuanyouwu
 * @email xuanyouwu@163.com
 * @time 2016-05-05 10:38
 */
public class LogUtils {
    public static boolean isDebug = true;
    public static final String TAG = LogUtils.class.getSimpleName();

    static {
        isDebug = BuildConfig.IS_DEBUG;
    }


    public static void d(String msg) {
        if (isDebug)
            Log.d(TAG, msg);
    }

    public static void d(String tag, String msg) {
        if (isDebug)
            Log.d(tag, msg);
    }

    public static void e(String msg) {
        if (isDebug)
            Log.e(TAG, msg);
    }

    public static void w(String msg) {
        if (isDebug)
            Log.w(TAG, msg);
    }

    public static void i(String msg) {
        if (isDebug)
            Log.i(TAG, msg);
    }

    public static void i(String tag, String msg) {
        if (isDebug)
            Log.i(tag, msg);
    }

    public static void maxLog(String msg) {
        maxLog(TAG, msg);
    }

    public static void maxLog(String tag, String msg) {
        if (isDebug) {
            if (TextUtils.isEmpty(tag)) return;
            if (msg.length() > 4000) {
                Log.i(tag, "sb.length = " + msg.length());
                int chunkCount = msg.length() / 4000;     // integer division
                for (int i = 0; i <= chunkCount; i++) {
                    int max = 4000 * (i + 1);
                    if (max >= msg.length()) {
                        Log.d(tag, msg.substring(4000 * i));
                    } else {
                        Log.d(tag, msg.substring(4000 * i, max));
                    }
                }
            } else {
                Log.d(tag, msg);
            }
        }
    }

    public static void feedToServer(String errorlog) {
        if(TextUtils.isEmpty(errorlog)) return;
        Bugtags.sendFeedback(errorlog);
    }

}
