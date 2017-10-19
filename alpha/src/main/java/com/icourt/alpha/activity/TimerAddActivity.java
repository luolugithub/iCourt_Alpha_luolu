package com.icourt.alpha.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.icourt.alpha.R;
import com.icourt.alpha.base.BaseDialogFragment;
import com.icourt.alpha.entity.bean.ProjectEntity;
import com.icourt.alpha.entity.bean.TaskEntity;
import com.icourt.alpha.entity.bean.TimeEntity;
import com.icourt.alpha.entity.bean.WorkType;
import com.icourt.alpha.fragment.dialogfragment.ProjectSimpleSelectDialogFragment;
import com.icourt.alpha.fragment.dialogfragment.TaskSelectDialogFragment;
import com.icourt.alpha.fragment.dialogfragment.TimingChangeDialogFragment;
import com.icourt.alpha.fragment.dialogfragment.WorkTypeSelectDialogFragment;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.interfaces.OnFragmentCallBackListener;
import com.icourt.alpha.utils.DateUtils;
import com.icourt.alpha.utils.JsonUtils;
import com.icourt.alpha.utils.SpUtils;
import com.icourt.alpha.utils.StringUtils;
import com.icourt.alpha.utils.SystemUtils;
import com.icourt.alpha.utils.UMMobClickAgent;
import com.icourt.alpha.view.CircleTimerView;
import com.icourt.alpha.widget.filter.InputActionNextFilter;
import com.icourt.api.RequestUtils;
import com.umeng.analytics.MobclickAgent;

