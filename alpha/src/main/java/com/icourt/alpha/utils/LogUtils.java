package com.icourt.alpha.utils;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.icourt.alpha.BuildConfig;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

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

    /**
     * 打印任何对象
     *
     * @param obj
     */
    public static void logObject(Object obj) {
        if (isDebug) {
            d("------>" + reflect(obj));
        }
    }

    /**
     * 打印任何对象
     *
     * @param obj
     */
    public static void logObject(String tag, Object obj) {
        if (isDebug) {
            d(tag + " " + reflect(obj));
        }
    }

    public static Map<String, Object> reflect(Object obj) {
        if (obj == null) return null;
        Map<String, Object> data = new HashMap<>();
        Field[] fields = obj.getClass().getDeclaredFields();
        for (int j = 0; j < fields.length; j++) {
            fields[j].setAccessible(true);
            // 字段值
            if (fields[j].getType().getName().equals(
                    java.lang.String.class.getName())) {
                // String type
                try {
                    data.put(fields[j].getName(), fields[j].get(obj));
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            } else if (fields[j].getType().getName().equals(
                    java.lang.Integer.class.getName())
                    || fields[j].getType().getName().equals("int")) {
                // Integer type
                try {
                    data.put(fields[j].getName(), fields[j].getInt(obj));
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    data.put(fields[j].getName(), fields[j].get(obj));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return data;
    }


    public static final void logBundle(Bundle bundle) {
        if (bundle == null) {
            d("--------->bundle=null");
        } else {
            for (String key : bundle.keySet()) {
                d("--------->bundle key=" + key + ", content=" + bundle.get(key));
            }
        }
    }
}
