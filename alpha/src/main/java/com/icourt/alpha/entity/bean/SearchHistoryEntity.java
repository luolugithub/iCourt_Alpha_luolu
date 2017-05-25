package com.icourt.alpha.entity.bean;

import java.util.List;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/5/25
 * version 1.0.0
 */
public class SearchHistoryEntity {
    public String keyWord;
    public List<SearchEngineEntity> searchEngines;

    @Override
    public String toString() {
        return "SearhHistoryEntity{" +
                "keyWord='" + keyWord + '\'' +
                ", searchEngines=" + searchEngines +
                '}';
    }
}
