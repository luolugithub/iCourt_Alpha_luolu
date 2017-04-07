package com.icourt.alpha.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.icourt.alpha.base.BaseApplication;
import com.icourt.alpha.constants.Const;


/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：16/7/3
 * version
 */
public class SpUtils {
    private static final String DEFAULT_FILE = Const.SHARE_PREFERENCES_FILE_NAME;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private static SpUtils instance;


    private Context getContext() {
        return BaseApplication.getApplication();
    }


    public SpUtils(String fileName) {
        sp = getContext().getSharedPreferences(TextUtils.isEmpty(fileName) ? DEFAULT_FILE : fileName, Context.MODE_PRIVATE);
        editor = sp.edit();
    }

    private SharedPreferences.Editor getEditor() {
        if (editor == null) {
            editor = sp.edit();
        }
        return editor;
    }


    public static SpUtils getInstance() {
        if (instance == null) {
            instance = new SpUtils(DEFAULT_FILE);
        }
        return instance;
    }

    public SpUtils remove(String key) {
        if (sp.contains(key)) {
            getEditor().remove(key);
        }
        return this;
    }

    public SpUtils clear() {
        getEditor().clear().commit();
        return this;
    }


    public SpUtils putData(String key, String data) {
        getEditor().putString(key, data).commit();
        return this;
    }

    public SpUtils putData(String key, boolean data) {
        getEditor().putBoolean(key, data).commit();
        return this;
    }

    public SpUtils putData(String key, int data) {
        getEditor().putInt(key, data).commit();
        return this;
    }

    public SpUtils putData(String key, long data) {
        getEditor().putLong(key, data).commit();
        return this;
    }

    public SpUtils putData(String key, float data) {
        getEditor().putFloat(key, data).commit();
        return this;
    }

    public String getStringData(String key, String defaultString) {
        if (sp.contains(key)) {
            return sp.getString(key, defaultString);
        }
        return defaultString;
    }

    public boolean getBooleanData(String key, boolean defaultBool) {
        if (sp.contains(key)) {
            return sp.getBoolean(key, defaultBool);
        }
        return defaultBool;
    }

    public int getIntData(String key, int defaultInt) {
        if (sp.contains(key)) {
            return sp.getInt(key, defaultInt);
        }
        return defaultInt;
    }

    public long getLongData(String key, long defaultLong) {
        if (sp.contains(key)) {
            return sp.getLong(key, defaultLong);
        }
        return defaultLong;
    }

    public float getFloatData(String key, float defaultFloat) {
        if (sp.contains(key)) {
            return sp.getFloat(key, defaultFloat);
        }
        return defaultFloat;
    }


}
