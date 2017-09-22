package com.icourt.alpha.fragment.dialogfragment;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.base.BaseDialogFragment;
import com.icourt.alpha.constants.SFileConfig;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/8/21
 * version 2.1.0
 */
public class FileDetailsBaseDialogFragment extends BaseDialogFragment {
    protected static final String KEY_SEA_FILE_FROM_REPO_TYPE = "seaFileFromRepoType";//原仓库类型
    protected static final String KEY_SEA_FILE_FROM_REPO_ID = "seaFileFromRepoId";//原仓库id
    protected static final String KEY_SEA_FILE_DIR_PATH = "seaFileDirPath";//原文件目录
    protected static final String KEY_SEA_FILE_REPO_PERMISSION = "seaFileRepoPermission";//repo的权限
    protected static final String KEY_SEA_FILE_NAME = "seaFileName";//file name
    protected static final String KEY_SEA_FILE_SIZE = "seaFileSize";//file size
    @BindView(R.id.titleContent)
    TextView titleContent;
    @BindView(R.id.titleAction)
    CheckedTextView titleAction;
    @BindView(R.id.file_type_iv)
    ImageView fileTypeIv;
    @BindView(R.id.file_version_tv)
    TextView fileVersionTv;
    @BindView(R.id.file_title_tv)
    TextView fileTitleTv;
    @BindView(R.id.file_size_tv)
    TextView fileSizeTv;
    @BindView(R.id.file_create_info_tv)
    TextView fileCreateInfoTv;
    @BindView(R.id.file_update_info_tv)
    TextView fileUpdateInfoTv;
    @BindView(R.id.tabLayout)
    TabLayout tabLayout;
    @BindView(R.id.viewPager)
    ViewPager viewPager;
    Unbinder unbinder;

    @Nullable
    @Override
    public final View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(R.layout.dialog_fragment_folder_document_detail, inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @CallSuper
    @Override
    protected void initView() {
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.setCancelable(false);
            Window window = dialog.getWindow();
            if (window != null) {
                WindowManager.LayoutParams attributes = window.getAttributes();
                attributes.windowAnimations = R.style.SlideAnimBottom;
                window.setAttributes(attributes);
            }
        }
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

    /**
     * 获取资料库类型
     *
     * @return
     */
    @SFileConfig.REPO_TYPE
    protected int getRepoType() {
        return SFileConfig.convert2RepoType(getArguments().getInt(KEY_SEA_FILE_FROM_REPO_TYPE));
    }

    @OnClick({R.id.titleAction})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.titleAction:
                dismiss();
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
