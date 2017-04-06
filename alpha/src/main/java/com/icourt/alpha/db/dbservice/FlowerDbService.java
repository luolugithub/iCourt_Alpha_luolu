package com.icourt.alpha.db.dbservice;

import com.icourt.alpha.db.BaseRealmService;
import com.icourt.alpha.db.dbdao.FlowerDao;
import com.icourt.alpha.db.dbmodel.FlowerModel;

import io.realm.Case;
import io.realm.RealmResults;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/6
 * version 1.0.0
 */
public class FlowerDbService
        extends BaseRealmService<FlowerModel, FlowerDao> {
    public FlowerDbService() {
        super(new FlowerDao());
    }

    public RealmResults<FlowerModel> queryAll() {
        return dao.queryAll(FlowerModel.class);
    }

    public FlowerModel insertOrUpdate(FlowerModel f) {
        return dao.insertOrUpdate(f);
    }

    public void delete(FlowerModel f) {
        dao.delete(f);
    }

    public void deleteAll() {
        dao.delete(FlowerModel.class);
    }

    public RealmResults<FlowerModel> query(String pk) {
        return dao.query(FlowerModel.class, "pk", pk);
    }

    public RealmResults<FlowerModel> contains(String name) {
        return dao.contains(FlowerModel.class, "name", name, Case.INSENSITIVE);
    }

    public FlowerModel queryFirst(String pk) {
        return dao.queryFirst(FlowerModel.class, "pk", pk);
    }
}
