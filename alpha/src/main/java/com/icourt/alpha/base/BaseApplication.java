package com.icourt.alpha.base;

import android.graphics.Color;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.support.multidex.MultiDexApplication;

import com.bugtags.library.Bugtags;
import com.bugtags.library.BugtagsOptions;
import com.icourt.alpha.BuildConfig;
import com.icourt.alpha.R;
import com.icourt.alpha.activity.ChatActivity;
import com.icourt.alpha.entity.bean.AlphaUserInfo;
import com.icourt.alpha.http.HConst;
import com.icourt.alpha.utils.ActivityLifecycleTaskCallbacks;
import com.icourt.alpha.utils.GlideImageLoader;
import com.icourt.alpha.utils.LogUtils;
import com.icourt.alpha.utils.LoginInfoUtils;
import com.icourt.alpha.utils.SpUtils;
import com.icourt.alpha.utils.SystemUtils;
import com.icourt.alpha.utils.UserPreferences;
import com.icourt.alpha.utils.logger.AndroidLogAdapter;
import com.icourt.alpha.utils.logger.LogLevel;
import com.icourt.alpha.utils.logger.Logger;
import com.icourt.alpha.widget.nim.AlphaMessageNotifierCustomization;
import com.icourt.alpha.widget.parser.CustomAttachParser;
import com.liulishuo.filedownloader.FileDownloader;
import com.liulishuo.filedownloader.util.FileDownloadHelper;
import com.liulishuo.filedownloader.util.FileDownloadLog;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.SDKOptions;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.team.constant.TeamFieldEnum;
import com.netease.nimlib.sdk.team.model.IMMessageFilter;
import com.netease.nimlib.sdk.team.model.UpdateTeamAttachment;
import com.umeng.socialize.Config;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.utils.Log;

import java.net.Proxy;
import java.util.Map;
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
        initStrictMode();
        initActivityLifecycleCallbacks();
        initRealm();
        initEmoji();
        initYunXin();
        initUMShare();
        initLogger();
        initDownloader();
        initBugtags();
        initGalleryFinal();
    }


    /**
     * 初始化StrictMode
     */
    private void initStrictMode() {
        //必须添加  否则FileUriExposedException
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }
    }

    private void initEmoji() {

        //EmojiManager.install(new EmojiOneProvider());
    }

    private void initYunXin() {
        LoginInfo loginInfo = null;
        AlphaUserInfo loginUserInfo = LoginInfoUtils.getLoginUserInfo();
        if (loginUserInfo != null) {
            loginInfo = new LoginInfo(loginUserInfo.getThirdpartId(), loginUserInfo.getChatToken());
        }
        SDKOptions options = new SDKOptions();

        // 如果将新消息通知提醒托管给SDK完成，需要添加以下配置。
        com.netease.nimlib.sdk.StatusBarNotificationConfig config = UserPreferences.getStatusConfig();
        if (config == null) {
            config = new com.netease.nimlib.sdk.StatusBarNotificationConfig();
        }
        // 点击通知需要跳转到的界面
        config.notificationEntrance = ChatActivity.class;//通知栏提醒的响应intent的activity类型
        config.notificationSmallIconId = R.mipmap.android_app_icon;//状态栏提醒的小图标的资源ID

        // 通知铃声的uri字符串
//        config.notificationSound = "android.resource://com.netease.nim.demo/raw/msg";

        // 呼吸灯配置
        config.ledARGB = Color.GREEN;//呼吸灯的颜色 The color of the led.
        config.ledOnMs = 1000;//呼吸灯亮时的持续时间（毫秒）
        config.ledOffMs = 1500;//呼吸灯熄灭时的持续时间（毫秒）

        options.statusBarNotificationConfig = config;
        UserPreferences.setStatusConfig(config);

        // 配置保存图片，文件，log等数据的目录
        String sdkPath = Environment.getExternalStorageDirectory() + "/" + getPackageName() + "/nim";
        options.sdkStorageRootPath = sdkPath;

        // 配置数据库加密秘钥
        options.databaseEncryptKey = "NETEASE";

        // 配置是否需要预下载附件缩略图
        options.preloadAttach = true;

//        // 配置附件缩略图的尺寸大小，
//        options.thumbnailSize = MsgViewHolderThumbBase.getImageMaxEdge();

//        // 用户信息提供者
//        options.userInfoProvider = infoProvider;

        // 定制通知栏提醒文案（可选，如果不定制将采用SDK默认文案）
        options.messageNotifierCustomization = new AlphaMessageNotifierCustomization(this);

        // 在线多端同步未读数
        options.sessionReadAck = true;
        NIMClient.init(this, loginInfo, options);
        if (SystemUtils.isMainProcess(this)) {
//             注册通知消息过滤器
            registerIMMessageFilter();
//             初始化消息提醒
            NIMClient.toggleNotification(false);
            NIMClient.toggleNotification(UserPreferences.getNotificationToggle());
            NIMClient.updateStatusBarNotificationConfig(UserPreferences.getStatusConfig());
            NIMClient.getService(MsgService.class).registerCustomAttachmentParser(new CustomAttachParser());
        }

        //云信数据库删除v2.2.0
        if (!SpUtils.getInstance().getBooleanData("nimDatabaseupdate_v2.0.0", false)) {
            NIMClient.getService(MsgService.class).clearMsgDatabase(true);
            SpUtils.getInstance().putData("nimDatabaseupdate_v2.0.0", true);
        }
    }

    /**
     * 通知消息过滤器（如果过滤则该消息不存储不上报）
     */
    private void registerIMMessageFilter() {
        NIMClient.getService(MsgService.class).registerIMMessageFilter(new IMMessageFilter() {
            @Override
            public boolean shouldIgnore(IMMessage message) {
                LogUtils.logObject("--------------->application IMMessageFilter:", message);
                if (UserPreferences.getMsgIgnore() && message.getAttachment() != null) {
                    if (message.getAttachment() instanceof UpdateTeamAttachment) {
                        UpdateTeamAttachment attachment = (UpdateTeamAttachment) message.getAttachment();
                        for (Map.Entry<TeamFieldEnum, Object> field : attachment.getUpdatedFields().entrySet()) {
                            if (field.getKey() == TeamFieldEnum.ICON) {
                                return true;
                            }
                        }
                    }
                }
                return false;
            }
        });
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

        FileDownloader.init(getApplicationContext(),
                new FileDownloadHelper.OkHttpClientCustomMaker() {
                    @Override
                    public OkHttpClient customMake() {
                        final OkHttpClient.Builder builder = new OkHttpClient.Builder();
                        // you can set the connection timeout.
                        builder.connectTimeout(35_000, TimeUnit.MILLISECONDS);
                        // you can set the HTTP proxy.
                        builder.proxy(Proxy.NO_PROXY);

                      /*  builder.addInterceptor(new Interceptor() {
                            @Override
                            public Response intercept(Chain chain) throws IOException {
                                Request request = chain.request();
                                Request requestBuilder = request.newBuilder()
                                        .addHeader("Cookie", "officeId==" + AlphaClient.getInstance().getOfficeId())
                                        .addHeader("token", AlphaClient.getInstance().getToken())
                                        .build();
                                return chain.proceed(requestBuilder);
                            }
                        });*/
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
                .trackingCrashLog(true)//是否收集crash !BuildConfig.IS_DEBUG
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
