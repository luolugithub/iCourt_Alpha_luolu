package com.icourt.alpha.utils;

import android.os.Environment;
import android.text.TextUtils;

import java.io.File;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/18
 * version 1.0.0
 */
public class FileUtils {

    /**
     * 获取 友好提示文件单位
     *
     * @param b B
     * @return
     */
    public static final String bFormat(long b) {
        long kb = 1024;
        long mb = kb * 1024;
        long gb = mb * 1024;
        if (b >= gb) {
            return String.format("%.1f GB", (float) b / gb);
        } else if (b >= mb) {
            float f = (float) b / mb;
            return String.format(f > 100 ? "%.0f MB" : "%.1f MB", f);
        } else if (b >= kb) {
            float f = (float) b / kb;
            return String.format(f > 100 ? "%.0f KB" : "%.1f KB", f);
        } else
            return String.format("%d B", b);
    }

    /**
     * 获取文件单位聚合换算
     *
     * @param kb kb
     * @return
     */
    public static final String kbFromat(long kb) {
        return bFormat(1024 * kb);
    }

    /**
     * 检查文件是否存在
     *
     * @param path
     * @return
     */
    public static final boolean isFileExists(String path) {
        if (TextUtils.isEmpty(path)) return false;
        File file = new File(path);
        return file != null && file.exists();
    }

    /**
     * sd卡是否可用
     *
     * @return
     */
    public static boolean sdAvailable() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }
}
