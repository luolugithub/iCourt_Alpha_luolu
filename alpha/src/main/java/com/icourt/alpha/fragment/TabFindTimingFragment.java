package com.icourt.alpha.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.andview.refreshview.XRefreshView;
import com.icourt.alpha.R;
import com.icourt.alpha.activity.TimerDetailActivity;
import com.icourt.alpha.adapter.TimeAdapter;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.base.BaseFragment;
import com.icourt.alpha.entity.bean.ItemPageEntity;
import com.icourt.alpha.entity.bean.TimeEntity;
import com.icourt.alpha.entity.bean.TimingCountEntity;
import com.icourt.alpha.entity.event.TimingEvent;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.utils.DateUtils;
import com.icourt.alpha.utils.SystemUtils;
import com.icourt.alpha.view.xrefreshlayout.RefreshLayout;
import com.icourt.alpha.widget.manager.TimerManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/17
 * version 1.0.0
 */
public class TabFindTimingFragment extends BaseFragment implements BaseRecyclerAdapter.OnItemClickListener {

    @BindView(R.id.tabLayout)
    TabLayout tabLayout;
    @BindView(R.id.timing_date_title)
    TextView timingDateTitle;
    @BindView(R.id.timing_count_total)
    TextView timingCountTotal;
    @BindView(R.id.timing_today_total)
    TextView timingTodayTotal;
    @BindView(R.id.timing_chart_view)
    LineChartView timingChartView;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.refreshLayout)
    RefreshLayout refreshLayout;
    Unbinder unbinder;

    public static TabFindTimingFragment newInstance() {
        return new TabFindTimingFragment();
    }

    private LineChartData data;
    private int numberOfPoints = 7;
    private boolean hasLines = true;

    private boolean hasPoints = false;//不要圆点
    private ValueShape shape = ValueShape.CIRCLE;
    private boolean isFilled = true;
    private boolean hasLabels = false;
    private boolean isCubic = true;
    private boolean hasLabelForSelected = false;

    private final int weekMillSecond = 7 * 24 * 60 * 60 * 1000;
    private TimeAdapter timeAdapter;
    private final List<TimingCountEntity> timingCountEntities = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(R.layout.fragment_tab_find_timing, inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    int pageIndex = 0;

    @Override
    protected void initView() {
        EventBus.getDefault().register(this);
        tabLayout.addTab(tabLayout.newTab().setText("我的计时"), 0, true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(timeAdapter = new TimeAdapter(true));
        timeAdapter.setOnItemClickListener(this);

        String weekStart = new SimpleDateFormat("MMM月dd日").format(DateUtils.getCurrWeekStartTime());
        String weekEnd = new SimpleDateFormat("MMM月dd日").format(DateUtils.getCurrWeekEndTime());
        timingDateTitle.setText(String.format("%s-%s", weekStart, weekEnd));
        generateData();

        resetViewport();

        refreshLayout.setXRefreshViewListener(new XRefreshView.SimpleXRefreshListener() {
            @Override
            public void onRefresh(boolean isPullDown) {
                super.onRefresh(isPullDown);
                getData(true);
            }

            @Override
            public void onLoadMore(boolean isSilence) {
                super.onLoadMore(isSilence);
                getData(false);
            }
        });
        refreshLayout.setPullLoadEnable(true);
        refreshLayout.startRefresh();
    }


    private void stopRefresh() {
        if (refreshLayout != null) {
            refreshLayout.stopRefresh();
            refreshLayout.stopLoadMore();
        }
    }

    @Override
    protected void getData(final boolean isRefresh) {
        super.getData(isRefresh);
        if (isRefresh) {
            pageIndex = 0;
            getCurrentWeekTimingCount();
        }
        String weekStartTime = new SimpleDateFormat("yyyy-MM-dd").format(DateUtils.getCurrWeekStartTime() - (pageIndex * weekMillSecond));
        String weekEndTime = getFromatTime(DateUtils.getCurrWeekEndTime() - (pageIndex * weekMillSecond));
        getApi().timingListQueryByTime(getLoginUserId(), weekStartTime, weekEndTime, 0, 1000)
                .enqueue(new SimpleCallBack<TimeEntity>() {
                    @Override
                    public void onSuccess(Call<ResEntity<TimeEntity>> call, Response<ResEntity<TimeEntity>> response) {
                        if (response.body().result != null) {
                            pageIndex += 1;
                            timeAdapter.bindData(isRefresh, response.body().result.items);
                            stopRefresh();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResEntity<TimeEntity>> call, Throwable t) {
                        super.onFailure(call, t);
                        stopRefresh();
                    }
                });
    }

    private void getCurrentWeekTimingCount() {
        String weekStartTime = getFromatTime(DateUtils.getCurrWeekStartTime());
        String weekEndTime = getFromatTime(DateUtils.getCurrWeekEndTime());
        getApi().queryTimingCountByTime(weekStartTime, weekEndTime)
                .enqueue(new SimpleCallBack<ItemPageEntity<TimingCountEntity>>() {
                    @Override
                    public void onSuccess(Call<ResEntity<ItemPageEntity<TimingCountEntity>>> call, Response<ResEntity<ItemPageEntity<TimingCountEntity>>> response) {
                        if (response.body().result != null) {
                            timingCountEntities.clear();
                            timingCountEntities.addAll(response.body().result.items);
                            generateData();
                        }
                    }
                });

    }

    private String getFromatTime(long time) {
        return new SimpleDateFormat("yyyy-MM-dd").format(time);
    }


    private void resetViewport() {
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
        resetViewport();
        List<Line> lines = new ArrayList<Line>();

        List<PointValue> values = new ArrayList<PointValue>();
        List<AxisValue> axisXValues = Arrays.asList(
                new AxisValue(0).setLabel("星期一"),
                new AxisValue(1).setLabel("星期二"),
                new AxisValue(2).setLabel("星期三"),
                new AxisValue(3).setLabel("星期四"),
                new AxisValue(4).setLabel("星期五"),
                new AxisValue(5).setLabel("星期六"),
                new AxisValue(6).setLabel("星期七"));
        List<AxisValue> axisYValues = new ArrayList<>();
        for (int i = 0; i <= 24; i += 4) {
            axisYValues.add(new AxisValue(i).setLabel(String.format("%sh", i)));
        }
        long totalHours = 0;
        int day_of_week = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        for (int j = 0; j < numberOfPoints; j++) {
            float countTime = 0;
            if (j < timingCountEntities.size()) {
                TimingCountEntity itemEntity = timingCountEntities.get(j);
                if (itemEntity != null) {
                    long ss = 1000;
                    long mi = ss * 60;
                    long hh = mi * 60;
                    long dd = hh * 24;

                    long day = itemEntity.timingCount / dd;
                    long hour = (itemEntity.timingCount - day * dd) / hh;
                    countTime = hour;
                }
            }
            if (day_of_week == j) {
                timingTodayTotal.setText(String.valueOf(countTime));
            }
            totalHours += countTime;
            if (countTime >= 24) {
                countTime = 23.9f;
            }
            log("--------j:" + j + "  time:" + countTime);
            values.add(new PointValue(j, countTime));
        }
        timingCountTotal.setText(String.valueOf(totalHours));

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
        data = new LineChartData(lines);


        Axis axisX = new Axis().setHasLines(true).setValues(axisXValues);
        Axis axisY = new Axis().setHasLines(true);
        //.setValues(axisYValues);
        data.setAxisXBottom(axisX);
        data.setAxisYLeft(axisY);

        //data.setBaseValue(Float.NEGATIVE_INFINITY);
        timingChartView.setLineChartData(data);

    }

    @OnClick({R.id.titleAction})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.titleAction:
                TimerDetailActivity.launchAdd(getContext());
                break;
            default:
                super.onClick(v);
                break;
        }
    }

    /**
     * 计时事件
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTimerEvent(TimingEvent event) {
        if (event == null) return;
        switch (event.action) {
            case TimingEvent.TIMING_ADD:
                List<TimeEntity.ItemEntity> data = timeAdapter.getData();
                for (int i = 0; i < data.size(); i++) {
                    TimeEntity.ItemEntity itemEntity = data.get(i);
                    if (itemEntity != null && itemEntity.state == TimeEntity.ItemEntity.TIMER_STATE_START) {
                        itemEntity.state = TimeEntity.ItemEntity.TIMER_STATE_STOP;
                    }
                }
                timeAdapter.notifyDataSetChanged();
                TimeEntity.ItemEntity timer = TimerManager.getInstance().getTimer();
                if (timer == null) return;
                if (!timeAdapter.getData().contains(timer)) {
                    timeAdapter.addItem(0, timer);
                }
                break;
            case TimingEvent.TIMING_UPDATE_PROGRESS:
                TimeEntity.ItemEntity itemEntity = TimeEntity.ItemEntity.singleInstace;
                itemEntity.pkId = event.timingId;
                int indexOf = timeAdapter.getData().indexOf(itemEntity);
                if (indexOf >= 0) {
                    TimeEntity.ItemEntity item = timeAdapter.getItem(indexOf);
                    item.state = TimeEntity.ItemEntity.TIMER_STATE_START;
                    item.useTime = event.timingSecond;

                    timeAdapter.updateItem(item);
                }
                break;
            case TimingEvent.TIMING_STOP:
                TimeEntity.ItemEntity itemEntity2 = TimeEntity.ItemEntity.singleInstace;
                itemEntity2.pkId = event.timingId;
                int indexOf2 = timeAdapter.getData().indexOf(itemEntity2);
                if (indexOf2 >= 0) {
                    TimeEntity.ItemEntity item = timeAdapter.getData().get(indexOf2);
                    item.state = TimeEntity.ItemEntity.TIMER_STATE_STOP;
                    timeAdapter.updateItem(item);
                }
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public void onItemClick(BaseRecyclerAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
        TimeEntity.ItemEntity item = timeAdapter.getItem(timeAdapter.getRealPos(position));
        TimerDetailActivity.launch(getContext(), item);
    }
}
