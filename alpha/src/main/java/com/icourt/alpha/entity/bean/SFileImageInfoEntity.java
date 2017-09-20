package com.icourt.alpha.entity.bean;

import android.support.annotation.Nullable;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/5/31
 * version 1.0.0
 */
public class SFileImageInfoEntity implements ISeaFile {
    public SFileImageInfoEntity(long size, String path, String name, String repo_id, String thumb, int width, int height) {
        this.size = size;
        this.path = path;
        this.name = name;
        this.repo_id = repo_id;
        this.thumb = thumb;
        this.width = width;
        this.height = height;
    }

    public long size;
    public String path;
    public String name;
    public String repo_id;
    public String thumb;
    public int width;
    public int height;

    public long chatMsgId;//聊天msg id

    @Override
    public String getSeaFileRepoId() {
        return repo_id;
    }

    @Override
    public String getSeaFileFullPath() {
        return String.format("%s/%s",path,name);
    }

    @Nullable
    @Override
    public String getSeaFileVersionId() {
        return null;
    }

    @Override
    public long getSeaFileSize() {
        return size;
    }
}
