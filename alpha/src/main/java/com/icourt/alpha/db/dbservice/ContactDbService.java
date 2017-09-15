package com.icourt.alpha.db.dbservice;

import android.support.annotation.CheckResult;

import com.icourt.alpha.db.BaseRealmService;
import com.icourt.alpha.db.convertor.IConvertModel;
import com.icourt.alpha.db.convertor.ListConvertor;
import com.icourt.alpha.db.dbdao.ContactDao;
import com.icourt.alpha.db.dbmodel.ContactDbModel;

import java.util.List;

import io.realm.Case;
import io.realm.RealmResults;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/11
 * version 1.0.0
 */
public class ContactDbService extends BaseRealmService<ContactDao> {
    public ContactDbService(String uid) {
        super(new ContactDao(uid));
    }

    /**
     * 批量异步插入
     *
     * @param objects
     */
    public void insertAsyn(final Iterable<ContactDbModel> objects) {
        if (isServiceAvailable()) {
            dao.insertAsyn(objects);
        }
    }


    /**
     * 全部删除
     */
    public void deleteAll() {
        if (isServiceAvailable()) {
            dao.delete(ContactDbModel.class);
        }
    }

    /**
     * 批量异步插入
     *
     * @param from
     */
    public void insertOrUpdateAsyn(List<IConvertModel<ContactDbModel>> from) {
        if (isServiceAvailable()) {
            List<ContactDbModel> contactDbModels
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
    public RealmResults<ContactDbModel> contains(String fieldName, String value) {
        if (isServiceAvailable()) {
            return dao.contains(ContactDbModel.class, fieldName, value);
        }
        return null;
    }


    /**
     * 查询所有
     *
     * @return
     */
    @CheckResult
    public RealmResults<ContactDbModel> contains(String fieldName, String value, Case casing) {
        if (isServiceAvailable()) {
            return dao.contains(ContactDbModel.class, fieldName, value, casing);
        }
        return null;
    }

    /**
     * 多个查询
     *
     * @param fieldName1
     * @param value1
     * @param fieldName2
     * @param value2
     * @param casing
     * @return
     */
    public RealmResults<ContactDbModel> contains(String fieldName1, String value1, String fieldName2, String value2, Case casing) {
        if (isServiceAvailable()) {
            return dao.contains(ContactDbModel.class, fieldName1, value1, fieldName2, value2, casing);
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
    public ContactDbModel queryFirst(String fieldName, String value) {
        if (isServiceAvailable()) {
            return dao.queryFirst(ContactDbModel.class, fieldName, value);
        }
        return null;
    }


    /**
     * 查询所有
     *
     * @return
     */
    @CheckResult
    public RealmResults<ContactDbModel> queryAll() {
        if (isServiceAvailable()) {
            return dao.queryAll(ContactDbModel.class);
        }
        return null;
    }
}
