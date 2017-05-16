package com.icourt.alpha.entity.bean;

import java.util.List;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/5/15
 * version 1.0.0
 */
public class ItemPageEntity<T> {
    public int pageIndex;
    public int pageSize;
    public int totalCount;
    public List<T> items;
}
