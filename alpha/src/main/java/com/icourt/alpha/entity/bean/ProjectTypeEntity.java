package com.icourt.alpha.entity.bean;

import java.io.Serializable;

/**
 * Description  项目类型实体
 * Company Beijing icourt
 * author  lu.zhao  E-mail:zhaolu@icourt.cc
 * date createTime：17/8/8
 * version 2.0.0
 */

public class ProjectTypeEntity implements Serializable {

    public String name;
    public int type;// [0:争议解决 1:非诉专项 2:常年顾问 3:内部事务] ,
    public int icon;

    public ProjectTypeEntity() {
    }

    public ProjectTypeEntity(String name, int type, int icon) {
        this.name = name;
        this.type = type;
        this.icon = icon;
    }
}
