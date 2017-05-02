package com.icourt.alpha.entity.bean;

import java.util.List;

/**
 * Description  项目实体
 * Company Beijing icourt
 * author  lu.zhao  E-mail:zhaolu@icourt.cc
 * date createTime：17/5/2
 * version 2.0.0
 */

public class ProjectEntity {


    /**
     * pkId : 8E9E19E208B711E79B4900163E30718E
     * name : &gt;
     * status : 2
     * matterType : 0
     * statusName : 进行中
     * sumTime : 1489497012000
     * logTime : 1489497012000
     * logDescription :
     * permission : 0
     * clients : []
     * unfinishTask : 0
     * allTask : 0
     * openDate : 1489497012000
     * closeDate : 1489497012000
     * isJoin : 1
     * myStar : 0
     */

    public String pkId;
    public String name;
    public String status;
    public String matterType;
    public String statusName;
    public long sumTime;
    public long logTime;
    public String logDescription;
    public int permission;
    public int unfinishTask;
    public int allTask;
    public long openDate;
    public long closeDate;
    public int isJoin;
    public int myStar;
    public List<?> clients;


    @Override
    public String toString() {
        return "ProjectEntity{" +
                "pkId='" + pkId + '\'' +
                ", name='" + name + '\'' +
                ", status='" + status + '\'' +
                ", matterType='" + matterType + '\'' +
                ", statusName='" + statusName + '\'' +
                ", sumTime=" + sumTime +
                ", logTime=" + logTime +
                ", logDescription='" + logDescription + '\'' +
                ", permission=" + permission +
                ", unfinishTask=" + unfinishTask +
                ", allTask=" + allTask +
                ", openDate=" + openDate +
                ", closeDate=" + closeDate +
                ", isJoin=" + isJoin +
                ", myStar=" + myStar +
                ", clients=" + clients +
                '}';
    }
}
