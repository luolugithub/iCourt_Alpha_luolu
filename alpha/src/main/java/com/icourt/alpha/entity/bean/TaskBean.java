package com.icourt.alpha.entity.bean;

import java.io.Serializable;
import java.util.List;

/**
 * @data 创建时间:16/11/28
 *
 * @author 创建人:lu.zhao
 *
 * 任务bean
 */

public class TaskBean implements Serializable {


    /**
     * id : F3714E51B48B11E6992300163E162ADD
     * matterId : 5EC208BAB0D311E6992300163E162ADD
     * name : 无数任务
     * description : null
     * taskOrder : 0
     * state : true
     * valid : true
     * assignTo : CABE921084B111E6992300163E162ADD
     * parentId : 4E5FFBFFA95511E6992300163E162ADD
     * type : 0
     * startTime : null
     * endTime : null
     * dueTime : null
     * createUserId : CABE921084B111E6992300163E162ADD
     * createTime : 1480242385000
     * updateTime : 1480244111000
     * notifyType : null
     * assignTime : null
     * assignUserId : null
     * attendees : null
     * matter : {"id":"5EC208BAB0D311E6992300163E162ADD","name":"110902个人pppp等与北京合众思壮科技股份有限公司等劳动争议、人事争议一审","matterType":"0"}
     * assignToUser : {"userId":"CABE921084B111E6992300163E162ADD","userName":"克维","pic":"http://wx.qlogo.cn/mmopen/Q3auHgzwzM5LZ3RYldDOBiamerpEGhrAcS3xicico73ODyfh7MeEtoiaicP4ibiaejQTv2dibd18lqaLHBFf6CZ0CA4jUtibnXbybkYl50GlfQZYPFH4/0"}
     * parentFlow : {"id":"4E5FFBFFA95511E6992300163E162ADD","name":"无聊"}
     * attendeeUsers : []
     * contain : 0
     * commentCount : 0
     */

    private String id;
    private String matterId;//项目id ,
    private String name;//任务名称
    private Object description;//任务描述 ,
    private int taskOrder;
    private boolean state;//任务是否完成;1表示完成；0表示没有完成 ,
    private boolean valid;//任务是否被删除;1表示未删除；0表示删除
    private String assignTo;//负责人
    private String parentId;//任务的流程id ,
    private int type;//流程或任务0表示任务,1表示流程 ,
    private Object startTime;//任务的开始时间 ,
    private Object endTime;//任务的结束时间 ,
    private Object dueTime;//任务的过期时间 ,
    private String createUserId;
    private long createTime;
    private long updateTime;//修改时间 ,
    private Object notifyType;//通知类型;1表示一小时前；2表示一天前；3表示一周前 ,
    private Object assignTime;//分配时间 ,
    private Object assignUserId;//分配人 ,
    private Object attendees;//任务相关人员
    /**
     * id : 5EC208BAB0D311E6992300163E162ADD
     * name : 110902个人pppp等与北京合众思壮科技股份有限公司等劳动争议、人事争议一审
     * matterType : 0
     */

    private MatterBean matter;
    /**
     * userId : CABE921084B111E6992300163E162ADD
     * userName : 克维
     * pic : http://wx.qlogo.cn/mmopen/Q3auHgzwzM5LZ3RYldDOBiamerpEGhrAcS3xicico73ODyfh7MeEtoiaicP4ibiaejQTv2dibd18lqaLHBFf6CZ0CA4jUtibnXbybkYl50GlfQZYPFH4/0
     */

    private AssignToUserBean assignToUser;
    /**
     * id : 4E5FFBFFA95511E6992300163E162ADD
     * name : 无聊
     */

    private ParentFlowBean parentFlow;
    private int contain;
    private int commentCount;
    private List<AttendeeUserBean> attendeeUsers;
    private AssignUserBean assignUserBean;
    private CreateUserBean createUserBean;
    private int attentioned;  //0:未关注 ; 1:已关注

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMatterId() {
        return matterId;
    }

