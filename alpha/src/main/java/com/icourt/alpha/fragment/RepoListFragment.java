package com.icourt.alpha.fragment;

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
import com.icourt.alpha.R;
import com.icourt.alpha.activity.FolderListActivity;
import com.icourt.alpha.activity.RepoRenameActivity;
import com.icourt.alpha.adapter.RepoAdapter;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.adapter.baseadapter.HeaderFooterAdapter;
import com.icourt.alpha.adapter.baseadapter.adapterObserver.RefreshViewEmptyObserver;
import com.icourt.alpha.base.BaseFragment;
import com.icourt.alpha.constants.SFileConfig;
import com.icourt.alpha.entity.bean.DefaultRepoEntity;
import com.icourt.alpha.entity.bean.RepoEntity;
import com.icourt.alpha.entity.bean.SFileTokenEntity;
import com.icourt.alpha.fragment.dialogfragment.RepoDetailsDialogFragment;
import com.icourt.alpha.http.callback.SFileCallBack;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.callback.SimpleCallBack2;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.utils.ActionConstants;
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
import static com.icourt.alpha.constants.SFileConfig.REPO_MINE;

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
    HeaderFooterAdapter<RepoAdapter> headerFooterAdapter;
    TextView footer_textview;

    /**
     * @param type
     * @return
     */
    public static RepoListFragment newInstance(@SFileConfig.REPO_TYPE int type) {
        RepoListFragment documentsListFragment = new RepoListFragment();
        Bundle args = new Bundle();
        args.putInt("repoType", type);
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
        repoType = getArguments().getInt("repoType");
        EventBus.getDefault().register(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        headerFooterAdapter = new HeaderFooterAdapter<>(repoAdapter = new RepoAdapter(repoType));
        footer_textview = (TextView) HeaderFooterAdapter.inflaterView(getContext(), R.layout.footer_textview, recyclerView);
        headerFooterAdapter.addFooter(footer_textview);
        repoAdapter.registerAdapterDataObserver(new RefreshViewEmptyObserver(refreshLayout, repoAdapter) {
            @Override
            protected void updateUI() {
                super.updateUI();
                if (footer_textview != null) {
                    footer_textview.setText(String.format("%s个资料库", repoAdapter.getItemCount()));
                }
            }
        });
        recyclerView.setAdapter(headerFooterAdapter);

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
        getApi().repoDefaultQuery()
                .enqueue(new SimpleCallBack<DefaultRepoEntity>() {
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
            case 0: {
                getDocumentRoot(isRefresh, null);
            }
            break;
            case 1: {
                //获取管理员账号
                getApi().getOfficeAdmin(getLoginUserId())
                        .enqueue(new SimpleCallBack2<String>() {
                            @Override
                            public void onSuccess(Call<String> call, Response<String> response) {
                                getSfileTokenAndgetDocument(isRefresh, response.body());
                            }

                            @Override
                            public void onFailure(Call<String> call, Throwable t) {
                                super.onFailure(call, t);
                                stopRefresh();
                            }
                        });
            }
            break;
            case 2: {
                getDocumentRoot(isRefresh, null);
            }
            break;
            case 3: {
                //获取管理员账号
                getApi().getOfficeAdmin(getLoginUserId())
                        .enqueue(new SimpleCallBack2<String>() {
                            @Override
                            public void onSuccess(Call<String> call, Response<String> response) {
                                getSfileTokenAndgetDocument(isRefresh, response.body());
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
     * 获取token 并且 获取文档列表
     *
     * @param isRefresh
     */
    private void getSfileTokenAndgetDocument(final boolean isRefresh, final String adminId) {
        getApi().sFileTokenQuery()
                .enqueue(new SimpleCallBack2<SFileTokenEntity<String>>() {
                    @Override
                    public void onSuccess(Call<SFileTokenEntity<String>> call, Response<SFileTokenEntity<String>> response) {
                        if (TextUtils.isEmpty(response.body().authToken)) {
                            showTopSnackBar("sfile authToken返回为null");
                            stopRefresh();
                            return;
                        }
                        getDocumentRoot(isRefresh, adminId);
                    }

                    @Override
                    public void onFailure(Call<SFileTokenEntity<String>> call, Throwable t) {
                        super.onFailure(call, t);
                        stopRefresh();
                    }
                });
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
        if (listCall != null) {
            listCall.enqueue(new SFileCallBack<List<RepoEntity>>() {
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
        List<String> menus = Arrays.asList("详细信息", "重命名", "共享", "删除");
        if (isDefaultReop(item.repo_id)) {
            menus = Arrays.asList("详细信息", "重命名", "共享");
        }
        new BottomActionDialog(getContext(),
                null,
                menus,
                new BottomActionDialog.OnActionItemClickListener() {
                    @Override
                    public void onItemClick(BottomActionDialog dialog, BottomActionDialog.ActionItemAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
                        dialog.dismiss();
                        String s = adapter.getItem(position);
                        if (TextUtils.equals(s, "详细信息")) {
                            lookDetail(pos);
                        } else if (TextUtils.equals(s, "重命名")) {
                            renameDocument(pos);
                        } else if (TextUtils.equals(s, "共享")) {
                            shareDocument(pos);
                        } else if (TextUtils.equals(s, "删除")) {
                            showDelDialog(pos);
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


    private void showDelDialog(final int pos) {
        new BottomActionDialog(getContext(),
                "删除后不可恢复,确定要删除吗?",
                Arrays.asList("删除"), new BottomActionDialog.OnActionItemClickListener() {
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
        showLoadingDialog("资料库删除中...");
        getSFileApi().documentRootDelete(item.repo_id)
                .enqueue(new SFileCallBack<String>() {
                    @Override
                    public void onSuccess(Call<String> call, Response<String> response) {
                        dismissLoadingDialog();
                        if (TextUtils.equals("success", response.body())) {
                            showTopSnackBar("资料库删除成功");
                            repoAdapter.removeItem(item);
                        } else {
                            showTopSnackBar("资料库删除失败");
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
