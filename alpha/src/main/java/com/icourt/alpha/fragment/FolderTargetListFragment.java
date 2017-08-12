package com.icourt.alpha.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.util.ArraySet;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.icourt.alpha.R;
import com.icourt.alpha.adapter.FolderDocumentAdapter;
import com.icourt.alpha.constants.Const;
import com.icourt.alpha.entity.bean.FolderDocumentEntity;
import com.icourt.alpha.entity.bean.SFileTokenEntity;
import com.icourt.alpha.http.callback.SimpleCallBack2;
import com.icourt.alpha.interfaces.ISeaFileImageLoader;
import com.icourt.alpha.widget.filter.ListFilter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/8/12
 * version 2.1.0
 */
public class FolderTargetListFragment extends FolderBaseFragment {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    Unbinder unbinder;
    FolderDocumentAdapter folderDocumentAdapter;

    /**
     * @param seaFileRepoId
     * @param title
     * @param seaFileParentDirPath
     * @return
     */
    public static FolderTargetListFragment newInstance(
            String seaFileRepoId,
            String title,
            String seaFileParentDirPath) {
        FolderTargetListFragment fragment = new FolderTargetListFragment();
        Bundle args = new Bundle();
        args.putString(KEY_SEA_FILE_REPO_ID, seaFileRepoId);
        args.putString("title", title);
        args.putString(KEY_SEA_FILE_PARENT_DIR_PATH, seaFileParentDirPath);
        fragment.setArguments(args);
        return fragment;
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
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(folderDocumentAdapter = new FolderDocumentAdapter(Const.VIEW_TYPE_ITEM, new ISeaFileImageLoader() {
            @Override
            public void loadSFileImage(String fileName, ImageView view, int type, int size) {

            }
        }, false, new ArraySet<FolderDocumentEntity>()));
        getData(true);
    }

    @Override
    protected void getData(final boolean isRefresh) {
        super.getData(isRefresh);
        getSFileToken(new SimpleCallBack2<SFileTokenEntity<String>>() {
            @Override
            public void onSuccess(Call<SFileTokenEntity<String>> call, Response<SFileTokenEntity<String>> response) {
                if (TextUtils.isEmpty(response.body().authToken)) {
                    showTopSnackBar("sfile authToken返回为null");
                    return;
                }
                getSFileFolder(response.body().authToken, new SimpleCallBack2<List<FolderDocumentEntity>>() {
                    @Override
                    public void onSuccess(Call<List<FolderDocumentEntity>> call, Response<List<FolderDocumentEntity>> response) {
                        if (response.body() != null) {
                            new ListFilter<FolderDocumentEntity>().filter(response.body(), FolderDocumentEntity.TYPE_FILE);
                        }
                        folderDocumentAdapter.bindData(isRefresh, response.body());
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
}
