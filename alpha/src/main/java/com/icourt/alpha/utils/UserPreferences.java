package com.icourt.alpha.utils;

import com.netease.nimlib.sdk.StatusBarNotificationConfig;

import org.json.JSONObject;

/**
 * @author 创建人:lu.zhao
 * @data 创建时间:16/12/29
 */
public class UserPreferences {
    private final static String KEY_DOWNTIME_TOGGLE = "down_time_toggle";
    private final static String KEY_SB_NOTIFY_TOGGLE = "sb_notify_toggle";
    private final static String KEY_TEAM_ANNOUNCE_CLOSED = "team_announce_closed";
    private final static String KEY_STATUS_BAR_NOTIFICATION_CONFIG = "KEY_STATUS_BAR_NOTIFICATION_CONFIG";

    // 测试过滤通知
    private final static String KEY_MSG_IGNORE = "KEY_MSG_IGNORE";
    // 响铃配置
    private final static String KEY_RING_TOGGLE = "KEY_RING_TOGGLE";
    // 呼吸灯配置
    private final static String KEY_LED_TOGGLE = "KEY_LED_TOGGLE";
    // 通知栏标题配置
    private final static String KEY_NOTICE_CONTENT_TOGGLE = "KEY_NOTICE_CONTENT_TOGGLE";

    public static void setMsgIgnore(boolean enable) {
        saveBoolean(KEY_MSG_IGNORE, enable);
    }

    public static boolean getMsgIgnore() {
        return getBoolean(KEY_MSG_IGNORE, false);
    }

    public static void setNotificationToggle(boolean on) {
        saveBoolean(KEY_SB_NOTIFY_TOGGLE, on);
    }

    public static boolean getNotificationToggle() {
        return getBoolean(KEY_SB_NOTIFY_TOGGLE, true);
    }

    public static void setRingToggle(boolean on) {
        saveBoolean(KEY_RING_TOGGLE, on);
    }

    public static boolean getRingToggle() {
        return getBoolean(KEY_RING_TOGGLE, true);
    }

    public static void setLedToggle(boolean on) {
        saveBoolean(KEY_LED_TOGGLE, on);
    }

    public static boolean getLedToggle() {
        return getBoolean(KEY_LED_TOGGLE, true);
    }

    public static boolean getNoticeContentToggle() {
        return getBoolean(KEY_NOTICE_CONTENT_TOGGLE, false);
    }

    public static void setNoticeContentToggle(boolean on) {
        saveBoolean(KEY_NOTICE_CONTENT_TOGGLE, on);
    }

    public static void setDownTimeToggle(boolean on) {
        saveBoolean(KEY_DOWNTIME_TOGGLE, on);
    }

    public static boolean getDownTimeToggle() {
        return getBoolean(KEY_DOWNTIME_TOGGLE, false);
    }

    public static void setStatusConfig(StatusBarNotificationConfig config) {
        saveStatusBarNotificationConfig(KEY_STATUS_BAR_NOTIFICATION_CONFIG, config);
    }

    public static StatusBarNotificationConfig getStatusConfig() {
        return getConfig(KEY_STATUS_BAR_NOTIFICATION_CONFIG);
    }

    public static void setTeamAnnounceClosed(String teamId, boolean closed) {
        saveBoolean(KEY_TEAM_ANNOUNCE_CLOSED + teamId, closed);
    }

    public static boolean getTeamAnnounceClosed(String teamId) {
        return getBoolean(KEY_TEAM_ANNOUNCE_CLOSED + teamId, false);
    }

    private static StatusBarNotificationConfig getConfig(String key) {
        StatusBarNotificationConfig config = new StatusBarNotificationConfig();
        String jsonString = getString(key, "");
        try {
            JSONObject jsonObject = JsonUtils.getJSONObject(jsonString);
            if (jsonObject == null) {
                return null;
            }
            config.downTimeBegin = JsonUtils.getString(jsonObject, "downTimeBegin");
            config.downTimeEnd = JsonUtils.getString(jsonObject, "downTimeEnd");
            config.downTimeToggle = JsonUtils.getBoolean(jsonObject, "downTimeToggle");
            config.ring = JsonUtils.getBoolean(jsonObject, "ring");
            config.vibrate = JsonUtils.getBoolean(jsonObject, "vibrate");
            config.notificationSmallIconId = JsonUtils.getInt(jsonObject, "notificationSmallIconId");
            config.notificationSound = JsonUtils.getString(jsonObject, "notificationSound");
            config.hideContent = JsonUtils.getBoolean(jsonObject, "hideContent");
            config.ledARGB = JsonUtils.getInt(jsonObject, "ledargb");
            config.ledOnMs = JsonUtils.getInt(jsonObject, "ledonms");
            config.ledOffMs = JsonUtils.getInt(jsonObject, "ledoffms");
            config.titleOnlyShowAppName = JsonUtils.getBoolean(jsonObject, "titleOnlyShowAppName");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return config;
    }

    private static void saveStatusBarNotificationConfig(String key, StatusBarNotificationConfig config) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("downTimeBegin", config.downTimeBegin);
            jsonObject.put("downTimeEnd", config.downTimeEnd);
            jsonObject.put("downTimeToggle", config.downTimeToggle);
            jsonObject.put("ring", config.ring);
            jsonObject.put("vibrate", config.vibrate);
            jsonObject.put("notificationSmallIconId", config.notificationSmallIconId);
            jsonObject.put("notificationSound", config.notificationSound);
            jsonObject.put("hideContent", config.hideContent);
            jsonObject.put("ledargb", config.ledARGB);
            jsonObject.put("ledonms", config.ledOnMs);
            jsonObject.put("ledoffms", config.ledOffMs);
            jsonObject.put("titleOnlyShowAppName", config.titleOnlyShowAppName);
        } catch (Exception e) {
            e.printStackTrace();
        }

        putString(key, jsonObject.toString());
    }

    private static String getString(String key, String value) {
        return getUserSetSpUtils().getStringData(key, value);
    }

    private static void putString(String key, String value) {
        getUserSetSpUtils().putData(key, value);
    }

    private static boolean getBoolean(String key, boolean value) {
        return getUserSetSpUtils().getBooleanData(key, value);
    }

    private static void saveBoolean(String key, boolean value) {
        getUserSetSpUtils().putData(key, value);
    }

    static SpUtils userSetSpUtils;

    static SpUtils getUserSetSpUtils() {
        if (userSetSpUtils == null) {
            synchronized (UserPreferences.class) {
                userSetSpUtils = new SpUtils("user_set");
            }
        }
        return userSetSpUtils;
    }
}
