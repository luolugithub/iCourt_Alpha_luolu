package com.icourt.alpha.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.base.BaseActivity;
import com.icourt.alpha.base.BaseDialogFragment;
import com.icourt.alpha.constants.TimingConfig;
import com.icourt.alpha.entity.bean.TimingSelectEntity;
import com.icourt.alpha.fragment.TimingListDayFragment;
import com.icourt.alpha.fragment.TimingListMonthFragment;
import com.icourt.alpha.fragment.TimingListWeekFragment;
import com.icourt.alpha.fragment.TimingListYearFragment;
import com.icourt.alpha.fragment.dialogfragment.TimingSelectDialogFragment;
import com.icourt.alpha.interfaces.OnFragmentCallBackListener;
import com.icourt.alpha.interfaces.OnTimingChangeListener;
import com.icourt.alpha.utils.DateUtils;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Description  我的计时
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/10/9
 * version 2.1.1
 */
public class MyTimingActivity extends BaseActivity implements OnFragmentCallBackListener, OnTimingChangeListener {

    private static final String KEY_TIMING_TYPE = "keyTimingType";
    private static final String KEY_START_TIME = "keyStartTime";

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
    @BindView(R.id.fl_container)
    FrameLayout flContainer;

    @BindView(R.id.ll_today_time)
    LinearLayout llTodayTime;

    Calendar selectedDate = Calendar.getInstance();
    //当前选中的是日、周、月、年的哪一种状态。
    @TimingConfig.TIMINGQUERYTYPE
    int selectedType = TimingConfig.TIMING_QUERY_BY_WEEK;

    public static void launch(@NonNull Context context) {
        if (context == null) {
            return;
        }
        launch(context, TimingConfig.TIMING_QUERY_BY_WEEK, System.currentTimeMillis());
    }

    public static void launch(@NonNull Context context, @TimingConfig.TIMINGQUERYTYPE int type, long timeMillis) {
        if (context == null) {
            return;
        }
        Intent intent = new Intent(context, MyTimingActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_TIMING_TYPE, type);
        bundle.putLong(KEY_START_TIME, timeMillis);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

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
        setTitle(R.string.timing_my_timing);

        Bundle extras = getIntent().getExtras();
        long startTimeMillis = System.currentTimeMillis();
        if (extras != null) {
            selectedType = TimingConfig.convert2timingQueryType(extras.getInt(KEY_TIMING_TYPE, TimingConfig.TIMING_QUERY_BY_WEEK));
            startTimeMillis = extras.getLong(KEY_START_TIME);
            selectedDate.clear();
            selectedDate.setTimeInMillis(startTimeMillis);
        }
        showTargetFragment(selectedType, startTimeMillis, false);
    }

    @OnClick({R.id.ll_all_time, R.id.ll_today_time, R.id.titleAction})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_all_time:
                showTimingSelectDialogFragment();
                break;
            case R.id.ll_today_time:
                showCurrentWeekFragment();
                break;
            case R.id.titleAction:
                TimerAddActivity.launch(getContext());
                break;
            default:
                super.onClick(v);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    /**
     * 显示本周的计时
     */
    private void showCurrentWeekFragment() {
        showTargetFragment(TimingConfig.TIMING_QUERY_BY_WEEK, System.currentTimeMillis(), false);
    }

    /**
     * 显示选中的日期到界面上
     *
     * @param type
     * @param selectedTimeMillis
     */
    private void showSelectedDate(@TimingConfig.TIMINGQUERYTYPE int type, long selectedTimeMillis) {
        if (type == TimingConfig.TIMING_QUERY_BY_DAY) {//日
            String date = DateUtils.getMMMdd(selectedTimeMillis);
            timingDateTitleTv.setText(date);
        } else if (type == TimingConfig.TIMING_QUERY_BY_WEEK) {//周，周需要考虑又没有跨年
            long weekStartTime = DateUtils.getWeekStartTime(selectedTimeMillis);
            long weekEndTime = DateUtils.getWeekEndTime(selectedTimeMillis);
            String startDate;
            String endDate;
            if (DateUtils.getYear(System.currentTimeMillis()) == DateUtils.getYear(weekStartTime)
                    && DateUtils.getYear(System.currentTimeMillis()) == DateUtils.getYear(weekEndTime)) {//开始和结束时间都是是今年，不需要显示年份
                startDate = DateUtils.getMMdd(weekStartTime);
                endDate = DateUtils.getMMdd(weekEndTime);
            } else {//需要显示年份
                startDate = DateUtils.getyyyyMMdd(weekStartTime);
                endDate = DateUtils.getyyyyMMdd(weekEndTime);
            }
            timingDateTitleTv.setText(getString(R.string.timing_date_contact, startDate, endDate));
        } else if (type == TimingConfig.TIMING_QUERY_BY_MONTH) {//月
            String date = DateUtils.getyyyyMM(selectedTimeMillis);
            timingDateTitleTv.setText(date);
        } else if (type == TimingConfig.TIMING_QUERY_BY_YEAR) {//年
            int year = DateUtils.getYear(selectedTimeMillis);
            timingDateTitleTv.setText(getString(R.string.timing_year, String.valueOf(year)));
        }
    }

