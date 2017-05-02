package com.icourt.alpha.utils;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.text.TextUtils;
import android.widget.Toast;

import com.icourt.alpha.base.BaseApplication;

/**
 * Description
 * * <p>
 * 【不建议使用 不建议使用】
 * <p>
 * 某些系统可能屏蔽通知
 * 1:检查 SystemUtils.isEnableNotification(BaseApplication.getApplication());
 * 2:替代方案 SnackbarUtils.showSnack(topActivity, noticeStr);
 * 3:参考 @see {@link com.icourt.alpha.http.callback.SimpleCallBack} defNotify
 * <p>
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/27
 * version 1.0.0
 */
public class ToastUtils {

    public static void showToast(@NonNull CharSequence notice) {
        if (TextUtils.isEmpty(notice)) return;
        Toast.makeText(BaseApplication.getApplication(), notice, Toast.LENGTH_SHORT)
                .show();
    }

    public static void showToast(@StringRes int notice) {
        Toast.makeText(BaseApplication.getApplication(), notice, Toast.LENGTH_SHORT)
                .show();
    }
}
