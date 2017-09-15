package com.icourt.alpha.db.dbdao;

import com.icourt.alpha.constants.DbConfig;
import com.icourt.alpha.db.BaseRealmObjectDao;
import com.icourt.alpha.db.dbmodel.RepoEncryptionDbModel;

import io.realm.DynamicRealm;
import io.realm.RealmMigration;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/9/14
 * version 2.1.0
 */
public class RepoEncryptionDao extends BaseRealmObjectDao<RepoEncryptionDbModel> {

    public RepoEncryptionDao(String uid) {
        super(String.format(DbConfig.DB_REALM_REPO_ENCRYPTION, uid), 1, new RealmMigration() {

            @Override
            public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {

            }

            @Override
            public int hashCode() {
                return DbConfig.DB_REALM_REPO_ENCRYPTION.hashCode();
            }

            @Override
            public boolean equals(Object o) {
                return (o instanceof RealmMigration);
            }
        });
    }
}
