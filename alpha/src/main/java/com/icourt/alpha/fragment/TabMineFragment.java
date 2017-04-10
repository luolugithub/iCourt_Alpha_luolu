package com.icourt.alpha.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.SeekBar;

import com.icourt.alpha.R;
import com.icourt.alpha.activity.UpdatePhoneOrMailActivity;
import com.icourt.alpha.base.BaseFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/8
 * version 1.0.0
 */
public class TabMineFragment extends BaseFragment {


    @BindView(R.id.seekBar)
    SeekBar seekBar;
    @BindView(R.id.ratingBar)
    RatingBar ratingBar;
    @BindView(R.id.bt_phone)
    Button btPhone;
    @BindView(R.id.bt_email)
    Button btEmail;
    Unbinder unbinder;

    public static TabMineFragment newInstance() {
        return new TabMineFragment();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(R.layout.fragment_tab_mine, inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void initView() {
        log("==========initView:" + this);
    }

    @Override
    protected void getData(boolean isRefresh) {

    }

    @OnClick({R.id.bt_phone, R.id.bt_email})
    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.bt_phone:
                UpdatePhoneOrMailActivity.launch(getContext(), UpdatePhoneOrMailActivity.UPDATE_PHONE_TYPE, "18888887777");
                break;
            case R.id.bt_email:
                UpdatePhoneOrMailActivity.launch(getContext(), UpdatePhoneOrMailActivity.UPDATE_EMAIL_TYPE, "zhaolu@icourt.cc");
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
