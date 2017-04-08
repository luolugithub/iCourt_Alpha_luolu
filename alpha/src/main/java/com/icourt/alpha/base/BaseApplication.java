package com.icourt.alpha.base;

import android.content.Context;
import android.graphics.Color;
import android.support.multidex.MultiDexApplication;

import com.bugtags.library.Bugtags;
import com.bugtags.library.BugtagsOptions;
import com.icourt.alpha.BuildConfig;
import com.icourt.alpha.R;
import com.icourt.alpha.http.HConst;
import com.icourt.alpha.utils.ActivityLifecycleTaskCallbacks;
import com.icourt.alpha.utils.GlideImageLoader;
import com.icourt.alpha.utils.logger.AndroidLogAdapter;
import com.icourt.alpha.utils.logger.LogLevel;
import com.icourt.alpha.utils.logger.Logger;
import com.liulishuo.filedownloader.FileDownloader;
import com.liulishuo.filedownloader.util.FileDownloadHelper;
import com.liulishuo.filedownloader.util.FileDownloadLog;
import com.umeng.socialize.Config;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.utils.Log;

import java.net.Proxy;
import java.util.concurrent.TimeUnit;

import cn.finalteam.galleryfinal.CoreConfig;
import cn.finalteam.galleryfinal.FunctionConfig;
import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.ImageLoader;
import cn.finalteam.galleryfinal.ThemeConfig;
import io.realm.Realm;
import okhttp3.OkHttpClient;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：17/3/28
 * version
 */

public class BaseApplication extends MultiDexApplication {

    {// 友盟登陆/分享初始化
        PlatformConfig.setWeixin(HConst.WX_APPID, HConst.WX_APPSECRET);
        //PlatformConfig.setQQZone("1104872033", "lLB4ODaOnpLNzIxD");
        Config.isJumptoAppStore = false; //其中qq 微信会跳转到下载界面进行下载，其他应用会跳到应用商店进行下载
        Log.LOG = BuildConfig.IS_DEBUG;//umeng sdk日志跟踪
    }

    private static BaseApplication baseApplication;

    @Override
    public void onCreate() {
        super.onCreate();
        baseApplication = this;
        initActivityLifecycleCallbacks();
        initRealm();
        initUMShare();
        initLogger();
        initDownloader();
        initBugtags();
        initGalleryFinal();
    }


    private void initRealm() {
        Realm.init(this);
    }

    public static BaseApplication getApplication() {
        return baseApplication;
    }


    /**
     * 初始化activity生命周期监听
     */
    public void initActivityLifecycleCallbacks() {
        this.registerActivityLifecycleCallbacks(new ActivityLifecycleTaskCallbacks());
    }

    /**
     * 初始化umeng
     */
    private void initUMShare() {
        UMShareAPI.get(this);
    }

    /**
     * 初始化比较友好的日志工具
     */
    private void initLogger() {
        Logger.init("logger")                 // default PRETTYLOGGER or use just init()
                .methodCount(0)                 // default 2
                .hideThreadInfo()               // default shown
                .logLevel(BuildConfig.IS_DEBUG ? LogLevel.FULL : LogLevel.NONE)        // default LogLevel.FULL
                .methodOffset(0)                // default 0
                .logAdapter(new AndroidLogAdapter()); //default AndroidLogAdapter
    }

    /**
     * 初始化下载
     */
    private void initDownloader() {

       /* //方式1
        FileDownloader.init(getApplicationContext());
        */


        //方式2
        FileDownloadLog.NEED_LOG = BuildConfig.IS_DEBUG;

        /**
         * just for cache Application's Context, and ':filedownloader' progress will NOT be launched
         * by below code, so please do not worry about performance.
         * @see FileDownloader#init(Context)
         */
        FileDownloader.init(getApplicationContext(),
                new FileDownloadHelper.OkHttpClientCustomMaker() { // is not has to provide.
                    @Override
                    public OkHttpClient customMake() {
// just for OkHttpClient customize.
                        final OkHttpClient.Builder builder = new OkHttpClient.Builder();
                        // you can set the connection timeout.
                        builder.connectTimeout(35_000, TimeUnit.MILLISECONDS);
                        // you can set the HTTP proxy.
                        builder.proxy(Proxy.NO_PROXY);
                        // etc.
                        return builder.build();
                    }
                });

    }

    /**
     * 配置galleryfinal
     */
    private void initGalleryFinal() {
        ThemeConfig themeConfig = new ThemeConfig.Builder()
                .setTitleBarTextColor(getResources().getColor(R.color.alpha_font_color_black))
                .setTitleBarBgColor(Color.WHITE)
                .setTitleBarIconColor(getResources().getColor(R.color.alpha_font_color_orange))
                .build();

        FunctionConfig.Builder functionConfigBuilder = new FunctionConfig.Builder();
        ImageLoader imageLoader = new GlideImageLoader();
        functionConfigBuilder.setMutiSelectMaxSize(9);
        functionConfigBuilder.setEnableEdit(true);
        functionConfigBuilder.setEnableRotate(true);
        functionConfigBuilder.setRotateReplaceSource(true);
        functionConfigBuilder.setEnableCrop(true);
        functionConfigBuilder.setCropSquare(true);
        functionConfigBuilder.setEnableCamera(true);
        functionConfigBuilder.setEnablePreview(true);

        CoreConfig coreConfig = new CoreConfig.Builder(this, imageLoader, themeConfig)
                .setFunctionConfig(functionConfigBuilder.build())
                .setPauseOnScrollListener(null)
                .setNoAnimcation(false)
                .build();
        GalleryFinal.init(coreConfig);
    }

    private void initBugtags() {
        BugtagsOptions options = new BugtagsOptions.Builder()
                .trackingCrashLog(!BuildConfig.IS_DEBUG)//是否收集crash !BuildConfig.IS_DEBUG
                //  trackingLocation(true).//是否获取位置
                .startAsync(true)
                .trackingConsoleLog(true)//是否收集console log
                .uploadDataOnlyViaWiFi(true)//wifi 上传
                .trackingUserSteps(true)//是否收集用户操作步骤
                //.trackingNetworkURLFilter("(.*)")//自定义网络请求跟踪的 url 规则，默认 null
                .versionName(BuildConfig.VERSION_NAME)//自定义版本名称
                .versionCode(BuildConfig.VERSION_CODE)//自定义版本号
                .build();
        Bugtags.start("10420c3f18b352cf5613d9eb786a6e09", this, Bugtags.BTGInvocationEventNone, options);
    }


}
