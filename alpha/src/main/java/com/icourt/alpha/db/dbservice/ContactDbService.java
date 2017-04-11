package com.icourt.alpha.db.dbservice;

import android.support.annotation.CheckResult;

import com.icourt.alpha.db.BaseRealmService;
import com.icourt.alpha.db.convertor.IConvertModel;
import com.icourt.alpha.db.convertor.ListConvertor;
import com.icourt.alpha.db.dbdao.ContactDao;
import com.icourt.alpha.db.dbmodel.ContactDbModel;

import java.util.List;

import io.realm.RealmResults;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/11
 * version 1.0.0
 */
public class ContactDbService extends BaseRealmService<ContactDbModel, ContactDao> {
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
     * 批量异步插入
     *
     * @param from
     */
    public void insertAsyn(List<IConvertModel<ContactDbModel>> from) {
        if (isServiceAvailable()) {
            List<ContactDbModel> contactDbModels
                    = ListConvertor.convertList(from);
            if (contactDbModels != null) {
                dao.insertAsyn(contactDbModels);
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
    public RealmResults<ContactDbModel> queryAll() {
        if (isServiceAvailable()) {
            return dao.queryAll(ContactDbModel.class);
        }
        return null;
    }
}
