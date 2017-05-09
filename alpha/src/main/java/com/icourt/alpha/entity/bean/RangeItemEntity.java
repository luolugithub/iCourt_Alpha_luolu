package com.icourt.alpha.entity.bean;

/**
 * Description 项目详情：概览程序信息item模版
 * Company Beijing icourt
 * author  lu.zhao  E-mail:zhaolu@icourt.cc
 * date createTime：17/5/5
 * version 2.0.0
 */

public class RangeItemEntity {

    public String itemName;
    public String itemValue;
    public int itemType;

    public RangeItemEntity() {
    }

    public RangeItemEntity(String itemName, String itemValue) {
        this.itemName = itemName;
        this.itemValue = itemValue;
    }

    public RangeItemEntity(String itemName, String itemValue, int itemType) {
        this.itemName = itemName;
        this.itemValue = itemValue;
        this.itemType = itemType;
    }
}
