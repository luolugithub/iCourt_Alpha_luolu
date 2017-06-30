package com.icourt.alpha.fragment;

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
import com.icourt.alpha.adapter.ProjectFileBoxAdapter;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.adapter.baseadapter.adapterObserver.RefreshViewEmptyObserver;
import com.icourt.alpha.base.BaseFragment;
import com.icourt.alpha.entity.bean.FileBoxBean;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.utils.ItemDecorationUtils;
import com.icourt.alpha.view.xrefreshlayout.RefreshLayout;

import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Description   文件夹列表
 * Company Beijing icourt
 * author  lu.zhao  E-mail:zhaolu@icourt.cc
 * date createTime：17/6/30
 * version 2.0.0
 */

public class FileDirListFragment extends BaseFragment implements BaseRecyclerAdapter.OnItemClickListener {
    Unbinder unbinder;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.refreshLayout)
    RefreshLayout refreshLayout;
    ProjectFileBoxAdapter projectFileBoxAdapter;
    String projectId, authToken, seaFileRepoId, filePath, rootName;
    boolean isCanlookAddDocument;

    public static FileDirListFragment newInstance(@NonNull String projectId, @NonNull String authToken, @NonNull String seaFileRepoId, @NonNull String filePath, @NonNull String rootName) {
        FileDirListFragment fileDirListFragment = new FileDirListFragment();
        Bundle args = new Bundle();
        args.putString("projectId", projectId);
        args.putString("authToken", authToken);
        args.putString("seaFileRepoId", seaFileRepoId);
        args.putString("filePath", filePath);
        args.putString("rootName", rootName);
        fileDirListFragment.setArguments(args);
        return fileDirListFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(R.layout.fragment_filedir_layout, inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void initView() {
        projectId = getArguments().getString("projectId");
        seaFileRepoId = getArguments().getString("seaFileRepoId");
        filePath = getArguments().getString("filePath");
        authToken = getArguments().getString("authToken");
        rootName = getArguments().getString("rootName");
        refreshLayout.setNoticeEmpty(R.mipmap.icon_placeholder_project, "暂无文件夹");
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
                if (TextUtils.isEmpty(seaFileRepoId)) {
                    getDocumentId();
                } else {
                    getData(true);
                }
            }

            @Override
            public void onLoadMore(boolean isSilence) {
                super.onLoadMore(isSilence);
                if (TextUtils.isEmpty(seaFileRepoId)) {
                    getDocumentId();
                } else {
                    getData(true);
                }
            }
        });

        checkAddTaskAndDocumentPms();
    }

    /**
     * 获取项目权限
     */
    private void checkAddTaskAndDocumentPms() {
        getApi().permissionQuery(getLoginUserId(), "MAT", projectId).enqueue(new SimpleCallBack<List<String>>() {
            @Override
            public void onSuccess(Call<ResEntity<List<String>>> call, Response<ResEntity<List<String>>> response) {

                if (response.body().result != null) {
                    if (response.body().result.contains("MAT:matter.document:readwrite")) {
                        isCanlookAddDocument = true;
                        // TODO: 17/6/30  如果有权限，则有保存按钮
//                        titleAction.setVisibility(View.VISIBLE);
                        refreshLayout.startRefresh();
                    } else {
                        // TODO: 17/6/30  如果没有权限，则没有保存按钮
//                        titleAction.setVisibility(View.INVISIBLE);
                    }
                } else {
                    // TODO: 17/6/30  如果没有权限，则没有保存按钮
//                    titleAction.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onFailure(Call<ResEntity<List<String>>> call, Throwable t) {
                super.onFailure(call, t);
            }
        });
    }

    /**
     * 过滤掉文件，只保留文件夹
     *
     * @param fileBoxBeens
     * @return
     */
    private List<FileBoxBean> getFolders(List<FileBoxBean> fileBoxBeens) {
        Iterator<FileBoxBean> it = fileBoxBeens.iterator();
        while (it.hasNext()) {
            if (TextUtils.equals("file", it.next().type)) {
                it.remove();
            }
        }
        return fileBoxBeens;
    }

    @Override
    protected void getData(final boolean isRefresh) {
        super.getData(isRefresh);
        getSFileApi().projectQueryFileBoxByDir("Token " + authToken, seaFileRepoId, rootName).enqueue(new Callback<List<FileBoxBean>>() {
            @Override
            public void onResponse(Call<List<FileBoxBean>> call, Response<List<FileBoxBean>> response) {
                stopRefresh();
                if (response.body() != null) {
                    projectFileBoxAdapter.bindData(isRefresh, getFolders(response.body()));
                }
            }

            @Override
            public void onFailure(Call<List<FileBoxBean>> call, Throwable t) {
                stopRefresh();
                showTopSnackBar("获取文档列表失败");
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
                                getData(true);
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
    public void onItemClick(BaseRecyclerAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {

    }
}
