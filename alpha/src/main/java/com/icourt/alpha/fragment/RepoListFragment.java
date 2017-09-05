package com.icourt.alpha.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.andview.refreshview.XRefreshView;
import com.icourt.alpha.R;
import com.icourt.alpha.activity.FolderListActivity;
import com.icourt.alpha.activity.RepoRenameActivity;
import com.icourt.alpha.adapter.RepoAdapter;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.adapter.baseadapter.adapterObserver.RefreshViewEmptyObserver;
import com.icourt.alpha.base.BaseFragment;
import com.icourt.alpha.constants.SFileConfig;
import com.icourt.alpha.entity.bean.DefaultRepoEntity;
import com.icourt.alpha.entity.bean.RepoEntity;
import com.icourt.alpha.fragment.dialogfragment.RepoDetailsDialogFragment;
import com.icourt.alpha.http.callback.SFileCallBack;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.callback.SimpleCallBack2;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.utils.ActionConstants;
import com.icourt.alpha.utils.DensityUtil;
import com.icourt.alpha.utils.StringUtils;
import com.icourt.alpha.view.xrefreshlayout.RefreshLayout;
import com.icourt.alpha.widget.dialog.BottomActionDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Response;

import static com.icourt.alpha.constants.SFileConfig.PERMISSION_RW;
import static com.icourt.alpha.constants.SFileConfig.REPO_LAWFIRM;
import static com.icourt.alpha.constants.SFileConfig.REPO_MINE;
import static com.icourt.alpha.constants.SFileConfig.REPO_PROJECT;
import static com.icourt.alpha.constants.SFileConfig.REPO_SHARED_ME;

/**
 * Description  资料库列表
 * 只有我的资料库 长按才有效
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/8/9
 * version 2.1.0
 */
