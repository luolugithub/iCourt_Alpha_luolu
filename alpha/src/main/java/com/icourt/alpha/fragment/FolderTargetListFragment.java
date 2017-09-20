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
import com.google.gson.JsonElement;
import com.icourt.alpha.R;
import com.icourt.alpha.adapter.FolderAdapter;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.adapter.baseadapter.adapterObserver.DataChangeAdapterObserver;
import com.icourt.alpha.constants.Const;
import com.icourt.alpha.entity.bean.FolderDocumentEntity;
import com.icourt.alpha.entity.bean.ISeaFile;
import com.icourt.alpha.entity.event.SeaFolderEvent;
import com.icourt.alpha.http.callback.SFileCallBack;
import com.icourt.alpha.http.observer.BaseObserver;
import com.icourt.alpha.interfaces.OnFragmentCallBackListener;
import com.icourt.alpha.utils.FileUtils;
import com.icourt.alpha.view.xrefreshlayout.RefreshLayout;
import com.icourt.alpha.widget.filter.ListFilter;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import retrofit2.Call;
import retrofit2.Response;

import static com.icourt.alpha.constants.Const.FILE_ACTION_ADD;
import static com.icourt.alpha.constants.Const.FILE_ACTION_COPY;
import static com.icourt.alpha.constants.Const.FILE_ACTION_MOVE;
import static com.icourt.alpha.constants.SFileConfig.PERMISSION_RW;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/8/12
 * version 2.1.0
 */
