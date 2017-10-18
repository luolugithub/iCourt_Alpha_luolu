package com.icourt.alpha.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bigkoo.pickerview.adapter.WheelAdapter;
import com.bigkoo.pickerview.lib.WheelView;
import com.bigkoo.pickerview.listener.OnItemSelectedListener;
import com.icourt.alpha.R;
import com.icourt.alpha.base.BaseFragment;
import com.icourt.alpha.entity.bean.TimingSelectEntity;
import com.icourt.alpha.utils.DateUtils;
import com.icourt.alpha.widget.manager.TimerDateManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * 计时模块时间选择器的周选择器
 * Created by zhaodanyang on 2017/9/21.
 */

public class TimingSelectWeekFragment extends BaseFragment {

    private static final String KEY_SELECTED_DATE = "keySelectedDate";

    Unbinder unbinder;

    @BindView(R.id.wheelview_week)
    WheelView wheelView;
    @BindView(R.id.titleBack)
    ImageView titleBack;
    @BindView(R.id.titleContent)
    TextView titleContent;
    @BindView(R.id.titleForward)
    ImageView titleForward;
    @BindView(R.id.titleAction)
    TextView titleAction;

    TimeWheelAdapter adapter;
    Calendar currentMonthDate = Calendar.getInstance();//用来记录当前选中的月的时间戳。
    private SimpleDateFormat dateFormatForMonth = new SimpleDateFormat("yyyy年MM月", Locale.getDefault());

