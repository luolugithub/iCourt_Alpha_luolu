package com.icourt.alpha.db.dbmodel;

import com.icourt.alpha.db.convertor.IConvertModel;
import com.icourt.alpha.entity.bean.RepoEncryptionEntity;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;

/**
 * Description  资料库加密缓存 请勿修改
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/9/14
 * version 2.1.0
 */
@RealmClass
public class RepoEncryptionDbModel extends RealmObject implements IConvertModel<RepoEncryptionEntity> {
    @PrimaryKey
    private String repoId;//资料库id
    private boolean encrypted;//是否是加密的
    private long decryptMillisecond;//解密时间

    private int extInt1;
    private int extInt2;
    private int extInt3;
    private int extInt4;

    private int extString1;
    private int extString2;
    private int extString3;
    private int extString4;

    private boolean extBoolean1;
    private boolean extBoolean2;

    public RepoEncryptionDbModel(String repoId, boolean encrypted, long decryptMillisecond) {
        this.repoId = repoId;
        this.encrypted = encrypted;
        this.decryptMillisecond = decryptMillisecond;
    }

    public RepoEncryptionDbModel() {
    }

    @Override
    public RepoEncryptionEntity convert2Model() {
        return new RepoEncryptionEntity(repoId, encrypted, decryptMillisecond);
    }
}
