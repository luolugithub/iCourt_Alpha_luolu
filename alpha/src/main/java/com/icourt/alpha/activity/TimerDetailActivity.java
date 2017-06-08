package com.icourt.alpha.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.TimePickerView;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.icourt.alpha.R;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.entity.bean.ProjectEntity;
import com.icourt.alpha.entity.bean.TaskEntity;
import com.icourt.alpha.entity.bean.TimeEntity;
import com.icourt.alpha.entity.bean.WorkType;
import com.icourt.alpha.fragment.dialogfragment.BaseDialogFragment;
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
import com.icourt.alpha.view.CircleTimerView;
import com.icourt.alpha.widget.dialog.BottomActionDialog;
import com.icourt.api.RequestUtils;

import java.io.Serializable;
import java.util.Arrays;
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

public class TimerDetailActivity extends BaseTimerActivity
        implements
        OnFragmentCallBackListener {

    private static final String KEY_TIME = "key_time";


    TimeEntity.ItemEntity itemEntity;
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
    Calendar selectedStartDate, selectedEndDate;

    public static void launch(@NonNull Context context,
                              @NonNull TimeEntity.ItemEntity timeEntity) {
        if (context == null) return;
        if (timeEntity == null) return;
        Intent intent = new Intent(context, TimerDetailActivity.class);
        intent.putExtra(KEY_TIME, timeEntity);
        context.startActivity(intent);
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer_detail);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void initView() {
        super.initView();
        itemEntity = (TimeEntity.ItemEntity) getIntent().getSerializableExtra(KEY_TIME);
        setTitle("计时详情");
        ImageView titleActionImage = getTitleActionImage();
        if (titleActionImage != null) {
            titleActionImage.setImageResource(R.mipmap.header_icon_more);
        }
        //circleTimerView.setOneCircle(true);
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

            /**
             *
             * @param time 秒
             */
            @Override
            public void onTimerSetValueChanged(int time) {
                log("---------->onTimerSetValueChanged:" + time);
                selectedEndDate.setTimeInMillis(selectedStartDate.getTimeInMillis() + time * 1000);
                stopTimeMinTv.setText(DateUtils.getHHmm(selectedEndDate.getTimeInMillis()));
            }

            @Override
            public void onTimerSetValueChange(int time) {
            }
        });

        if (itemEntity != null) {
            selectedStartDate = Calendar.getInstance();
            selectedStartDate.setTimeInMillis(itemEntity.startTime);
            useTimeDate.setText(DateUtils.getTimeDateFormatYear(selectedStartDate.getTime().getTime()));
            startTimeMinTv.setText(DateUtils.getHHmm(selectedStartDate.getTime().getTime()));

            selectedEndDate = Calendar.getInstance();
            selectedEndDate.setTimeInMillis(itemEntity.endTime);
            stopTimeMinTv.setText(DateUtils.getHHmm(selectedEndDate.getTime().getTime()));

            timeNameTv.setText(itemEntity.name);
            if (!TextUtils.isEmpty(timeNameTv.getText())) {
                timeNameTv.setSelection(timeNameTv.getText().length());
            }
            projectNameTv.setText(TextUtils.isEmpty(itemEntity.matterName) ? "未设置" : itemEntity.matterName);
            worktypeNameTv.setText(TextUtils.isEmpty(itemEntity.workTypeName) ? "未设置" : itemEntity.workTypeName);
            taskNameTv.setText(TextUtils.isEmpty(itemEntity.taskName) ? "未关联" : itemEntity.taskName);
            circleTimerView.setMiniTime(70);
            circleTimerView.setCurrentTime((int) ((selectedEndDate.getTimeInMillis() - selectedStartDate.getTimeInMillis()) / 1000));
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
            timeNameTv.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (!TextUtils.isEmpty(s)) {
                        itemEntity.name = s.toString();
                    } else {
                        itemEntity.name = "";
                    }
                }
            });

        }

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
                new BottomActionDialog(getContext(),
                        null,
                        Arrays.asList("删除"),
                        new BottomActionDialog.OnActionItemClickListener() {
                            @Override
                            public void onItemClick(BottomActionDialog dialog, BottomActionDialog.ActionItemAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
                                dialog.dismiss();
                                deleteTiming(itemEntity.pkId);
                            }
                        }).show();
                break;
            case R.id.minus_time_image://－时间 //5分钟
                if (circleTimerView.getCurrentTime() >= 15 * 60) {
                    circleTimerView.setCurrentTime(circleTimerView.getCurrentTime() - 15 * 60);
                }
                break;
            case R.id.add_time_image://＋时间
                if (circleTimerView.getCurrentTime() < (24 * 60 * 60 - 15 * 60)) {
                    circleTimerView.setCurrentTime(circleTimerView.getCurrentTime() + 15 * 60);
                } else {
                    circleTimerView.setCurrentTime(24 * 60 * 60);
                }
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
                if (itemEntity != null) {
                    if (TextUtils.isEmpty(itemEntity.matterPkId)) {
                        showProjectSelectDialogFragment(null);
                    } else {
                        showBottomMeau();
                    }
                }
                break;
            case R.id.worktype_layout://工作类型
                if (TextUtils.isEmpty(itemEntity.matterPkId)) {
                    showTopSnackBar("请选择项目");
                    return;
                }
                showWorkTypeSelectDialogFragment(itemEntity.matterPkId);
                break;
            case R.id.task_layout://关联任务
                showTaskSelectDialogFragment(itemEntity.matterPkId, itemEntity.taskPkId);
                break;
        }
    }

    /**
     * 显示底部菜单
     */
    private void showBottomMeau() {
        new BottomActionDialog(getContext(),
                null,
                Arrays.asList("选择项目", "查看项目"),
                new BottomActionDialog.OnActionItemClickListener() {
                    @Override
                    public void onItemClick(BottomActionDialog dialog, BottomActionDialog.ActionItemAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
                        dialog.dismiss();
                        switch (position) {
                            case 0:
                                showProjectSelectDialogFragment(itemEntity.matterPkId);
                                break;
                            case 1:
                                if (itemEntity != null)
                                    ProjectDetailActivity.launch(getContext(), itemEntity.matterPkId, itemEntity.matterName);
                                break;

                        }
                    }
                }).show();
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

                circleTimerView.setCurrentTime((int) (selectedEndDate.getTimeInMillis() - selectedStartDate.getTimeInMillis()) / 1000);
            }
        }).setType(TimePickerView.Type.HOURS_MINS)
                .build();
        pvTime.setDate(Calendar.getInstance());
        pvTime.show();
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
                text.setText(DateUtils.getHHmm(selectedEndDate.getTime().getTime()));
                circleTimerView.setCurrentTime((int) (selectedEndDate.getTimeInMillis() - selectedStartDate.getTimeInMillis()) / 1000);
            }
        }).setType(TimePickerView.Type.HOURS_MINS)
                .build();
        pvTime.setDate(Calendar.getInstance());
        pvTime.show();
    }


    @Override
    protected void onPause() {
        super.onPause();
        saveTiming();
    }

    private void saveTiming() {
        //实时保存
        if (itemEntity != null) {
            JsonObject jsonBody = null;
            itemEntity.useTime = selectedEndDate.getTimeInMillis() - selectedStartDate.getTimeInMillis();
            itemEntity.startTime = selectedStartDate.getTimeInMillis();
            itemEntity.endTime = selectedEndDate.getTimeInMillis();
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
            getApi().timingUpdate(RequestUtils.createJsonBody(jsonBody.toString()))
                    .enqueue(new SimpleCallBack<JsonElement>() {
                        @Override
                        public void onSuccess(Call<ResEntity<JsonElement>> call, Response<ResEntity<JsonElement>> response) {

                        }
                    });
        }
    }

    @Override
    public void onFragmentCallBack(Fragment fragment, int type, Bundle params) {
        if (fragment instanceof WorkTypeSelectDialogFragment && params != null) {
            Serializable serializable = params.getSerializable(BaseDialogFragment.KEY_FRAGMENT_RESULT);
            if (serializable instanceof WorkType) {
                itemEntity.workTypeId = ((WorkType) serializable).pkId;
                worktypeNameTv.setText(((WorkType) serializable).name);
            }
        } else if (fragment instanceof TaskSelectDialogFragment && params != null) {
            Serializable serializable = params.getSerializable(BaseDialogFragment.KEY_FRAGMENT_RESULT);
            if (serializable instanceof TaskEntity.TaskItemEntity) {
                TaskEntity.TaskItemEntity item = ((TaskEntity.TaskItemEntity) serializable);
                itemEntity.taskPkId = item.id;
                itemEntity.taskName = item.name;

                if (item.matter != null) {
                    ProjectEntity projectEntity = item.matter.convert2Model();
                    if (!StringUtils.equalsIgnoreCase(itemEntity.matterPkId, projectEntity.pkId, false)) {
                        itemEntity.workTypeId = null;
                        itemEntity.workTypeName = null;
                        worktypeNameTv.setText("未选择");
                    }
                    itemEntity.matterPkId = projectEntity.pkId;
                    itemEntity.matterName = projectEntity.name;
                    projectNameTv.setText(projectEntity.name);
                }
                taskNameTv.setText(itemEntity.taskName);
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
                if (!TextUtils.equals(this.itemEntity.pkId, projectEntity.pkId)) {
                    this.itemEntity.taskPkId = null;
                    this.itemEntity.workTypeId = null;
                    worktypeNameTv.setText("未选择");
                    taskNameTv.setText("未关联");
                }
                itemEntity.matterPkId = projectEntity.pkId;
                itemEntity.matterName = projectEntity.name;
                projectNameTv.setText(projectEntity.name);
            }

        }
    }
}
