package com.icourt.alpha.entity.bean;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import com.icourt.alpha.db.convertor.IConvertModel;
import com.icourt.alpha.utils.DateUtils;

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


    public static class TaskItemEntity implements Serializable, MultiItemEntity, Cloneable {
        public String groupName;//任务所在分组名称
        public String groupId;//分组id
        public int groupTaskCount;//分组有多少个任务

        public String id;
        public String name;//任务名称
        public String parentId;//任务组id
        public String parentName;//任务组名称
        public String matterId;//项目id
        public String description;//任务描述
        public boolean state;//是否完成
        public long dueTime;//到期时间
        public long updateTime;//更新时间
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
        public String readUserIds;//查看过此任务的人的id

        public ParentFlowEntity parentFlow;//详情任务组信息
        public MatterEntity matter;//项目信息
        public CreateUserEntity createUser;//任务创建人
        public boolean valid;//是否有效 如果删除 返回false

        public List<AttendeeUserEntity> attendeeUsers;//任务相关人
        public List<String> right;//权限

        /**
         * 返回数据标记是任务还是任务组，给Adapter进行使用
         *
         * @return
         */
        @Override
        public int getItemType() {
            return type;
        }

        /**
         * 构建更新任务标题的json
         *
         * @return
         */
        @NonNull
        public static JsonObject createUpdateNameParam(@NonNull TaskItemEntity itemEntity, @NonNull String taskName) {
            JsonObject jsonObject = new JsonObject();
            if (itemEntity == null) return jsonObject;
            jsonObject.addProperty("id", itemEntity.id);
            jsonObject.addProperty("state", itemEntity.state);
            jsonObject.addProperty("name", TextUtils.isEmpty(taskName) ? "" : taskName);
            jsonObject.addProperty("parentId", itemEntity.parentId);
            jsonObject.addProperty("valid", true);
            jsonObject.addProperty("updateTime", DateUtils.millis());
            JsonArray jsonarr = new JsonArray();
            if (itemEntity.attendeeUsers != null) {
                if (itemEntity.attendeeUsers.size() > 0) {
                    for (TaskEntity.TaskItemEntity.AttendeeUserEntity attendeeUser : itemEntity.attendeeUsers) {
                        if (attendeeUser == null) continue;
                        jsonarr.add(attendeeUser.userId);
                    }
                }
                jsonObject.add("attendees", jsonarr);
            }
            return jsonObject;
        }

        /**
         * 构建更新任务描述的json
         *
         * @return
         */
        @NonNull
        public static JsonObject createUpdateDescParam(@NonNull TaskItemEntity itemEntity, @NonNull String taskDesc) {
            JsonObject jsonObject = new JsonObject();
            if (itemEntity == null) return jsonObject;
            jsonObject.addProperty("id", itemEntity.id);
            jsonObject.addProperty("state", itemEntity.state);
            jsonObject.addProperty("name", itemEntity.name);
            jsonObject.addProperty("parentId", itemEntity.parentId);
            jsonObject.addProperty("valid", true);
            jsonObject.addProperty("updateTime", DateUtils.millis());
            jsonObject.addProperty("description", TextUtils.isEmpty(taskDesc) ? "" : taskDesc);
            JsonArray jsonarr = new JsonArray();
            if (itemEntity.attendeeUsers != null) {
                if (itemEntity.attendeeUsers.size() > 0) {
                    for (TaskEntity.TaskItemEntity.AttendeeUserEntity attendeeUser : itemEntity.attendeeUsers) {
                        jsonarr.add(attendeeUser.userId);
                    }
                }
                jsonObject.add("attendees", jsonarr);
            }
            return jsonObject;
        }


        public static class MatterEntity
                implements Serializable, IConvertModel<ProjectEntity>, Cloneable {
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

            @Override
            public Object clone() throws CloneNotSupportedException {
                return super.clone();
            }
        }

        /**
         * 任务创建人
         */
        public static class CreateUserEntity implements Serializable, Cloneable {
            public String userId;
            public String userName;
            public String pic;

            @Override
            public Object clone() throws CloneNotSupportedException {
                return super.clone();
            }
        }

        /**
         * 任务相关人
         */
        public static class AttendeeUserEntity implements Serializable, Cloneable {
            @SerializedName(value = "userId", alternate = {"id"})
            public String userId;
            @SerializedName(value = "userName", alternate = {"name"})
            public String userName;
            public String pic;

            @Override
            public boolean equals(Object o) {
                if (this == o)
                    return true;
                if (o == null) return false;
                if (getClass() != o.getClass())
                    return false;
                final AttendeeUserEntity other = (AttendeeUserEntity) o;
                return TextUtils.equals(this.userId, other.userId);
            }

            @Override
            public Object clone() throws CloneNotSupportedException {
                return super.clone();
            }
        }

        public static class ParentFlowEntity implements Serializable, Cloneable {
            public String id;
            public String name;

            @Override
            public Object clone() throws CloneNotSupportedException {
                return super.clone();
            }
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null) return false;
            if (getClass() != o.getClass())
                return false;
            final TaskItemEntity other = (TaskItemEntity) o;
            return TextUtils.equals(this.id, other.id);
        }

        @Override
        public Object clone() throws CloneNotSupportedException {
            return super.clone();
        }

    }
}

