package com.icourt.alpha.db;

import java.util.List;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmModel;
import io.realm.RealmQuery;
import io.realm.RealmResults;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/6
 * version 1.0.0
 */
public abstract class BaseDao<T extends RealmModel> implements IRealmDao<T> {


    public Realm realm;

    public BaseDao(Realm realm) {
        this.realm = realm;
    }

    @Override
    public boolean isRealmAvailable() {
        return realm != null && !realm.isClosed();
    }

    @Override
    public void releaseRealm() {
        if (isRealmAvailable()) {
            realm.close();
        }
    }

    @Override
    public T insertFromJson(Class<T> c, String json) {
        return realm.createObjectFromJson(c, json);
    }

    @Override
    public final T insert(T t) {
        T retlT = null;
        try {
            realm.beginTransaction();
            retlT = realm.copyToRealm(t);
            realm.commitTransaction();
        } catch (Exception e) {
            realm.cancelTransaction();
        }
        return retlT;
    }

    @Override
    public T insertOrUpdate(T t) {
        T retlT = null;
        try {
            realm.beginTransaction();
            retlT = realm.copyToRealmOrUpdate(t);
            realm.commitTransaction();
        } catch (Exception e) {
            realm.cancelTransaction();
        }
        return retlT;
    }


    @Override
    public List<T> insert(Iterable<T> objects) {
        List<T> ts = null;
        try {
            realm.beginTransaction();
            ts = realm.copyToRealm(objects);
            realm.commitTransaction();
        } catch (Exception e) {
            realm.cancelTransaction();
        }
        return ts;
    }

    @Override
    public List<T> insertOrUpdate(Iterable<T> objects) {
        List<T> ts = null;
        try {
            realm.beginTransaction();
            ts = realm.copyToRealmOrUpdate(objects);
            realm.commitTransaction();
        } catch (Exception e) {
            realm.cancelTransaction();
        }
        return ts;
    }

    @Override
    public void delete(Class<T> c, String key, String value) {
        final RealmResults<T> all = realm.where(c).equalTo(key, value).findAll();
        try {
            realm.beginTransaction();
            all.deleteAllFromRealm();
            realm.commitTransaction();
        } catch (Exception e) {
            realm.cancelTransaction();
        }
    }


    @Override
    public void delete(Class<T> c) {
        try {
            realm.beginTransaction();
            realm.delete(c);
            realm.commitTransaction();
        } catch (Exception e) {
            realm.cancelTransaction();
        }
    }

    public abstract void delete(T t);

    @Override
    public RealmResults<T> queryAll(Class<T> c) {
        return realm.where(c).findAll();
    }

    @Override
    public RealmResults<T> query(Class<T> c, String fieldName, String value) {
        return realm.where(c).equalTo(fieldName, value).findAll();
    }

    @Override
    public RealmResults<T> contains(Class<T> c, String fieldName, String value) {
        return realm.where(c).contains(fieldName, value).findAll();
    }

    @Override
    public RealmResults<T> contains(Class<T> c, String fieldName, String value, Case casing) {
        return realm.where(c).contains(fieldName, value, casing).findAll();
    }

    @Override
    public T queryFirst(Class<T> c, String fieldName, String value) {
        return realm.where(c).equalTo(fieldName, value).findFirst();
    }

    @Override
    public RealmResults<T> queryAll(RealmQuery<T> query) {
        return query.findAll();
    }

    @Override
    public T queryFirst(RealmQuery<T> query) {
        return query.findFirst();
    }
}
