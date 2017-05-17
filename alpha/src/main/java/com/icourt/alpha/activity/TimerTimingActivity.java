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
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.icourt.alpha.R;
import com.icourt.alpha.entity.bean.ProjectEntity;
import com.icourt.alpha.entity.bean.TaskEntity;
import com.icourt.alpha.entity.bean.TaskGroupEntity;
import com.icourt.alpha.entity.bean.TimeEntity;
import com.icourt.alpha.entity.bean.WorkType;
import com.icourt.alpha.fragment.dialogfragment.BaseDialogFragment;
import com.icourt.alpha.fragment.dialogfragment.ProjectSelectDialogFragment;
import com.icourt.alpha.fragment.dialogfragment.TaskSelectDialogFragment;
import com.icourt.alpha.fragment.dialogfragment.WorkTypeSelectDialogFragment;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.interfaces.OnFragmentCallBackListener;
import com.icourt.alpha.utils.DateUtils;
import com.icourt.alpha.utils.JsonUtils;
import com.icourt.alpha.widget.manager.TimerManager;
import com.icourt.api.RequestUtils;

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

public class TimerTimingActivity extends BaseTimerActivity
        implements ProjectSelectDialogFragment.OnProjectTaskGroupSelectListener,
        OnFragmentCallBackListener {

    private static final String KEY_TIME = "key_time";


    TimeEntity.ItemEntity itemEntity;
    @BindView(R.id.titleBack)
    ImageView titleBack;
    @BindView(R.id.titleContent)
    TextView titleContent;
    @BindView(R.id.titleAction)
    CheckedTextView titleAction;
    @BindView(R.id.titleView)
    AppBarLayout titleView;
    @BindView(R.id.timing_tv)
    TextView timingTv;
    @BindView(R.id.start_time_tv)
    TextView startTimeTv;
    @BindView(R.id.stop_time_tv)
    TextView stopTimeTv;
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

    public static void launch(@NonNull Context context,
                              @NonNull TimeEntity.ItemEntity timeEntity) {
        if (context == null) return;
        if (timeEntity == null) return;
        Intent intent = new Intent(context, TimerTimingActivity.class);
        intent.putExtra(KEY_TIME, timeEntity);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer_timing);
        ButterKnife.bind(this);
        initView();
    }


    @Override
    protected void initView() {
        super.initView();
        itemEntity = (TimeEntity.ItemEntity) getIntent().getSerializableExtra(KEY_TIME);
        setTitle("计时详情");
        TextView titleActionTextView = getTitleActionTextView();
        if (titleActionTextView != null) {
            titleActionTextView.setText("完成");
        }
        if (itemEntity != null) {
            timingTv.setText(DateUtils.getTimeDateFormatYear(itemEntity.startTime));
            startTimeTv.setText(DateUtils.getTimeDurationDate(itemEntity.startTime));
            timeNameTv.setText(itemEntity.name);
            projectNameTv.setText(TextUtils.isEmpty(itemEntity.matterName) ? "未设置" : itemEntity.matterName);
            worktypeNameTv.setText(TextUtils.isEmpty(itemEntity.workTypeName) ? "未设置" : itemEntity.workTypeName);
            taskNameTv.setText(TextUtils.isEmpty(itemEntity.taskPkId) ? "未关联" : itemEntity.taskPkId);
        }
    }


    @OnClick({R.id.minus_time_image,
            R.id.add_time_image,
            R.id.project_layout,
            R.id.worktype_layout,
            R.id.task_layout,
            R.id.use_time_date,
            R.id.start_time_min_tv,
            R.id.stop_time_min_tv,
            R.id.titleAction,
            R.id.stop_time_tv})
    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.titleAction:
                saveTiming();
                break;
            case R.id.use_time_date:
                showCalendaerSelectDialogFragment();
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
            case R.id.stop_time_tv:
                TimerManager.getInstance().stopTimer();
                finish();
                break;
        }
    }

    /**
     * 时间选择
     *
     * @param text
     */
    private void showDateSelect(final TextView text) {
        //时间选择器
        TimePickerView pvTime = new TimePickerView.Builder(this, new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {//选中事件回调
                if (date == null) return;
                text.setText(DateUtils.getHHmm(date.getTime()));
            }
        }).setType(TimePickerView.Type.HOURS_MINS)
                .build();
        pvTime.setDate(Calendar.getInstance());
        pvTime.show();
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


    private void saveTiming() {
        //实时保存
        if (itemEntity != null) {
            JsonObject jsonBody = null;
            try {
                jsonBody = JsonUtils.object2JsonObject(itemEntity);
            } catch (JsonParseException e) {
                e.printStackTrace();
            }
            if (jsonBody == null) return;
            if (jsonBody.has("matterName")) {
                jsonBody.remove("matterName");
            }
            if (jsonBody.has("timingCount")) {
                jsonBody.remove("timingCount");
            }
            if (jsonBody.has("workTypeName")) {
                jsonBody.remove("workTypeName");
            }
            showLoadingDialog(null);
            getApi().timingUpdate(RequestUtils.createJsonBody(jsonBody.toString()))
                    .enqueue(new SimpleCallBack<JsonElement>() {
                        @Override
                        public void onSuccess(Call<ResEntity<JsonElement>> call, Response<ResEntity<JsonElement>> response) {
                            dismissLoadingDialog();
                            finish();
                        }

                        @Override
                        public void onFailure(Call<ResEntity<JsonElement>> call, Throwable t) {
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
                taskNameTv.setText(selectedTaskItem.name);
            }
        }
    }
}
