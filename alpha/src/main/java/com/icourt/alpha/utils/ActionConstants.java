package com.icourt.alpha.utils;

import com.icourt.alpha.R;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by icourt on 16/11/18.
 */

public class ActionConstants {
    public static final String APK_NAME = "alpha_release.apk";
    public static Map<String, Integer> resourcesMap = new HashMap<String, Integer>() {
        {
            put("doc", R.mipmap.filetype_doc_20);
            put("wps", R.mipmap.filetype_doc_20);
            put("rtf", R.mipmap.filetype_doc_20);
            put("docx", R.mipmap.filetype_doc_20);

            put("jpg", R.mipmap.filetype_image_20);
            put("jpeg", R.mipmap.filetype_image_20);
            put("png", R.mipmap.filetype_image_20);
            put("gif", R.mipmap.filetype_image_20);
            put("pic", R.mipmap.filetype_image_20);

            put("pdf", R.mipmap.filetype_pdf_20);
            put("ppt", R.mipmap.filetype_ppt_20);
            put("pptx", R.mipmap.filetype_ppt_20);

            put("xls", R.mipmap.filetype_excel_20);
            put("xlsx", R.mipmap.filetype_excel_20);
            put("xlsm", R.mipmap.filetype_excel_20);

            put("zip", R.mipmap.filetype_zip_20);
            put("rar", R.mipmap.filetype_zip_20);

            put("mp3", R.mipmap.filetype_music_20);
            put("wav", R.mipmap.filetype_music_20);

            put("mp4", R.mipmap.filetype_video_20);
            put("avi", R.mipmap.filetype_video_20);
            put("ram", R.mipmap.filetype_video_20);
            put("rm", R.mipmap.filetype_video_20);
            put("mpg", R.mipmap.filetype_video_20);
            put("mpeg", R.mipmap.filetype_video_20);
            put("wmv", R.mipmap.filetype_video_20);

            put("httpd/unix-directory", R.mipmap.filetype_folder_20);
        }
    };

    public static Map<String, Integer> resourcesMap40 = new HashMap<String, Integer>() {
        {
            put("doc", R.mipmap.filetype_doc_40);
            put("wps", R.mipmap.filetype_doc_40);
            put("rtf", R.mipmap.filetype_doc_40);
            put("docx", R.mipmap.filetype_doc_40);

            put("jpg", R.mipmap.filetype_image_40);
            put("jpeg", R.mipmap.filetype_image_40);
            put("png", R.mipmap.filetype_image_40);
            put("gif", R.mipmap.filetype_image_40);
            put("pic", R.mipmap.filetype_image_40);

            put("pdf", R.mipmap.filetype_pdf_40);
            put("ppt", R.mipmap.filetype_ppt_40);
            put("pptx", R.mipmap.filetype_ppt_40);

            put("xls", R.mipmap.filetype_excel_40);
            put("xlsx", R.mipmap.filetype_excel_40);
            put("xlsm", R.mipmap.filetype_excel_40);

            put("zip", R.mipmap.filetype_zip_40);
            put("rar", R.mipmap.filetype_zip_40);

            put("mp3", R.mipmap.filetype_music_40);
            put("wav", R.mipmap.filetype_music_40);

            put("mp4", R.mipmap.filetype_video_40);
            put("avi", R.mipmap.filetype_video_40);
            put("ram", R.mipmap.filetype_video_40);
            put("rm", R.mipmap.filetype_video_40);
            put("mpg", R.mipmap.filetype_video_40);
            put("mpeg", R.mipmap.filetype_video_40);
            put("wmv", R.mipmap.filetype_video_40);

            put("httpd/unix-directory", R.mipmap.filetype_folder_40);
        }
    };

    public static final int REQUEST_SYSTEM_SETTING_PERMISSION_CODE = 1001;//申请WRITE_SETTING权限

    public static final int REFRESH_RECYCLERVIEW_TAG = 1;//刷新
    public static final int LOADMORE_RECYCLERVIEW_TAG = 2;//加载更多
    public static final int DEFAULT_PAGE_SIZE = 20;//每页加载数量

    public static final String FILE_DOWNLOAD_PATH = "alpha_download";//下载文件保存路径

