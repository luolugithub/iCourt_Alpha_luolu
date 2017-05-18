package com.icourt.alpha.entity.bean;

import java.io.Serializable;

/**
 * Description  项目概览基本信息模型
 * Company Beijing icourt
 * author  lu.zhao  E-mail:zhaolu@icourt.cc
 * date createTime：17/5/17
 * version 2.0.0
 */

public class ProjectBasicItemEntity implements Serializable{

    public static final int PROJECT_NAME_TYPE = 1;//项目名称
    public static final int PROJECT_TYPE_TYPE = 2;//项目类型
    public static final int PROJECT_DEPARTMENT_TYPE = 3;//负责部门
    public static final int PROJECT_CLIENT_TYPE = 4;//客户
    public static final int PROJECT_OTHER_PERSON_TYPE = 5;//其他当事人
    public static final int PROJECT_TIME_TYPE = 6;//项目时间
    public static final int PROJECT_ANYUAN_LAWYER_TYPE = 7;//案源律师

    public String key;
    public String value;
    public int type;
}
