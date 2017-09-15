package com.icourt.alpha.entity.bean;

import com.icourt.alpha.constants.SFileConfig;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/9/11
 * version 2.1.0
 */
public class SeaFileSelectParam implements Serializable, ISeaFileSelectParams {
    @SFileConfig.REPO_TYPE
    int repoType;
    String repoName;
    String dstRepoId;
    String dstRepoDirPath;
    ArrayList<FolderDocumentEntity> selectedFolderDocumentEntities;

    public SeaFileSelectParam(int repoType, String repoName, String dstRepoId, String dstRepoDirPath, ArrayList<FolderDocumentEntity> selectedFolderDocumentEntities) {
        this.repoType = repoType;
        this.repoName = repoName;
        this.dstRepoId = dstRepoId;
        this.dstRepoDirPath = dstRepoDirPath;
        this.selectedFolderDocumentEntities = selectedFolderDocumentEntities;
    }

    @Override
    public int getRepoType() {
        return repoType;
    }

    @Override
    public String getRepoName() {
        return repoName;
    }

    @Override
    public String getDstRepoId() {
        return dstRepoId;
    }

    @Override
    public String getDstRepoDirPath() {
        return dstRepoDirPath;
    }

    @Override
    public ArrayList<FolderDocumentEntity> getSelectedFolderDocuments() {
        if (selectedFolderDocumentEntities == null) {
            selectedFolderDocumentEntities = new ArrayList<>();
        }
        return selectedFolderDocumentEntities;
    }
}
