package com.icourt.alpha.entity.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Description  任务检查项
 * Company Beijing icourt
 * author  lu.zhao  E-mail:zhaolu@icourt.cc
 * date createTime：17/5/12
 * version 2.0.0
 */

public class TaskCheckItemEntity implements Serializable {

    public List<TaskCheckItemEntity.ItemEntity> items;

    public static class ItemEntity {
        public String id;
        public String taskId;
        public String name;
        public boolean state;//是否完成     true:完成   false:未完成
    }

}
