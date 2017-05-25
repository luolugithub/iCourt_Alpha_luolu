package com.icourt.alpha.db.dbservice;

import android.support.annotation.CheckResult;

import com.icourt.alpha.db.BaseRealmService;
import com.icourt.alpha.db.convertor.IConvertModel;
import com.icourt.alpha.db.convertor.ListConvertor;
import com.icourt.alpha.db.dbdao.SearchEngineDao;
import com.icourt.alpha.db.dbmodel.SearchEngineModel;

import java.util.List;

import io.realm.RealmResults;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/5/25
 * version 1.0.0
 */
public class SearchEngineDbService extends BaseRealmService<SearchEngineModel, SearchEngineDao> {

    public SearchEngineDbService(String uid) {
        super(new SearchEngineDao(uid));
    }

    /**
     * 全部删除
     */
    public void deleteAll() {
        if (isServiceAvailable()) {
            dao.delete(SearchEngineModel.class);
        }
    }

    /**
     * 批量异步插入
     *
     * @param from
     */
    public void insertOrUpdateAsyn(List<IConvertModel<SearchEngineModel>> from) {
        if (isServiceAvailable()) {
            List<SearchEngineModel> searchEngineDbModels
                    = ListConvertor.convertList(from);
            if (searchEngineDbModels != null) {
                dao.insertOrUpdateAsyn(searchEngineDbModels);
            }
        }
    }

    /**
     * 批量插入
     *
     * @param from
     */
    public void insertOrUpdate(List<IConvertModel<SearchEngineModel>> from) {
        if (isServiceAvailable()) {
            List<SearchEngineModel> searchEngineDbModels
                    = ListConvertor.convertList(from);
            if (searchEngineDbModels != null) {
                dao.insertOrUpdate(searchEngineDbModels);
            }
        }
    }

    /**
     * 查询所有
     *
     * @return
     */
    @CheckResult
    public RealmResults<SearchEngineModel> queryAll() {
        if (isServiceAvailable()) {
            return dao.queryAll(SearchEngineModel.class);
        }
        return null;
    }

}
