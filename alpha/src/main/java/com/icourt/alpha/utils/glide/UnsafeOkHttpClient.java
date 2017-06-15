package com.icourt.alpha.utils.glide;

import android.annotation.SuppressLint;

import com.icourt.alpha.http.AlphaClient;
import com.icourt.alpha.utils.LogUtils;
import com.icourt.api.apiConfig;

import java.io.IOException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

public class UnsafeOkHttpClient {
    public static OkHttpClient getUnsafeOkHttpClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        //builder.retryOnConnectionFailure(true);
        //builder.cookieJar(cookieJar);
        builder.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                Request requestBuilder = request.newBuilder()
                        .addHeader("Cookie", "officeId=" + AlphaClient.getOfficeId())
                        .build();
                Response response = chain.proceed(requestBuilder);
                return response;
            }
        });
        builder.addInterceptor(new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String s) {
                LogUtils.d("------------->glide:" + s);
            }
        }).setLevel(HttpLoggingInterceptor.Level.BODY));
        builder.writeTimeout(apiConfig.SOCKET_WRITE_TIME_OUT, TimeUnit.MILLISECONDS);
        builder.connectTimeout(apiConfig.SOCKET_TIME_OUT, TimeUnit.MILLISECONDS);
        builder.readTimeout(apiConfig.SOCKET_RESPONSE_TIME_OUT, TimeUnit.MILLISECONDS);
        builder.sslSocketFactory(createSSLSocketFactory());
        builder.hostnameVerifier(new TrustAllHostnameVerifier());
        return builder.build();
    }

    /**
     * 默认信任所有的证书
     * TODO 最好加上证书认证，主流App都有自己的证书
     *
     * @return
     */
    @SuppressLint("TrulyRandom")
    private static SSLSocketFactory createSSLSocketFactory() {

        SSLSocketFactory sSLSocketFactory = null;

        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, new TrustManager[]{new TrustAllManager()},
                    new SecureRandom());
            sSLSocketFactory = sc.getSocketFactory();
        } catch (Exception e) {
        }

        return sSLSocketFactory;
    }

    private static class TrustAllManager implements X509TrustManager {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }

    private static class TrustAllHostnameVerifier implements HostnameVerifier {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }
}