    public static final int TIMER_LIST_ING_TYPE = 0;//正在计时type
    public static final int TIMER_LIST_END_TYPE = 1;//未计时type

    public static final int PROJECT_STATUS_BEFOREHAND = 0;//预立案
    public static final int PROJECT_STATUS_SEREVING = 2;//正在服务
    public static final int PROJECT_STATUS_SETTLED = 4;//已结案
    public static final int PROJECT_STATUS_SHELVED = 7;//已搁置

    //消息类型
    public static final int IM_MESSAGE_TEXT_SHOWTYPE = 0;//文本消息
    public static final int IM_MESSAGE_FILE_SHOWTYPE = 1;//文件消息
    public static final int IM_MESSAGE_PIN_SHOWTYPE = 2;//钉消息
    public static final int IM_MESSAGE_AT_SHOWTYPE = 3;//@消息
    public static final int IM_MESSAGE_NEW_GROUP_SHOWTYPE = 5;//web端新建讨论组头部信息
    public static final int IM_MESSAGE_SYSTEM_SHOWTYPE = 6;//通知消息
    public static final int IM_MESSAGE_CONTACT_UPDATE_SHOWTYPE = 100;//联系人更新通知
    public static final int IM_MESSAGE_SET_TOP_SHOWTYPE = 101;//设置置顶通知
    public static final int IM_MESSAGE_LEAVE_GROUP_SHOWTYPE = 102;//离开讨论组通知

    public static final int START_GROUPMEMBERS_ACTIVITY_REQUEST_CODE = 1;//at选择讨论组成员requestcode

    //讨论组类型
    public static final int IM_MY_ADD_GROUP_TYPE = 1;//我已经加入的讨论组
    public static final int IM_ALL_GROUP_TYPE = 2;//律所中所有讨论组
    public static final int IM_PIGEONHOLE_GROUP_TYPE = 3;//已归档的讨论组

    //讨论组私密类型
    public static final int IM_GROUP_PUBLICE_TYPE = 0;//公开
    public static final int IM_GROUP_PRIVITE_TYPE = 1;//私密
    public static final int IM_GROUP_P2P_TYPE = 2;//私聊

    //修改讨论组基本信息
    public static final int UPDATE_GTOUP_INFO_NAME = 1;//修改讨论组名称
    public static final int UPDATE_GTOUP_INFO_TOPIC = 2;//修改讨论组话题
    public static final int UPDATE_GTOUP_INFO_TARGETS = 3;//修改讨论组目标
    public static final String LEAVE_GROUP_ACTION = "leave_group_action";//离开讨论组广播action
    public static final String SET_GROUP_TOP_ACTION = "set_group_top_action";//设置讨论组置顶广播action
    public static final String SET_GROUP_NOFICTION_ACTION = "set_group_nofiction_action";//设置讨论组免打扰广播action

    public static final int IM_ALL_FILE_TYPE = 1;//所有文件
    public static final int IM_MY_FILE_TYPE = 2;//我的文件

    public static final int REQUEST_IM_ALL_FILE_TYPE = 0;//获取所有文件消息
    public static final int REQUEST_IM_GROUP_FILE_TYPE = 1;//获取组内文件消息
    public static final int REQUEST_IM_MY_ALL_FILE_TYPE = 2;//获取我的所有文件消息
    public static final int REQUEST_IM_GROUP_MY_ALL_FILE_TYPE = 3;//获取组内我的文件消息

    public static final int MESSAGE_LIST_FRAGMENT_TO_CHAT = 1;//会话列表到聊天页面
    public static final int GROUP_CONTACT_LIST_TO_CHAT = 2;//团队/通讯录列表到聊天页面
    public static final int GROUP_LIST_TO_CHAT = 3;//我加入/所有/已归档的讨论组
    public static final int MESSAGE_DETAIL_TO_CHAT = 4;//消息详情到聊天页面

    public static final long MESSAGE_GROUP_TOP = 11;//置顶
    public static final long MESSAGE_GROUP_NO_TOP = 10;//未置顶

    public static final String NEW_CREATE_GROUP_INVITE_MEMBER_ACTION = "new_create_group_invite_member_action";//新建讨论组邀请成员
    public static final String UPDATE_GROUP_INVITE_MEMBER_ACTION = "update_group_invite_member_action";//修改讨论组邀请成员

