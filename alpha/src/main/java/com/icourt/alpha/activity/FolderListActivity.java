package com.icourt.alpha.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.util.ArraySet;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.andview.refreshview.XRefreshView;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.icourt.alpha.BuildConfig;
import com.icourt.alpha.R;
import com.icourt.alpha.adapter.FolderDocumentAdapter;
import com.icourt.alpha.adapter.FolderDocumentWrapAdapter;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.adapter.baseadapter.HeaderFooterAdapter;
import com.icourt.alpha.adapter.baseadapter.adapterObserver.DataChangeAdapterObserver;
import com.icourt.alpha.base.BaseDialogFragment;
import com.icourt.alpha.constants.Const;
import com.icourt.alpha.constants.SFileConfig;
import com.icourt.alpha.entity.bean.FolderDocumentEntity;
import com.icourt.alpha.entity.event.SeaFolderEvent;
import com.icourt.alpha.fragment.dialogfragment.FileDetailDialogFragment;
import com.icourt.alpha.fragment.dialogfragment.FolderDetailDialogFragment;
import com.icourt.alpha.fragment.dialogfragment.FolderTargetListDialogFragment;
import com.icourt.alpha.fragment.dialogfragment.RepoDetailsDialogFragment;
import com.icourt.alpha.http.IDefNotify;
import com.icourt.alpha.http.callback.SFileCallBack;
import com.icourt.alpha.http.observer.BaseObserver;
import com.icourt.alpha.interfaces.OnDialogFragmentDismissListener;
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

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.model.PhotoInfo;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Response;

import static com.icourt.alpha.constants.Const.FILE_ACTION_COPY;
import static com.icourt.alpha.constants.Const.FILE_ACTION_MOVE;
import static com.icourt.alpha.constants.Const.VIEW_TYPE_GRID;
import static com.icourt.alpha.constants.Const.VIEW_TYPE_ITEM;
import static com.icourt.alpha.constants.SFileConfig.PERMISSION_RW;
import static com.icourt.alpha.widget.comparators.FileSortComparator.FILE_SORT_TYPE_DEFAULT;

/**
 * Description  文件或者文件夹列表
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/8/10
 * version 2.1.0
 */
