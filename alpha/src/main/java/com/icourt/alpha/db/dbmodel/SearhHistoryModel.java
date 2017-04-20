package com.icourt.alpha.db.dbmodel;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/20
 * version 1.0.0
 */
public class SearhHistoryModel extends RealmObject {
    public String keyWord;
    public RealmList<SearchEngineModel> searchEngines;

    @Override
    public String toString() {
        return "SearhHistoryModel{" +
                "keyWord='" + keyWord + '\'' +
                ", searchEngines=" + searchEngines +
                '}';
    }
}
