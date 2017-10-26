package com.icourt.alpha.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.icourt.alpha.entity.bean.TimingSelectEntity;
import com.icourt.alpha.entity.bean.TimingStatisticEntity;
import com.icourt.alpha.utils.DateUtils;
import com.icourt.alpha.utils.DensityUtil;
import com.icourt.alpha.utils.SystemUtils;
import com.icourt.alpha.widget.manager.TimerDateManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;

/**
 * Description 选中月情况下的计时列表
 * Company Beijing icourt
 * author zhaodanyang E-mail:zhaodanyang@icourt.cc
 * date createTime: 2017/10/10
 * version 2.1.1
 */

public class TimingListMonthFragment extends BaseTimingListFragment {

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

    private int numberOfPoints = 31;//点的数量
    private boolean hasLines = true;//折线图是否有分割线
    private boolean hasPoints = false;//不要圆点
    private ValueShape shape = ValueShape.CIRCLE;
    private boolean isFilled = true;
    private boolean hasLabels = false;
    private boolean isCubic = true;
    private boolean hasLabelForSelected = false;

    BaseRefreshFragmentAdapter baseFragmentAdapter;

    long startTimeMillis;//传递进来的开始时间

    private List<TimingSelectEntity> monthData = new ArrayList<>();//记录所有周的列表数据

