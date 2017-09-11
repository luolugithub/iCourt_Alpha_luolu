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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.base.BaseDialogFragment;
import com.icourt.alpha.fragment.RepoNavigationFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/9/11
 * version 2.1.0
 */
public class SeaFileSelectDialogFragment extends BaseDialogFragment {
    @BindView(R.id.titleBack)
    CheckedTextView titleBack;
    @BindView(R.id.titleContent)
    TextView titleContent;
    @BindView(R.id.foldr_go_parent_tv)
    TextView foldrGoParentTv;
    @BindView(R.id.foldr_parent_tv)
    TextView foldrParentTv;
    @BindView(R.id.dir_path_title_layout)
    RelativeLayout dirPathTitleLayout;
    @BindView(R.id.main_fl_content)
    FrameLayout mainFlContent;
    Unbinder unbinder;
    Fragment currFragment;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(R.layout.dialog_fragment_sea_file_select, inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        return view;
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

        replaceRepoTypeFragment();
    }

    /**
     * 替换资料库类型界面
     */
    private void replaceRepoTypeFragment() {
        dirPathTitleLayout.setVisibility(View.GONE);
        currFragment = addOrShowFragment(
                RepoNavigationFragment.newInstance(),
                currFragment,
                R.id.main_fl_content);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
