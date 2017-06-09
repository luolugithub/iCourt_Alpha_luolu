package com.icourt.alpha.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.andview.refreshview.XRefreshView;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.icourt.alpha.R;
import com.icourt.alpha.activity.FileBoxDownloadActivity;
import com.icourt.alpha.activity.FileBoxListActivity;
import com.icourt.alpha.adapter.ProjectFileBoxAdapter;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.adapter.baseadapter.adapterObserver.RefreshViewEmptyObserver;
import com.icourt.alpha.base.BaseFragment;
import com.icourt.alpha.entity.bean.FileBoxBean;
import com.icourt.alpha.utils.DateUtils;
import com.icourt.alpha.utils.ItemDecorationUtils;
import com.icourt.alpha.utils.PingYinUtil;
import com.icourt.alpha.utils.SystemUtils;
import com.icourt.alpha.utils.TextFormater;
import com.icourt.alpha.view.xrefreshlayout.RefreshLayout;
import com.icourt.alpha.widget.dialog.BottomActionDialog;
import com.icourt.api.RequestUtils;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.finalteam.galleryfinal.FunctionConfig;
import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.model.PhotoInfo;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Description 项目详情：文档
 * Company Beijing icourt
 * author  lu.zhao  E-mail:zhaolu@icourt.cc
 * date createTime：17/5/9
 * version 2.0.0
 */

public class ProjectFileBoxFragment extends BaseFragment implements BaseRecyclerAdapter.OnItemClickListener {

    private static final String KEY_PROJECT_ID = "key_project_id";
    private static final int REQUEST_CODE_CAMERA = 1000;
    private static final int REQUEST_CODE_GALLERY = 1001;
    private static final int REQUEST_CODE_AT_MEMBER = 1002;

