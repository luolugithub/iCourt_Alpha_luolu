package com.icourt.alpha.constants;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/8/21
 * version 2.1.0
 */
public class SFileConfig {

    public static final int REPO_MINE = 0;
    public static final int REPO_SHARED_ME = 1;
    public static final int REPO_LAWFIRM = 2;
    public static final int REPO_PROJECT = 3;

    /**
     * 0： "我的资料库",
     * 1： "共享给我的",
     * 2： "律所资料库",
     * 3： "项目资料库"
     */
    @IntDef({REPO_MINE,
            REPO_SHARED_ME,
            REPO_LAWFIRM,
            REPO_PROJECT})
    @Retention(RetentionPolicy.SOURCE)
    public @interface REPO_TYPE {

    }

    /**
     * 转换
     *
     * @param repoType
     * @return
     */
    @REPO_TYPE
    public static final int convert2RepoType(int repoType) {
        switch (repoType) {
            case REPO_MINE:
                return REPO_MINE;
            case REPO_SHARED_ME:
                return REPO_SHARED_ME;
            case REPO_LAWFIRM:
                return REPO_LAWFIRM;
            case REPO_PROJECT:
                return REPO_PROJECT;
            default:
                return REPO_MINE;
        }
    }
}
