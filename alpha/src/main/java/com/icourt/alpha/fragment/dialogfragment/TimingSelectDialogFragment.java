package com.icourt.alpha.fragment.dialogfragment;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CheckedTextView;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.adapter.baseadapter.BaseFragmentAdapter;
import com.icourt.alpha.base.BaseDialogFragment;
import com.icourt.alpha.fragment.TimingSelectDayFragment;
import com.icourt.alpha.fragment.TimingSelectMonthFragment;
import com.icourt.alpha.fragment.TimingSelectWeekFragment;
import com.icourt.alpha.fragment.TimingSelectYearFragment;
import com.icourt.alpha.utils.DensityUtil;
import com.icourt.alpha.view.NoScrollViewPager;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:zhaodanyang@icourt.cc
 * date createTime：2017/9/16
 * version 1.0.0
 */

public class TimingSelectDialogFragment extends BaseDialogFragment {

    private Unbinder bind;

    @BindView(R.id.tv_date_day)
    CheckedTextView tvDateDay;
    @BindView(R.id.tv_date_week)
    CheckedTextView tvDateWeek;
    @BindView(R.id.tv_date_month)
    CheckedTextView tvDateMonth;
    @BindView(R.id.tv_date_year)
    CheckedTextView tvDateYear;
    @BindView(R.id.viewpager)
    NoScrollViewPager viewpager;
    @BindView(R.id.tv_cancel)
    TextView tvCancel;
    @BindView(R.id.tv_finish)
    TextView tvFinish;

    private BaseFragmentAdapter baseFragmentAdapter;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(R.layout.dialog_fragment_timer_select, inflater, container, savedInstanceState);
        bind = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void initView() {
        Dialog dialog = getDialog();
        if (dialog != null) {
            Window window = dialog.getWindow();
            if (window != null) {
                window.setWindowAnimations(R.style.AppThemeSlideAnimation);
                window.setGravity(Gravity.BOTTOM);
                View decorView = window.getDecorView();
                if (decorView != null) {
                    int dp15 = DensityUtil.dip2px(getContext(), 15);
                    decorView.setPadding(dp15, dp15, dp15, dp15);
                }
            }
        }

        viewpager.setAdapter(baseFragmentAdapter = new BaseFragmentAdapter(getChildFragmentManager()));

        baseFragmentAdapter.bindData(true,
                Arrays.asList(new TimingSelectDayFragment(),
                        new TimingSelectWeekFragment(),
                        new TimingSelectMonthFragment(),
                        new TimingSelectYearFragment()));
        viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                selectDateItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void selectDateItem(int position) {
        switch (position) {
            case 0://说明是日
                tvDateDay.setChecked(true);
                tvDateWeek.setChecked(false);
                tvDateMonth.setChecked(false);
                tvDateYear.setChecked(false);
                break;
            case 1://说明是周
                tvDateDay.setChecked(false);
                tvDateWeek.setChecked(true);
                tvDateMonth.setChecked(false);
                tvDateYear.setChecked(false);
                break;
            case 2://说明是月
                tvDateDay.setChecked(false);
                tvDateWeek.setChecked(false);
                tvDateMonth.setChecked(true);
                tvDateYear.setChecked(false);
                break;
            case 3://说明是年
                tvDateDay.setChecked(false);
                tvDateWeek.setChecked(false);
                tvDateMonth.setChecked(false);
                tvDateYear.setChecked(true);
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        bind.unbind();
    }
}
