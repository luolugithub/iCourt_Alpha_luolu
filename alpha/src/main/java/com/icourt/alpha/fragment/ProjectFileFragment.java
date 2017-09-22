package com.icourt.alpha.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.andview.refreshview.XRefreshView;
import com.google.gson.JsonElement;
import com.icourt.alpha.BuildConfig;
import com.icourt.alpha.R;
import com.icourt.alpha.activity.FileDownloadActivity;
import com.icourt.alpha.activity.FileSimpleListActivity;
import com.icourt.alpha.activity.FolderCreateActivity;
import com.icourt.alpha.activity.ImageViewerActivity;
import com.icourt.alpha.adapter.FolderAdapter;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.adapter.baseadapter.HeaderFooterAdapter;
import com.icourt.alpha.adapter.baseadapter.adapterObserver.DataChangeAdapterObserver;
import com.icourt.alpha.constants.SFileConfig;
import com.icourt.alpha.entity.bean.FolderDocumentEntity;
import com.icourt.alpha.entity.bean.RepoIdResEntity;
import com.icourt.alpha.http.IDefNotify;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.http.observer.BaseObserver;
import com.icourt.alpha.interfaces.OnParentTitleBarClickListener;
import com.icourt.alpha.utils.IMUtils;
import com.icourt.alpha.utils.SFileTokenUtils;
import com.icourt.alpha.utils.StringUtils;
import com.icourt.alpha.utils.SystemUtils;
import com.icourt.alpha.utils.UriUtils;
import com.icourt.alpha.utils.UrlUtils;
import com.icourt.alpha.view.xrefreshlayout.RefreshLayout;
import com.icourt.alpha.widget.comparators.FileSortComparator;
import com.icourt.alpha.widget.dialog.BottomActionDialog;
import com.icourt.alpha.widget.dialog.SortTypeSelectDialog;
import com.icourt.alpha.widget.filter.SFileNameFilter;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.model.PhotoInfo;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

import static com.icourt.alpha.widget.comparators.FileSortComparator.FILE_SORT_TYPE_DEFAULT;

/**
 * Description  项目下的文档列表
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/9/8
 * version 2.1.0
 */
