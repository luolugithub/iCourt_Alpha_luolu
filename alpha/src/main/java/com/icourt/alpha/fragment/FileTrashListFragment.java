package com.icourt.alpha.fragment;

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
import com.icourt.alpha.adapter.SFileTrashAdapter;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.adapter.baseadapter.HeaderFooterAdapter;
import com.icourt.alpha.adapter.baseadapter.adapterObserver.DataChangeAdapterObserver;
import com.icourt.alpha.constants.SFileConfig;
import com.icourt.alpha.entity.bean.FolderDocumentEntity;
import com.icourt.alpha.entity.bean.SeaFileTrashPageEntity;
import com.icourt.alpha.http.callback.SFileCallBack;
import com.icourt.alpha.utils.ActionConstants;
import com.icourt.alpha.utils.JsonUtils;
import com.icourt.alpha.view.xrefreshlayout.RefreshLayout;
import com.icourt.alpha.widget.dialog.BottomActionDialog;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Response;

import static com.icourt.alpha.constants.SFileConfig.PERMISSION_RW;

/**
 * Description  文件回收站
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/8/19
 * version 2.1.0
 */
public class FileTrashListFragment extends SeaFileBaseFragment
        implements BaseRecyclerAdapter.OnItemClickListener,
        BaseRecyclerAdapter.OnItemChildClickListener {

    @BindView(R.id.recyclerView)
    @Nullable
    RecyclerView recyclerView;
    @BindView(R.id.refreshLayout)
    RefreshLayout refreshLayout;
    Unbinder unbinder;
    protected static final String KEY_SEA_FILE_REPO_ID = "seaFileRepoId";//仓库id
    protected static final String KEY_SEA_FILE_DIR_PATH = "seaFileDirPath";//目录路径
    protected static final String KEY_SEA_FILE_REPO_PERMISSION = "seaFileRepoPermission";//repo的权限
    SFileTrashAdapter folderDocumentAdapter;
    TextView emptyView;
    String scanStat;

    public static FileTrashListFragment newInstance(
            String fromRepoId,
            String fromRepoDirPath,
            @SFileConfig.FILE_PERMISSION String repoPermission) {
        FileTrashListFragment fragment = new FileTrashListFragment();
        Bundle args = new Bundle();
        args.putString(KEY_SEA_FILE_REPO_ID, fromRepoId);
        args.putString(KEY_SEA_FILE_DIR_PATH, fromRepoDirPath);
        args.putString(KEY_SEA_FILE_REPO_PERMISSION, repoPermission);
        fragment.setArguments(args);
        return fragment;
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
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(folderDocumentAdapter = new SFileTrashAdapter(
                false,
                TextUtils.equals(getRepoPermission(), PERMISSION_RW)));
        emptyView = (TextView) HeaderFooterAdapter.inflaterView(getContext(), R.layout.footer_folder_document_num, recyclerView);
        emptyView.setText(R.string.sfile_recycle_bin_empty);
        refreshLayout.setEmptyView(emptyView);
        folderDocumentAdapter.registerAdapterDataObserver(new DataChangeAdapterObserver() {
            @Override
            protected void updateUI() {
                if (refreshLayout != null) {
                    refreshLayout.enableEmptyView(folderDocumentAdapter.getItemCount() <= 0);
                }
            }
        });

        folderDocumentAdapter.setOnItemClickListener(this);
        folderDocumentAdapter.setOnItemChildClickListener(this);
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
        refreshLayout.startRefresh();
    }


    protected String getSeaFileRepoId() {
        return getArguments().getString(KEY_SEA_FILE_REPO_ID, "");
    }

    protected String getSeaFileDirPath() {
        return getArguments().getString(KEY_SEA_FILE_DIR_PATH, "");
    }

    @Override
    protected void getData(final boolean isRefresh) {
        super.getData(isRefresh);
        if (isRefresh) {
            scanStat = null;
        }
        callEnqueue(getSFileApi().folderTrashQuery(
                getSeaFileRepoId(),
                getSeaFileDirPath(),
                ActionConstants.DEFAULT_PAGE_SIZE,
                scanStat),
                new SFileCallBack<SeaFileTrashPageEntity<FolderDocumentEntity>>() {
                    @Override
                    public void onSuccess(Call<SeaFileTrashPageEntity<FolderDocumentEntity>> call, Response<SeaFileTrashPageEntity<FolderDocumentEntity>> response) {
                        scanStat = response.body().scan_stat;
                        folderDocumentAdapter.bindData(isRefresh, wrapData(getSeaFileRepoId(),getSeaFileDirPath(),response.body().data));
                        stopRefresh();
                        if (refreshLayout != null) {
                            refreshLayout.setPullLoadEnable(response.body().more);
                        }
                    }

                    @Override
                    public void onFailure(Call<SeaFileTrashPageEntity<FolderDocumentEntity>> call, Throwable t) {
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

    @Override
    public void onItemClick(BaseRecyclerAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
        final FolderDocumentEntity item = folderDocumentAdapter.getItem(position);
        if (item == null) return;
        if (item.isDir()) {
            showToast(R.string.sfile_recycle_bin_folder_not_clickable);
        } else {
            showToast(R.string.sfile_recycle_bin_file_not_clickable);
        }
    }

    /**
     * 文件恢复
     *
     * @param position
     */
    private void fileRevert(int position) {
        final FolderDocumentEntity item = folderDocumentAdapter.getItem(position);
        if (item == null) return;
        Call<JsonObject> jsonObjectCall;
        if (item.isDir()) {
            jsonObjectCall = getSFileApi().folderRevert(
                    getSeaFileRepoId(),
                    String.format("%s%s", item.parent_dir, item.name),
                    item.commit_id);
        } else {
            jsonObjectCall = getSFileApi().fileRevert(
                    getSeaFileRepoId(),
                    String.format("%s%s", item.parent_dir, item.name),
                    item.commit_id);
        }
        showLoadingDialog(R.string.str_executing);
        callEnqueue(jsonObjectCall,
                new SFileCallBack<JsonObject>() {
                    @Override
                    public void onSuccess(Call<JsonObject> call, Response<JsonObject> response) {
                        dismissLoadingDialog();
                        if (JsonUtils.getBoolValue(response.body(),"success")) {
                            getData(true);
                            showToast(R.string.sfile_recovery_success);
                        } else {
                            showToast(R.string.sfile_recovery_fail);
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        dismissLoadingDialog();
                        super.onFailure(call, t);
                    }
                });
    }

    @Override
    public void onItemChildClick(BaseRecyclerAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
        switch (view.getId()) {
            case R.id.document_restore_iv:
                showFileRevertConfirmDialog(position);
                break;
        }
    }

    /**
     * 展示文件恢复确认对话框
     *
     * @param pos
     */
    private void showFileRevertConfirmDialog(final int pos) {
        new BottomActionDialog(getContext(),
                null,
                Arrays.asList(getString(R.string.sfile_recovery)),
                new BottomActionDialog.OnActionItemClickListener() {
                    @Override
                    public void onItemClick(BottomActionDialog dialog, BottomActionDialog.ActionItemAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
                        dialog.dismiss();
                        fileRevert(pos);
                    }
                }).show();
    }
}