    public static TimingListMonthFragment newInstance(long startTimeMillis) {
        TimingListMonthFragment fragment = new TimingListMonthFragment();
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
        //初始化折线图的padding值
        int dp5 = DensityUtil.dip2px(getActivity(), 5);
        int dp10 = DensityUtil.dip2px(getActivity(), 10);
        int dp12 = DensityUtil.dip2px(getActivity(), 12);
        setChartViewPadding(timingChartView, dp5, dp10, dp12, dp5);
        timingChartView.setZoomEnabled(false);//设置折线图不可缩放
        timingChartView.setContainerScrollEnabled(false, ContainerScrollType.VERTICAL);//设置折线图的包裹布局是否可以滚动
        addAppbarHidenListener(appBarLayout);//给AppBarLayout添加监听

        if (getArguments() != null) {
            long startTime = getArguments().getLong(KEY_START_TIME);
            startTimeMillis = DateUtils.getMonthStartTime(startTime);
        }

        //初始化折线图，先给一个空列表数据
        generateData(new ArrayList<Long>());

        timingChartView.setVisibility(View.VISIBLE);
        timingTextShowTimingLl.setVisibility(View.GONE);

        monthData = TimerDateManager.getMonthData();

        viewPager.setAdapter(baseFragmentAdapter = new BaseRefreshFragmentAdapter(getChildFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                TimingSelectEntity timingSelectEntity = monthData.get(position);
                return TimingListFragment.newInstance(TimingConfig.TIMING_QUERY_BY_MONTH, timingSelectEntity.startTimeMillis);
            }

            @Override
            public int getCount() {
                return monthData.size();
            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                TimingSelectEntity timingSelectEntity = monthData.get(position);
                getTimingStatistic(TimingConfig.TIMING_QUERY_BY_MONTH, timingSelectEntity.startTimeMillis, timingSelectEntity.endTimeMillis);
                if (getParentListener() != null) {
                    getParentListener().onTimeChanged(TimingConfig.TIMING_QUERY_BY_MONTH, timingSelectEntity.startTimeMillis);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        //记录当前所在的position，根据position获取所在月份的统计数据
        int position = 0;
        for (int i = 0; i < monthData.size(); i++) {
            if (startTimeMillis >= monthData.get(i).startTimeMillis && startTimeMillis <= monthData.get(i).endTimeMillis) {
                position = i;
                break;
            }
        }
        viewPager.setCurrentItem(position, true);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (viewPager != null && monthData != null) {
            int position = viewPager.getCurrentItem();
            TimingSelectEntity timingSelectEntity = monthData.get(position);
            getTimingStatistic(TimingConfig.TIMING_QUERY_BY_MONTH, timingSelectEntity.startTimeMillis, timingSelectEntity.endTimeMillis);
        }
    }

    @Override
    protected void getTimingStatisticSuccess(TimingStatisticEntity statisticEntity) {
        super.getTimingStatisticSuccess(statisticEntity);
        if (getParentListener() != null) {
            getParentListener().onTimeSumChanged(TimingConfig.TIMING_QUERY_BY_MONTH, statisticEntity.allTimingSum, statisticEntity.todayTimingSum);
            if (statisticEntity.timingList != null) {
                generateData(statisticEntity.timingList);
            }
        }
    }

    /**
     * 初始化折线图的上下左右刻度相关
     */
    private void resetViewport() {
        if (timingChartView == null) {
            return;
        }
        // Reset viewport height range to (0,100)
        final Viewport v = new Viewport(timingChartView.getMaximumViewport());
        v.bottom = 0;
        v.top = 24;
        v.left = 0;
        v.right = numberOfPoints;
        timingChartView.setCurrentViewport(v);
    }

    private void generateData(@NonNull List<Long> list) {
        if (timingChartView == null) {
            return;
        }
        resetViewport();

        List<Line> lines = new ArrayList<>();

        List<PointValue> values = new ArrayList<>();
        //x轴的坐标点，横轴显示1, 5, 10, 15, 20, 25, 30
        List<AxisValue> axisXValues = Arrays.asList(
                new AxisValue(0).setLabel("1"),
                new AxisValue(4).setLabel("5"),
                new AxisValue(9).setLabel("10"),
                new AxisValue(14).setLabel("15"),
                new AxisValue(19).setLabel("20"),
                new AxisValue(24).setLabel("25"),
                new AxisValue(29).setLabel("30"));
        List<AxisValue> axisYValues = new ArrayList<>();
        for (int i = 0; i <= 24; i += 4) {
            axisYValues.add(new AxisValue(i).setLabel(String.format("%sh ", i)));
        }

        //计算出要展示的那天的计时时间
        float maxValue = 0f;
        if (list.size() >= numberOfPoints) {
            for (int j = 0; j < numberOfPoints; j++) {
                float hour = 0;
                Long weekDayTime = list.get(j);
                if (weekDayTime != null) {
                    hour = weekDayTime * 1.0f / TimeUnit.HOURS.toMillis(1);
                }
                //最大24
                if (hour >= 24) {
                    hour = 23.9f;
                }
                if (hour > maxValue) {
                    maxValue = hour;
                }
                log("--------j:" + j + "  time:" + hour);
                values.add(new PointValue(j, hour));
            }
        }

        //用第二条先提高纵轴高度，防止真正要显示的折线图数据过少而显示不全的问题。
        int secondMax;
        if (maxValue <= 8) {
            secondMax = 8;
        } else if (maxValue <= 12) {
            secondMax = 12;
        } else if (maxValue <= 16) {
            secondMax = 16;
        } else if (maxValue <= 20) {
            secondMax = 20;
        } else {
            secondMax = 24;
        }
        List<PointValue> values2 = Arrays.asList(
                new PointValue(0, 2.0f),
                new PointValue(1, 4.0f),
                new PointValue(2, 6.0f),
                new PointValue(3, secondMax),
                new PointValue(4, 6.0f),
                new PointValue(5, 4.0f),
                new PointValue(6, 2.0f));
        Line line2 = new Line(values2);
        line2.setShape(shape);
        line2.setCubic(false);
        line2.setFilled(false);
        line2.setHasLabels(hasLabels);
        line2.setHasLabelsOnlyForSelected(hasLabelForSelected);
        line2.setHasLines(false);
        line2.setHasPoints(hasPoints);
        line2.setColor(Color.TRANSPARENT);
        lines.add(line2);

        Line line = new Line(values);
        line.setShape(shape);
        line.setCubic(isCubic);
        line.setFilled(isFilled);
        line.setHasLabels(hasLabels);
        line.setHasLabelsOnlyForSelected(hasLabelForSelected);
        line.setHasLines(hasLines);
        line.setHasPoints(hasPoints);
        line.setColor(SystemUtils.getColor(getContext(), R.color.alpha_font_color_orange));
        lines.add(line);
        LineChartData data = new LineChartData(lines);


        Axis axisX = new Axis()
                .setHasLines(true)
                .setValues(axisXValues)
                .setTextSize(10)
                .setTextColor(SystemUtils.getColor(getActivity(), R.color.timer_chart_text_color))
                .setMaxLabelChars("00".toCharArray().length);
        Axis axisY = new Axis()
                .setHasLines(true)
                .setValues(axisYValues)
                .setTextSize(10)
                .setTextColor(SystemUtils.getColor(getActivity(), R.color.timer_chart_text_color));
        data.setAxisXBottom(axisX);
        data.setAxisYLeft(axisY);

        data.setBaseValue(Float.NEGATIVE_INFINITY);
        timingChartView.setLineChartData(data);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        bind.unbind();
    }
}
