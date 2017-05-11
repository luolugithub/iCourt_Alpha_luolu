package com.icourt.alpha.entity.bean;

import com.icourt.alpha.widget.comparators.ILongFieldEntity;

import java.io.Serializable;
import java.util.List;

/**
 * Description 计时模型
 * Company Beijing icourt
 * author  lu.zhao  E-mail:zhaolu@icourt.cc
 * date createTime：17/5/4
 * version 2.0.0
 */

public class TimeEntity implements Serializable {
    public static final int TIMER_STATE_ING_TYPE = 0;//正在计时type
    public static final int TIMER_STATE_END_TYPE = 1;//未计时type

    public String totalDate;
    public List<ItemEntity> items;


    public static class ItemEntity implements Serializable, ILongFieldEntity {

        public String suspensionTag;

        public String pkId;
        public String name;
        public String matterPkId;
        public String taskPkId;
        public long startTime;//计时的起始点
        public long endTime;
        public int useTime;
        public String createUserId;
        public long createTime;
        public long workDate;
        public int state;//计时的状态
        public String workTypeId;
        public String officeId;
        public String matterName;
        public int timingCount;
        public String workTypeName;
        public String username;
        public String highLightName;
        public boolean timingValid;


        @Override
        public boolean equals(Object obj) {
            return super.equals(obj);
        }

        @Override
        public Long getCompareLongField() {
            return startTime;
        }
    }

}
