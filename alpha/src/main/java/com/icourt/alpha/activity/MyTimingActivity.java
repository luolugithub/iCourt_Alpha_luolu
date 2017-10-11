package com.icourt.alpha.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.adapter.baseadapter.BaseRefreshFragmentAdapter;
import com.icourt.alpha.base.BaseActivity;
import com.icourt.alpha.base.BaseDialogFragment;
import com.icourt.alpha.base.BaseFragment;
import com.icourt.alpha.constants.TimingConfig;
import com.icourt.alpha.entity.bean.TimingCountEntity;
import com.icourt.alpha.entity.bean.TimingSelectEntity;
import com.icourt.alpha.fragment.TaskListCalendarFragment;
import com.icourt.alpha.fragment.TimingDayListFragment;
import com.icourt.alpha.fragment.TimingListFragment;
import com.icourt.alpha.fragment.TimingWeekListFragment;
import com.icourt.alpha.fragment.dialogfragment.TimingSelectDialogFragment;
import com.icourt.alpha.interfaces.OnFragmentCallBackListener;
import com.icourt.alpha.utils.DateUtils;
import com.icourt.alpha.utils.SystemUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
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
public class MyTimingActivity extends BaseActivity implements OnFragmentCallBackListener {

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

    BaseRefreshFragmentAdapter baseFragmentAdapter;

    @TimingConfig.TIMINGQUERYTYPE
    int timingQueryType = TIMING_QUERY_BY_WEEK;//默认按周

    public static void launch(@NonNull Context context) {
        if (context == null) return;
        Intent intent = new Intent(context, MyTimingActivity.class);
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


//        viewPager.setAdapter(baseFragmentAdapter = new BaseRefreshFragmentAdapter(getSupportFragmentManager()) {
//            @Override
//            public Fragment getItem(int position) {
//                int distance = position - CENTER_PAGE;
//                long startTime = 0;
//                switch (timingQueryType) {
//                    case TIMING_QUERY_BY_DAY:
//                        startTime = DateUtils.getTodayStartTime() + distance * TimeUnit.DAYS.toMillis(1);
//                        break;
//                    case TIMING_QUERY_BY_WEEK:
//                        startTime = DateUtils.getCurrWeekStartTime() + distance * TimeUnit.DAYS.toMillis(7);
//                        break;
//                    case TIMING_QUERY_BY_MONTH:
//                        break;
//                    case TIMING_QUERY_BY_YEAR:
//                        break;
//                }
//                return TimingListFragment.newInstance(timingQueryType, startTime);
//            }
//
//            @Override
//            public int getCount() {
//                return MAX_PAGE;
//            }
//        });
//        viewPager.setCurrentItem(CENTER_PAGE, false);
    }

    @OnClick({R.id.timing_date_title_tv})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.timing_date_title_tv:
                showTimingSelectDialogFragment();
                break;
            default:
                super.onClick(v);
                break;
        }
    }

    protected Fragment addOrShowFragmentAnim(@NonNull Fragment targetFragment, @IdRes int containerViewId, boolean isAnim) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        if (isAnim) {
            transaction.setCustomAnimations(R.anim.fragment_slide_top_in, R.anim.fragment_slide_top_out);
        }
        transaction.replace(containerViewId, targetFragment, String.valueOf(targetFragment.hashCode())).commitAllowingStateLoss();
        transaction.addToBackStack(null);
        return targetFragment;
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
        TimingSelectDialogFragment.newInstance()
                .show(mFragTransaction, tag);
    }

    @Override
    public void onFragmentCallBack(Fragment fragment, int type, Bundle params) {
        if (fragment instanceof TimingSelectDialogFragment) {
            TimingSelectEntity timingSelectEntity = (TimingSelectEntity) params.getSerializable(BaseDialogFragment.KEY_FRAGMENT_RESULT);
            if (timingSelectEntity == null) return;
            if (type == TimingConfig.TIMING_QUERY_BY_DAY) {//日
                TimingDayListFragment dayListFragment = TimingDayListFragment.newInstance(timingSelectEntity.startTimeMillis);
                addOrShowFragmentAnim(dayListFragment, R.id.fl_container, true);
            } else if (type == TimingConfig.TIMING_QUERY_BY_WEEK) {//周
                TimingWeekListFragment weekListFragment = TimingWeekListFragment.newInstance(timingSelectEntity.startTimeMillis);
                addOrShowFragmentAnim(weekListFragment, R.id.fl_container, true);
            } else if (type == TimingConfig.TIMING_QUERY_BY_MONTH) {//月

            } else if (type == TimingConfig.TIMING_QUERY_BY_YEAR) {//年

            }
        }
    }


}
