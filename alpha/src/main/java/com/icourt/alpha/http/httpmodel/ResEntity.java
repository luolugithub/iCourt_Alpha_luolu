package com.icourt.alpha.http.httpmodel;

import com.google.gson.annotations.SerializedName;

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

    @SerializedName(FIELD_MESSAGE)
    public String message;

    @SerializedName(FIELD_SUCCEED)
    public boolean succeed;

    @SerializedName(FIELD_RESULT)
    public T result;
}
