package com.icourt.alpha.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.base.BaseActivity;
import com.icourt.alpha.entity.bean.TimeEntity;
import com.icourt.alpha.utils.DateUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Description  计时详情
 * Company Beijing icourt
 * author  lu.zhao  E-mail:zhaolu@icourt.cc
 * date createTime：17/5/10
 * version 2.0.0
 */

public class TimerDetailActivity extends BaseActivity {

    private static final String KEY_TIME = "key_time";
    @BindView(R.id.titleBack)
    ImageView titleBack;
    @BindView(R.id.titleContent)
    TextView titleContent;
    @BindView(R.id.titleAction)
    ImageView titleAction;
    @BindView(R.id.titleView)
    AppBarLayout titleView;
    @BindView(R.id.minus_time_image)
    ImageView minusTimeImage;
    @BindView(R.id.use_time_tv)
    TextView useTimeTv;
    @BindView(R.id.add_time_image)
    ImageView addTimeImage;
    @BindView(R.id.time_day_tv)
    TextView timeDayTv;
    @BindView(R.id.start_time_min_tv)
    TextView startTimeMinTv;
    @BindView(R.id.stop_time_min_tv)
    TextView stopTimeMinTv;
    @BindView(R.id.time_name_tv)
    TextView timeNameTv;
    @BindView(R.id.project_name_tv)
    TextView projectNameTv;
    @BindView(R.id.project_layout)
    LinearLayout projectLayout;
    @BindView(R.id.worktype_name_tv)
    TextView worktypeNameTv;
    @BindView(R.id.worktype_layout)
    LinearLayout worktypeLayout;
    @BindView(R.id.task_name_tv)
    TextView taskNameTv;
    @BindView(R.id.task_layout)
    LinearLayout taskLayout;

    TimeEntity.ItemEntity itemEntity;

    public static void launch(@NonNull Context context, @NonNull TimeEntity.ItemEntity timeEntity) {
        if (context == null) return;
        Intent intent = new Intent(context, TimerDetailActivity.class);
        intent.putExtra(KEY_TIME, timeEntity);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_detail_layout);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void initView() {
        super.initView();
        itemEntity = (TimeEntity.ItemEntity) getIntent().getSerializableExtra(KEY_TIME);
        setTitle("计时详情");
        setDataToView();
    }

    /**
     * 设置数据到view
     */
    private void setDataToView() {
        if (itemEntity != null) {
            useTimeTv.setText(DateUtils.getTimeDurationDate(itemEntity.useTime));
            timeDayTv.setText(DateUtils.getTimeDateFormatYear(itemEntity.startTime));
            startTimeMinTv.setText(DateUtils.getTimeDurationDate(itemEntity.startTime));
            stopTimeMinTv.setText(DateUtils.getTimeDurationDate(itemEntity.endTime));

            timeNameTv.setText(itemEntity.name);
            projectNameTv.setText(itemEntity.matterName);
            worktypeNameTv.setText(itemEntity.workTypeName);
            taskNameTv.setText(TextUtils.isEmpty(itemEntity.taskPkId) ? "未关联" : itemEntity.taskPkId);
        }
    }

    @OnClick({R.id.minus_time_image, R.id.add_time_image, R.id.project_layout, R.id.worktype_layout, R.id.task_layout})
    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.minus_time_image://－时间
                break;
            case R.id.add_time_image://＋时间
                break;
            case R.id.project_layout://所属项目
                break;
            case R.id.worktype_layout://工作类型
                break;
            case R.id.task_layout://关联任务
                break;
        }
    }
}
