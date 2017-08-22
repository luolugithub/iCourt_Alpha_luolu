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
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.icourt.alpha.R;
import com.icourt.alpha.adapter.FileChangedHistoryAdapter;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.adapter.baseadapter.adapterObserver.DataChangeAdapterObserver;
import com.icourt.alpha.base.BaseDialogFragment;
import com.icourt.alpha.constants.SFileConfig;
import com.icourt.alpha.entity.bean.FileChangedHistoryEntity;
import com.icourt.alpha.entity.bean.FolderDocumentEntity;
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

import static com.icourt.alpha.constants.SFileConfig.PERMISSION_RW;

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
    protected static final String KEY_SEA_FILE_REPO_PERMISSION = "seaFileRepoPermission";//repo的权限

    OnFragmentDataChangeListener onFragmentDataChangeListener;

    public static FileChangeHistoryFragment newInstance(
            @SFileConfig.REPO_TYPE int repoType,
            String fromRepoId,
            @SFileConfig.FILE_PERMISSION String repoPermission) {
        FileChangeHistoryFragment fragment = new FileChangeHistoryFragment();
        Bundle args = new Bundle();
        args.putString(KEY_SEA_FILE_FROM_REPO_ID, fromRepoId);
        args.putString(KEY_SEA_FILE_REPO_PERMISSION, repoPermission);
        args.putInt("repoType", repoType);
        fragment.setArguments(args);
        return fragment;
    }

    @SFileConfig.REPO_TYPE
    private int getRepoType() {
        return SFileConfig.convert2RepoType(getArguments().getInt("repoType"));
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
        recyclerView.setAdapter(fileChangedHistoryAdapter = new FileChangedHistoryAdapter(
                TextUtils.equals(getRepoPermission(), PERMISSION_RW)));
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

    private String getSeaFileRepoId() {
        return getArguments().getString(KEY_SEA_FILE_FROM_REPO_ID, "");
    }


    @Override
    protected void getData(final boolean isRefresh) {
        super.getData(isRefresh);
        if (isRefresh) {
            page = 0;
        }
        switch (getRepoType()) {
            case SFileConfig.REPO_MINE:
                getChangeHistory(isRefresh, null);
                break;
            case SFileConfig.REPO_SHARED_ME:
                getChangeHistory(isRefresh, null);
                break;
            case SFileConfig.REPO_LAWFIRM:
                getChangeHistory(isRefresh, null);
                break;
            case SFileConfig.REPO_PROJECT:
                getApi().repoMatterIdQuery(getSeaFileRepoId())
                        .enqueue(new SimpleCallBack2<RepoMatterEntity>() {
                            @Override
                            public void onSuccess(Call<RepoMatterEntity> call, Response<RepoMatterEntity> response) {
                                getChangeHistory(isRefresh, response.body().matterId);
                            }

                            @Override
                            public void onFailure(Call<RepoMatterEntity> call, Throwable t) {
                                super.onFailure(call, t);
                                stopRefresh();
                            }
                        });
                break;
        }
    }

    private void getChangeHistory(final boolean isRefresh, String matterId) {
        Call<ResEntity<List<FileChangedHistoryEntity>>> resEntityCall = null;
        switch (getRepoType()) {
            case SFileConfig.REPO_MINE:
                resEntityCall = getApi().repoMineChangeHistory(
                        page,
                        getSeaFileRepoId());
                break;
            case SFileConfig.REPO_SHARED_ME:
                resEntityCall = getApi().repoSharedChangeHistory(
                        page,
                        getSeaFileRepoId());
                break;
            case SFileConfig.REPO_LAWFIRM:

                break;
            case SFileConfig.REPO_PROJECT:
                resEntityCall = getApi().repoProjectChangeHistory(
                        page,
                        matterId);
                break;
        }
        if (resEntityCall == null) {
            stopRefresh();
            return;
        }
        resEntityCall.enqueue(new SimpleCallBack<List<FileChangedHistoryEntity>>() {

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
                        dispatchRevoke(item);
                    }
                })
                .show();
    }

    private void dispatchRevoke(FileChangedHistoryEntity item) {
        String actionTypeEnglish = item.op_type;
        if (TextUtils.equals(actionTypeEnglish, "delete")) {
            if (item.isDir()) {
                revokeFolder(item);
            } else {
                revokeFile(item);
            }
        } else if (TextUtils.equals(actionTypeEnglish, "create")) {
            if (item.isDir()) {
                deleteFolder(item);
            } else {
                deleteFile(item);
            }
        } else if (TextUtils.equals(actionTypeEnglish, "move")) {
            fileRevokeMove(item);
        } else if (TextUtils.equals(actionTypeEnglish, "recover")) {
            //不需要处理
        } else if (TextUtils.equals(actionTypeEnglish, "rename")) {
            if (item.isDir()) {
                renameFolderRevoke(item);
            } else {
                renameFileRevoke(item);
            }
        } else if (TextUtils.equals(actionTypeEnglish, "edit")) {
            revokeFile(item);
        } else {
            showToast("未命名动作");
        }
    }

    private void renameFileRevoke(FileChangedHistoryEntity item) {
        if (item == null) return;
        //"/aaaa/Android手机推送设置.docx"---->"Android手机推送设置.docx",
        String orginName = item.path;
        if (!TextUtils.isEmpty(item.path)) {
            String[] split = item.path.split("/");
            if (split != null && split.length > 0) {
                orginName = split[split.length - 1];
            }
        }
        showLoadingDialog(null);
        getSFileApi().fileRename(
                item.repo_id,
                TextUtils.isEmpty(item.new_path) ? item.path : item.new_path,
                "rename",
                orginName)
                .enqueue(new SFileCallBack<FolderDocumentEntity>() {
                    @Override
                    public void onSuccess(Call<FolderDocumentEntity> call, Response<FolderDocumentEntity> response) {
                        dismissLoadingDialog();
                        getData(true);
                    }

                    @Override
                    public void onFailure(Call<FolderDocumentEntity> call, Throwable t) {
                        super.onFailure(call, t);
                        dismissLoadingDialog();
                    }
                });
    }

    private void renameFolderRevoke(FileChangedHistoryEntity item) {
        if (item == null) return;
        //"/aaaa/bbbb"---->"bbbb",
        String orginName = item.path;
        if (!TextUtils.isEmpty(item.path)) {
            String[] split = item.path.split("/");
            if (split != null && split.length > 0) {
                orginName = split[split.length - 1];
            }
        }
        showLoadingDialog(null);
        getSFileApi().folderRename(
                item.repo_id,
                TextUtils.isEmpty(item.new_path) ? item.path : item.new_path,
                "rename",
                orginName)
                .enqueue(new SFileCallBack<String>() {
                    @Override
                    public void onSuccess(Call<String> call, Response<String> response) {
                        dismissLoadingDialog();
                        getData(true);
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        dismissLoadingDialog();
                        super.onFailure(call, t);
                    }
                });
    }

    /**
     * 反向移动回去
     *
     * @param item
     */
    private void fileRevokeMove(FileChangedHistoryEntity item) {
        if (item == null) return;
        showLoadingDialog(null);
        //eg.   "/hshh"---->"/" "/hshh/xx1"---> "/hshh"
        String orginPath = item.path;
        if (!TextUtils.isEmpty(item.path)) {
            int indexOf = item.path.lastIndexOf("/");
            if (indexOf >= 1) {
                orginPath = item.path.substring(0, indexOf);
            } else {
                orginPath = "/";
            }
        }
        String fromDir = item.new_path;
        if (!TextUtils.isEmpty(item.new_path)) {
            int indexOf = item.new_path.lastIndexOf("/");
            if (indexOf >= 1) {
                fromDir = item.path.substring(0, indexOf);
            } else {
                fromDir = "/";
            }
        }
        getSFileApi().fileMove(
                getSeaFileRepoId(),
                fromDir,
                item.file_name,
                item.repo_id,
                orginPath)
                .enqueue(new SFileCallBack<JsonElement>() {
                    @Override
                    public void onSuccess(Call<JsonElement> call, Response<JsonElement> response) {
                        dismissLoadingDialog();
                        getData(true);
                    }

                    @Override
                    public void onFailure(Call<JsonElement> call, Throwable t) {
                        super.onFailure(call, t);
                        dismissLoadingDialog();
                    }
                });
    }

    /**
     * 文件夹回退
     *
     * @param item
     */
    private void revokeFolder(FileChangedHistoryEntity item) {
        if (item == null) return;
        showLoadingDialog(null);
        getSFileApi().folderRevert(
                item.repo_id,
                item.path,
                item.pre_commit_id)
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
                        super.onFailure(call, t);
                        dismissLoadingDialog();
                    }
                });
    }

    /**
     * 删除文件
     *
     * @param item
     */
    private void deleteFile(FileChangedHistoryEntity item) {
        showLoadingDialog(null);
        getSFileApi().fileDelete(
                item.repo_id,
                item.path)
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

    /**
     * 删除文件夹
     *
     * @param item
     */
    private void deleteFolder(FileChangedHistoryEntity item) {
        showLoadingDialog(null);
        getSFileApi().folderDelete(
                item.repo_id,
                item.path)
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

    /**
     * 撤销文件的修改
     *
     * @param item
     */
    private void revokeFile(FileChangedHistoryEntity item) {
        if (item == null) return;
        showLoadingDialog(null);
        getSFileApi().fileRevert(
                item.repo_id,
                item.path,
                item.pre_commit_id)
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