    /**
     * 显示选中日期和今天的总计时到界面上
     *
     * @param selectedTimeSum
     * @param todayTimeSum
     */
    private void showTimeSum(@TimingConfig.TIMINGQUERYTYPE int type, long selectedTimeSum, long todayTimeSum) {
        timingTodayTotal.setText(DateUtils.getHm(todayTimeSum));
        if (type == TimingConfig.TIMING_QUERY_BY_DAY) {
            timingCountTotalTv.setText("");
        } else {
            timingCountTotalTv.setText(DateUtils.getHm(selectedTimeSum));
        }
    }

    /**
     * 展示时间选择对话框
     */
    private void showTimingSelectDialogFragment() {
        String tag = TimingSelectDialogFragment.class.getSimpleName();
        FragmentTransaction mFragTransaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);
        if (fragment != null) {
            mFragTransaction.remove(fragment);
        }
        TimingSelectDialogFragment.newInstance(selectedType, selectedDate.getTimeInMillis())
                .show(mFragTransaction, tag);
    }

    /**
     * 根据type和起始时间，替换fragment
     *
     * @param type
     * @param startTimeMillis
     */
    private void showTargetFragment(@TimingConfig.TIMINGQUERYTYPE int type, long startTimeMillis, boolean isAnim) {
        Fragment selectedFragment = null;
        switch (type) {
            //日
            case TimingConfig.TIMING_QUERY_BY_DAY:
                selectedFragment = TimingListDayFragment.newInstance(startTimeMillis);
                break;
            //周
            case TimingConfig.TIMING_QUERY_BY_WEEK:
                selectedFragment = TimingListWeekFragment.newInstance(startTimeMillis);
                break;
            //月
            case TimingConfig.TIMING_QUERY_BY_MONTH:
                selectedFragment = TimingListMonthFragment.newInstance(startTimeMillis);
                break;
            //年
            case TimingConfig.TIMING_QUERY_BY_YEAR:
                selectedFragment = TimingListYearFragment.newInstance(startTimeMillis);
                break;
            default:
                break;
        }
        if (selectedFragment == null) {
            return;
        }
        selectedType = type;
        selectedDate.clear();
        selectedDate.setTimeInMillis(startTimeMillis);
        showSelectedDate(selectedType, startTimeMillis);
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        if (isAnim) {
            transaction.setCustomAnimations(R.anim.fragment_slide_top_in, R.anim.fragment_slide_top_out);
        }
        transaction.replace(R.id.fl_container, selectedFragment, String.valueOf(selectedFragment.hashCode())).commitAllowingStateLoss();
        transaction.addToBackStack(null);
    }

    @Override
    public void onFragmentCallBack(Fragment fragment, int type, Bundle params) {
        if (fragment instanceof TimingSelectDialogFragment) {
            TimingSelectEntity timingSelectEntity = (TimingSelectEntity) params.getSerializable(BaseDialogFragment.KEY_FRAGMENT_RESULT);
            if (timingSelectEntity == null) {
                return;
            }
            int convertType = TimingConfig.convert2timingQueryType(type);
            showTargetFragment(convertType, timingSelectEntity.startTimeMillis, false);
        }
    }

    @Override
    public void onHeaderHide(boolean isHide) {
        if (isHide) {
            if (llTodayTime.getVisibility() == View.VISIBLE) {
                llTodayTime.setVisibility(View.GONE);
            }
        } else {
            if (llTodayTime.getVisibility() == View.GONE) {
                llTodayTime.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onTimeChanged(@TimingConfig.TIMINGQUERYTYPE int type, long selectedTimeMillis) {
        selectedDate.clear();
        selectedDate.setTimeInMillis(selectedTimeMillis);
        selectedType = type;
        showSelectedDate(type, selectedTimeMillis);
    }

    @Override
    public void onTimeSumChanged(@TimingConfig.TIMINGQUERYTYPE int type, long selectedTimeSum, long todayTimeSum) {
        showTimeSum(type, selectedTimeSum, todayTimeSum);
    }
}
