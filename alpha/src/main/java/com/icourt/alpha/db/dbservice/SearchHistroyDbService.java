package com.icourt.alpha.db.dbservice;

import android.support.annotation.CheckResult;

import com.icourt.alpha.db.BaseRealmService;
import com.icourt.alpha.db.dbdao.SearchHistoryDao;
import com.icourt.alpha.db.dbmodel.SearhHistoryModel;

import io.realm.RealmResults;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/11
 * version 1.0.0
 */
public class SearchHistroyDbService extends BaseRealmService<SearhHistoryModel, SearchHistoryDao> {
    public SearchHistroyDbService(String uid) {
        super(new SearchHistoryDao(uid));
    }


    /**
     * 插入
     *
     * @param searhHistoryModel
     * @return
     */
    public SearhHistoryModel insert(SearhHistoryModel searhHistoryModel) {
        if (isServiceAvailable()) {
            return dao.insert(searhHistoryModel);
        }
        return null;
    }

    /**
     * 查询所有
     *
     * @return
     */
    @CheckResult
    public RealmResults<SearhHistoryModel> queryAll() {
        if (isServiceAvailable()) {
            return dao.queryAll(SearhHistoryModel.class);
        }
        return null;
    }

    /**
     * 删除所有
     */
    public void deleteAll() {
        if (isServiceAvailable()) {
            dao.delete(SearhHistoryModel.class);
        }
    }
}
