package com.icourt.alpha.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
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
import com.icourt.alpha.base.BaseDialogFragment;
import com.icourt.alpha.entity.bean.ProjectEntity;
import com.icourt.alpha.entity.bean.TaskEntity;
import com.icourt.alpha.entity.bean.TimeEntity;
import com.icourt.alpha.entity.bean.WorkType;
import com.icourt.alpha.fragment.dialogfragment.CalendaerSelectDialogFragment;
import com.icourt.alpha.fragment.dialogfragment.ProjectSimpleSelectDialogFragment;
import com.icourt.alpha.fragment.dialogfragment.TaskSelectDialogFragment;
import com.icourt.alpha.fragment.dialogfragment.WorkTypeSelectDialogFragment;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.interfaces.OnFragmentCallBackListener;
import com.icourt.alpha.utils.DateUtils;
import com.icourt.alpha.utils.JsonUtils;
import com.icourt.alpha.utils.StringUtils;
import com.icourt.alpha.utils.SystemUtils;
import com.icourt.alpha.utils.UMMobClickAgent;
import com.icourt.alpha.view.CircleTimerView;
import com.icourt.api.RequestUtils;
import com.umeng.analytics.MobclickAgent;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

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
        implements
        OnFragmentCallBackListener {

    private static final String KEY_PROJECT_ID = "key_project_id";
    private static final String KEY_PROJECT_NAME = "key_project_name";
    private static final String KEY_TASKITEMENTITY = "key_taskItemEntity";

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
    Calendar selectedStartDate, selectedEndDate;
    String projectId, projectName;

    public static void launch(@NonNull Context context) {
        if (context == null) return;
        Intent intent = new Intent(context, TimerAddActivity.class);
        context.startActivity(intent);
    }

    public static void launch(@NonNull Context context, @NonNull String projectId, @NonNull String projectName) {
        if (context == null) return;
        Intent intent = new Intent(context, TimerAddActivity.class);
        intent.putExtra(KEY_PROJECT_ID, projectId);
        intent.putExtra(KEY_PROJECT_NAME, projectName);
        context.startActivity(intent);
    }

    public static void launch(@NonNull Context context, TaskEntity.TaskItemEntity taskItemEntity) {
        if (context == null) return;
        Intent intent = new Intent(context, TimerAddActivity.class);
        intent.putExtra(KEY_TASKITEMENTITY, taskItemEntity);
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
        projectId = getIntent().getStringExtra(KEY_PROJECT_ID);
        projectName = getIntent().getStringExtra(KEY_PROJECT_NAME);
        selectedTaskItem = (TaskEntity.TaskItemEntity) getIntent().getSerializableExtra(KEY_TASKITEMENTITY);
        if (selectedProjectEntity == null) {
            selectedProjectEntity = new ProjectEntity();
        }
        if (selectedTaskItem != null) {
            if (selectedTaskItem.matter != null) {
                projectId = selectedTaskItem.matter.id;
                projectName = selectedTaskItem.matter.name;
            }
            if (!TextUtils.isEmpty(selectedTaskItem.name)) {
                timeNameTv.setText(selectedTaskItem.name);
                taskNameTv.setText(selectedTaskItem.name);
            }
        }
        if (!TextUtils.isEmpty(projectName)) {
            projectNameTv.setText(projectName);
            selectedProjectEntity.name = projectName;
        }
        if (!TextUtils.isEmpty(projectId)) {
            selectedProjectEntity.pkId = projectId;
        }

        selectedStartDate = Calendar.getInstance();
        //默认开始时间 早上9点整开始
        selectedStartDate.set(Calendar.HOUR_OF_DAY, 9);
        selectedStartDate.set(Calendar.MINUTE, 0);
        selectedStartDate.set(Calendar.SECOND, 0);


        //默认结束时间 9:15
        selectedEndDate = Calendar.getInstance();
        selectedEndDate.set(Calendar.HOUR_OF_DAY, 9);
        selectedEndDate.set(Calendar.MINUTE, 15);
        selectedEndDate.set(Calendar.SECOND, 0);

        useTimeDate.setText(DateUtils.getyyyyMMdd(selectedStartDate.getTimeInMillis()));
        startTimeMinTv.setText(DateUtils.getHHmm(selectedStartDate.getTimeInMillis()));
        stopTimeMinTv.setText(DateUtils.getHHmm(selectedEndDate.getTimeInMillis()));

        //circleTimerView.setOneCircle(true);
        circleTimerView.setMiniTime(70);
        circleTimerView.setCurrentTime((int) ((selectedEndDate.getTimeInMillis() - selectedStartDate.getTimeInMillis()) / 1000));
        circleTimerView.setHintText("");
        circleTimerView.setCircleTimerListener(new CircleTimerView.CircleTimerListener() {
            @Override
            public void onTimerStop() {

            }

            @Override
            public void onTimerStart(long time) {

            }

            @Override
            public void onTimerPause(long time) {

            }

            @Override
            public void onTimerTimingValueChanged(long time) {
            }

            @Override
            public void onTimerTouchValueChanged(long time) {
                log("---------->onTimerSetValueChanged:" + time);
                selectedEndDate.setTimeInMillis(selectedStartDate.getTimeInMillis() + time * 1000);
                stopTimeMinTv.setText(DateUtils.getHHmm(selectedEndDate.getTimeInMillis()));
            }

            @Override
            public void onTimerSetValueChanged(long time) {
                log("---------->onTimerSetValueChanged:" + time);
                selectedEndDate.setTimeInMillis(selectedStartDate.getTimeInMillis() + time * 1000);
                stopTimeMinTv.setText(DateUtils.getHHmm(selectedEndDate.getTimeInMillis()));
            }

            @Override
            public void onTimerSetValueChange(long time) {
            }
        });
        circleTimerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        SystemUtils.hideSoftKeyBoard(getActivity(), true);
                        break;
                }
                return false;
            }
        });
        timeNameTv.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                return (event.getKeyCode() == KeyEvent.KEYCODE_ENTER);
            }
        });
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
            case R.id.minus_time_image://－时间
                if (circleTimerView.getCurrentTime() >= 16 * 60) {
                    circleTimerView.setCurrentTime(circleTimerView.getCurrentTime() - 15 * 60);
                } else {
                    circleTimerView.setCurrentTime(60);
                }
                break;
            case R.id.add_time_image://＋时间
                circleTimerView.setCurrentTime(circleTimerView.getCurrentTime() + 15 * 60);
                break;
            case R.id.use_time_date:
                showCalendaerSelectDialogFragment();
                break;
            case R.id.start_time_min_tv:
                showDateSelectStart(startTimeMinTv);
                break;
            case R.id.stop_time_min_tv:
                showDateSelectEnd(stopTimeMinTv);
                break;
            case R.id.project_layout://所属项目
                showProjectSelectDialogFragment(selectedProjectEntity != null ? selectedProjectEntity.pkId : null);
                break;
            case R.id.worktype_layout://工作类型
                if (selectedProjectEntity == null) {
                    showTopSnackBar("请选择项目");
                    return;
                }
                showWorkTypeSelectDialogFragment(selectedProjectEntity.pkId, selectedWorkType != null ? selectedWorkType.pkId : null);
                break;
            case R.id.task_layout://关联任务
                showTaskSelectDialogFragment(selectedProjectEntity != null ? selectedProjectEntity.pkId : null,
                        selectedTaskItem != null ? selectedTaskItem.id : null);
                break;
        }
    }


    /**
     * 时间选择
     *
     * @param text
     */
    private void showDateSelectStart(final TextView text) {
        //时间选择器
        TimePickerView pvTime = new TimePickerView.Builder(this, new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {//选中事件回调
                if (date == null) return;
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                selectedStartDate.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY));
                selectedStartDate.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE));
                text.setText(DateUtils.getHHmm(selectedStartDate.getTime().getTime()));

                selectedEndDate.setTimeInMillis(circleTimerView.getCurrentTime() * 1_000 + selectedStartDate.getTimeInMillis());
                stopTimeMinTv.setText(DateUtils.getHHmm(selectedEndDate.getTimeInMillis()));

            }
        }).setType(TimePickerView.Type.HOURS_MINS)
                .build();
