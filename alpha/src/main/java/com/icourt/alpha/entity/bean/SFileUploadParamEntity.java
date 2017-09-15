package com.icourt.alpha.entity.bean;

import java.util.List;

/**
 * Description  sfile 文件上传 参数模型实体
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/9/9
 * version 2.1.0
 */
public class SFileUploadParamEntity {
    public String seaFileRepoId;
    public String seaFileDirPath;
    public List<String> filePaths;
    public String uploadServerUrl;

    public SFileUploadParamEntity(String seaFileRepoId, String seaFileDirPath, List<String> filePaths) {
        this.seaFileRepoId = seaFileRepoId;
        this.seaFileDirPath = seaFileDirPath;
        this.filePaths = filePaths;
    }
}
