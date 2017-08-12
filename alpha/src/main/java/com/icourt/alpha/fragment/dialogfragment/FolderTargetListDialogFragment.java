package com.icourt.alpha.fragment.dialogfragment;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckedTextView;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.base.BaseDialogFragment;
import com.icourt.alpha.fragment.FolderTargetListFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Description  文件移动copy目标路径
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/8/12
 * version 2.1.0
 */
public class FolderTargetListDialogFragment extends BaseDialogFragment {
    @BindView(R.id.titleBack)
    CheckedTextView titleBack;
    @BindView(R.id.titleContent)
    TextView titleContent;
    @BindView(R.id.foldr_go_parent_tv)
    TextView foldrGoParentTv;
    @BindView(R.id.foldr_parent_tv)
    TextView foldrParentTv;
    Unbinder unbinder;
    @BindView(R.id.main_fl_content)
    FrameLayout mainFlContent;

    protected static final String KEY_SEA_FILE_REPO_ID = "seaFileRepoId";//仓库id
    protected static final String KEY_SEA_FILE_PARENT_DIR_PATH = "seaFileParentDirPath";//父目录路径

    /**
     * @param seaFileRepoId
     * @param title
     * @param seaFileParentDirPath
     * @return
     */
    public static FolderTargetListDialogFragment newInstance(
            String seaFileRepoId,
            String title,
            String seaFileParentDirPath) {
        FolderTargetListDialogFragment fragment = new FolderTargetListDialogFragment();
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
        View view = super.onCreateView(R.layout.dialog_fragment_folder_targt_list, inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    private Fragment currFragment;

    @Override
    public void onStart() {
        super.onStart();
        getDialog()
                .getWindow()
                .setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
    }

    @Override
    protected void initView() {
        Dialog dialog = getDialog();
        if (dialog != null) {
            Window window = dialog.getWindow();
            if (window != null) {
                WindowManager.LayoutParams attributes = window.getAttributes();
                attributes.windowAnimations = R.style.SlideAnimBottom;
                window.setAttributes(attributes);
            }
        }
        currFragment = addOrShowFragment(
                FolderTargetListFragment.newInstance(
                        getArguments().getString(KEY_SEA_FILE_REPO_ID, ""),
                        getArguments().getString("title", ""),
                        getArguments().getString(KEY_SEA_FILE_PARENT_DIR_PATH, "")),
                currFragment,
                R.id.main_fl_content);
    }

    @OnClick({R.id.titleBack})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.titleBack:
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
