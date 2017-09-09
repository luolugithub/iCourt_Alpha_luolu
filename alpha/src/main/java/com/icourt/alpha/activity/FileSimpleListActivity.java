package com.icourt.alpha.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.andview.refreshview.XRefreshView;
import com.google.gson.JsonElement;
import com.icourt.alpha.BuildConfig;
import com.icourt.alpha.R;
import com.icourt.alpha.adapter.FolderAdapter;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.adapter.baseadapter.HeaderFooterAdapter;
import com.icourt.alpha.adapter.baseadapter.adapterObserver.DataChangeAdapterObserver;
import com.icourt.alpha.constants.SFileConfig;
import com.icourt.alpha.entity.bean.FolderDocumentEntity;
import com.icourt.alpha.http.IDefNotify;
import com.icourt.alpha.http.callback.SFileCallBack;
import com.icourt.alpha.http.observer.BaseObserver;
import com.icourt.alpha.utils.GlideUtils;
import com.icourt.alpha.utils.IMUtils;
import com.icourt.alpha.utils.SFileTokenUtils;
import com.icourt.alpha.utils.StringUtils;
import com.icourt.alpha.utils.SystemUtils;
import com.icourt.alpha.utils.UriUtils;
import com.icourt.alpha.view.xrefreshlayout.RefreshLayout;
import com.icourt.alpha.widget.comparators.FileSortComparator;
import com.icourt.alpha.widget.dialog.BottomActionDialog;
import com.icourt.alpha.widget.dialog.SortTypeSelectDialog;
import com.icourt.alpha.widget.filter.SFileNameFilter;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.model.PhotoInfo;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Response;

import static com.icourt.alpha.constants.SFileConfig.PERMISSION_R;
import static com.icourt.alpha.constants.SFileConfig.PERMISSION_RW;
import static com.icourt.alpha.widget.comparators.FileSortComparator.FILE_SORT_TYPE_DEFAULT;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/9/8
 * version 2.1.0
 */
