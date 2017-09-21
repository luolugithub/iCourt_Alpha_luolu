package com.icourt.alpha.entity.bean;

import java.io.Serializable;

/**
 * Description  任务附件模型
 * Company Beijing icourt
 * author  lu.zhao  E-mail:zhaolu@icourt.cc
 * date createTime：17/5/15
 * version 2.0.0
 */

public class TaskAttachmentEntity implements ISeaFile {

    public String id;
    public String taskId;
    public String fileExt;
    public long fileSize;
    public PathInfoVoEntity pathInfoVo;
    public String filePermission;//rw,r

    @Override
    public String getSeaFileFullPath() {
        return pathInfoVo != null ? pathInfoVo.filePath : "";
    }

    @Override
    public String getSeaFileVersionId() {
        return null;
    }

    @Override
    public long getSeaFileSize() {
        return fileSize;
    }

    @Override
    public String getSeaFilePermission() {
        return filePermission;
    }

    @Override
    public String getSeaFileId() {
        return id;
    }

    @Override
    public String getSeaFileRepoId() {
        return pathInfoVo != null ? pathInfoVo.repoId : "";
    }

    public static class PathInfoVoEntity implements Serializable {
        public String repoId;
        public String filePath;
    }
}
