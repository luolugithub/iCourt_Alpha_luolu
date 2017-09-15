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
import android.widget.TextView;

import com.andview.refreshview.XRefreshView;
import com.google.gson.JsonObject;
import com.icourt.alpha.R;
import com.icourt.alpha.activity.FileDownloadActivity;
import com.icourt.alpha.adapter.FileVersionAdapter;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.adapter.baseadapter.HeaderFooterAdapter;
import com.icourt.alpha.adapter.baseadapter.adapterObserver.DataChangeAdapterObserver;
import com.icourt.alpha.constants.SFileConfig;
import com.icourt.alpha.entity.bean.FileVersionCommits;
import com.icourt.alpha.entity.bean.FileVersionEntity;
import com.icourt.alpha.http.callback.SFileCallBack;
import com.icourt.alpha.interfaces.OnFragmentDataChangeListener;
import com.icourt.alpha.utils.IMUtils;
import com.icourt.alpha.view.xrefreshlayout.RefreshLayout;
import com.icourt.alpha.widget.comparators.LongFieldEntityComparator;
import com.icourt.alpha.widget.comparators.ORDER;
import com.icourt.alpha.widget.dialog.BottomActionDialog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.HttpException;
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

        final TextView emptyView = (TextView) HeaderFooterAdapter.inflaterView(getContext(), R.layout.footer_folder_document_num, recyclerView);
        emptyView.setText("");
        refreshLayout.setEmptyView(emptyView);


        recyclerView.setAdapter(fileVersionAdapter = new FileVersionAdapter(TextUtils.equals(getRepoPermission(), PERMISSION_RW)));
        fileVersionAdapter.registerAdapterDataObserver(new DataChangeAdapterObserver() {
            @Override
            protected void updateUI() {
                if (refreshLayout != null) {
                    refreshLayout.enableEmptyView(fileVersionAdapter.getItemCount() <= 0);
                    emptyView.setText(R.string.sfile_version_list_empty);
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
        callEnqueue(getSFileApi().fileVersionQuery(fromRepoId, fromRepoFilePath),
                new SFileCallBack<FileVersionCommits>() {
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
                            //通知数据更新

                            if (onFragmentDataChangeListener != null) {
                                onFragmentDataChangeListener.onFragmentDataChanged(
                                        FileVersionListFragment.this,
                                        0,
                                        response.body().commits);
                            }
                            //图片不加载历史版本
                            if (!IMUtils.isPIC(fromRepoFilePath)) {
                                //填充布局  最新版本不计入历史版本
                                List<FileVersionEntity> dispFileVersionEntities = new ArrayList<FileVersionEntity>();
                                if (response.body().commits.size() > 1) {
                                    dispFileVersionEntities.addAll(
                                            response.body().commits.subList(1, response.body().commits.size()));
                                }
                                fileVersionAdapter.bindData(isRefresh, dispFileVersionEntities);
                            }
                        }
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
                showRestoreConfirmDialog(item);
                break;
        }
    }

    /**
     * 回滚确认对话框
     *
     * @param item
     */
    private void showRestoreConfirmDialog(final FileVersionEntity item) {
        if (item == null) return;
        new BottomActionDialog(getContext(),
                null,
                Arrays.asList(getString(R.string.sfile_backspace_confirm)),
                new BottomActionDialog.OnActionItemClickListener() {
                    @Override
                    public void onItemClick(BottomActionDialog dialog, BottomActionDialog.ActionItemAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
                        dialog.dismiss();
                        restoreFile(item);
                    }
                }).show();

    }

    /**
     * 回滚版本
     *
     * @param item
     */
    private void restoreFile(FileVersionEntity item) {
        if (item == null) return;
        showLoadingDialog(R.string.sfile_backspacing);
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
                            showToast(R.string.sfile_backspace_success);
                            getData(true);
                        } else {
                            showToast(R.string.sfile_backspace_fail);
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        dismissLoadingDialog();
                        super.onFailure(call, t);
                        if (t instanceof HttpException
                                && ((HttpException) t).code() == 500) {
                            showToast("文件可能被移除或者重命名无法回退!");
                        }
                    }

                    @Override
                    public void defNotify(String noticeStr) {
                        // super.defNotify(noticeStr);
                        showToast(noticeStr);
                    }
                });
    }
}
