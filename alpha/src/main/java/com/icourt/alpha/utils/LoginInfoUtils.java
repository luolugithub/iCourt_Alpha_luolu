package com.icourt.alpha.utils;

import android.support.annotation.CheckResult;
import android.support.annotation.Nullable;

import com.icourt.alpha.entity.bean.AlphaUserInfo;

import java.io.Serializable;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/10
 * version 1.0.0
 */
public class LoginInfoUtils {

    private LoginInfoUtils() {
    }

    /**
     * @return 登陆信息
     */
    @Nullable
    @CheckResult
    public static final AlphaUserInfo getLoginUserInfo() {
        Serializable user = SpUtils.getInstance().getSerializableData("user");
        if (user instanceof AlphaUserInfo) {
            return (AlphaUserInfo) user;
        }
        return null;
    }

    /**
     * @return 登陆信息
     */
    @Nullable
    @CheckResult
    public static final String getLoginUserId() {
        AlphaUserInfo loginUserInfo = getLoginUserInfo();
        return loginUserInfo == null ? "" : loginUserInfo.getUserId();
    }

    /**
     * 清除登陆信息
     */
    public static final void clearLoginUserInfo() {
        SpUtils.getInstance().remove("user");
    }

    /**
     * 保存登陆信息
     *
     * @param alphaUserInfo
     */
    public static final void saveLoginUserInfo(AlphaUserInfo alphaUserInfo) {
        if (alphaUserInfo == null) return;
        SpUtils.getInstance().putData("user", alphaUserInfo);
    }

    /**
     * @return 是否登陆
     */
    public static final boolean isUserLogin() {
        return getLoginUserInfo() != null;
    }

    /**
     * 获取登陆的token
     *
     * @return
     */
    @Nullable
    @CheckResult
    public static final String getUserToken() {
        AlphaUserInfo loginUserInfo = getLoginUserInfo();
        if (loginUserInfo != null) {
            return loginUserInfo.getToken();
        }
        return null;
    }
}
