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


    public String totalDate;
    public List<ItemEntity> items;


    public static class ItemEntity implements Serializable ,ILongFieldEntity {
        /**
         * pkId : 51EA68DC30EC11E79B4900163E30718E
         * name : 王储给华子的
         * matterPkId : 88EE3604C2DA11E69FB200163E162ADD
         * taskPkId : F1E3DABA30EB11E79B4900163E30718E
         * startTime : 1493917719000
         * endTime : null
         * useTime : 0
         * createUserId : 1E942F02A02111E69A3800163E0020D1
         * createTime : 1493917720000
         * workDate : 1493913600000
         * state : 0
         * workTypeId : null
         * officeId : null
         * matterName : #1215非诉2
         * timingCount : 0
         * workTypeName : null
         * username : 胡关荣
         * highLightName : null
         * timingValid : true
         */
        public String suspensionTag;

        public String pkId;
        public String name;
        public String matterPkId;
        public String taskPkId;
        public long startTime;
        public long endTime;
        public int useTime;
        public String createUserId;
        public long createTime;
        public long workDate;
        public String state;
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
