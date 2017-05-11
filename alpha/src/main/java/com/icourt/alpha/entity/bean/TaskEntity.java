package com.icourt.alpha.entity.bean;

import java.util.List;

/**
 * Description  任务模型
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/21
 * version 1.0.0
 */
public class TaskEntity {

    public String groupName;//分组名称(今天、即将到期)
    public int groupTaskCount;//分组内任务个数
    public List<TaskItemEntity> items;

    public static class TaskItemEntity {
        public String id;
        public String name;//任务名称
        public String parentName;//任务组名称
        public boolean state;//是否完成
        public long dueTime;//到期时间
        public long timingSum;//总计时
        public int itemCount;//子任务总数
        public int doneItemCount;//完成子任务数
        public int attachmentCount;//附件总数
        public int commentCount;//评论总数

        public MatterEntity matterEntity;//项目信息

        public List<AttendeeUserEntity> attendeeUserEntities;//任务相关人

        public static class MatterEntity {
            public String id;
            public String name;
            public String matterType;
        }

        /**
         * 任务相关人
         */
        public static class AttendeeUserEntity {
            public String userId;
            public String userName;
            public String pic;
        }
    }


}
