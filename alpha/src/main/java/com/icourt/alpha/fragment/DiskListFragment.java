package com.icourt.alpha.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.andview.refreshview.XRefreshView;
import com.icourt.alpha.R;
import com.icourt.alpha.activity.DiskActionActivity;
import com.icourt.alpha.activity.FolderListActivity;
import com.icourt.alpha.adapter.DocumentAdapter;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.base.BaseFragment;
import com.icourt.alpha.entity.bean.DocumentRootEntity;
import com.icourt.alpha.entity.bean.SFileTokenEntity;
import com.icourt.alpha.http.callback.SimpleCallBack2;
import com.icourt.alpha.utils.ActionConstants;
import com.icourt.alpha.view.xrefreshlayout.RefreshLayout;
import com.icourt.alpha.widget.comparators.ILongFieldEntity;
import com.icourt.alpha.widget.comparators.LongFieldEntityComparator;
import com.icourt.alpha.widget.dialog.AlertListDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Collections;
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
 * date createTime：2017/8/9
 * version 2.1.0
 */
public class DiskListFragment extends BaseFragment implements BaseRecyclerAdapter.OnItemClickListener, BaseRecyclerAdapter.OnItemLongClickListener, BaseRecyclerAdapter.OnItemChildClickListener {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    Unbinder unbinder;
    DocumentAdapter documentAdapter;
    @BindView(R.id.refreshLayout)
    RefreshLayout refreshLayout;
    int pageIndex = 1;

