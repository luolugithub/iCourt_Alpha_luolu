package com.icourt.alpha.db.dbservice;

import android.support.annotation.CheckResult;
import android.support.annotation.Nullable;

import com.icourt.alpha.db.BaseRealmService;
import com.icourt.alpha.db.dbdao.RepoEncryptionDao;
import com.icourt.alpha.db.dbmodel.RepoEncryptionDbModel;

/**
 * Description  资料库加密解密查询
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/9/14
 * version 2.1.0
 */
public class RepoEncryptionDbService extends BaseRealmService<RepoEncryptionDao> {

    public RepoEncryptionDbService(String uid) {
        super(new RepoEncryptionDao(uid));
    }

    /**
     * 插入或者更新
     *
     * @param repoEncryptionDbModel
     */
    public void insertOrUpdate(final RepoEncryptionDbModel repoEncryptionDbModel) {
        if (isServiceAvailable()) {
            dao.insertOrUpdate(repoEncryptionDbModel);
        }
    }

    /**
     * 查询
     *
     * @param repoId
     * @return
     */
    @Nullable
    @CheckResult
    public RepoEncryptionDbModel query(String repoId) {
        if (isServiceAvailable()) {
            return dao.queryFirst(RepoEncryptionDbModel.class, "repoId", repoId);
        }
        return null;
    }
}
