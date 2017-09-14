package com.icourt.alpha.entity.bean;

import com.icourt.alpha.db.convertor.IConvertModel;
import com.icourt.alpha.db.dbmodel.RepoEncryptionDbModel;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/9/14
 * version 2.1.0
 */
public class RepoEncryptionEntity implements IConvertModel<RepoEncryptionDbModel> {
    public String repoId;//资料库id
    public boolean encrypted;//是否是加密的
    public long decryptMillisecond;//解密时间

    public RepoEncryptionEntity(String repoId, boolean encrypted, long decryptMillisecond) {
        this.repoId = repoId;
        this.encrypted = encrypted;
        this.decryptMillisecond = decryptMillisecond;
    }

    public RepoEncryptionEntity() {
    }

    @Override
    public RepoEncryptionDbModel convert2Model() {
        return new RepoEncryptionDbModel(repoId, encrypted, decryptMillisecond);
    }
}
