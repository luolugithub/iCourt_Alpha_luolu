package com.icourt.alpha.constants;

/**
 * @author 创建人:lu.zhao
 *         <p>
 *         助手通知 配置
 * @data 创建时间:17/3/31
 */

public class HelperNotifiConfig {

    // 任务通知
    public static String TASK_OBJECT = "TASK";
    // 任务类型
    public static String TASK_DETAILS = "TASK_DETAILS";//编辑
    public static String TASK_STATUS = "TASK_STATUS";//状态:(新建、完成、重启、删除、恢复)
    public static String TASK_PRINCIPAL = "TASK_PRINCIPAL";//编辑负责人
    public static String TASK_DEADLINE = "TASK_DEADLINE";//编辑任务日期
    public static String TASK_ATTACH = "TASK_ATTACH";//编辑任务附件
    public static String TASK_REPLY = "TASK_REPLY";//回复任务
    // 任务场景
    public static String TASK_DETAILS_EDIT = "TASK_DETAILS_EDIT";//编辑了任务

    public static String TASK_STATUS_CREATE = "TASK_STATUS_CREATE";//新建了任务
    public static String TASK_STATUS_FINISH = "TASK_STATUS_FINISH";//完成了任务
    public static String TASK_STATUS_RESTART = "TASK_STATUS_RESTART";//重启了任务
    public static String TASK_STATUS_DELETE = "TASK_STATUS_DELETE";//删除了任务
    public static String TASK_STATUS_RECOVERY = "TASK_STATUS_RECOVERY";//恢复了任务

    public static String TASK_PRINCIPAL_ADDU = "TASK_PRINCIPAL_ADDU";//给你分配了任务
    public static String TASK_PRINCIPAL_REMOVEU = "TASK_PRINCIPAL_REMOVEU";//将你移出任务
    public static String TASK_PRINCIPAL_ADD = "TASK_PRINCIPAL_ADD";//添加了任务负责人
    public static String TASK_PRINCIPAL_EDIT = "TASK_PRINCIPAL_EDIT";//编辑了任务负责人

    public static String TASK_DEADLINE_ADD = "TASK_DEADLINE_ADD";//指定了任务到期时间
    public static String TASK_DEADLINE_UPDATE = "TASK_DEADLINE_UPDATE";//修改了任务到期时间
    public static String TASK_DEADLINE_DELETE = "TASK_DEADLINE_DELETE";//删除了任务到期时间

    public static String TASK_ATTACH_UPLOAD = "TASK_ATTACH_UPLOAD";//上传了任务附件
    public static String TASK_ATTACH_DELETE = "TASK_ATTACH_DELETE";//删除了任务附件

    public static String TASK_REPLY_ADD = "TASK_REPLY_ADD";//回复了任务


    //任务组通知
    public static String TGROUP_OBJECT = "TGROUP";
    public static String TGROUP_STATUS = "TGROUP_STATUS";//状态:(新建、完成、重启、删除、恢复)
    public static String TGROUP_DETAILS = "TGROUP_DETAILS";//编辑

    public static String TGROUP_STATUS_CREATE = "TGROUP_STATUS_CREATE";//新建了任务组
    public static String TGROUP_STATUS_DELETE = "TGROUP_STATUS_DELETE";//删除了任务组
    public static String TGROUP_STATUS_RECOVERY = "TGROUP_STATUS_RECOVERY";//恢复了任务组
    public static String TGROUP_STATUS_FINISH = "TGROUP_STATUS_FINISH";//完成了任务组
    public static String TGROUP_STATUS_RESTART = "TGROUP_STATUS_RESTART";//重启了任务组
    public static String TGROUP_DETAILS_EDIT = "TGROUP_DETAILS_EDIT";//编辑了任务组


    //项目通知
    public static String MATTER_OBJECT = "MATTER";
    public static String MATTER_DETAILS = "MATTER_DETAILS";//编辑
    public static String MATTER_STATUS = "MATTER_STATUS";//修改状态
    public static String MATTER_MEMBER = "MATTER_MEMBER";//修改成员

    public static String MATTER_DETAILS_EDIT = "MATTER_DETAILS_EDIT";//编辑了项目详情
    public static String MATTER_STATUS_CHANGE = "MATTER_STATUS_CHANGE";//修改了项目状态
    public static String MATTER_MEMBER_ADDU = "MATTER_MEMBER_ADDU";//邀请你加入项目
    public static String MATTER_MEMBER_REMOVEU = "MATTER_MEMBER_REMOVEU";//将你移除了项目
    public static String MATTER_MEMBER_CHANGE = "MATTER_MEMBER_CHANGE";//修改了项目成员

}