import java.io.Serializable;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

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

    private static final String KEY_LAUNCH_TYPE = "key_launch_type";//启动方式，如果是没有携带参数的启动，则从缓存里取数据。
    private static final String KEY_PROJECT_ID = "key_project_id";//用来传递项目id的key。
    private static final String KEY_PROJECT_NAME = "key_project_name";//用来传递项目名称的key。
    private static final String KEY_TASKITEMENTITY = "key_taskItemEntity";//用来传递任务实体的key。

    private static final int LAUNCH_TYPE_NORMAL = 1;//说明是默认的启动方式，没有携带数据，需要从本地取缓存。
    private static final int LAUNCH_TYPE_OTHER = 2;//说明是其他携带参数的启动方式，有携带数据。

    /**
     * 以下常量是用来缓存添加计时的相关数据
     */
    private static final String CACHE_NAME = "cache_name";
    private static final String CACHE_PROJECT = "cache_project";
    private static final String CACHE_WORKTYPE = "cache_worktype";
    private static final String CACHE_TASK = "cache_task";
    private static final String CACHE_START_TIME = "cache_start_time";
    private static final String CACHE_END_TIME = "cache_end_time";

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
    @BindView(R.id.tv_surpass_day)
    TextView tvSurpassDay;
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

    private ProjectEntity selectedProjectEntity;//用来记录计时选中的项目信息
    private WorkType selectedWorkType;//用来记录计时选中的工作类型
    private TaskEntity.TaskItemEntity selectedTaskItem;//用来记录计时选中的任务信息
    Calendar selectedStartDate;//计时器选中的开始时间
    Calendar selectedEndDate;//计时器选中的结束时间
    String projectId, projectName;//用来记录计时的项目id和项目名称

    public static void launch(@NonNull Context context) {
        if (context == null) {
            return;
        }
        Intent intent = new Intent(context, TimerAddActivity.class);
        intent.putExtra(KEY_LAUNCH_TYPE, LAUNCH_TYPE_NORMAL);
        context.startActivity(intent);
    }

    public static void launch(@NonNull Context context, @NonNull String projectId, @NonNull String projectName) {
        if (context == null) {
            return;
        }
        Intent intent = new Intent(context, TimerAddActivity.class);
        intent.putExtra(KEY_LAUNCH_TYPE, LAUNCH_TYPE_OTHER);
        intent.putExtra(KEY_PROJECT_ID, projectId);
        intent.putExtra(KEY_PROJECT_NAME, projectName);
        context.startActivity(intent);
    }

    public static void launch(@NonNull Context context, TaskEntity.TaskItemEntity taskItemEntity) {
        if (context == null) {
            return;
        }
        Intent intent = new Intent(context, TimerAddActivity.class);
        intent.putExtra(KEY_LAUNCH_TYPE, LAUNCH_TYPE_OTHER);
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
        setTitle(R.string.timing_add_timer);
        TextView titleActionTextView = getTitleActionTextView();
        if (titleActionTextView != null) {
            titleActionTextView.setText(R.string.timing_finish);
        }
        int launchType = getIntent().getIntExtra(KEY_LAUNCH_TYPE, LAUNCH_TYPE_NORMAL);
        String cacheName = null;
        //根据启动方式，判断是从缓存取数据，还是取传递过来的数据。
        if (launchType == LAUNCH_TYPE_NORMAL) {//取本地缓存的数据。
            cacheName = SpUtils.getTemporaryCache().getStringData(CACHE_NAME, null);
            selectedProjectEntity = SpUtils.getTemporaryCache().getObjectData(CACHE_PROJECT, ProjectEntity.class);
            selectedWorkType = SpUtils.getTemporaryCache().getObjectData(CACHE_WORKTYPE, WorkType.class);
            selectedTaskItem = SpUtils.getTemporaryCache().getObjectData(CACHE_TASK, TaskEntity.TaskItemEntity.class);
            selectedStartDate = SpUtils.getTemporaryCache().getObjectData(CACHE_START_TIME, Calendar.class);
            selectedEndDate = SpUtils.getTemporaryCache().getObjectData(CACHE_END_TIME, Calendar.class);
        } else {
            projectId = getIntent().getStringExtra(KEY_PROJECT_ID);
            projectName = getIntent().getStringExtra(KEY_PROJECT_NAME);
            selectedTaskItem = (TaskEntity.TaskItemEntity) getIntent().getSerializableExtra(KEY_TASKITEMENTITY);
        }

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
                if (!TextUtils.isEmpty(cacheName)) {//如果缓存的名字不为空，则显示缓存的名字
                    timeNameTv.setText(cacheName);
                } else {//如果缓存的名字为空，则显示任务的名字
                    taskNameTv.setText(selectedTaskItem.name);
                }
            }
        }
        if (!TextUtils.isEmpty(projectName)) {
            selectedProjectEntity.name = projectName;
        }
        if (!TextUtils.isEmpty(projectId)) {
            selectedProjectEntity.pkId = projectId;
        }
        projectNameTv.setText(selectedProjectEntity.name);

        //默认开始时间 早上9点整开始
        if (selectedStartDate == null) {
            selectedStartDate = Calendar.getInstance();
            selectedStartDate.set(Calendar.HOUR_OF_DAY, 9);
            selectedStartDate.set(Calendar.MINUTE, 0);
            selectedStartDate.set(Calendar.SECOND, 0);
        }

        //默认结束时间 9:15
        if (selectedEndDate == null) {
            selectedEndDate = Calendar.getInstance();
            selectedEndDate.set(Calendar.HOUR_OF_DAY, 9);
            selectedEndDate.set(Calendar.MINUTE, 15);
            selectedEndDate.set(Calendar.SECOND, 0);
        }

        setTimeViewData();

        circleTimerView.setMiniTime(70);
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
                //如果选中的时间超过当前时间，则记录为当前时间。
                if (selectedStartDate.getTimeInMillis() + time * TimeUnit.SECONDS.toMillis(1) > System.currentTimeMillis()) {
                    showTopSnackBar(getString(R.string.timing_donot_select_future_time));
                    selectedEndDate.clear();
                    selectedEndDate.setTimeInMillis(System.currentTimeMillis());
                } else {
                    selectedEndDate.setTimeInMillis(selectedStartDate.getTimeInMillis() + time * 1000);
                }
                setTimeViewData();
            }

            @Override
            public void onTimerSetValueChanged(long time) {
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
                    default:
                        break;
                }
                return false;
            }
        });
        timeNameTv.setFilters(timingNameInputFilters);
        timeNameTv.setOnEditorActionListener(new InputActionNextFilter());
    }

    /**
     * 设置要显示的时间信息（时间圆盘的数据、开始时间年月日、开始时间分钟秒、结束时间分钟秒）
     * 避免秒的差异 展示取分钟差距
     */
    private void setTimeViewData() {
        if (selectedStartDate == null || selectedEndDate == null) {
            return;
        }
        useTimeDate.setText(DateUtils.getTimeDateFormatYear(selectedStartDate.getTimeInMillis()));
        startTimeMinTv.setText(DateUtils.getHHmm(selectedStartDate.getTimeInMillis()));
        stopTimeMinTv.setText(DateUtils.getHHmm(selectedEndDate.getTimeInMillis()));

        int differentDay = DateUtils.differentDays(selectedStartDate.getTimeInMillis(), selectedEndDate.getTimeInMillis());
        if (differentDay >= 1) {
            tvSurpassDay.setText(getString(R.string.timing_add_days, differentDay));
        } else {
            tvSurpassDay.setText("");
        }

        long one_minutes_millis = TimeUnit.MINUTES.toMillis(1);
        long rangeTime = (selectedEndDate.getTimeInMillis() / one_minutes_millis * one_minutes_millis
                - selectedStartDate.getTimeInMillis() / one_minutes_millis * one_minutes_millis);
        int time = (int) (rangeTime / 1000);
        circleTimerView.setCurrentTime(time);
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
        switch (view.getId()) {
            case R.id.titleBack:
                cacheData();
                finish();
                break;
            case R.id.titleAction:
                addTimer();
                break;
            //－时间，会有个最小值。
            case R.id.minus_time_image:
                long useTime = selectedEndDate.getTimeInMillis() - selectedStartDate.getTimeInMillis();
                if (useTime >= TimeUnit.MINUTES.toMillis(16)) {
                    useTime = useTime - TimeUnit.MINUTES.toMillis(15);
                } else {
                    useTime = TimeUnit.MINUTES.toMillis(1);
                }
                selectedEndDate.clear();
                selectedEndDate.setTimeInMillis(selectedStartDate.getTimeInMillis() + useTime);
                setTimeViewData();
                break;
            //＋时间，不能超过当前时间。
            case R.id.add_time_image:
                long endTime = selectedEndDate.getTimeInMillis();
                if (endTime + TimeUnit.MINUTES.toMillis(16) <= System.currentTimeMillis()) {
                    endTime = endTime + TimeUnit.MINUTES.toMillis(15);
                    selectedEndDate.clear();
                    selectedEndDate.setTimeInMillis(endTime);
                    setTimeViewData();
                } else {
                    showTopSnackBar(R.string.timing_donot_select_future_time);
                }
                break;
            //显示计时开始时间的日期
            case R.id.use_time_date:
                showDateTimeSelectDialogFragment(TimingChangeDialogFragment.TYPE_CHANGE_START_TIME, selectedStartDate.getTimeInMillis(), selectedEndDate.getTimeInMillis());
                break;
            //显示计时开始时间的时分
            case R.id.start_time_min_tv:
                showDateTimeSelectDialogFragment(TimingChangeDialogFragment.TYPE_CHANGE_START_TIME, selectedStartDate.getTimeInMillis(), selectedEndDate.getTimeInMillis());
                break;
            //显示计时结束时间的时分
            case R.id.stop_time_min_tv:
                showDateTimeSelectDialogFragment(TimingChangeDialogFragment.TYPE_CHANGE_END_TIME, selectedStartDate.getTimeInMillis(), selectedEndDate.getTimeInMillis());
                break;
            //所属项目
            case R.id.project_layout:
                showProjectSelectDialogFragment(selectedProjectEntity != null ? selectedProjectEntity.pkId : null);
                break;
            //工作类型
            case R.id.worktype_layout:
                if (selectedProjectEntity == null) {
                    showTopSnackBar(R.string.timing_please_select_project);
                    return;
                }
                showWorkTypeSelectDialogFragment(selectedProjectEntity.pkId, selectedWorkType != null ? selectedWorkType.pkId : null);
                break;
            //关联任务
            case R.id.task_layout:
                showTaskSelectDialogFragment(selectedProjectEntity != null ? selectedProjectEntity.pkId : null,
                        selectedTaskItem != null ? selectedTaskItem.id : null);
                break;
            default:
                super.onClick(view);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        cacheData();
        super.onBackPressed();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                SystemUtils.hideSoftKeyBoard(getActivity(), true);
                break;
            default:
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 缓存数据
     */
    private void cacheData() {
        SpUtils.getTemporaryCache().putData(CACHE_NAME, timeNameTv.getText().toString());
        SpUtils.getTemporaryCache().putObjectData(CACHE_PROJECT, selectedProjectEntity);
        SpUtils.getTemporaryCache().putObjectData(CACHE_WORKTYPE, selectedWorkType);
        SpUtils.getTemporaryCache().putObjectData(CACHE_TASK, selectedTaskItem);
        SpUtils.getTemporaryCache().putObjectData(CACHE_START_TIME, selectedStartDate);
        SpUtils.getTemporaryCache().putObjectData(CACHE_END_TIME, selectedEndDate);
    }

    /**
     * 清除历史记录
     */
    private void clearCache() {
        SpUtils.getTemporaryCache().remove(CACHE_NAME);
        SpUtils.getTemporaryCache().remove(CACHE_PROJECT);
        SpUtils.getTemporaryCache().remove(CACHE_WORKTYPE);
        SpUtils.getTemporaryCache().remove(CACHE_TASK);
        SpUtils.getTemporaryCache().remove(CACHE_START_TIME);
        SpUtils.getTemporaryCache().remove(CACHE_END_TIME);
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
                            showToast(R.string.timing_add_timer_success);
                            clearCache();
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
            //修改工作类型的回调
            Serializable serializable = params.getSerializable(BaseDialogFragment.KEY_FRAGMENT_RESULT);
            if (serializable instanceof WorkType) {
                selectedWorkType = (WorkType) serializable;
                worktypeNameTv.setText(selectedWorkType.name);
            }
        } else if (fragment instanceof TaskSelectDialogFragment && params != null) {
            //修改所属任务的回调
            Serializable serializable = params.getSerializable(BaseDialogFragment.KEY_FRAGMENT_RESULT);
            if (serializable instanceof TaskEntity.TaskItemEntity) {
                selectedTaskItem = (TaskEntity.TaskItemEntity) serializable;
                if (selectedTaskItem.matter != null) {
                    ProjectEntity projectEntity = selectedTaskItem.matter.convert2Model();
                    if (selectedProjectEntity != null
                            && !StringUtils.equalsIgnoreCase(projectEntity.pkId, selectedProjectEntity.pkId, false)) {
                        selectedWorkType = null;
                        worktypeNameTv.setText(R.string.timing_not_select);
                    }
                    selectedProjectEntity = projectEntity;
                    projectNameTv.setText(selectedProjectEntity.name);
                }
                taskNameTv.setText(selectedTaskItem.name);
            }
        } else if (fragment instanceof ProjectSimpleSelectDialogFragment && params != null) {
            //从选择所属项目的回调
            Serializable serializable = params.getSerializable(BaseDialogFragment.KEY_FRAGMENT_RESULT);
            if (serializable instanceof ProjectEntity) {
                ProjectEntity projectEntity = (ProjectEntity) serializable;
                if (this.selectedProjectEntity != null) {
                    if (!TextUtils.equals(this.selectedProjectEntity.pkId, projectEntity.pkId)) {
                        this.selectedWorkType = null;
                        this.selectedTaskItem = null;
                        worktypeNameTv.setText(R.string.timing_not_select);
                        taskNameTv.setText(R.string.timing_not_relevance);
                    }
                }
                this.selectedProjectEntity = projectEntity;
                if (selectedProjectEntity != null) {
                    projectNameTv.setText(selectedProjectEntity.name);
                }
            }
        } else if (fragment instanceof TimingChangeDialogFragment) {
            //从选择时间的回调
            long resultTime = params.getLong(TimingChangeDialogFragment.TIME_RESULT_MILLIS);
            if (type == TimingChangeDialogFragment.TYPE_CHANGE_START_TIME) {//修改开始时间
                //修改开始时间，同时会修改结束时间（时长保持不变）。
                long useTime = selectedEndDate.getTimeInMillis() - selectedStartDate.getTimeInMillis();
                long endTime = resultTime + useTime;
                //若用户选择的开始时间导致结束时间晚于当前时间，（点击【完成】后 ）toast 提示无法记录未来时间并回到编辑前状态。
                if (endTime > System.currentTimeMillis()) {
                    showTopSnackBar(getString(R.string.timing_donot_select_future_time));
                    return;
                }
                selectedStartDate.clear();
                selectedStartDate.setTimeInMillis(resultTime);
                selectedEndDate.clear();
                selectedEndDate.setTimeInMillis(endTime);
            } else {//修改结束时间
                selectedEndDate.clear();
                selectedEndDate.setTimeInMillis(resultTime);
            }
            setTimeViewData();
        }
    }
}
