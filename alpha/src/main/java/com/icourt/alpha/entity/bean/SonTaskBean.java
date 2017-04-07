package com.icourt.alpha.entity.bean;

import java.io.Serializable;

/**
 * @data 创建时间:16/11/30
 *
 * @author 创建人:lu.zhao
 *
 * 子任务
 */

public class SonTaskBean implements Serializable{


    /**
     * createTime : 2016-11-29T05:20:46.104Z
     * createUserId : string
     * id : string
     * name : string
     * state : true
     * taskId : string
     * updateTime : 2016-11-29T05:20:46.104Z
     */

    private String createTime;
    private String createUserId;
    private String id;
    private String name;
    private boolean state;
    private String taskId;
    private String updateTime;

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(String createUserId) {
        this.createUserId = createUserId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }
}
