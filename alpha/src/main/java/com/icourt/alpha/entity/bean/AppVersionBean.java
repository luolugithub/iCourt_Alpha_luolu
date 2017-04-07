package com.icourt.alpha.entity.bean;

import java.io.Serializable;
import java.util.List;

/**
 * @author 创建人:lu.zhao
 *         <p>
 *         版本配置
 * @data 创建时间:17/1/5
 */

public class AppVersionBean implements Serializable {


    /**
     * version : v1.7.3
     * type : 1
     * list : ["修复了部分手机无法登录的bug","修复了长时间登录后无法看到任务和计时的bug","新增了智聊的功能，快聊起来吧"]
     */

    private String version;//移动客户端当前版本
    private int type;//更新的类型：1为推荐更新，2为强制更新
    private String downloadUrl;//下载地址
    private List<String> list;//修复的更新列表
    private String content;

    private String versionShort;
    private String build;


    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public List<String> getList() {
        return list;
    }

    public void setList(List<String> list) {
        this.list = list;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getVersionShort() {
        return versionShort;
    }

    public void setVersionShort(String versionShort) {
        this.versionShort = versionShort;
    }

    public String getBuild() {
        return build;
    }

    public void setBuild(String build) {
        this.build = build;
    }
}
