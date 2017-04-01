package com.icourt.alpha.http.callback;

import android.app.Activity;
import android.text.TextUtils;
import android.view.TextureView;

import com.google.gson.JsonParseException;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.utils.AppManager;
import com.icourt.alpha.utils.LogUtils;
import com.icourt.alpha.utils.SnackbarUtils;

import java.net.ConnectException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.NoSuchElementException;

import retrofit2.Call;
import retrofit2.Response;


/**
 * @author xuanyouwu
 * @email xuanyouwu@163.com
 * @time 2016-04-20 18:38
 */
public abstract class SimpleCallBack<T> extends BaseCallBack<ResEntity<T>> {

    public static class ResponseException extends RuntimeException {
        public final int code;
        public final String message;


        public ResponseException(int code, String message) {
            this.code = code;
            this.message = message;
        }

        @Override
        public String toString() {
            return "ResponseException{" +
                    "code=" + code +
                    ", message='" + message + '\'' +
                    '}';
        }
    }

    @Override
    protected void dispatchHttpSuccess(Call<ResEntity<T>> call, Response<ResEntity<T>> response) {
        if (response.body() != null && response.body().succeed) {
            onSuccess(call, response);
        } else {
            onFailure(call,
                    response.body() != null ?
                            new ResponseException(-2, response.body().message)
                            : new ResponseException(-1, "响应为null"));
        }
    }

    @Override
    public void onFailure(Call<ResEntity<T>> call, Throwable t) {
        super.onFailure(call, t);
        if (t instanceof ResponseException) {
            defNotify(((ResponseException) t).message);

        } else if (t instanceof retrofit2.HttpException) {
            retrofit2.HttpException httpException = (retrofit2.HttpException) t;
            String combHttpExceptionStr = String.format("%s:%s", httpException.code(), httpException.message());
            defNotify(combHttpExceptionStr);

            sendLimitHttpLog(call, t, "http状态异常:" + combHttpExceptionStr);
        } else if (t instanceof JsonParseException) {
            defNotify("解析异常,PHP在弄啥呢？");

            sendLimitHttpLog(call, t, "json解析异常");
        } else if (t instanceof java.net.UnknownHostException) {
            defNotify("网络已断开,请检查网络");
        } else if (t instanceof ConnectException) {
            defNotify("服务器拒绝连接或代理错误");
        } else if (t instanceof SocketException) {
            defNotify("网络不稳定或服务器繁忙");
        } else if (t instanceof SocketTimeoutException) {
            defNotify("服务器响应超时");

            sendLimitHttpLog(call, t, "服务器响应超时");
        } else {
            defNotify("未知异常");
        }
        LogUtils.d("http", "------->throwable:" + t);
    }


    /**
     * 系统可能屏蔽通知[典型的华为]
     * 替代为Snackbar
     *
     * @param noticeStr
     */
    public void defNotify(String noticeStr) {
        if (TextUtils.isEmpty(noticeStr)) return;
        Activity currentActivity = null;
        try {
            currentActivity = AppManager.getAppManager().currentActivity();
        } catch (Exception e) {
        }
        if (currentActivity != null && !currentActivity.isFinishing()) {
            try {
                SnackbarUtils.showTopSnackBarWithError(currentActivity, noticeStr);
            } catch (Throwable e) {
            }
        }
/*
        boolean enableNotification = SystemUtils.isEnableNotification(BaseApplication.getApplication());
        if (enableNotification) {
            ToastUtils.showFillToast(noticeStr);
        } else {
            try {
                Activity topActivity = AppManager.getAppManager().currentActivity();
                if (topActivity != null && !topActivity.isFinishing()) {
                    SnackbarUtils.showSnack(topActivity, noticeStr);
                }
            } catch (Exception e) {
            }
        }*/

    }


}