    private static final int REQ_CODE_PERMISSION_CAMERA = 1100;
    private static final int REQ_CODE_PERMISSION_ACCESS_FILE = 1101;
    Unbinder unbinder;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.refreshLayout)
    RefreshLayout refreshLayout;

    ProjectFileBoxAdapter projectFileBoxAdapter;
    String projectId;
    String authToken;//文档仓库token
    String seaFileRepoId;//文档根目录id
    String path;
    List<FileBoxBean> fileBoxBeanList;
    private List<String> firstlist;

    public static ProjectFileBoxFragment newInstance(@NonNull String projectId) {
        ProjectFileBoxFragment projectFileBoxFragment = new ProjectFileBoxFragment();
        Bundle bundle = new Bundle();
        bundle.putString(KEY_PROJECT_ID, projectId);
        projectFileBoxFragment.setArguments(bundle);
        return projectFileBoxFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(R.layout.fragment_project_mine, inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void initView() {
        projectId = getArguments().getString(KEY_PROJECT_ID);
        firstlist = TextFormater.firstList();
        firstlist.add(0, "#");
        refreshLayout.setNoticeEmpty(R.mipmap.icon_placeholder_project, "暂无文件");
        refreshLayout.setMoveForHorizontal(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(ItemDecorationUtils.getCommFull05Divider(getContext(), true));
        recyclerView.setHasFixedSize(true);

        recyclerView.setAdapter(projectFileBoxAdapter = new ProjectFileBoxAdapter());
        projectFileBoxAdapter.setOnItemClickListener(this);
        projectFileBoxAdapter.registerAdapterDataObserver(new RefreshViewEmptyObserver(refreshLayout, projectFileBoxAdapter));

        refreshLayout.setXRefreshViewListener(new XRefreshView.SimpleXRefreshListener() {
            @Override
            public void onRefresh(boolean isPullDown) {
                super.onRefresh(isPullDown);
                getData(true);
            }

            @Override
            public void onLoadMore(boolean isSilence) {
                super.onLoadMore(isSilence);
                getData(false);
            }
        });
        getFileBoxToken();

    }

    /**
     * 获取文档token
     */
    private void getFileBoxToken() {
        getApi().projectQueryFileBoxToken().enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.code() == 200) {
                    if (response.body() != null) {
                        if (response.body().has("authToken")) {
                            JsonElement element = response.body().get("authToken");
                            if (!TextUtils.isEmpty(element.toString()) && !TextUtils.equals("null", element.toString())) {
                                authToken = element.getAsString();
                                if (!TextUtils.isEmpty(authToken)) getDocumentId();
                            } else {
                                onFailure(call, new retrofit2.HttpException(response));
                            }
                        }
                    }
                } else {
                    onFailure(call, new retrofit2.HttpException(response));
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable throwable) {
                showTopSnackBar("获取文档token失败");
            }
        });
    }

    /**
     * 获取根目录id
     */
    private void getDocumentId() {
        getApi().projectQueryDocumentId(projectId).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.code() == 200) {
                    if (response.body() != null) {
                        if (response.body().has("seaFileRepoId")) {
                            JsonElement element = response.body().get("seaFileRepoId");
                            if (!TextUtils.isEmpty(element.toString()) && !TextUtils.equals("null", element.toString())) {
                                seaFileRepoId = element.getAsString();
                                getData(false);
                            } else {
                                onFailure(call, new retrofit2.HttpException(response));
                            }
                        }
                    }
                } else {
                    onFailure(call, new retrofit2.HttpException(response));
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable throwable) {
                showTopSnackBar("获取文档根目录id失败");
            }
        });
    }

    @Override
    protected void getData(final boolean isRefresh) {
        if (!TextUtils.isEmpty(authToken) && !TextUtils.isEmpty(seaFileRepoId)) {
            getSFileApi().projectQueryFileBoxList("Token " + authToken, seaFileRepoId).enqueue(new Callback<List<FileBoxBean>>() {
                @Override
                public void onResponse(Call<List<FileBoxBean>> call, Response<List<FileBoxBean>> response) {
                    stopRefresh();
                    if (response.body() != null) {
                        fileBoxBeanList = response.body();
                        projectFileBoxAdapter.bindData(isRefresh, fileBoxBeanList);
                    }
                }

                @Override
                public void onFailure(Call<List<FileBoxBean>> call, Throwable t) {
                    stopRefresh();
                    showTopSnackBar("获取文档列表失败");
                }
            });
        } else {
            stopRefresh();
            showTopSnackBar("获取文档列表失败");
        }
    }

    private void stopRefresh() {
        if (refreshLayout != null) {
            refreshLayout.stopRefresh();
            refreshLayout.stopLoadMore();
        }
    }

    /**
     * 按名称排序
     *
     * @param isUp
     */
    public void sortFileByNameList(final boolean isUp) {
        Collections.sort(fileBoxBeanList, new Comparator<FileBoxBean>() {
            @Override
            public int compare(FileBoxBean o1, FileBoxBean o2) {
                if (o1 == null || o2 == null) {
                    return 1;
                }
                int o1Name = firstlist.indexOf(String.valueOf(PingYinUtil.getFirstUpperLetter(getContext(), PingYinUtil.getPingYin(getContext(), o1.name))).toLowerCase());
                int o2Name = firstlist.indexOf(String.valueOf(PingYinUtil.getFirstUpperLetter(getContext(), PingYinUtil.getPingYin(getContext(), o2.name))).toLowerCase());

                if (isUp) {
                    //按照名称进行降序排列
                    if (o1Name < o2Name) {
                        return 1;
                    }
                    if (o1Name == o2Name) {
                        return 0;
                    }
                    return -1;
                } else {
                    //按照名称进行升序排列
                    if (o1Name > o2Name) {
                        return 1;
                    }
                    if (o1Name == o2Name) {
                        return 0;
                    }
                    return -1;
                }
            }
        });

        projectFileBoxAdapter.bindData(true, fileBoxBeanList);
    }

    /**
     * 按文件大小排序
     *
     * @param isUp
     */
    public void sortFileBySizeList(final boolean isUp) {
        Collections.sort(fileBoxBeanList, new Comparator<FileBoxBean>() {
            @Override
            public int compare(FileBoxBean o1, FileBoxBean o2) {
                long o1Size = o1.size;
                long o2Size = o2.size;

                if (isUp) {
                    //按照文件大小进行降序排列
                    if (o1Size < o2Size) {
                        return 1;
                    }
                    if (o1Size == o2Size) {
                        return 0;
                    }
                    return -1;
                } else {
                    //按照文件大小进行升序排列
                    if (o1Size > o2Size) {
                        return 1;
                    }
                    if (o1Size == o2Size) {
                        return 0;
                    }
                    return -1;
                }
            }
        });

        projectFileBoxAdapter.bindData(true, fileBoxBeanList);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @Override
    public void onItemClick(BaseRecyclerAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
        FileBoxBean fileBoxBean = (FileBoxBean) adapter.getItem(position);
        if (!TextUtils.isEmpty(fileBoxBean.type)) {
            if (TextUtils.equals("file", fileBoxBean.type)) {
                FileBoxDownloadActivity.launch(getContext(), authToken, seaFileRepoId, fileBoxBean.name, FileBoxDownloadActivity.PROJECT_DOWNLOAD_FILE_ACTION);
            }
            if (TextUtils.equals("dir", fileBoxBean.type)) {
                FileBoxListActivity.launch(getContext(), fileBoxBean, authToken, seaFileRepoId, fileBoxBean.name);
            }
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
            if (resultList != null) {
                for (PhotoInfo photoInfo : resultList) {
                    if (photoInfo != null && !TextUtils.isEmpty(photoInfo.getPhotoPath())) {
                        getUploadUrl(photoInfo.getPhotoPath());
                    }
                }
            }
        }

        @Override
        public void onHanlderFailure(int requestCode, String errorMsg) {

        }
    };

    /**
     * 显示底部菜单
     */
    public void showBottomMeau() {
        new BottomActionDialog(getContext(),
                null,
                Arrays.asList("拍照", "从手机相册选择"),
                new BottomActionDialog.OnActionItemClickListener() {
                    @Override
                    public void onItemClick(BottomActionDialog dialog, BottomActionDialog.ActionItemAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
                        dialog.dismiss();
                        switch (position) {
                            case 0:
                                checkAndOpenCamera();
                                break;
                            case 1:
                                checkAndOpenPhotos();
                                break;
                        }
                    }
                }).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_CAMERA:
                if (resultCode == Activity.RESULT_OK) {
                    getUploadUrl(path);
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    /**
     * 获取上传文件url
     *
     * @param filePath
     */
    private void getUploadUrl(final String filePath) {
        if (TextUtils.isEmpty(filePath)) return;
        File file = new File(filePath);
        if (!file.exists()) {
            showTopSnackBar("文件不存在啦");
            return;
        }
        showLoadingDialog("正在上传...");
        getSFileApi().projectUploadUrlQuery("Token " + authToken, seaFileRepoId).enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (response.body() != null) {
                    String uploadUrl = response.body().getAsString();
                    if (!TextUtils.isEmpty(uploadUrl) && uploadUrl.startsWith("http")) {
                        uploadFile(uploadUrl, filePath);
                    } else {
                        dismissLoadingDialog();
                        showTopSnackBar("上传失败");
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable throwable) {
                dismissLoadingDialog();
                showTopSnackBar("上传失败");
            }

        });
    }

    /**
     * 上传文件
     *
     * @param uploadUrl
     * @param filePath
     */
    private void uploadFile(String uploadUrl, String filePath) {
        String key = "file\";filename=\"" + DateUtils.millis() + ".png";
        Map<String, RequestBody> params = new HashMap<>();
        params.put("parent_dir", RequestUtils.createTextBody("/"));
        params.put(key, RequestUtils.createImgBody(new File(filePath)));
        getSFileApi().projectUploadFile("Token " + authToken, uploadUrl, params).enqueue(new Callback<JsonElement>() {

            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                dismissLoadingDialog();
                showTopSnackBar("上传成功");
                getData(true);
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                dismissLoadingDialog();
                showTopSnackBar("上传失败");
            }
        });
    }
}
