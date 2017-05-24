package com.icourt.alpha.entity.bean;

import java.util.List;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/5/24
 * version 1.0.0
 */
public class SearchPolymerizationEntity {
    public int type;
    public String headerTitle;
    public String footerTitle;
    public List<? extends ISearchItemEntity> data;

    public SearchPolymerizationEntity(int type, String headerTitle, String footerTitle, List<? extends ISearchItemEntity> data) {
        this.type = type;
        this.headerTitle = headerTitle;
        this.footerTitle = footerTitle;
        this.data = data;
    }
}
