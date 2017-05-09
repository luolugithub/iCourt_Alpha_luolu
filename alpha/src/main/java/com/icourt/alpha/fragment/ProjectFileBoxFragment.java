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
import com.icourt.alpha.adapter.baseadapter.adapterObserver.RefreshViewEmptyObserver;
import com.icourt.alpha.base.BaseFragment;
import com.icourt.alpha.entity.bean.FileBoxBean;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.utils.ItemDecorationUtils;
import com.icourt.alpha.view.xrefreshlayout.RefreshLayout;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
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

public class ProjectFileBoxFragment extends BaseFragment {

    private static final String KEY_PROJECT_ID = "key_project_id";
    Unbinder unbinder;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.refreshLayout)
    RefreshLayout refreshLayout;

    ProjectFileBoxAdapter projectFileBoxAdapter;
    String projectId;
    String authToken;//文档仓库token
    String seaFileRepoId;//文档根目录id

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
        refreshLayout.setNoticeEmpty(R.mipmap.icon_placeholder_project, R.string.null_project);
        refreshLayout.setMoveForHorizontal(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(ItemDecorationUtils.getCommTrans5Divider(getContext(), true));
        recyclerView.setHasFixedSize(true);

        recyclerView.setAdapter(projectFileBoxAdapter = new ProjectFileBoxAdapter());
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
            getApi().projectQueryFileBoxList("Token " + authToken, seaFileRepoId).enqueue(new SimpleCallBack<List<FileBoxBean>>() {
                @Override
                public void onSuccess(Call<ResEntity<List<FileBoxBean>>> call, Response<ResEntity<List<FileBoxBean>>> response) {
                    stopRefresh();
                    if (response.body().result != null) {
                        projectFileBoxAdapter.bindData(isRefresh, response.body().result);
                    }
                }

                @Override
                public void onFailure(Call<ResEntity<List<FileBoxBean>>> call, Throwable t) {
                    super.onFailure(call, t);
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

}