public class RepoListFragment extends BaseFragment
        implements BaseRecyclerAdapter.OnItemClickListener,
        BaseRecyclerAdapter.OnItemLongClickListener,
        BaseRecyclerAdapter.OnItemChildClickListener {
    private static final String KEY_REPO_TYPE = "repoType";

    @BindView(R.id.recyclerView)
    @Nullable
    RecyclerView recyclerView;
    Unbinder unbinder;
    @BindView(R.id.refreshLayout)
    RefreshLayout refreshLayout;
    int pageIndex = 1;
    String defaultRopoId;
    int repoType;

    RepoAdapter repoAdapter;

    /**
     * @param type
     * @return
     */
    public static RepoListFragment newInstance(@SFileConfig.REPO_TYPE int type) {
        RepoListFragment documentsListFragment = new RepoListFragment();
        Bundle args = new Bundle();
        args.putInt(KEY_REPO_TYPE, type);
        documentsListFragment.setArguments(args);
        return documentsListFragment;
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
        repoType = SFileConfig.convert2RepoType(getArguments().getInt(KEY_REPO_TYPE));
        EventBus.getDefault().register(this);

        recyclerView.setClipToPadding(false);
        recyclerView.setPadding(0, DensityUtil.dip2px(getContext(), 20), 0, 0);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        switch (repoType) {
            case REPO_MINE:
                refreshLayout.setNoticeEmptyText(R.string.repo_empty);
                break;
            case REPO_SHARED_ME:
                refreshLayout.setNoticeEmptyText(R.string.repo_share_empty);
                break;
            case REPO_LAWFIRM:
                refreshLayout.setNoticeEmptyText(R.string.repo_lawfirm_empty);
                break;
            case REPO_PROJECT:
                refreshLayout.setNoticeEmptyText(R.string.repo_empty);
                break;
        }
        recyclerView.setAdapter(repoAdapter = new RepoAdapter(repoType));
        repoAdapter.registerAdapterDataObserver(new RefreshViewEmptyObserver(refreshLayout, repoAdapter) {
            @Override
            protected void updateUI() {
                super.updateUI();
                if (refreshLayout != null) {
                    refreshLayout.enableEmptyView(repoAdapter.getItemCount() <= 0);
                }
            }
        });

        repoAdapter.setOnItemClickListener(this);
        repoAdapter.setOnItemChildClickListener(this);
        repoAdapter.setOnItemLongClickListener(this);
        refreshLayout.setXRefreshViewListener(new XRefreshView.SimpleXRefreshListener() {
            @Override
            public void onLoadMore(boolean isSilence) {
                super.onLoadMore(isSilence);
                getData(false);
            }

            @Override
            public void onRefresh(boolean isPullDown) {
                super.onRefresh(isPullDown);
                getData(true);
            }
        });
        refreshLayout.startRefresh();
    }

    private void enableLoadMore(List result) {
        if (refreshLayout != null) {
            refreshLayout.setPullLoadEnable(result != null
                    && result.size() >= ActionConstants.DEFAULT_PAGE_SIZE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getData(true);
    }

    /**
     * 0： "我的资料库",
     * 1： "共享给我的",
     * 2： "律所资料库",
     * 3： "项目资料库"
     *
     * @param isRefresh 是否刷新
     */
    @Override
    protected void getData(final boolean isRefresh) {
        super.getData(isRefresh);
        callEnqueue(getApi().repoDefaultQuery(),
                new SimpleCallBack<DefaultRepoEntity>() {
                    @Override
                    public void onSuccess(Call<ResEntity<DefaultRepoEntity>> call, Response<ResEntity<DefaultRepoEntity>> response) {
                        if (response.body().result != null) {
                            defaultRopoId = response.body().result.repoId;
                            getRepoList(isRefresh);
                        } else {
                            stopRefresh();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResEntity<DefaultRepoEntity>> call, Throwable t) {
                        super.onFailure(call, t);
                        stopRefresh();
                    }
                });
    }

    private void getRepoList(final boolean isRefresh) {
        switch (repoType) {
            case REPO_MINE: {
                getDocumentRoot(isRefresh, null);
            }
            break;
            case REPO_SHARED_ME: {
                //获取管理员账号
                callEnqueue(getApi().getOfficeAdmin(getLoginUserId()),
                        new SimpleCallBack2<String>() {
                            @Override
                            public void onSuccess(Call<String> call, Response<String> response) {
                                getDocumentRoot(isRefresh, response.body());
                            }

                            @Override
                            public void onFailure(Call<String> call, Throwable t) {
                                super.onFailure(call, t);
                                stopRefresh();
                            }
                        });
            }
            break;
            case REPO_LAWFIRM: {
                getDocumentRoot(isRefresh, null);
            }
            break;
            case REPO_PROJECT: {
                //获取管理员账号
                callEnqueue(getApi().getOfficeAdmin(getLoginUserId()),
                        new SimpleCallBack2<String>() {
                            @Override
                            public void onSuccess(Call<String> call, Response<String> response) {
                                getDocumentRoot(isRefresh, response.body());
                            }

                            @Override
                            public void onFailure(Call<String> call, Throwable t) {
                                super.onFailure(call, t);
                                stopRefresh();
                            }
                        });
            }
            break;
        }
    }

    /**
     * 获取资料库
     *
     * @param isRefresh
     * @param officeAdminId 律所管理员id
     */
    private void getDocumentRoot(final boolean isRefresh, @Nullable String officeAdminId) {
        if (isRefresh) {
            pageIndex = 1;
        }
        Call<List<RepoEntity>> listCall = null;
        final int pageSize = ActionConstants.DEFAULT_PAGE_SIZE;
        switch (repoType) {
            case 0:
                listCall = getSFileApi().documentRootQuery(pageIndex, pageSize, null, null, null);
                break;
            case 1:
                listCall = getSFileApi().documentRootQuery(pageIndex, pageSize, officeAdminId, null, "shared");
                break;
            case 2:
                listCall = getSFileApi().documentRootQuery();
                break;
            case 3:
                listCall = getSFileApi().documentRootQuery(pageIndex, pageSize, null, officeAdminId, "shared");
                break;
        }
        callEnqueue(listCall, new SFileCallBack<List<RepoEntity>>() {
            @Override
            public void onSuccess(Call<List<RepoEntity>> call, Response<List<RepoEntity>> response) {
                stopRefresh();
                repoAdapter.bindData(isRefresh, response.body());
                pageIndex += 1;
                enableLoadMore(response.body());
            }

            @Override
            public void onFailure(Call<List<RepoEntity>> call, Throwable t) {
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDocumentRootEvent(RepoEntity repoEntity) {
        if (repoEntity == null) return;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public void onItemClick(BaseRecyclerAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
        RepoEntity item = repoAdapter.getItem(position);
        if (item == null) return;
        String repo_permission = item.permission;
        if (repoType == REPO_MINE) {
            repo_permission = PERMISSION_RW;
        }
        FolderListActivity.launch(getContext(),
                SFileConfig.convert2RepoType(getArguments().getInt("repoType", 0)),
                SFileConfig.convert2filePermission(repo_permission),
                item.repo_id,
                item.repo_name,
                "/");
    }

    @Override
    public boolean onItemLongClick(BaseRecyclerAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, final View view, final int position) {
        if (repoType == 0) {
            showDocumentActionDialog(position);
        }
        return true;
    }

    private boolean isDefaultReop(String repo_id) {
        return StringUtils.equalsIgnoreCase(repo_id, defaultRopoId, false);
    }

    /**
     * 展示资料库 操作菜单
     *
     * @param pos
     */
    private void showDocumentActionDialog(final int pos) {
        RepoEntity item = repoAdapter.getItem(pos);
        if (item == null) return;
        List<String> menus = Arrays.asList(getResources().getStringArray(R.array.repo_action_menus_rw_array));
        if (isDefaultReop(item.repo_id)) {
            menus = Arrays.asList(getResources().getStringArray(R.array.repo_action_menus_r_array));
        }
        new BottomActionDialog(getContext(),
                null,
                menus,
                new BottomActionDialog.OnActionItemClickListener() {
                    @Override
                    public void onItemClick(BottomActionDialog dialog, BottomActionDialog.ActionItemAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
                        dialog.dismiss();
                        String s = adapter.getItem(position);
                        if (TextUtils.equals(s, "查看资料库详情")) {
                            lookDetail(pos);
                        } else if (TextUtils.equals(s, "重命名")) {
                            renameDocument(pos);
                        } else if (TextUtils.equals(s, "内部共享")) {
                            shareDocument(pos);
                        } else if (TextUtils.equals(s, "删除")) {
                            showDelConfirmDialog(pos);
                        }
                    }
                }).show();
    }

    /**
     * 查看详情
     *
     * @param pos
     */
    private void lookDetail(int pos) {
        RepoEntity item = repoAdapter.getItem(pos);
        if (item == null) return;
        if (repoType == REPO_MINE) {
            item.permission = PERMISSION_RW;
        }
        RepoDetailsDialogFragment.show(
                SFileConfig.convert2RepoType(repoType),
                item.repo_id,
                0,
                item.permission,
                getChildFragmentManager());
    }

    /**
     * 重命名
     *
     * @param pos
     */
    private void renameDocument(int pos) {
        final RepoEntity item = repoAdapter.getItem(pos);
        if (item == null) return;
        RepoRenameActivity.launch(getContext(), item);
    }

    /**
     * 共享
     *
     * @param pos
     */
    private void shareDocument(int pos) {
        final RepoEntity item = repoAdapter.getItem(pos);
        if (item == null) return;
        if (repoType == REPO_MINE) {
            item.permission = PERMISSION_RW;
        }
        RepoDetailsDialogFragment.show(
                SFileConfig.convert2RepoType(repoType),
                item.repo_id,
                1,
                item.permission,
                getChildFragmentManager());
    }


    /**
     * 删除确认对话框
     *
     * @param pos
     */
    private void showDelConfirmDialog(final int pos) {
        new BottomActionDialog(getContext(),
                getString(R.string.repo_delete_confirm),
                Arrays.asList(getString(R.string.str_delete)), new BottomActionDialog.OnActionItemClickListener() {
            @Override
            public void onItemClick(BottomActionDialog dialog, BottomActionDialog.ActionItemAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
                dialog.dismiss();
                delDocument(pos);
            }
        }).show();
    }

    /**
     * 删除
     *
     * @param pos
     */
    private void delDocument(int pos) {
        final RepoEntity item = repoAdapter.getItem(pos);
        if (item == null) return;
        showLoadingDialog(R.string.repo_delete_ing);
        callEnqueue(getSFileApi().documentRootDelete(item.repo_id),
                new SFileCallBack<String>() {
                    @Override
                    public void onSuccess(Call<String> call, Response<String> response) {
                        dismissLoadingDialog();
                        if (TextUtils.equals("success", response.body())) {
                            showTopSnackBar(R.string.repo_delete_success);
                            repoAdapter.removeItem(item);
                        } else {
                            showTopSnackBar(R.string.repo_delete_fail);
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        dismissLoadingDialog();
                        super.onFailure(call, t);
                    }
                });
    }

    @Override
    public void onItemChildClick(BaseRecyclerAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
        switch (view.getId()) {
            case R.id.document_expand_iv:
                showDocumentActionDialog(position);
                break;
            case R.id.document_detail_iv:
                lookDetail(position);
                break;
        }
    }
}
