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
import com.icourt.alpha.constants.Const;
import com.icourt.alpha.entity.bean.FolderDocumentEntity;
import com.icourt.alpha.entity.bean.SFileTokenEntity;
import com.icourt.alpha.entity.event.SeaFolderEvent;
import com.icourt.alpha.http.callback.SimpleCallBack2;
import com.icourt.alpha.interfaces.OnFragmentCallBackListener;
import com.icourt.alpha.view.xrefreshlayout.RefreshLayout;
import com.icourt.alpha.widget.filter.ListFilter;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Response;

import static com.icourt.alpha.constants.Const.FILE_ACTION_COPY;
import static com.icourt.alpha.constants.Const.FILE_ACTION_MOVE;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/8/12
 * version 2.1.0
 */
public class FolderTargetListFragment extends FolderBaseFragment
        implements BaseRecyclerAdapter.OnItemClickListener {

    protected static final String KEY_SEA_FILE_SELCTED_FILES = "seaFileSelctedFiles";
    protected static final String KEY_SEA_FILE_FROM_REPO_ID = "seaFileFromRepoId";//原仓库id
    protected static final String KEY_SEA_FILE_FROM_DIR_PATH = "seaFileFromDirPath";//原仓库路径

    protected static final String KEY_SEA_FILE_DST_REPO_ID = "seaFileDstRepoId";//目标仓库id
    protected static final String KEY_SEA_FILE_DST_DIR_PATH = "seaFileDstDirPath";//目标仓库路径

    protected static final String KEY_FOLDER_ACTION_TYPE = "folderActionType";//文件操作类型

    public static FolderTargetListFragment newInstance(
            @Const.FILE_ACTION_TYPE int folderActionType,
            String fromRepoId,
            String fromRepoDirPath,
            String dstRepoId,
            String dstRepoDirPath,
            ArrayList<FolderDocumentEntity> selectedFolderDocumentEntities) {
        FolderTargetListFragment fragment = new FolderTargetListFragment();
        Bundle args = new Bundle();
        args.putInt(KEY_FOLDER_ACTION_TYPE, folderActionType);
        args.putString(KEY_SEA_FILE_FROM_REPO_ID, fromRepoId);
        args.putString(KEY_SEA_FILE_FROM_DIR_PATH, fromRepoDirPath);

        args.putString(KEY_SEA_FILE_DST_REPO_ID, dstRepoId);
        args.putString(KEY_SEA_FILE_DST_DIR_PATH, dstRepoDirPath);

        args.putSerializable(KEY_SEA_FILE_SELCTED_FILES, selectedFolderDocumentEntities);
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

    @Const.FILE_ACTION_TYPE
    private int getFileActionType() {
        switch (getArguments().getInt(KEY_FOLDER_ACTION_TYPE)) {
            case FILE_ACTION_COPY:
                return FILE_ACTION_COPY;
            case FILE_ACTION_MOVE:
                return FILE_ACTION_MOVE;
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
        switch (getFileActionType()) {
            case FILE_ACTION_COPY:
                copyOrMoveTv.setText("复制到");
                break;
            case FILE_ACTION_MOVE:
                copyOrMoveTv.setText("移动到");
                break;
            default:
                copyOrMoveTv.setText("确定");
                break;
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(folderAdapter = new FolderAdapter());
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
        getSFileToken(new SimpleCallBack2<SFileTokenEntity<String>>() {
            @Override
            public void onSuccess(Call<SFileTokenEntity<String>> call, Response<SFileTokenEntity<String>> response) {
                if (TextUtils.isEmpty(response.body().authToken)) {
                    showTopSnackBar("sfile authToken返回为null");
                    stopRefresh();
                    return;
                }
                getSFileApi().documentDirQuery(
                        String.format("Token %s", response.body().authToken),
                        getSeaFileDstRepoId(),
                        getSeaFileDstDirPath())
                        .enqueue(new SimpleCallBack2<List<FolderDocumentEntity>>() {
                            @Override
                            public void onSuccess(Call<List<FolderDocumentEntity>> call, Response<List<FolderDocumentEntity>> response) {
                                stopRefresh();
                                //过滤 非文件夹的文件
                                if (response.body() != null) {
                                    new ListFilter<FolderDocumentEntity>().filter(response.body(), FolderDocumentEntity.TYPE_FILE);
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

            @Override
            public void onFailure(Call<SFileTokenEntity<String>> call, Throwable t) {
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

    @OnClick({R.id.copy_or_move_tv})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.copy_or_move_tv:
                copyOrMove();
                break;
            default:
                super.onClick(v);
                break;
        }
    }

    /**
     * 移动或复制
     */
    private void copyOrMove() {

        getSFileToken(new SimpleCallBack2<SFileTokenEntity<String>>() {
            @Override
            public void onSuccess(Call<SFileTokenEntity<String>> call, Response<SFileTokenEntity<String>> response) {
                if (TextUtils.isEmpty(response.body().authToken)) {
                    showTopSnackBar("sfile authToken返回为null");
                    return;
                }


                final ArrayList<FolderDocumentEntity> selectedFolderDocumentEntities = (ArrayList<FolderDocumentEntity>) getArguments().getSerializable(KEY_SEA_FILE_SELCTED_FILES);
                if (selectedFolderDocumentEntities == null || selectedFolderDocumentEntities.isEmpty())
                    return;

                StringBuilder fileNameSb = new StringBuilder();
                String spliteStr = ":";
                for (int i = 0; i < selectedFolderDocumentEntities.size(); i++) {
                    FolderDocumentEntity folderDocumentEntity = selectedFolderDocumentEntities.get(i);
                    if (folderDocumentEntity == null) continue;
                    fileNameSb.append(spliteStr);
                    fileNameSb.append(folderDocumentEntity.name);
                }
                fileNameSb.replace(0, spliteStr.length(), "");

                showLoadingDialog(null);
                Call<JsonElement> jsonElementCall;
                String actionSucessNoticeStr = null;
                switch (getFileActionType()) {
                    case FILE_ACTION_COPY:
                        actionSucessNoticeStr = "复制成功";
                        jsonElementCall = getSFileApi().fileCopy(
                                String.format("Token %s", response.body().authToken),
                                getSeaFileFromRepoId(),
                                getSeaFileFromDirPath(),
                                fileNameSb.toString(),
                                getSeaFileDstRepoId(),
                                getSeaFileDstDirPath());
                        break;
                    case FILE_ACTION_MOVE:
                        actionSucessNoticeStr = "移动成功";
                        jsonElementCall = getSFileApi().fileMove(
                                String.format("Token %s", response.body().authToken),
                                getSeaFileFromRepoId(),
                                getSeaFileFromDirPath(),
                                fileNameSb.toString(),
                                getSeaFileDstRepoId(),
                                getSeaFileDstDirPath());
                        break;
                    default:
                        actionSucessNoticeStr = "操作成功";
                        jsonElementCall = getSFileApi().fileCopy(
                                String.format("Token %s", response.body().authToken),
                                getSeaFileFromRepoId(),
                                getSeaFileFromDirPath(),
                                fileNameSb.toString(),
                                getSeaFileDstRepoId(),
                                getSeaFileDstDirPath());
                        break;
                }
                final String finalActionSucessNoticeStr = actionSucessNoticeStr;
                jsonElementCall.enqueue(new SimpleCallBack2<JsonElement>() {
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
        if (onFragmentCallBackListener != null) {
            Bundle bundle = new Bundle();
            bundle.putString(KEY_SEA_FILE_DST_REPO_ID, getSeaFileDstRepoId());
            bundle.putString(KEY_SEA_FILE_DST_DIR_PATH, String.format("%s%s/", getSeaFileDstDirPath(), item.name));
            onFragmentCallBackListener.onFragmentCallBack(this, 1, bundle);
        }
    }
}
