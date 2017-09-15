package com.icourt.alpha.http;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.UiThread;
import android.text.TextUtils;

import com.google.gson.JsonParseException;
import com.icourt.alpha.activity.LoginBaseActivity;
import com.icourt.alpha.activity.LoginSelectActivity;
import com.icourt.alpha.base.BaseApplication;
import com.icourt.alpha.http.exception.ResponseException;
import com.icourt.alpha.utils.BugUtils;
import com.icourt.alpha.utils.LogUtils;
import com.icourt.alpha.utils.NetUtils;
import com.icourt.alpha.utils.SnackbarUtils;
import com.icourt.alpha.utils.SystemUtils;

import java.io.FileNotFoundException;
import java.net.ConnectException;
import java.net.NoRouteToHostException;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import io.reactivex.annotations.NonNull;

import static com.icourt.alpha.utils.AppManager.getAppManager;

/**
 * Description 处理网络异常
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/8/28
 * version 2.1.0
 */
public class HttpThrowableUtils {

    private static final Handler mHandler = new Handler(Looper.getMainLooper());

    /**
     * 处理http异常
     *
     * @param t
     */
    public static final void handleHttpThrowable(@android.support.annotation.NonNull IDefNotify iDefNotify, @NonNull Throwable t) {
        if (t != null) {
            LogUtils.d("----------->handleHttpThrowable:" + t);
            if (t instanceof ResponseException) {
                defNotify(iDefNotify, ((ResponseException) t).message);

            } else if (t instanceof retrofit2.HttpException) {
                retrofit2.HttpException httpException = (retrofit2.HttpException) t;

                String combHttpExceptionStr = String.format("%s:%s", httpException.code(), httpException.message());
                sendHttpLog(t, "http状态异常:" + combHttpExceptionStr);
                if (httpException.code() == 401) {
                    //强制登陆  token过期
                    try {
                        Activity activity = getAppManager().currentActivity();
                        if (!(activity instanceof LoginBaseActivity)
                                && !SystemUtils.isDestroyOrFinishing(activity)) {
                            LoginSelectActivity.launch(activity);
                        }
                    } catch (Exception e) {
                    }
                    return;
                }
                defNotify(iDefNotify, combHttpExceptionStr);
            } else if (t instanceof JsonParseException) {
                defNotify(iDefNotify, "服务器Json格式错误");

                sendHttpLog(t, "json解析异常");
            } else if (t instanceof java.net.UnknownHostException) {
                defNotify(iDefNotify, "网络已断开,请检查网络");
            } else if (t instanceof NoRouteToHostException) {
                defNotify(iDefNotify, "服务器路由地址错误");
            } else if (t instanceof ConnectException) {
                if (NetUtils.hasNetwork(BaseApplication.getApplication())) {
                    defNotify(iDefNotify, "服务器拒绝连接");
                } else {
                    defNotify(iDefNotify, "网络未连接");
                }
            } else if (t instanceof SocketException) {
                defNotify(iDefNotify, "网络不稳定或服务器繁忙");
            } else if (t instanceof SocketTimeoutException) {
                defNotify(iDefNotify, "服务器响应超时");

                sendHttpLog(t, "服务器响应超时");
            } else if (t instanceof FileNotFoundException) {
                defNotify(iDefNotify, "文件权限被拒绝或文件找不到");
                sendHttpLog(t, "文件权限被拒绝或文件找不到");
            } else {
                defNotify(iDefNotify, "未知异常");
                sendHttpLog(t, "未知异常");
            }
        }
    }

    /**
     * 发送http日志
     *
     * @param t
     * @param throwableTypeDesc
     */
    private static void sendHttpLog(Throwable t, String throwableTypeDesc) {
        BugUtils.bugSync(throwableTypeDesc, t);
    }

    /**
     * 系统可能屏蔽通知[典型的华为]
     * 替代为Snackbar
     *
     * @param noticeStr
     */
    @UiThread
    private static final void defNotify(@android.support.annotation.NonNull IDefNotify iDefNotify,
                                        String noticeStr) {
        if (iDefNotify != null) {
            iDefNotify.defNotify(noticeStr);
        }
    }

    /**
     * 默认的网络提示
     * 注意自子线程的问题
     *
     * @param noticeStr
     */
    public static final void defaultNotify(final String noticeStr) {
        if (TextUtils.isEmpty(noticeStr)) return;
        if (!SystemUtils.isMainThread()) return;
        Activity currentActivity = null;
        try {
            currentActivity = getAppManager().currentActivity();
        } catch (Exception e) {
        }
        if (currentActivity != null && !currentActivity.isFinishing()) {
            if (SystemUtils.isMainThread()) {
                try {
                    SnackbarUtils.showTopSnackBarWithError(currentActivity, noticeStr);
                } catch (Throwable e) {
                }
            } else {
                final Activity finalCurrentActivity = currentActivity;
                mHandler.removeCallbacksAndMessages(null);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            SnackbarUtils.showTopSnackBarWithError(finalCurrentActivity, noticeStr);
                        } catch (Throwable e) {
                        }
                    }
                });
            }
        }
    }
}