//        pvTime.setDate(Calendar.getInstance());
        pvTime.setDate(selectedStartDate);
        pvTime.show();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                SystemUtils.hideSoftKeyBoard(getActivity(), true);
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 时间选择
     *
     * @param text
     */
    private void showDateSelectEnd(final TextView text) {
        //时间选择器
        TimePickerView pvTime = new TimePickerView.Builder(this, new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {//选中事件回调
                if (date == null) return;
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                if (calendar.get(Calendar.HOUR_OF_DAY) == selectedStartDate.get(Calendar.HOUR_OF_DAY)) {
                    if (calendar.get(Calendar.MINUTE) <= selectedStartDate.get(Calendar.MINUTE)) {
                        showTopSnackBar("结束时间不能小于开始时间");
                        return;
                    }
                } else if (calendar.get(Calendar.HOUR_OF_DAY) < selectedStartDate.get(Calendar.HOUR_OF_DAY)) {
                    showTopSnackBar("结束时间不能小于开始时间");
                    return;
                }
                selectedEndDate.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY));
                selectedEndDate.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE));

                long rangeTime = (selectedEndDate.getTimeInMillis() - selectedStartDate.getTimeInMillis());
                log("------------>endTime:" + (rangeTime / 1000));
                circleTimerView.setCurrentTime((int) (rangeTime / 1000));
            }
        }).setType(TimePickerView.Type.HOURS_MINS)
                .build();
