package com.icourt.alpha.entity.bean;

import android.support.annotation.StringDef;
import android.text.TextUtils;

import com.icourt.alpha.widget.filter.IFilterEntity;
import com.netease.nimlib.sdk.msg.model.IMMessage;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Description  文档地址：http://wiki.alphalawyer.cn/pages/viewpage.action?pageId=854324
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/5/13
 * version 1.0.0
 */
public class AlphaSecialHeplerMsgEntity implements IFilterEntity {

    public static String TASK_REPLY = "TASK_REPLY";//回复任务


    /**
     * 项目成员添加
     */
    public static final String SCENE_TASK_REPLY_ADD = "TASK_REPLY_ADD";
    /**
     * 上传了任务附件
     */
    public static final String SCENE_TASK_ATTACH_UPLOAD = "TASK_ATTACH_UPLOAD";
    /**
     * 上传了任务附件
     */
    public static final String SCENE_TASK_ATTACH_DELETE = "TASK_ATTACH_DELETE";
    /**
     * 删除了任务到期时间
     */
    public static final String SCENE_TASK_DEADLINE_DELETE = "TASK_DEADLINE_DELETE";
    /**
     * 修改了任务到期时间
     */
    public static final String SCENE_TASK_DEADLINE_UPDATE = "TASK_DEADLINE_UPDATE";
    /**
     * 指定了任务到期时间
     */
    public static final String SCENE_TASK_DEADLINE_ADD = "TASK_DEADLINE_ADD";

    /**
     * 编辑了任务负责人
     */
    public static final String SCENE_TASK_PRINCIPAL_EDIT = "TASK_PRINCIPAL_EDIT";
    /**
     * 新增任务负责人
     */
    public static final String SCENE_TASK_PRINCIPAL_ADD = "TASK_PRINCIPAL_ADD";
    /**
     * 将你移出任务
     */
    public static final String SCENE_TASK_PRINCIPAL_REMOVEU = "TASK_PRINCIPAL_REMOVEU";
    /**
     * 给你分配了任务
     */
    public static final String SCENE_TASK_PRINCIPAL_ADDU = "TASK_PRINCIPAL_ADDU";
    /**
     * 任务到期提醒
     */
    public static final String SCENE_TASK_REMINDER_ADD = "TASK_REMINDER_ADD";
    /**
     * 恢复了任务
     */
    public static final String SCENE_TASK_STATUS_RECOVERY = "TASK_STATUS_RECOVERY";
    /**
     * 删除了任务
     */
    public static final String SCENE_TASK_STATUS_DELETE = "TASK_STATUS_DELETE";
    /**
     * 重启了任务
     */
    public static final String SCENE_TASK_STATUS_RESTART = "TASK_STATUS_RESTART";
    /**
     * 完成了任务
     */
    public static final String SCENE_TASK_STATUS_FINISH = "TASK_STATUS_FINISH";
    /**
     * 新建了任务
     */
    public static final String SCENE_TASK_STATUS_CREATE = "TASK_STATUS_CREATE";
    /**
     * 编辑了任务
     */
    public static final String SCENE_TASK_DETAILS_EDIT = "TASK_DETAILS_EDIT";


    /**
     * 任务的场景 17
     */
    private static final List<String> SCENE_TASK_LIST = new ArrayList<String>(
            Arrays.asList(
                    SCENE_TASK_REPLY_ADD,
                    SCENE_TASK_ATTACH_UPLOAD,
                    SCENE_TASK_ATTACH_DELETE,
                    SCENE_TASK_DEADLINE_DELETE,
                    SCENE_TASK_DEADLINE_UPDATE,
                    SCENE_TASK_DEADLINE_ADD,
                    SCENE_TASK_PRINCIPAL_EDIT,
                    SCENE_TASK_PRINCIPAL_ADD,
                    SCENE_TASK_PRINCIPAL_REMOVEU,
                    SCENE_TASK_PRINCIPAL_ADDU,
                    SCENE_TASK_REMINDER_ADD,
                    SCENE_TASK_STATUS_RECOVERY,
                    SCENE_TASK_STATUS_DELETE,
                    SCENE_TASK_STATUS_RESTART,
                    SCENE_TASK_STATUS_FINISH,
                    SCENE_TASK_STATUS_CREATE,
                    SCENE_TASK_DETAILS_EDIT));


