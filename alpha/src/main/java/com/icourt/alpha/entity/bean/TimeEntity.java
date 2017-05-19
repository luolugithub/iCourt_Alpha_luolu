package com.icourt.alpha.entity.bean;

import android.text.TextUtils;

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
    public long timingSum;


    public static class ItemEntity implements Serializable,
            ILongFieldEntity,
            Cloneable {

        public static final ItemEntity singleInstace = new ItemEntity();

        public static final int TIMER_STATE_START = 0;
        public static final int TIMER_STATE_STOP = 1;

        public String suspensionTag;

        public String pkId;
        public String name;
        public String matterPkId;
        public String taskPkId;
        public long startTime;//计时的起始点
        public long endTime;
        public long useTime;
        public String createUserId;
        public long createTime;
        public long workDate;
        public int state;//计时的状态 0 正在计时  1 暂停就是结束状态
        public String workTypeId;
        public String officeId;
        public String matterName;
        public int timingCount;
        public String workTypeName;
        public String username;
        public String highLightName;
        public boolean timingValid;

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null) return false;
            if (getClass() != o.getClass())
                return false;
            final ItemEntity other = (ItemEntity) o;
            return TextUtils.equals(this.pkId, other.pkId);
        }

        @Override
        public Object clone() throws CloneNotSupportedException {
            return (ItemEntity) super.clone();
        }

        @Override
        public Long getCompareLongField() {
            return startTime;
        }

        @Override
        public String toString() {
            return "ItemEntity{" +
                    "suspensionTag='" + suspensionTag + '\'' +
                    ", pkId='" + pkId + '\'' +
                    ", name='" + name + '\'' +
                    ", matterPkId='" + matterPkId + '\'' +
                    ", taskPkId='" + taskPkId + '\'' +
                    ", startTime=" + startTime +
                    ", endTime=" + endTime +
                    ", useTime=" + useTime +
                    ", createUserId='" + createUserId + '\'' +
                    ", createTime=" + createTime +
                    ", workDate=" + workDate +
                    ", state=" + state +
                    ", workTypeId='" + workTypeId + '\'' +
                    ", officeId='" + officeId + '\'' +
                    ", matterName='" + matterName + '\'' +
                    ", timingCount=" + timingCount +
                    ", workTypeName='" + workTypeName + '\'' +
                    ", username='" + username + '\'' +
                    ", highLightName='" + highLightName + '\'' +
                    ", timingValid=" + timingValid +
                    '}';
        }
    }

}
