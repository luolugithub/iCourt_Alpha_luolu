package com.icourt.alpha.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.andview.refreshview.XRefreshView;
import com.google.gson.JsonObject;
import com.icourt.alpha.R;
import com.icourt.alpha.adapter.FileInnerShareAdapter;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.adapter.baseadapter.HeaderFooterAdapter;
import com.icourt.alpha.base.BaseFragment;
import com.icourt.alpha.constants.SFileConfig;
import com.icourt.alpha.entity.bean.GroupContactBean;
import com.icourt.alpha.entity.bean.SFileShareUserInfo;
import com.icourt.alpha.fragment.dialogfragment.ContactSelectDialogFragment;
import com.icourt.alpha.http.callback.SFileCallBack;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.callback.SimpleCallBack2;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.interfaces.OnFragmentCallBackListener;
import com.icourt.alpha.utils.ItemDecorationUtils;
import com.icourt.alpha.view.xrefreshlayout.RefreshLayout;
import com.icourt.alpha.widget.dialog.BottomActionDialog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Response;

import static com.icourt.alpha.constants.SFileConfig.PERMISSION_R;
import static com.icourt.alpha.constants.SFileConfig.PERMISSION_RW;

/**
 * Description  内部共享页面
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/8/16
 * version 2.1.0
 */
