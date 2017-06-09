package com.icourt.alpha.http;


import com.icourt.alpha.BuildConfig;
import com.icourt.alpha.base.BaseApplication;

import java.util.concurrent.ConcurrentHashMap;

import okhttp3.OkHttpClient;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：17/3/29
 * version
 */
public class RetrofitServiceFactory {
    private static final ConcurrentHashMap<Class, Object> ApiServiceMap = new ConcurrentHashMap<>();

    private static final AlphaClient getClient(String url) {
        return AlphaClient.getInstance(BaseApplication.getApplication(), url);
    }

    private static final <T> T getApiService(String url, Class<T> t) {
        if (ApiServiceMap.get(t) == null) {
            ApiServiceMap.put(t, getClient(url).createService(t));
        }
        return (T) ApiServiceMap.get(t);
    }

    public static final ApiAlphaService getAlphaApiService() {
        return getApiService(BuildConfig.API_URL, ApiAlphaService.class);
    }

    public static final ApiChatService getChatApiService() {
        return getApiService(BuildConfig.API_CHAT_URL, ApiChatService.class);
    }

    public static final ApiProjectService getProjectApiService() {
        return getApiService(BuildConfig.API_URL, ApiProjectService.class);
    }

    public static final ApiSFileService getSFileApiService() {
        return getApiService(BuildConfig.API_SFILE_URL, ApiSFileService.class);
    }

    private static OkHttpClient mOkHttpClient;

    public static final OkHttpClient provideOkHttpClient() {
        if (mOkHttpClient == null) {
            synchronized (RetrofitServiceFactory.class) {
                mOkHttpClient = new OkHttpClient();
            }
        }
        return mOkHttpClient;
    }
}