    public static TimingSelectWeekFragment newInstance(long selectedDate) {
        TimingSelectWeekFragment fragment = new TimingSelectWeekFragment();
        Bundle bundle = new Bundle();
        bundle.putLong(KEY_SELECTED_DATE, selectedDate);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(R.layout.fragment_timing_select_week, inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }


    @Override
    protected void initView() {

        if (getArguments() != null) {
            long timeMillis = getArguments().getLong(KEY_SELECTED_DATE, System.currentTimeMillis());
            currentMonthDate.setTimeInMillis(timeMillis);
        }

        setMonthData(currentMonthDate);

        wheelView.setTextSize(20);
        try {//预加载10条数据
            adapter = new TimeWheelAdapter();
            ArrayList<TimingSelectEntity> tempMenus = new ArrayList<>();
            //起始时间是2015年1月1日
            Calendar instance = TimerDateManager.getStartDate();
            //当前周的开始时间
            long weekStartTime;
            //当前周的结束时间
            long weekEndTime;
            //先预加载10条，如果一下子把数据全部加载出来，会导致DialogFragment弹出过慢。
            for (int i = 0; i < 5; i++) {
                //所在周开始日期
                weekStartTime = DateUtils.getWeekStartTime(instance.getTimeInMillis());
                weekEndTime = DateUtils.getWeekEndTime(instance.getTimeInMillis());
                TimingSelectEntity timingSelectEntity = new TimingSelectEntity();
                timingSelectEntity.startTimeMillis = weekStartTime;
                timingSelectEntity.endTimeMillis = weekEndTime;
                timingSelectEntity.startTimeStr = DateUtils.getyyyy_MM_dd(weekStartTime);
                timingSelectEntity.endTimeStr = DateUtils.getyyyy_MM_dd(weekEndTime);
                instance.add(Calendar.DAY_OF_YEAR, 1);
                tempMenus.add(timingSelectEntity);
            }
            adapter.setTimeList(tempMenus);
            wheelView.setAdapter(adapter);
        } catch (Exception e) {

        }
        setWeekData();
        wheelView.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int i) {
                TimingSelectEntity item = adapter.getItem(i);
                currentMonthDate.setTimeInMillis(item.endTimeMillis);//设置为周的结束时间，因为以周的结束时间所在月份来显示的
                setMonthData(currentMonthDate);
            }
        });
    }

    /**
     * 设置显示年／月的日期
     *
     * @param calendar
     */
    private void setMonthData(Calendar calendar) {
        if (titleContent == null)
            return;
        titleContent.setText(dateFormatForMonth.format(calendar.getTimeInMillis()));
    }


    /**
     * 设置周数据
     */
    private void setWeekData() {
        Observable.create(new ObservableOnSubscribe<List<TimingSelectEntity>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<List<TimingSelectEntity>> e) throws Exception {
                e.onNext(TimerDateManager.getWeekData());
                e.onComplete();
            }
        }).delay(300, TimeUnit.MILLISECONDS)
                .compose(this.<List<TimingSelectEntity>>bindToLifecycle())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<TimingSelectEntity>>() {
                    @Override
                    public void accept(@NonNull final List<TimingSelectEntity> timingDateEntities) throws Exception {
                        adapter.setTimeList(timingDateEntities);
                        wheelView.invalidate();
                        wheelView.setCyclic(false);
                        selectMonthItem(currentMonthDate);
                    }
                });
    }


    private class TimeWheelAdapter implements WheelAdapter<TimingSelectEntity> {
        List<TimingSelectEntity> timeList = new ArrayList<>();

        public TimeWheelAdapter() {
        }

        public List<TimingSelectEntity> getTimeList() {
            return timeList;
        }

        public void setTimeList(List<TimingSelectEntity> timeList) {
            this.timeList = timeList;
        }

        @Override
        public int getItemsCount() {
            return timeList.size();
        }

        @Override
        public TimingSelectEntity getItem(int i) {
            return timeList.get(i);
        }

        @Override
        public int indexOf(TimingSelectEntity o) {
            return timeList.indexOf(o);
        }

    }

    @OnClick({R.id.titleBack,
            R.id.titleForward,
            R.id.titleAction})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.titleBack:
                setBeforeOrLastMonth(false);
                break;
            case R.id.titleForward:
                setBeforeOrLastMonth(true);
                break;
            case R.id.titleAction:
                scrollToToday();
                break;
            default:
                super.onClick(v);
                break;
        }
    }

    /**
     * 滚动到今天所在周
     */
    private void scrollToToday() {
        currentMonthDate.clear();
        currentMonthDate.setTimeInMillis(System.currentTimeMillis());
        setMonthData(currentMonthDate);
        selectMonthItem(currentMonthDate);
    }

    /**
     * 获取前／后一个月
     *
     * @param isNext false，前一个月；true，后一个月。
     * @return
     */
    private void setBeforeOrLastMonth(boolean isNext) {
        int year = currentMonthDate.get(Calendar.YEAR);
        int month = currentMonthDate.get(Calendar.MONTH);
        //如果是减月份，并且当前已经到了2015年1月1日，说明到最起始时间了，不能再减了
        if (!isNext && year == 2015 && month == Calendar.JANUARY) {
            return;
        }
        Calendar instance = Calendar.getInstance();
        instance.setTimeInMillis(System.currentTimeMillis());
        int lastYear = instance.get(Calendar.YEAR);
        int lastMonth = instance.get(Calendar.MONTH);
        //如果是加月份，并且当前已经到了当前时间的最后一个月，不能再加了
        if (isNext && year == lastYear && month == lastMonth) {
            return;
        }
        if (isNext) {
            currentMonthDate.add(Calendar.MONTH, 1);
        } else {
            currentMonthDate.add(Calendar.MONTH, -1);
        }
        currentMonthDate.set(Calendar.DAY_OF_MONTH, 1);
        setMonthData(currentMonthDate);
        selectMonthItem(currentMonthDate);
    }

    /**
     * 滚动到指定的月份
     *
     * @param calendar
     */
    private void selectMonthItem(Calendar calendar) {
        if (adapter == null || adapter.getTimeList().isEmpty()) {
            return;
        }
        int position = 0;
        for (int i = 0; i < adapter.getTimeList().size(); i++) {
            TimingSelectEntity timingSelectEntity = adapter.getTimeList().get(i);
            if (calendar.getTimeInMillis() >= timingSelectEntity.startTimeMillis && calendar.getTimeInMillis() <= timingSelectEntity.endTimeMillis) {
                position = i;
            }
        }
        wheelView.setCurrentItem(position);
    }

    @Override
    public Bundle getFragmentData(int type, Bundle inBundle) {
        Bundle arguments = new Bundle();
        TimingSelectEntity timingSelectEntity = adapter.getItem(wheelView.getCurrentItem());
        if (timingSelectEntity != null) {
            arguments.putSerializable(KEY_FRAGMENT_RESULT, timingSelectEntity);
        }
        return arguments;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
