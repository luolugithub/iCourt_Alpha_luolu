package com.icourt.alpha.fragment.dialogfragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.icourt.alpha.R;

/**
 * Description  项目选择对话框
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/5/11
 * version 1.0.0
 */
public class ProjectSelectDialogFragment extends BaseDialogFragment {
    public static ProjectSelectDialogFragment newInstance() {
        return new ProjectSelectDialogFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(R.layout.dialog_fragment_project_select, inflater, container, savedInstanceState);
    }

    @Override
    protected void initView() {

    }
}
