package com.icourt.alpha.fragment;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.asange.recyclerviewadapter.BaseViewHolder;
import com.asange.recyclerviewadapter.OnItemChildClickListener;
import com.asange.recyclerviewadapter.OnItemClickListener;
import com.asange.recyclerviewadapter.OnItemLongClickListener;
import com.icourt.alpha.R;
import com.icourt.alpha.activity.FolderListActivity;
import com.icourt.alpha.activity.RepoRenameActivity;
import com.icourt.alpha.adapter.RepoAdapter;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.adapter.baseadapter.adapterObserver.DataChangeAdapterObserver;
import com.icourt.alpha.constants.SFileConfig;
import com.icourt.alpha.entity.bean.DefaultRepoEntity;
import com.icourt.alpha.entity.bean.RepoEntity;
import com.icourt.alpha.fragment.dialogfragment.RepoDetailsDialogFragment;
import com.icourt.alpha.http.callback.SFileCallBack;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.callback.SimpleCallBack2;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.http.observer.BaseObserver;
import com.icourt.alpha.utils.ActionConstants;
import com.icourt.alpha.utils.DateUtils;
import com.icourt.alpha.utils.DensityUtil;
import com.icourt.alpha.utils.StringUtils;
import com.icourt.alpha.widget.dialog.BottomActionDialog;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadmoreListener;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.HttpException;
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
public class RepoListFragment extends RepoBaseFragment implements OnItemClickListener, OnItemChildClickListener, OnItemLongClickListener {
    private static final String KEY_REPO_TYPE = "repoType";

    @BindView(R.id.recyclerView)
    @Nullable
    RecyclerView recyclerView;
    Unbinder unbinder;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    int pageIndex = 1;
    String defaultRopoId;
    int repoType;

