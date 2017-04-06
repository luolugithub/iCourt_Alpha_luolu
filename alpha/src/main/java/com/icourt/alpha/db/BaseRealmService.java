package com.icourt.alpha.db;

import android.support.annotation.CallSuper;

import io.realm.RealmModel;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/6
 * version 1.0.0
 */
public abstract class BaseRealmService<T extends RealmModel, D extends BaseDao<T>> {
    protected D dao;

    public BaseRealmService(D d) {
        dao = d;
    }

    public boolean isServiceAvailable() {
        return dao.isRealmAvailable();
    }


    public void releaseService() {
        dao.releaseRealm();
    }
}
