package com.icourt.alpha.interfaces;

import com.icourt.alpha.entity.bean.FolderDocumentEntity;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/9/12
 * version 2.1.0
 */
public interface OnSeaFileSelectListener {
    /**
     * 选中
     *
     * @param folderDocumentEntity
     */
    void onSeaFileSelect(FolderDocumentEntity folderDocumentEntity);

    /**
     * 取消选中
     *
     * @param folderDocumentEntity
     */
    void onSeaFileSelectCancel(FolderDocumentEntity folderDocumentEntity);
}
