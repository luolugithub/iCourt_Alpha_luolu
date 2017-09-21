package com.icourt.alpha.constants;

import android.os.Environment;
import android.text.TextUtils;

import com.icourt.alpha.entity.bean.ISeaFile;
import com.icourt.alpha.utils.BugUtils;
import com.icourt.alpha.utils.FileUtils;
import com.icourt.alpha.utils.StringUtils;

import java.io.File;

/**
 * Description  下载路径配置
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/9/19
 * version 2.1.0
 */
public class DownloadConfig {

    //  app更新安装包保存路径              /alpha_download/new_apk/
    //  多用户默认保存路径                 /alpha_download/userId/
    //  多用户文件全路径(seafile)保存路径   /alpha_download/userId/repoId/x_dir/yyy_versionId.doc

    //下载文件根目录
    public static final String FILE_DOWNLOAD_ROOT_DIR = "alpha_download";

    //App更新的文件根目录
    private static final String FILE_DOWNLOAD_APK_DIR = FILE_DOWNLOAD_ROOT_DIR + File.separator + "new_apk";


    /**
     * 获取app 安装包的下载目录
     *
     * @return
     */
    public static final String getAppDownloadDir() {
        try {
            return new StringBuilder(Environment.getExternalStorageDirectory().getAbsolutePath())
                    .append(File.separator)
                    .append(FILE_DOWNLOAD_APK_DIR)
                    .toString();
        } catch (Exception e) {
            e.printStackTrace();
            BugUtils.bugSync("getAppDownloadDir exception", e);
        }
        return null;
    }

    /**
     * 先获取文件读写权限
     * 获取seaFile 保存路径
     *
     * @param userId   用户id
     * @param iSeaFile
     * @return
     */
    public static final String getSeaFileDownloadPath(String userId, ISeaFile iSeaFile) {
        try {
            //如果有版本 标记版本号
            String fileFullPath = iSeaFile.getSeaFileFullPath();
            if (!TextUtils.isEmpty(iSeaFile.getSeaFileVersionId())) {
                String fileNameWithoutSuffix = FileUtils.getFileNameWithoutSuffix(fileFullPath);
                String fileSuffix = FileUtils.getFileSuffix(fileFullPath);
                fileFullPath = String.format("%s_%s%s", fileNameWithoutSuffix, iSeaFile.getSeaFileVersionId().hashCode(), fileSuffix);
            }
            return getFormatedFileName(new StringBuilder(Environment.getExternalStorageDirectory().getAbsolutePath())
                    .append(File.separator)
                    .append(FILE_DOWNLOAD_ROOT_DIR)
                    .append(File.separator)
                    .append(userId)
                    .append(File.separator)
                    .append(iSeaFile.getSeaFileRepoId())
                    .append(File.separator)
                    .append(fileFullPath)
                    .toString());
        } catch (Exception e) {
            e.printStackTrace();
            BugUtils.bugSync("getSeaFileDownloadDir exception",
                    new StringBuilder()
                            .append("userId:")
                            .append(userId)
                            .append("\nseaFileRepoId:")
                            .append(iSeaFile.getSeaFileRepoId())
                            .append("\nseaFileDir:")
                            .append(iSeaFile.getSeaFileFullPath())
                            .append("\nexception:")
                            .append(StringUtils.throwable2string(e))
                            .toString());
        }
        return null;
    }



    /**
     * //去掉"//"
     *
     * @param name
     * @return
     */
    private static final String getFormatedFileName(String name) {
        String illegalSeparator = String.format("%s%s", File.separator, File.separator);
        if (!TextUtils.isEmpty(name)
                && name.contains(illegalSeparator)) {
            name = name.replace(illegalSeparator, File.separator);
        }
        return name;
    }

    /**
     * 获取一般文件保存路径
     *
     * @param userId
     * @param fileName
     * @return
     */
    public static final String getCommFileDownloadPath(String userId, String fileName) {
        try {
            return getFormatedFileName(new StringBuilder(Environment.getExternalStorageDirectory().getAbsolutePath())
                    .append(File.separator)
                    .append(FILE_DOWNLOAD_ROOT_DIR)
                    .append(File.separator)
                    .append(userId)
                    .append(File.separator)
                    .append(fileName)
                    .toString());
        } catch (Exception e) {
            e.printStackTrace();
            BugUtils.bugSync("getCommFileDownloadPath exception",
                    new StringBuilder()
                            .append("userId:")
                            .append(userId)
                            .append("\nfileName:")
                            .append(fileName)
                            .append(StringUtils.throwable2string(e))
                            .toString());
        }
        return null;
    }

}
