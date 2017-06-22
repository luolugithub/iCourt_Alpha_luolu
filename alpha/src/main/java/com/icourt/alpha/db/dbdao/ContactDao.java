package com.icourt.alpha.db.dbdao;

import com.icourt.alpha.constants.DbConfig;
import com.icourt.alpha.db.BaseRealmObjectDao;
import com.icourt.alpha.db.dbmodel.ContactDbModel;

import io.realm.Case;
import io.realm.DynamicRealm;
import io.realm.RealmMigration;
import io.realm.RealmResults;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/11
 * version 1.0.0
 */
public class ContactDao extends BaseRealmObjectDao<ContactDbModel> {
    public ContactDao(String uid) {
        super(String.format(DbConfig.DB_REALM_CONTACT, uid), 2, new RealmMigration() {

            @Override
            public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {

            }

            @Override
            public int hashCode() {
                return DbConfig.DB_REALM_CONTACT.hashCode();
            }

            @Override
            public boolean equals(Object o) {
                return (o instanceof RealmMigration);
            }
        });
    }

    /**
     * 多个查询
     *
     * @param c
     * @param fieldName1
     * @param value1
     * @param fieldName2
     * @param value2
     * @param casing
     * @return
     */
    public RealmResults<ContactDbModel> contains(Class<ContactDbModel> c, String fieldName1, String value1, String fieldName2, String value2, Case casing) {
        if (!isRealmAvailable()) return null;
        return realm.where(c).contains(fieldName1, value1, casing)
                .or()
                .contains(fieldName2, value2, casing).findAll();
    }
}
