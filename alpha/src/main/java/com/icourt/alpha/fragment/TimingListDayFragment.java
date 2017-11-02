package com.icourt.alpha.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.adapter.baseadapter.BaseRefreshFragmentAdapter;
import com.icourt.alpha.constants.TimingConfig;
import com.icourt.alpha.entity.bean.TimingStatisticEntity;
import com.icourt.alpha.utils.DateUtils;
import com.icourt.alpha.widget.manager.TimerDateManager;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import lecho.lib.hellocharts.view.LineChartView;

/**
 * Description 选中日情况下的计时列表
 * Company Beijing icourt
 * author zhaodanyang E-mail:zhaodanyang@icourt.cc
 * date createTime: 2017/10/10
 * version 2.1.1
 */

public class TimingListDayFragment extends BaseTimingListFragment {

    private static final String KEY_START_TIME = "key_start_time";

    Unbinder bind;

    @BindView(R.id.appbar)
    AppBarLayout appBarLayout;
    @BindView(R.id.timing_chart_view)
    LineChartView timingChartView;
    @BindView(R.id.timing_count_total2_tv)
    TextView timingCountTotal2Tv;
    @BindView(R.id.timing_text_show_timing_ll)
    LinearLayout timingTextShowTimingLl;
    @BindView(R.id.viewPager)
    ViewPager viewPager;

    BaseRefreshFragmentAdapter baseFragmentAdapter;

    long startTimeMillis;//传递进来的开始时间
    long selectedDayTime;//选中的某一天的开始时间

    public static TimingListDayFragment newInstance(long startTimeMillis) {
        TimingListDayFragment fragment = new TimingListDayFragment();
        Bundle bundle = new Bundle();
        bundle.putLong(KEY_START_TIME, startTimeMillis);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(R.layout.fragment_timing_day_list, inflater, container, savedInstanceState);
        bind = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void initView() {
        addAppbarHidenListener(appBarLayout);

        if (getArguments() != null) {
            startTimeMillis = getArguments().getLong(KEY_START_TIME);
        }

        timingChartView.setVisibility(View.GONE);
        timingTextShowTimingLl.setVisibility(View.VISIBLE);

        //起始日期为2015年1月1日
        final Calendar calendar = TimerDateManager.getStartDate();

        viewPager.setAdapter(baseFragmentAdapter = new BaseRefreshFragmentAdapter(getChildFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                long startTime = calendar.getTimeInMillis();
                return TimingListFragment.newInstance(TimingConfig.TIMING_QUERY_BY_DAY, startTime + position * TimeUnit.DAYS.toMillis(1));
            }

            @Override
            public int getCount() {
                return DateUtils.differentDays(calendar.getTimeInMillis(), System.currentTimeMillis()) + 1;
            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {//当左右切换的时候，需要更新标题栏时期，还要获取切换到的tab页的统计数据
                selectedDayTime = calendar.getTimeInMillis() + position * TimeUnit.DAYS.toMillis(1);
                getTimingStatistic(TimingConfig.TIMING_QUERY_BY_DAY, DateUtils.getDayStartTime(selectedDayTime), DateUtils.getDayEndTime(selectedDayTime));
                if (getParentListener() != null) {
                    getParentListener().onTimeChanged(TimingConfig.TIMING_QUERY_BY_DAY, selectedDayTime);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        //当前界面所传递的时间和起始时间（2015年1月1日）相差的天数。
        int differentDays = DateUtils.differentDays(calendar.getTimeInMillis(), startTimeMillis);
        viewPager.setCurrentItem(differentDays, false);
        selectedDayTime = calendar.getTimeInMillis() + viewPager.getCurrentItem() * TimeUnit.DAYS.toMillis(1);
        getTimingStatistic(TimingConfig.TIMING_QUERY_BY_DAY, DateUtils.getDayStartTime(selectedDayTime), DateUtils.getDayEndTime(selectedDayTime));
    }

    /**
     * 显示总计时
     *
     * @param timeMillis
     */
    private void showSumTime(long timeMillis) {
        String hm = DateUtils.getHHmmIntegral(timeMillis);
        timingCountTotal2Tv.setText(hm);
    }

    @Override
    protected void getTimingStatisticSuccess(TimingStatisticEntity statisticEntity) {
        super.getTimingStatisticSuccess(statisticEntity);
        showSumTime(statisticEntity.allTimingSum);
        if (getParentListener() != null) {
            getParentListener().onTimeSumChanged(TimingConfig.TIMING_QUERY_BY_DAY, statisticEntity.allTimingSum, statisticEntity.todayTimingSum);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        bind.unbind();
    }
}
