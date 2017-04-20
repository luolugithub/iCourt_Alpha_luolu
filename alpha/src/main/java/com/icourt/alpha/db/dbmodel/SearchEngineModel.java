package com.icourt.alpha.db.dbmodel;

import com.icourt.alpha.db.convertor.IConvertModel;
import com.icourt.alpha.entity.bean.SearchEngineEntity;

import io.realm.RealmObject;
import io.realm.annotations.RealmClass;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/20
 * version 1.0.0
 */
@RealmClass
public class SearchEngineModel extends RealmObject implements IConvertModel<SearchEngineEntity> {
    public int id;
    public String name;
    public String site;

    public SearchEngineModel() {
    }

    public SearchEngineModel(int id, String name, String site) {
        this.id = id;
        this.name = name;
        this.site = site;
    }

    @Override
    public String toString() {
        return "SearchEngineModel{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", site='" + site + '\'' +
                '}';
    }

    @Override
    public SearchEngineEntity convert2Model() {
        return new SearchEngineEntity(id, name, site);
    }
}
