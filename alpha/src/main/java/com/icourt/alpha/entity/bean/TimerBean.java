package com.icourt.alpha.entity.bean;

import java.io.Serializable;

/**
 * Created by icourt on 16/11/21.
 */

public class TimerBean implements Serializable {


    /**
     * createTime : 2016-11-18T11:26:12.781Z
     * createUserId : string
     * endTime : 2016-11-18T11:26:12.781Z
     * matterPkId : string
     * name : string
     * pkId : string
     * startTime : 2016-11-18T11:26:12.781Z
     * state : 0
     * useTime : 0
     * workDate : 2016-11-18T11:26:12.781Z
     */

    private String createTime ;
    private String createUserId ;
    private String endTime = "";
    private String matterPkId ;
    private String name = "";
    private String pkId ;
    private String startTime = "";
    private int state = 0;
    private long useTime ;
    private String workDate ;
    private String workTypeId;
    private String workTypeName;

    private String matterName ;

    public String getTaskPkId() {
        return taskPkId;
    }

    public void setTaskPkId(String taskPkId) {
        this.taskPkId = taskPkId;
    }

    private String taskPkId ;
    private long timingCount;

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

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getMatterPkId() {
        return matterPkId;
    }

    public void setMatterPkId(String matterPkId) {
        this.matterPkId = matterPkId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPkId() {
        return pkId;
    }

    public void setPkId(String pkId) {
        this.pkId = pkId;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public long getUseTime() {
        return useTime;
    }

    public void setUseTime(long useTime) {
        this.useTime = useTime;
    }

    public String getWorkDate() {
        return workDate;
    }

    public void setWorkDate(String workDate) {
        this.workDate = workDate;
    }

    public String getMatterName() {
        return matterName;
    }

    public void setMatterName(String matterName) {
        this.matterName = matterName;
    }

    public long getTimingCount() {
        return timingCount;
    }

    public void setTimingCount(long timingCount) {
        this.timingCount = timingCount;
    }

    public String getWorkTypeId() {
        return workTypeId;
    }

    public void setWorkTypeId(String workTypeId) {
        this.workTypeId = workTypeId;
    }

    public String getWorkTypeName() {
        return workTypeName;
    }

    public void setWorkTypeName(String workTypeName) {
        this.workTypeName = workTypeName;
    }
}
