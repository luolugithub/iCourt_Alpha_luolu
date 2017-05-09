package com.icourt.alpha.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.text.TextUtils;

import com.icourt.alpha.R;

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

    /**
     * drawable转bitmap
     *
     * @param drawable
     * @return
     */
    public static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = Bitmap.createBitmap(
                drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(),
                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        //canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    /**
     * 获取文件对应图标 40
     *
     * @param fileName
     * @return
     */
    public static int getFileIcon40(String fileName) {
        if (!TextUtils.isEmpty(fileName) && fileName.length() > 0) {
            String type = fileName.substring(fileName.lastIndexOf(".") + 1);
            if (ActionConstants.resourcesMap40.containsKey(type)) {
                return ActionConstants.resourcesMap40.get(type);
            }
        }
        return R.mipmap.filetype_default_40;
    }

    /**
     * 获取文件对应图标 20
     *
     * @param fileName
     * @return
     */
    public static int getFileIcon20(String fileName) {
        if (!TextUtils.isEmpty(fileName) && fileName.length() > 0) {
            String type = fileName.substring(fileName.lastIndexOf(".") + 1);
            if (ActionConstants.resourcesMap.containsKey(type)) {
                return ActionConstants.resourcesMap.get(type);
            }
        }
        return R.mipmap.filetype_default_20;
    }
}