    public static final String MY_COLLECT_MSG_ACTION = "my_collect_msg_action";//我收藏的消息列表
    public static final String GROUP_DING_MSG_ACTION = "group_ding_msg_action";//讨论组钉的消息列表
    public static final String P2P_DING_MSG_ACTION = "p2p_ding_msg_action";//个人钉的消息列表

    public static final String UNREAD_MESCOUNT_RECEIVER_ACTION = "unread_msgcount_receiver_action";//未读消息数广播action
    public static final String START_CHAT_ACTION = "start_chat_action";//开始聊天action

    public static final String IM_IMAGE_TO_BROWSE_ACTION = "";//聊天图片浏览action

    //action
    public static final String ADD_NEW_TIMER_GOTO_THIS_ACTION = "add_new_timer_goto_this_action";//补计时跳转到编辑计时页
    public static final String STOP_TIMER_GOTO_THIS_ACTION = "stop_timer_goto_this_action";//完成计时跳转到编辑计时页

    public static final String TIMER_GOTO_PROJECT_LIST_ACTION = "timer_goto_project_list_action";//计时选择项目
    public static final String TASK_GOTO_PROJECT_LIST_ACTION = "task_goto_project_list_action";//任务选择项目

    public static final String NEW_TASK_GOTO_GROUPLIST_ACTION = "new_task_goto_grouplist_action";//新建任务跳转至任务组
    public static final String PROJECT_DETAIL_GOTO_GROUPLIST_ACTION = "project_detail_goto_grouplist_action";//项目详情跳转至任务组

    public static final String STOP_TIMERING_ACTION = "stop_timering_action";//停止计时
    public static final String START_TIMERING_ACTION = "start_timering_action";//开始计时

    public static final String UPDATE_PHONE_ACTION = "update_phone_action";//修改电话号码
    public static final String UPDATE_MAIL_ACTION = "update_mail_action";//修改邮箱地址

    public static final String TASK_SELECT_OBJECT_ACTION = "task_select_object_action";//任务选择对象
    public static final String PROJECT_SELECT_OBJECT_ACTION = "project_select_object_action";//项目选择对象

    //个人联系人
    public static final String SELECT_PHONE_TAG_ACTION = "select_phone_tag_action";//选择电话标签action
    public static final String SELECT_EMAIL_TAG_ACTION = "select_email_tag_action";//选择邮箱标签action
    public static final String SELECT_ADDRESS_TAG_ACTION = "select_address_tag_action";//选择地址标签action
    public static final String SELECT_PAPERS_TAG_ACTION = "select_papers_tag_action";//选择证件标签action
    public static final String SELECT_IM_TAG_ACTION = "select_im_tag_action";//选择IM标签action
    public static final String SELECT_DATE_TAG_ACTION = "select_date_tag_action";//选择日期标签action
    public static final String SELECT_ENTERPRISE_TAG_ACTION = "select_enterprise_tag_action";//选择工作单位标签action
    public static final String SELECT_RELATION_TAG_ACTION = "select_relation_tag_action";//选择联络人关系标签action

    //机构联系人
    public static final String SELECT_ENTERPRISE_ADDRESS_TAG_ACTION = "select_enterprise_address_tag_action";//选择机构地址action
    public static final String SELECT_ENTERPRISE_EMAIL_TAG_ACTION = "select_enterprise_email_tag_action";//选择机构邮箱action
    public static final String SELECT_ENTERPRISE_DATE_TAG_ACTION = "select_enterprise_date_tag_action";//选择机构重要日期action
    public static final String SELECT_ENTERPRISE_PARPER_TAG_ACTION = "select_enterprise_parper_tag_action";//选择机构证件action
    public static final String SELECT_ENTERPRISE_LIAISONS_TAG_ACTION = "select_enterprise_liaisons_tag_action";//选择机构联络人action

    public static final String CONTACT_LIST_GOTO_DETAIL_ACTION = "contact_list_goto_detail_action";//团队联系人跳转到详情
    public static final String IM_PHOTO_GOTO_DETAIL_ACTION = "im_photo_goto_detail_action";//聊天点击头像跳转到详情
    public static final String IM_MORE_GOTO_DETAIL_ACTION = "im_more_goto_detail_action";//聊天点击更多跳转到详情

}
