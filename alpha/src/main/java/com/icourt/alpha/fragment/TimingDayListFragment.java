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
import com.icourt.alpha.base.BaseFragment;
import com.icourt.alpha.constants.TimingConfig;
import com.icourt.alpha.entity.bean.TimeEntity;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.interfaces.OnTimingChangeListener;
import com.icourt.alpha.utils.DateUtils;
import com.icourt.alpha.utils.LogUtils;
import com.icourt.alpha.widget.manager.TimerDateManager;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import lecho.lib.hellocharts.view.LineChartView;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Description 选中日情况下的计时列表
 * Company Beijing icourt
 * author zhaodanyang E-mail:zhaodanyang@icourt.cc
 * date createTime: 2017/10/10
 * version 2.1.1
 */

public class TimingDayListFragment extends BaseFragment {

    private static final String KEY_START_TIME = "key_start_time";

    Unbinder bind;

    @BindView(R.id.timing_chart_view)
    LineChartView timingChartView;
    @BindView(R.id.timing_count_total2_tv)
    TextView timingCountTotal2Tv;
    @BindView(R.id.timing_text_show_timing_ll)
    LinearLayout timingTextShowTimingLl;
    @BindView(R.id.viewPager)
    ViewPager viewPager;

    @BindView(R.id.appbar)
    AppBarLayout appBarLayout;

    BaseRefreshFragmentAdapter baseFragmentAdapter;

    long startTimeMillis;//传递进来的开始时间
    long selectedDayTime;//选中的某一天的开始时间

    public static TimingDayListFragment newInstance(long startTimeMillis) {
        TimingDayListFragment fragment = new TimingDayListFragment();
        Bundle bundle = new Bundle();
        bundle.putLong(KEY_START_TIME, startTimeMillis);
        fragment.setArguments(bundle);
        return fragment;
    }

    private OnTimingChangeListener getParentListener() {
        if (getActivity() != null && getActivity() instanceof OnTimingChangeListener) {
            return (OnTimingChangeListener) getActivity();
        }
        return null;
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
        //监听toolbar的缺省，来控制标题栏的显示情况
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

        if (getArguments() != null)
            startTimeMillis = getArguments().getLong(KEY_START_TIME);

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
            public void onPageSelected(int position) {
                selectedDayTime = calendar.getTimeInMillis() + position * TimeUnit.DAYS.toMillis(1);
                updateDayTime(selectedDayTime, DateUtils.getDayEndTime(selectedDayTime));
                if (getParentListener() != null) {
                    getParentListener().onTimeChanged(TimingConfig.TIMING_QUERY_BY_DAY, selectedDayTime);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        int differentDays = DateUtils.differentDays(calendar.getTimeInMillis(), startTimeMillis);
        viewPager.setCurrentItem(differentDays, false);
        selectedDayTime = calendar.getTimeInMillis() + viewPager.getCurrentItem() * TimeUnit.DAYS.toMillis(1);
        updateDayTime(selectedDayTime, DateUtils.getDayEndTime(selectedDayTime));
    }

    private void updateDayTime(long startTime, long endTime) {
        String weekStartTime = DateUtils.getyyyy_MM_dd(startTime);
        String weekEndTime = DateUtils.getyyyy_MM_dd(endTime);
        callEnqueue(
                getApi().timingListQueryByTime(getLoginUserId(), weekStartTime, weekEndTime, 0, Integer.MAX_VALUE),
                new SimpleCallBack<TimeEntity>() {
                    @Override
                    public void onSuccess(Call<ResEntity<TimeEntity>> call, Response<ResEntity<TimeEntity>> response) {
                        if (response.body().result != null) {
                            //获取完数据，遍历列表
                            if (response.body().result.items != null && response.body().result.items.size() > 0) {
                                traverseTime(response.body().result.items);
                            } else {
                                showSumTime(0);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResEntity<TimeEntity>> call, Throwable t) {
                        super.onFailure(call, t);
                    }
                }
        );
    }

    private void traverseTime(final List<TimeEntity.ItemEntity> items) {
        Observable.create(new ObservableOnSubscribe<Long>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Long> e) throws Exception {
                long timeMillis = 0;
                for (TimeEntity.ItemEntity item : items) {
                    timeMillis += item.useTime;
                }
                e.onNext(timeMillis);
                e.onComplete();
            }
        })
                .compose(this.<Long>bindToLifecycle())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(@NonNull Long aLong) throws Exception {
                        showSumTime(aLong);
                    }
                });
    }

    private void showSumTime(long timeMillis) {
        String hm = DateUtils.getHm(timeMillis);
        timingCountTotal2Tv.setText(hm);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        bind.unbind();
    }
}
