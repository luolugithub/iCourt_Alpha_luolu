package com.icourt.alpha.entity.bean;

import android.support.annotation.StringDef;

import com.netease.nimlib.sdk.msg.model.IMMessage;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/5/13
 * version 1.0.0
 */
public class AlphaSecialHeplerMsgEntity {

    public static String MATTER_DETAILS_EDIT = "MATTER_DETAILS_EDIT";//编辑了项目详情
    public static String MATTER_STATUS_CHANGE = "MATTER_STATUS_CHANGE";//修改了项目状态
    public static String MATTER_MEMBER_ADDU = "MATTER_MEMBER_ADDU";//邀请你加入项目
    public static String MATTER_MEMBER_REMOVEU = "MATTER_MEMBER_REMOVEU";//将你移除了项目
    public static String MATTER_MEMBER_CHANGE = "MATTER_MEMBER_CHANGE";//修改了项目成员

    public static String TASK_DETAILS = "TASK_DETAILS";//编辑
    public static String TASK_STATUS = "TASK_STATUS";//状态:(新建、完成、重启、删除、恢复)
    public static String TASK_PRINCIPAL = "TASK_PRINCIPAL";//编辑负责人
    public static String TASK_DEADLINE = "TASK_DEADLINE";//编辑任务日期
    public static String TASK_ATTACH = "TASK_ATTACH";//编辑任务附件
    public static String TASK_REPLY = "TASK_REPLY";//回复任务

    public static final String TASK_STATUS_CREATE = "TASK_STATUS_CREATE";//新建了任务
    public static final String TASK_STATUS_FINISH = "TASK_STATUS_FINISH";//完成了任务
    public static final String TASK_STATUS_RESTART = "TASK_STATUS_RESTART";//重启了任务
    public static final String TASK_STATUS_DELETE = "TASK_STATUS_DELETE";//删除了任务
    public static final String TASK_STATUS_RECOVERY = "TASK_STATUS_RECOVERY";//恢复了任务
    public static final String TASK_PRINCIPAL_REMOVEU = "TASK_PRINCIPAL_REMOVEU";//将你移出任务

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

    public IMMessage imMessage;
    public int showType;
    public String content;
    public String id;
    public String matterName;
    public String route;

    @ObjectType
    public String object;

    public String type;
    /**
     * 使用场景
     */
    public String scene;
    public String taskName;
    public String pic;
    public String operator;

    public long dueTime;//截止时间
    public String fileName;//文件名称
    public String reply;//评论

    public long endDate;//项目结束时间
    public long startDate;//项目开始时间
    public String clientName;//客户名
    public String serveContent;//服务内容
    public String caseProcess;//程序-案由
    public String status;//项目状态

    public static final String OBJECT_TYPE_TASK = "TASK";
    public static final String OBJECT_TYPE_PROJECT = "MATTER";

    @StringDef({
            "TASK",
            "MATTER"})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ObjectType {

    }


    /**
     * 项目成员添加
     */
    public static final String SCENE_PROJECT_MEMBER_ADDED = "MATTER_MEMBER_ADDU";

    /**
     * 项目删除
     */
    public static final String SCENE_PROJECT_DELETE = "MATTER_STATUS_DELETE";
}
