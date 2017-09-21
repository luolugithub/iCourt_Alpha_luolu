package com.icourt.alpha.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.icourt.alpha.R;
import com.icourt.alpha.base.BaseFragment;

/**
 * Created by zhaodanyang on 2017/9/21.
 */

public class TimingSelectYearFragment extends BaseFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(R.layout.fragment_timing_select_year, inflater, container, savedInstanceState);
        return view;
    }

    @Override
    protected void initView() {

    }
}
