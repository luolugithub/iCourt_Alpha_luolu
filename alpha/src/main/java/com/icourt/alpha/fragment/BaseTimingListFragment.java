package com.icourt.alpha.fragment;

import android.support.annotation.IntDef;
import android.support.design.widget.AppBarLayout;

import com.icourt.alpha.base.BaseFragment;
import com.icourt.alpha.entity.bean.TimingStatisticEntity;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.interfaces.OnTimingChangeListener;
import com.icourt.alpha.utils.DateUtils;
import com.icourt.alpha.utils.LogUtils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

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

    public static final int TYPE_DAY = 1;
    public static final int TYPE_WEEK = 2;
    public static final int TYPE_MONTH = 3;
    public static final int TYPE_YEAR = 4;

    @IntDef({TYPE_DAY, TYPE_WEEK, TYPE_MONTH, TYPE_YEAR})
    @Retention(RetentionPolicy.SOURCE)
    @interface TimingQueryType {

    }

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
     * 获取计时统计数据，日、周、月、年的总计时，以及显示在折线图上的数据
     *
     * @param queryType 查询的时间区间类型：day，天；week，周；month，月；year，年；
     * @param startTime 起始时间，时间戳（服务端要格式化成yyyy-MM-dd格式的字符串）
     * @param endTime   结束时间，（服务端要格式化成yyyy-MM-dd格式的字符串）
     */
    protected void getTimingStatistic(@TimingQueryType int queryType, long startTime, long endTime) {
        String type = "day";
        switch (queryType) {
            case TYPE_DAY:
                type = "day";
                break;
            case TYPE_WEEK:
                type = "week";
                break;
            case TYPE_MONTH:
                type = "month";
                break;
            case TYPE_YEAR:
                type = "year";
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
