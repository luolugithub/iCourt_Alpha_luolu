package com.icourt.alpha.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.base.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import lecho.lib.hellocharts.view.LineChartView;

/**
 * Description  我的计时
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/10/9
 * version 2.1.1
 */
public class MyTimingActivity extends BaseActivity {

    public static void launch(@NonNull Context context) {
        if (context == null) return;
        Intent intent = new Intent(context, MyTimingActivity.class);
        context.startActivity(intent);
    }

    @BindView(R.id.titleBack)
    ImageView titleBack;
    @BindView(R.id.titleContent)
    TextView titleContent;
    @BindView(R.id.titleAction)
    ImageView titleAction;
    @BindView(R.id.titleView)
    AppBarLayout titleView;
    @BindView(R.id.timing_date_title_tv)
    TextView timingDateTitleTv;
    @BindView(R.id.timing_count_total_tv)
    TextView timingCountTotalTv;
    @BindView(R.id.timing_today_total)
    TextView timingTodayTotal;
    @BindView(R.id.timing_chart_view)
    LineChartView timingChartView;
    @BindView(R.id.timing_count_total2_tv)
    TextView timingCountTotal2Tv;
    @BindView(R.id.timing_text_show_timing_ll)
    LinearLayout timingTextShowTimingLl;

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
        setTitle(R.string.timing_my_timing);
        titleAction.setImageResource(R.mipmap.header_icon_add);
    }
}
