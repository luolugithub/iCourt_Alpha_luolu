package com.icourt.alpha.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.icourt.alpha.R;
import com.icourt.alpha.adapter.FileChangedHistoryAdapter;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.adapter.baseadapter.HeaderFooterAdapter;
import com.icourt.alpha.adapter.baseadapter.adapterObserver.DataChangeAdapterObserver;
import com.icourt.alpha.base.BaseDialogFragment;
import com.icourt.alpha.constants.DownloadConfig;
import com.icourt.alpha.constants.SFileConfig;
import com.icourt.alpha.entity.bean.FileChangedHistoryEntity;
import com.icourt.alpha.entity.bean.FolderDocumentEntity;
import com.icourt.alpha.entity.bean.ISeaFile;
import com.icourt.alpha.entity.bean.RepoMatterEntity;
import com.icourt.alpha.entity.event.FileRenameEvent;
import com.icourt.alpha.http.callback.SFileCallBack;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.callback.SimpleCallBack2;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.interfaces.OnFragmentDataChangeListener;
import com.icourt.alpha.utils.ActionConstants;
import com.icourt.alpha.utils.FileUtils;
import com.icourt.alpha.utils.JsonUtils;
import com.icourt.alpha.view.smartrefreshlayout.EmptyRecyclerView;
import com.icourt.alpha.widget.dialog.BottomActionDialog;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadmoreListener;

import org.greenrobot.eventbus.EventBus;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.HttpException;
import retrofit2.Response;

import static com.icourt.alpha.constants.SFileConfig.PERMISSION_RW;

/**
 * Description   修改历史列表
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/8/17
 * version 2.1.0
 */
public class FileChangeHistoryFragment extends BaseDialogFragment implements BaseRecyclerAdapter.OnItemChildClickListener {
    FileChangedHistoryAdapter fileChangedHistoryAdapter;
    int page = 0;

    protected static final String STRING_HTTP_SUCCESS = "success";

    protected static final String KEY_SEA_FILE_FROM_REPO_ID = "seaFileFromRepoId";//原仓库id
    protected static final String KEY_SEA_FILE_REPO_PERMISSION = "seaFileRepoPermission";//repo的权限
    protected static final String KEY_SEA_FILE_REPO_TYPE = "repoType";//repo类型

    OnFragmentDataChangeListener onFragmentDataChangeListener;
    @BindView(R.id.recyclerView)
    @Nullable
    EmptyRecyclerView recyclerView;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    Unbinder unbinder;
    Handler mHandler = new Handler();

    public static FileChangeHistoryFragment newInstance(
            @SFileConfig.REPO_TYPE int repoType,
            String fromRepoId,
            @SFileConfig.FILE_PERMISSION String repoPermission) {
        FileChangeHistoryFragment fragment = new FileChangeHistoryFragment();
        Bundle args = new Bundle();
        args.putString(KEY_SEA_FILE_FROM_REPO_ID, fromRepoId);
        args.putString(KEY_SEA_FILE_REPO_PERMISSION, repoPermission);
        args.putInt(KEY_SEA_FILE_REPO_TYPE, repoType);
        fragment.setArguments(args);
        return fragment;
    }

