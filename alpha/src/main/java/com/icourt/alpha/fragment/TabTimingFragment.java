package com.icourt.alpha.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.andview.refreshview.XRefreshView;
import com.icourt.alpha.R;
import com.icourt.alpha.activity.TimerAddActivity;
import com.icourt.alpha.activity.TimerDetailActivity;
import com.icourt.alpha.activity.TimerTimingActivity;
import com.icourt.alpha.adapter.TimeAdapter;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.adapter.baseadapter.adapterObserver.DataChangeAdapterObserver;
import com.icourt.alpha.base.BaseFragment;
import com.icourt.alpha.entity.bean.ItemPageEntity;
import com.icourt.alpha.entity.bean.TimeEntity;
import com.icourt.alpha.entity.bean.TimingCountEntity;
import com.icourt.alpha.entity.event.TimingEvent;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.utils.DateUtils;
import com.icourt.alpha.utils.DensityUtil;
import com.icourt.alpha.utils.StringUtils;
import com.icourt.alpha.utils.SystemUtils;
import com.icourt.alpha.view.CustomerXRefreshViewFooter;
import com.icourt.alpha.view.CustomerXRefreshViewHeader;
import com.icourt.alpha.view.xrefreshlayout.RefreshLayout;
import com.icourt.alpha.widget.manager.TimerManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
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
public class TabTimingFragment extends BaseFragment implements BaseRecyclerAdapter.OnItemClickListener {

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
    @BindView(R.id.titleAction)
    ImageView titleAction;
    @BindView(R.id.empty_layout)
    LinearLayout emptyLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(R.layout.fragment_tab_find_timing, inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getData(true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    public static TabTimingFragment newInstance() {
        return new TabTimingFragment();
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

    private final long weekMillSecond = 7 * 24 * 60 * 60 * 1000;
    private TimeAdapter timeAdapter;
    private final List<TimingCountEntity> timingCountEntities = new ArrayList<>();
    int pageIndex = 0;
    CustomerXRefreshViewFooter customerXRefreshViewFooter;
    CustomerXRefreshViewHeader customerXRefreshViewHeader;

    @Override
    protected void initView() {
        EventBus.getDefault().register(this);
        tabLayout.addTab(tabLayout.newTab().setText("我的计时"), 0, true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(timeAdapter = new TimeAdapter(true));
        timeAdapter.setOnItemClickListener(this);

        String weekStart = DateUtils.getMMMdd(DateUtils.getCurrWeekStartTime());
        String weekEnd = DateUtils.getMMMdd(DateUtils.getCurrWeekEndTime());
        timingDateTitle.setText(String.format("%s-%s", weekStart, weekEnd));
        generateData();

        resetViewport();

        customerXRefreshViewFooter = new CustomerXRefreshViewFooter(getContext());
        int dp20 = DensityUtil.dip2px(getContext(), 20);
        customerXRefreshViewFooter.setPadding(0, dp20, 0, dp20);
        customerXRefreshViewFooter.setFooterLoadmoreTitle("加载前一周");
        refreshLayout.setCustomFooterView(customerXRefreshViewFooter);

        customerXRefreshViewHeader = new CustomerXRefreshViewHeader(getContext());
        customerXRefreshViewHeader.setPadding(0, dp20, 0, 0);
        customerXRefreshViewHeader.setHeaderRefreshTitle("加载后一周");
        refreshLayout.setCustomHeaderView(customerXRefreshViewHeader);

        //refreshLayout.setNoticeEmpty(R.mipmap.icon_placeholder_timing, "暂无计时");
        timeAdapter.registerAdapterDataObserver(new DataChangeAdapterObserver() {
            @Override
            protected void updateUI() {
                if (emptyLayout != null) {
                    emptyLayout.setVisibility(timeAdapter.getItemCount() <= 0 ? View.VISIBLE : View.GONE);
                }
            }
        });
        refreshLayout.setXRefreshViewListener(new XRefreshView.SimpleXRefreshListener() {
            @Override
            public void onRefresh(boolean isPullDown) {
                super.onRefresh(isPullDown);
                if (pageIndex > 0) {
                    pageIndex--;
                }
                getData(true);
            }

            @Override
            public void onLoadMore(boolean isSilence) {
                super.onLoadMore(isSilence);
                if (RefreshLayout.isLoadMoreMaxDistance(refreshLayout, 1.0f)) {
                    pageIndex++;
                    getData(false);
                } else {
                    stopRefresh();
                }
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
            TimerManager.getInstance().timerQuerySync();
        }
        long dividerTime = (pageIndex * weekMillSecond);
        long weekStartTimeMillSecond = DateUtils.getCurrWeekStartTime() - dividerTime;
        long weekEndTimeMillSecond = DateUtils.getCurrWeekEndTime() - dividerTime;

        String weekStartTime = getFromatTime(weekStartTimeMillSecond);
        String weekEndTime = getFromatTime(weekEndTimeMillSecond);

        String weekStart = DateUtils.getMMMdd(weekStartTimeMillSecond);
        String weekEnd = DateUtils.getMMMdd(weekEndTimeMillSecond);
        timingDateTitle.setText(String.format("%s-%s", weekStart, weekEnd));


        //header 设置
        String preWeekStart = null;
        String preWeekEnd = null;
        if (pageIndex <= 0) {
            preWeekStart = DateUtils.getMMMdd(weekStartTimeMillSecond);
            preWeekEnd = DateUtils.getMMMdd(weekEndTimeMillSecond);
            customerXRefreshViewHeader.setHeaderRefreshTitle("加载本周");
        } else {
            preWeekStart = DateUtils.getMMMdd(weekStartTimeMillSecond + weekMillSecond);
            preWeekEnd = DateUtils.getMMMdd(weekEndTimeMillSecond + weekMillSecond);
            customerXRefreshViewHeader.setHeaderRefreshTitle("加载后一周");
        }
        customerXRefreshViewHeader.setHeaderRefreshDesc(String.format("%s-%s", preWeekStart, preWeekEnd));


        //footer设置
        String lastWeekStart = DateUtils.getMMMdd(weekStartTimeMillSecond - weekMillSecond);
        String lastWeekEnd = DateUtils.getMMMdd(weekEndTimeMillSecond - weekMillSecond);
        customerXRefreshViewFooter.setFooterLoadmoreDesc(String.format("%s-%s", lastWeekStart, lastWeekEnd));

        getWeekTimingCount(weekStartTime, weekEndTime);

        timingListQueryByTime(weekStartTime, weekEndTime);

    }


    /**
     * 获取某周的计时项
     *
     * @param weekStartTime
     * @param weekEndTime
     */
    private void timingListQueryByTime(String weekStartTime, String weekEndTime) {
        getApi().timingListQueryByTime(getLoginUserId(), weekStartTime, weekEndTime, 0, 1000)
                .enqueue(new SimpleCallBack<TimeEntity>() {
                    @Override
                    public void onSuccess(Call<ResEntity<TimeEntity>> call, Response<ResEntity<TimeEntity>> response) {
                        if (response.body().result != null) {
                            timeAdapter.bindData(true, response.body().result.items);
                            stopRefresh();
                            if (timingCountTotal != null)
                                timingCountTotal.setText(getHm(response.body().result.timingSum));
                        }
                    }

                    @Override
                    public void onFailure(Call<ResEntity<TimeEntity>> call, Throwable t) {
                        super.onFailure(call, t);
                        stopRefresh();
                    }
                });
    }

    /**
     * 获取某周的计时统计
     *
     * @param weekStartTime
     * @param weekEndTime
     */
    private void getWeekTimingCount(String weekStartTime, String weekEndTime) {
        getApi().queryTimingCountByTime(weekStartTime, weekEndTime)
                .enqueue(new SimpleCallBack<ItemPageEntity<TimingCountEntity>>() {
                    @Override
                    public void onSuccess(Call<ResEntity<ItemPageEntity<TimingCountEntity>>> call, Response<ResEntity<ItemPageEntity<TimingCountEntity>>> response) {
                        if (response.body().result != null && timingTodayTotal != null) {
                            timingCountEntities.clear();
                            timingCountEntities.addAll(response.body().result.items);
                            generateData();
                            timingTodayTotal.setText(getHm(0));
                            if (response.body().result.items != null && pageIndex <= 0) {
                                for (TimingCountEntity timingCountEntity : response.body().result.items) {
                                    if (timingCountEntity != null) {
                                        boolean isToday = DateUtils.isToday(timingCountEntity.workDate);
                                        if (isToday) {
                                            timingTodayTotal.setText(getHm(timingCountEntity.timingCount));
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                });

    }

    private String getFromatTime(long time) {
        return new SimpleDateFormat("yyyy-MM-dd").format(time);
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
        List<Line> lines = new ArrayList<Line>();

        List<PointValue> values = new ArrayList<PointValue>();
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
        for (int j = 0; j < numberOfPoints; j++) {
            float hour = 0;
            if (j < timingCountEntities.size()) {
                TimingCountEntity itemEntity = timingCountEntities.get(j);
                if (itemEntity != null) {
                    long ss = 1000;
                    long mi = ss * 60;
                    long hh = mi * 60;
                    long dd = hh * 24;

                    long day = itemEntity.timingCount / dd;
                    hour = (itemEntity.timingCount - day * dd) * 1.0f / hh;
                }
            }
            //最大24
            if (hour >= 24) {
                hour = 23.9f;
            }
            log("--------j:" + j + "  time:" + hour);
            values.add(new PointValue(j, hour));
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
        data = new LineChartData(lines);


        Axis axisX = new Axis().setHasLines(true).setValues(axisXValues);
        Axis axisY = new Axis().setHasLines(true).setValues(axisYValues);
        //.setValues(axisYValues);
        data.setAxisXBottom(axisX);
        data.setAxisYLeft(axisY);

        data.setBaseValue(Float.NEGATIVE_INFINITY);
        timingChartView.setLineChartData(data);
    }

    @OnClick({R.id.titleAction})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.titleAction:
                TimerAddActivity.launch(getContext());
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
                getData(true);
                break;
            case TimingEvent.TIMING_UPDATE_PROGRESS:
                TimeEntity.ItemEntity itemEntity = TimeEntity.ItemEntity.singleInstace;
                itemEntity.pkId = event.timingId;
                int indexOf = timeAdapter.getData().indexOf(itemEntity);
                if (indexOf >= 0) {
                    TimeEntity.ItemEntity item = timeAdapter.getItem(indexOf);
                    item.state = TimeEntity.ItemEntity.TIMER_STATE_START;
                    item.useTime = event.timingSecond * 1_000;

                    timeAdapter.updateItem(item);
                }
                break;
            case TimingEvent.TIMING_STOP:
                getData(true);
                break;
        }
    }

    public String getHm(long milliSecond) {
        milliSecond /= 1000;
        long hour = milliSecond / 3600;
        long minute = milliSecond % 3600 / 60;
        return String.format("%d:%02d", hour, minute);
    }


    @Override
    public void onItemClick(BaseRecyclerAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
        TimeEntity.ItemEntity itemEntity = timeAdapter.getItem(timeAdapter.getRealPos(position));
        if (itemEntity != null && StringUtils.equalsIgnoreCase(itemEntity.pkId, TimerManager.getInstance().getTimerId(), false)) {
            TimerTimingActivity.launch(view.getContext(), itemEntity);
        } else {
            TimerDetailActivity.launch(view.getContext(), itemEntity);
        }
    }

}
