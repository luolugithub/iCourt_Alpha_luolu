package com.icourt.alpha.entity.bean;

import android.text.TextUtils;

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

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            ItemEntity itemEntity = (ItemEntity) o;

            return !TextUtils.isEmpty(id) && id.equals(itemEntity.id);
        }
    }

}
