package com.icourt.alpha.entity.bean;

import android.support.annotation.Nullable;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/5/31
 * version 1.0.0
 */
public class ChatFileInfoEntity implements IChatImageFile {

    public ChatFileInfoEntity(long size, String path, String name, String repo_id, String thumbPic, String middlePic, long chatMsgId) {
        this.size = size;
        this.path = path;
        this.name = name;
        this.repo_id = repo_id;
        this.thumbPic = thumbPic;
        this.middlePic = middlePic;
        this.chatMsgId = chatMsgId;
    }

    private long size;
    private String path;
    private String name;
    private String repo_id;
    private String thumbPic;//缩略图
    private String middlePic;//中图
    private long chatMsgId;//聊天msg id

    public void setChatMsgId(long chatMsgId) {
        this.chatMsgId = chatMsgId;
    }


    @Override
    public String getSeaFileId() {
        return null;
    }

    @Override
    public String getSeaFileRepoId() {
        return repo_id;
    }

    @Override
    public String getSeaFileFullPath() {
        return String.format("%s/%s", path, name);
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

    @Override
    public String getSeaFilePermission() {
        return null;
    }

    @Override
    public String getSeaFileDownloadTag() {
        return null;
    }

    @Override
    public long getChatMsgId() {
        return chatMsgId;
    }

    @Override
    public String getChatThumbPic() {
        return thumbPic;
    }

    @Override
    public String getChatMiddlePic() {
        return middlePic;
    }
}
