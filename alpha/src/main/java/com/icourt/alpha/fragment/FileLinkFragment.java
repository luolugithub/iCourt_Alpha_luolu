package com.icourt.alpha.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.icourt.alpha.R;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.base.BaseFragment;
import com.icourt.alpha.constants.SFileConfig;
import com.icourt.alpha.entity.bean.SFileLinkInfoEntity;
import com.icourt.alpha.http.callback.SFileCallBack;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.utils.DateUtils;
import com.icourt.alpha.utils.SystemUtils;
import com.icourt.alpha.widget.dialog.BottomActionDialog;
import com.icourt.api.RequestUtils;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/8/15
 * version 2.1.0
 */
public class FileLinkFragment extends BaseFragment {

    protected static final String KEY_SEA_FILE_FROM_REPO_ID = "seaFileFromRepoId";//原仓库id
    protected static final String KEY_SEA_FILE_FROM_FILE_PATH = "seaFileFromFilePath";//原文件路径
    protected static final String KEY_SEA_FILE_LINK_TYPE = "seaFileLinkType";//文件链接类型
    @BindView(R.id.file_access_pwd_tv)
    TextView fileAccessPwdTv;
    @BindView(R.id.file_access_time_limit_tv)
    TextView fileAccessTimeLimitTv;
    Unbinder unbinder;
    @BindView(R.id.file_share_link_tv)
    TextView fileShareLinkTv;
    @BindView(R.id.link_copy_tv)
    TextView linkCopyTv;
    SFileLinkInfoEntity sFileLinkInfoEntity;
    @BindView(R.id.file_link_create_tv)
    TextView fileLinkCreateTv;
    @BindView(R.id.file_share_link_title_tv)
    TextView fileShareLinkTitleTv;
    protected static final String KEY_SEA_FILE_REPO_PERMISSION = "seaFileRepoPermission";//repo的权限
    @BindView(R.id.file_link_delete_tv)
    TextView fileLinkDeleteTv;

