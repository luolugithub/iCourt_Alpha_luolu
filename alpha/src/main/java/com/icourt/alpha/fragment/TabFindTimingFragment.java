package com.icourt.alpha.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.icourt.alpha.R;
import com.icourt.alpha.base.BaseFragment;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/17
 * version 1.0.0
 */
public class TabFindTimingFragment extends BaseFragment {

    public static TabFindTimingFragment newInstance() {
        return new TabFindTimingFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(R.layout.fragment_tab_find_timing, inflater, container, savedInstanceState);
    }

    @Override
    protected void initView() {

    }

}