//        pvTime.setDate(Calendar.getInstance());
        pvTime.setDate(selectedEndDate);
        pvTime.show();
    }

    /**
     * 添加计时
     */
    public void addTimer() {
        TimeEntity.ItemEntity itemEntityCopy = new TimeEntity.ItemEntity();
        itemEntityCopy.createUserId = getLoginUserId();
        itemEntityCopy.startTime = selectedStartDate.getTimeInMillis();
        itemEntityCopy.useTime = selectedEndDate.getTimeInMillis() - selectedStartDate.getTimeInMillis();
        itemEntityCopy.endTime = selectedEndDate.getTimeInMillis();
        itemEntityCopy.state = 1;
        itemEntityCopy.workDate = selectedStartDate.getTimeInMillis();
        itemEntityCopy.matterPkId = selectedProjectEntity != null ? selectedProjectEntity.pkId : "";
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
            MobclickAgent.onEvent(getContext(), UMMobClickAgent.creat_timer_click_id);
            callEnqueue(
                    getApi().timingAdd(RequestUtils.createJsonBody(jsonObject.toString())),
                    new SimpleCallBack<String>() {
                        @Override
                        public void onSuccess(Call<ResEntity<String>> call, Response<ResEntity<String>> response) {
                            dismissLoadingDialog();
                            showToast("添加计时成功");
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
                if (selectedTaskItem.matter != null) {
                    ProjectEntity projectEntity = selectedTaskItem.matter.convert2Model();
                    if (selectedProjectEntity != null
                            && !StringUtils.equalsIgnoreCase(projectEntity.pkId, selectedProjectEntity.pkId, false)) {
                        selectedWorkType = null;
                        worktypeNameTv.setText("未选择");
                    }
                    selectedProjectEntity = projectEntity;
                    projectNameTv.setText(selectedProjectEntity.name);
                }
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
                    selectedStartDate.set(Calendar.YEAR, calendar.get(Calendar.YEAR));
                    selectedStartDate.set(Calendar.MONTH, calendar.get(Calendar.MONTH));
                    selectedStartDate.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR));

                    selectedEndDate.set(Calendar.YEAR, calendar.get(Calendar.YEAR));
                    selectedEndDate.set(Calendar.MONTH, calendar.get(Calendar.MONTH));
                    selectedEndDate.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR));
                    useTimeDate.setText(DateUtils.getyyyyMMdd(selectedStartDate.getTime().getTime()));
                }
            }
        } else if (fragment instanceof ProjectSimpleSelectDialogFragment && params != null) {
            Serializable serializable = params.getSerializable(BaseDialogFragment.KEY_FRAGMENT_RESULT);
            if (serializable instanceof ProjectEntity) {
                ProjectEntity projectEntity = (ProjectEntity) serializable;
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

        }
    }
}
