package com.icourt.alpha.entity.event;

/**
 * Description  文件/文件夹 重命名事件
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/9/25
 * version 2.1.0
 */
public class FileRenameEvent {

    public String seaFileRepoId;//资料库id
    public boolean isDir;//是否是目录
    public String oldFullPath;//修改前的路径
    public String newFullPath;//修改后的路径

    public FileRenameEvent(String seaFileRepoId, boolean isDir, String oldFullPath, String newFullPath) {
        this.seaFileRepoId = seaFileRepoId;
        this.isDir = isDir;
        this.oldFullPath = oldFullPath;
        this.newFullPath = newFullPath;
    }
}
