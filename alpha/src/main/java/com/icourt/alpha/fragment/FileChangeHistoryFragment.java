package com.icourt.alpha.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.andview.refreshview.XRefreshView;
import com.icourt.alpha.BuildConfig;
import com.icourt.alpha.R;
import com.icourt.alpha.adapter.FileChangedHistoryAdapter;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.base.BaseDialogFragment;
import com.icourt.alpha.entity.bean.FileChangedHistoryEntity;
import com.icourt.alpha.entity.bean.RepoMatterEntity;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.callback.SimpleCallBack2;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.view.xrefreshlayout.RefreshLayout;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/8/17
 * version 2.1.0
 */
public class FileChangeHistoryFragment extends BaseDialogFragment implements BaseRecyclerAdapter.OnItemChildClickListener {
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.refreshLayout)
    RefreshLayout refreshLayout;
    Unbinder unbinder;
    FileChangedHistoryAdapter fileChangedHistoryAdapter;
    int page = 0;

    protected static final String KEY_SEA_FILE_FROM_REPO_ID = "seaFileFromRepoId";//原仓库id
    protected static final String KEY_SEA_FILE_FROM_DIR_PATH = "seaFileFromDirPath";//原仓库路径

    public static FileChangeHistoryFragment newInstance(
            String fromRepoId,
            String fromRepoDirPath) {
        FileChangeHistoryFragment fragment = new FileChangeHistoryFragment();
        Bundle args = new Bundle();
        args.putString(KEY_SEA_FILE_FROM_REPO_ID, fromRepoId);
        args.putString(KEY_SEA_FILE_FROM_DIR_PATH, fromRepoDirPath);
        fragment.setArguments(args);
        return fragment;
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
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(fileChangedHistoryAdapter = new FileChangedHistoryAdapter());
        fileChangedHistoryAdapter.setOnItemChildClickListener(this);
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
    protected void getData(final boolean isRefresh) {
        super.getData(isRefresh);
        if (isRefresh) {
            page = 0;
        }
        getApi().repoMatterIdQuery(getArguments().getString(KEY_SEA_FILE_FROM_REPO_ID, ""))
                .enqueue(new SimpleCallBack2<RepoMatterEntity>() {

                    @Override
                    public void onSuccess(Call<RepoMatterEntity> call, Response<RepoMatterEntity> response) {
                        getApi().folderChangeHistory(
                                BuildConfig.API_URL.replace("ilaw/", "").concat("inotice/api/notices/docs/matters"),
                                response.body().matterId,
                                page)
                                .enqueue(new SimpleCallBack<List<FileChangedHistoryEntity>>() {

                                    @Override
                                    protected void dispatchHttpSuccess(Call<ResEntity<List<FileChangedHistoryEntity>>> call, Response<ResEntity<List<FileChangedHistoryEntity>>> response) {
                                        if (response.body() != null) {
                                            onSuccess(call, response);
                                        } else {
                                            super.dispatchHttpSuccess(call, response);
                                        }
                                    }

                                    @Override
                                    public void onSuccess(Call<ResEntity<List<FileChangedHistoryEntity>>> call, Response<ResEntity<List<FileChangedHistoryEntity>>> response) {
                                        page += 1;
                                        fileChangedHistoryAdapter.bindData(isRefresh, response.body().result);
                                        stopRefresh();
                                    }

                                    @Override
                                    public void onFailure(Call<ResEntity<List<FileChangedHistoryEntity>>> call, Throwable t) {
                                        super.onFailure(call, t);
                                        stopRefresh();
                                    }
                                });
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
    public void onItemChildClick(BaseRecyclerAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
        switch (view.getId()) {
            case R.id.file_restore_iv:
                break;
        }
    }
}
