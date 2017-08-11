package com.icourt.alpha.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import com.icourt.alpha.adapter.ProjectFileBoxAdapter;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.adapter.baseadapter.adapterObserver.RefreshViewEmptyObserver;
import com.icourt.alpha.base.BaseActivity;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.finalteam.galleryfinal.FunctionConfig;
import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.model.PhotoInfo;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Description  文档列表
 * Company Beijing icourt
 * author  lu.zhao  E-mail:zhaolu@icourt.cc
 * date createTime：17/5/15
 * version 2.0.0
 */

public class FileBoxListActivity extends BaseActivity implements BaseRecyclerAdapter.OnItemClickListener {
    private static final int REQUEST_CODE_CAMERA = 1000;
    private static final int REQUEST_CODE_GALLERY = 1001;
    private static final int REQUEST_CODE_AT_MEMBER = 1002;

    private static final int REQ_CODE_PERMISSION_CAMERA = 1100;
    private static final int REQ_CODE_PERMISSION_ACCESS_FILE = 1101;
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

    FileBoxBean fileBoxBean;
    String authToken, seaFileRepoId, rootName, path;
    ProjectFileBoxAdapter projectFileBoxAdapter;
    List<FileBoxBean> fileBoxBeanList;
    private List<String> firstlist;
    private boolean nameIsUp = false, timeIsUp = false, sizeIsUp = false;
    final List<String> list = new ArrayList<>();
    private boolean sortIsUp = false;
    private int sort_type = 0;//排序方式
    private static final int NAME_SORT_TYPE = 1;//按名称排序
    private static final int TIME_SORT_TYPE = 2;//按时间排序
    private static final int SIZE_SORT_TYPE = 3;//按大小排序

