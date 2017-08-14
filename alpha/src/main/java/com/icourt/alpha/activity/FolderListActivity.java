package com.icourt.alpha.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
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
import com.icourt.alpha.constants.Const;
import com.icourt.alpha.entity.bean.FolderDocumentEntity;
import com.icourt.alpha.entity.bean.ItemsEntity;
import com.icourt.alpha.entity.bean.SFileTokenEntity;
import com.icourt.alpha.entity.event.SeaFolderEvent;
import com.icourt.alpha.fragment.dialogfragment.DocumentDetailDialogFragment;
import com.icourt.alpha.fragment.dialogfragment.FolderDetailDialogFragment;
import com.icourt.alpha.fragment.dialogfragment.FolderTargetListDialogFragment;
import com.icourt.alpha.http.callback.SimpleCallBack2;
import com.icourt.alpha.interfaces.ISeaFileImageLoader;
import com.icourt.alpha.utils.GlideUtils;
import com.icourt.alpha.utils.ImageUtils;
import com.icourt.alpha.utils.SystemUtils;
import com.icourt.alpha.utils.UriUtils;
import com.icourt.alpha.view.xrefreshlayout.RefreshLayout;
import com.icourt.alpha.widget.dialog.BottomActionDialog;
import com.icourt.alpha.widget.dialog.CenterMenuDialog;
import com.icourt.api.RequestUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.finalteam.galleryfinal.FunctionConfig;
import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.model.PhotoInfo;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;

import static com.icourt.alpha.constants.Const.FILE_ACTION_COPY;
import static com.icourt.alpha.constants.Const.FILE_ACTION_MOVE;
import static com.icourt.alpha.constants.Const.VIEW_TYPE_GRID;
import static com.icourt.alpha.constants.Const.VIEW_TYPE_ITEM;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/8/10
 * version 2.1.0
 */
