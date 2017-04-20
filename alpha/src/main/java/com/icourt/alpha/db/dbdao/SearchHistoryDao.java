package com.icourt.alpha.db.dbdao;

import com.icourt.alpha.constants.DbConfig;
import com.icourt.alpha.db.BaseRealmObjectDao;
import com.icourt.alpha.db.dbmodel.SearhHistoryModel;

import io.realm.DynamicRealm;
import io.realm.RealmMigration;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/11
 * version 1.0.0
 */
public class SearchHistoryDao extends BaseRealmObjectDao<SearhHistoryModel> {
    public SearchHistoryDao(String uid) {
        super(String.format(DbConfig.DB_REALM_SEARCH_HISTORY, uid), 1, new RealmMigration() {

            @Override
            public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {

            }

            @Override
            public int hashCode() {
                return DbConfig.DB_REALM_SEARCH_HISTORY.hashCode();
            }

            @Override
            public boolean equals(Object o) {
                return (o instanceof RealmMigration);
            }
        });
    }

}
