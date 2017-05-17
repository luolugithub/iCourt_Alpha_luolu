package com.icourt.alpha.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.TimePickerView;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.icourt.alpha.R;
import com.icourt.alpha.entity.bean.ProjectEntity;
import com.icourt.alpha.entity.bean.TaskEntity;
import com.icourt.alpha.entity.bean.TaskGroupEntity;
import com.icourt.alpha.entity.bean.TimeEntity;
import com.icourt.alpha.entity.bean.WorkType;
import com.icourt.alpha.fragment.dialogfragment.BaseDialogFragment;
import com.icourt.alpha.fragment.dialogfragment.CalendaerSelectDialogFragment;
import com.icourt.alpha.fragment.dialogfragment.ProjectSelectDialogFragment;
import com.icourt.alpha.fragment.dialogfragment.TaskSelectDialogFragment;
import com.icourt.alpha.fragment.dialogfragment.WorkTypeSelectDialogFragment;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.interfaces.OnFragmentCallBackListener;
import com.icourt.alpha.utils.DateUtils;
import com.icourt.alpha.utils.JsonUtils;
import com.icourt.alpha.view.CircleTimerView;
import com.icourt.api.RequestUtils;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Description  计时详情
 * Company Beijing icourt
 * author  lu.zhao  E-mail:zhaolu@icourt.cc
 * date createTime：17/5/10
 * version 2.0.0
 */

