package com.icourt.alpha.entity.bean;

import java.util.List;

/**
 * Description 计时模型
 * Company Beijing icourt
 * author  lu.zhao  E-mail:zhaolu@icourt.cc
 * date createTime：17/5/4
 * version 2.0.0
 */

public class TimeEntity {

    public long totalTime;//累计计时
    public long timeDate;//计时日期
    public List<ItemEntity> itemEntities;

    public static class ItemEntity {
        public String id;
        public String timeDes;//计时描述
        public String timeUserName;//计时人名称
        public String timeUserPic;//计时人头像
        public String timeType;//计时类型
        public long timeDuration;//计时时长
        public long startTime;//开始计时时间
        public long stopTime;//结束计时时间
    }

}
