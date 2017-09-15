package com.icourt.alpha.entity.bean;

import java.util.List;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/8/14
 * version 2.1.0
 */
public class SeaFileTrashPageEntity<T> {
    public String scan_stat;
    public List<T> data;
    public boolean more;//是否有更多
}