public class FolderTargetListFragment extends SeaFileBaseFragment
        implements BaseRecyclerAdapter.OnItemClickListener {

    protected static final String KEY_SEA_FILE_SELCTED_FILES = "seaFileSelctedFiles";
    protected static final String KEY_SEA_FILE_FROM_REPO_ID = "seaFileFromRepoId";//原仓库id
    protected static final String KEY_SEA_FILE_FROM_DIR_PATH = "seaFileFromDirPath";//原仓库路径

    protected static final String KEY_SEA_FILE_DST_REPO_ID = "seaFileDstRepoId";//目标仓库id
    protected static final String KEY_SEA_FILE_DST_DIR_PATH = "seaFileDstDirPath";//目标仓库路径

    protected static final String KEY_FOLDER_ACTION_TYPE = "folderActionType";//文件操作类型
    protected static final String KEY_SEA_FILE_LOCAL_PATH = "fileLocalPath";   //文件保存到地点
    @BindView(R.id.empty_text)
    TextView emptyText;

    /**
     * @param folderActionType
     * @param fromRepoId
     * @param fromRepoDirPath
     * @param dstRepoId
     * @param dstRepoDirPath
     * @param selectedFolderFiles
     * @return
     */
    public static FolderTargetListFragment newInstance(
            @Const.FILE_ACTION_TYPE int folderActionType,
            String fromRepoId,
            String fromRepoDirPath,
            @Nullable String dstRepoId,
            String dstRepoDirPath,
            ArrayList<? extends ISeaFile> selectedFolderFiles,
            String fileLocalPath) {
        FolderTargetListFragment fragment = new FolderTargetListFragment();
        Bundle args = new Bundle();
        args.putInt(KEY_FOLDER_ACTION_TYPE, folderActionType);
        args.putString(KEY_SEA_FILE_FROM_REPO_ID, fromRepoId);
        args.putString(KEY_SEA_FILE_FROM_DIR_PATH, fromRepoDirPath);

        args.putString(KEY_SEA_FILE_DST_REPO_ID, dstRepoId);
        args.putString(KEY_SEA_FILE_DST_DIR_PATH, dstRepoDirPath);

        args.putSerializable(KEY_SEA_FILE_SELCTED_FILES, selectedFolderFiles);
        args.putString(KEY_SEA_FILE_LOCAL_PATH, fileLocalPath);
        fragment.setArguments(args);
        return fragment;
    }

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    Unbinder unbinder;
    FolderAdapter folderAdapter;
    @BindView(R.id.copy_or_move_tv)
    TextView copyOrMoveTv;
    @BindView(R.id.refreshLayout)
    RefreshLayout refreshLayout;
    OnFragmentCallBackListener onFragmentCallBackListener;
    ArrayList<ISeaFile> selectedFolderFiles;

    @Const.FILE_ACTION_TYPE
    private int getFileActionType() {
        switch (getArguments().getInt(KEY_FOLDER_ACTION_TYPE)) {
            case FILE_ACTION_COPY:
                return FILE_ACTION_COPY;
            case FILE_ACTION_MOVE:
                return FILE_ACTION_MOVE;
            case FILE_ACTION_ADD:
                return FILE_ACTION_ADD;
        }
        return FILE_ACTION_MOVE;
    }

    /**
     * 源仓库id
     *
     * @return
     */
    protected String getSeaFileFromRepoId() {
        return getArguments().getString(KEY_SEA_FILE_FROM_REPO_ID, "");
    }

    /**
     * 源仓库地址
     *
     * @return
     */
    protected String getSeaFileFromDirPath() {
        return getArguments().getString(KEY_SEA_FILE_FROM_DIR_PATH, "");
    }

    /**
     * 目标仓库id
     *
     * @return
     */
    protected String getSeaFileDstRepoId() {
        return getArguments().getString(KEY_SEA_FILE_DST_REPO_ID, "");
    }

    /**
     * 目标仓库路径
     *
     * @return
     */
    protected String getSeaFileDstDirPath() {
        return getArguments().getString(KEY_SEA_FILE_DST_DIR_PATH, "");
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (getParentFragment() instanceof OnFragmentCallBackListener) {
            onFragmentCallBackListener = (OnFragmentCallBackListener) getParentFragment();
        } else {
            try {
                onFragmentCallBackListener = (OnFragmentCallBackListener) context;
            } catch (ClassCastException e) {
                e.printStackTrace();
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(R.layout.fragment_folder_target_list, inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void initView() {
        selectedFolderFiles = (ArrayList<ISeaFile>) getArguments().getSerializable(KEY_SEA_FILE_SELCTED_FILES);
        switch (getFileActionType()) {
            case FILE_ACTION_COPY:
                emptyText.setText("点击\"复制\"，将所选项复制到此目录");
                if (!isSameDir()) {
                    copyOrMoveTv.setText("复制");
                    copyOrMoveTv.setEnabled(true);
                } else {
                    copyOrMoveTv.setText("无法复制到同目录");
                    copyOrMoveTv.setEnabled(false);
                }
                break;
            case FILE_ACTION_MOVE:
                emptyText.setText("点击\"移动\"，将所选项移动到此目录");
                if (!isSameDir()) {
                    copyOrMoveTv.setText("移动");
                    copyOrMoveTv.setEnabled(true);
                } else {
                    copyOrMoveTv.setText("无法移动到同目录");
                    copyOrMoveTv.setEnabled(false);
                }
                break;
            case FILE_ACTION_ADD:
                emptyText.setText("点击\"保存\"，将所选项保存到此目录");
                copyOrMoveTv.setText("保存");
                copyOrMoveTv.setEnabled(true);
                break;
            default:
                emptyText.setText("点击“确定”，将所选项操作到此目录");
                copyOrMoveTv.setEnabled(true);
                copyOrMoveTv.setText("确定");
                break;
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(folderAdapter = new FolderAdapter());
        folderAdapter.registerAdapterDataObserver(new DataChangeAdapterObserver() {
            @Override
            protected void updateUI() {
                if (emptyText != null) {
                    emptyText.setVisibility(folderAdapter.getItemCount() <= 0 ? View.VISIBLE : View.GONE);
                }
            }
        });
        folderAdapter.setOnItemClickListener(this);
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
        callEnqueue(getSFileApi().documentDirQuery(
                getSeaFileDstRepoId(),
                getSeaFileDstDirPath()),
                new SFileCallBack<List<FolderDocumentEntity>>() {
                    @Override
                    public void onSuccess(Call<List<FolderDocumentEntity>> call, Response<List<FolderDocumentEntity>> response) {
                        stopRefresh();
                        //过滤 非文件夹的文件
                        if (response.body() != null) {
                            new ListFilter<FolderDocumentEntity>().filter(response.body(), FolderDocumentEntity.TYPE_FILE);

                            filterOnlyReadFolder(response.body());

                            filterSameRepoFolder(response.body());

                        }
                        folderAdapter.bindData(isRefresh, response.body());
                    }

                    @Override
                    public void onFailure(Call<List<FolderDocumentEntity>> call, Throwable t) {
                        super.onFailure(call, t);
                        stopRefresh();
                    }
                });
    }

    /**
     * 过滤只读权限的
     *
     * @param datas
     */
    private void filterOnlyReadFolder(List<FolderDocumentEntity> datas) {
        if (datas == null) return;
        if (datas.isEmpty()) return;
        for (int i = datas.size() - 1; i >= 0; i--) {
            FolderDocumentEntity folderDocumentEntity = datas.get(i);
            if (folderDocumentEntity == null) continue;
            if (!TextUtils.equals(PERMISSION_RW, folderDocumentEntity.permission)) {
                datas.remove(i);
            }
        }
    }

    /**
     * //过滤  移动的时候 不能自己移动到自己内部 eg.  /a-->/a/
     * 统一 不能自己移动 复制 到自己里面
     *
     * @param datas
     */
    private void filterSameRepoFolder(List<FolderDocumentEntity> datas) {
        if (datas != null
                && !datas.isEmpty()
                && selectedFolderFiles != null
                && !selectedFolderFiles.isEmpty()) {
            for (int i = datas.size() - 1; i >= 0; i--) {
                FolderDocumentEntity folderDocumentEntity = datas.get(i);
                if (folderDocumentEntity == null) continue;

                if (!folderDocumentEntity.isDir()) continue;

                for (int j = 0; j < selectedFolderFiles.size(); j++) {
                    ISeaFile iSeaFile = selectedFolderFiles.get(j);
                    //比较id  全路径为null  repoid 也为null
                    if (TextUtils.equals(iSeaFile.getSeaFileId(), folderDocumentEntity.id)) {
                        datas.remove(i);
                    }
                }
            }
        }
    }

    private void stopRefresh() {
        if (refreshLayout != null) {
            refreshLayout.stopRefresh();
            refreshLayout.stopLoadMore();
        }
    }

    /**
     * 是否是同一级目录
     *
     * @return
     */
    private boolean isSameDir() {
        return (TextUtils.equals(getSeaFileFromRepoId(), getSeaFileDstRepoId())
                && TextUtils.equals(getSeaFileFromDirPath(), getSeaFileDstDirPath()));
    }

    @OnClick({R.id.copy_or_move_tv})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.copy_or_move_tv:
                switch (getFileActionType()) {
                    case FILE_ACTION_COPY:
                        copyOrMove();
                        break;
                    case FILE_ACTION_MOVE:
                        copyOrMove();
                        break;
                    case FILE_ACTION_ADD:
                        addDocument();
                        return;
                    default:
                        copyOrMove();
                        break;
                }
                break;
            default:
                super.onClick(v);
                break;
        }
    }

    /**
     * 添加文件到选择的目录
     */
    private void addDocument() {
        String localFilePath = getArguments().getString(KEY_SEA_FILE_LOCAL_PATH, "");
        if (TextUtils.isEmpty(localFilePath)) {
            showToast(R.string.sfile_not_exist);
            return;
        }
        File file = null;
        try {
            file = new File(localFilePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (file != null && file.exists()) {
            seaFileUploadFiles(
                    getSeaFileDstRepoId(),
                    getSeaFileDstDirPath(),
                    Arrays.asList(localFilePath),
                    new BaseObserver<JsonElement>() {
                        @Override
                        public void onNext(@NonNull JsonElement jsonElement) {

                        }

                        @Override
                        public void onError(@NonNull Throwable throwable) {
                            super.onError(throwable);
                            dismissLoadingDialog();
                        }

                        @Override
                        public void onSubscribe(@NonNull Disposable disposable) {
                            super.onSubscribe(disposable);
                            showLoadingDialog(R.string.str_uploading);
                        }

                        @Override
                        public void onComplete() {
                            super.onComplete();
                            dismissLoadingDialog();
                            //发送广播
                            EventBus.getDefault().post(new SeaFolderEvent(getFileActionType(), getSeaFileFromRepoId(), getSeaFileFromDirPath()));
                            showToast("保存成功");

                            //回调 关闭页面
                            if (onFragmentCallBackListener != null) {
                                onFragmentCallBackListener.onFragmentCallBack(FolderTargetListFragment.this, -1, null);
                            }
                        }
                    });
        } else {
            showToast(R.string.sfile_not_exist);
        }
    }

    /**
     * 移动或复制
     * 注意:不能复制或者移动到当前文件所在目录
     */

    private void copyOrMove() {
        if (selectedFolderFiles == null || selectedFolderFiles.isEmpty()) return;

        //拼接字符串 以冒号做分割
        StringBuilder fileNameSb = new StringBuilder();
        String spliteStr = ":";
        for (int i = 0; i < selectedFolderFiles.size(); i++) {
            String fileName = FileUtils.getFileName(selectedFolderFiles.get(i).getSeaFileFullPath());
            fileNameSb.append(spliteStr);
            fileNameSb.append(fileName);
        }
        fileNameSb.replace(0, spliteStr.length(), "");

        showLoadingDialog(null);
        Call<JsonElement> jsonElementCall;
        String actionSucessNoticeStr = null;
        switch (getFileActionType()) {
            case FILE_ACTION_COPY:
                actionSucessNoticeStr = "复制成功";
                jsonElementCall = getSFileApi().fileCopy(
                        getSeaFileFromRepoId(),
                        getSeaFileFromDirPath(),
                        fileNameSb.toString(),
                        getSeaFileDstRepoId(),
                        getSeaFileDstDirPath());
                break;
            case FILE_ACTION_ADD:
                actionSucessNoticeStr = "保存成功";
                jsonElementCall = getSFileApi().fileCopy(
                        getSeaFileFromRepoId(),
                        getSeaFileFromDirPath(),
                        fileNameSb.toString(),
                        getSeaFileDstRepoId(),
                        getSeaFileDstDirPath());
                break;
            case FILE_ACTION_MOVE:
                actionSucessNoticeStr = "移动成功";
                jsonElementCall = getSFileApi().fileMove(
                        getSeaFileFromRepoId(),
                        getSeaFileFromDirPath(),
                        fileNameSb.toString(),
                        getSeaFileDstRepoId(),
                        getSeaFileDstDirPath());
                break;
            default:
                actionSucessNoticeStr = "操作成功";
                jsonElementCall = getSFileApi().fileCopy(
                        getSeaFileFromRepoId(),
                        getSeaFileFromDirPath(),
                        fileNameSb.toString(),
                        getSeaFileDstRepoId(),
                        getSeaFileDstDirPath());
                break;
        }
        final String finalActionSucessNoticeStr = actionSucessNoticeStr;
        callEnqueue(jsonElementCall,
                new SFileCallBack<JsonElement>() {
                    @Override
                    public void onSuccess(Call<JsonElement> call, Response<JsonElement> response) {
                        dismissLoadingDialog();
                        //发送广播
                        EventBus.getDefault().post(new SeaFolderEvent(getFileActionType(), getSeaFileFromRepoId(), getSeaFileFromDirPath()));
                        showToast(finalActionSucessNoticeStr);

                        //回调 关闭页面
                        if (onFragmentCallBackListener != null) {
                            onFragmentCallBackListener.onFragmentCallBack(FolderTargetListFragment.this, -1, null);
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonElement> call, Throwable t) {
                        dismissLoadingDialog();
                        super.onFailure(call, t);
                    }
                });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onItemClick(BaseRecyclerAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
        FolderDocumentEntity item = folderAdapter.getItem(position);
        if (item == null) return;
        //注意:不能复制或者移动到当前文件所在目录
        String dstDirPath = String.format("%s%s/", getSeaFileDstDirPath(), item.name);
        if (TextUtils.equals(getSeaFileFromRepoId(), getSeaFileDstRepoId())
                && TextUtils.equals(getSeaFileFromDirPath(), dstDirPath)) {
            switch (getFileActionType()) {
                case FILE_ACTION_COPY:
                    showToast("不能复制到当前目录");
                    break;
                case FILE_ACTION_MOVE:
                    showToast("不能移动到当前目录");
                    break;
                default:
                    showToast("不能选择当前目录");
                    break;
            }
            return;
        }
        if (onFragmentCallBackListener != null) {
            Bundle bundle = new Bundle();
            bundle.putString(KEY_SEA_FILE_DST_REPO_ID, getSeaFileDstRepoId());
            bundle.putString(KEY_SEA_FILE_DST_DIR_PATH, dstDirPath);
            onFragmentCallBackListener.onFragmentCallBack(this, 1, bundle);
        }
    }
}
