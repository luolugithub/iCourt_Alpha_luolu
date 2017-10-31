package com.icourt.alpha.entity.bean;

import java.io.Serializable;

/**
 * Description 筛选菜单item
 * Company Beijing icourt
 * author  lu.zhao  E-mail:zhaolu@icourt.cc
 * date createTime：17/8/4
 * version 2.0.0
 */

public class FilterDropEntity implements Serializable {

    public FilterDropEntity() {
    }

    public FilterDropEntity(String name, String count, int stateType) {
        this.name = name;
        this.count = count;
        this.stateType = stateType;
    }

    public String name;//名称
    public String count;//数量
    public int stateType;//状态；

}