public class FolderListActivity extends FolderBaseActivity
        implements BaseRecyclerAdapter.OnItemClickListener,
        BaseRecyclerAdapter.OnItemLongClickListener,
        BaseRecyclerAdapter.OnItemChildClickListener,
        OnDialogFragmentDismissListener {

    private static final int MAX_LENGTH_FILE_NAME = 100;
    @BindView(R.id.titleBack)
    ImageView titleBack;
    @BindView(R.id.titleContent)
    TextView titleContent;
    @BindView(R.id.titleAction)
    ImageView titleAction;
    @BindView(R.id.titleAction2)
    ImageView titleAction2;
    @BindView(R.id.titleView)
    AppBarLayout titleView;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.refreshLayout)
    RefreshLayout refreshLayout;
    FolderDocumentWrapAdapter folderDocumentAdapter;
    HeaderFooterAdapter<FolderDocumentWrapAdapter> headerFooterAdapter;
    @BindView(R.id.bottom_bar_select_num_tv)
    TextView bottomBarSelectNumTv;
    private ArraySet<FolderDocumentEntity> selectedFolderDocuments = new ArraySet<>();
    View headerView;
    TextView footerView;


    @BindView(R.id.titleEditCancelView)
    CheckedTextView titleEditCancelView;
    @BindView(R.id.titleEditView)
    RelativeLayout titleEditView;
    @BindView(R.id.bottom_bar_copy_tv)
    TextView bottomBarCopyTv;
    @BindView(R.id.bottom_bar_move_tv)
    TextView bottomBarMoveTv;
    @BindView(R.id.bottom_bar_delete_tv)
    TextView bottomBarDeleteTv;
    @BindView(R.id.bottom_bar_layout)
    LinearLayout bottomBarLayout;
    @BindView(R.id.bottom_bar_all_select_cb)
    CheckBox bottomBarAllSelectCb;

    int fileSortType = FILE_SORT_TYPE_DEFAULT;
    final ArrayList<String> bigImageUrls = new ArrayList<>();
    final ArrayList<String> smallImageUrls = new ArrayList<>();

    private static final int REQUEST_CODE_CHOOSE_FILE = 1002;
    CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            if (b) {
                List<List<FolderDocumentEntity>> data = folderDocumentAdapter.getData();
                for (List<FolderDocumentEntity> documentEntities : data) {
                    selectedFolderDocuments.addAll(documentEntities);
                }
                folderDocumentAdapter.notifyDataSetChanged();
            } else {
                if (!selectedFolderDocuments.isEmpty()) {
                    selectedFolderDocuments.clear();
                    folderDocumentAdapter.notifyDataSetChanged();
                }
            }
            bottomBarSelectNumTv.setText(getString(R.string.sfile_file_already_selected, String.valueOf(selectedFolderDocuments.size())));
            updateActionViewStatus();
        }
    };

    /**
     * @param context
     * @param repoType       repo类型
     * @param repoPermission repo权限 "rw" "r"
     * @param seaFileRepoId  repoid
     * @param repoTitle      repo 标题
     * @param seaFileDirPath repo目录路径
     */
    public static void launch(@NonNull Context context,
                              @SFileConfig.REPO_TYPE int repoType,
                              @SFileConfig.FILE_PERMISSION String repoPermission,
                              String seaFileRepoId,
                              String repoTitle,
                              String seaFileDirPath) {
        if (context == null) return;
        Intent intent = new Intent(context, FolderListActivity.class);
        intent.putExtra(KEY_SEA_FILE_REPO_TYPE, repoType);
        intent.putExtra(KEY_SEA_FILE_REPO_TITLE, repoTitle);
        intent.putExtra(KEY_SEA_FILE_REPO_PERMISSION, repoPermission);
        intent.putExtra(KEY_SEA_FILE_REPO_ID, seaFileRepoId);
        intent.putExtra(KEY_SEA_FILE_DIR_PATH, seaFileDirPath);
        context.startActivity(intent);
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folder_list);
        ButterKnife.bind(this);
        initView();
    }


    @Override
    protected void initView() {
        super.initView();
        EventBus.getDefault().register(this);
        setTitle(getRepoTitle());
        ImageView titleActionImage = getTitleActionImage();
        if (titleActionImage != null) {
            titleActionImage.setImageResource(R.mipmap.header_icon_add);
            setViewInVisible(titleActionImage, TextUtils.equals(getRepoPermission(), PERMISSION_RW));
        }

        ImageView titleActionImage2 = getTitleActionImage2();
        if (titleActionImage2 != null) {
            titleActionImage2.setImageResource(R.mipmap.header_icon_more);
        }

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setAutoMeasureEnabled(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        headerFooterAdapter = new HeaderFooterAdapter<>(
                folderDocumentAdapter = new FolderDocumentWrapAdapter(
                        SFileConfig.getSFileLayoutType(getSeaFileRepoId(), VIEW_TYPE_ITEM),
                        getSeaFileRepoId(),
                        getSeaFileDirPath(),
                        false,
                        selectedFolderDocuments));
        addHeadView();

        addFooterView();


        folderDocumentAdapter.registerAdapterDataObserver(new DataChangeAdapterObserver() {
            @Override
            protected void updateUI() {
                if (footerView != null) {
                    int dirNum = 0, fileNum = 0;
                    for (int i = 0; i < folderDocumentAdapter.getItemCount(); i++) {
                        List<FolderDocumentEntity> items = folderDocumentAdapter.getItem(i);
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
                    }
                    if (dirNum == 0 && fileNum == 0) {
                        footerView.setText(R.string.sfile_folder_empty);
                    } else {
                        footerView.setText(getString(R.string.sfile_folder_statistics, String.valueOf(dirNum), String.valueOf(fileNum)));
                    }

                }
            }
        });

        recyclerView.setAdapter(headerFooterAdapter);

        folderDocumentAdapter.setOnItemLongClickListener(this);
        folderDocumentAdapter.setOnItemClickListener(this);
        folderDocumentAdapter.setOnItemChildClickListener(this);
        refreshLayout.setXRefreshViewListener(new XRefreshView.SimpleXRefreshListener() {

            @Override
            public void onRefresh(boolean isPullDown) {
                super.onRefresh(isPullDown);
                getData(true);
            }
        });

        bottomBarAllSelectCb.setOnCheckedChangeListener(onCheckedChangeListener);
        refreshLayout.setPullLoadEnable(false);

        showLoadingDialog(null);
        getData(true);
    }

    private boolean isAllSelected() {
        List<FolderDocumentEntity> allData = getAllData();
        return !allData.isEmpty() && selectedFolderDocuments.size() == allData.size();
    }

    private List<FolderDocumentEntity> getAllData() {
        List<FolderDocumentEntity> totals = new ArrayList<>();
        List<List<FolderDocumentEntity>> data = folderDocumentAdapter.getData();
        for (List<FolderDocumentEntity> documentEntities : data) {
            totals.addAll(documentEntities);
        }
        return totals;
    }


    private void addHeadView() {
        headerView = HeaderFooterAdapter.inflaterView(getContext(), R.layout.header_search_folder_document, recyclerView);
        registerClick(headerView.findViewById(R.id.header_search_direction_iv));
        registerClick(headerView.findViewById(R.id.header_search_sort_iv));
        registerClick(headerView.findViewById(R.id.header_comm_search_ll));
        headerFooterAdapter.addHeader(headerView);
    }

    private void addFooterView() {
        footerView = (TextView) HeaderFooterAdapter.inflaterView(getContext(), R.layout.footer_folder_document_num, recyclerView);
        headerFooterAdapter.addFooter(footerView);
        footerView.setText("");
    }

    @Override
    protected void onResume() {
        super.onResume();
        //恢复布局样式
        int sFileLayoutType = SFileConfig.getSFileLayoutType(getSeaFileRepoId(), VIEW_TYPE_ITEM);
        if (sFileLayoutType
                != folderDocumentAdapter.getAdapterViewType()) {
            folderDocumentAdapter.setAdapterViewType(sFileLayoutType);
        }
        //重写加载数据
        getData(true);
    }

    @Override
    protected void getData(final boolean isRefresh) {
        super.getData(isRefresh);
        callEnqueue(getSFileApi().documentDirQuery(
                getSeaFileRepoId(),
                getSeaFileDirPath()),
                new SFileCallBack<List<FolderDocumentEntity>>() {
                    @Override
                    public void onSuccess(Call<List<FolderDocumentEntity>> call, Response<List<FolderDocumentEntity>> response) {
                        //取消批量操作界面
                        onClick(titleEditCancelView);

                        sortFile(response.body());
                        stopRefresh();
                    }

                    @Override
                    public void onFailure(Call<List<FolderDocumentEntity>> call, Throwable t) {
                        super.onFailure(call, t);
                        dismissLoadingDialog();
                        stopRefresh();
                    }
                });
    }

    /**
     * 拆分列表
     *
     * @param datas
     * @return
     */
    private List<List<FolderDocumentEntity>> wrapGridData(List<FolderDocumentEntity> datas) {
        List<List<FolderDocumentEntity>> result = new ArrayList<>();
        if (datas != null && !datas.isEmpty()) {
            SparseArray<List<FolderDocumentEntity>> sparseArray = new SparseArray<>();
            for (int i = 0; i < datas.size(); i++) {
                int groupIndex = i / 4;
                List<FolderDocumentEntity> folderDocumentEntities = sparseArray.get(groupIndex, new ArrayList<FolderDocumentEntity>());
                folderDocumentEntities.add(datas.get(i));
                sparseArray.put(groupIndex, folderDocumentEntities);
            }
            for (int i = 0; i < sparseArray.size(); i++) {
                List<FolderDocumentEntity> folderDocumentEntities = sparseArray.get(i, new ArrayList<FolderDocumentEntity>());
                if (folderDocumentEntities.isEmpty()) continue;
                result.add(folderDocumentEntities);
            }
        }
        return result;
    }


    private void stopRefresh() {
        if (refreshLayout != null) {
            refreshLayout.stopRefresh();
            refreshLayout.stopLoadMore();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSeaFolderEvent(SeaFolderEvent event) {
        if (event == null) return;
        if (TextUtils.equals(event.from_repo_id, getSeaFileRepoId()) &&
                TextUtils.equals(event.from_parent_dir, getSeaFileDirPath())) {
            switch (event.action_type) {
                case FILE_ACTION_MOVE:
                    getData(true);
                    break;
                case FILE_ACTION_COPY:
                    getData(true);
                    break;
            }
        }
    }

    @OnClick({R.id.titleAction,
            R.id.titleAction2,
            R.id.titleEditCancelView,
            R.id.bottom_bar_copy_tv,
            R.id.bottom_bar_move_tv,
            R.id.bottom_bar_delete_tv})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.titleAction:
                showActionDialog();
                break;
            case R.id.titleAction2:
                showActionMoreDialog();
                break;
            case R.id.header_search_direction_iv:
                switch (folderDocumentAdapter.getAdapterViewType()) {
                    case VIEW_TYPE_ITEM:
                        folderDocumentAdapter.setAdapterViewType(VIEW_TYPE_GRID);
                        break;
                    case VIEW_TYPE_GRID:
                        folderDocumentAdapter.setAdapterViewType(VIEW_TYPE_ITEM);
                        break;
                }
                //内存保存
                SFileConfig.putSFileLayoutType(getSeaFileRepoId(), folderDocumentAdapter.getAdapterViewType());
                break;
            case R.id.header_search_sort_iv:
                showSortDialog();
                break;
            case R.id.titleEditCancelView:
                selectedFolderDocuments.clear();
                folderDocumentAdapter.setSelectable(false);
                folderDocumentAdapter.notifyDataSetChanged();
                updateSelectableModeSatue(folderDocumentAdapter.isSelectable());
                break;
            case R.id.bottom_bar_copy_tv:
                showFolderTargetListDialogFragment(Const.FILE_ACTION_COPY, new ArrayList<FolderDocumentEntity>(selectedFolderDocuments));
                break;
            case R.id.bottom_bar_move_tv:
                showFolderTargetListDialogFragment(Const.FILE_ACTION_MOVE, new ArrayList<FolderDocumentEntity>(selectedFolderDocuments));
                break;
            case R.id.bottom_bar_delete_tv:
                showDeleteComfirmDialog(selectedFolderDocuments);
                break;
            case R.id.header_comm_search_ll:
                SFileSearchActivity.launch(getActivity(), v.findViewById(R.id.rl_comm_search));
                break;
            default:
                super.onClick(v);
                break;
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

    private void showActionMoreDialog() {
        List<String> strings;
        if (folderDocumentAdapter.getItemCount() <= 0
                || !TextUtils.equals(getRepoPermission(), PERMISSION_RW)) {
            strings = Arrays.asList(getResources().getStringArray(R.array.sfile_folder_menus_r_array));
        } else {
            strings = Arrays.asList(getResources().getStringArray(R.array.sfile_folder_menus_rw_array));
        }
        new BottomActionDialog(getContext(),
                null,
                strings,
                new BottomActionDialog.OnActionItemClickListener() {
                    @Override
                    public void onItemClick(BottomActionDialog dialog, BottomActionDialog.ActionItemAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
                        dialog.dismiss();
                        String action = adapter.getItem(position);
                        if (TextUtils.equals(action, getString(R.string.sfile_menu_batch_operation))) {
                            folderDocumentAdapter.setSelectable(true);
                            folderDocumentAdapter.notifyDataSetChanged();
                            updateSelectableModeSatue(folderDocumentAdapter.isSelectable());
                        } else if (TextUtils.equals(action, getString(R.string.sfile_menu_repo_details))) {
                            RepoDetailsDialogFragment.show(
                                    getRepoType(),
                                    getSeaFileRepoId(),
                                    0,
                                    getRepoPermission(),
                                    getSupportFragmentManager());
                        } else if (TextUtils.equals(action, getString(R.string.sfile_menu_recycle_bin))) {
                            RepoDetailsDialogFragment.show(
                                    getRepoType(),
                                    getSeaFileRepoId(),
                                    2,
                                    getRepoPermission(),
                                    getSupportFragmentManager());
                        }
                    }
                }).show();
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
                            sortFile(getAllData());
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
                        folderDocumentAdapter.bindData(true, wrapGridData(folderDocumentEntities));
                    }

                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable throwable) {
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

    /**
     * 到选择模式 view的状态
     *
     * @param isSelectable
     */
    private void updateSelectableModeSatue(boolean isSelectable) {
        refreshLayout.setPullRefreshEnable(!isSelectable);
        titleEditView.setVisibility(isSelectable ? View.VISIBLE : View.GONE);
        titleView.setVisibility(!isSelectable ? View.VISIBLE : View.GONE);
        bottomBarLayout.setVisibility(isSelectable ? View.VISIBLE : View.GONE);
        bottomBarAllSelectCb.setChecked(false);
        bottomBarSelectNumTv.setText(getString(R.string.sfile_file_already_selected, String.valueOf(selectedFolderDocuments.size())));
        updateActionViewStatus();
    }


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
     * 上传文件
     *
     * @param filePaths 文件路径
     */
    private void uploadFiles(final List<String> filePaths) {
        if (filePaths != null
                && !filePaths.isEmpty()) {
            if (isDestroyOrFinishing()) return;
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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

    private void toggleSelect(FolderDocumentAdapter folderDocumentAdapter, int position) {
        FolderDocumentEntity item = folderDocumentAdapter.getItem(position);
        if (selectedFolderDocuments.contains(item)) {
            selectedFolderDocuments.remove(item);
        } else {
            selectedFolderDocuments.add(item);
        }
        bottomBarAllSelectCb.setOnCheckedChangeListener(null);
        bottomBarAllSelectCb.setChecked(isAllSelected());
        bottomBarAllSelectCb.setOnCheckedChangeListener(onCheckedChangeListener);
        folderDocumentAdapter.notifyDataSetChanged();
        updateActionViewStatus();
        bottomBarSelectNumTv.setText(getString(R.string.sfile_file_already_selected, String.valueOf(selectedFolderDocuments.size())));
    }

    private void updateActionViewStatus() {
        //未选中的时候 不能点击移动复制
        boolean canAction = !selectedFolderDocuments.isEmpty();
        bottomBarCopyTv.setEnabled(canAction);
        bottomBarMoveTv.setEnabled(canAction);
        bottomBarDeleteTv.setEnabled(canAction);
    }

    @Override
    public void onItemClick(BaseRecyclerAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
        if (adapter instanceof FolderDocumentAdapter) {
            FolderDocumentAdapter folderDocumentAdapter = (FolderDocumentAdapter) adapter;
            if (folderDocumentAdapter.isSelectable()) {
                toggleSelect(folderDocumentAdapter, position);
            } else {
                final FolderDocumentEntity item = folderDocumentAdapter.getItem(position);
                if (item == null) return;
                if (item.isDir()) {
                    FolderListActivity.launch(getContext(),
                            getRepoType(),
                            getRepoPermission(),
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
    }


    protected String getSFileImageUrl(String name, int size) {
        return String.format("%silaw/api/v2/documents/thumbnailImage?repoId=%s&seafileToken=%s&size=%s&p=%s",
                BuildConfig.API_URL,
                getSeaFileRepoId(),
                SFileTokenUtils.getSFileToken(),
                size,
                String.format("%s%s", getSeaFileDirPath(), name));
    }


    @Override
    public boolean onItemLongClick(BaseRecyclerAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
        if (adapter instanceof FolderDocumentAdapter) {
            FolderDocumentAdapter folderDocumentAdapter = (FolderDocumentAdapter) adapter;
            FolderDocumentEntity item = folderDocumentAdapter.getItem(position);
            if (item == null) return false;
            //有可读写权限
            boolean showFolderActionMenu = !folderDocumentAdapter.isSelectable()
                    && TextUtils.equals(item.permission, PERMISSION_RW);
            if (showFolderActionMenu) {
                showFolderActionMenu(adapter, position);
            }
        }
        return true;
    }

    @Override
    public void onItemChildClick(BaseRecyclerAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
        if (adapter instanceof FolderDocumentAdapter) {
            switch (view.getId()) {
                case R.id.document_expand_iv:
                    showFolderActionMenu(adapter, position);
                    break;
                case R.id.document_detail_iv:
                    FolderDocumentAdapter folderDocumentAdapter = (FolderDocumentAdapter) adapter;
                    lookDetails(folderDocumentAdapter.getItem(position));
                    break;
            }
        }
    }

    /**
     * 展示文件夹/文件复制移动等操作菜单
     *
     * @param position
     */
    private void showFolderActionMenu(BaseRecyclerAdapter adapter, int position) {
        final FolderDocumentEntity item = (FolderDocumentEntity) adapter.getItem(position);
        if (item == null) return;
        List<String> strings = Arrays.asList(getResources().getStringArray(R.array.sfile_file_menus_array));
        if (item.isDir()) {
            strings = Arrays.asList(getResources().getStringArray(R.array.sfile_folder_menus_array));
        }
        new BottomActionDialog(
                getContext(),
                null,
                strings,
                new BottomActionDialog.OnActionItemClickListener() {
                    @Override
                    public void onItemClick(BottomActionDialog dialog, BottomActionDialog.ActionItemAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
                        dialog.dismiss();
                        String action = adapter.getItem(position);
                        if (TextUtils.equals(action, getString(R.string.sfile_file_details))
                                || TextUtils.equals(action, getString(R.string.sfile_folder_details))) {
                            lookDetails(item);
                        } else if (TextUtils.equals(action, getString(R.string.sfile_file_rename))) {
                            FolderRenameActivity.launch(
                                    getContext(),
                                    item,
                                    getSeaFileRepoId(),
                                    getSeaFileDirPath());
                        } else if (TextUtils.equals(action, getString(R.string.sfile_file_share))) {
                            if (item.isDir()) {
                                FolderDetailDialogFragment.show(
                                        getSeaFileRepoId(),
                                        getSeaFileDirPath(),
                                        item.name,
                                        item.size,
                                        0,
                                        getRepoPermission(),
                                        getSupportFragmentManager());
                            } else {
                                FileDetailDialogFragment.show(
                                        getSeaFileRepoId(),
                                        getSeaFileDirPath(),
                                        item.name,
                                        item.size,
                                        1,
                                        getRepoPermission(),
                                        getSupportFragmentManager());
                            }
                        } else if (TextUtils.equals(action, getString(R.string.sfile_file_copy))) {
                            ArrayList<FolderDocumentEntity> folderDocumentEntities = new ArrayList<>();
                            folderDocumentEntities.add(item);
                            showFolderTargetListDialogFragment(Const.FILE_ACTION_COPY, folderDocumentEntities);
                        } else if (TextUtils.equals(action, getString(R.string.sfile_file_move))) {
                            ArrayList<FolderDocumentEntity> folderDocumentEntities1 = new ArrayList<>();
                            folderDocumentEntities1.add(item);
                            showFolderTargetListDialogFragment(Const.FILE_ACTION_MOVE, folderDocumentEntities1);
                        } else if (TextUtils.equals(action, getString(R.string.sfile_file_delete))) {
                            HashSet<FolderDocumentEntity> objects = new HashSet<>();
                            objects.add(item);
                            showDeleteComfirmDialog(objects);
                        }
                    }
                }).show();
    }

    private void lookDetails(final FolderDocumentEntity item) {
        if (item == null) return;
        if (item.isDir()) {
            FolderDetailDialogFragment.show(
                    getSeaFileRepoId(),
                    getSeaFileDirPath(),
                    item.name,
                    item.size,
                    0,
                    getRepoPermission(),
                    getSupportFragmentManager());
        } else {
            FileDetailDialogFragment.show(
                    getSeaFileRepoId(),
                    getSeaFileDirPath(),
                    item.name,
                    item.size,
                    0,
                    getRepoPermission(),
                    getSupportFragmentManager());
        }
    }

    protected final void showFolderTargetListDialogFragment(@Const.FILE_ACTION_TYPE int folderActionType,
                                                            ArrayList<FolderDocumentEntity> folderDocumentEntities) {
        if (folderDocumentEntities == null || folderDocumentEntities.isEmpty()) return;
        String tag = FolderTargetListDialogFragment.class.getSimpleName();
        FragmentTransaction mFragTransaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);
        if (fragment != null) {
            mFragTransaction.remove(fragment);
        }
        FolderTargetListDialogFragment.newInstance(
                folderActionType,
                getRepoType(),
                getSeaFileRepoId(),
                getSeaFileDirPath(),
                getSeaFileRepoId(),
                getSeaFileDirPath(),
                folderDocumentEntities)
                .show(mFragTransaction, tag);
    }

    /**
     * 批量删除文件或者删除文件夹
     *
     * @param items
     */
    private void deleteFolderOrDocuments(final Set<FolderDocumentEntity> items) {
        if (items == null || items.isEmpty()) return;
        seaFileDelete(getSeaFileRepoId(), getSeaFileDirPath(), items, new BaseObserver<JsonObject>() {
            @Override
            public void onNext(@io.reactivex.annotations.NonNull JsonObject jsonObject) {

            }

            @Override
            public void onSubscribe(@io.reactivex.annotations.NonNull Disposable disposable) {
                super.onSubscribe(disposable);
                showLoadingDialog(R.string.str_deleting);
            }

            @Override
            public void onError(@io.reactivex.annotations.NonNull Throwable throwable) {
                super.onError(throwable);
                dismissLoadingDialog();
            }

            @Override
            public void onComplete() {
                super.onComplete();
                dismissLoadingDialog();
                getData(true);
            }
        });
    }


    /**
     * 展示删除确认对话框
     *
     * @param items
     */
    private void showDeleteComfirmDialog(final Set<FolderDocumentEntity> items) {
        if (items == null || items.isEmpty()) return;
        showDeleteComfirmDialog(new BottomActionDialog.OnActionItemClickListener() {
            @Override
            public void onItemClick(BottomActionDialog dialog, BottomActionDialog.ActionItemAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
                dialog.dismiss();
                deleteFolderOrDocuments(items);
            }
        });
    }

    private void showDeleteComfirmDialog(BottomActionDialog.OnActionItemClickListener l) {
        new BottomActionDialog(
                getContext(),
                getString(R.string.sfile_delete_confirm),
                Arrays.asList(getString(R.string.str_delete)),
                l).show();
    }


    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (folderDocumentAdapter.isSelectable()) {
                onClick(titleEditCancelView);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onDialogFragmentDismiss(BaseDialogFragment baseDialogFragment) {
        if (isDestroyOrFinishing()) return;
        getData(true);
    }
}
