package com.icourt.alpha.http.httpmodel;

import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.icourt.alpha.utils.BooleanTypeAdapter;

/**
 * @author xuanyouwu
 * @email xuanyouwu@163.com
 * @time 2016-06-01 16:59
 * <p>
 * 不要轻易修改
 * 不要轻易修改
 */

public class ResEntity<T> {
    private static final String FIELD_SUCCEED = "succeed";
    private static final String FIELD_MESSAGE = "message";
    private static final String FIELD_RESULT = "result";

    @SerializedName(value = FIELD_MESSAGE, alternate = {"resultMess", "resultMsg"})
    public String message;

    @JsonAdapter(BooleanTypeAdapter.class)
    @SerializedName(value = FIELD_SUCCEED, alternate = {"resultCode", "isSuccess"})
    public boolean succeed;

    @SerializedName(value = FIELD_RESULT, alternate = {"data"})
    public T result;

    @Override
    public String toString() {
        return "ResEntity{" +
                "message='" + message + '\'' +
                ", succeed=" + succeed +
                ", result=" + result +
                '}';
    }
}
