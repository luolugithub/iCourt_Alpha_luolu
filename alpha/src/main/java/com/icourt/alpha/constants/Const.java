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
}
