package com.icourt.alpha.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.JsonElement;
import com.icourt.alpha.R;
import com.icourt.alpha.adapter.ProjectFileBoxAdapter;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.adapter.baseadapter.adapterObserver.RefreshViewEmptyObserver;
import com.icourt.alpha.base.BaseActivity;
import com.icourt.alpha.entity.bean.FileBoxBean;
import com.icourt.alpha.entity.bean.RepoIdResEntity;
import com.icourt.alpha.http.callback.SFileCallBack;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.callback.SimpleCallBack2;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.utils.ItemDecorationUtils;
import com.icourt.alpha.view.smartrefreshlayout.EmptyRecyclerView;
import com.icourt.api.RequestUtils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadmoreListener;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Description  选择文件夹
 * Company Beijing icourt
 * author  lu.zhao  E-mail:zhaolu@icourt.cc
 * date createTime：17/5/16
 * version 2.0.0
 */

public class FolderboxSelectActivity extends BaseActivity implements BaseRecyclerAdapter.OnItemClickListener {

    @BindView(R.id.titleBack)
    ImageView titleBack;
    @BindView(R.id.titleContent)
    TextView titleContent;
    @BindView(R.id.titleAction)
    CheckedTextView titleAction;
    @BindView(R.id.titleView)
    AppBarLayout titleView;
    @BindView(R.id.recyclerView)
    EmptyRecyclerView recyclerView;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    ProjectFileBoxAdapter projectFileBoxAdapter;
    String projectId, seaFileRepoId, filePath, rootName;
    boolean isCanlookAddDocument;

