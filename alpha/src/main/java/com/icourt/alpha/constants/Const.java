package com.icourt.alpha.constants;

import android.support.annotation.IntDef;

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
    public static final int MSG_TYPE_TXT = 0;     //文本消息
    public static final int MSG_TYPE_FILE = 1;    //文件消息
    public static final int MSG_TYPE_DING = 2;    //钉消息
    public static final int MSG_TYPE_AT = 3;      //@消息
    public static final int MSG_TYPE_SYS = 4;     //系统辅助消息
    public static final int MSG_TYPE_LINK = 5;    //链接消息
    public static final int MSG_TYPE_ALPHA = 6;   //alpha系统内业务消息
    public static final int MSG_TYPE_VOICE = 7;   //文件消息


    @IntDef({MSG_TYPE_TXT,
            MSG_TYPE_FILE,
            MSG_TYPE_DING,
            MSG_TYPE_AT,
            MSG_TYPE_SYS,
            MSG_TYPE_LINK,
            MSG_TYPE_ALPHA,
            MSG_TYPE_VOICE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface MSG_TYPE {

    }
}
