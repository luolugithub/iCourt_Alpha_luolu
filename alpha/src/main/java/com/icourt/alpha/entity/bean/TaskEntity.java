package com.icourt.alpha.entity.bean;

import com.icourt.alpha.db.convertor.IConvertModel;

import java.io.Serializable;
import java.util.List;

/**
 * Description  任务模型
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/21
 * version 1.0.0
 */
public class TaskEntity implements Serializable {

    public static final int UNATTENTIONED = 0;
    public static final int ATTENTIONED = 1;

    public String groupName;//分组名称(今天、即将到期)
    public String groupId;//分组id
    public int groupTaskCount;//分组内任务个数
    public List<TaskItemEntity> items;

    public static class TaskItemEntity implements Serializable {
        public String id;
        public String name;//任务名称
        public String parentId;//任务组id
        public String parentName;//任务组名称
        public String matterId;//项目id
        public String description;//任务描述
        public boolean state;//是否完成
        public long dueTime;//到期时间
        public long timingSum;//总计时
        public long assignTime;//任务分配时间
        public long createTime;//任务创建时间
        public int itemCount;//子任务总数
        public int doneItemCount;//完成子任务数
        public int attachmentCount;//附件总数
        public int commentCount;//评论总数
        public int attentioned;//关注   0:未关注  1:关注
        public int type;//类型   0:任务  1:任务组
        public boolean isTiming;

        public ParentFlowEntity parentFlow;//详情任务组信息
        public MatterEntity matter;//项目信息
        public CreateUserEntity createUser;//任务创建人

        public List<AttendeeUserEntity> attendeeUsers;//任务相关人

        public static class MatterEntity
                implements Serializable, IConvertModel<ProjectEntity> {
            public String id;
            public String name;
            public String matterType;

            @Override
            public ProjectEntity convert2Model() {
                ProjectEntity projectEntity = new ProjectEntity();
                projectEntity.pkId = id;
                projectEntity.name = name;
                projectEntity.matterType = matterType;
                return projectEntity;
            }
        }

        /**
         * 任务创建人
         */
        public static class CreateUserEntity implements Serializable {
            public String userId;
            public String userName;
            public String pic;
        }

        /**
         * 任务相关人
         */
        public static class AttendeeUserEntity implements Serializable {
            public String userId;
            public String userName;
            public String pic;
        }

        public static class ParentFlowEntity implements Serializable {
            public String id;
            public String name;
        }


    }


}