public class FileSimpleListActivity extends FolderBaseActivity
        implements BaseRecyclerAdapter.OnItemClickListener {
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
    int fileSortType = FILE_SORT_TYPE_DEFAULT;


    final ArrayList<String> bigImageUrls = new ArrayList<>();
    final ArrayList<String> smallImageUrls = new ArrayList<>();
    @BindView(R.id.titleBack)
    ImageView titleBack;
    @BindView(R.id.titleContent)
    TextView titleContent;
    @BindView(R.id.titleAction)
    ImageView titleAction;
    @BindView(R.id.titleView)
    AppBarLayout titleView;
    @BindView(R.id.titleAction2)
    ImageView titleAction2;


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

    /**
     * @param context
     * @param repoPermission repo权限 "rw" "r"
     * @param seaFileRepoId  repoid
     * @param repoTitle      repo 标题
     * @param seaFileDirPath repo目录路径
     */
    public static void launch(@NonNull Context context,
                              @SFileConfig.FILE_PERMISSION String repoPermission,
                              String seaFileRepoId,
                              String repoTitle,
                              String seaFileDirPath) {
        if (context == null) return;
        Intent intent = new Intent(context, FileSimpleListActivity.class);
        intent.putExtra(KEY_SEA_FILE_REPO_TITLE, repoTitle);
        intent.putExtra(KEY_SEA_FILE_REPO_PERMISSION, repoPermission);
        intent.putExtra(KEY_SEA_FILE_REPO_ID, seaFileRepoId);
        intent.putExtra(KEY_SEA_FILE_DIR_PATH, seaFileDirPath);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_simple_list);
        ButterKnife.bind(this);
        initView();
    }


    @Override
    public void initView() {
        super.initView();

        setTitle(getRepoTitle());
        titleAction.setImageResource(R.mipmap.header_icon_add);
        if (TextUtils.equals(getIntent().getStringExtra(KEY_SEA_FILE_REPO_PERMISSION), PERMISSION_R)) {
            titleAction.setVisibility(View.GONE);
        }
        titleAction2.setImageDrawable(GlideUtils.getTintedDrawable(getDrawable(getContext(), R.mipmap.sort),
                ColorStateList.valueOf(getContextColor(R.color.alpha_font_color_orange))));

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        headerFooterAdapter = new HeaderFooterAdapter<>(folderAdapter = new FolderAdapter(getSeaFileRepoId(), getSeaFileDirPath(), false));

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

    @OnClick({R.id.titleAction,
            R.id.titleAction2})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.titleAction:
                showActionDialog();
                break;
            case R.id.titleAction2:
                showSortDialog();
                break;
            default:
                super.onClick(v);
                break;
        }
    }

    @Override
    protected void getData(boolean isRefresh) {
        super.getData(isRefresh);
        getSFileApi().projectQueryFileBoxByDir2(
                getSeaFileRepoId(),
                getSeaFileDirPath())
                .enqueue(new SFileCallBack<List<FolderDocumentEntity>>() {
                    @Override
                    public void onSuccess(Call<List<FolderDocumentEntity>> call, Response<List<FolderDocumentEntity>> response) {
                        sortFile(response.body());
                        stopRefresh();
                    }

                    @Override
                    public void onFailure(Call<List<FolderDocumentEntity>> call, Throwable t) {
                        super.onFailure(call, t);
                        stopRefresh();
                        dismissLoadingDialog();
                    }
                });
    }

    private void stopRefresh() {
        if (refreshLayout != null) {
            refreshLayout.stopRefresh();
            refreshLayout.stopLoadMore();
        }
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
                                    SystemUtils.chooseFile(getActivity(), REQUEST_CODE_CHOOSE_FILE);
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
                            showLoadingDialog(R.string.str_sorting);
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
                .map(new Function<List<FolderDocumentEntity>, List<FolderDocumentEntity>>() {
                    @Override
                    public List<FolderDocumentEntity> apply(@NonNull List<FolderDocumentEntity> folderDocumentEntities) throws Exception {
                        bigImageUrls.clear();
                        smallImageUrls.clear();
                        for (int i = 0; i < folderDocumentEntities.size(); i++) {
                            FolderDocumentEntity folderDocumentEntity = folderDocumentEntities.get(i);
                            if (folderDocumentEntity == null) continue;
                            if (IMUtils.isPIC(folderDocumentEntity.name)) {
                                bigImageUrls.add(getSFileImageUrl(folderDocumentEntity.name, Integer.MAX_VALUE));
                                smallImageUrls.add(getSFileImageUrl(folderDocumentEntity.name, 800));
                            }
                        }
                        return folderDocumentEntities;
                    }
                })
                .compose(this.<List<FolderDocumentEntity>>bindToLifecycle())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<List<FolderDocumentEntity>>() {
                    @Override
                    public void onNext(@NonNull List<FolderDocumentEntity> folderDocumentEntities) {
                        folderAdapter.bindData(true, folderDocumentEntities);
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
                String.format("%s%s", getSeaFileDirPath(), name));
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
                        public void onSubscribe(@NonNull Disposable disposable) {
                            super.onSubscribe(disposable);
                            showLoadingDialog(R.string.str_uploading);
                        }

                        @Override
                        public void onNext(@NonNull JsonElement jsonElement) {

                        }

                        @Override
                        public void onError(@NonNull Throwable throwable) {
                            super.onError(throwable);
                            dismissLoadingDialog();
                        }

                        @Override
                        public void onComplete() {
                            super.onComplete();
                            getData(true);
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
                    PERMISSION_RW,
                    getSeaFileRepoId(),
                    item.name,
                    String.format("%s%s/", getSeaFileDirPath(), item.name));
        } else {
            //图片 直接预览
            if (IMUtils.isPIC(item.name)) {
                int indexOf = bigImageUrls.indexOf(getSFileImageUrl(item.name, Integer.MAX_VALUE));
                ImageViewerActivity.launch(
                        getContext(),
                        smallImageUrls,
                        bigImageUrls,
                        indexOf);
            } else {
                FileDownloadActivity.launch(
                        getContext(),
                        getSeaFileRepoId(),
                        item.name,
                        item.size,
                        String.format("%s%s", getSeaFileDirPath(), item.name),
                        null);
            }
        }
    }
}
