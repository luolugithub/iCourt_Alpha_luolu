package com.icourt.alpha.widget.manager;

import android.os.Environment;

import com.icourt.alpha.utils.FileUtils;

import java.io.File;

import static com.icourt.alpha.constants.DownloadConfig.FILE_DOWNLOAD_ROOT_DIR;

/**
 * @author 创建人:lu.zhao
 *         <p>
 *         清除缓存
 * @data 创建时间:16/12/8
 */

public class DataCleanManager {

    /**
     * 获取本地缓存文件
     *
     * @param userId
     * @return
     */
    private static File getCacheFile(String userId) {
        String filename = new StringBuilder(Environment.getExternalStorageDirectory().getAbsolutePath())
                .append(File.separator)
                .append(FILE_DOWNLOAD_ROOT_DIR)
                .append(File.separator)
                .append(userId).toString();
        return new File(filename);
    }

    /**
     * 获取本地缓存大小
     *
     * @param userId
     * @return
     * @throws Exception
     */
    public static String getTotalCacheSize(String userId) throws Exception {
        long cacheSize = getFolderSize(getCacheFile(userId));
        return FileUtils.bFormat(cacheSize);
    }

    /**
     * 清除userid 用户本地缓存文件
     *
     * @param userId
     * @return
     */
    public static boolean clearAllCache(String userId) {
        boolean isSuccee = false;
        File file = getCacheFile(userId);
        if (file.exists()) {
            isSuccee = deleteDir(file);
        } else {
            isSuccee = true;
        }

        return isSuccee;
    }

    private static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            if (children != null) {
                for (int i = 0; i < children.length; i++) {
                    boolean success = deleteDir(new File(dir, children[i]));
                    if (!success) {
                        return false;
                    }
                }
            }
        }
        return dir.delete();
    }

    // 获取文件
    //Context.getExternalFilesDir() --> SDCard/Android/data/你的应用的包名/files/ 目录，一般放一些长时间保存的数据
    //Context.getExternalCacheDir() --> SDCard/Android/data/你的应用包名/cache/目录，一般存放临时缓存数据
    public static long getFolderSize(File file) throws Exception {
        long size = 0;
        try {
            File[] fileList = file.listFiles();
            if (fileList == null || fileList.length <= 0) return 0;
            for (int i = 0; i < fileList.length; i++) {
                // 如果下面还有文件
                if (fileList[i].isDirectory()) {
                    size = size + getFolderSize(fileList[i]);
                } else {
                    size = size + fileList[i].length();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }
}
