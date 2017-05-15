package com.icourt.alpha.entity.bean;

import java.io.Serializable;

/**
 * Description  任务附件模型
 * Company Beijing icourt
 * author  lu.zhao  E-mail:zhaolu@icourt.cc
 * date createTime：17/5/15
 * version 2.0.0
 */

public class TaskAttachmentEntity implements Serializable {

    public String id;
    public String taskId;
    public String fileExt;
    public long fileSize;
    public PathInfoVoEntity pathInfoVo;

    public static class PathInfoVoEntity implements Serializable {
        public String repoId;
        public String filePath;
    }
}
