package com.icourt.alpha.fragment;

import android.support.design.widget.AppBarLayout;
import android.view.MotionEvent;
import android.view.View;

import com.icourt.alpha.base.BaseFragment;
import com.icourt.alpha.constants.TimingConfig;
import com.icourt.alpha.entity.bean.TimingStatisticEntity;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.interfaces.OnTimingChangeListener;
import com.icourt.alpha.utils.DateUtils;
import com.icourt.alpha.utils.LogUtils;

import lecho.lib.hellocharts.view.LineChartView;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Description 日、周、月、年计时列表的基类
 * Company Beijing icourt
 * author zhaodanyang E-mail:zhaodanyang@icourt.cc
 * date createTime: 2017/10/14
 * version
 */

public abstract class BaseTimingListFragment extends BaseFragment {

    /**
     * 获取监听，监听Appbar的隐藏显示、监听日期的左右切换、监听获取总计时的接口
     *
     * @return
     */
    protected OnTimingChangeListener getParentListener() {
        if (getActivity() != null && getActivity() instanceof OnTimingChangeListener) {
            return (OnTimingChangeListener) getActivity();
        }
        return null;
    }

    /**
     * 设置折线图的padding值
     *
     * @param chartView
     * @param paddingLeft
     * @param paddingTop
     * @param paddingRight
     * @param paddingBottom
     */
    protected void setChartViewPadding(LineChartView chartView, int paddingLeft, int paddingTop, int paddingRight, int paddingBottom) {
        if (chartView == null) return;
        chartView.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
    }

    /**
     * 监听AppBar的缺省，来控制标题栏的显示情况
     *
     * @param appBarLayout
     */
    protected void addAppbarHidenListener(AppBarLayout appBarLayout) {
        if (appBarLayout == null) return;
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                LogUtils.i("appbar verticalOffset" + verticalOffset);
                LogUtils.i("appbar height" + appBarLayout.getHeight());

                if (getParentListener() != null) {
                    if (verticalOffset < -appBarLayout.getHeight() * 0.9) {//如果滚动距离超过了AppBar高度的百分之90
                        getParentListener().onHeaderHide(true);
                    } else {
                        getParentListener().onHeaderHide(false);
                    }
                }
            }
        });
    }

    /**
     * 解决折线图和AppBarlayout在CoordinatorLayout中的滑动冲突问题
     *
     * @param appBarLayout
     * @param chartView
     */
    protected void dispatchTouchEvent(final AppBarLayout appBarLayout, LineChartView chartView) {
        if (appBarLayout == null || chartView == null) return;
        chartView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        appBarLayout.getParent().requestDisallowInterceptTouchEvent(true);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        appBarLayout.getParent().requestDisallowInterceptTouchEvent(true);
                        break;
                    case MotionEvent.ACTION_UP:
                        appBarLayout.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                    default:
                        break;
                }
                return false;
            }
        });
    }

    /**
     * 获取计时统计数据，日、周、月、年的总计时，以及显示在折线图上的数据
     *
     * @param queryType 查询的时间区间类型：day，天；week，周；month，月；year，年；
     * @param startTime 起始时间，时间戳（服务端要格式化成yyyy-MM-dd格式的字符串）
     * @param endTime   结束时间，（服务端要格式化成yyyy-MM-dd格式的字符串）
     */
    protected void getTimingStatistic(@TimingConfig.TIMINGQUERYTYPE int queryType, long startTime, long endTime) {
        String type = "day";
        switch (queryType) {
            case TimingConfig.TIMING_QUERY_BY_DAY:
                type = "day";
                break;
            case TimingConfig.TIMING_QUERY_BY_WEEK:
                type = "week";
                break;
            case TimingConfig.TIMING_QUERY_BY_MONTH:
                type = "month";
                break;
            case TimingConfig.TIMING_QUERY_BY_YEAR:
                type = "year";
                break;
            default:
                break;
        }
        String startTimeStr = DateUtils.getyyyy_MM_dd(startTime);
        String endTimeStr = DateUtils.getyyyy_MM_dd(endTime);
        callEnqueue(
                getApi().getTimingStatistic(type, startTimeStr, endTimeStr),
                new SimpleCallBack<TimingStatisticEntity>() {

                    @Override
                    public void onSuccess(Call<ResEntity<TimingStatisticEntity>> call, Response<ResEntity<TimingStatisticEntity>> response) {
                        if (response.body().result != null) {
                            getTimingStatisticSuccess(response.body().result);
                        }
                    }
                }
        );
    }

    /**
     * 获取计时统计数据成功
     *
     * @param statisticEntity
     */
    protected void getTimingStatisticSuccess(TimingStatisticEntity statisticEntity) {

    }

}
