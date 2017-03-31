package com.icourt.alpha.http.httpmodel;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Description
 * 统一分页模板  不要轻易改动
 * Company Beijing guokeyuzhou
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：16/7/15
 * version
 */
public class ResPageEntity<T> {
    private static final String FIELD_RESULTCODE = "resultCode";
    private static final String FIELD_RESULTMESS = "resultMess";

    private static final String FIELD_INDEX = "curPage";
    private static final String FIELD_PAGESIZE = "pageSize";
    private static final String FIELD_TOTAL = "total";
    private static final String FIELD_TOTALPAGE = "totalPage";
    private static final String FIELD_PAGEDATA = "data";

    @SerializedName(FIELD_RESULTCODE)
    public int resultCode;

    @SerializedName(FIELD_RESULTMESS)
    public String resultMess;

    @SerializedName(FIELD_INDEX)
    public int pageIndex;

    @SerializedName(FIELD_PAGESIZE)
    public int pageSize;

    @SerializedName(FIELD_TOTAL)
    public int total;

    @SerializedName(FIELD_TOTALPAGE)
    public int totalPage;

    @SerializedName(FIELD_PAGEDATA)
    public List<T> pageData;
}
