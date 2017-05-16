package com.icourt.alpha.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.base.BaseActivity;
import com.icourt.alpha.entity.bean.ProjectEntity;
import com.icourt.alpha.entity.bean.TaskEntity;
import com.icourt.alpha.entity.bean.TaskGroupEntity;
import com.icourt.alpha.entity.bean.TimeEntity;
import com.icourt.alpha.entity.bean.WorkType;
import com.icourt.alpha.fragment.dialogfragment.BaseDialogFragment;
import com.icourt.alpha.fragment.dialogfragment.ProjectSelectDialogFragment;
import com.icourt.alpha.fragment.dialogfragment.TaskSelectDialogFragment;
import com.icourt.alpha.fragment.dialogfragment.WorkTypeSelectDialogFragment;
import com.icourt.alpha.interfaces.OnFragmentCallBackListener;
import com.icourt.alpha.utils.DateUtils;
import com.icourt.alpha.view.CircleTimerView;

import java.io.Serializable;

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

public class TimerDetailActivity extends BaseActivity
        implements ProjectSelectDialogFragment.OnProjectTaskGroupSelectListener, OnFragmentCallBackListener {

    private static final String KEY_TIME = "key_time";


    TimeEntity.ItemEntity itemEntity;
    @BindView(R.id.minus_time_image)
    ImageView minusTimeImage;
    @BindView(R.id.circleTimerView)
    CircleTimerView circleTimerView;
    @BindView(R.id.use_time_date)
    TextView useTimeDate;
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
    private ProjectEntity selectedProjectEntity;
    private TaskGroupEntity selectedTaskGroupEntity;
    private WorkType selectedWorkType;
    private TaskEntity.TaskItemEntity selectedTaskItem;

    public static void launch(@NonNull Context context,
                              @NonNull TimeEntity.ItemEntity timeEntity) {
        if (context == null) return;
        if (timeEntity == null) return;
        Intent intent = new Intent(context, TimerDetailActivity.class);
        intent.putExtra(KEY_TIME, timeEntity);
        context.startActivity(intent);
    }

    /**
     * 新建计时
     *
     * @param context
     */
    public static void launchAdd(@NonNull Context context) {
        if (context == null) return;
        Intent intent = new Intent(context, TimerDetailActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        itemEntity = (TimeEntity.ItemEntity) getIntent().getSerializableExtra(KEY_TIME);
        setContentView(isAddType() ? R.layout.activity_time_detail_layout2 : R.layout.activity_time_detail_layout);
        ButterKnife.bind(this);
        initView();
    }

    /**
     * 🔟否是添加计时
     *
     * @return
     */
    private boolean isAddType() {
        return itemEntity == null;
    }

    @Override
    protected void initView() {
        super.initView();
        setTitle("计时详情");
        ImageView titleActionImage = getTitleActionImage();
        if (titleActionImage != null) {
            titleActionImage.setImageResource(R.mipmap.header_icon_more);
        }
        setDataToView();
    }

    /**
     * 设置数据到view
     */
    private void setDataToView() {
        if (itemEntity != null) {
            timeDayTv.setText(DateUtils.getTimeDateFormatYear(itemEntity.startTime));
            startTimeMinTv.setText(DateUtils.getTimeDurationDate(itemEntity.startTime));
            stopTimeMinTv.setText(DateUtils.getTimeDurationDate(itemEntity.endTime));

            timeNameTv.setText(itemEntity.name);
            projectNameTv.setText(itemEntity.matterName);
            worktypeNameTv.setText(itemEntity.workTypeName);
            taskNameTv.setText(TextUtils.isEmpty(itemEntity.taskPkId) ? "未关联" : itemEntity.taskPkId);
        }
    }

    @OnClick({R.id.minus_time_image,
            R.id.add_time_image,
            R.id.project_layout,
            R.id.worktype_layout,
            R.id.task_layout})
    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.minus_time_image://－时间 //5分钟
                log("------------->circleTimerView.getCurrentTime():"+circleTimerView.getCurrentTime());
                if (circleTimerView.getCurrentTime() >= 5*60) {
                    circleTimerView.setCurrentTime(circleTimerView.getCurrentTime() - 5*60);
                }
                break;
            case R.id.add_time_image://＋时间
                log("------------->circleTimerView.getCurrentTime()2:"+circleTimerView.getCurrentTime());
                circleTimerView.setCurrentTime(circleTimerView.getCurrentTime() + 5*60);
                break;
            case R.id.project_layout://所属项目
                showProjectSelectDialogFragment();
                break;
            case R.id.worktype_layout://工作类型
                showWorkTypeSelectDialogFragment();
                break;
            case R.id.task_layout://关联任务
                showTaskSelectDialogFragment();
                break;
        }
    }

    /**
     * 展示选择项目对话框
     */
    public void showProjectSelectDialogFragment() {
        String tag = ProjectSelectDialogFragment.class.getSimpleName();
        FragmentTransaction mFragTransaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);
        if (fragment != null) {
            mFragTransaction.remove(fragment);
        }
        ProjectSelectDialogFragment.newInstance()
                .show(mFragTransaction, tag);
    }

    /**
     * 展示选择工作类型对话框
     */
    public void showWorkTypeSelectDialogFragment() {
        if (selectedTaskGroupEntity == null) return;
        String tag = WorkTypeSelectDialogFragment.class.getSimpleName();
        FragmentTransaction mFragTransaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);
        if (fragment != null) {
            mFragTransaction.remove(fragment);
        }
        WorkTypeSelectDialogFragment.newInstance(selectedProjectEntity.pkId)
                .show(mFragTransaction, tag);
    }

    /**
     * 展示选择关联任务对话框
     */
    public void showTaskSelectDialogFragment() {
        if (selectedTaskGroupEntity == null) return;
        String tag = TaskSelectDialogFragment.class.getSimpleName();
        FragmentTransaction mFragTransaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);
        if (fragment != null) {
            mFragTransaction.remove(fragment);
        }
        TaskSelectDialogFragment.newInstance(selectedProjectEntity.pkId)
                .show(mFragTransaction, tag);
    }

    @Override
    public void onProjectTaskGroupSelect(ProjectEntity projectEntity, TaskGroupEntity taskGroupEntity) {
        this.selectedProjectEntity = projectEntity;
        this.selectedTaskGroupEntity = taskGroupEntity;
        if (selectedProjectEntity != null) {
            projectNameTv.setText(selectedProjectEntity.name);
        }
    }

    @Override
    public void onFragmentCallBack(Fragment fragment, int type, Bundle params) {
        if (fragment instanceof WorkTypeSelectDialogFragment && params != null) {
            Serializable serializable = params.getSerializable(BaseDialogFragment.KEY_FRAGMENT_RESULT);
            if (serializable instanceof WorkType) {
                selectedWorkType = (WorkType) serializable;
                worktypeNameTv.setText(selectedWorkType.name);
            }
        } else if (fragment instanceof TaskSelectDialogFragment && params != null) {
            Serializable serializable = params.getSerializable(BaseDialogFragment.KEY_FRAGMENT_RESULT);
            if (serializable instanceof TaskEntity.TaskItemEntity) {
                selectedTaskItem = (TaskEntity.TaskItemEntity) serializable;
                taskNameTv.setText(selectedTaskItem.name);
            }
        }
    }
}
