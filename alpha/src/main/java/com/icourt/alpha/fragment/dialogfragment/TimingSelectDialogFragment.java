package com.icourt.alpha.fragment.dialogfragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import com.icourt.alpha.base.BaseFragment;
import com.icourt.alpha.entity.bean.TimingSelectEntity;
import com.icourt.alpha.fragment.TimingSelectDayFragment;
import com.icourt.alpha.fragment.TimingSelectMonthFragment;
import com.icourt.alpha.fragment.TimingSelectWeekFragment;
import com.icourt.alpha.fragment.TimingSelectYearFragment;
import com.icourt.alpha.interfaces.OnDateSelectedListener;
import com.icourt.alpha.interfaces.OnFragmentCallBackListener;
import com.icourt.alpha.utils.DensityUtil;
import com.icourt.alpha.view.NoScrollViewPager;
import com.icourt.alpha.widget.manager.TimerDateManager;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:zhaodanyang@icourt.cc
 * date createTime：2017/9/16
 * version 1.0.0
 */

public class TimingSelectDialogFragment extends BaseDialogFragment implements OnDateSelectedListener {

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
    OnFragmentCallBackListener onFragmentCallBackListener;

    public static TimingSelectDialogFragment newInstance() {
        return new TimingSelectDialogFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(R.layout.dialog_fragment_timer_select, inflater, container, savedInstanceState);
        bind = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            onFragmentCallBackListener = (OnFragmentCallBackListener) context;
        } catch (ClassCastException e) {
            e.printStackTrace();
            bugSync("选择计时时间：onFragmentCallBackListener", e);
        }
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
        viewpager.setOffscreenPageLimit(3);
        baseFragmentAdapter.bindData(true,
                Arrays.asList(TimingSelectDayFragment.newInstance(),
                        TimingSelectWeekFragment.newInstance(),
                        TimingSelectMonthFragment.newInstance(),
                        TimingSelectYearFragment.newInstance()));
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
        selectDateItem(0);
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

        if (position == 0) {//如果是日的选择时间，需要判断选择的时间是否在有效范围内
            Fragment fragment = baseFragmentAdapter.getItem(position);
            if (fragment != null && fragment instanceof TimingSelectDayFragment) {
                TimingSelectDayFragment dayFragment = (TimingSelectDayFragment) fragment;
                onDateSelected(dayFragment.getSelectedTime());
            }
        } else {
            tvFinish.setBackgroundResource(R.drawable.bg_round_orange);
            tvFinish.setEnabled(true);
        }
    }

    @OnClick({R.id.tv_cancel,
            R.id.tv_finish,
            R.id.tv_date_day,
            R.id.tv_date_week,
            R.id.tv_date_month,
            R.id.tv_date_year})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_date_day:
                viewpager.setCurrentItem(0);
                break;
            case R.id.tv_date_week:
                viewpager.setCurrentItem(1);
                break;
            case R.id.tv_date_month:
                viewpager.setCurrentItem(2);
                break;
            case R.id.tv_date_year:
                viewpager.setCurrentItem(3);
                break;
            case R.id.tv_cancel:
                dismiss();
                break;
            case R.id.tv_finish:
                if (onFragmentCallBackListener == null) {
                    onFragmentCallBackListener = (OnFragmentCallBackListener) getParentFragment();
                }
                BaseFragment fragment = (BaseFragment) baseFragmentAdapter.getItem(viewpager.getCurrentItem());
                Bundle fragmentData = fragment.getFragmentData(0, null);
                if (fragmentData == null) return;
                TimingSelectEntity timingSelectEntity = (TimingSelectEntity) fragmentData.getSerializable(KEY_FRAGMENT_RESULT);
                if (timingSelectEntity != null) {
                    onFragmentCallBackListener.onFragmentCallBack(fragment, 0, fragmentData);
                    log("开始时间： －－－－  " + timingSelectEntity.startTimeStr);
                    log("结束时间： －－－－  " + timingSelectEntity.endTimeStr);
                }
                break;

        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        bind.unbind();
    }

    @Override
    public void onDateSelected(long timeMillis) {
        if (timeMillis < TimerDateManager.getStartDate().getTimeInMillis()) {//如果选中的时间小于2015年1月1日，则完成按钮不可点击
            tvFinish.setEnabled(false);
            tvFinish.setBackgroundResource(R.drawable.bg_round_gray);
        } else if (timeMillis > System.currentTimeMillis()) {//如果选中的时间大于当前时间，则完成按钮不可点击
            tvFinish.setEnabled(false);
            tvFinish.setBackgroundResource(R.drawable.bg_round_gray);
        } else {//可点击
            tvFinish.setEnabled(true);
            tvFinish.setBackgroundResource(R.drawable.bg_round_orange);
        }
    }
}
