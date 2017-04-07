package com.icourt.alpha.entity.bean;

import java.io.Serializable;

/**
 * @author 创建人:lu.zhao
 *         任务组
 * @data 创建时间:16/11/28
 */


public class TaskGroupBean implements Serializable {

    private String id;
    private String name;
    private String taskCount;

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

    public String getTaskCount() {
        return taskCount;
    }

    public void setTaskCount(String taskCount) {
        this.taskCount = taskCount;
    }
}
