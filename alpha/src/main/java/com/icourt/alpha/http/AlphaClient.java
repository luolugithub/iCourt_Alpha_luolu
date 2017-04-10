package com.icourt.alpha.http;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;

import com.icourt.alpha.BuildConfig;
import com.icourt.alpha.base.BaseApplication;
import com.icourt.alpha.utils.LogUtils;
import com.icourt.alpha.utils.logger.Logger;
import com.icourt.api.SimpleClient;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：17/3/29
 * version
 */
public class AlphaClient extends SimpleClient implements HttpLoggingInterceptor.Logger, Interceptor {
    private static AlphaClient mInstance;

    public static AlphaClient getInstance() {
        if (mInstance == null) {
            synchronized (AlphaClient.class) {
                if (mInstance == null) {
                    mInstance = new AlphaClient(BaseApplication.getApplication());
                }
            }
        }
        return mInstance;
    }

    private String officeId;
    private String token;

    public void setToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return String.valueOf(token);
    }

    public String getOfficeId() {
        return String.valueOf(officeId);
    }

    public void setOfficeId(String officeId) {
        this.officeId = officeId;
    }

    private AlphaClient(Context context) {
        attachBaseUrl(context, BuildConfig.API_URL, this, this);
    }

    @Override
    public void log(String message) {
        if (HConst.HTTP_LOG_ENABLE) {
            logHttp(message);
        }
    }

    protected void logHttp(String message) {
        if (TextUtils.isEmpty(message)) return;
        LogUtils.d("logger-http", message);
        if (message.startsWith("{") && message.endsWith("}")) {
            Logger.t("http-format").json(message);
        } else if (message.startsWith("[") && message.endsWith("]")) {
            Logger.t("http-format").json(message);
        }
    }


    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Request requestBuilder = request.newBuilder()
                .addHeader("Cookie", getOfficeId())
                .addHeader("token", getToken())
                .addHeader("osVer", String.valueOf(Build.VERSION.SDK_INT))
                .addHeader("osType", HConst.OS_TYPE)
                .addHeader("appVer", BuildConfig.VERSION_NAME)
                .build();
        Response response = chain.proceed(requestBuilder);
        String cookeHeader = response.header("Set-Cookie", "");
        if (!TextUtils.isEmpty(cookeHeader)) {
            HConst.cookie = cookeHeader.split(";")[0];
        }
        return response;
    }
}
