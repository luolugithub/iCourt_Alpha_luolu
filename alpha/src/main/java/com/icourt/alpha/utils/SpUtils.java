package com.icourt.alpha.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.CheckResult;
import android.text.TextUtils;

import com.google.gson.JsonParseException;
import com.icourt.alpha.base.BaseApplication;
import com.icourt.alpha.constants.Const;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.StreamCorruptedException;

import Decoder.BASE64Decoder;
import Decoder.BASE64Encoder;


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

    public SpUtils putData(String key, Object o) {
        if (o != null) {
            try {
                getEditor().putString(key, JsonUtils.Gson2String(o)).commit();
            } catch (JsonParseException e) {
                e.printStackTrace();
            }
        }
        return this;
    }

    public SpUtils putData(String key, Serializable object) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(object);
            String productBase64 = new String(new BASE64Encoder().encode(baos.toByteArray()));
            getEditor().putString(key, productBase64).commit();
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    @CheckResult
    public <T> T getObjectData(String key, Class<T> c) {
        try {
            String stringData = getStringData(key, null);
            if (!TextUtils.isEmpty(stringData)) {
                return JsonUtils.Gson2Bean(key, c);
            }
        } catch (JsonParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    @CheckResult
    public Serializable getSerializableData(String key) {
        ByteArrayInputStream bais = null;
        ObjectInputStream ois = null;
        Serializable object = null;
        try {
            String personBase64 = getStringData(key, "");
            byte[] base64Bytes = new BASE64Decoder().decodeBuffer(personBase64);
            bais = new ByteArrayInputStream(base64Bytes);
            ois = new ObjectInputStream(bais);
            object = (Serializable) ois.readObject();
            ois.close();
            bais.close();
            return object;
        } catch (EOFException e) {
            e.printStackTrace();
        } catch (StreamCorruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }


}
