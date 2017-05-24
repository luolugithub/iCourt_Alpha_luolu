package com.icourt.alpha.constants;

import android.support.annotation.IntDef;
import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Description 常量配置定义
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：17/3/29
 * version
 */

public class Const {
    public static final String SHARE_PREFERENCES_FILE_NAME = "icourt_cache_data";//SharedPreferences 文件名称
    //下载文件
    public static final String HTTP_DOWNLOAD_FILE = "ilaw/api/v2/file/download";


    public static final int VIEW_TYPE_ITEM = 101;
    public static final int VIEW_TYPE_GRID = 102;

    @IntDef({VIEW_TYPE_ITEM,
            VIEW_TYPE_GRID})
    @Retention(RetentionPolicy.SOURCE)
    public @interface AdapterViewType {
    }


    public static final int CHOICE_TYPE_SINGLE = 201;
    public static final int CHOICE_TYPE_MULTIPLE = 202;

    @IntDef({CHOICE_TYPE_SINGLE,
            CHOICE_TYPE_MULTIPLE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ChoiceType {
    }


    public static final int CHAT_TYPE_P2P = 0;//单聊
    public static final int CHAT_TYPE_TEAM = 1;//群聊


    @IntDef({CHAT_TYPE_P2P,
            CHAT_TYPE_TEAM})
    @Retention(RetentionPolicy.SOURCE)
    public @interface CHAT_TYPE {

    }


    //文档地址 https://www.showdoc.cc/1620156?page_id=14893614
    public static final int MSG_TYPE_TXT = 0;            //文本消息
    public static final int MSG_TYPE_FILE = 1;           //文件消息
    public static final int MSG_TYPE_DING = 2;           //钉消息
    public static final int MSG_TYPE_AT = 3;             //@消息
    public static final int MSG_TYPE_SYS = 4;            //系统辅助消息
    public static final int MSG_TYPE_LINK = 5;           //链接消息
    public static final int MSG_TYPE_ALPHA = 6;          //alpha系统内业务消息
    public static final int MSG_TYPE_VOICE = 7;          //语音消息
    public static final int MSG_TYPE_IMAGE = 8;          //图片消息
    //扩展消息 非展示类型
    public static final int MSG_TYPE_CONTACT_UPDATE = 100;//联系人更新通知
    public static final int MSG_TYPE_SET_TOP = 101;       //设置置顶通知
    public static final int MSG_TYPE_LEAVE_GROUP = 102;   //离开讨论组通知


    @IntDef({MSG_TYPE_TXT,
            MSG_TYPE_FILE,
            MSG_TYPE_DING,
            MSG_TYPE_AT,
            MSG_TYPE_SYS,
            MSG_TYPE_LINK,
            MSG_TYPE_ALPHA,
            MSG_TYPE_VOICE,
            MSG_TYPE_IMAGE,
            MSG_TYPE_CONTACT_UPDATE,
            MSG_TYPE_SET_TOP,
            MSG_TYPE_LEAVE_GROUP})
    @Retention(RetentionPolicy.SOURCE)
    public @interface MSG_TYPE {

    }

    public static final int MSG_STATU_DRAFT = 0;     //草稿
    public static final int MSG_STATU_SENDING = 1;   //正在发送中
    public static final int MSG_STATU_SUCCESS = 2;   //发送成功
    public static final int MSG_STATU_FAIL = 3;      //发送失败
    public static final int MSG_STATU_READ = 4;      // 消息已读 发送消息时表示对方已看过该消息 接收消息时表示自己已读过，一般仅用于音频消息
    public static final int MSG_STATU_UNREAD = 5;    //未读状态

    @IntDef({MSG_STATU_DRAFT,
            MSG_STATU_SENDING,
            MSG_STATU_SUCCESS,
            MSG_STATU_FAIL,
            MSG_STATU_READ,
            MSG_STATU_UNREAD})
    @Retention(RetentionPolicy.SOURCE)
    public @interface MSG_STATU {

    }

    public static final String PROJECT_TYPE_DISPUTE = "0";//争议解决
    public static final String PROJECT_TYPE_NOJUDICIAL = "1";//非诉专项
    public static final String PROJECT_TYPE_COUNSELOR = "2";//常年顾问
    public static final String PROJECT_TYPE_AFFAIR = "3";//内部事务

    @StringDef({PROJECT_TYPE_DISPUTE, PROJECT_TYPE_NOJUDICIAL, PROJECT_TYPE_COUNSELOR, PROJECT_TYPE_AFFAIR})
    @Retention(RetentionPolicy.SOURCE)
    public @interface PROJECT_TYPE {

    }

    public static final String PROJECT_ORDER_STATUS = "status";//项目状态
    public static final String PROJECT_ORDER_NAME = "name";//项目名称
    public static final String PROJECT_ORDER_MATTERTYPE = "matterType";//项目类型
    public static final String PROJECT_ORDER_OPENDATE = "openDate";//创建时间
    public static final String PROJECT_ORDER_CLOSEDATE = "closeDate";//结束时间

    @StringDef({PROJECT_ORDER_STATUS, PROJECT_ORDER_NAME, PROJECT_ORDER_MATTERTYPE, PROJECT_ORDER_OPENDATE, PROJECT_ORDER_CLOSEDATE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface PROJECT_ORDERBY {

    }


    public static final int SEARCH_TYPE_CONTACT = 1;
    public static final int SEARCH_TYPE_MSG = 2;
    public static final int SEARCH_TYPE_TEAM = 3;


    @IntDef({SEARCH_TYPE_CONTACT,
            SEARCH_TYPE_MSG,
            SEARCH_TYPE_TEAM})
    @Retention(RetentionPolicy.SOURCE)
    public @interface SEARCH_TYPE {

    }


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
}