public class FolderListActivity extends FolderBaseActivity
        implements BaseRecyclerAdapter.OnItemClickListener, BaseRecyclerAdapter.OnItemLongClickListener, BaseRecyclerAdapter.OnItemChildClickListener {
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

    String path;
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
    private String sFileToken;

    private static final int REQUEST_CODE_CAMERA = 1000;
    private static final int REQUEST_CODE_GALLERY = 1001;
    private static final int REQUEST_CODE_CHOOSE_FILE = 1002;

    private static final int REQ_CODE_PERMISSION_CAMERA = 1100;
    private static final int REQ_CODE_PERMISSION_ACCESS_FILE = 1101;
    CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            if (b) {
                List<List<FolderDocumentEntity>> data = folderDocumentAdapter.getData();
                for (List<FolderDocumentEntity> documentEntities : data) {
                    selectedFolderDocuments.addAll(documentEntities);
                }
                folderDocumentAdapter.notifyDataSetChanged();
                bottomBarSelectNumTv.setText(String.format("已选择: %s", selectedFolderDocuments.size()));
            } else {
                if (!selectedFolderDocuments.isEmpty()) {
                    selectedFolderDocuments.clear();
                    folderDocumentAdapter.notifyDataSetChanged();
                }
                bottomBarSelectNumTv.setText(String.format("已选择: %s", selectedFolderDocuments.size()));
            }
        }
    };

    public static void launch(@NonNull Context context,
                              String seaFileRepoId,
                              String title,
                              String seaFileDirPath) {
        if (context == null) return;
        Intent intent = new Intent(context, FolderListActivity.class);
        intent.putExtra("title", title);
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
        setTitle(getIntent().getStringExtra("title"));
        ImageView titleActionImage = getTitleActionImage();
        if (titleActionImage != null) {
            titleActionImage.setImageResource(R.mipmap.header_icon_add);
        }

        ImageView titleActionImage2 = getTitleActionImage2();
        if (titleActionImage2 != null) {
            titleActionImage2.setImageResource(R.mipmap.header_icon_more);
        }
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setAutoMeasureEnabled(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        headerFooterAdapter = new HeaderFooterAdapter<>(
                folderDocumentAdapter = new FolderDocumentWrapAdapter(VIEW_TYPE_ITEM,
                        new ISeaFileImageLoader() {
                            @Override
                            public void loadSFileImage(String fileName, ImageView view, int type, int size) {
                                GlideUtils.loadSFilePic(getContext(), getSfileThumbnailImage(fileName), view);
                            }
                        },
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
                    footerView.setText(String.format("%s个文件夹, %s个文件", dirNum, fileNum));
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
    }

    private boolean isAllSelected() {
        List<FolderDocumentEntity> totals = new ArrayList<>();
        List<List<FolderDocumentEntity>> data = folderDocumentAdapter.getData();
        for (List<FolderDocumentEntity> documentEntities : data) {
            totals.addAll(documentEntities);
        }
        return selectedFolderDocuments.size() == totals.size();
    }

    /**
     * 获取缩略图地址
     *
     * @param name
     * @return
     */
    private String getSfileThumbnailImage(String name) {
        //https://test.alphalawyer.cn/ilaw/api/v2/documents/thumbnailImage?repoId=d4f82446-a37f-478c-b6b5-ed0e779e1768&seafileToken=%20d6c69d6f4fc208483c243246c6973d8eb141501c&p=//1502507774237.png&size=250
        return String.format("%sapi/v2/documents/thumbnailImage?repoId=%s&seafileToken=%s&p=%s&size=%s",
                BuildConfig.API_URL,
                getSeaFileRepoId(),
                sFileToken,
                String.format("%s%s", getSeaFileDirPath(), name),
                150);
    }

    private void addHeadView() {
        headerView = HeaderFooterAdapter.inflaterView(getContext(), R.layout.header_search_folder_document, recyclerView);
        registerClick(headerView.findViewById(R.id.header_search_direction_iv));
        registerClick(headerView.findViewById(R.id.header_search_sort_iv));
        headerFooterAdapter.addHeader(headerView);
    }

    private void addFooterView() {
        footerView = (TextView) HeaderFooterAdapter.inflaterView(getContext(), R.layout.footer_folder_document_num, recyclerView);
        headerFooterAdapter.addFooter(footerView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshLayout.startRefresh();
    }

    @Override
    protected void getData(final boolean isRefresh) {
        super.getData(isRefresh);
        getSFileToken(new SimpleCallBack2<SFileTokenEntity<String>>() {
            @Override
            public void onSuccess(Call<SFileTokenEntity<String>> call, Response<SFileTokenEntity<String>> response) {
                if (TextUtils.isEmpty(response.body().authToken)) {
                    stopRefresh();
                    showTopSnackBar("sfile authToken返回为null");
                    return;
                }
                getFolder(isRefresh, response.body().authToken);
            }
        });

    }

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

    private void getFolder(final boolean isRefresh, String sfileToken) {
        this.sFileToken = sfileToken;
        getSFileApi().documentDirQuery(
                String.format("Token %s", sfileToken),
                getSeaFileRepoId(),
                getSeaFileDirPath())
                .enqueue(new SimpleCallBack2<List<FolderDocumentEntity>>() {
                    @Override
                    public void onSuccess(Call<List<FolderDocumentEntity>> call, Response<List<FolderDocumentEntity>> response) {
                        folderDocumentAdapter.bindData(isRefresh, wrapGridData(response.body()));
                        stopRefresh();
                    }

                    @Override
                    public void onFailure(Call<List<FolderDocumentEntity>> call, Throwable t) {
                        super.onFailure(call, t);
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
                new BottomActionDialog(getContext(),
                        null,
                        Arrays.asList("新建文件夹", "上传文件", "从相册选取", "拍照"),
                        new BottomActionDialog.OnActionItemClickListener() {
                            @Override
                            public void onItemClick(BottomActionDialog dialog, BottomActionDialog.ActionItemAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
                                dialog.dismiss();
                                switch (position) {
                                    case 0:
                                        FolderActionActivity.launchCreate(
                                                getContext(),
                                                getSeaFileRepoId(),
                                                getSeaFileDirPath());
                                        break;
                                    case 1:
                                        if (checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                                            SystemUtils.chooseFile(getActivity(), REQUEST_CODE_CHOOSE_FILE);
                                        } else {
                                            reqPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, "我们需要文件读写权限!", REQ_CODE_PERMISSION_ACCESS_FILE);
                                        }
                                        break;
                                    case 2:
                                        checkAndOpenPhotos();
                                        break;
                                    case 3:
                                        checkAndOpenCamera();
                                        break;
                                }
                            }
                        }).show();
                break;
            case R.id.titleAction2:
                new BottomActionDialog(getContext(),
                        null,
                        Arrays.asList("批量操作", "回收站", "修改历史", "文件夹详情"),
                        new BottomActionDialog.OnActionItemClickListener() {
                            @Override
                            public void onItemClick(BottomActionDialog dialog, BottomActionDialog.ActionItemAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
                                dialog.dismiss();
                                switch (position) {
                                    case 0:
                                        folderDocumentAdapter.setSelectable(true);
                                        folderDocumentAdapter.notifyDataSetChanged();
                                        updateSelectableModeSatue(folderDocumentAdapter.isSelectable());
                                        break;
                                    case 1:
                                        break;
                                    case 2:
                                        break;
                                    case 3:
                                        break;
                                }
                            }
                        }).show();
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
                break;
            case R.id.header_search_sort_iv:
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
                deleteFolderOrDocuments(selectedFolderDocuments);
                break;
            default:
                super.onClick(v);
                break;
        }
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
        bottomBarSelectNumTv.setText(String.format("已选择: %s", selectedFolderDocuments.size()));
    }


    /**
     * 打开相机
     */
    private void checkAndOpenCamera() {
        if (checkPermission(Manifest.permission.CAMERA)) {
            path = SystemUtils.getFileDiskCache(getContext()) + File.separator
                    + System.currentTimeMillis() + ".png";
            Uri picUri = Uri.fromFile(new File(path));
            SystemUtils.doTakePhotoAction(this, picUri, REQUEST_CODE_CAMERA);
        } else {
            reqPermission(Manifest.permission.CAMERA, "我们需要拍照权限!", REQ_CODE_PERMISSION_CAMERA);
        }
    }

    /**
     * 打开相册
     */
    private void checkAndOpenPhotos() {
        if (checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            FunctionConfig config = new FunctionConfig.Builder()
                    .setMutiSelectMaxSize(9)
                    .build();
            GalleryFinal.openGalleryMuti(REQUEST_CODE_GALLERY, config, mOnHanlderResultCallback);
        } else {
            reqPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, "我们需要文件读写权限!", REQ_CODE_PERMISSION_ACCESS_FILE);
        }
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
            showTopSnackBar("文件不存在");
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
            //1.获取token
            showLoadingDialog("sfile token获取中...");
            getSFileToken(new SimpleCallBack2<SFileTokenEntity<String>>() {
                @Override
                public void onSuccess(Call<SFileTokenEntity<String>> call, Response<SFileTokenEntity<String>> response) {
                    dismissLoadingDialog();
                    if (TextUtils.isEmpty(response.body().authToken)) {
                        stopRefresh();
                        showTopSnackBar("sfile authToken返回为null");
                        return;
                    }
                    sFileToken = response.body().authToken;
                    if (isDestroyOrFinishing()) return;

                    final String sfileToken = response.body().authToken;
                    //2.获取上传地址
                    showLoadingDialog("sfile 上传地址获取中...");
                    getSFileApi().sfileUploadUrlQuery(
                            String.format("Token %s", sfileToken),
                            getSeaFileRepoId(),
                            "upload",
                            getSeaFileDirPath())
                            .enqueue(new SimpleCallBack2<String>() {
                                @Override
                                public void onSuccess(Call<String> call, Response<String> response) {
                                    if (isDestroyOrFinishing()) return;
                                    dismissLoadingDialog();
                                    uploadFiles(filePaths, response.body(), sfileToken);
                                }

                                @Override
                                public void onFailure(Call<String> call, Throwable t) {
                                    super.onFailure(call, t);
                                    dismissLoadingDialog();
                                }
                            });
                }

                @Override
                public void onFailure(Call<SFileTokenEntity<String>> call, Throwable t) {
                    dismissLoadingDialog();
                    super.onFailure(call, t);
                }
            });
        }
    }

    /**
     * 上传文件
     *
     * @param filePaths 文件路径
     * @param serverUrl 服务器路径
     */
    private void uploadFiles(List<String> filePaths, @NonNull String serverUrl, @NonNull String sfileToken) {
        if (filePaths != null
                && !filePaths.isEmpty()
                && !TextUtils.isEmpty(serverUrl)) {
            for (int i = 0; i < filePaths.size(); i++) {
                if (isDestroyOrFinishing()) break;

                String filePath = filePaths.get(i);
                if (TextUtils.isEmpty(filePath)) {
                    showTopSnackBar(String.format("第%s个文件不存在", i + 1));
                    continue;
                }
                File file = new File(filePath);
                if (!file.exists()) {
                    showTopSnackBar(String.format("第%s个文件不存在", i + 1));
                    continue;
                }

                showLoadingDialog("上传中...");
                Map<String, RequestBody> params = new HashMap<>();
                params.put(RequestUtils.createStreamKey(file), RequestUtils.createStreamBody(file));
                params.put("parent_dir", RequestUtils.createTextBody(getSeaFileDirPath()));
                // params.put("relative_path",RequestUtils.createTextBody(""));
                getSFileApi().sfileUploadFile(sfileToken, serverUrl, params)
                        .enqueue(new SimpleCallBack2<JsonElement>() {
                            @Override
                            public void onSuccess(Call<JsonElement> call, Response<JsonElement> response) {
                                dismissLoadingDialog();
                                getData(true);
                            }

                            @Override
                            public void onFailure(Call<JsonElement> call, Throwable t) {
                                super.onFailure(call, t);
                                dismissLoadingDialog();
                            }
                        });
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQ_CODE_PERMISSION_CAMERA:
                if (grantResults != null) {
                    if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                        showTopSnackBar("拍照权限被拒绝");
                    }
                }
                break;
            case REQ_CODE_PERMISSION_ACCESS_FILE:
                if (grantResults != null) {
                    if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                        showTopSnackBar("文件读写权限被拒绝");
                    }
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_CAMERA:
                if (resultCode == Activity.RESULT_OK) {
                    if (!TextUtils.isEmpty(path) && ImageUtils.getBitmapDegree(path) > 0) {
                        ImageUtils.degreeImage(path);
                    }
                    uploadFile(path);
                }
                break;
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
        bottomBarSelectNumTv.setText(String.format("已选择: %s", selectedFolderDocuments.size()));
    }

    @Override
    public void onItemClick(BaseRecyclerAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
        if (adapter instanceof FolderDocumentAdapter) {
            FolderDocumentAdapter folderDocumentAdapter = (FolderDocumentAdapter) adapter;
            if (folderDocumentAdapter.isSelectable()) {
                toggleSelect(folderDocumentAdapter, position);
            } else {
                FolderDocumentEntity item = folderDocumentAdapter.getItem(position);
                if (item == null) return;
                if (item.isDir()) {
                    FolderListActivity.launch(getContext(),
                            getSeaFileRepoId(),
                            item.name,
                            String.format("%s%s/", getSeaFileDirPath(), item.name));
                } else {
                    DocumentDetailDialogFragment.show("",
                            getSupportFragmentManager());
                }
            }
        }
    }

    @Override
    public boolean onItemLongClick(BaseRecyclerAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
        if (adapter instanceof FolderDocumentAdapter) {
            FolderDocumentAdapter folderDocumentAdapter = (FolderDocumentAdapter) adapter;
            if (!folderDocumentAdapter.isSelectable()) {
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
        final CenterMenuDialog centerMenuDialog = new CenterMenuDialog(getContext(), null, Arrays.asList(
                new ItemsEntity("详细信息", R.mipmap.info_orange),
                new ItemsEntity("复制", R.mipmap.copy_orange),
                new ItemsEntity("移动", R.mipmap.move_orange),
                new ItemsEntity("重命名", R.mipmap.rename_orange),
                new ItemsEntity("共享", R.mipmap.share_orange),
                new ItemsEntity("删除", R.mipmap.trash_orange)));
        centerMenuDialog.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseRecyclerAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
                if (centerMenuDialog != null) {
                    centerMenuDialog.dismiss();
                }
                switch (position) {
                    case 0:
                        if (item.isDir()) {
                            FolderDetailDialogFragment.show(
                                    item.id,
                                    getSupportFragmentManager());
                        } else {
                            DocumentDetailDialogFragment.show(
                                    item.id,
                                    getSupportFragmentManager());
                        }
                        break;
                    case 1:
                        ArrayList<FolderDocumentEntity> folderDocumentEntities = new ArrayList<>();
                        folderDocumentEntities.add(item);
                        showFolderTargetListDialogFragment(Const.FILE_ACTION_COPY, folderDocumentEntities);
                        break;
                    case 2:
                        ArrayList<FolderDocumentEntity> folderDocumentEntities1 = new ArrayList<>();
                        folderDocumentEntities1.add(item);
                        showFolderTargetListDialogFragment(Const.FILE_ACTION_MOVE, folderDocumentEntities1);
                        break;
                    case 3:
                        FolderActionActivity.launchUpdateTitle(getContext(),
                                item,
                                getSeaFileRepoId(),
                                getSeaFileDirPath());
                        break;
                    case 4:
                        break;
                    case 5:
                        deleteFolderOrDocument(item);
                        break;
                }
            }
        });
        centerMenuDialog.show();
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
        showLoadingDialog("sfile token获取中...");
        getSFileToken(new SimpleCallBack2<SFileTokenEntity<String>>() {
            @Override
            public void onSuccess(Call<SFileTokenEntity<String>> call, Response<SFileTokenEntity<String>> response) {
                if (TextUtils.isEmpty(response.body().authToken)) {
                    stopRefresh();
                    dismissLoadingDialog();
                    showTopSnackBar("sfile authToken返回为null");
                    return;
                }
                sFileToken = response.body().authToken;


                //循环删除
                for (FolderDocumentEntity item : items) {
                    Call<JsonObject> delCall = null;
                    if (item.isDir()) {
                        delCall = getSFileApi()
                                .folderDelete(
                                        String.format("Token %s", response.body().authToken),
                                        getSeaFileRepoId(),
                                        String.format("%s%s", getSeaFileDirPath(), item.name));
                    } else {
                        delCall = getSFileApi()
                                .documentDelete(
                                        String.format("Token %s", response.body().authToken),
                                        getSeaFileRepoId(),
                                        String.format("%s%s", getSeaFileDirPath(), item.name));
                    }
                    showLoadingDialog("删除中...");
                    delCall.enqueue(new SimpleCallBack2<JsonObject>() {
                        @Override
                        public void onSuccess(Call<JsonObject> call, Response<JsonObject> response) {
                            dismissLoadingDialog();
                            if (response.body().has("success")
                                    && response.body().get("success").getAsBoolean()) {
                                getData(true);
                            }
                        }

                        @Override
                        public void onFailure(Call<JsonObject> call, Throwable t) {
                            super.onFailure(call, t);
                            dismissLoadingDialog();
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<SFileTokenEntity<String>> call, Throwable t) {
                super.onFailure(call, t);
                dismissLoadingDialog();
            }
        });
    }

    /**
     * 删除文件或者删除文件夹
     *
     * @param item
     */
    private void deleteFolderOrDocument(final FolderDocumentEntity item) {
        if (item == null) return;
        showLoadingDialog("sfile token获取中...");
        getSFileToken(new SimpleCallBack2<SFileTokenEntity<String>>() {
            @Override
            public void onSuccess(Call<SFileTokenEntity<String>> call, Response<SFileTokenEntity<String>> response) {
                if (TextUtils.isEmpty(response.body().authToken)) {
                    stopRefresh();
                    dismissLoadingDialog();
                    showTopSnackBar("sfile authToken返回为null");
                    return;
                }
                sFileToken = response.body().authToken;
                //删除
                Call<JsonObject> delCall = null;
                if (item.isDir()) {
                    delCall = getSFileApi()
                            .folderDelete(
                                    String.format("Token %s", response.body().authToken),
                                    getSeaFileRepoId(),
                                    String.format("%s%s", getSeaFileDirPath(), item.name));
                } else {
                    delCall = getSFileApi()
                            .documentDelete(
                                    String.format("Token %s", response.body().authToken),
                                    getSeaFileRepoId(),
                                    String.format("%s%s", getSeaFileDirPath(), item.name));
                }
                showLoadingDialog("删除中...");
                delCall.enqueue(new SimpleCallBack2<JsonObject>() {
                    @Override
                    public void onSuccess(Call<JsonObject> call, Response<JsonObject> response) {
                        dismissLoadingDialog();
                        if (response.body().has("success") && response.body().get("success").getAsBoolean()) {
                            showTopSnackBar("删除成功");
                            getData(true);
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        super.onFailure(call, t);
                        dismissLoadingDialog();
                    }
                });
            }

            @Override
            public void onFailure(Call<SFileTokenEntity<String>> call, Throwable t) {
                super.onFailure(call, t);
                dismissLoadingDialog();
            }
        });
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
