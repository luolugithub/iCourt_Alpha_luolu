package com.icourt.alpha.utils;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * ClassName JsonUtils
 * Description  json处理工具类，暂时核心封装Gson解析方式
 * Company icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date 创建时间：2015/6/17 9:43
 * version
 */
public class JsonUtils {

    private JsonUtils() {
    }

    private static Gson gson = null;

    static {
        if (gson == null) {
            // gson = new Gson();
            gson = new GsonBuilder()
                    .setLenient()// json宽松
                    .enableComplexMapKeySerialization()//支持Map的key为复杂对象的形式
                    .setPrettyPrinting()// 调教格式
                    .disableHtmlEscaping() //默认是GSON把HTML 转义的
                    .create();
        }
    }

    public static Gson getGson() {
        return gson;
    }

    /**
     * 转成json
     *
     * @param object
     * @return
     */
    public static String Gson2String(Object object) throws JsonParseException {
        String gsonString = null;
        if (gson != null) {
            gsonString = gson.toJson(object);
        }
        return gsonString;
    }


    public static <T> JsonElement List2JsonArray(List<T> list) throws JsonParseException {
        return gson.toJsonTree(list);
    }

    /**
     * 转成bean
     *
     * @param gsonString
     * @param cls
     * @return
     */
    public static <T> T Gson2Bean(String gsonString, Class<T> cls) throws JsonParseException {
        T t = null;
        if (gson != null) {
            t = gson.fromJson(gsonString, cls);
        }
        return t;
    }

    /**
     * @param gsonString
     * @param cls
     * @return
     */
    public static <T> List<T> Gson2List(String gsonString, Class<T> cls) throws JsonParseException {
       /* List<T> list = null;
        if (gson != null) {
            Type type = new TypeToken<List<T>>() {
            }.getType();
            list = gson.fromJson(gsonString, type);
        }
        return list;*/

        List<T> lst = new ArrayList<T>();
        JsonArray array = new JsonParser().parse(gsonString).getAsJsonArray();
        for (final JsonElement elem : array) {
            lst.add(gson.fromJson(elem, cls));
        }
        return lst;
    }

    public static <T> T Gson2Type(String json, Type typeOfT) throws JsonParseException {
        return gson.fromJson(json, typeOfT);
    }

    /**
     * 转成list中有map的
     *
     * @param gsonString
     * @return
     */
    public static <T> List<Map<String, T>> Gson2ListMaps(String gsonString) throws JsonParseException {
        List<Map<String, T>> list = null;
        if (gson != null) {
            list = gson.fromJson(gsonString,
                    new TypeToken<List<Map<String, T>>>() {
                    }.getType());
        }
        return list;
    }

    /**
     * 转成map的
     *
     * @param gsonString
     * @return
     */
    public static <T> Map<String, T> Gson2Maps(String gsonString) throws JsonParseException {
        Map<String, T> map = null;
        if (gson != null) {
            map = gson.fromJson(gsonString, new TypeToken<Map<String, T>>() {
            }.getType());
        }
        return map;
    }

}

