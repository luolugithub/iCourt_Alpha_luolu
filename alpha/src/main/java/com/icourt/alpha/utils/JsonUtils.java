package com.icourt.alpha.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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


    public static final JsonObject object2JsonObject(Object object) throws JsonParseException {
        if (gson != null) {
            return (JsonObject) gson.toJsonTree(object);
        }
        return null;
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


    /**
     * JSONArray报文解析容错处理，获取数组中的JSONObject对象
     *
     * @param jsonArray ：需要解析的jsonArray对象、i：JSONObject在JSONArray中的索引
     * @return JSONObject：通过键名得到的JSONObject对象
     */
    public static JSONObject getJSONObject(JSONArray jsonArray, int i) {

        JSONObject value = new JSONObject();
        try {
            if (jsonArray != null) {
                value = jsonArray.getJSONObject(i);
            }
        } catch (JSONException e) {
        }
        return value;
    }

    /**
     * JSONArray报文解析容错处理，获取数组中的JSONObject对象
     *
     * @param jsonObject ：需要解析的jsonObject对象、name:健名
     * @return JSONObject：通过键名得到的JSONObject对象
     */
    public static JSONObject getJSONObject(JSONObject jsonObject, String name) {

        JSONObject value = new JSONObject();
        try {
            if (jsonObject != null) {
                value = jsonObject.getJSONObject(name);
            }
        } catch (JSONException e) {
        }
        return value;
    }

    /**
     * JSONObject报文解析容错处理，获取JSONObject中的JSONArray数组
     *
     * @param jsonObject ：需要解析的json对象、name：json中的键名
     * @return JSONArray：通过键名得到的JSONArray对象
     */
    public static JSONArray getJSONArray(JSONObject jsonObject, String name) {

        JSONArray value = new JSONArray();
        try {
            if (jsonObject != null) {
                value = jsonObject.getJSONArray(name);
            }
        } catch (JSONException e) {
        }
        return value;
    }

    /**
     * JSONObject报文解析容错处理，判断报文格式是否正确
     *
     * @param string ：需要转换为JSONObject的字符串
     * @return JSONObject：通过键名得到的JSONArray对象
     */
    public static JSONObject getJSONObject(String string) {

        JSONObject value = new JSONObject();
        if (string != null && string != "") {
            try {
                value = new JSONObject(string);
            } catch (JSONException e) {
            }
        }
        return value;
    }

    /**
     * JSONArray报文解析容错处理，判断报文格式是否正确
     *
     * @param string ：需要转换为JSONArray的字符串
     * @return JSONArray：通过键名得到的JSONArray对象
     */
    public static JSONArray getJSONArray(String string) {

        JSONArray value = new JSONArray();
        if (string != null && string != "") {
            try {
                value = new JSONArray(string);
            } catch (JSONException e) {
            }
        }
        return value;
    }

    public static String getString(JSONArray jsonArray, int i) {

        String value = "";
        if (jsonArray != null) {
            try {
                value = jsonArray.getString(i);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return isNull(value);
    }

    public static String isNull(String value) {
        if (value == null || value.length() <= 0 || "null".equals(value)) {
            return "";
        }
        return value;
    }

    /**
     * JSONObjec报文获取键值对中值时进行容错处理， 获取String
     *
     * @param jsonObject ：需要解析的json对象、name：json中的键名
     * @return isBoolean：通过键名得到的boolean值
     */
    public static Boolean getBoolean(JSONObject jsonObject, String name) {

        Boolean value = null;
        try {
            if (jsonObject != null) {
                value = jsonObject.getBoolean(name);
            }
        } catch (JSONException e) {
            value = null;
        }
        return value;
    }

    /**
     * JSONObject查看是否有这个key容错处理
     *
     * @param jsonObject
     * @param name
     * @return
     */
    public static boolean has(JSONObject jsonObject, String name) {

        return jsonObject.has(name);
    }

    /**
     * JSONObjec报文获取键值对中值时进行容错处理， 获取String
     *
     * @param jsonObject ：需要解析的json对象、name：json中的键名
     * @return int：通过键名得到的int值
     */
    public static int getInt(JSONObject jsonObject, String name) {

        int value = 0;
        try {
            if (jsonObject != null) {
                value = jsonObject.getInt(name);
            }
        } catch (JSONException e) {
            value = 0;
        }
        return value;
    }


    /**
     * JSONArray报文获取键值对中值时进行容错处理， 获取String
     *
     * @param jsonArray ：需要解析的json对象、i:数组中某个对象、name：json中的键名
     * @return String：通过键名得到的值
     */
    public static String getString(JSONArray jsonArray, int i, String name) {

        String value = "";
        JSONObject jsonObject = getJSONObject(jsonArray, i);
        if (jsonObject != null) {
            value = jsonObject.optString(name, "");
        }
        return isNull(value);
    }

    /**
     * JSONObjec报文获取键值对中值时进行容错处理， 获取String
     *
     * @param jsonObject ：需要解析的json对象、name：json中的键名
     * @return String：通过键名得到的值
     */
    public static String getString(JSONObject jsonObject, String name) {

        String value = "";
        try {
            if (jsonObject != null) {
                value = jsonObject.getString(name);
            }
        } catch (JSONException e) {
            value = "";
        }
        return isNull(value);
    }

    /**
     * JSONObjec报文获取键值对中值时进行容错处理， 获取String
     *
     * @param jsonObject ：需要解析的json对象、name：json中的键名
     * @return long：通过键名得到的long值
     */
    public static long getLong(JSONObject jsonObject, String name) {

        long value = 0;
        try {
            if (jsonObject != null) {
                value = jsonObject.getLong(name);
            }
        } catch (JSONException e) {
            value = 0;
        }
        return value;
    }


}

