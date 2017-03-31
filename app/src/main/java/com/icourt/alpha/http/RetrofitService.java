package com.icourt.alpha.http;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.icourt.alpha.BuildConfig;
import com.icourt.alpha.utils.JsonUtils;
import com.icourt.alpha.utils.LogUtils;
import com.icourt.alpha.utils.logger.Logger;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * ClassName RetrofitService
 * Description
 * Company
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2015/12/24 10:42
 * version
 */
public class RetrofitService {
    private static RetrofitService retrofitService;
    private ApiService apiCacheRetryService;//http协议缓存 重连 适合做数据获取
    public String token;

    public static RetrofitService getInstance() {
        if (retrofitService == null) {
            retrofitService = new RetrofitService();
        }
        return retrofitService;
    }

    public void restLoginInfo(String token) {
        this.token = token;
    }


    public RetrofitService() {
        initRetrofit();
    }

    public String getToken() {
        return String.valueOf(token);
    }

    public String getCookie() {
        return String.valueOf(HConst.cookie);
    }

    public ApiService getApiService() {
        return apiCacheRetryService;
    }

    private void initRetrofit() {
        apiCacheRetryService = getRetrofit(getOkHttpClient(true, true)).create(ApiService.class);
    }


    public OkHttpClient getOkHttpClient(boolean cache, boolean retry) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
       /* if (cache) {
            File httpCacheDirectory = new File(context.getCacheDir(), "responses");
            if (!httpCacheDirectory.exists())
                httpCacheDirectory.mkdirs();
            builder.cache(new Cache(httpCacheDirectory, HConst.CACHE_MAXSIZE));
        }*/
        builder.retryOnConnectionFailure(retry);
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                if (HConst.HTTP_LOG_ENABLE && !TextUtils.isEmpty(message)) {
                    LogUtils.d("logger-http", message);
                    if (message.startsWith("{") && message.endsWith("}")) {
                        Logger.t("http-format").json(message);
                    } else if (message.startsWith("[") && message.endsWith("]")) {
                        Logger.t("http-format").json(message);
                    }
                }
            }
        });
        builder.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                Request requestBuilder = request.newBuilder()
                        .addHeader("Cookie", getCookie())
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
        });
        loggingInterceptor.setLevel(HConst.HTTP_LOG_ENABLE ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE);
        builder.addInterceptor(loggingInterceptor);
        builder.connectTimeout(HConst.SOCKET_TIME_OUT, TimeUnit.MILLISECONDS);
        builder.readTimeout(HConst.SOCKET_RESPONSE_TIME_OUT, TimeUnit.MILLISECONDS);

        /*int[] certificates = {R.raw.myssl};//cer文件
        String hosts[] = {HConst.BASE_DEBUG_URL, HConst.BASE_PREVIEW_URL, HConst.BASE_RELEASE_URL, HConst.BASE_RELEASE_SHARE_URL};
        builder.socketFactory(HttpsFactroy.getSSLSocketFactory(context, certificates));
        builder.hostnameVerifier(HttpsFactroy.getHostnameVerifier(hosts));*/

        return builder.build();
    }

    public Retrofit getRetrofit(OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .baseUrl(HConst.BASE_HTTP)
                //.addConverterFactory(ProtoConverterFactory.create())//适合数据同步
                .addConverterFactory(GsonConverterFactory.create(JsonUtils.getGson()))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(okHttpClient)
                .build();
    }


    /**
     * 构建文本请求体
     *
     * @param text
     * @return
     */
    public static RequestBody createTextBody(@Nullable String text) {
        return RequestBody
                .create(MediaType.parse("text/plain"),
                        TextUtils.isEmpty(text) ? "" : text);
    }


    /**
     * 构建图片请求体
     *
     * @param file
     * @return
     */
    public static RequestBody createImgBody(@NonNull File file) {
        if (file != null && file.exists()) {
            return RequestBody.create(MediaType.parse("image/png"), file);
        }
        return null;
    }

}

