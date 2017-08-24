package com.icourt.alpha.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.andview.refreshview.XRefreshView;
import com.google.gson.JsonObject;
import com.icourt.alpha.R;
import com.icourt.alpha.activity.FileDownloadActivity;
import com.icourt.alpha.adapter.FileVersionAdapter;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.adapter.baseadapter.adapterObserver.DataChangeAdapterObserver;
import com.icourt.alpha.constants.SFileConfig;
import com.icourt.alpha.entity.bean.FileVersionCommits;
import com.icourt.alpha.entity.bean.FileVersionEntity;
import com.icourt.alpha.http.callback.SFileCallBack;
import com.icourt.alpha.interfaces.OnFragmentDataChangeListener;
import com.icourt.alpha.view.xrefreshlayout.RefreshLayout;
import com.icourt.alpha.widget.comparators.LongFieldEntityComparator;
import com.icourt.alpha.widget.comparators.ORDER;

import java.util.Collections;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Response;

import static com.icourt.alpha.constants.SFileConfig.PERMISSION_RW;

/**
 * Description  文件版本历史
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/8/15
 * version 2.1.0
 */
public class FileVersionListFragment extends SeaFileBaseFragment implements BaseRecyclerAdapter.OnItemClickListener, BaseRecyclerAdapter.OnItemChildClickListener {

    protected static final String KEY_SEA_FILE_FROM_REPO_ID = "seaFileFromRepoId";//原仓库id
    protected static final String KEY_SEA_FILE_FROM_FILE_PATH = "seaFileFromFilePath";//原文件路径
    protected static final String KEY_SEA_FILE_REPO_PERMISSION = "seaFileRepoPermission";//repo的权限

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.refreshLayout)
    RefreshLayout refreshLayout;
    Unbinder unbinder;
    String fromRepoId, fromRepoFilePath;
    FileVersionAdapter fileVersionAdapter;
    OnFragmentDataChangeListener onFragmentDataChangeListener;

    /**
     * @param fromRepoId
     * @param fromRepoFilePath
     * @return
     */
    public static FileVersionListFragment newInstance(
            String fromRepoId,
            String fromRepoFilePath,
            @SFileConfig.FILE_PERMISSION String repoPermission) {
        FileVersionListFragment fragment = new FileVersionListFragment();
        Bundle args = new Bundle();
        args.putString(KEY_SEA_FILE_FROM_REPO_ID, fromRepoId);
        args.putString(KEY_SEA_FILE_FROM_FILE_PATH, fromRepoFilePath);
        args.putString(KEY_SEA_FILE_REPO_PERMISSION, repoPermission);
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

    /**
     * repo 的权限
     *
     * @return
     */
    @SFileConfig.FILE_PERMISSION
    protected String getRepoPermission() {
        String stringPermission = getArguments().getString(KEY_SEA_FILE_REPO_PERMISSION, "");
        return SFileConfig.convert2filePermission(stringPermission);
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
        fromRepoId = getArguments().getString(KEY_SEA_FILE_FROM_REPO_ID, "");
        fromRepoFilePath = getArguments().getString(KEY_SEA_FILE_FROM_FILE_PATH, "");
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(fileVersionAdapter = new FileVersionAdapter(TextUtils.equals(getRepoPermission(), PERMISSION_RW)));
        fileVersionAdapter.registerAdapterDataObserver(new DataChangeAdapterObserver() {
            @Override
            protected void updateUI() {
                if (onFragmentDataChangeListener != null) {
                    onFragmentDataChangeListener.onFragmentDataChanged(
                            FileVersionListFragment.this,
                            0,
                            fileVersionAdapter.getData());
                }
            }
        });
        fileVersionAdapter.setOnItemClickListener(this);
        fileVersionAdapter.setOnItemChildClickListener(this);
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
        getSFileApi().fileVersionQuery(fromRepoId, fromRepoFilePath)
                .enqueue(new SFileCallBack<FileVersionCommits>() {
                    @Override
                    public void onSuccess(Call<FileVersionCommits> call, Response<FileVersionCommits> response) {
                        if (response.body().commits != null) {
                            try {
                                Collections.sort(response.body().commits, new LongFieldEntityComparator<FileVersionEntity>(ORDER.DESC));
                            } catch (Throwable e) {
                                bugSync("排序异常", e);
                                e.printStackTrace();
                            }
                            //服务器返回的version 不靠谱
                            int size = response.body().commits.size();
                            for (int i = 0; i < size; i++) {
                                FileVersionEntity fileVersionEntity = response.body().commits.get(i);
                                if (fileVersionEntity == null) continue;
                                fileVersionEntity.version = size - i;
                            }
                        }
                        fileVersionAdapter.bindData(isRefresh, response.body().commits);
                        stopRefresh();
                    }

                    @Override
                    public void onFailure(Call<FileVersionCommits> call, Throwable t) {
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


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private String getFileName() {
        String string = fromRepoFilePath;
        if (!TextUtils.isEmpty(string)) {
            String[] split = string.split("/");
            if (split != null && split.length > 0) {
                return split[split.length - 1];
            }
        }
        return string;
    }

    @Override
    public void onItemClick(BaseRecyclerAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
        FileVersionEntity item = fileVersionAdapter.getItem(position);
        if (item == null) return;
        FileDownloadActivity.launch(
                getContext(),
                item.repo_id,
                getFileName(),
                item.rev_file_size,
                fromRepoFilePath,
                item.id);
    }

    @Override
    public void onItemChildClick(BaseRecyclerAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
        FileVersionEntity item = fileVersionAdapter.getItem(position);
        if (item == null) return;
        switch (view.getId()) {
            case R.id.file_restore_iv:
                showLoadingDialog("回退中...");
                getSFileApi().fileRetroversion(
                        getArguments().getString(KEY_SEA_FILE_FROM_REPO_ID),
                        getArguments().getString(KEY_SEA_FILE_FROM_FILE_PATH),
                        item.id,
                        "revert")
                        .enqueue(new SFileCallBack<JsonObject>() {
                            @Override
                            public void onSuccess(Call<JsonObject> call, Response<JsonObject> response) {
                                dismissLoadingDialog();
                                if (response.body().has("success")
                                        && response.body().get("success").getAsBoolean()) {
                                    showToast("回退成功");
                                    getData(true);
                                } else {
                                    showToast("回退失败");
                                }
                            }

                            @Override
                            public void onFailure(Call<JsonObject> call, Throwable t) {
                                dismissLoadingDialog();
                                super.onFailure(call, t);
                            }
                        });
                break;
        }
    }
}
