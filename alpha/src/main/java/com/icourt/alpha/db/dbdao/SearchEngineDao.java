package com.icourt.alpha.db.dbdao;

import com.icourt.alpha.constants.DbConfig;
import com.icourt.alpha.db.BaseRealmObjectDao;
import com.icourt.alpha.db.dbmodel.SearchEngineModel;

import io.realm.DynamicRealm;
import io.realm.RealmMigration;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/5/25
 * version 1.0.0
 */
public class SearchEngineDao extends BaseRealmObjectDao<SearchEngineModel> {

    public SearchEngineDao(String uid) {
        super(String.format(DbConfig.DB_REALM_SEARCH_ENGINE, uid), 1, new RealmMigration() {

            @Override
            public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {

            }

            @Override
            public int hashCode() {
                return DbConfig.DB_REALM_SEARCH_ENGINE.hashCode();
            }

            @Override
            public boolean equals(Object o) {
                return (o instanceof RealmMigration);
            }
        });
    }
}
