package com.icourt.alpha.base;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.support.multidex.MultiDexApplication;

import com.icourt.alpha.BuildConfig;
import com.icourt.alpha.http.HConst;
import com.icourt.alpha.utils.AppManager;
import com.icourt.alpha.utils.LogUtils;
import com.icourt.alpha.utils.logger.AndroidLogAdapter;
import com.icourt.alpha.utils.logger.LogLevel;
import com.icourt.alpha.utils.logger.Logger;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：17/3/28
 * version
 */

public class BaseApplication extends MultiDexApplication
        implements Application.ActivityLifecycleCallbacks {

    {// 友盟登陆/分享初始化
        PlatformConfig.setWeixin(HConst.WX_APPID, HConst.WX_APPSECRET);
        //PlatformConfig.setQQZone("1104872033", "lLB4ODaOnpLNzIxD");
    }

    private static BaseApplication baseApplication;

    @Override
    public void onCreate() {
        super.onCreate();
        baseApplication = this;
        this.registerActivityLifecycleCallbacks(this);
        UMShareAPI.get(this);
        initLogger();
    }

    public static BaseApplication getApplication() {
        return baseApplication;
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

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        AppManager.getAppManager().addActivity(activity);
        LogUtils.d("===========>onActivityCreated:" + activity + " savedInstanceState:" + savedInstanceState);
    }

    @Override
    public void onActivityStarted(Activity activity) {
        LogUtils.d("===========>onActivityStarted:" + activity);
    }

    @Override
    public void onActivityResumed(Activity activity) {
        LogUtils.d("===========>onActivityResumed:" + activity);
    }

    @Override
    public void onActivityPaused(Activity activity) {
        LogUtils.d("===========>onActivityPaused:" + activity);
    }

    @Override
    public void onActivityStopped(Activity activity) {
        LogUtils.d("===========>onActivityStopped:" + activity);
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        LogUtils.d("===========>onActivitySaveInstanceState:" + activity);
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        AppManager.getAppManager().removeActivity(activity);
        LogUtils.d("===========>onActivityDestroyed:" + activity);
    }
}
