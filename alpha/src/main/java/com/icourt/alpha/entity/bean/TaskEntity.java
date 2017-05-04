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

    public String id;
    public String groupName;//分组名称(今天、即将到期)
    public int groupTaskCount;//分组内任务个数
    public List<TaskItemEntity> taskItemEntitys;
    public List<AttendeesEntity> attendeesEntity;
    public MatterEntity matter;

    public static class MatterEntity {
        public String id;
        public String name;
        public int matterType;
    }

    public static class TaskItemEntity {
        public String name;//任务名称
        public String taskGroupName;//任务组名称
        public long dueTime;//到期时间
        public int itemTaskCount;//子任务总数
        public int doneItemTaskCount;//完成子任务数
        public int documentCount;//附件总数
        public int commentCount;//评论总数
    }

    /**
     * 任务相关人
     */
    public static class AttendeesEntity {
        public String id;
        public String pic;
    }
}
