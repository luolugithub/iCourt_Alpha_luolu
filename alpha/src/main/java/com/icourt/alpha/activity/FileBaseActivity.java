package com.icourt.alpha.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.text.TextUtils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.icourt.alpha.base.BaseActivity;
import com.icourt.alpha.constants.DownloadConfig;
import com.icourt.alpha.constants.SFileConfig;
import com.icourt.alpha.entity.bean.FolderDocumentEntity;
import com.icourt.alpha.entity.bean.ISeaFile;
import com.icourt.alpha.entity.bean.SFileUploadParamEntity;
import com.icourt.alpha.utils.FileUtils;
import com.icourt.alpha.utils.IndexUtils;
import com.icourt.alpha.utils.SFileTokenUtils;
import com.icourt.alpha.widget.comparators.FileSortComparator;
import com.icourt.api.RequestUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import okhttp3.RequestBody;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/9/8
 * version 2.1.0
 */
public class FileBaseActivity extends BaseActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SFileTokenUtils.syncServerSFileToken();
    }


    /**
     * 批量文件上传
     */
    @UiThread
    protected void seaFileUploadFiles(@android.support.annotation.NonNull String seaFileRepoId,
                                      @android.support.annotation.NonNull String seaFileDirPath,
                                      @android.support.annotation.NonNull List<String> filePaths,
                                      @android.support.annotation.NonNull Observer<JsonElement> observer) {
        Observable.just(new SFileUploadParamEntity(seaFileRepoId, seaFileDirPath, filePaths))
                .filter(new Predicate<SFileUploadParamEntity>() {
                    @Override
                    public boolean test(@NonNull SFileUploadParamEntity sFileUploadParamEntity) throws Exception {
                        return sFileUploadParamEntity.filePaths != null
                                && !sFileUploadParamEntity.filePaths.isEmpty();
                    }
                })
                .flatMap(new Function<SFileUploadParamEntity, ObservableSource<SFileUploadParamEntity>>() {
                    @Override
                    public ObservableSource<SFileUploadParamEntity> apply(@NonNull final SFileUploadParamEntity sFileUploadParamEntity) throws Exception {
                        return getSFileApi().sfileUploadUrlQueryObservable(
                                sFileUploadParamEntity.seaFileRepoId,
                                "upload",
                                sFileUploadParamEntity.seaFileDirPath)
                                .map(new Function<String, SFileUploadParamEntity>() {
                                    @Override
                                    public SFileUploadParamEntity apply(@NonNull String s) throws Exception {
                                        sFileUploadParamEntity.uploadServerUrl = s;
                                        return sFileUploadParamEntity;
                                    }
                                });
                    }
                })
                .flatMap(new Function<SFileUploadParamEntity, ObservableSource<JsonElement>>() {
                    @Override
                    public ObservableSource<JsonElement> apply(@NonNull SFileUploadParamEntity sFileUploadParamEntity) throws Exception {
                        List<Observable<JsonElement>> observables = new ArrayList<Observable<JsonElement>>();
                        for (int i = 0; i < sFileUploadParamEntity.filePaths.size(); i++) {
                            String filePath = sFileUploadParamEntity.filePaths.get(i);
                            if (TextUtils.isEmpty(filePath)) {
                                continue;
                            }
                            File file = new File(filePath);
                            if (!file.exists()) {
                                continue;
                            }
                            Map<String, RequestBody> params = new HashMap<>();
                            params.put(RequestUtils.createStreamKey(file), RequestUtils.createStreamBody(file));
                            params.put("parent_dir", RequestUtils.createTextBody(sFileUploadParamEntity.seaFileDirPath));
                            observables.add(getSFileApi().sfileUploadFileObservable(sFileUploadParamEntity.uploadServerUrl, params));
                        }
                        return Observable.concat(observables);
                    }
                })
                .compose(this.<JsonElement>bindToLifecycle())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    /**
     * 排序
     *
     * @param fileSortType
     * @param datas
     * @return
     */
    protected Observable<List<FolderDocumentEntity>> seaFileSort(@FileSortComparator.FileSortType final int fileSortType,
                                                                 @android.support.annotation.NonNull List<FolderDocumentEntity> datas) {
        return Observable
                .just(datas)
                .map(new Function<List<FolderDocumentEntity>, List<FolderDocumentEntity>>() {
                    @Override
                    public List<FolderDocumentEntity> apply(@NonNull List<FolderDocumentEntity> folderDocumentEntities) throws Exception {
                        try {
                            IndexUtils.setSuspensions(getContext(), folderDocumentEntities);
                            Collections.sort(folderDocumentEntities, new FileSortComparator(fileSortType));
                        } catch (Throwable e) {
                            e.printStackTrace();
                            bugSync("排序异常", e);
                        }
                        return folderDocumentEntities;
                    }
                });
    }


    /**
     * 包装 repoid 和dirpath
     * 避免 parent_dir与repoId为空
     *
     * @param seaFileRepoId
     * @param seaFileDirPath
     * @param items
     * @return
     */
    protected List<FolderDocumentEntity> wrapData(
            @android.support.annotation.NonNull final String seaFileRepoId,
            @android.support.annotation.NonNull final String seaFileDirPath,
            @android.support.annotation.NonNull final List<FolderDocumentEntity> items) {
        if (items != null) {
            for (int i = 0; i < items.size(); i++) {
                FolderDocumentEntity folderDocumentEntity = items.get(i);
                if (folderDocumentEntity == null) continue;
                if (TextUtils.isEmpty(folderDocumentEntity.parent_dir)) {
                    folderDocumentEntity.parent_dir = seaFileDirPath;
                }
                if (TextUtils.isEmpty(folderDocumentEntity.repoId)) {
                    folderDocumentEntity.repoId = seaFileRepoId;
                }
            }
        }
        return items;
    }

    /**
     * 填充权限
     *
     * @param permission
     * @param items
     * @return
     */
    protected List<FolderDocumentEntity> wrapData(@SFileConfig.FILE_PERMISSION String permission,
                                                  @android.support.annotation.NonNull final List<FolderDocumentEntity> items) {
        if (items != null) {
            for (int i = 0; i < items.size(); i++) {
                FolderDocumentEntity folderDocumentEntity = items.get(i);
                if (folderDocumentEntity == null) continue;
                folderDocumentEntity.permission = permission;
            }
        }
        return items;
    }

    /**
     * 文件批量删除
     *
     * @param items
     */
    protected void seaFileDelete(
            @android.support.annotation.NonNull final String seaFileRepoId,
            @android.support.annotation.NonNull final String seaFileDirPath,
            @android.support.annotation.NonNull final ArrayList<FolderDocumentEntity> items,
            Observer<JsonObject> observer) {
        Observable.just(items)
                .filter(new Predicate<ArrayList<FolderDocumentEntity>>() {
                    @Override
                    public boolean test(@NonNull ArrayList<FolderDocumentEntity> folderDocumentEntities) throws Exception {
                        return !folderDocumentEntities.isEmpty();
                    }
                })
                .flatMap(new Function<ArrayList<FolderDocumentEntity>, ObservableSource<JsonObject>>() {
                    @Override
                    public ObservableSource<JsonObject> apply(@io.reactivex.annotations.NonNull ArrayList<FolderDocumentEntity> folderDocumentEntities) throws Exception {
                        List<Observable<JsonObject>> observables = new ArrayList<Observable<JsonObject>>();
                        for (FolderDocumentEntity item : folderDocumentEntities) {
                            Observable<JsonObject> delCall = null;
                            if (item.isDir()) {
                                delCall = getSFileApi()
                                        .folderDeleteObservable(
                                                seaFileRepoId,
                                                String.format("%s%s", seaFileDirPath, item.name));
                            } else {

                                //1.网络删除
                                delCall = getSFileApi()
                                        .fileDeleteObservable(
                                                seaFileRepoId,
                                                String.format("%s%s", seaFileDirPath, item.name));


                                //2.本地缓存文件删除
                                deletCachedSeaFile(item);
                            }
                            observables.add(delCall);
                        }
                        return Observable.concat(observables);
                    }
                })
                .compose(this.<JsonObject>bindToLifecycle())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    /**
     * 删除缓存的seafile
     *
     * @param item
     */
    private void deletCachedSeaFile(ISeaFile item) {
        FileUtils.deleteFile(DownloadConfig.getSeaFileDownloadPath(getLoginUserId(), item));
    }
}
