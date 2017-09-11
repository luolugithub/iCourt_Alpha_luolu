package com.icourt.alpha.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.adapter.FolderDocumentAdapter;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.adapter.baseadapter.adapterObserver.DataChangeAdapterObserver;
import com.icourt.alpha.base.BaseFragment;
import com.icourt.alpha.constants.Const;
import com.icourt.alpha.entity.bean.FolderDocumentEntity;
import com.icourt.alpha.entity.bean.ISeaFileSelectParams;
import com.icourt.alpha.entity.bean.SeaFileSelectParam;
import com.icourt.alpha.http.callback.SFileCallBack;
import com.icourt.alpha.interfaces.OnFragmentCallBackListener;

import java.util.HashSet;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
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
public class SeaFileSelectFragment extends BaseFragment implements BaseRecyclerAdapter.OnItemClickListener {
    public static final String KEY_SEAFILE_SELECT_PARAM = "key_seafile_select_param";

    public static SeaFileSelectFragment newInstance(SeaFileSelectParam seaFileSelectParam) {
        SeaFileSelectFragment fragment = new SeaFileSelectFragment();
        Bundle args = new Bundle();
        args.putSerializable(KEY_SEAFILE_SELECT_PARAM, seaFileSelectParam);
        fragment.setArguments(args);
        return fragment;
    }

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.selected_num_tv)
    TextView selectedNumTv;
    Unbinder unbinder;
    @BindView(R.id.ok_tv)
    TextView okTv;
    ISeaFileSelectParams iSeaFileSelectParams;
    FolderDocumentAdapter folderDocumentAdapter;
    HashSet<FolderDocumentEntity> selectedFolderDocumentEntities;
    OnFragmentCallBackListener onFragmentCallBackListener;

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
        View view = super.onCreateView(R.layout.fragment_sea_file_select, inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void initView() {
        iSeaFileSelectParams = (ISeaFileSelectParams) getArguments().getSerializable(KEY_SEAFILE_SELECT_PARAM);
        selectedFolderDocumentEntities = iSeaFileSelectParams.getSelectedFolderDocuments();
        if (selectedFolderDocumentEntities == null) {
            selectedFolderDocumentEntities = new HashSet<>();
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(
                folderDocumentAdapter = new FolderDocumentAdapter(
                        Const.VIEW_TYPE_ITEM,
                        iSeaFileSelectParams.getDstRepoId(),
                        iSeaFileSelectParams.getDstRepoDirPath(),
                        true,
                        selectedFolderDocumentEntities));
        folderDocumentAdapter.setOnItemClickListener(this);
        folderDocumentAdapter.registerAdapterDataObserver(new DataChangeAdapterObserver() {
            @Override
            protected void updateUI() {
                updateSelectNum();
            }
        });

        Object[] selectArgs = new String[2];
        selectArgs[0] = String.valueOf(selectedFolderDocumentEntities.size());
        selectArgs[1] = String.valueOf(10);
        updateSelectNum();
        getData(true);
    }

    /**
     * 更新选中的数量
     */
    private void updateSelectNum() {
        selectedNumTv.setText(
                getString(R.string.sfile_file_already_selected,
                        String.valueOf(selectedFolderDocumentEntities.size()) + "/" + String.valueOf(10))
        );
    }

    @Override
    protected void getData(boolean isRefresh) {
        super.getData(isRefresh);
        showLoadingDialog(null);
        callEnqueue(
                getSFileApi().documentDirQuery(
                        iSeaFileSelectParams.getDstRepoId(),
                        iSeaFileSelectParams.getDstRepoDirPath()),
                new SFileCallBack<List<FolderDocumentEntity>>() {
                    @Override
                    public void onSuccess(Call<List<FolderDocumentEntity>> call, Response<List<FolderDocumentEntity>> response) {
                        dismissLoadingDialog();
                        folderDocumentAdapter.bindData(true, response.body());
                    }

                    @Override
                    public void onFailure(Call<List<FolderDocumentEntity>> call, Throwable t) {
                        super.onFailure(call, t);
                        dismissLoadingDialog();
                    }
                });
    }

    @OnClick({R.id.ok_tv})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ok_tv:
                //TODO 未完成
                showToast("未完成");
                break;
            default:
                super.onClick(v);
                break;
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
                        selectedFolderDocumentEntities);
                bundle.putSerializable(KEY_FRAGMENT_RESULT, seaFileSelectParam);
                onFragmentCallBackListener.onFragmentCallBack(this, 1, bundle);
            }
        } else {
            if (folderDocumentAdapter.isSelectable()) {
                if (!selectedFolderDocumentEntities.contains(item)) {
                    selectedFolderDocumentEntities.add(item);
                } else {
                    selectedFolderDocumentEntities.remove(item);
                }
                folderDocumentAdapter.notifyDataSetChanged();
            }
        }
    }
}
