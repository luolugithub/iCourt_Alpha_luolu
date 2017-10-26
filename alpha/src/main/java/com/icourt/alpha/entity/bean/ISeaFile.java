package com.icourt.alpha.entity.bean;

import android.support.annotation.Nullable;

import com.icourt.alpha.constants.SFileConfig;

import java.io.Serializable;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/9/12
 * version 2.1.0
 */
public interface ISeaFile extends Serializable {


    /**
     * id
     *
     * @return
     */
    String getSeaFileId();

    /**
     * 仓库id
     *
     * @return
     */
    String getSeaFileRepoId();

    /**
     * 全路径
     *
     * @return
     */
    String getSeaFileFullPath();


    /**
     * 版本标识别
     *
     * @return
     */
    @Nullable
    String getSeaFileVersionId();

    /**
     * 文件大小
     *
     * @return
     */
    long getSeaFileSize();


    /**
     * 权限
     *
     * @return
     */
    @SFileConfig.FILE_PERMISSION
    String getSeaFilePermission();

    /**
     * 下载的tag
     *
     * @return
     */
    String getSeaFileDownloadTag();
}
