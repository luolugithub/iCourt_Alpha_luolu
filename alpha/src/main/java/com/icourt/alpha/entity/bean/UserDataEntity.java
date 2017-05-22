package com.icourt.alpha.entity.bean;

import java.io.Serializable;

/**
 * Description  用户数据
 * Company Beijing icourt
 * author  lu.zhao  E-mail:zhaolu@icourt.cc
 * date createTime：17/5/22
 * version 2.0.0
 */

public class UserDataEntity implements Serializable {

    public long timingCountToday;//今日计时
    public long timingCountMonth;//本月计时

    public int taskMonthConut;//本月任务总数
    public int taskMonthConutDone;//本月完成任务数
}