    /**
     * @param fromRepoId
     * @param fromRepoFilePath
     * @param linkType         分享类型 0下载 1上传
     * @return
     */
    public static FileLinkFragment newInstance(
            String fromRepoId,
            String fromRepoFilePath,
            int linkType,
            @SFileConfig.FILE_PERMISSION String repoPermission) {
        FileLinkFragment fragment = new FileLinkFragment();
        Bundle args = new Bundle();
        args.putString(KEY_SEA_FILE_FROM_REPO_ID, fromRepoId);
        args.putString(KEY_SEA_FILE_FROM_FILE_PATH, fromRepoFilePath);
        args.putInt(KEY_SEA_FILE_LINK_TYPE, linkType);
        args.putString(KEY_SEA_FILE_REPO_PERMISSION, repoPermission);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(R.layout.fragment_file_link, inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        return view;
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

    @Override
    protected void initView() {
        getData(true);
    }

    @Override
    protected void getData(boolean isRefresh) {
        super.getData(isRefresh);
        callEnqueue(getApi().fileShareLinkQuery(
                getArguments().getString(KEY_SEA_FILE_FROM_REPO_ID, ""),
                getArguments().getString(KEY_SEA_FILE_FROM_FILE_PATH, ""),
                getArguments().getInt(KEY_SEA_FILE_LINK_TYPE)),
                new SFileCallBack<SFileLinkInfoEntity>() {
                    @Override
                    public void onSuccess(Call<SFileLinkInfoEntity> call, Response<SFileLinkInfoEntity> response) {
                        updateUI(response.body());
                    }
                });
    }

    private void updateUI(SFileLinkInfoEntity data) {
        sFileLinkInfoEntity = data;
        if (linkCopyTv == null) return;
        if (!isNoFileShareLink()) {
            linkCopyTv.setVisibility(View.VISIBLE);
            fileLinkCreateTv.setVisibility(View.GONE);
            fileLinkDeleteTv.setVisibility(View.VISIBLE);

            if (getArguments().getInt(KEY_SEA_FILE_LINK_TYPE) == 0) {
                fileShareLinkTitleTv.setText("已生成下载链接");
                fileLinkDeleteTv.setText("删除下载链接");
            } else {
                fileShareLinkTitleTv.setText("已生成上传链接");
                fileLinkDeleteTv.setText("删除上传链接");
            }
            fileAccessPwdTv.setText(sFileLinkInfoEntity.isNeedAccessPwd() ? sFileLinkInfoEntity.password : "不需要");
            fileAccessTimeLimitTv.setText(sFileLinkInfoEntity.expireTime <= 0 ? "永不过期" : DateUtils.getyyyy_MM_dd(sFileLinkInfoEntity.expireTime));
            fileShareLinkTv.setText(sFileLinkInfoEntity.getRealShareLink());
        } else {
            fileLinkCreateTv.setVisibility(View.VISIBLE);
            fileLinkDeleteTv.setVisibility(View.GONE);

            fileAccessTimeLimitTv.setText("7天有效期");
            fileShareLinkTv.setText("");
            fileAccessPwdTv.setText("自动生成");
            if (getArguments().getInt(KEY_SEA_FILE_LINK_TYPE) == 0) {
                fileShareLinkTitleTv.setText("生成下载链接");
            } else {
                fileShareLinkTitleTv.setText("生成上传链接");
            }
            linkCopyTv.setVisibility(View.GONE);
        }
    }

    @OnClick({R.id.link_copy_tv,
            R.id.file_link_create_tv,
            R.id.file_access_pwd_tv,
            R.id.file_access_time_limit_tv,
            R.id.file_link_delete_tv,
    })
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.link_copy_tv:
                copyFileShareLink();
                break;
            case R.id.file_link_create_tv:
                createFileShareLink();
                break;
            case R.id.file_access_pwd_tv:
                if (isNoFileShareLink()) {
                    showSelectPwdType();
                }
                break;
            case R.id.file_access_time_limit_tv:
                if (isNoFileShareLink()) {
                    showSelectTimeLimit();
                }
                break;
            case R.id.file_link_delete_tv:
                showDeleteFileConfirmDialog();
                break;
            default:
                super.onClick(v);
                break;
        }
    }

    private void showDeleteFileConfirmDialog() {
        if (sFileLinkInfoEntity == null) return;
        new BottomActionDialog(getContext(),
                "删除链接会导致已分享的链接失效",
                Arrays.asList("删除"),
                new BottomActionDialog.OnActionItemClickListener() {
                    @Override
                    public void onItemClick(BottomActionDialog dialog, BottomActionDialog.ActionItemAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
                        dialog.dismiss();
                        deleteFileShareLink();
                    }
                })
                .show();
    }

    /**
     * 选择过期时间限制
     */
    private void showSelectTimeLimit() {
        new BottomActionDialog(getContext(),
                "下载链接有效期",
                Arrays.asList("永不过期", "1天有效期", "5天有效期", "7天有效期", "14天有效期"),
                new BottomActionDialog.OnActionItemClickListener() {

                    @Override
                    public void onItemClick(BottomActionDialog dialog, BottomActionDialog.ActionItemAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
                        dialog.dismiss();
                        fileAccessTimeLimitTv.setText(adapter.getData(position));
                    }
                })
                .show();
    }

    /**
     * 展示密码生成方式选择对话框
     */
    private void showSelectPwdType() {
        new BottomActionDialog(getContext(),
                "是否需要访问密码?",
                Arrays.asList("自动生成", "不需要"),
                new BottomActionDialog.OnActionItemClickListener() {

                    @Override
                    public void onItemClick(BottomActionDialog dialog, BottomActionDialog.ActionItemAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
                        dialog.dismiss();
                        fileAccessPwdTv.setText(adapter.getData(position));
                    }
                })
                .show();
    }

