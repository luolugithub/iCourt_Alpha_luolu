package com.icourt.alpha.entity.bean;

import java.util.HashSet;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/9/11
 * version 2.1.0
 */
public interface ISeaFileSelectParams {
    int getRepoType();

    String getRepoName();

    String getDstRepoId();

    String getDstRepoDirPath();

    HashSet<FolderDocumentEntity> getSelectedFolderDocuments();
}
