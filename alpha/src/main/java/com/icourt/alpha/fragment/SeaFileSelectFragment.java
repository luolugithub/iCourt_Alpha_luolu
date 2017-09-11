package com.icourt.alpha.fragment;

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
import com.icourt.alpha.base.BaseFragment;
import com.icourt.alpha.constants.Const;
import com.icourt.alpha.entity.bean.FolderDocumentEntity;
import com.icourt.alpha.http.callback.SFileCallBack;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
public class SeaFileSelectFragment extends BaseFragment {
    protected static final String KEY_SEA_FILE_DST_REPO_ID = "seaFileDstRepoId";//目标仓库id
    protected static final String KEY_SEA_FILE_DST_DIR_PATH = "seaFileDstDirPath";//目标仓库路径
    protected static final String KEY_SEA_FILE_SELECTED = "key_sea_file_selected";//已经选中的

    /**
     * @param dstRepoId
     * @param dstRepoDirPath
     * @return
     */
    public static SeaFileSelectFragment newInstance(
            String dstRepoId,
            String dstRepoDirPath,
            HashSet<FolderDocumentEntity> selectedFolderDocumentEntities) {
        SeaFileSelectFragment fragment = new SeaFileSelectFragment();
        Bundle args = new Bundle();
        args.putString(KEY_SEA_FILE_DST_REPO_ID, dstRepoId);
        args.putString(KEY_SEA_FILE_DST_DIR_PATH, dstRepoDirPath);
        args.putSerializable(KEY_SEA_FILE_SELECTED, selectedFolderDocumentEntities);
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
    String dstRepoId;
    String dstRepoDirPath;
    FolderDocumentAdapter folderDocumentAdapter;
    Set<FolderDocumentEntity> selectedFolderDocumentEntities;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(R.layout.fragment_sea_file_select, inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void initView() {
        dstRepoId = getArguments().getString(KEY_SEA_FILE_DST_REPO_ID, "");
        dstRepoDirPath = getArguments().getString(KEY_SEA_FILE_DST_DIR_PATH, "");
        selectedFolderDocumentEntities = (Set<FolderDocumentEntity>) getArguments().getSerializable(KEY_SEA_FILE_SELECTED);
        if (selectedFolderDocumentEntities != null) {
            selectedFolderDocumentEntities = new HashSet<>();
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(
                folderDocumentAdapter = new FolderDocumentAdapter(
                        Const.VIEW_TYPE_ITEM,
                        dstRepoId,
                        dstRepoDirPath,
                        true,
                        selectedFolderDocumentEntities));

        Object[] selectArgs = new String[2];
        selectArgs[0] = String.valueOf(selectedFolderDocumentEntities.size());
        selectArgs[1] = String.valueOf(10);
        selectedNumTv.setText(
                getString(R.string.sfile_file_already_selected,
                        String.valueOf(selectedFolderDocumentEntities.size()) + "/" + String.valueOf(10))
        );

        getData(true);
    }

    @Override
    protected void getData(boolean isRefresh) {
        super.getData(isRefresh);
        showLoadingDialog(null);
        callEnqueue(
                getSFileApi().documentDirQuery(
                        dstRepoId,
                        dstRepoDirPath),
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
}
