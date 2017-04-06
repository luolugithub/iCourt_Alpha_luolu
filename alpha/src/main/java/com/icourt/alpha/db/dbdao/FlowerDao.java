package com.icourt.alpha.db.dbdao;

import com.icourt.alpha.db.BaseRealmObjectDao;
import com.icourt.alpha.db.dbmodel.FlowerModel;

import java.security.SecureRandom;

import io.realm.DynamicRealm;
import io.realm.Realm;
import io.realm.RealmMigration;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/6
 * version 1.0.0
 */
public class FlowerDao extends BaseRealmObjectDao<FlowerModel> {
    public FlowerDao() {
        super("flowers.realm", 1, new RealmMigration() {

            @Override
            public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {

            }
        });
    }

}
