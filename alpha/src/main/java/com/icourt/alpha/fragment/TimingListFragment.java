package com.icourt.alpha.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.andview.refreshview.XRefreshView;
import com.icourt.alpha.R;
import com.icourt.alpha.activity.TimerDetailActivity;
import com.icourt.alpha.activity.TimerTimingActivity;
import com.icourt.alpha.adapter.TimeAdapter;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.adapter.baseadapter.adapterObserver.RefreshViewEmptyObserver;
import com.icourt.alpha.base.BaseFragment;
import com.icourt.alpha.constants.TimingConfig;
import com.icourt.alpha.entity.bean.TimeEntity;
import com.icourt.alpha.entity.event.TimingEvent;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.utils.ActionConstants;
import com.icourt.alpha.utils.DateUtils;
import com.icourt.alpha.utils.StringUtils;
import com.icourt.alpha.view.xrefreshlayout.RefreshLayout;
import com.icourt.alpha.widget.manager.TimerManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Description  计时列表
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/10/9
 * version 2.1.0
 */
public class TimingListFragment extends BaseFragment implements BaseRecyclerAdapter.OnItemClickListener {

    private static final String KEY_START_TIME = "startTime";
    private static final String KEY_QUERY_TYPE = "queryType";

    //以下两个标记为是为了给Fragment在ViewPager中进行缓加载使用的。
    private boolean isVisible;//是否可见

    Unbinder unbinder;

    @Nullable
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.refreshLayout)
    RefreshLayout refreshLayout;

    /**
     * @param queryType
     * @param startTimeMillis 毫秒 开始时间 1:日的开始时间 2:周的开始时间 3:月的开始时间 4:年的开始时间
     * @return
     */
    public static TimingListFragment newInstance(@TimingConfig.TIMINGQUERYTYPE int queryType, long startTimeMillis) {
        TimingListFragment fragment = new TimingListFragment();
        Bundle args = new Bundle();
        args.putLong(KEY_START_TIME, startTimeMillis);
        args.putInt(KEY_QUERY_TYPE, queryType);
        fragment.setArguments(args);
        return fragment;
    }

    @TimingConfig.TIMINGQUERYTYPE
    int queryType;
    long startTimeMillis;
    long endTimeMillis;
    TimeAdapter timeAdapter;

    int pageIndex = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(R.layout.layout_refresh_recyclerview, inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint()) {
            isVisible = true;
            initData();
        } else {
            isVisible = false;
        }
    }

    @Override
    protected void initView() {
        queryType = TimingConfig.convert2timingQueryType(getArguments().getInt(KEY_QUERY_TYPE));
        long startTime = getArguments().getLong(KEY_START_TIME);

        if (queryType == TimingConfig.TIMING_QUERY_BY_DAY) {//日
            startTimeMillis = DateUtils.getDayStartTime(startTime);
            endTimeMillis = DateUtils.getDayEndTime(startTimeMillis);
        } else if (queryType == TimingConfig.TIMING_QUERY_BY_WEEK) {//周
            startTimeMillis = DateUtils.getWeekStartTime(startTime);
            endTimeMillis = DateUtils.getWeekEndTime(startTimeMillis);
        } else if (queryType == TimingConfig.TIMING_QUERY_BY_MONTH) {//月
            startTimeMillis = DateUtils.getMonthFirstDay(startTime);
            endTimeMillis = DateUtils.getMonthLastDay(startTimeMillis);
        } else if (queryType == TimingConfig.TIMING_QUERY_BY_YEAR) {//年
            startTimeMillis = DateUtils.getYearStartDay(startTime);
            endTimeMillis = DateUtils.getYearLastDay(startTimeMillis);
        }
        recyclerView.setBackgroundColor(Color.WHITE);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(null);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(timeAdapter = new TimeAdapter(true));
        timeAdapter.setOnItemClickListener(this);
        refreshLayout.setNoticeEmpty(R.mipmap.icon_placeholder_timing, R.string.timing_empty);
        refreshLayout.setMoveForHorizontal(true);
        timeAdapter.registerAdapterDataObserver(new RefreshViewEmptyObserver(refreshLayout, timeAdapter));
        refreshLayout.setXRefreshViewListener(new XRefreshView.SimpleXRefreshListener() {
            @Override
            public void onLoadMore(boolean isSilence) {
                super.onLoadMore(isSilence);
                getData(false);
            }
        });
        refreshLayout.setPullRefreshEnable(false);
        boolean canLoadMore = (queryType != TimingConfig.TIMING_QUERY_BY_DAY && queryType != TimingConfig.TIMING_QUERY_BY_WEEK); //年月可以上拉加载
        refreshLayout.setPullLoadEnable(canLoadMore);
        initData();
    }

    private void initData() {
        if (isVisible && isAlreadyInit()) {
            getData(true);
        }
    }

    @Override
    protected void getData(boolean isRefresh) {
        super.getData(isRefresh);
//        long dividerTime = (pageIndex * TimeUnit.DAYS.toMillis(7));
//        long weekStartTimeMillSecond = startTimeMillis - dividerTime;
//        long weekEndTimeMillSecond = weekStartTimeMillSecond + TimeUnit.DAYS.toMillis(7);
//
        String weekStartTime = DateUtils.getyyyy_MM_dd(startTimeMillis);
        String weekEndTime = DateUtils.getyyyy_MM_dd(endTimeMillis);

        int pageSize;
        if (queryType == TimingConfig.TIMING_QUERY_BY_DAY || queryType == TimingConfig.TIMING_QUERY_BY_WEEK) {//如果是日／周，则一次性加载完。
            pageSize = Integer.MAX_VALUE;
        } else {
            pageSize = ActionConstants.DEFAULT_PAGE_SIZE;
        }
        if (isRefresh) {
            pageIndex = 0;
        } else {
            pageIndex += 1;
        }
        timingListQueryByTime(isRefresh, pageIndex, pageSize, weekStartTime, weekEndTime);
    }

    /**
     * 获取某日／周／月／年的计时项（如果日、周一次性加载完数据；如果是月、年则分页加载，一次加载20条）
     *
     * @param weekStartTime
     * @param weekEndTime
     */
    private void timingListQueryByTime(final boolean isRefresh, int pageIndex, int pageSize, String weekStartTime, String weekEndTime) {
        callEnqueue(
                getApi().timingListQueryByTime(getLoginUserId(), weekStartTime, weekEndTime, pageIndex, pageSize),
                new SimpleCallBack<TimeEntity>() {
                    @Override
                    public void onSuccess(Call<ResEntity<TimeEntity>> call, Response<ResEntity<TimeEntity>> response) {
                        if (response.body().result != null) {
                            timeAdapter.bindData(isRefresh, response.body().result.items);
                        }
                        stopRefresh();
                    }

                    @Override
                    public void onFailure(Call<ResEntity<TimeEntity>> call, Throwable t) {
                        super.onFailure(call, t);
                        stopRefresh();
                    }
                }
        );
    }

    private void stopRefresh() {
        if (refreshLayout != null) {
            refreshLayout.stopRefresh();
            refreshLayout.stopLoadMore();
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
                initData();
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
                initData();
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
        unbinder.unbind();
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
