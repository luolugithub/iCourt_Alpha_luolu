package com.icourt.alpha.entity.bean;

import java.io.Serializable;

/**
 * @author 创建人:lu.zhao
 *         <p>
 *         文档
 * @data 创建时间:16/12/6
 */

public class FileBean implements Serializable {

    /**
     * ownerId : null
     * fileName : 6.jpg
     * filePath : /iCourt/110902个人pppp等与北京合众思壮科技股份等劳动争议人
     * contenttype : jpg
     * contentlength : 0
     * permission : null
     * modifyNumber : null
     * downloads : null
     * updTime : null
     * fileSize : 143956
     * sharelink : null
     * qrCode : null
     * username : 胡关荣
     */

    private Object ownerId;
    private String fileName;
    private String filePath;
    private String contenttype;
    private int contentlength;
    private Object permission;
    private Object modifyNumber;
    private Object downloads;
    private Object updTime;
    private String fileSize;
    private Object sharelink;
    private Object qrCode;
    private String username;

    private String sortLetters;  //显示数据拼音的首字母

    public Object getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Object ownerId) {
        this.ownerId = ownerId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getContenttype() {
        return contenttype;
    }

    public void setContenttype(String contenttype) {
        this.contenttype = contenttype;
    }

    public int getContentlength() {
        return contentlength;
    }

    public void setContentlength(int contentlength) {
        this.contentlength = contentlength;
    }

    public Object getPermission() {
        return permission;
    }

    public void setPermission(Object permission) {
        this.permission = permission;
    }

    public Object getModifyNumber() {
        return modifyNumber;
    }

    public void setModifyNumber(Object modifyNumber) {
        this.modifyNumber = modifyNumber;
    }

    public Object getDownloads() {
        return downloads;
    }

    public void setDownloads(Object downloads) {
        this.downloads = downloads;
    }

    public Object getUpdTime() {
        return updTime;
    }

    public void setUpdTime(Object updTime) {
        this.updTime = updTime;
    }

    public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }

    public Object getSharelink() {
        return sharelink;
    }

    public void setSharelink(Object sharelink) {
        this.sharelink = sharelink;
    }

    public Object getQrCode() {
        return qrCode;
    }

    public void setQrCode(Object qrCode) {
        this.qrCode = qrCode;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSortLetters() {
        return sortLetters;
    }

    public void setSortLetters(String sortLetters) {
        this.sortLetters = sortLetters;
    }
}