    /**
     * 0： "我的资料库",
     * 1： "共享给我的",
     * 2： "律所资料库",
     * 3： "项目资料库"
     *
     * @param type
     * @return
     */
    public static DiskListFragment newInstance(int type) {
        DiskListFragment documentsListFragment = new DiskListFragment();
        Bundle args = new Bundle();
        args.putInt("type", type);
        documentsListFragment.setArguments(args);
        return documentsListFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(R.layout.fragment_documents_list, inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void initView() {
        EventBus.getDefault().register(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(documentAdapter = new DocumentAdapter());
        documentAdapter.setOnItemClickListener(this);
        documentAdapter.setOnItemChildClickListener(this);
        documentAdapter.setOnItemLongClickListener(this);
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
        switch (getArguments().getInt("type")) {
            case 0: {
                getApi().documentTokenQuery()
                        .enqueue(new SimpleCallBack2<SFileTokenEntity<String>>() {
                            @Override
                            public void onSuccess(Call<SFileTokenEntity<String>> call, Response<SFileTokenEntity<String>> response) {
                                if (TextUtils.isEmpty(response.body().authToken)) {
                                    showTopSnackBar("sfile authToken返回为null");
                                    stopRefresh();
                                    return;
                                }
                                getDocumentRoot(isRefresh, response.body().authToken, null);
                            }

                            @Override
                            public void onFailure(Call<SFileTokenEntity<String>> call, Throwable t) {
                                super.onFailure(call, t);
                                stopRefresh();
                            }
                        });
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
                getApi().documentTokenQuery()
                        .enqueue(new SimpleCallBack2<SFileTokenEntity<String>>() {
                            @Override
                            public void onSuccess(Call<SFileTokenEntity<String>> call, Response<SFileTokenEntity<String>> response) {
                                if (TextUtils.isEmpty(response.body().authToken)) {
                                    showTopSnackBar("sfile authToken返回为null");
                                    stopRefresh();
                                    return;
                                }
                                getDocumentRoot(isRefresh, response.body().authToken, null);
                            }

                            @Override
                            public void onFailure(Call<SFileTokenEntity<String>> call, Throwable t) {
                                super.onFailure(call, t);
                                stopRefresh();
                            }
                        });
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
        getApi().documentTokenQuery()
                .enqueue(new SimpleCallBack2<SFileTokenEntity<String>>() {
                    @Override
                    public void onSuccess(Call<SFileTokenEntity<String>> call, Response<SFileTokenEntity<String>> response) {
                        if (TextUtils.isEmpty(response.body().authToken)) {
                            showTopSnackBar("sfile authToken返回为null");
                            stopRefresh();
                            return;
                        }
                        getDocumentRoot(isRefresh, response.body().authToken, adminId);
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
     * @param sfileToken
     * @param officeAdminId 律所管理员id
     */
    private void getDocumentRoot(final boolean isRefresh, @NonNull String sfileToken, @Nullable String officeAdminId) {
        if (isRefresh) {
            pageIndex = 1;
        }
        String headerToken = String.format("Token %s", sfileToken);
        Call<List<DocumentRootEntity>> listCall = null;
        final int pageSize = ActionConstants.DEFAULT_PAGE_SIZE;
        switch (getArguments().getInt("type")) {
            case 0:
                listCall = getSFileApi().documentRootQuery(headerToken, pageIndex, pageSize, null, null, null);
                break;
            case 1:
                listCall = getSFileApi().documentRootQuery(headerToken, pageIndex, pageSize, officeAdminId, null, "shared");
                break;
            case 2:
                listCall = getSFileApi().documentRootQuery(headerToken);
                break;
            case 3:
                listCall = getSFileApi().documentRootQuery(headerToken, pageIndex, pageSize, null, officeAdminId, "shared");
                break;
        }
        if (listCall != null) {
            listCall.enqueue(new SimpleCallBack2<List<DocumentRootEntity>>() {
                @Override
                public void onSuccess(Call<List<DocumentRootEntity>> call, Response<List<DocumentRootEntity>> response) {
                    stopRefresh();
                    documentAdapter.bindData(isRefresh, response.body());
                    pageIndex += 1;
                    enableLoadMore(response.body());
                }

                @Override
                public void onFailure(Call<List<DocumentRootEntity>> call, Throwable t) {
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
    public void onDocumentRootEvent(DocumentRootEntity documentRootEntity) {
        if (documentRootEntity == null) return;
        if (!documentAdapter.addItem(documentRootEntity)) {
            documentAdapter.updateItem(documentRootEntity);
        }
        List<DocumentRootEntity> data = documentAdapter.getData();
        try {
            Collections.sort(data, new LongFieldEntityComparator<ILongFieldEntity>(LongFieldEntityComparator.ORDER.DESC));
        } catch (Throwable e) {
            e.printStackTrace();
        }
        documentAdapter.notifyDataSetChanged();
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
        lookDetail(position);
    }

    @Override
    public boolean onItemLongClick(BaseRecyclerAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, final View view, final int position) {
        showDocumentActionDialog(position);
        return true;
    }

    /**
     * 展示资料库 操作菜单
     *
     * @param position
     */
    private void showDocumentActionDialog(final int position) {
        new AlertListDialog.ListBuilder(getContext())
                .setDividerColorRes(R.color.alpha_divider_color)
                .setDividerHeightRes(R.dimen.alpha_height_divider)
                .setItems(new String[]{"详细信息", "重命名", "共享", "删除"},
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                switch (i) {
                                    case 0:
                                        lookDetail(position);
                                        break;
                                    case 1:
                                        renameDocument(position);
                                        break;
                                    case 2:
                                        sahreDocument(position);
                                        break;
                                    case 3:
                                        getSfileToken(new SimpleCallBack2<SFileTokenEntity<String>>() {
                                            @Override
                                            public void onSuccess(Call<SFileTokenEntity<String>> call, Response<SFileTokenEntity<String>> response) {
                                                dismissLoadingDialog();
                                                if (TextUtils.isEmpty(response.body().authToken)) {
                                                    showTopSnackBar("sfile authToken返回为null");
                                                    return;
                                                }

                                                delDocument(position, response.body().authToken);
                                            }

                                            @Override
                                            public void onFailure(Call<SFileTokenEntity<String>> call, Throwable t) {
                                                dismissLoadingDialog();
                                                super.onFailure(call, t);
                                            }
                                        });
                                        break;
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
       /* if (pos % 2 == 0) {
            FolderDetailDialogFragment.show("",
                    getChildFragmentManager());
        } else {
            DocumentDetailDialogFragment.show("",
                    getChildFragmentManager());
        }*/
        DocumentRootEntity item = documentAdapter.getItem(pos);
        if (item == null) return;
        FolderListActivity.launch(getContext(),
                item.repo_id,
                item.repo_name,
                "/");
    }

    /**
     * 重命名
     *
     * @param pos
     */
    private void renameDocument(int pos) {
        final DocumentRootEntity item = documentAdapter.getItem(pos);
        if (item == null) return;
        DiskActionActivity.launchUpdateTitle(getContext(), item);
    }

    /**
     * 共享
     *
     * @param pos
     */
    private void sahreDocument(int pos) {

    }


    /**
     * 获取sfile token
     *
     * @param callBack2
     */
    public void getSfileToken(@NonNull SimpleCallBack2<SFileTokenEntity<String>> callBack2) {
        showLoadingDialog("sfileToken获取中...");
        getApi().documentTokenQuery()
                .enqueue(callBack2);
    }

    /**
     * 删除
     *
     * @param pos
     */
    private void delDocument(int pos, String sfileToken) {
        final DocumentRootEntity item = documentAdapter.getItem(pos);
        if (item == null) return;
        showLoadingDialog("资料库删除中...");
        getSFileApi().documentRootDelete(String.format("Token %s", sfileToken), item.repo_id)
                .enqueue(new SimpleCallBack2<String>() {
                    @Override
                    public void onSuccess(Call<String> call, Response<String> response) {
                        dismissLoadingDialog();
                        if (TextUtils.equals("success", response.body())) {
                            showTopSnackBar("资料库删除成功");
                            documentAdapter.removeItem(item);
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
        }
    }
}