    private void copyFileShareLink() {
        if (isNoFileShareLink()) {
            showTopSnackBar("暂无链接可复制");
            return;
        }
        if (sFileLinkInfoEntity.isNeedAccessPwd()) {
            SystemUtils.copyToClipboard(getContext(), "link", String.format("链接:%s 密码:%s", sFileLinkInfoEntity.getRealShareLink(), sFileLinkInfoEntity.password));
        } else {
            SystemUtils.copyToClipboard(getContext(), "link", String.format("链接:%s", sFileLinkInfoEntity.getRealShareLink()));
        }
        showToast("已复制到剪切版");
    }


    /**
     * 获取选中的天数
     */
    private int getSelectedExpireDays() {
        List<String> expiredays = Arrays.asList("永不过期", "1天有效期", "5天有效期", "7天有效期", "14天有效期");
        List<Integer> expiredayValues = Arrays.asList(0, 1, 5, 7, 14);
        int indexof = expiredays.indexOf(fileAccessTimeLimitTv.getText());
        if (indexof >= 0) {
            return expiredayValues.get(indexof);
        }
        return expiredayValues.get(0);
    }

    /**
     * 是否需要密码
     *
     * @return
     */
    private boolean isCreatePassword() {
        return !TextUtils.equals(fileAccessPwdTv.getText(), "不需要");
    }

    /**
     * 创建文件分享链接
     */
    private void createFileShareLink() {
        JsonObject paramJsonObject = new JsonObject();
        paramJsonObject.addProperty("createPassword", isCreatePassword());
        paramJsonObject.addProperty("dir", getArguments().getString(KEY_SEA_FILE_FROM_FILE_PATH, "").endsWith("/"));
        paramJsonObject.addProperty("expireDays", getSelectedExpireDays());
        paramJsonObject.addProperty("path", getArguments().getString(KEY_SEA_FILE_FROM_FILE_PATH, ""));
        paramJsonObject.addProperty("repoId", getArguments().getString(KEY_SEA_FILE_FROM_REPO_ID, ""));
        paramJsonObject.addProperty("type", getArguments().getInt(KEY_SEA_FILE_LINK_TYPE));
        showLoadingDialog("创建中...");
        callEnqueue(getApi().fileShareLinkCreate(RequestUtils.createJsonBody(paramJsonObject.toString()))
                , new SFileCallBack<SFileLinkInfoEntity>() {
                    @Override
                    public void onSuccess(Call<SFileLinkInfoEntity> call, Response<SFileLinkInfoEntity> response) {
                        dismissLoadingDialog();
                        //创建失败
                        if (TextUtils.isEmpty(response.body().officeShareLink) &&
                                TextUtils.isEmpty(response.body().shareLinkId)) {
                            showToast("创建共享链接失败");
                        } else {
                            getData(true);
                        }
                    }

                    @Override
                    public void onFailure(Call<SFileLinkInfoEntity> call, Throwable t) {
                        dismissLoadingDialog();
                        super.onFailure(call, t);
                    }
                });
    }


    /**
     * 删除文件分享链接
     */
    private void deleteFileShareLink() {
        if (sFileLinkInfoEntity == null) return;
        showLoadingDialog("删除中...");
        callEnqueue(getApi().fileShareLinkDelete(sFileLinkInfoEntity.shareLinkId),
                new SFileCallBack<ResEntity<JsonElement>>() {
                    @Override
                    public void onSuccess(Call<ResEntity<JsonElement>> call, Response<ResEntity<JsonElement>> response) {
                        dismissLoadingDialog();
                        showToast("删除成功");
                        getData(true);
                    }

                    @Override
                    public void onFailure(Call<ResEntity<JsonElement>> call, Throwable t) {
                        dismissLoadingDialog();
                        super.onFailure(call, t);
                    }
                });
    }

    /**
     * 是否无链接
     *
     * @return
     */
    private boolean isNoFileShareLink() {
        return sFileLinkInfoEntity == null || sFileLinkInfoEntity.isNoLink();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
