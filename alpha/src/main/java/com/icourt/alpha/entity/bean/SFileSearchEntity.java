package com.icourt.alpha.entity.bean;

import android.text.TextUtils;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/8/20
 * version 2.1.0
 */
public class SFileSearchEntity implements ISeaFile {
    /**
     * "repo_id": "f362f49f-084b-4940-ae01-49097dbbddee",
     * "name": "screen_image.jpg",
     * "oid": "e9c523a806090d2d8c01aa6dfba4a861b164369a",
     * "last_modified": 1503128706,
     * "content_highlight": "",
     * "fullpath": "/screen_image.jpg",
     * "repo_name": "aaa",
     * "is_dir": false,
     * "size": 129231
     */

    public String repo_id;
    public String name;
    public String oid;
    public long last_modified;
    public String content_highlight;
    public String fullpath;
    public String repo_name;
    public boolean is_dir;
    public long size;

    public boolean isSearchContent() {
        return !TextUtils.isEmpty(content_highlight);
    }

    @Override
    public String getSeaFileFullPath() {
        return fullpath;
    }

    @Override
    public String getSeaFileVersionId() {
        return null;
    }

    @Override
    public long getSeaFileSize() {
        return size;
    }

    @Override
    public String getSeaFilePermission() {
        return null;
    }

    @Override
    public String getSeaFileRepoId() {
        return repo_id;
    }
}