    RepoAdapter repoAdapter;
    @BindView(R.id.contentEmptyImage)
    ImageView contentEmptyImage;
    @BindView(R.id.contentEmptyText)
    TextView contentEmptyText;
    @BindView(R.id.empty_layout)
    LinearLayout emptyLayout;


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
        View view = super.onCreateView(R.layout.layout_refresh_recyclerview5, inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void initView() {
        repoType = SFileConfig.convert2RepoType(getArguments().getInt(KEY_REPO_TYPE));

        recyclerView.setClipToPadding(false);
        recyclerView.setPadding(0, DensityUtil.dip2px(getContext(), 20), 0, 0);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        switch (repoType) {
            case REPO_MINE:
                contentEmptyText.setText(R.string.empty_list_repo_my);
                break;
            case REPO_SHARED_ME:
                contentEmptyText.setText(R.string.empty_list_repo_shared);
                break;
            case REPO_LAWFIRM:
                contentEmptyText.setText(R.string.empty_list_repo_lawyer);
                break;
            case REPO_PROJECT:
                contentEmptyText.setText(R.string.empty_list_repo_project);
                break;
            default:
                contentEmptyText.setText(R.string.empty_list_repo_project);
                break;
        }
        recyclerView.setAdapter(repoAdapter = new RepoAdapter(repoType));
        repoAdapter.registerAdapterDataObserver(new DataChangeAdapterObserver() {
            @Override
            protected void updateUI() {
                if (emptyLayout != null) {
                    emptyLayout.setVisibility(repoAdapter.getItemCount() > 0 ? View.GONE : View.VISIBLE);
                }
            }
        });

        repoAdapter.setOnItemClickListener(this);
        repoAdapter.setOnItemChildClickListener(this);
        repoAdapter.setOnItemLongClickListener(this);
        refreshLayout.setOnRefreshLoadmoreListener(new OnRefreshLoadmoreListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                getData(true);
            }

            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                getData(false);
            }
        });
        refreshLayout.autoRefresh();
    }

    private void enableLoadMore(List result) {
        if (refreshLayout != null) {
            refreshLayout.setEnableLoadmore(result != null
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
            default:
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
        Observable<List<RepoEntity>> listCall = null;
        final int pageSize = ActionConstants.DEFAULT_PAGE_SIZE;
        switch (repoType) {
            case REPO_MINE:
                listCall = getSFileApi().documentRootQueryObservable(pageIndex, pageSize, null, null, null);
                break;
            case REPO_SHARED_ME:
                listCall = getSFileApi().documentRootQueryObservable(pageIndex, pageSize, officeAdminId, null, "shared");
                break;
            case REPO_LAWFIRM:
                listCall = sendObservable(getApi().getOfficeLibsObservable(null));
                break;
            case REPO_PROJECT:
                listCall = getSFileApi().documentRootQueryObservable(pageIndex, pageSize, null, officeAdminId, "shared");
                break;
            default:
                break;
        }
        if (listCall == null) {
            return;
        }
        listCall.flatMap(new Function<List<RepoEntity>, ObservableSource<List<RepoEntity>>>() {
            @Override
            public ObservableSource<List<RepoEntity>> apply(@NonNull List<RepoEntity> repoEntities) throws Exception {
                return saveEncryptedData(repoEntities);
            }
        }).compose(this.<List<RepoEntity>>bindToLifecycle())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<List<RepoEntity>>() {
                    @Override
                    public void onNext(@NonNull List<RepoEntity> repoEntities) {
                        stopRefresh();
                        repoAdapter.bindData(isRefresh, repoEntities);
                        pageIndex += 1;
                        enableLoadMore(repoEntities);
                    }

                    @Override
                    public void onError(@NonNull Throwable throwable) {
                        super.onError(throwable);
                        stopRefresh();
                    }

                    @Override
                    public void onComplete() {
                        super.onComplete();
                        stopRefresh();
                        //数据有问题
                        repoAdapter.notifyDataSetChanged();
                    }
                });
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


    /**
     * 展示资料库解锁的对话框
     *
     * @param item
     */
    private void showDecryptDialog(final RepoEntity item) {
        new AlertDialog.Builder(getContext())
                .setTitle(R.string.repo_encrypted)
                .setMessage(R.string.repo_encrypted_unsupport)
                .setPositiveButton(R.string.str_good, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .show();

        // 暂时不解密
        /*View dialogView = View.inflate(getContext(), R.layout.dialog_repo_decriypt, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                .setView(dialogView)
                .create();
        final ClearEditText repoPwdEt = dialogView.findViewById(R.id.repo_pwd_et);
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.cancel_tv:
                        alertDialog.dismiss();
                        break;
                    case R.id.ok_tv: {
                        if (StringUtils.isEmpty(repoPwdEt.getText())) {
                            showToast("密码不能为空");
                            return;
                        }
                        decryptRepo(alertDialog, item, repoPwdEt.getText().toString());
                    }
                    break;
                }
            }
        };
        dialogView.findViewById(R.id.cancel_tv).setOnClickListener(onClickListener);
        dialogView.findViewById(R.id.ok_tv).setOnClickListener(onClickListener);
        alertDialog.show();*/
    }

    /**
     * 解密资料库
     *
     * @param alertDialog
     * @param item
     * @param pwd
     */
    private void decryptRepo(@Nullable final AlertDialog alertDialog, final RepoEntity item, String pwd) {
        if (item == null) {
            return;
        }
        showLoadingDialog(null);
        callEnqueue(
                getSFileApi().repoDecrypt(item.repo_id, pwd),
                new SimpleCallBack2<String>() {
                    @Override
                    public void onSuccess(Call<String> call, Response<String> response) {
                        dismissLoadingDialog();
                        if (alertDialog != null) {
                            alertDialog.dismiss();
                        }
                        if (StringUtils.equalsIgnoreCase("success", response.body(), false)) {
                            //1.更新适配器
                            item.decryptMillisecond = DateUtils.millis();
                            repoAdapter.updateItem(item);

                            //2.更新本地
                            updateEncryptedRecord(item.repo_id, item.encrypted, item.decryptMillisecond);

                            //3.进入到文件目录列表
                            lookFolderList(item);
                        } else {
                            bugSync("资料库解密返回失败", response.body());
                            showTopSnackBar("解密返回失败:" + response.body());
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        dismissLoadingDialog();
                        if (t instanceof HttpException &&
                                ((HttpException) t).code() == 400) {
                            showToast("密码错误");
                        } else {
                            super.onFailure(call, t);
                        }
                    }
                });
    }


    /**
     * 查看文档目录子文件列表
     *
     * @param item
     */
    private void lookFolderList(RepoEntity item) {
        if (item == null) {
            return;
        }
        String repo_permission = item.permission;
        if (repoType == REPO_MINE) {
            repo_permission = PERMISSION_RW;
        }
        FolderListActivity.launch(getContext(),
                SFileConfig.convert2RepoType(getArguments().getInt(KEY_REPO_TYPE, 0)),
                SFileConfig.convert2filePermission(repo_permission),
                item.repo_id,
                item.repo_name,
                "/",
                item.encrypted);
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
        if (item == null) {
            return;
        }
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
                        if (TextUtils.equals(s, getString(R.string.repo_manage))) {//查看资料库详情
                            lookDetail(pos);
                        } else if (TextUtils.equals(s, getString(R.string.str_rename))) {//重命名
                            renameDocument(pos);
                        } else if (TextUtils.equals(s, getString(R.string.repo_share))) {//共享
                            shareDocument(pos);
                        } else if (TextUtils.equals(s, getString(R.string.str_delete))) {//删除
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
        if (item == null) {
            return;
        }
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
        if (item == null) {
            return;
        }
        RepoRenameActivity.launch(getContext(), item);
    }

    /**
     * 共享
     *
     * @param pos
     */
    private void shareDocument(int pos) {
        final RepoEntity item = repoAdapter.getItem(pos);
        if (item == null) {
            return;
        }
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
        AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                .setMessage(R.string.repo_delete_confirm)
                .setPositiveButton(R.string.str_delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        delDocument(pos);
                    }
                })
                .setNegativeButton(R.string.str_cancel, null)
                .create();
        alertDialog.show();
        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.GRAY);
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED);
    }

    /**
     * 删除
     *
     * @param pos
     */
    private void delDocument(int pos) {
        final RepoEntity item = repoAdapter.getItem(pos);
        if (item == null) {
            return;
        }
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
    public void onItemClick(com.asange.recyclerviewadapter.BaseRecyclerAdapter baseRecyclerAdapter, BaseViewHolder baseViewHolder, View view, int i) {
        RepoEntity item = repoAdapter.getItem(i);
        if (item == null) {
            return;
        }
        if (item.isNeedDecrypt()) {
            showDecryptDialog(item);
            return;
        }
        lookFolderList(item);
    }

    @Override
    public void onItemChildClick(com.asange.recyclerviewadapter.BaseRecyclerAdapter baseRecyclerAdapter, BaseViewHolder baseViewHolder, View view, int i) {
        RepoEntity item = repoAdapter.getItem(i);
        if (item == null) {
            return;
        }
        if (item.isNeedDecrypt()) {
            showDecryptDialog(item);
            return;
        }
        switch (view.getId()) {
            case R.id.document_expand_iv:
                showDocumentActionDialog(i);
                break;
            case R.id.document_detail_iv:
                lookDetail(i);
                break;
        }
    }

    @Override
    public boolean onItemLongClick(com.asange.recyclerviewadapter.BaseRecyclerAdapter baseRecyclerAdapter, BaseViewHolder baseViewHolder, View view, int i) {
        RepoEntity item = repoAdapter.getItem(i);
        if (item != null && item.isNeedDecrypt()) {
            showDecryptDialog(item);
        } else {
            if (repoType == 0) {
                showDocumentActionDialog(i);
            }
        }
        return true;
    }
}
