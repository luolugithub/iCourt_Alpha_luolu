package com.icourt.alpha.fragment;

import com.icourt.alpha.base.BaseFragment;
import com.icourt.alpha.db.dbmodel.RepoEncryptionDbModel;
import com.icourt.alpha.db.dbservice.RepoEncryptionDbService;
import com.icourt.alpha.entity.bean.RepoEncryptionEntity;
import com.icourt.alpha.entity.bean.RepoEntity;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/9/14
 * version 2.1.0
 */
public abstract class RepoBaseFragment extends BaseFragment {


    /**
     * 资料库 加密状态保存
     * @param datas
     * @return
     */
    protected Observable<List<RepoEntity>> saveEncryptedData(List<RepoEntity> datas) {
        return Observable.just(datas)
                .filter(new Predicate<List<RepoEntity>>() {
                    @Override
                    public boolean test(@NonNull List<RepoEntity> repoEntities) throws Exception {
                        return repoEntities != null
                                && !repoEntities.isEmpty();
                    }
                })
                .map(new Function<List<RepoEntity>, List<RepoEntity>>() {
                    @Override
                    public List<RepoEntity> apply(@NonNull List<RepoEntity> repoEntities) throws Exception {
                        RepoEncryptionDbService encryptionDbService = null;
                        try {
                            encryptionDbService = new RepoEncryptionDbService(getLoginUserId());
                            for (RepoEntity entity : repoEntities) {
                                if (entity == null) continue;
                                RepoEncryptionDbModel query = encryptionDbService.query(entity.repo_id);
                                if (query != null) {
                                    RepoEncryptionEntity dbRepoEncryptionEntity = query.convert2Model();
                                    dbRepoEncryptionEntity.encrypted = entity.encrypted;
                                    dbRepoEncryptionEntity.decryptMillisecond = dbRepoEncryptionEntity.encrypted ? dbRepoEncryptionEntity.decryptMillisecond : 0;

                                    //更新解密时间
                                    entity.decryptMillisecond = dbRepoEncryptionEntity.decryptMillisecond;
                                    encryptionDbService.insertOrUpdate(dbRepoEncryptionEntity.convert2Model());
                                } else {
                                    RepoEncryptionEntity dbRepoEncryptionEntity = new RepoEncryptionEntity(entity.repo_id, entity.encrypted, 0);
                                    encryptionDbService.insertOrUpdate(dbRepoEncryptionEntity.convert2Model());
                                }
                            }
                        } catch (Throwable e) {
                            e.printStackTrace();
                            bugSync("更新资料库缓存失败",
                                    new StringBuilder("data:")
                                            .append(repoEntities)
                                            .toString());
                        } finally {
                            if (encryptionDbService != null) {
                                encryptionDbService.releaseService();
                            }
                        }
                        return repoEntities;
                    }
                });
    }
    /**
     * 更新本地记录状态
     *
     * @param repoId
     * @param encrypted
     * @param decryptMillisecond
     */
    protected void updateEncryptedRecord(String repoId, boolean encrypted, long decryptMillisecond) {
        RepoEncryptionDbService repoEncryptionDbService = null;
        try {
            repoEncryptionDbService = new RepoEncryptionDbService(getLoginUserId());
            repoEncryptionDbService.insertOrUpdate(new RepoEncryptionEntity(repoId, encrypted, decryptMillisecond).convert2Model());
        } catch (Exception e) {
            e.printStackTrace();
            bugSync("更新资料库缓存失败",
                    new StringBuilder("repoId:")
                            .append(repoId)
                            .append("encrypted:")
                            .append(encrypted).append("decryptMillisecond:")
                            .append(decryptMillisecond)
                            .toString());
        } finally {
            if (repoEncryptionDbService != null) {
                repoEncryptionDbService.releaseService();
            }
        }
    }

}