    public static void launch(@NonNull Context context, @NonNull FileBoxBean fileBoxBean, @NonNull String authToken, @NonNull String seaFileRepoId, @NonNull String rootName) {
        if (context == null) return;
        Intent intent = new Intent(context, FileBoxListActivity.class);
        intent.putExtra("file", fileBoxBean);
        intent.putExtra("authToken", authToken);
        intent.putExtra("seaFileRepoId", seaFileRepoId);
        intent.putExtra("rootName", rootName);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_box_list_layout);
        ButterKnife.bind(this);
        initView();

    }

    @Override
    protected void initView() {
        super.initView();
        list.add("按文件名升序排序");
        list.add("按文件大小升序排序");
        list.add("按修改时间升序排序");
        seaFileRepoId = getIntent().getStringExtra("seaFileRepoId");
        rootName = getIntent().getStringExtra("rootName");
        authToken = getIntent().getStringExtra("authToken");
        fileBoxBean = (FileBoxBean) getIntent().getSerializableExtra("file");
        if (fileBoxBean != null) {
            setTitle(fileBoxBean.name);
        }
        firstlist = TextFormater.firstList();
        firstlist.add(0, "#");
        titleAction.setImageResource(R.mipmap.header_icon_add);
        titleAction2.setImageResource(R.mipmap.header_icon_more);
        refreshLayout.setNoticeEmpty(R.mipmap.icon_placeholder_project, "暂无附件");
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
        refreshLayout.setAutoRefresh(true);
        refreshLayout.startRefresh();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.titleAction:
                showBottomMeau();
                break;
            case R.id.titleAction2:
                showDocumentMeau();
                break;
        }
    }

    @Override
    protected void getData(final boolean isRefresh) {
        super.getData(isRefresh);
        getSFileApi().projectQueryFileBoxByDir("Token " + authToken, seaFileRepoId, rootName).enqueue(new Callback<List<FileBoxBean>>() {
            @Override
            public void onResponse(Call<List<FileBoxBean>> call, Response<List<FileBoxBean>> response) {
                stopRefresh();
                if (response.body() != null) {
                    fileBoxBeanList = response.body();
                    enableEmptyView(fileBoxBeanList);
                    switch (sort_type) {
                        case 0:
                            projectFileBoxAdapter.bindData(isRefresh, fileBoxBeanList);
                            break;
                        case NAME_SORT_TYPE:
                            sortFileByNameList(sortIsUp);
                            break;
                        case TIME_SORT_TYPE:
                            sortFileByTimeList(sortIsUp);
                            break;
                        case SIZE_SORT_TYPE:
                            sortFileBySizeList(sortIsUp);
                            break;
                    }
                }
            }

            @Override
            public void onFailure(Call<List<FileBoxBean>> call, Throwable t) {
                stopRefresh();
                showTopSnackBar("获取文档列表失败");
            }
        });
    }

    private void stopRefresh() {
        if (refreshLayout != null) {
            refreshLayout.stopRefresh();
            refreshLayout.stopLoadMore();
        }
    }

    private void enableEmptyView(List result) {
        if (refreshLayout != null) {
            if (result != null) {
                if (result.size() > 0) {
                    refreshLayout.enableEmptyView(false);
                } else {
                    refreshLayout.enableEmptyView(true);
                }
            } else {
                refreshLayout.enableEmptyView(true);
            }
        }
    }

    @Override
    public void onItemClick(BaseRecyclerAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
        FileBoxBean fileBoxBean = (FileBoxBean) adapter.getItem(position);
        if (!TextUtils.isEmpty(fileBoxBean.type)) {
            if (TextUtils.equals("file", fileBoxBean.type)) {
                FileBoxDownloadActivity.launch(getContext(), authToken, seaFileRepoId, rootName + "/" + fileBoxBean.name, FileBoxDownloadActivity.PROJECT_DOWNLOAD_FILE_ACTION);
            }
            if (TextUtils.equals("dir", fileBoxBean.type)) {
                FileBoxListActivity.launch(this, fileBoxBean, authToken, seaFileRepoId, rootName + "/" + fileBoxBean.name);
            }
        }
    }

    /**
     * 按名称排序
     *
     * @param isUp
     */
    public void sortFileByNameList(final boolean isUp) {
        sort_type = NAME_SORT_TYPE;
        sortIsUp = isUp;
        if (fileBoxBeanList == null) return;
        if (fileBoxBeanList.size() <= 0) return;
        Collections.sort(fileBoxBeanList, new Comparator<FileBoxBean>() {
            @Override
            public int compare(FileBoxBean o1, FileBoxBean o2) {
                if (o1 == null || o2 == null) {
                    return 1;
                }
                if (TextUtils.isEmpty(o1.name) || TextUtils.isEmpty(o2.name)) {
                    return 1;
                }
                int o1Name, o2Name;
                if (PingYinUtil.isNumeric(String.valueOf(o1.name.charAt(0)))) {
                    o1Name = firstlist.indexOf(String.valueOf(o1.name.charAt(0)));
                } else {
                    o1Name = firstlist.indexOf(String.valueOf(PingYinUtil.getFirstUpperLetter(getContext(), PingYinUtil.getPingYin(getContext(), o1.name))).toLowerCase());
                }
                if (PingYinUtil.isNumeric(String.valueOf(o2.name.charAt(0)))) {
                    o2Name = firstlist.indexOf(String.valueOf(o2.name.charAt(0)));
                } else {
                    o2Name = firstlist.indexOf(String.valueOf(PingYinUtil.getFirstUpperLetter(getContext(), PingYinUtil.getPingYin(getContext(), o2.name))).toLowerCase());
                }

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
        sort_type = SIZE_SORT_TYPE;
        sortIsUp = isUp;
        if (fileBoxBeanList == null) return;
        if (fileBoxBeanList.size() <= 0) return;
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

    /**
     * 按修改时间排序
     *
     * @param isUp
     */
    public void sortFileByTimeList(final boolean isUp) {
        sort_type = TIME_SORT_TYPE;
        sortIsUp = isUp;
        if (fileBoxBeanList == null) return;
        if (fileBoxBeanList.size() <= 0) return;
        Collections.sort(fileBoxBeanList, new Comparator<FileBoxBean>() {
            @Override
            public int compare(FileBoxBean o1, FileBoxBean o2) {

                long o1Time = o1.mtime;
                long o2Time = o2.mtime;
                if (isUp) {
                    //按照修改时间进行降序排列
                    if (o1Time < o2Time) {
                        return 1;
                    }
                    if (o1Time == o2Time) {
                        return 0;
                    }
                    return -1;
                } else {
                    //按照修改时间进行升序排列
                    if (o1Time > o2Time) {
                        return 1;
                    }
                    if (o1Time == o2Time) {
                        return 0;
                    }
                    return -1;
                }
            }
        });
        projectFileBoxAdapter.bindData(true, fileBoxBeanList);
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

    /**
     * 显示文档更多菜单
     */
    private void showDocumentMeau() {
        new BottomActionDialog(getContext(),
                null,
                list,
                new BottomActionDialog.OnActionItemClickListener() {
                    @Override
                    public void onItemClick(BottomActionDialog dialog, BottomActionDialog.ActionItemAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
                        dialog.dismiss();
                        switch (position) {
                            case 0:
                                sortFileByNameList(nameIsUp);
                                nameIsUp = !nameIsUp;
                                timeIsUp = false;
                                sizeIsUp = false;
                                list.clear();
                                if (nameIsUp) {
                                    list.add("按文件名降序排序");
                                    list.add("按文件大小升序排序");
                                    list.add("按修改时间升序排序");
                                } else {
                                    list.add("按文件名升序排序");
                                    list.add("按文件大小升序排序");
                                    list.add("按修改时间升序排序");
                                }
                                break;
                            case 1:
                                sortFileBySizeList(sizeIsUp);
                                sizeIsUp = !sizeIsUp;
                                nameIsUp = false;
                                timeIsUp = false;
                                list.clear();
                                if (sizeIsUp) {
                                    list.add("按文件名升序排序");
                                    list.add("按文件大小降序排序");
                                    list.add("按修改时间升序排序");
                                } else {
                                    list.add("按文件名升序排序");
                                    list.add("按文件大小升序排序");
                                    list.add("按修改时间升序排序");
                                }

                                break;

                            case 2:
                                sortFileByTimeList(timeIsUp);
                                timeIsUp = !timeIsUp;
                                nameIsUp = false;
                                sizeIsUp = false;
                                list.clear();
                                if (timeIsUp) {
                                    list.add("按文件名升序排序");
                                    list.add("按文件大小升序排序");
                                    list.add("按修改时间降序排序");
                                } else {
                                    list.add("按文件名升序排序");
                                    list.add("按文件大小升序排序");
                                    list.add("按修改时间升序排序");
                                }
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
                    uploadFile(uploadUrl, filePath);
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
        params.put("parent_dir", RequestUtils.createTextBody("/" + rootName));
        params.put(key, RequestUtils.createImgBody(new File(filePath)));
        getSFileApi().sfileUploadFile("Token " + authToken, uploadUrl, params).enqueue(new Callback<JsonElement>() {
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
