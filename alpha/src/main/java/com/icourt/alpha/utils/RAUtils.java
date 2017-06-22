package com.icourt.alpha.utils;

/**
 * @author xuanyouwu
 * @email xuanyouwu@163.com
 * @time 2016-11-10 17:50
 * 防爆点击
 */

public class RAUtils {

    private RAUtils() {
    }

    public static final long DURATION_DEFAULT = 350;
    private static long lastClickTime;

    /**
     * 防爆 阻力 false 表示暴力点击
     *
     * @param duration 点击间隔
     * @return
     */
    public static boolean isLegal(long duration) {
        long current;
        current = System.currentTimeMillis();
        if (0 == lastClickTime) {
            lastClickTime = current;
            return true;
        } else {
            long distance = current - lastClickTime;
            lastClickTime = current;
            return duration < distance;
        }
    }
}