public class ProjectFileFragment extends SeaFileBaseFragment
        implements OnParentTitleBarClickListener, BaseRecyclerAdapter.OnItemClickListener {
    private static final String KEY_PROJECT_ID = "key_project_id";
    private static final int REQUEST_CODE_CHOOSE_FILE = 1002;
    private static final int MAX_LENGTH_FILE_NAME = 100;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.refreshLayout)
    RefreshLayout refreshLayout;
    Unbinder unbinder;
    TextView footerView;

    HeaderFooterAdapter<FolderAdapter> headerFooterAdapter;
    FolderAdapter folderAdapter;
    String projectId;
    String repoId;
    String path;
    int fileSortType = FILE_SORT_TYPE_DEFAULT;

    private GalleryFinal.OnHanlderResultCallback mOnHanlderResultCallback = new GalleryFinal.OnHanlderResultCallback() {
        @Override
        public void onHanlderSuccess(int reqeustCode, List<PhotoInfo> resultList) {
            if (resultList != null && !resultList.isEmpty()) {
                List<String> filePaths = new ArrayList<>();
                for (PhotoInfo photoInfo : resultList) {
                    if (photoInfo != null && !TextUtils.isEmpty(photoInfo.getPhotoPath())) {
                        filePaths.add(photoInfo.getPhotoPath());
                    }
                }
                uploadFiles(filePaths);
            }
        }

        @Override
        public void onHanlderFailure(int requestCode, String errorMsg) {

        }
    };

    public static ProjectFileFragment newInstance(@NonNull String projectId) {
        ProjectFileFragment projectFileBoxFragment = new ProjectFileFragment();
        Bundle bundle = new Bundle();
        bundle.putString(KEY_PROJECT_ID, projectId);
        projectFileBoxFragment.setArguments(bundle);
        return projectFileBoxFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(R.layout.layout_refresh_recyclerview, inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void initView() {
        SFileTokenUtils.syncServerSFileToken();

        projectId = getArguments().getString(KEY_PROJECT_ID, "");

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        headerFooterAdapter = new HeaderFooterAdapter<>(folderAdapter = new FolderAdapter());

        footerView = (TextView) HeaderFooterAdapter.inflaterView(getContext(), R.layout.footer_folder_document_num, recyclerView);
        headerFooterAdapter.addFooter(footerView);
        footerView.setText("");

        recyclerView.setAdapter(headerFooterAdapter);
        folderAdapter.setOnItemClickListener(this);
        folderAdapter.registerAdapterDataObserver(new DataChangeAdapterObserver() {
            @Override
            protected void updateUI() {
                if (footerView != null) {
                    int dirNum = 0, fileNum = 0;
                    List<FolderDocumentEntity> items = folderAdapter.getData();
                    for (int j = 0; j < items.size(); j++) {
                        FolderDocumentEntity folderDocumentEntity = items.get(j);
                        if (folderDocumentEntity != null) {
                            if (folderDocumentEntity.isDir()) {
                                dirNum += 1;
                            } else {
                                fileNum += 1;
                            }
                        }
                    }
                    if (dirNum == 0 && fileNum == 0) {
                        footerView.setText(R.string.sfile_folder_empty);
                    } else {
                        footerView.setText(getString(R.string.sfile_folder_statistics, String.valueOf(dirNum), String.valueOf(fileNum)));
                    }
                }
            }
        });
        refreshLayout.setXRefreshViewListener(new XRefreshView.SimpleXRefreshListener() {
            @Override
            public void onRefresh(boolean isPullDown) {
                super.onRefresh(isPullDown);
                getData(true);
            }
        });
        refreshLayout.startRefresh();
    }

    @Override
    public void onResume() {
        super.onResume();
        getData(true);
    }

    @Override
    protected void getData(boolean isRefresh) {
        super.getData(isRefresh);
        Observable.just(projectId)//1:项目id-->获取权限
                .flatMap(new Function<String, ObservableSource<List<String>>>() {
                    @Override
                    public ObservableSource<List<String>> apply(@NonNull String s) throws Exception {
                        return getApi().permissionQueryObservable(getLoginUserId(), "MAT", s)
                                .map(new Function<ResEntity<List<String>>, List<String>>() {
                                    @Override
                                    public List<String> apply(@NonNull ResEntity<List<String>> listResEntity) throws Exception {
                                        return listResEntity != null ? listResEntity.result : new ArrayList<String>();
                                    }
                                });
                    }
                })
                .filter(new Predicate<List<String>>() {  //2--->是否有可读或者可读写权限
                    @Override
                    public boolean test(@NonNull List<String> strings) throws Exception {
                        return (strings.contains("MAT:matter.document:readwrite")
                                || strings.contains("MAT:matter.document:read"));
                    }
                })
                .flatMap(new Function<List<String>, ObservableSource<String>>() {//3--->项目id转换repoid
                    @Override
                    public ObservableSource<String> apply(@NonNull List<String> strings) throws Exception {
                        return getApi().projectQueryDocumentIdObservable(projectId)
                                .map(new Function<RepoIdResEntity, String>() {
                                    @Override
                                    public String apply(@NonNull RepoIdResEntity repoIdResEntity) throws Exception {
                                        return repoIdResEntity != null ? repoIdResEntity.seaFileRepoId : "";
                                    }
                                });
                    }
                })
                .filter(new Predicate<String>() {//4----->校验repoid不能为空
                    @Override
                    public boolean test(@NonNull String s) throws Exception {
                        boolean canNext = !TextUtils.isEmpty(s);
                        if (!canNext) {
                            bugSync("项目repo 获取null", "projectid:" + projectId);
                        }
                        return canNext;
                    }
                })
                .flatMap(new Function<String, ObservableSource<List<FolderDocumentEntity>>>() {//5--->获取该repo下面的文件
                    @Override
                    public ObservableSource<List<FolderDocumentEntity>> apply(@NonNull String s) throws Exception {
                        repoId = s;
                        return getSFileApi().projectQueryFileBoxListObservable(s);
                    }
                })
                .compose(this.<List<FolderDocumentEntity>>bindToLifecycle())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<List<FolderDocumentEntity>>() {
                    @Override
                    public void onNext(@NonNull List<FolderDocumentEntity> fileBoxBeen) {
                        sortFile(wrapData(getSeaFileRepoId(), getSeaFileDirPath(), fileBoxBeen));
                    }

                    @Override
                    public void onError(@NonNull Throwable throwable) {
                        super.onError(throwable);
                        stopRefresh();
                    }

                    @Override
                    public void onComplete() {
                        super.onComplete();
                        stopRefresh();
                    }
                });
    }

    private void stopRefresh() {
        if (refreshLayout != null) {
            refreshLayout.stopRefresh();
            refreshLayout.stopLoadMore();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public boolean onParentTitleBack(Object parent, View v, int type, Bundle bundle) {
        return false;
    }

    @Override
    public void onParentTitleClick(Object parent, View v, int type, Bundle bundle) {

    }

    @Override
    public void onParentTitleActionClick(Object parent, View v, int type, Bundle bundle) {
        showActionDialog();
    }

    @Override
    public void onParentTitleActionClick2(Object parent, View v, int type, Bundle bundle) {
        showSortDialog();
    }

    private void showActionDialog() {
        new BottomActionDialog(getContext(),
                null,
                Arrays.asList(getResources().getStringArray(R.array.sfile_folder_action_menus_array)),
                new BottomActionDialog.OnActionItemClickListener() {
                    @Override
                    public void onItemClick(BottomActionDialog dialog, BottomActionDialog.ActionItemAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
                        dialog.dismiss();
                        switch (position) {
                            case 0:
                                FolderCreateActivity.launch(
                                        getContext(),
                                        getSeaFileRepoId(),
                                        getSeaFileDirPath());
                                break;
                            case 1:
                                if (checkAcessFilePermission()) {
                                    SystemUtils.chooseFile(ProjectFileFragment.this, REQUEST_CODE_CHOOSE_FILE);
                                } else {
                                    requestAcessFilePermission();
                                }
                                break;
                            case 2:
                                checkAndSelectMutiPhotos(mOnHanlderResultCallback);
                                break;
                            case 3:
                                checkAndSelectFromCamera(mOnHanlderResultCallback);
                                break;
                        }
                    }
                }).show();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_CHOOSE_FILE:
                if (resultCode == Activity.RESULT_OK) {
                    if (data != null) {
                        String path = UriUtils.getPath(getContext(), data.getData());
                        uploadFile(path);
                    }
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    /**
     * 上传文件
     *
     * @param filePath 文件路径
     */
    private void uploadFile(String filePath) {
        if (TextUtils.isEmpty(filePath)) return;
        File file = new File(filePath);
        if (!file.exists()) {
            showTopSnackBar(R.string.sfile_not_exist);
            return;
        }
        uploadFiles(Arrays.asList(filePath));
    }


    /**
     * 展示排序对话框
     */
    private void showSortDialog() {
        new SortTypeSelectDialog(getContext(),
                fileSortType,
                new SortTypeSelectDialog.OnSortTypeChangeListener() {
                    @Override
                    public void onSortTypeSelected(@FileSortComparator.FileSortType int sortType) {
                        if (fileSortType != sortType) {
                            fileSortType = sortType;
                            showLoadingDialog(R.string.str_executing);
                            sortFile(new ArrayList<FolderDocumentEntity>(folderAdapter.getData()));
                        }
                    }
                }).show();
    }

    /**
     * 排序
     */
    private void sortFile(List<FolderDocumentEntity> datas) {
        seaFileSort(fileSortType, datas)
                .delay(500, TimeUnit.MILLISECONDS)
                .compose(this.<List<FolderDocumentEntity>>bindToLifecycle())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<List<FolderDocumentEntity>>() {
                    @Override
                    public void onNext(@NonNull List<FolderDocumentEntity> folderDocumentEntities) {
                        folderAdapter.bindData(true, folderDocumentEntities);
                    }

                    @Override
                    public void onError(@NonNull Throwable throwable) {
                        super.onError(throwable);
                        dismissLoadingDialog();
                    }

                    @Override
                    public void onComplete() {
                        super.onComplete();
                        dismissLoadingDialog();
                    }
                });
    }

    protected String getSFileImageUrl(String name, int size) {
        return String.format("%silaw/api/v2/documents/thumbnailImage?repoId=%s&seafileToken=%s&size=%s&p=%s",
                BuildConfig.API_URL,
                getSeaFileRepoId(),
                SFileTokenUtils.getSFileToken(),
                size,
                UrlUtils.encodeUrl(String.format("%s%s", getSeaFileDirPath(), name)));
    }

    public String getSeaFileRepoId() {
        return repoId;
    }

    public String getSeaFileDirPath() {
        return "/";
    }

    /**
     * 上传文件
     *
     * @param filePaths 文件路径
     */
    private void uploadFiles(final List<String> filePaths) {
        if (filePaths != null
                && !filePaths.isEmpty()) {
            final ArrayList<String> filePathsArray = new ArrayList<>(filePaths);

            //1.检验文件名称合法性
            for (int i = filePathsArray.size() - 1; i >= 0; i--) {
                String path = filePathsArray.get(i);

                File file = null;
                try {
                    //可能出现路径异常
                    file = new File(path);
                    if (!file.exists()) {
                        filePathsArray.remove(path);
                        continue;
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                if (file != null) {
                    //2.先检验文件合法
                    boolean isLegal = SFileNameFilter.checkFileNameIsLegal(
                            file.getName(),
                            new IDefNotify() {
                                @Override
                                public void defNotify(String noticeStr) {
                                    showTopSnackBar(noticeStr);
                                }
                            });
                    if (!isLegal) {
                        filePathsArray.remove(path);
                    } else {
                        //3.再校验文件名称长度
                        if (StringUtils.isOverLength(file.getName(), MAX_LENGTH_FILE_NAME)) {
                            showTopSnackBar(getString(R.string.sfile_length_limit_format, String.valueOf(MAX_LENGTH_FILE_NAME)));
                            filePathsArray.remove(path);
                        }
                    }
                }
            }

            //2.避免为空
            if (filePathsArray.isEmpty()) {
                return;
            }
            seaFileUploadFiles(getSeaFileRepoId(),
                    getSeaFileDirPath(),
                    filePathsArray,
                    new BaseObserver<JsonElement>() {
                        @Override
                        public void onNext(@NonNull JsonElement jsonElement) {

                        }

                        @Override
                        public void onSubscribe(@NonNull Disposable disposable) {
                            super.onSubscribe(disposable);
                            showLoadingDialog(R.string.str_uploading);
                        }

                        @Override
                        public void onError(@NonNull Throwable throwable) {
                            super.onError(throwable);
                            dismissLoadingDialog();
                        }

                        @Override
                        public void onComplete() {
                            super.onComplete();
                            dismissLoadingDialog();
                        }
                    });
        }
    }


    @Override
    public void onItemClick(BaseRecyclerAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
        final FolderDocumentEntity item = folderAdapter.getItem(position);
        if (item == null) return;
        if (item.isDir()) {
            FileSimpleListActivity.launch(
                    getContext(),
                    SFileConfig.PERMISSION_RW,
                    getSeaFileRepoId(),
                    item.name,
                    String.format("%s%s/", getSeaFileDirPath(), item.name));
        } else {
            //图片 直接预览
            if (IMUtils.isPIC(item.name)) {
                ArrayList<FolderDocumentEntity> imageDatas = new ArrayList<>();
                for (int i = 0; i < folderAdapter.getItemCount(); i++) {
                    FolderDocumentEntity folderDocumentEntity = folderAdapter.getItem(i);
                    if (folderDocumentEntity == null) continue;
                    if (IMUtils.isPIC(folderDocumentEntity.name)) {
                        imageDatas.add(folderDocumentEntity);
                    }
                }
                int indexOf = imageDatas.indexOf(item);
                ImageViewerActivity.launch(
                        getContext(),
                        SFileConfig.FILE_FROM_PROJECT,
                        imageDatas,
                        indexOf);
            } else {
                FileDownloadActivity.launch(
                        getContext(),
                        item,
                        SFileConfig.FILE_FROM_PROJECT);
            }
        }
    }
}
