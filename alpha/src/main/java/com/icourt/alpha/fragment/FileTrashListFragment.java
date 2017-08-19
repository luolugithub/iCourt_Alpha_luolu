package com.icourt.alpha.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.util.ArraySet;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.andview.refreshview.XRefreshView;
import com.google.gson.JsonObject;
import com.icourt.alpha.BuildConfig;
import com.icourt.alpha.R;
import com.icourt.alpha.adapter.FolderDocumentAdapter;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.entity.bean.FolderDocumentEntity;
import com.icourt.alpha.entity.bean.SeaFileTrashPageEntity;
import com.icourt.alpha.http.callback.SFileCallBack;
import com.icourt.alpha.interfaces.ISeaFileImageLoader;
import com.icourt.alpha.utils.ActionConstants;
import com.icourt.alpha.utils.GlideUtils;
import com.icourt.alpha.utils.SFileTokenUtils;
import com.icourt.alpha.view.xrefreshlayout.RefreshLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Response;

import static com.icourt.alpha.constants.Const.VIEW_TYPE_ITEM;

/**
 * Description  文件回收站
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/8/19
 * version 2.1.0
 */
public class FileTrashListFragment extends SeaFileBaseFragment implements BaseRecyclerAdapter.OnItemClickListener, BaseRecyclerAdapter.OnItemChildClickListener {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.refreshLayout)
    RefreshLayout refreshLayout;
    Unbinder unbinder;
    protected static final String KEY_SEA_FILE_REPO_ID = "seaFileRepoId";//仓库id
    protected static final String KEY_SEA_FILE_DIR_PATH = "seaFileDirPath";//目录路径
    FolderDocumentAdapter folderDocumentAdapter;

    public static FileTrashListFragment newInstance(
            String fromRepoId,
            String fromRepoDirPath) {
        FileTrashListFragment fragment = new FileTrashListFragment();
        Bundle args = new Bundle();
        args.putString(KEY_SEA_FILE_REPO_ID, fromRepoId);
        args.putString(KEY_SEA_FILE_DIR_PATH, fromRepoDirPath);
        fragment.setArguments(args);
        return fragment;
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
        recyclerView.setAdapter(folderDocumentAdapter = new FolderDocumentAdapter(
                VIEW_TYPE_ITEM,
                new ISeaFileImageLoader() {
                    @Override
                    public void loadSFileImage(String fileName, ImageView view, int type, int size) {
                        GlideUtils.loadSFilePic(getContext(), getSfileThumbnailImage(fileName), view);
                    }
                },
                false,
                new ArraySet<FolderDocumentEntity>(),
                true));
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

    /**
     * 获取缩略图地址
     *
     * @param name
     * @return
     */
    private String getSfileThumbnailImage(String name) {
        //https://test.alphalawyer.cn/ilaw/api/v2/documents/thumbnailImage?repoId=d4f82446-a37f-478c-b6b5-ed0e779e1768&seafileToken=%20d6c69d6f4fc208483c243246c6973d8eb141501c&p=//1502507774237.png&size=250
        return String.format("%sapi/v2/documents/thumbnailImage?repoId=%s&seafileToken=%s&p=%s&size=%s",
                BuildConfig.API_URL,
                getSeaFileRepoId(),
                SFileTokenUtils.getSFileToken(),
                String.format("%s%s", getSeaFileDirPath(), name),
                150);
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
        getSFileApi().folderTrashQuery(
                getSeaFileRepoId(),
                getSeaFileDirPath(),
                ActionConstants.DEFAULT_PAGE_SIZE)
                .enqueue(new SFileCallBack<SeaFileTrashPageEntity<FolderDocumentEntity>>() {
                    @Override
                    public void onSuccess(Call<SeaFileTrashPageEntity<FolderDocumentEntity>> call, Response<SeaFileTrashPageEntity<FolderDocumentEntity>> response) {
                        folderDocumentAdapter.bindData(isRefresh, response.body().data);
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
        showToast("无法浏览回收站里的文件");
    }

    /**
     * 文件恢复
     *
     * @param position
     */
    private void fileRevert(FolderDocumentAdapter adapter, int position) {
        final FolderDocumentEntity item = adapter.getItem(position);
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
        jsonObjectCall.enqueue(new SFileCallBack<JsonObject>() {
            @Override
            public void onSuccess(Call<JsonObject> call, Response<JsonObject> response) {
                dismissLoadingDialog();
                if (response.body().has("success")
                        && response.body().get("success").getAsBoolean()) {
                    getData(true);
                    showTopSnackBar("文件恢复成功");
                } else {
                    showTopSnackBar("文件恢复失败");
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
        if (adapter instanceof FolderDocumentAdapter) {
            switch (view.getId()) {
                case R.id.document_expand_iv:
                    fileRevert((FolderDocumentAdapter) adapter, position);
                    break;
            }
        }
    }
}
