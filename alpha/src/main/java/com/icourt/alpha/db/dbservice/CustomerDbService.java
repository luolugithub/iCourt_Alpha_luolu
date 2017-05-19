package com.icourt.alpha.db.dbservice;

import android.support.annotation.CheckResult;

import com.icourt.alpha.db.BaseRealmService;
import com.icourt.alpha.db.convertor.IConvertModel;
import com.icourt.alpha.db.convertor.ListConvertor;
import com.icourt.alpha.db.dbdao.CustomerDao;
import com.icourt.alpha.db.dbmodel.CustomerDbModel;

import java.util.List;

import io.realm.RealmResults;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/11
 * version 1.0.0
 */
public class CustomerDbService extends BaseRealmService<CustomerDbModel, CustomerDao> {
    public CustomerDbService(String uid) {
        super(new CustomerDao(uid));
    }

    /**
     * 批量异步插入
     *
     * @param objects
     */
    public void insertAsyn(final Iterable<CustomerDbModel> objects) {
        if (isServiceAvailable()) {
            dao.insertAsyn(objects);
        }
    }


    /**
     * 全部删除
     */
    public void deleteAll() {
        if (isServiceAvailable()) {
            dao.delete(CustomerDbModel.class);
        }
    }

    /**
     * 批量异步插入
     *
     * @param from
     */
    public void insertOrUpdateAsyn(List<IConvertModel<CustomerDbModel>> from) {
        if (isServiceAvailable()) {
            List<CustomerDbModel> contactDbModels
                    = ListConvertor.convertList(from);
            if (contactDbModels != null) {
                dao.insertOrUpdateAsyn(contactDbModels);
            }
        }
    }

    /**
     * 查询所有
     *
     * @return
     */
    @CheckResult
    public RealmResults<CustomerDbModel> contains(String fieldName, String value) {
        if (isServiceAvailable()) {
            return dao.contains(CustomerDbModel.class, fieldName, value);
        }
        return null;
    }

    /**
     * 查询得到第一条对应实体
     *
     * @param fieldName
     * @param value
     * @return
     */
    public CustomerDbModel queryFirst(String fieldName, String value) {
        if (isServiceAvailable()) {
            return dao.queryFirst(CustomerDbModel.class, fieldName, value);
        }
        return null;
    }


    /**
     * 查询所有
     *
     * @return
     */
    @CheckResult
    public RealmResults<CustomerDbModel> queryAll() {
        if (isServiceAvailable()) {
            return dao.queryAll(CustomerDbModel.class);
        }
        return null;
    }

    /**
     * 添加或者更新一条对应实体
     *
     * @param customerDbModel
     * @return
     */
    public CustomerDbModel insertOrUpdate(CustomerDbModel customerDbModel) {
        if (isServiceAvailable()) {
            return dao.insertOrUpdate(customerDbModel);
        }
        return null;
    }
}
