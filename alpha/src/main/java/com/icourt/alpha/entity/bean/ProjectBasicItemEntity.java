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

    public String key;
    public String value;
    public int type;

    public ProjectBasicItemEntity() {
    }

    public ProjectBasicItemEntity(String key, String value, int type) {
        this.key = key;
        this.value = value;
        this.type = type;
    }
}
