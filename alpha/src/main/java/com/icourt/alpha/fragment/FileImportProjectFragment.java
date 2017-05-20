package com.icourt.alpha.fragment;

import android.os.Bundle;

import com.icourt.alpha.base.BaseFragment;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/5/20
 * version 1.0.0
 */
public class FileImportProjectFragment extends BaseFragment {
    private static final String KEY_PATH = "path";

    public static FileImportProjectFragment newInstance(String path) {
        Bundle bundle = new Bundle();
        bundle.putString(KEY_PATH, path);
        FileImportProjectFragment importFilePathFragment = new FileImportProjectFragment();
        importFilePathFragment.setArguments(bundle);
        return importFilePathFragment;
    }

    @Override
    protected void initView() {

    }
}
