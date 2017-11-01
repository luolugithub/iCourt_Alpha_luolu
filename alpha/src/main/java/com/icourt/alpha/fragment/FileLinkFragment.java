package com.icourt.alpha.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.icourt.alpha.R;
import com.icourt.alpha.activity.WebViewActivity;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.base.BaseFragment;
import com.icourt.alpha.constants.SFileConfig;
import com.icourt.alpha.entity.bean.SFileLinkInfoEntity;
import com.icourt.alpha.http.callback.SFileCallBack;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.utils.DateUtils;
import com.icourt.alpha.utils.SystemUtils;
import com.icourt.alpha.utils.UrlUtils;
import com.icourt.alpha.widget.dialog.AlertListDialog;
import com.icourt.alpha.widget.dialog.BottomActionDialog;
import com.icourt.api.RequestUtils;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
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
    protected static final String KEY_SEA_FILE_REPO_PERMISSION = "seaFileRepoPermission";//repo的权限

    public static final int LINK_TYPE_DOWNLOAD = 0;//下载链接类型
    public static final int LINK_TYPE_UPLOAD = 1;//上传链接类型

    @IntDef({LINK_TYPE_DOWNLOAD,
            LINK_TYPE_UPLOAD})
    public @interface LINK_TYPE {

    }


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
    @BindView(R.id.file_link_delete_tv)
    TextView fileLinkDeleteTv;

    @LINK_TYPE
    int linkType;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(R.layout.fragment_file_link, inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    /**
     * @param fromRepoId
     * @param fromRepoFilePath
     * @param linkType         分享类型 0下载 1上传
     * @return
     */
    public static FileLinkFragment newInstance(
            String fromRepoId,
            String fromRepoFilePath,
            @LINK_TYPE int linkType,
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
        switch (getArguments().getInt(KEY_SEA_FILE_LINK_TYPE)) {
            case LINK_TYPE_DOWNLOAD:
                linkType = LINK_TYPE_DOWNLOAD;
                break;
            case LINK_TYPE_UPLOAD:
                linkType = LINK_TYPE_UPLOAD;
                break;
            default:
                linkType = LINK_TYPE_UPLOAD;
                break;
        }
        getData(true);
    }

    @Override
    protected void getData(boolean isRefresh) {
        super.getData(isRefresh);
        String fileFullPath = getArguments().getString(KEY_SEA_FILE_FROM_FILE_PATH, "");
        fileFullPath = UrlUtils.encodeUrl(fileFullPath);
        callEnqueue(getApi().fileShareLinkQuery(
                getArguments().getString(KEY_SEA_FILE_FROM_REPO_ID, ""),
                fileFullPath,
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
        if (!isNoFileShareLink()) {
            linkCopyTv.setVisibility(View.VISIBLE);
            fileLinkCreateTv.setVisibility(View.GONE);
            fileLinkDeleteTv.setVisibility(View.VISIBLE);

            if (linkType == LINK_TYPE_DOWNLOAD) {
                fileShareLinkTitleTv.setText(R.string.sfile_link_download_created);
                fileLinkDeleteTv.setText(R.string.sfile_link_download_delete);
            } else {
                fileShareLinkTitleTv.setText(R.string.sfile_link_upload_created);
                fileLinkDeleteTv.setText(R.string.sfile_link_upload_delete);
            }
            fileAccessPwdTv.setText(sFileLinkInfoEntity.isNeedAccessPwd() ? sFileLinkInfoEntity.password : getString(R.string.sfile_link_password_null));
            linkCopyTv.setText(sFileLinkInfoEntity.isNeedAccessPwd() ? "复制链接和密码" : "复制链接");
            fileAccessTimeLimitTv.setText(sFileLinkInfoEntity.expireTime <= 0 ? getString(R.string.sfile_link_limit_date_0) : DateUtils.getFormatDate(sFileLinkInfoEntity.expireTime, DateUtils.DATE_MMDD_HHMM_STYLE1));
            fileShareLinkTv.setText(sFileLinkInfoEntity.getRealShareLink());
        } else {
            fileLinkCreateTv.setVisibility(View.VISIBLE);
            fileLinkDeleteTv.setVisibility(View.GONE);

            fileAccessTimeLimitTv.setText(getString(R.string.sfile_link_limit_date_7));
            fileShareLinkTv.setText("");
            fileAccessPwdTv.setText(R.string.sfile_link_password_auto_generation);
            if (linkType == LINK_TYPE_DOWNLOAD) {
                fileShareLinkTitleTv.setText(R.string.sfile_link_download_create);
            } else {
                fileShareLinkTitleTv.setText(R.string.sfile_link_upload_create);
            }
            linkCopyTv.setVisibility(View.GONE);
        }
    }

    @OnLongClick({R.id.file_access_pwd_tv,
            R.id.file_share_link_tv})
    public boolean onLongClick(View v) {
        switch (v.getId()) {
            case R.id.file_access_pwd_tv:
                if (isNeedAccessPwd()) {
                    showCopyMenuDialog(new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            SystemUtils.copyToClipboard(getContext(),
                                    "pwd",
                                    sFileLinkInfoEntity.password);
                            showToast(R.string.sfile_link_already_copied);
                        }
                    });
                }
                break;
            case R.id.file_share_link_tv:
                if (!isNoFileShareLink()) {
                    showCopyMenuDialog(new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            SystemUtils.copyToClipboard(getContext(),
                                    "link",
                                    sFileLinkInfoEntity.getRealShareLink());
                            showToast(R.string.sfile_link_already_copied);
                        }
                    });
                }
                break;
        }
        return true;
    }

    private void showCopyMenuDialog(DialogInterface.OnClickListener listener) {
        new AlertListDialog.ListBuilder(getContext())
                .setItems(new String[]{getString(R.string.str_copy)}, listener)
                .show();
    }

    @OnClick({R.id.link_copy_tv,
            R.id.file_link_create_tv,
            R.id.file_access_pwd_tv,
            R.id.file_access_time_limit_tv,
            R.id.file_link_delete_tv,
            R.id.file_share_link_tv,
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
            case R.id.file_share_link_tv:
                if (!isNoFileShareLink()) {
                    WebViewActivity.launch(getContext(),
                            sFileLinkInfoEntity.getRealShareLink());
                }
                break;
            default:
                super.onClick(v);
                break;
        }
    }

    private void showDeleteFileConfirmDialog() {
        if (sFileLinkInfoEntity == null) return;
        new BottomActionDialog(getContext(),
                getString(R.string.sfile_link_delete_confirm),
                Arrays.asList(getString(R.string.str_delete)),
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
        String noticeTitle = getString(R.string.sfile_link_limit_download_date);
        if (linkType != LINK_TYPE_DOWNLOAD) {
            noticeTitle = getString(R.string.sfile_link_limit_upload_date);
        }
        new BottomActionDialog(getContext(),
                noticeTitle,
                Arrays.asList(getResources().getStringArray(R.array.sfile_link_limit_date_array)),
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
                getString(R.string.sfile_link_access_password_confirm),
                Arrays.asList(
                        getString(R.string.sfile_link_password_auto_generation),
                        getString(R.string.sfile_link_password_null)
                ),
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
            return;
        }
        if (sFileLinkInfoEntity.isNeedAccessPwd()) {
            SystemUtils.copyToClipboard(getContext(), "link", String.format("链接:%s 密码:%s", sFileLinkInfoEntity.getRealShareLink(), sFileLinkInfoEntity.password));
        } else {
            SystemUtils.copyToClipboard(getContext(), "link", String.format("链接:%s", sFileLinkInfoEntity.getRealShareLink()));
        }
        showToast(R.string.sfile_link_already_copied);
    }


    /**
     * 获取选中的天数
     */
    private int getSelectedExpireDays() {
        List<String> expiredays = Arrays.asList(getResources().getStringArray(R.array.sfile_link_limit_date_array));
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
        return !TextUtils.equals(fileAccessPwdTv.getText(), getString(R.string.sfile_link_password_null));
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
        paramJsonObject.addProperty("type", linkType);
        showLoadingDialog(R.string.str_creating);
        callEnqueue(getApi().fileShareLinkCreate(RequestUtils.createJsonBody(paramJsonObject.toString()))
                , new SFileCallBack<SFileLinkInfoEntity>() {
                    @Override
                    public void onSuccess(Call<SFileLinkInfoEntity> call, Response<SFileLinkInfoEntity> response) {
                        dismissLoadingDialog();
                        //创建失败
                        if (TextUtils.isEmpty(response.body().officeShareLink) &&
                                TextUtils.isEmpty(response.body().shareLinkId)) {
                            showToast(R.string.sfile_link_create_fail);
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
        showLoadingDialog(R.string.str_deleting);
        callEnqueue(getApi().fileShareLinkDelete(sFileLinkInfoEntity.shareLinkId),
                new SFileCallBack<ResEntity<JsonElement>>() {
                    @Override
                    public void onSuccess(Call<ResEntity<JsonElement>> call, Response<ResEntity<JsonElement>> response) {
                        dismissLoadingDialog();
                        //showToast(R.string.str_delete_sucess);
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


    /**
     * 是否需要pwd
     *
     * @return
     */
    private boolean isNeedAccessPwd() {
        return !isNoFileShareLink() && sFileLinkInfoEntity.isNeedAccessPwd();
    }
}
