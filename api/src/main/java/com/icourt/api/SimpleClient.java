package com.icourt.api;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * @author xuanyouwu
 * @email xuanyouwu@163.com
 * @time 2016-06-02 14:26
 */
public class SimpleClient extends BaseClient {

    protected void attachBaseUrl(Context context, String baseUrl, HttpLoggingInterceptor.Logger logger, Interceptor interceptor) {
        //okhttp3 cookie 持久化
        // ClearableCookieJar cookieJar = new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(context));

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.retryOnConnectionFailure(true);
        //builder.cookieJar(cookieJar);
        builder.addInterceptor(interceptor);
        builder.addInterceptor(new HttpLoggingInterceptor(logger).setLevel(HttpLoggingInterceptor.Level.BODY));
        builder.connectTimeout(apiConfig.SOCKET_TIME_OUT, TimeUnit.MILLISECONDS);
        builder.readTimeout(apiConfig.SOCKET_RESPONSE_TIME_OUT, TimeUnit.MILLISECONDS);

        /*int[] certificates = {R.raw.myssl};//cer文件
        String hosts[] = {HConst.BASE_DEBUG_URL, HConst.BASE_PREVIEW_URL, HConst.BASE_RELEASE_URL, HConst.BASE_RELEASE_SHARE_URL};
        builder.socketFactory(HttpsFactroy.getSSLSocketFactory(context, certificates));
        builder.hostnameVerifier(HttpsFactroy.getHostnameVerifier(hosts));*/

        super.attachBaseUrl(builder.build(), baseUrl);
    }

    @Override
    public final void attachBaseUrl(OkHttpClient client, String baseUrl) {
        super.attachBaseUrl(client, baseUrl);
    }
}
