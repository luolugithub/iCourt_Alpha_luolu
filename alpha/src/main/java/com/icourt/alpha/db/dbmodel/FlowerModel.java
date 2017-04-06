package com.icourt.alpha.db.dbmodel;

import io.realm.DynamicRealmObject;
import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/6
 * version 1.0.0
 */
public class FlowerModel extends RealmObject {

    @PrimaryKey
    public String pk;
    public String name;

    public FlowerModel(String pk, String name) {
        this.pk = pk;
        this.name = name;
    }

    public FlowerModel() {
    }
}
