package com.icourt.alpha.entity.bean;

import java.io.Serializable;

/**
 * @author 创建人:lu.zhao
 *         <p>
 *         alpha助手推送通知bean
 * @data 创建时间:17/3/30
 */

public class HelperNotification implements Serializable {

//    TASK_DETAILS_EDIT("TASK","TASK_DETAILS","TASK_DETAILS_EDIT","{0}编辑了任务"),
//
//    TASK_STATUS_CREATE("TASK","TASK_STATUS","TASK_STATUS_CREATE","{0}新建了任务"),
//    TASK_STATUS_FINISH("TASK","TASK_STATUS","TASK_STATUS_FINISH","{0}完成了任务"),
//    TASK_STATUS_RESTART("TASK","TASK_STATUS","TASK_STATUS_RESTART","{0}重启了任务"),
//    TASK_STATUS_DELETE("TASK","TASK_STATUS","TASK_STATUS_DELETE","{0}删除了任务"),
//    TASK_STATUS_RECOVERY("TASK","TASK_STATUS","TASK_STATUS_RECOVERY","{0}恢复了任务"),
//
//    TASK_PRINCIPAL_ADDU("TASK"," TASK_PRINCIPAL","TASK_STATUS_RESTART","{0}给你分配了任务"),
//    TASK_PRINCIPAL_REMOVEU("TASK"," TASK_PRINCIPAL","TASK_PRINCIPAL_REMOVEU","{0}将你移出任务"),
//    TASK_PRINCIPAL_ADD("TASK"," TASK_PRINCIPAL","TASK_PRINCIPAL_ADD","{0}编辑了任务负责人"),
//    TASK_PRINCIPAL_EDIT("TASK"," TASK_PRINCIPAL","TASK_PRINCIPAL_EDIT","{0}编辑了任务负责人"),
//
//    TASK_DEADLINE_ADD("TASK"," TASK_DEADLINE","TASK_DEADLINE_ADD","{0}指定了任务到期时间"),
//    TASK_DEADLINE_UPDATE("TASK"," TASK_DEADLINE","TASK_DEADLINE_UPDATE","{0}修改了任务到期时间"),
//    TASK_DEADLINE_DELETE("TASK"," TASK_DEADLINE","TASK_DEADLINE_DELETE","{0}删除了任务到期时间"),
//
//    TASK_ATTACH_UPLOAD("TASK","TASK_ATTACH","TASK_ATTACH_UPLOAD","{0}上传了任务附件"),
//    TASK_ATTACH_DELETE("TASK","TASK_ATTACH","TASK_ATTACH_DELETE","{0}删除了任务附件"),
//
//    TASK_REPLY_ADD("TASK","TASK_REPLY","TASK_REPLY_ADD","{0}回复了任务"),
//
//    TGROUP_STATUS_CREATE("TGROUP","TGROUP_STATUS","TGROUP_STATUS_CREATE","{0}新建了任务组"),
//    TGROUP_STATUS_DELETE("TGROUP","TGROUP_STATUS","TGROUP_STATUS_DELETE","{0}删除了任务组"),
//    TGROUP_STATUS_RECOVERY("TGROUP","TGROUP_STATUS","TGROUP_STATUS_RECOVERY","{0}恢复了任务组"),
//    TGROUP_STATUS_FINISH("TGROUP","TGROUP_STATUS","TGROUP_STATUS_FINISH","{0}完成了任务组"),
//    TGROUP_STATUS_RESTART("TGROUP","TGROUP_STATUS","TGROUP_STATUS_RESTART","{0}重启了任务组"),
//
//    TGROUP_DETAILS_EDIT("TGROUP","TGROUP_DETAILS","TGROUP_DETAILS_EDIT","{0}新建了任务组"),
//
//    MATTER_DETAILS_EDIT("MATTER","MATTER_DETAILS","MATTER_DETAILS_EDIT","{0}编辑了项目详情"),
//
//    MATTER_STATUS_CHANGE("MATTER","MATTER_STATUS","MATTER_STATUS_CHANGE","{0}修改了项目状态"),
//
//    MATTER_MEMBER_ADDU("MATTER","MATTER_MEMBER","MATTER_MEMBER_ADDU","{0}邀请你加入项目"),
//    MATTER_MEMBER_REMOVEU("MATTER","MATTER_MEMBER","MATTER_MEMBER_REMOVEU","{0}将你移除了项目"),
//    MATTER_MEMBER_CHANGE("MATTER"," MATTER_MEMBER","MATTER_MEMBER_CHANGE","{0}修改了项目成员");

    /**
     * showType : 200
     * content : 小明完成了任务
     * id : 1B18624D0F7811E79B4900163E30718E
     * matterName : 作为****集团的兼职法务提供法律咨询
     * route : alpha://tasks/1B18624D0F7811E79B4900163E30718E
     * object : TASK
     * type : TASK_STATUS
     * scene : TASK_STATUS_FINISH
     * taskName : A级任务
     */

    private int showType;
    private String content;
    private String id;
    private String matterName;
    private String route;
    private String object;
    private String type;
    private String scene;
    private String taskName;
    private String pic;
    private String operator;

    private long dueTime;//截止时间
    private String fileName;//文件名称
    private String reply;//评论

    private long endDate;//项目结束时间
    private long startDate;//项目开始时间
    private String clientName;//客户名
    private String serveContent;//服务内容
    private String caseProcess;//程序-案由
    private String status;//项目状态

    public int getShowType() {
        return showType;
    }

    public void setShowType(int showType) {
        this.showType = showType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMatterName() {
        return matterName;
    }

    public void setMatterName(String matterName) {
        this.matterName = matterName;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getScene() {
        return scene;
    }

    public void setScene(String scene) {
        this.scene = scene;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public long getDueTime() {
        return dueTime;
    }

    public void setDueTime(long dueTime) {
        this.dueTime = dueTime;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public long getEndDate() {
        return endDate;
    }

    public void setEndDate(long endDate) {
        this.endDate = endDate;
    }

    public long getStartDate() {
        return startDate;
    }

    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getServeContent() {
        return serveContent;
    }

    public void setServeContent(String serveContent) {
        this.serveContent = serveContent;
    }

    public String getCaseProcess() {
        return caseProcess;
    }

    public void setCaseProcess(String caseProcess) {
        this.caseProcess = caseProcess;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