public class TimerAddActivity extends BaseTimerActivity
        implements ProjectSelectDialogFragment.OnProjectTaskGroupSelectListener,
        OnFragmentCallBackListener {


    @BindView(R.id.titleBack)
    CheckedTextView titleBack;
    @BindView(R.id.titleContent)
    TextView titleContent;
    @BindView(R.id.titleAction)
    CheckedTextView titleAction;
    @BindView(R.id.titleView)
    AppBarLayout titleView;
    @BindView(R.id.minus_time_image)
    ImageView minusTimeImage;
    @BindView(R.id.circleTimerView)
    CircleTimerView circleTimerView;
    @BindView(R.id.add_time_image)
    ImageView addTimeImage;
    @BindView(R.id.use_time_date)
    TextView useTimeDate;
    @BindView(R.id.start_time_min_tv)
    TextView startTimeMinTv;
    @BindView(R.id.stop_time_min_tv)
    TextView stopTimeMinTv;
    @BindView(R.id.time_name_tv)
    EditText timeNameTv;
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
    private WorkType selectedWorkType;
    private TaskEntity.TaskItemEntity selectedTaskItem;
    private Date selectedDate;

    public static void launch(@NonNull Context context) {
        if (context == null) return;
        Intent intent = new Intent(context, TimerAddActivity.class);
        context.startActivity(intent);
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer_add);
        ButterKnife.bind(this);
        initView();
    }


    @Override
    protected void initView() {
        super.initView();
        setTitle("添加计时");
        TextView titleActionTextView = getTitleActionTextView();
        if (titleActionTextView != null) {
            titleActionTextView.setText("完成");
        }
        circleTimerView.setOneCircle(true);
        circleTimerView.setHintText("");
        circleTimerView.setCircleTimerListener(new CircleTimerView.CircleTimerListener() {
            @Override
            public void onTimerStop() {

            }

            @Override
            public void onTimerStart(int time) {

            }

            @Override
            public void onTimerPause(int time) {

            }

            @Override
            public void onTimerTimingValueChanged(int time) {
            }

            @Override
            public void onTimerSetValueChanged(int time) {
                log("---------->onTimerSetValueChanged:" + time);
            }

            @Override
            public void onTimerSetValueChange(int time) {
            }
        });
        selectedDate = Calendar.getInstance().getTime();
        useTimeDate.setText(DateUtils.getyyyyMMdd(selectedDate.getTime()));
    }


    @OnClick({R.id.minus_time_image,
            R.id.add_time_image,
            R.id.project_layout,
            R.id.worktype_layout,
            R.id.task_layout,
            R.id.use_time_date,
            R.id.start_time_min_tv,
            R.id.stop_time_min_tv,
            R.id.titleAction})
    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.titleAction:
                addTimer();
                break;
            case R.id.minus_time_image://－时间 //5分钟
                if (circleTimerView.getCurrentTime() >= 5 * 60) {
                    circleTimerView.setCurrentTime(circleTimerView.getCurrentTime() - 5 * 60);
                }
                break;
            case R.id.add_time_image://＋时间
                if (circleTimerView.getCurrentTime() < (24 * 60 * 60 - 5 * 60)) {
                    circleTimerView.setCurrentTime(circleTimerView.getCurrentTime() + 5 * 60);
                } else {
                    circleTimerView.setCurrentTime(24 * 60 * 60);
                }
                break;
            case R.id.use_time_date:
                showCalendaerSelectDialogFragment();
                break;
            case R.id.start_time_min_tv:
                showDateSelect(startTimeMinTv,true);
                break;
            case R.id.stop_time_min_tv:
                showDateSelect(stopTimeMinTv,false);
                break;
            case R.id.project_layout://所属项目
                showProjectSelectDialogFragment();
                break;
            case R.id.worktype_layout://工作类型
                if (selectedProjectEntity == null) {
                    showTopSnackBar("请选择项目");
                    return;
                }
                showWorkTypeSelectDialogFragment(selectedProjectEntity.pkId);
                break;
            case R.id.task_layout://关联任务
                if (selectedProjectEntity == null) {
                    showTopSnackBar("请选择项目");
                    return;
                }
                showTaskSelectDialogFragment(selectedProjectEntity.pkId);
                break;
        }
    }

    /**
     * 时间选择
     *
     * @param text
     */
    private void showDateSelect(final TextView text, final boolean isSelectStart) {
        //时间选择器
        TimePickerView pvTime = new TimePickerView.Builder(this, new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {//选中事件回调
                if (date == null) return;
                if (isSelectStart) {
                    Calendar instance = Calendar.getInstance();
                    instance.setTime(date);

                    Calendar timing = Calendar.getInstance();
                    timing.setTime(selectedDate);
                    timing.set(Calendar.HOUR_OF_DAY, instance.get(Calendar.HOUR_OF_DAY));
                    timing.set(Calendar.MINUTE, instance.get(Calendar.MINUTE));
                    selectedDate = timing.getTime();
                }
                text.setText(DateUtils.getHHmm(date.getTime()));
            }
        }).setType(TimePickerView.Type.HOURS_MINS)
                .build();
        pvTime.setDate(Calendar.getInstance());
        pvTime.show();
    }


    /**
     * 添加计时
     */
    public void addTimer() {
        TimeEntity.ItemEntity itemEntityCopy = new TimeEntity.ItemEntity();
        itemEntityCopy.createUserId = getLoginUserId();
        itemEntityCopy.startTime = selectedDate.getTime();
        itemEntityCopy.useTime = circleTimerView.getCurrentTime() * 1_000;
        itemEntityCopy.endTime = itemEntityCopy.startTime + itemEntityCopy.useTime;
        itemEntityCopy.state = 1;
        itemEntityCopy.matterPkId = selectedProjectEntity != null ? selectedProjectEntity.pkId : null;
        itemEntityCopy.workTypeId = selectedWorkType != null ? selectedWorkType.pkId : null;
        itemEntityCopy.taskPkId = selectedTaskItem != null ? selectedTaskItem.id : null;
        itemEntityCopy.name = TextUtils.isEmpty(timeNameTv.getText()) ? null : timeNameTv.getText().toString();
        JsonObject jsonObject = null;
        try {
            jsonObject = JsonUtils.object2JsonObject(itemEntityCopy);
        } catch (JsonParseException e) {
            e.printStackTrace();
        }
        if (jsonObject != null) {
            jsonObject.addProperty("workDate", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
            if (jsonObject.has("matterName")) {
                jsonObject.remove("matterName");
            }
            if (jsonObject.has("createTime")) {
                jsonObject.remove("createTime");
            }
            if (jsonObject.has("timingCount")) {
                jsonObject.remove("timingCount");
            }
            showLoadingDialog(null);
            getApi().timingAdd(RequestUtils.createJsonBody(jsonObject.toString()))
                    .enqueue(new SimpleCallBack<String>() {
                        @Override
                        public void onSuccess(Call<ResEntity<String>> call, Response<ResEntity<String>> response) {
                            dismissLoadingDialog();
                            finish();
                        }

                        @Override
                        public void onFailure(Call<ResEntity<String>> call, Throwable t) {
                            super.onFailure(call, t);
                            dismissLoadingDialog();
                        }
                    });
        }
    }


    @Override
    public void onProjectTaskGroupSelect(ProjectEntity projectEntity, TaskGroupEntity taskGroupEntity) {
        if (projectEntity == null) return;
        if (this.selectedProjectEntity != null) {
            if (!TextUtils.equals(this.selectedProjectEntity.pkId, projectEntity.pkId)) {
                this.selectedWorkType = null;
                this.selectedTaskItem = null;
                worktypeNameTv.setText("未选择");
                taskNameTv.setText("未关联");
            }
        }
        this.selectedProjectEntity = projectEntity;
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
        } else if (fragment instanceof CalendaerSelectDialogFragment && params != null) {
            long aLong = params.getLong(BaseDialogFragment.KEY_FRAGMENT_RESULT);
            if (aLong > 0) {
                if (DateUtils.isOverToday(aLong)) {
                    showTopSnackBar("不能选择超过当前时间,请重写选择");
                } else {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(aLong);
                    selectedDate = calendar.getTime();
                    useTimeDate.setText(new SimpleDateFormat("yyyy年MM月dd日", Locale.getDefault()).format(calendar.getTime()));
                }
            }
        }
    }
}
