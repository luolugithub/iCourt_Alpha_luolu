package com.icourt.alpha.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.icourt.alpha.BuildConfig;
import com.icourt.alpha.entity.bean.AlphaUserInfo;
import com.sensorsdata.analytics.android.sdk.SensorsDataAPI;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.icourt.alpha.constants.BugTagConfig.BUG_TAG_SENCE_INIT_EXCEPTION;

/**
 * @author youxuan  E-mail:xuanyouwu@163.com
 * @version 2.2.1
 * @Description 神策数据统计的工具类
 * @Company Beijing icourt
 * @date createTime：2017/11/2
 */
public class SenCeUtils {

    private SenCeUtils() {

    }

    /**
     * 初始化
     *
     * @param context
     */
    public static final void init(@NonNull Context context) {
        if (context != null) {
            try {
                // 数据接收的 URL
                final String SA_SERVER_URL = BuildConfig.API_SENSORS_DATA_SERVER;
                // 配置分发的 URL
                final String SA_CONFIGURE_URL = BuildConfig.API_SENSORS_DATA_CONFIGURE;
                // Debug 模式选项
                //   SensorsDataAPI.DebugMode.DEBUG_OFF - 关闭 Debug 模式
                //   SensorsDataAPI.DebugMode.DEBUG_ONLY - 打开 Debug 模式，校验数据，但不进行数据导入
                //   SensorsDataAPI.DebugMode.DEBUG_AND_TRACK - 打开 Debug 模式，校验数据，并将数据导入到 Sensors Analytics 中
                // 注意！请不要在正式发布的 App 中使用 Debug 模式！
                final SensorsDataAPI.DebugMode SA_DEBUG_MODE =
                        BuildConfig.IS_DEBUG ? SensorsDataAPI.DebugMode.DEBUG_AND_TRACK : SensorsDataAPI.DebugMode.DEBUG_OFF;

                // 初始化 SDK
                SensorsDataAPI.sharedInstance(
                        context,                               // 传入 Context
                        SA_SERVER_URL,                      // 数据接收的 URL
                        SA_CONFIGURE_URL,                   // 配置分发的 URL
                        SA_DEBUG_MODE);                     // Debug 模式选项
                // 打开自动采集, 并指定追踪哪些 AutoTrack 事件
                List<SensorsDataAPI.AutoTrackEventType> eventTypeList = new ArrayList<>();
                // $AppStart
                eventTypeList.add(SensorsDataAPI.AutoTrackEventType.APP_START);
                // $AppEnd
                eventTypeList.add(SensorsDataAPI.AutoTrackEventType.APP_END);
                // $AppViewScreen
                eventTypeList.add(SensorsDataAPI.AutoTrackEventType.APP_VIEW_SCREEN);
                // $AppClick
                eventTypeList.add(SensorsDataAPI.AutoTrackEventType.APP_CLICK);
                SensorsDataAPI.sharedInstance(context)
                        .enableAutoTrack(eventTypeList);
                //初始化 SDK 之后，开启自动采集 Fragment 页面浏览事件
                SensorsDataAPI.sharedInstance().trackFragmentAppViewScreen();
                //日志输出控制
                SensorsDataAPI.sharedInstance().enableLog(BuildConfig.IS_DEBUG);
            } catch (Exception e) {
                e.printStackTrace();
                BugUtils.bugSync(BUG_TAG_SENCE_INIT_EXCEPTION, e);
            }
        }
    }

    /**
     * 登陆
     *
     * @param context
     * @param userId
     */
    public static final void login(@NonNull Context context, @NonNull String userId) {
        if (context != null
                && !TextUtils.isEmpty(userId)) {
            try {
                SensorsDataAPI.sharedInstance(context)
                        .login(userId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 设置用户信息
     *
     * @param context
     * @param alphaUserInfo
     */
    public static final void setLoginInfo(@NonNull Context context, @NonNull AlphaUserInfo alphaUserInfo) {
        if (context != null
                && alphaUserInfo != null) {
            try {
                JSONObject properties = new JSONObject();
                properties.put("userId", alphaUserInfo.getUserId());
                properties.put("Phone", alphaUserInfo.getPhone());
                properties.put("$name", alphaUserInfo.getName());
                // 设定用户属性
                SensorsDataAPI.sharedInstance(context)
                        .profileSet(properties);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 登陆退出
     *
     * @param context
     */
    public static final void logout(@NonNull Context context) {
        if (context != null) {
            try {
                SensorsDataAPI.sharedInstance(context)
                        .logout();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 记录事件
     *
     * @param context
     * @param eventName
     */
    public static final void track(@NonNull Context context, @NonNull String eventName) {
        if (context != null) {
            try {
                SensorsDataAPI.sharedInstance(context)
                        .track(eventName);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