    @SFileConfig.REPO_TYPE
    private int getRepoType() {
        return SFileConfig.convert2RepoType(getArguments().getInt(KEY_SEA_FILE_REPO_TYPE));
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
        TextView footerView = (TextView) HeaderFooterAdapter.inflaterView(getContext(), R.layout.footer_folder_document_num, recyclerView.getRecyclerView());
        footerView.setText(R.string.sfile_change_history_empty);
        recyclerView.setEmptyView(footerView);


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
                if (refreshLayout != null) {
                    recyclerView.enableEmptyView(fileChangedHistoryAdapter.getData());
                }
            }
        });
        fileChangedHistoryAdapter.setOnItemChildClickListener(this);
        refreshLayout.setOnRefreshLoadmoreListener(new OnRefreshLoadmoreListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                getData(true);
            }

            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                getData(false);
            }
        });
        refreshLayout.autoRefresh();
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
            case SFileConfig.REPO_SHARED_ME:
            case SFileConfig.REPO_LAWFIRM:
                getChangeHistory(isRefresh, null);
                break;
            case SFileConfig.REPO_PROJECT:
                callEnqueue(getApi().repoMatterIdQuery(getSeaFileRepoId()),
                        new SimpleCallBack2<RepoMatterEntity>() {
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
        callEnqueue(resEntityCall, new SimpleCallBack<List<FileChangedHistoryEntity>>() {

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
                enableLoadMore(response.body().result);
            }

            @Override
            public void onFailure(Call<ResEntity<List<FileChangedHistoryEntity>>> call, Throwable t) {
                super.onFailure(call, t);
                stopRefresh();
            }
        });
    }

    private void enableLoadMore(List result) {
        if (refreshLayout != null) {
            refreshLayout.setEnableLoadmore(result != null
                    && result.size() >= ActionConstants.DEFAULT_PAGE_SIZE);
        }
    }

    private void stopRefresh() {
        if (refreshLayout != null) {
            refreshLayout.finishRefresh();
            refreshLayout.finishLoadmore();
        }
    }

    /**
     * 延迟刷新 sfile有延迟
     */
    private void delayRefresh() {
        mHandler.removeCallbacksAndMessages(null);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getData(true);
            }
        }, 500);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mHandler.removeCallbacksAndMessages(null);
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
                null,
                Arrays.asList(getString(R.string.sfile_change_history_revert)),
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
        if (TextUtils.equals(actionTypeEnglish, FileChangedHistoryEntity.OP_TYPE_DELETE)) {
            if (item.isDir()) {
                revokeFolder(item);
            } else {
                revokeFile(item);
            }
        } else if (TextUtils.equals(actionTypeEnglish, FileChangedHistoryEntity.OP_TYPE_CREATE)) {
            if (item.isDir()) {
                deleteFolder(item);
            } else {
                deleteFile(item);
            }
        } else if (TextUtils.equals(actionTypeEnglish, FileChangedHistoryEntity.OP_TYPE_MOVE)) {
            fileRevokeMove(item);
        } else if (TextUtils.equals(actionTypeEnglish, FileChangedHistoryEntity.OP_TYPE_RECOVER)) {
            //不需要处理
        } else if (TextUtils.equals(actionTypeEnglish, FileChangedHistoryEntity.OP_TYPE_RENAME)) {
            if (item.isDir()) {
                renameFolderRevoke(item);
            } else {
                renameFileRevoke(item);
            }
        } else if (TextUtils.equals(actionTypeEnglish, FileChangedHistoryEntity.OP_TYPE_EDIT)) {
            revokeEditFile(item);
        } else {
            showToast("未命名动作");
        }
    }

    private void renameFileRevoke(final FileChangedHistoryEntity item) {
        if (item == null) return;
        //"/aaaa/Android手机推送设置.docx"---->"Android手机推送设置.docx",
        String orginName = item.path;
        if (!TextUtils.isEmpty(item.path)) {
            String[] split = item.path.split("/");
            if (split != null && split.length > 0) {
                orginName = split[split.length - 1];
            }
        }
        showLoadingDialog(R.string.str_executing);
        callEnqueue(getSFileApi().fileRename(
                item.repo_id,
                TextUtils.isEmpty(item.new_path) ? item.path : item.new_path,
                "rename",
                orginName),
                new SFileCallBack<FolderDocumentEntity>() {
                    @Override
                    public void onSuccess(Call<FolderDocumentEntity> call, Response<FolderDocumentEntity> response) {
                        dismissLoadingDialog();
                        showToast(R.string.sfile_revert_success);
                        delayRefresh();
                    }

                    @Override
                    public void onFailure(Call<FolderDocumentEntity> call, Throwable t) {
                        dismissLoadingDialog();
                        if (!handleRevokeFail(item, t)) {
                            super.onFailure(call, t);
                        }
                    }

                    @Override
                    public void defNotify(String noticeStr) {
                        showToast(noticeStr);
                    }
                });
    }

    /**
     * 处理撤销失败的提示
     *
     * @param t
     * @return
     */
    private boolean handleRevokeFail(@Nullable final FileChangedHistoryEntity item, @NonNull Throwable t) {
        if (t instanceof HttpException
                && ((HttpException) t).code() == 404
                || ((HttpException) t).code() == 400) {
            showToast(R.string.sfile_revert_fail);
            if (item != null) {
                bugSync("文件撤销失败",
                        new StringBuilder("\nitem:")
                                .append(item.toString())
                                .append("\nthrowable")
                                .append(t.toString())
                                .toString());
            }
            return true;
        }
        return false;
    }

    private void renameFolderRevoke(final FileChangedHistoryEntity item) {
        if (item == null) return;
        if (TextUtils.isEmpty(item.new_path)) {
            //1. 提示
            String notice = "服务器返回 new_path 为null";

            //2.反馈上报
            bugSync(notice, item.toString());
            showToast(notice);
            return;
        }
        //eg.  "/aaaa/b1"---->"/aaaa/b",
        String orginName = item.path;
        if (!TextUtils.isEmpty(item.path)) {
            String[] split = item.path.split("/");
            if (split != null && split.length > 0) {
                orginName = split[split.length - 1];
            }
        }
        showLoadingDialog(R.string.str_executing);
        callEnqueue(getSFileApi().folderRename(
                item.repo_id,
                item.new_path,
                "rename",
                orginName),
                new SFileCallBack<String>() {
                    @Override
                    public void onSuccess(Call<String> call, Response<String> response) {
                        dismissLoadingDialog();

                        //3. 广播通知其他页面
                        EventBus.getDefault().post(
                                new FileRenameEvent(
                                        item.getSeaFileRepoId(),
                                        item.isDir(),
                                        item.new_path,
                                        item.path));


                        showToast(R.string.sfile_revert_success);
                        delayRefresh();
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        dismissLoadingDialog();
                        if (!handleRevokeFail(item, t)) {
                            super.onFailure(call, t);
                        }
                    }

                    @Override
                    public void defNotify(String noticeStr) {
                        showToast(noticeStr);
                        //super.defNotify(noticeStr);
                    }
                });
    }

    /**
     * 反向移动回去
     *
     * @param item
     */
    private void fileRevokeMove(final FileChangedHistoryEntity item) {
        if (item == null) return;
        showLoadingDialog(R.string.str_executing);
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
        if (!TextUtils.isEmpty(fromDir)) {
            int indexOf = fromDir.lastIndexOf("/");
            if (indexOf >= 1) {
                fromDir = fromDir.substring(0, indexOf);
            } else {
                fromDir = "/";
            }
        }
        callEnqueue(getSFileApi().fileMove(
                getSeaFileRepoId(),
                fromDir,
                item.file_name,
                item.repo_id,
                orginPath),
                new SFileCallBack<JsonElement>() {
                    @Override
                    public void onSuccess(Call<JsonElement> call, Response<JsonElement> response) {
                        dismissLoadingDialog();
                        showToast(R.string.sfile_revert_success);
                        delayRefresh();
                    }

                    @Override
                    public void onFailure(Call<JsonElement> call, Throwable t) {
                        dismissLoadingDialog();
                        if (!handleRevokeFail(item, t)) {
                            super.onFailure(call, t);
                        }
                    }

                    @Override
                    public void defNotify(String noticeStr) {
                        showToast(noticeStr);
                        //super.defNotify(noticeStr);
                    }
                });
    }

    /**
     * 文件夹回退
     *
     * @param item
     */
    private void revokeFolder(final FileChangedHistoryEntity item) {
        if (item == null) return;
        showLoadingDialog(R.string.str_executing);
        callEnqueue(getSFileApi().folderRevert(
                item.repo_id,
                item.path,
                item.pre_commit_id),
                new SFileCallBack<JsonObject>() {
                    @Override
                    public void onSuccess(Call<JsonObject> call, Response<JsonObject> response) {
                        dismissLoadingDialog();
                        if (JsonUtils.getBoolValue(response.body(), STRING_HTTP_SUCCESS)) {
                            showToast(R.string.sfile_revert_success);
                            delayRefresh();

                        } else {
                            showToast(R.string.sfile_revert_fail);
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        dismissLoadingDialog();
                        if (!handleRevokeFail(item, t)) {
                            super.onFailure(call, t);
                        }
                    }

                    @Override
                    public void defNotify(String noticeStr) {
                        showToast(noticeStr);
                        //super.defNotify(noticeStr);
                    }
                });
    }

    /**
     * 删除文件
     *
     * @param item
     */
    private void deleteFile(final FileChangedHistoryEntity item) {
        showLoadingDialog(R.string.str_executing);
        callEnqueue(getSFileApi().fileDelete(
                item.repo_id,
                item.path),
                new SFileCallBack<JsonObject>() {
                    @Override
                    public void onSuccess(Call<JsonObject> call, Response<JsonObject> response) {
                        dismissLoadingDialog();
                        if (JsonUtils.getBoolValue(response.body(), STRING_HTTP_SUCCESS)) {
                            showToast(R.string.sfile_revert_success);
                            deletCachedSeaFile(item);
                            delayRefresh();
                        } else {
                            showToast(R.string.sfile_revert_fail);
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        dismissLoadingDialog();
                        if (!handleRevokeFail(item, t)) {
                            super.onFailure(call, t);
                        }
                    }

                    @Override
                    public void defNotify(String noticeStr) {
                        showToast(noticeStr);
                        //super.defNotify(noticeStr);
                    }
                });
    }

    /**
     * 删除缓存的seafile
     *
     * @param item
     */
    private void deletCachedSeaFile(ISeaFile item) {
        FileUtils.deleteFile(DownloadConfig.getSeaFileDownloadPath(getLoginUserId(), item));
    }

    /**
     * 删除文件夹
     *
     * @param item
     */
    private void deleteFolder(final FileChangedHistoryEntity item) {
        showLoadingDialog(R.string.str_executing);
        callEnqueue(getSFileApi().folderDelete(
                item.repo_id,
                item.path),
                new SFileCallBack<JsonObject>() {
                    @Override
                    public void onSuccess(Call<JsonObject> call, Response<JsonObject> response) {
                        dismissLoadingDialog();
                        if (JsonUtils.getBoolValue(response.body(), STRING_HTTP_SUCCESS)) {
                            showToast(R.string.sfile_revert_success);
                            delayRefresh();
                        } else {
                            showToast(R.string.sfile_revert_fail);
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        dismissLoadingDialog();
                        if (!handleRevokeFail(item, t)) {
                            super.onFailure(call, t);
                        }
                    }

                    @Override
                    public void defNotify(String noticeStr) {
                        showToast(noticeStr);
                        //super.defNotify(noticeStr);
                    }
                });
    }

    /**
     * 撤销文件的修改
     *
     * @param item
     */
    private void revokeFile(final FileChangedHistoryEntity item) {
        if (item == null) return;
        showLoadingDialog(R.string.str_executing);
        callEnqueue(getSFileApi().fileRevert(
                item.repo_id,
                item.path,
                item.pre_commit_id),
                new SFileCallBack<JsonObject>() {
                    @Override
                    public void onSuccess(Call<JsonObject> call, Response<JsonObject> response) {
                        dismissLoadingDialog();
                        if (JsonUtils.getBoolValue(response.body(), STRING_HTTP_SUCCESS)) {
                            showToast(R.string.sfile_revert_success);
                            delayRefresh();
                        } else {
                            showToast(R.string.sfile_revert_fail);
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        dismissLoadingDialog();
                        if (!handleRevokeFail(item, t)) {
                            super.onFailure(call, t);
                        }
                    }

                    @Override
                    public void defNotify(String noticeStr) {
                        showToast(noticeStr);
                        //super.defNotify(noticeStr);
                    }
                });
    }

    /**
     * 撤销文件的修改
     *
     * @param item
     */
    private void revokeEditFile(final FileChangedHistoryEntity item) {
        if (item == null) return;
        showLoadingDialog(R.string.str_executing);
        callEnqueue(getSFileApi().fileRevertEdit(
                item.repo_id,
                item.path,
                item.pre_commit_id,
                "revert"),
                new SFileCallBack<JsonObject>() {
                    @Override
                    public void onSuccess(Call<JsonObject> call, Response<JsonObject> response) {
                        dismissLoadingDialog();
                        if (JsonUtils.getBoolValue(response.body(), STRING_HTTP_SUCCESS)) {
                            showToast(R.string.sfile_revert_success);
                            delayRefresh();
                        } else {
                            showToast(R.string.sfile_revert_fail);
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        dismissLoadingDialog();
                        if (!handleRevokeFail(item, t)) {
                            super.onFailure(call, t);
                        }
                    }

                    @Override
                    public void defNotify(String noticeStr) {
                        showToast(noticeStr);
                        //super.defNotify(noticeStr);
                    }
                });
    }
}
