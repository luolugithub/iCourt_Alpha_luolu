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
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.base.BaseActivity;
import com.icourt.alpha.base.BaseDialogFragment;
import com.icourt.alpha.constants.TimingConfig;
import com.icourt.alpha.entity.bean.TimingSelectEntity;
import com.icourt.alpha.fragment.TimingDayListFragment;
import com.icourt.alpha.fragment.TimingMonthListFragment;
import com.icourt.alpha.fragment.TimingWeekListFragment;
import com.icourt.alpha.fragment.TimingYearListFragment;
import com.icourt.alpha.fragment.dialogfragment.TimingSelectDialogFragment;
import com.icourt.alpha.interfaces.OnFragmentCallBackListener;

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
        showCurrentWeekFragment();
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

    private void showCurrentWeekFragment() {
        long currentTimeMillis = System.currentTimeMillis();
        TimingWeekListFragment weekListFragment = TimingWeekListFragment.newInstance(currentTimeMillis);
        addOrShowFragmentAnim(weekListFragment, R.id.fl_container, true);
    }

    /**
     * 替换所显示的Fragment
     *
     * @param targetFragment  要替换成哪个Fragment
     * @param containerViewId
     * @param isAnim
     * @return
     */
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
                TimingMonthListFragment monthListFragment = TimingMonthListFragment.newInstance(timingSelectEntity.startTimeMillis);
                addOrShowFragmentAnim(monthListFragment, R.id.fl_container, true);
            } else if (type == TimingConfig.TIMING_QUERY_BY_YEAR) {//年
                TimingYearListFragment yearListFragment = TimingYearListFragment.newInstance(timingSelectEntity.startTimeMillis);
                addOrShowFragmentAnim(yearListFragment, R.id.fl_container, true);
            }
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