    /**
     * 项目成员添加
     */
    public static final String SCENE_PROJECT_MEMBER_ADDED = "MATTER_MEMBER_ADDU";
    /**
     * 项目删除
     */
    public static final String SCENE_PROJECT_DELETE = "MATTER_STATUS_DELETE";
    /**
     * 项目更改成员
     */
    public static final String SCENE_PROJECT_MEMBER_CHANGE = "MATTER_MEMBER_CHANGE";
    /**
     * 将你移除了项目
     */
    public static final String SCENE_PROJECT_MEMBER_REMOVEU = "MATTER_MEMBER_REMOVEU";
    /**
     * 修改了项目状态
     */
    public static final String SCENE_PROJECT_STATUS_CHANGE = "MATTER_STATUS_CHANGE";
    /**
     * 编辑了项目详情
     */
    public static final String SCENE_PROJECT_DETAILS_EDIT = "MATTER_DETAILS_EDIT";


    /**
     * 项目的场景 6
     */
    private static final List<String> SCENE_PROJECT_LIST = new ArrayList<String>(
            Arrays.asList(
                    SCENE_PROJECT_MEMBER_ADDED,
                    SCENE_PROJECT_DELETE,
                    SCENE_PROJECT_MEMBER_CHANGE,
                    SCENE_PROJECT_MEMBER_REMOVEU,
                    SCENE_PROJECT_STATUS_CHANGE,
                    SCENE_PROJECT_DETAILS_EDIT));


    /**
     * 重启了任务组
     */
    public static final String SCENE_TASK_GROUP_STATUS_RESTART = "TGROUP_STATUS_RESTART";
    /**
     * 完成了任务组
     */
    public static final String SCENE_TASK_GROUP_STATUS_FINISH = "TGROUP_STATUS_FINISH";
    /**
     * 恢复了任务组
     */
    public static final String SCENE_TASK_GROUP_STATUS_RECOVERY = "TGROUP_STATUS_RECOVERY";
    /**
     * 删除了任务组
     */
    public static final String SCENE_TASK_GROUP_STATUS_DELETE = "TGROUP_STATUS_DELETE";
    /**
     * 新建了任务组
     */
    public static final String SCENE_TASK_GROUP_STATUS_CREATE = "TGROUP_STATUS_CREATE";


    /**
     * 任务组的场景 5
     */
    private static final List<String> SCENE_TASK_GROUP_LIST = new ArrayList<String>(
            Arrays.asList(
                    SCENE_TASK_GROUP_STATUS_RESTART,
                    SCENE_TASK_GROUP_STATUS_FINISH,
                    SCENE_TASK_GROUP_STATUS_RECOVERY,
                    SCENE_TASK_GROUP_STATUS_DELETE,
                    SCENE_TASK_GROUP_STATUS_CREATE));


    public static final String OBJECT_TYPE_TASK = "TASK";
    public static final String OBJECT_TYPE_PROJECT = "MATTER";

    @StringDef({
            OBJECT_TYPE_TASK,
            OBJECT_TYPE_PROJECT})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ObjectType {

    }


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


    /**
     * 考虑到web端任意加类型 所以先过滤
     *
     * @param type
     * @return true 就过滤掉
     */
    @Override
    public boolean isFilter(int type) {
        //任务项目有效
        if (TextUtils.equals(OBJECT_TYPE_TASK, object)
                || TextUtils.equals(OBJECT_TYPE_PROJECT, object)) {

            return !(
                    SCENE_PROJECT_LIST.contains(scene)
                            || SCENE_TASK_LIST.contains(scene)
                            || SCENE_TASK_GROUP_LIST.contains(scene)
            );

        }
        return true;
    }
}
