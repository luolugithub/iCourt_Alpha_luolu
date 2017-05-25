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
    public static final String ALPHA_PAGENAME_FILE = "com.icourt.alpha";
    public static final String THUMB_IMAGE_ROOT_PATH = getSDPath() + "/" + ALPHA_PAGENAME_FILE + "/image";
    public static final String THUMB_FILE_ROOT_PATH = getSDPath() + "/" + ALPHA_PAGENAME_FILE + "/file";

    public static final String[][] MIME_MapTable = {
            //{后缀名，MIME类型}
            {".3gp", "video/3gpp"},
            {".apk", "application/vnd.android.package-archive"},
            {".asf", "video/x-ms-asf"},
            {".avi", "video/x-msvideo"},
            {".bin", "application/octet-stream"},
            {".bmp", "image/bmp"},
            {".c", "text/plain"},
            {".class", "application/octet-stream"},
            {".conf", "text/plain"},
            {".cpp", "text/plain"},
            {".doc", "application/msword"},
            {".docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"},
            {".xls", "application/vnd.ms-excel"},
            {".xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"},
            {".exe", "application/octet-stream"},
            {".gif", "image/gif"},
            {".gtar", "application/x-gtar"},
            {".gz", "application/x-gzip"},
            {".h", "text/plain"},
            {".htm", "text/html"},
            {".html", "text/html"},
            {".jar", "application/java-archive"},
            {".java", "text/plain"},
            {".jpeg", "image/jpeg"},
            {".jpg", "image/jpeg"},
            {".js", "application/x-javascript"},
            {".log", "text/plain"},
            {".m3u", "audio/x-mpegurl"},
            {".m4a", "audio/mp4a-latm"},
            {".m4b", "audio/mp4a-latm"},
            {".m4p", "audio/mp4a-latm"},
            {".m4u", "video/vnd.mpegurl"},
            {".m4v", "video/x-m4v"},
            {".mov", "video/quicktime"},
            {".mp2", "audio/x-mpeg"},
            {".mp3", "audio/x-mpeg"},
            {".mp4", "video/mp4"},
            {".mpc", "application/vnd.mpohun.certificate"},
            {".mpe", "video/mpeg"},
            {".mpeg", "video/mpeg"},
            {".mpg", "video/mpeg"},
            {".mpg4", "video/mp4"},
            {".mpga", "audio/mpeg"},
            {".msg", "application/vnd.ms-outlook"},
            {".ogg", "audio/ogg"},
            {".pdf", "application/pdf"},
            {".png", "image/png"},
            {".pps", "application/vnd.ms-powerpoint"},
            {".ppt", "application/vnd.ms-powerpoint"},
            {".pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation"},
            {".prop", "text/plain"},
            {".rc", "text/plain"},
            {".rmvb", "audio/x-pn-realaudio"},
            {".rtf", "application/rtf"},
            {".sh", "text/plain"},
            {".tar", "application/x-tar"},
            {".tgz", "application/x-compressed"},
            {".txt", "text/plain"},
            {".wav", "audio/x-wav"},
            {".wma", "audio/x-ms-wma"},
            {".wmv", "audio/x-ms-wmv"},
            {".wps", "application/vnd.ms-works"},
            {".xml", "text/plain"},
            {".z", "application/x-compress"},
            {".zip", "application/x-zip-compressed"},
            {"", "*/*"}
    };

    /**
     * 获取跟目录
     *
     * @return
     */
    public static String getSDPath() {
        return Environment.getExternalStorageDirectory() + "/";
    }

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
            return String.format("%.1f G", (float) b / gb);
        } else if (b >= mb) {
            float f = (float) b / mb;
            return String.format(f > 100 ? "%.0f M" : "%.1f M", f);
        } else if (b >= kb) {
            float f = (float) b / kb;
            return String.format(f > 100 ? "%.0f K" : "%.1f K", f);
        } else
            return String.format("%d B", b);
    }

    /**
     * 获取文件单位聚合换算
     *
     * @param kb kb
     * @return
     */
    public static final String kbFormat(long kb) {
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

    /**
     * @data 创建时间:16/12/7
     * @author 创建人:lu.zhao
     * <p>
     * 创建一个文件，如果其所在目录不存在时，他的目录也会被跟着创建
     */
    public static File newFileWithPath(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return null;
        }

        int index = filePath.lastIndexOf(File.separator);

        String path = "";
        if (index != -1) {
            path = filePath.substring(0, index);
            if (!TextUtils.isEmpty(path)) {
                File file = new File(path.toString());
                // 如果文件夹不存在
                if (!file.exists() && !file.isDirectory()) {
                    boolean flag = file.mkdirs();
                    if (flag) {
                        LogUtils.i("httpFrame  threadName:" + Thread.currentThread().getName() + " 创建文件夹成功："
                                + file.getPath());
                    } else {
                        LogUtils.e("httpFrame  threadName:" + Thread.currentThread().getName() + " 创建文件夹失败："
                                + file.getPath());
                    }
                }
            }
        }
        return new File(filePath);
    }

    //判断文件是否存在
    public static boolean fileIsExists(String strFile) {
        try {
            File f = new File(strFile);
            if (!f.exists()) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * 根据文件后缀名获得对应的MIME类型。
     *
     * @param file
     */
    public static String getMIMEType(File file) {

        String type = "*/*";
        String fName = file.getName();
        //获取后缀名前的分隔符"."在fName中的位置。
        int dotIndex = fName.lastIndexOf(".");
        if (dotIndex < 0) {
            return type;
        }
    /* 获取文件的后缀名*/
        String end = fName.substring(dotIndex, fName.length()).toLowerCase();
        if (end == "") return type;
        //在MIME和文件类型的匹配表中找到对应的MIME类型。
        for (int i = 0; i < MIME_MapTable.length; i++) { //MIME_MapTable??在这里你一定有疑问，这个MIME_MapTable是什么？
            if (end.equals(MIME_MapTable[i][0]))
                type = MIME_MapTable[i][1];
        }
        return type;
    }

}