public class FileInnerShareFragment extends BaseFragment
        implements OnFragmentCallBackListener,
        BaseRecyclerAdapter.OnItemChildClickListener {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.refreshLayout)
    RefreshLayout refreshLayout;
    Unbinder unbinder;
    FileInnerShareAdapter fileInnerShareAdapter;
    HeaderFooterAdapter<FileInnerShareAdapter> headerFooterAdapter;


    protected static final String KEY_SEA_FILE_FROM_REPO_ID = "seaFileFromRepoId";//原仓库id
    protected static final String KEY_SEA_FILE_FROM_DIR_PATH = "seaFileFromDirPath";//原仓库路径
    protected static final String KEY_SEA_FILE_REPO_PERMISSION = "seaFileRepoPermission";//repo的权限

    public static FileInnerShareFragment newInstance(
            String fromRepoId,
            String fromRepoDirPath,
            @SFileConfig.FILE_PERMISSION String repoPermission) {
        FileInnerShareFragment fragment = new FileInnerShareFragment();
        Bundle args = new Bundle();
        args.putString(KEY_SEA_FILE_FROM_REPO_ID, fromRepoId);
        args.putString(KEY_SEA_FILE_FROM_DIR_PATH, fromRepoDirPath);
        args.putString(KEY_SEA_FILE_REPO_PERMISSION, repoPermission);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * repo 的权限
     *
     * @return
     */
    @SFileConfig.FILE_PERMISSION
    protected String getRepoPermission() {
        String stringPermission = getArguments().getString(KEY_SEA_FILE_REPO_PERMISSION, "");
        return SFileConfig.convert2filePermission(stringPermission);
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
        boolean hasEditPermission = TextUtils.equals(getRepoPermission(), PERMISSION_RW);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(ItemDecorationUtils.getCommFullDivider(getContext(), true));
        headerFooterAdapter = new HeaderFooterAdapter<>(fileInnerShareAdapter
                = new FileInnerShareAdapter(hasEditPermission));
        fileInnerShareAdapter.setOnItemChildClickListener(this);
        //有编辑的权限
        if (hasEditPermission) {
            View footerView = HeaderFooterAdapter.inflaterView(getContext(), R.layout.footer_add_attachment, recyclerView);
            TextView attachmentTv = footerView.findViewById(R.id.add_attachment_view);
            if (attachmentTv != null) {
                attachmentTv.setText(R.string.sfile_add_share_member);
            }
            registerClick(attachmentTv);
            headerFooterAdapter.addFooter(footerView);
        }

        recyclerView.setAdapter(headerFooterAdapter);
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
        callEnqueue(getApi().folderSharedUserQuery(
                getArguments().getString(KEY_SEA_FILE_FROM_REPO_ID, ""),
                getArguments().getString(KEY_SEA_FILE_FROM_DIR_PATH, "")),
                new SimpleCallBack<List<SFileShareUserInfo>>() {
                    @Override
                    public void onSuccess(Call<ResEntity<List<SFileShareUserInfo>>> call, Response<ResEntity<List<SFileShareUserInfo>>> response) {
                        fileInnerShareAdapter.bindData(isRefresh, response.body().result);
                        stopRefresh();
                    }

                    @Override
                    public void onFailure(Call<ResEntity<List<SFileShareUserInfo>>> call, Throwable t) {
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

    /**
     * 展示选择成员对话框
     */
    public void showMemberSelectDialogFragment() {
        String tag = ContactSelectDialogFragment.class.getSimpleName();
        FragmentTransaction mFragTransaction = getChildFragmentManager().beginTransaction();
        Fragment fragment = getChildFragmentManager().findFragmentByTag(tag);
        if (fragment != null) {
            mFragTransaction.remove(fragment);
        }
        List<SFileShareUserInfo> data = fileInnerShareAdapter.getData();
        ArrayList<String> uids = new ArrayList<>();
        for (SFileShareUserInfo sFileShareUserInfo : data) {
            if (sFileShareUserInfo == null) continue;
            if (sFileShareUserInfo.userInfo == null) continue;
            uids.add(sFileShareUserInfo.userInfo.userId);
        }
        ContactSelectDialogFragment.newInstanceWithUids(uids, true)
                .show(mFragTransaction, tag);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onFragmentCallBack(Fragment fragment, int type, Bundle params) {
        if (fragment instanceof ContactSelectDialogFragment && params != null) {
            final String permission = params.getString("permission", PERMISSION_RW);
            ArrayList<GroupContactBean> contactBeens = (ArrayList<GroupContactBean>) params.getSerializable(KEY_FRAGMENT_RESULT);
            covertSeaFileUser(permission, contactBeens);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_attachment_view:
                showMemberSelectDialogFragment();
                break;
            default:
                super.onClick(v);
                break;
        }
    }

    /**
     * 分享文件给用户
     *
     * @param permission
     * @param users
     */
    private void shareFile2User(final String permission, List<String> users) {
        if (users == null || users.isEmpty()) return;
        showLoadingDialog(null);
        for (int i = 0; i < users.size(); i++) {
            String s = users.get(i);
            callEnqueue(getSFileApi().folderShareUserPermission(
                    getArguments().getString(KEY_SEA_FILE_FROM_REPO_ID, ""),
                    getArguments().getString(KEY_SEA_FILE_FROM_DIR_PATH, ""),
                    permission,
                    "user",
                    s),
                    new SFileCallBack<JsonObject>() {
                        @Override
                        public void onSuccess(Call<JsonObject> call, Response<JsonObject> response) {
                            dismissLoadingDialog();
                            if (response.body().has("success")) {
                                getData(true);
                            }
                        }

                        @Override
                        public void onFailure(Call<JsonObject> call, Throwable t) {
                            dismissLoadingDialog();
                            super.onFailure(call, t);
                        }
                    });
        }
    }

    /**
     * 转换sfile用户
     *
     * @param permission
     * @param contactBeens
     */
    public void covertSeaFileUser(final String permission, ArrayList<GroupContactBean> contactBeens) {
        if (contactBeens != null && !contactBeens.isEmpty()) {
            StringBuilder uidBuilder = new StringBuilder();
            for (int i = 0; i < contactBeens.size(); i++) {
                GroupContactBean contactBean = contactBeens.get(i);
                if (contactBean == null) continue;
                if (uidBuilder.length() > 0) {
                    uidBuilder.append(",");
                }
                uidBuilder.append(contactBean.userId);
            }
            if (uidBuilder.length() > 0) {
                showLoadingDialog(R.string.sfile_user_transform_to_alpha_user);
                callEnqueue(getApi().sfileUserInfosQuery(uidBuilder.toString()),
                        new SimpleCallBack2<List<String>>() {
                            @Override
                            public void onSuccess(Call<List<String>> call, Response<List<String>> response) {
                                dismissLoadingDialog();
                                shareFile2User(permission, response.body());
                            }

                            @Override
                            public void onFailure(Call<List<String>> call, Throwable t) {
                                super.onFailure(call, t);
                                dismissLoadingDialog();
                            }
                        });
            }
        }
    }


    /**
     * 删除用户共享
     *
     * @param sfileUser
     */
    private void deleteUserSharedFile(String sfileUser) {
        showLoadingDialog(null);
        callEnqueue(getSFileApi().folderShareUserDelete(
                getArguments().getString(KEY_SEA_FILE_FROM_REPO_ID, ""),
                getArguments().getString(KEY_SEA_FILE_FROM_DIR_PATH, ""),
                "user",
                sfileUser),
                new SFileCallBack<JsonObject>() {
                    @Override
                    public void onSuccess(Call<JsonObject> call, Response<JsonObject> response) {
                        dismissLoadingDialog();
                        getData(true);
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        super.onFailure(call, t);
                        dismissLoadingDialog();
                    }
                });
    }

    /**
     * 改变用户分享的权限
     *
     * @param per
     * @param item
     */
    private void changeUserPermission(String per, SFileShareUserInfo item) {
        if (item == null) return;
        if (item.userInfo == null) return;
        if (TextUtils.equals(per, item.permission)) {
            return;
        }
        showLoadingDialog(null);
        callEnqueue(getSFileApi().folderShareUserChangePermission(
                getArguments().getString(KEY_SEA_FILE_FROM_REPO_ID, ""),
                getArguments().getString(KEY_SEA_FILE_FROM_DIR_PATH, ""),
                per,
                "user",
                item.userInfo.name),
                new SFileCallBack<JsonObject>() {
                    @Override
                    public void onSuccess(Call<JsonObject> call, Response<JsonObject> response) {
                        dismissLoadingDialog();
                        getData(true);
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        super.onFailure(call, t);
                        dismissLoadingDialog();
                    }
                });
    }

    @Override
    public void onItemChildClick(BaseRecyclerAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
        final SFileShareUserInfo item = fileInnerShareAdapter.getItem(fileInnerShareAdapter.getRealPos(position));
        if (item == null) return;
        if (item.userInfo == null) return;
        switch (view.getId()) {
            case R.id.user_action_tv:
                showBottomChangePermissionDialog(item);
                break;
        }
    }

    /**
     * 展示底部改变用户权限的对话框
     *
     * @param item
     */
    private void showBottomChangePermissionDialog(final SFileShareUserInfo item) {
        if (item == null) return;
        new BottomActionDialog(getContext(),
                null,
                Arrays.asList(
                        getString(R.string.sfile_permission_rw),
                        getString(R.string.sfile_permission_r),
                        getString(R.string.sfile_delete_share_member)),
                new BottomActionDialog.OnActionItemClickListener() {
                    @Override
                    public void onItemClick(BottomActionDialog dialog, BottomActionDialog.ActionItemAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
                        dialog.dismiss();
                        switch (position) {
                            case 0:
                                changeUserPermission(PERMISSION_RW, item);
                                break;
                            case 1:
                                changeUserPermission(PERMISSION_R, item);
                                break;
                            case 2:
                                deleteUserSharedFile(item.userInfo.name);
                                break;
                        }
                    }
                }).show();
    }
}
