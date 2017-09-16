package com.icourt.alpha.http.callback;

import android.app.Activity;
import android.support.annotation.CallSuper;
import android.text.TextUtils;

import com.bugtags.library.Bugtags;
import com.google.gson.JsonParseException;
import com.icourt.alpha.activity.LoginBaseActivity;
import com.icourt.alpha.activity.LoginSelectActivity;
import com.icourt.alpha.base.BaseApplication;
import com.icourt.alpha.entity.bean.AlphaUserInfo;
import com.icourt.alpha.http.exception.ResponseException;
import com.icourt.alpha.utils.AppManager;
import com.icourt.alpha.utils.LogUtils;
import com.icourt.alpha.utils.LoginInfoUtils;
import com.icourt.alpha.utils.NetUtils;
import com.icourt.alpha.utils.SnackbarUtils;
import com.icourt.alpha.utils.StringUtils;
import com.icourt.alpha.utils.SystemUtils;

import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ConnectException;
import java.net.NoRouteToHostException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.charset.Charset;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okio.Buffer;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.icourt.alpha.utils.AppManager.getAppManager;

/**
 * @author xuanyouwu
 * @email xuanyouwu@163.com
 * @time 2016-04-20 18:38
 */
public abstract class BaseCallBack<T> implements Callback<T> {
    private static final Charset UTF8 = Charset.forName("UTF-8");

    @Override
    public final void onResponse(Call<T> call, Response<T> response) {
        dispatchResponse(call, response);
    }

    private void dispatchResponse(Call<T> call, Response<T> response) {
        if (response.code() == 200) {
            dispatchHttpSuccess(call, response);
        } else {
            onFailure(call, new retrofit2.HttpException(response));
        }
    }

    protected abstract void dispatchHttpSuccess(Call<T> call, Response<T> response);

    public abstract void onSuccess(Call<T> call, Response<T> response);

    @CallSuper
    @Override
    public void onFailure(Call<T> call, Throwable t) {
        if (call != null && call.isCanceled()) {
            LogUtils.d("http", "------->canceled");
            return;
        }
        if (t instanceof ResponseException) {
            defNotify(((ResponseException) t).message);

        } else if (t instanceof retrofit2.HttpException) {
            retrofit2.HttpException httpException = (retrofit2.HttpException) t;

            String combHttpExceptionStr = String.format("%s:%s", httpException.code(), httpException.message());
            sendLimitHttpLog(call, t, "http状态异常:" + combHttpExceptionStr);
            if (httpException.code() == 401) {
                //强制登陆  token过期
                try {
                    Activity activity = AppManager.getAppManager().currentActivity();
                    if (!(activity instanceof LoginBaseActivity)
                            && !SystemUtils.isDestroyOrFinishing(activity)) {
                        LoginSelectActivity.launch(activity);
                    }
                } catch (Exception e) {
                }
                return;
            }
            defNotify(combHttpExceptionStr);
        } else if (t instanceof JsonParseException) {
            defNotify("服务器Json格式错误");

            sendLimitHttpLog(call, t, "json解析异常");
        } else if (t instanceof java.net.UnknownHostException) {
            defNotify("网络已断开,请检查网络");
        } else if (t instanceof NoRouteToHostException) {
            defNotify("服务器路由地址错误");
        } else if (t instanceof ConnectException) {
            if (NetUtils.hasNetwork(BaseApplication.getApplication())) {
                defNotify("服务器拒绝连接");
            } else {
                defNotify("网络未连接");
            }
        } else if (t instanceof SocketException) {
            defNotify("网络不稳定或服务器繁忙");
        } else if (t instanceof SocketTimeoutException) {
            defNotify("服务器响应超时");

            sendLimitHttpLog(call, t, "服务器响应超时");
        } else if (t instanceof FileNotFoundException) {
            defNotify("文件权限被拒绝或文件找不到");
            sendLimitHttpLog(call, t, "文件权限被拒绝或文件找不到");
        } else {
            defNotify("未知异常");
            sendLimitHttpLog(call, t, "未知异常");
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
            currentActivity = getAppManager().currentActivity();
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

    /**
     * 发送http错误日志
     *
     * @param call
     * @param t
     */
    protected void sendHttpLog(Call call, Throwable t, String throwableTypeDesc) {
        if (call == null) return;
        if (t == null) return;
        try {
            StringBuilder httpLogBuilder = new StringBuilder();
            httpLogBuilder.append("API接口错误日志:");
            httpLogBuilder.append("\n错误描述:" + throwableTypeDesc);
            if (call.request() != null) {
                Request request = call.request();
                httpLogBuilder.append("\napi:" + request);
                //httpLogBuilder.append("\nheaders:" + (request.headers() != null ? request.headers().toString() : "null"));
                httpLogBuilder.append("\nbody:" + body2String(request.body()));
            }
            // httpLogBuilder.append("\nuid:" + getLoginUid());
            httpLogBuilder.append("\n错误信息:" + StringUtils.throwable2string(t));
            //发生的用户信息
            AlphaUserInfo loginUserInfo = LoginInfoUtils.getLoginUserInfo();
            httpLogBuilder.append("\nuserInfo:" + loginUserInfo);

            sendHttpLog(httpLogBuilder.toString());

        } catch (Throwable e) {
            e.printStackTrace();
            sendHttpLog("API日志记录异常:" + StringUtils.throwable2string(e));
        }
    }


    /**
     * 是否限制上传日志
     *
     * @param call
     * @param t
     * @param throwableTypeDesc
     */
    protected void sendLimitHttpLog(Call call, Throwable t, String throwableTypeDesc) {
        if (!isInterceptHttpLog()) {//拦截上传 否则日志过多
            sendHttpLog(call, t, throwableTypeDesc);
        }
    }

    /**
     * 是否拦截http 日志传递到bugtags
     * 解决方案:1:限制uid uid为单数或者未登录(未登录uid=null) 2:限制时间(拦截双秒请求日志上传)
     *
     * @return
     */
    protected boolean isInterceptHttpLog() {
        long currentSecond = System.currentTimeMillis() / 1_000;
        return false;
    }

    /**
     * 请求体转字符串
     *
     * @param requestBody
     * @return
     * @throws IOException
     */
    protected String body2String(RequestBody requestBody) throws IOException {
        if (requestBody == null) return "body==null";
        Buffer buffer = new Buffer();
        requestBody.writeTo(buffer);
        Charset charset = UTF8;
        MediaType contentType = requestBody.contentType();
        if (contentType != null) {
            charset = contentType.charset(UTF8);
        }
        if (isPlaintext(buffer)) {
            return buffer.readString(charset);
        } else {
            return "二进制 length" + requestBody.contentLength();
        }
    }

    /**
     * 是否是文本
     *
     * @param buffer
     * @return
     */
    private boolean isPlaintext(Buffer buffer) {
        try {
            Buffer prefix = new Buffer();
            long byteCount = buffer.size() < 64 ? buffer.size() : 64;
            buffer.copyTo(prefix, 0, byteCount);
            for (int i = 0; i < 16; i++) {
                if (prefix.exhausted()) {
                    break;
                }
                int codePoint = prefix.readUtf8CodePoint();
                if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) {
                    return false;
                }
            }
            return true;
        } catch (EOFException e) {
            return false; // Truncated UTF-8 sequence.
        }
    }

    /**
     * 发送http日志
     *
     * @param httpLog
     */
    protected void sendHttpLog(String httpLog) {
        if (!TextUtils.isEmpty(httpLog)) {
            Bugtags.sendFeedback(httpLog);
        }
    }
}