    public static void launch(@NonNull Context context,
                              @NonNull String projectId,
                              @NonNull String seaFileRepoId,
                              @NonNull String filePath,
                              @NonNull String rootName) {
        if (context == null) return;
        Intent intent = new Intent(context, FolderboxSelectActivity.class);
        intent.putExtra("projectId", projectId);
        intent.putExtra("seaFileRepoId", seaFileRepoId);
        intent.putExtra("filePath", filePath);
        intent.putExtra("rootName", rootName);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folder_box_select_layout);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void initView() {
        super.initView();
        setTitle("选择文件夹");
        projectId = getIntent().getStringExtra("projectId");
        seaFileRepoId = getIntent().getStringExtra("seaFileRepoId");
        filePath = getIntent().getStringExtra("filePath");
        rootName = getIntent().getStringExtra("rootName");
        recyclerView.setNoticeEmpty(R.mipmap.icon_placeholder_project, "暂无文件夹");
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(ItemDecorationUtils.getCommFull05Divider(getContext(), true));

        recyclerView.setAdapter(projectFileBoxAdapter = new ProjectFileBoxAdapter());
        projectFileBoxAdapter.setOnItemClickListener(this);
        projectFileBoxAdapter.registerAdapterDataObserver(new RefreshViewEmptyObserver(recyclerView, projectFileBoxAdapter));

        refreshLayout.setOnRefreshLoadmoreListener(new OnRefreshLoadmoreListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                if (TextUtils.isEmpty(seaFileRepoId)) {
                    getDocumentId();
                } else {
                    getData(true);
                }
            }

            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                if (TextUtils.isEmpty(seaFileRepoId)) {
                    getDocumentId();
                } else {
                    getData(true);
                }
            }
        });

        checkAddTaskAndDocumentPms();

    }

    @OnClick({R.id.titleAction})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.titleAction:
                getUploadUrl(filePath);
                break;
            default:
                super.onClick(v);
                break;
        }
    }

    /**
     * 获取项目权限
     */
    private void checkAddTaskAndDocumentPms() {
        callEnqueue(getApi().permissionQuery(getLoginUserId(), "MAT", projectId),
                new SimpleCallBack<List<String>>() {
                    @Override
                    public void onSuccess(Call<ResEntity<List<String>>> call, Response<ResEntity<List<String>>> response) {

                        if (response.body().result != null) {
                            if (response.body().result.contains("MAT:matter.document:readwrite")) {
                                isCanlookAddDocument = true;
                                titleAction.setVisibility(View.VISIBLE);
                                getData(true);
                            } else {
                                titleAction.setVisibility(View.INVISIBLE);
                            }
                        } else {
                            titleAction.setVisibility(View.INVISIBLE);
                        }
                    }

                    @Override
                    public void onFailure(Call<ResEntity<List<String>>> call, Throwable t) {
                        super.onFailure(call, t);
                    }
                });
    }

    @Override
    protected void getData(final boolean isRefresh) {
        super.getData(isRefresh);
        callEnqueue(getSFileApi().projectQueryFileBoxByDir(seaFileRepoId, rootName),
                new SFileCallBack<List<FileBoxBean>>() {
                    @Override
                    public void onSuccess(Call<List<FileBoxBean>> call, Response<List<FileBoxBean>> response) {
                        stopRefresh();
                        if (response.body() != null) {
                            projectFileBoxAdapter.bindData(isRefresh, getFolders(response.body()));
                        }
                    }

                    @Override
                    public void onFailure(Call<List<FileBoxBean>> call, Throwable t) {
                        super.onFailure(call, t);
                        stopRefresh();
                        showTopSnackBar("获取文档列表失败");
                    }
                });
    }

    /**
     * 获取根目录id
     */
    private void getDocumentId() {
        callEnqueue(getApi().projectQueryDocumentId(projectId),
                new SimpleCallBack2<RepoIdResEntity>() {
                    @Override
                    public void onSuccess(Call<RepoIdResEntity> call, Response<RepoIdResEntity> response) {
                        if (!TextUtils.isEmpty(response.body().seaFileRepoId)) {
                            seaFileRepoId = response.body().seaFileRepoId;
                            getData(true);
                        } else {
                            bugSync("项目repo 获取null", "projectid:" + projectId);
                            showTopSnackBar("seaFileRepoId 返回null");
                        }
                    }

                    @Override
                    public void onFailure(Call<RepoIdResEntity> call, Throwable t) {
                        super.onFailure(call, t);
                    }
                });
    }

    private List<FileBoxBean> getFolders(List<FileBoxBean> fileBoxBeens) {
        Iterator<FileBoxBean> it = fileBoxBeens.iterator();
        while (it.hasNext()) {
            if (TextUtils.equals("file", it.next().type)) {
                it.remove();
            }
        }
        return fileBoxBeens;
    }

    private void stopRefresh() {
        if (refreshLayout != null) {
            refreshLayout.finishLoadmore();
            refreshLayout.finishRefresh();
        }
    }

    @Override
    public void onItemClick(BaseRecyclerAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
        FileBoxBean fileBoxBean = (FileBoxBean) adapter.getItem(position);
        if (!TextUtils.isEmpty(fileBoxBean.type)) {
            if (TextUtils.equals("dir", fileBoxBean.type)) {
                if (TextUtils.isEmpty(rootName)) {
                    FolderboxSelectActivity.launch(this,
                            projectId,
                            seaFileRepoId,
                            filePath,
                            "/" + fileBoxBean.name);
                } else {
                    FolderboxSelectActivity.launch(this,
                            projectId,
                            seaFileRepoId,
                            filePath,
                            rootName + "/" + fileBoxBean.name);
                }
            }
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
        callEnqueue(getSFileApi().projectUploadUrlQuery(seaFileRepoId),
                new SFileCallBack<JsonElement>() {
                    @Override
                    public void onSuccess(Call<JsonElement> call, Response<JsonElement> response) {
                        if (response.body() != null) {
                            String uploadUrl = response.body().getAsString();
                            uploadFile(uploadUrl, filePath);
                        } else {
                            dismissLoadingDialog();
                            showTopSnackBar("上传失败");
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonElement> call, Throwable t) {
                        super.onFailure(call, t);
                        dismissLoadingDialog();
                        showTopSnackBar("获取上传文件地址失败");
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
        if (TextUtils.isEmpty(filePath)) return;
        File file = new File(filePath);
        String fileName = file.getName();
        String key = String.format("file\";filename=\"%s", fileName);
        Map<String, RequestBody> params = new HashMap<>();
        params.put("parent_dir", TextUtils.isEmpty(rootName) ? RequestUtils.createTextBody("/") : RequestUtils.createTextBody(rootName));
        params.put(key, RequestUtils.createStreamBody(file));
        callEnqueue(getSFileApi().sfileUploadFile(uploadUrl, params),
                new SFileCallBack<JsonElement>() {
                    @Override
                    public void onSuccess(Call<JsonElement> call, Response<JsonElement> response) {
                        dismissLoadingDialog();
                        showTopSnackBar("上传成功");
                        ProjectSelectActivity.lauchClose(FolderboxSelectActivity.this);
                        ImportFile2AlphaActivity.lauchClose(FolderboxSelectActivity.this);
                        finish();
                    }

                    @Override
                    public void onFailure(Call<JsonElement> call, Throwable t) {
                        super.onFailure(call, t);
                        dismissLoadingDialog();
                        showTopSnackBar("文件上传失败");
                    }
                });
    }

}
