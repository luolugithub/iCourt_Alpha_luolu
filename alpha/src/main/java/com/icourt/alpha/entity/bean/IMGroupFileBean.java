package com.icourt.alpha.entity.bean;

import java.io.Serializable;

/**
 * @data 创建时间:17/1/9
 *
 * @author 创建人:lu.zhao
 *
 * 组内文件
 */

public class IMGroupFileBean implements Serializable{


    /**
     * id : 3
     * groupId : 83
     * createId : A2EFA3E2A02011E69A3800163E0020D1
     * createName : 胡谢进
     * fileName : 屏幕快照 2016-12-05 上午11.17.36.png
     * fileId : 640
     * createDate : 1482323973000
     * updateDate : 1482323973000
     * msgId : 0
     * fileSize : null
     */

    private int id;
    private int groupId;
    private String createId;
    private String createName;
    private String fileName;
    private String fileId;
    private long createDate;
    private long updateDate;
    private int msgId;
    private long fileSize;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public String getCreateId() {
        return createId;
    }

    public void setCreateId(String createId) {
        this.createId = createId;
    }

    public String getCreateName() {
        return createName;
    }

    public void setCreateName(String createName) {
        this.createName = createName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public long getCreateDate() {
        return createDate;
    }

    public void setCreateDate(long createDate) {
        this.createDate = createDate;
    }

    public long getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(long updateDate) {
        this.updateDate = updateDate;
    }

    public int getMsgId() {
        return msgId;
    }

    public void setMsgId(int msgId) {
        this.msgId = msgId;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }
}