    public void setMatterId(String matterId) {
        this.matterId = matterId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getDescription() {
        return description;
    }

    public void setDescription(Object description) {
        this.description = description;
    }

    public int getTaskOrder() {
        return taskOrder;
    }

    public void setTaskOrder(int taskOrder) {
        this.taskOrder = taskOrder;
    }

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public String getAssignTo() {
        return assignTo;
    }

    public void setAssignTo(String assignTo) {
        this.assignTo = assignTo;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Object getStartTime() {
        return startTime;
    }

    public void setStartTime(Object startTime) {
        this.startTime = startTime;
    }

    public Object getEndTime() {
        return endTime;
    }

    public void setEndTime(Object endTime) {
        this.endTime = endTime;
    }

    public Object getDueTime() {
        return dueTime;
    }

    public void setDueTime(Object dueTime) {
        this.dueTime = dueTime;
    }

    public String getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(String createUserId) {
        this.createUserId = createUserId;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public Object getNotifyType() {
        return notifyType;
    }

    public void setNotifyType(Object notifyType) {
        this.notifyType = notifyType;
    }

    public Object getAssignTime() {
        return assignTime;
    }

    public void setAssignTime(Object assignTime) {
        this.assignTime = assignTime;
    }

    public Object getAssignUserId() {
        return assignUserId;
    }

    public void setAssignUserId(Object assignUserId) {
        this.assignUserId = assignUserId;
    }

    public Object getAttendees() {
        return attendees;
    }

    public void setAttendees(Object attendees) {
        this.attendees = attendees;
    }

    public MatterBean getMatter() {
        return matter;
    }

    public void setMatter(MatterBean matter) {
        this.matter = matter;
    }

    public AssignToUserBean getAssignToUser() {
        return assignToUser;
    }

    public void setAssignToUser(AssignToUserBean assignToUser) {
        this.assignToUser = assignToUser;
    }

    public ParentFlowBean getParentFlow() {
        return parentFlow;
    }

    public void setParentFlow(ParentFlowBean parentFlow) {
        this.parentFlow = parentFlow;
    }

    public int getContain() {
        return contain;
    }

    public void setContain(int contain) {
        this.contain = contain;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public List<AttendeeUserBean> getAttendeeUsers() {
        return attendeeUsers;
    }

    public void setAttendeeUsers(List<AttendeeUserBean> attendeeUsers) {
        this.attendeeUsers = attendeeUsers;
    }

    public AssignUserBean getAssignUserBean() {
        return assignUserBean;
    }

    public void setAssignUserBean(AssignUserBean assignUserBean) {
        this.assignUserBean = assignUserBean;
    }

    public CreateUserBean getCreateUserBean() {
        return createUserBean;
    }

    public void setCreateUserBean(CreateUserBean createUserBean) {
        this.createUserBean = createUserBean;
    }

    public int getAttentioned() {
        return attentioned;
    }

    public void setAttentioned(int attentioned) {
        this.attentioned = attentioned;
    }

    public static class MatterBean implements Serializable{
        private String id;
        private String name;
        private String matterType;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getMatterType() {
            return matterType;
        }

        public void setMatterType(String matterType) {
            this.matterType = matterType;
        }
    }

    public static class AssignToUserBean implements Serializable{
        private String userId;
        private String userName;
        private String pic;

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getPic() {
            return pic;
        }

        public void setPic(String pic) {
            this.pic = pic;
        }
    }

    public static class ParentFlowBean implements Serializable {
        private String id;
        private String name;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static class AttendeeUserBean implements Serializable{
        private String userId;
        private String userName;
        private String pic;

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getPic() {
            return pic;
        }

        public void setPic(String pic) {
            this.pic = pic;
        }
    }

    public static class AssignUserBean implements Serializable{
        private String userId;
        private String userName;
        private String pic;

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getPic() {
            return pic;
        }

        public void setPic(String pic) {
            this.pic = pic;
        }
    }

    public static class CreateUserBean{
        private String userId;
        private String userName;
        private String pic;

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getPic() {
            return pic;
        }

        public void setPic(String pic) {
            this.pic = pic;
        }
    }
}
