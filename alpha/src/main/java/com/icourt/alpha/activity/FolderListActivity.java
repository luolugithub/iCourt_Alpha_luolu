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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.andview.refreshview.XRefreshView;
import com.google.gson.JsonElement;
import com.icourt.alpha.R;
import com.icourt.alpha.adapter.FolderDocumentAdapter;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.base.BaseActivity;
import com.icourt.alpha.entity.bean.FolderDocumentEntity;
import com.icourt.alpha.entity.bean.SFileTokenEntity;
import com.icourt.alpha.fragment.dialogfragment.DocumentDetailDialogFragment;
import com.icourt.alpha.http.callback.SimpleCallBack2;
import com.icourt.alpha.utils.ImageUtils;
import com.icourt.alpha.utils.SystemUtils;
import com.icourt.alpha.utils.UriUtils;
import com.icourt.alpha.view.xrefreshlayout.RefreshLayout;
import com.icourt.alpha.widget.dialog.BottomActionDialog;
import com.icourt.api.RequestUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.finalteam.galleryfinal.FunctionConfig;
import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.model.PhotoInfo;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/8/10
 * version 2.1.0
 */
public class FolderListActivity extends BaseActivity implements BaseRecyclerAdapter.OnItemClickListener {
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
    FolderDocumentAdapter folderDocumentAdapter;

    String path;
    private static final int REQUEST_CODE_CAMERA = 1000;
    private static final int REQUEST_CODE_GALLERY = 1001;
    private static final int REQUEST_CODE_CHOOSE_FILE = 1002;

    private static final int REQ_CODE_PERMISSION_CAMERA = 1100;
    private static final int REQ_CODE_PERMISSION_ACCESS_FILE = 1101;

    public static void launch(@NonNull Context context,
                              String documentRootId,
                              String title,
                              String dirPath) {
        if (context == null) return;
        Intent intent = new Intent(context, FolderListActivity.class);
        intent.putExtra("documentRootId", documentRootId);
        intent.putExtra("title", title);
        intent.putExtra("dirPath", dirPath);
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
        setTitle(getIntent().getStringExtra("title"));
        ImageView titleActionImage = getTitleActionImage();
        if (titleActionImage != null) {
            titleActionImage.setImageResource(R.mipmap.header_icon_add);
        }

        ImageView titleActionImage2 = getTitleActionImage2();
        if (titleActionImage2 != null) {
            titleActionImage2.setImageResource(R.mipmap.header_icon_more);
        }


        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(folderDocumentAdapter = new FolderDocumentAdapter());
        folderDocumentAdapter.setOnItemClickListener(this);
        refreshLayout.setXRefreshViewListener(new XRefreshView.SimpleXRefreshListener() {

            @Override
            public void onRefresh(boolean isPullDown) {
                super.onRefresh(isPullDown);
                getData(true);
            }
        });
        refreshLayout.startRefresh();
    }

    private String getDocumentRootId() {
        return getIntent().getStringExtra("documentRootId");
    }

    private String getDocumentDirPath() {
        return getIntent().getStringExtra("dirPath");
    }

    @Override
    protected void getData(final boolean isRefresh) {
        super.getData(isRefresh);
        getSfileToken(new SimpleCallBack2<SFileTokenEntity<String>>() {
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

    private void getFolder(final boolean isRefresh, String sfileToken) {
        getSFileApi().documentDirQuery(
                String.format("Token %s", sfileToken),
                getDocumentRootId(),
                getDocumentDirPath())
                .enqueue(new SimpleCallBack2<List<FolderDocumentEntity>>() {
                    @Override
                    public void onSuccess(Call<List<FolderDocumentEntity>> call, Response<List<FolderDocumentEntity>> response) {
                        folderDocumentAdapter.bindData(isRefresh, response.body());
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

    /**
     * 获取sfile token
     *
     * @param callBack2
     */
    public void getSfileToken(@NonNull SimpleCallBack2<SFileTokenEntity<String>> callBack2) {
        getApi().documentTokenQuery()
                .enqueue(callBack2);
    }

    @OnClick({R.id.titleAction,
            R.id.titleAction2})
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
                                switch (position) {
                                    case 0:
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
            default:
                super.onClick(v);
                break;
        }
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
            getSfileToken(new SimpleCallBack2<SFileTokenEntity<String>>() {
                @Override
                public void onSuccess(Call<SFileTokenEntity<String>> call, Response<SFileTokenEntity<String>> response) {
                    dismissLoadingDialog();
                    if (TextUtils.isEmpty(response.body().authToken)) {
                        stopRefresh();
                        showTopSnackBar("sfile authToken返回为null");
                        return;
                    }
                    if (isDestroyOrFinishing()) return;

                    final String sfileToken = response.body().authToken;
                    //2.获取上传地址
                    showLoadingDialog("sfile 上传地址获取中...");
                    getSFileApi().sfileUploadUrlQuery(
                            String.format("Token %s", sfileToken),
                            getDocumentRootId(),
                            "upload",
                            getDocumentDirPath())
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
                params.put("parent_dir", RequestUtils.createTextBody(getDocumentDirPath()));
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

    @Override
    public void onItemClick(BaseRecyclerAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
        FolderDocumentEntity item = folderDocumentAdapter.getItem(position);
        if (item == null) return;
        if (item.isDir()) {
            FolderListActivity.launch(getContext(),
                    getDocumentRootId(),
                    item.name,
                    String.format("%s%s/", getDocumentDirPath(), item.name));
        } else {
            DocumentDetailDialogFragment.show("",
                    getSupportFragmentManager());
        }
    }
}
