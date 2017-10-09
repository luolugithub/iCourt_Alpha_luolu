package com.icourt.alpha.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.adapter.baseadapter.BaseRefreshFragmentAdapter;
import com.icourt.alpha.base.BaseActivity;
import com.icourt.alpha.constants.TimingConfig;
import com.icourt.alpha.entity.bean.TimingCountEntity;
import com.icourt.alpha.fragment.TimingListFragment;
import com.icourt.alpha.utils.DateUtils;
import com.icourt.alpha.utils.SystemUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;

import static com.icourt.alpha.constants.TimingConfig.TIMING_QUERY_BY_DAY;
import static com.icourt.alpha.constants.TimingConfig.TIMING_QUERY_BY_MONTH;
import static com.icourt.alpha.constants.TimingConfig.TIMING_QUERY_BY_WEEK;
import static com.icourt.alpha.constants.TimingConfig.TIMING_QUERY_BY_YEAR;

/**
 * Description  我的计时
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/10/9
 * version 2.1.1
 */
public class MyTimingActivity extends BaseActivity {
    static final int MAX_PAGE = Integer.MAX_VALUE;
    static final int CENTER_PAGE = MAX_PAGE / 2;

    public static void launch(@NonNull Context context) {
        if (context == null) return;
        Intent intent = new Intent(context, MyTimingActivity.class);
        context.startActivity(intent);
    }

    @BindView(R.id.titleBack)
    ImageView titleBack;
    @BindView(R.id.titleContent)
    TextView titleContent;
    @BindView(R.id.titleAction)
    ImageView titleAction;
    @BindView(R.id.timing_date_title_tv)
    TextView timingDateTitleTv;
    @BindView(R.id.timing_count_total_tv)
    TextView timingCountTotalTv;
    @BindView(R.id.timing_today_total)
    TextView timingTodayTotal;
    @BindView(R.id.timing_chart_view)
    LineChartView timingChartView;
    @BindView(R.id.timing_count_total2_tv)
    TextView timingCountTotal2Tv;
    @BindView(R.id.timing_text_show_timing_ll)
    LinearLayout timingTextShowTimingLl;
    @BindView(R.id.viewPager)
    ViewPager viewPager;


    private int numberOfPoints = 7;//点的数量
    private boolean hasLines = true;//折线图是否有分割线
    private boolean hasPoints = false;//不要圆点
    private ValueShape shape = ValueShape.CIRCLE;
    private boolean isFilled = true;
    private boolean hasLabels = false;
    private boolean isCubic = true;
    private boolean hasLabelForSelected = false;

    private final List<TimingCountEntity> timingCountEntities = new ArrayList<>();//服务器返回的每日的计时时常
    BaseRefreshFragmentAdapter baseFragmentAdapter;

    @TimingConfig.TIMINGQUERYTYPE
    int timingQueryType = TIMING_QUERY_BY_WEEK;//默认按周


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_timing);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void initView() {
        super.initView();
        titleAction.setImageResource(R.mipmap.header_icon_add);
        resetViewport();
        generateData();

        viewPager.setAdapter(baseFragmentAdapter = new BaseRefreshFragmentAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                int distance = position - CENTER_PAGE;
                long startTime = 0;
                switch (timingQueryType) {
                    case TIMING_QUERY_BY_DAY:
                        startTime = DateUtils.getTodayStartTime() + distance * TimeUnit.DAYS.toMillis(1);
                        break;
                    case TIMING_QUERY_BY_WEEK:
                        startTime = DateUtils.getCurrWeekStartTime() + distance * TimeUnit.DAYS.toMillis(7);
                        break;
                    case TIMING_QUERY_BY_MONTH:
                        break;
                    case TIMING_QUERY_BY_YEAR:
                        break;
                }
                return TimingListFragment.newInstance(timingQueryType, startTime);
            }

            @Override
            public int getCount() {
                return MAX_PAGE;
            }
        });
        viewPager.setCurrentItem(CENTER_PAGE, false);
    }

    private void resetViewport() {
        if (timingChartView == null) return;
        // Reset viewport height range to (0,100)
        final Viewport v = new Viewport(timingChartView.getMaximumViewport());
        v.bottom = 0;
        v.top = 24;
        v.left = 0;
        v.right = numberOfPoints;
        // timingChartView.setMaximumViewport(v);
        timingChartView.setCurrentViewport(v);
    }

    private void generateData() {
        if (timingChartView == null) return;
        resetViewport();
        List<Line> lines = new ArrayList<>();

        List<PointValue> values = new ArrayList<>();
        List<AxisValue> axisXValues = Arrays.asList(
                new AxisValue(0).setLabel("周一"),
                new AxisValue(1).setLabel("周二"),
                new AxisValue(2).setLabel("周三"),
                new AxisValue(3).setLabel("周四"),
                new AxisValue(4).setLabel("周五"),
                new AxisValue(5).setLabel("周六"),
                new AxisValue(6).setLabel("周日"));
        List<AxisValue> axisYValues = new ArrayList<>();
        for (int i = 0; i <= 24; i += 4) {
            axisYValues.add(new AxisValue(i).setLabel(String.format("%sh ", i)));
        }

        SparseArray<Long> weekDataArray = new SparseArray<>();
        for (int i = 0; i < timingCountEntities.size(); i++) {//遍历每日的计时时常
            TimingCountEntity itemEntity = timingCountEntities.get(i);
            if (itemEntity != null) {
                try {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setFirstDayOfWeek(Calendar.MONDAY);
                    calendar.setTimeInMillis(itemEntity.workDate);

                    log("--------------->>i:" + i + "  day:" + (calendar.get(Calendar.DAY_OF_WEEK) + 5) % 7 + "  count:" + itemEntity.timingCount);
                    weekDataArray.put((calendar.get(Calendar.DAY_OF_WEEK) + 5) % 7, itemEntity.timingCount);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        //计算出要展示的那天的计时时间
        float maxValue = 0f;
        for (int j = 0; j < numberOfPoints; j++) {
            float hour = 0;
            Long weekDayTime = weekDataArray.get(j);
            if (weekDayTime != null) {
                hour = weekDayTime.longValue() * 1.0f / TimeUnit.HOURS.toMillis(1);
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

        //用第二条先提高高度
        if (maxValue < 8.0f) {
            List<PointValue> values2 = Arrays.asList(
                    new PointValue(0, 1.0f),
                    new PointValue(1, 2.0f),
                    new PointValue(2, 2.0f),
                    new PointValue(3, 3.0f),
                    new PointValue(4, 3.0f),
                    new PointValue(5, 5.0f),
                    new PointValue(6, 8.0f));
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
        }

        Line line = new Line(values);
        line.setShape(shape);
        line.setCubic(isCubic);
        line.setFilled(isFilled);
        line.setHasLabels(hasLabels);
        line.setHasLabelsOnlyForSelected(hasLabelForSelected);
        line.setHasLines(hasLines);
        line.setHasPoints(hasPoints);
        line.setColor(SystemUtils.getColor(getContext(), R.color.alpha_font_color_orange));
        //line.setHasGradientToTransparent(hasGradientToTransparent);
        lines.add(line);
        LineChartData data = new LineChartData(lines);


        Axis axisX = new Axis().setHasLines(true).setValues(axisXValues);
        Axis axisY = new Axis().setHasLines(true).setValues(axisYValues);
        data.setAxisXBottom(axisX);
        data.setAxisYLeft(axisY);

        data.setBaseValue(Float.NEGATIVE_INFINITY);
        timingChartView.setLineChartData(data);
    }
}
