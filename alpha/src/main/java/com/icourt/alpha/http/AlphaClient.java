package com.icourt.alpha.http;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;

import com.icourt.alpha.BuildConfig;
import com.icourt.alpha.utils.BugUtils;
import com.icourt.alpha.utils.LogUtils;
import com.icourt.api.SimpleClient;
import com.orhanobut.logger.Logger;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

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
public class AlphaClient extends SimpleClient implements HttpLoggingInterceptor.Logger {
    private static final ConcurrentHashMap<String, AlphaClient> clientMap = new ConcurrentHashMap<>();

    /**
     * 获取指定主机的地址api服务
     *
     * @param context
     * @param api_url
     * @return
     */
    public static AlphaClient getInstance(Application context, String api_url) {
        AlphaClient alphaClient;
        if (clientMap.get(api_url) == null) {
            clientMap.put(api_url, alphaClient = new AlphaClient(context, api_url));
        } else {
            alphaClient = clientMap.get(api_url);
        }
        return alphaClient;
    }

    private static String officeId;
    private static String token;
    private static String sFileToken;

    public static void setToken(String tk) {
        token = tk;
    }

    public static String getToken() {
        return String.valueOf(token);
    }

    public static String getOfficeId() {
        return String.valueOf(officeId);
    }

    public static void setOfficeId(String ofId) {
        officeId = ofId;
    }

    public static String getSFileToken() {
        return String.valueOf(sFileToken);
    }

    public static void setSFileToken(String sftk) {
        sFileToken = sftk;
    }

    private AlphaClient(Context context, String api_url) {
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor(this)
                .setLevel(BuildConfig.IS_DEBUG ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE);
        attachBaseUrl(
                context,
                api_url,
                httpLoggingInterceptor);
    }

    @Override
    public void log(String message) {
        logHttp(message);
    }


    protected void logHttp(String message) {
        if (TextUtils.isEmpty(message)) return;
        try {
            LogUtils.d("logger-http", message);
            if (message.startsWith("{") && message.endsWith("}")) {
                Logger.t("http-format").json(message);
            } else if (message.startsWith("[") && message.endsWith("]")) {
                Logger.t("http-format").json(message);
            }
        } catch (OutOfMemoryError e) {
            System.gc();
            e.printStackTrace();
            BugUtils.bugSync("logHttp OutOfMemoryError:", e);
        }
    }


    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Request requestBuilder = request.newBuilder()
                .addHeader("Authorization", String.format("Token %s", getSFileToken()))
                .addHeader("Cookie", "officeId=" + getOfficeId())
                .addHeader("token", getToken())
                .addHeader("osVer", String.valueOf(Build.VERSION.SDK_INT))
                .addHeader("osType", HConst.OS_TYPE)
                .addHeader("appVer", BuildConfig.VERSION_NAME)
                .addHeader("appVersion", "v2.1.0")
                .addHeader("buildVer", String.valueOf(BuildConfig.VERSION_CODE))
                .build();
        Response response = chain.proceed(requestBuilder);
        return response;
    }
}
