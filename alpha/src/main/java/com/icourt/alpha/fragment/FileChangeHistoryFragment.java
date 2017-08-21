package com.icourt.alpha.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.andview.refreshview.XRefreshView;
import com.google.gson.JsonObject;
import com.icourt.alpha.BuildConfig;
import com.icourt.alpha.R;
import com.icourt.alpha.adapter.FileChangedHistoryAdapter;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.adapter.baseadapter.adapterObserver.DataChangeAdapterObserver;
import com.icourt.alpha.base.BaseDialogFragment;
import com.icourt.alpha.entity.bean.FileChangedHistoryEntity;
import com.icourt.alpha.entity.bean.RepoMatterEntity;
import com.icourt.alpha.http.callback.SFileCallBack;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.callback.SimpleCallBack2;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.interfaces.OnFragmentDataChangeListener;
import com.icourt.alpha.utils.JsonUtils;
import com.icourt.alpha.view.xrefreshlayout.RefreshLayout;
import com.icourt.alpha.widget.dialog.BottomActionDialog;

import java.util.Arrays;
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
    OnFragmentDataChangeListener onFragmentDataChangeListener;

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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (getParentFragment() instanceof OnFragmentDataChangeListener) {
            onFragmentDataChangeListener = (OnFragmentDataChangeListener) getParentFragment();
        } else {
            try {
                onFragmentDataChangeListener = (OnFragmentDataChangeListener) context;
            } catch (ClassCastException e) {
                e.printStackTrace();
            }
        }
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
        fileChangedHistoryAdapter.registerAdapterDataObserver(new DataChangeAdapterObserver() {
            @Override
            protected void updateUI() {
                if (onFragmentDataChangeListener != null) {
                    onFragmentDataChangeListener.onFragmentDataChanged(
                            FileChangeHistoryFragment.this,
                            0,
                            fileChangedHistoryAdapter.getData());
                }
            }
        });
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
                showRevokeConfirmDialog(position);
                break;
        }
    }

    /**
     * 展示撤销对话框
     *
     * @param position
     */
    private void showRevokeConfirmDialog(int position) {
        final FileChangedHistoryEntity item = fileChangedHistoryAdapter.getItem(position);
        if (item == null) return;
        new BottomActionDialog(
                getContext(),
                "将资料库恢复到该次修改前到状态",
                Arrays.asList("撤销修改"),
                new BottomActionDialog.OnActionItemClickListener() {
                    @Override
                    public void onItemClick(BottomActionDialog dialog, BottomActionDialog.ActionItemAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
                        dialog.dismiss();
                        revokeFileChange(item);
                    }
                })
                .show();
    }

    /**
     * 撤销文件的修改
     *
     * @param item
     */
    private void revokeFileChange(FileChangedHistoryEntity item) {
        if (item == null) return;
        String path = String.format("%s%s", getArguments().getString(KEY_SEA_FILE_FROM_DIR_PATH, ""), item.file_name);
        showLoadingDialog(null);
        getSFileApi().fileChangeRevoke(
                getArguments().getString(KEY_SEA_FILE_FROM_REPO_ID, ""),
                path)
                .enqueue(new SFileCallBack<JsonObject>() {
                    @Override
                    public void onSuccess(Call<JsonObject> call, Response<JsonObject> response) {
                        dismissLoadingDialog();
                        if (JsonUtils.getBoolValue(response.body(), "success")) {
                            getData(true);
                        } else {
                            showToast("撤销失败");
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        dismissLoadingDialog();
                        super.onFailure(call, t);
                    }
                });
    }
}
