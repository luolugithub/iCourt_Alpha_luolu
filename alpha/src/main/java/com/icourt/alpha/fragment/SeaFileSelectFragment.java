package com.icourt.alpha.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.adapter.FolderOnlySelectAdapter;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.adapter.baseadapter.HeaderFooterAdapter;
import com.icourt.alpha.adapter.baseadapter.adapterObserver.DataChangeAdapterObserver;
import com.icourt.alpha.base.BaseFragment;
import com.icourt.alpha.entity.bean.FolderDocumentEntity;
import com.icourt.alpha.entity.bean.SeaFileSelectParam;
import com.icourt.alpha.http.callback.SFileCallBack;
import com.icourt.alpha.interfaces.OnFragmentCallBackListener;
import com.icourt.alpha.interfaces.OnSeaFileSelectListener;
import com.icourt.alpha.view.smartrefreshlayout.EmptyRecyclerView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Response;


/**
 * Description  文件列表
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/9/11
 * version 2.1.0
 */
public class SeaFileSelectFragment extends BaseFragment
        implements BaseRecyclerAdapter.OnItemClickListener {
    public static final String KEY_SEAFILE_SELECT_PARAM = "key_seafile_select_param";
    private static final int MAX_SELECT_NUM = 10;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;


    public static SeaFileSelectFragment newInstance(@NonNull SeaFileSelectParam seaFileSelectParam) {
        SeaFileSelectFragment fragment = new SeaFileSelectFragment();
        Bundle args = new Bundle();
        args.putSerializable(KEY_SEAFILE_SELECT_PARAM, seaFileSelectParam);
        fragment.setArguments(args);
        return fragment;
    }

    @BindView(R.id.recyclerView)
    EmptyRecyclerView recyclerView;
    Unbinder unbinder;
    SeaFileSelectParam iSeaFileSelectParams;
    FolderOnlySelectAdapter folderDocumentAdapter;
    OnFragmentCallBackListener onFragmentCallBackListener;
    OnSeaFileSelectListener onSeaFileSelectListener;
    ArrayList<FolderDocumentEntity> selectedFolderDocumentEntities;

    protected ArrayList<FolderDocumentEntity> getSelectedFolderDocumentEntities() {
        return selectedFolderDocumentEntities;
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

        if (getParentFragment() instanceof OnSeaFileSelectListener) {
            onSeaFileSelectListener = (OnSeaFileSelectListener) getParentFragment();
        } else {
            try {
                onSeaFileSelectListener = (OnSeaFileSelectListener) context;
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
        iSeaFileSelectParams = (SeaFileSelectParam) getArguments().getSerializable(KEY_SEAFILE_SELECT_PARAM);
        selectedFolderDocumentEntities = iSeaFileSelectParams.getSelectedFolderDocuments();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(
                folderDocumentAdapter = new FolderOnlySelectAdapter(
                        getSelectedFolderDocumentEntities(),
                        true,
                        false
                ));
        folderDocumentAdapter.setOnItemClickListener(this);

        final TextView emptyView = (TextView) HeaderFooterAdapter.inflaterView(getContext(), R.layout.footer_folder_document_num, recyclerView.getRecyclerView());
        emptyView.setText(R.string.sfile_folder_empty);
        recyclerView.setEmptyView(emptyView);
        folderDocumentAdapter.registerAdapterDataObserver(new DataChangeAdapterObserver() {
            @Override
            protected void updateUI() {
                recyclerView.enableEmptyView(folderDocumentAdapter.getData());
            }
        });
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(com.scwang.smartrefresh.layout.api.RefreshLayout refreshlayout) {
                getData(true);
            }
        });
        refreshLayout.autoRefresh();
    }


    @Override
    protected void getData(boolean isRefresh) {
        super.getData(isRefresh);
        callEnqueue(
                getSFileApi().documentDirQuery(
                        iSeaFileSelectParams.getDstRepoId(),
                        iSeaFileSelectParams.getDstRepoDirPath()),
                new SFileCallBack<List<FolderDocumentEntity>>() {
                    @Override
                    public void onSuccess(Call<List<FolderDocumentEntity>> call, Response<List<FolderDocumentEntity>> response) {
                        folderDocumentAdapter.bindData(true, wrapDatas(response.body()));
                        recyclerView.enableEmptyView(response.body());
                        stopRefresh();
                    }

                    @Override
                    public void onFailure(Call<List<FolderDocumentEntity>> call, Throwable t) {
                        super.onFailure(call, t);
                        stopRefresh();
                        recyclerView.enableEmptyView(null);
                    }
                });
    }

    /**
     * type=-1 表示移除元素
     *
     * @param targetFrgament
     * @param type
     * @param bundle
     */
    @Override
    public void notifyFragmentUpdate(Fragment targetFrgament, int type, Bundle bundle) {
        super.notifyFragmentUpdate(targetFrgament, type, bundle);
        if (type == -1 && bundle != null) {
            Serializable serializable = bundle.getSerializable(KEY_FRAGMENT_UPDATE_KEY);
            if (serializable instanceof FolderDocumentEntity) {
                FolderDocumentEntity folderDocumentEntity = (FolderDocumentEntity) serializable;
                boolean removed = selectedFolderDocumentEntities.remove(folderDocumentEntity);
                if (removed) {
                    folderDocumentAdapter.notifyDataSetChanged();
                }
            }
        }

    }

    /**
     * 包装数据
     *
     * @param datas
     */
    private List<FolderDocumentEntity> wrapDatas(List<FolderDocumentEntity> datas) {
        if (datas != null) {
            for (int i = 0; i < datas.size(); i++) {
                FolderDocumentEntity folderDocumentEntity = datas.get(i);
                if (folderDocumentEntity == null) continue;
                folderDocumentEntity.repoId = iSeaFileSelectParams.getDstRepoId();
                folderDocumentEntity.parent_dir = iSeaFileSelectParams.getDstRepoDirPath();
            }
        }
        return datas;
    }

    private void stopRefresh() {
        if (refreshLayout != null) {
            refreshLayout.finishRefresh();
            refreshLayout.finishLoadmore();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onItemClick(BaseRecyclerAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
        FolderDocumentEntity item = folderDocumentAdapter.getItem(position);
        if (item == null) return;
        if (item.isDir()) {
            //请求父容器
            if (onFragmentCallBackListener != null) {
                Bundle bundle = new Bundle();
                SeaFileSelectParam seaFileSelectParam = new SeaFileSelectParam(
                        iSeaFileSelectParams.getRepoType(),
                        iSeaFileSelectParams.getRepoName(),
                        iSeaFileSelectParams.getDstRepoId(),
                        String.format("%s%s/", iSeaFileSelectParams.getDstRepoDirPath(), item.name),
                        getSelectedFolderDocumentEntities());
                bundle.putSerializable(KEY_FRAGMENT_RESULT, seaFileSelectParam);
                onFragmentCallBackListener.onFragmentCallBack(this, 1, bundle);
            }
        } else {
            if (folderDocumentAdapter.isSelectable()) {
                if (!getSelectedFolderDocumentEntities().contains(item)) {
                    if (getSelectedFolderDocumentEntities().size() >= MAX_SELECT_NUM) {
                        showToast(String.format("最多同时选择 %s 个文件", MAX_SELECT_NUM));
                        return;
                    }
                    getSelectedFolderDocumentEntities().add(item);

                    if (onSeaFileSelectListener != null) {
                        onSeaFileSelectListener.onSeaFileSelect(item);
                    }
                } else {
                    getSelectedFolderDocumentEntities().remove(item);

                    if (onSeaFileSelectListener != null) {
                        onSeaFileSelectListener.onSeaFileSelectCancel(item);
                    }
                }
                folderDocumentAdapter.notifyDataSetChanged();
            }
        }
    }
}
