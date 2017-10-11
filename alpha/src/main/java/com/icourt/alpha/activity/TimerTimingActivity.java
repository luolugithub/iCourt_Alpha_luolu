package com.icourt.alpha.activity;

import android.animation.Animator;
import android.animation.ValueAnimator;
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
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.icourt.alpha.R;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.base.BaseDialogFragment;
import com.icourt.alpha.entity.bean.AlphaUserInfo;
import com.icourt.alpha.entity.bean.ProjectEntity;
import com.icourt.alpha.entity.bean.TaskEntity;
import com.icourt.alpha.entity.bean.TimeEntity;
import com.icourt.alpha.entity.bean.WorkType;
import com.icourt.alpha.entity.event.OverTimingRemindEvent;
import com.icourt.alpha.entity.event.TimingEvent;
import com.icourt.alpha.fragment.dialogfragment.ProjectSimpleSelectDialogFragment;
import com.icourt.alpha.fragment.dialogfragment.TaskSelectDialogFragment;
import com.icourt.alpha.fragment.dialogfragment.TimingChangeDialogFragment;
import com.icourt.alpha.fragment.dialogfragment.WorkTypeSelectDialogFragment;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.interfaces.OnFragmentCallBackListener;
import com.icourt.alpha.utils.DateUtils;
import com.icourt.alpha.utils.DensityUtil;
import com.icourt.alpha.utils.JsonUtils;
import com.icourt.alpha.utils.LoginInfoUtils;
import com.icourt.alpha.utils.StringUtils;
import com.icourt.alpha.utils.SystemUtils;
import com.icourt.alpha.utils.UMMobClickAgent;
import com.icourt.alpha.widget.dialog.BottomActionDialog;
import com.icourt.alpha.widget.filter.InputActionNextFilter;
import com.icourt.alpha.widget.manager.TimerManager;
import com.icourt.api.RequestUtils;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;
import java.util.Arrays;
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

public class TimerTimingActivity extends BaseTimerActivity
        implements
        OnFragmentCallBackListener {

    public static final String KEY_TIME = "key_time";

    @BindView(R.id.titleBack)
    ImageView titleBack;
    @BindView(R.id.titleContent)
    TextView titleContent;
    @BindView(R.id.titleView)
    AppBarLayout titleView;
    @BindView(R.id.timing_tv)
    TextView timingTv;
    @BindView(R.id.tv_start_date)
    TextView tvStartDate;
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
    @BindView(R.id.root_view)
    LinearLayout rootView;
    @BindView(R.id.over_timing_remind_layout)
    ViewGroup overTimingRemind;
    @BindView(R.id.over_timing_title_tv)
    TextView overTimingTitleTv;

    TimeEntity.ItemEntity itemEntity;
    Calendar selectedStartDate = Calendar.getInstance();//选中的开始时间

    public static void launch(@NonNull Context context,
                              @NonNull TimeEntity.ItemEntity timeEntity) {
        if (context == null) return;
        if (timeEntity == null) return;
        Intent intent = new Intent(context, TimerTimingActivity.class);
        intent.putExtra(KEY_TIME, timeEntity);
        context.startActivity(intent);
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
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
        setTitle(R.string.timing_detail);
        ImageView titleActionImage = getTitleActionImage();
        if (titleActionImage != null) {
            titleActionImage.setImageResource(R.mipmap.header_icon_more);
        }
        initTimingView();

        timeNameTv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (itemEntity == null) return;
                if (!TextUtils.isEmpty(s)) {
                    itemEntity.name = s.toString();
                } else {
                    itemEntity.name = "";
                }
            }
        });
        EventBus.getDefault().register(this);
        timeNameTv.setFilters(timingNameInputFilters);
        timeNameTv.setOnEditorActionListener(new InputActionNextFilter());
    }

    private void initTimingView() {
        if (itemEntity != null) {
            selectedStartDate.clear();
            selectedStartDate.setTimeInMillis(itemEntity.startTime);
            setDisplayTiming();
            timingTv.setText(toTime(itemEntity.useTime / 1000));
            timeNameTv.setText(itemEntity.name);
            timeNameTv.setSelection(timeNameTv.getText().length());
            projectNameTv.setText(TextUtils.isEmpty(itemEntity.matterName) ? getString(R.string.timing_not_set) : itemEntity.matterName);
            worktypeNameTv.setText(TextUtils.isEmpty(itemEntity.workTypeName) ? getString(R.string.timing_not_set) : itemEntity.workTypeName);
            taskNameTv.setText(TextUtils.isEmpty(itemEntity.taskName) ? getString(R.string.timing_not_relevance) : itemEntity.taskName);

        }
    }

    /**
     * 设置显示日期、开始时间的显示内容
     */
    private void setDisplayTiming() {
        tvStartDate.setText(DateUtils.getTimeDateFormatYear(itemEntity.startTime));
        startTimeTv.setText(DateUtils.getHHmm(itemEntity.startTime));
    }

    /**
     * 关闭计时提醒
     *
     * @param isSyncServer true：同步到服务器，false：不同步
     */
    private void closeOverTimingRemind(boolean isSyncServer) {
        itemEntity.noRemind = TimeEntity.ItemEntity.STATE_REMIND_OFF;
        viewMoveAnimation(false);
        EventBus.getDefault().post(new OverTimingRemindEvent(OverTimingRemindEvent.ACTION_TIMING_REMIND_NO_REMIND));
        if (isSyncServer) {
            TimerManager.getInstance().setOverTimingRemindClose(TimerManager.OVER_TIME_REMIND_NO_REMIND);
        }
    }

    @OnClick({
            R.id.tv_start_date,
            R.id.start_time_tv,
            R.id.project_layout,
            R.id.worktype_layout,
            R.id.task_layout,
            R.id.titleAction,
            R.id.stop_time_tv,
            R.id.over_timing_close_ll})
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.titleBack:
                saveTiming(false);
                finish();
                break;
            case R.id.titleAction:
                new BottomActionDialog(getContext(),
                        null,
                        Arrays.asList(getString(R.string.timing_delete)),
                        new BottomActionDialog.OnActionItemClickListener() {
                            @Override
                            public void onItemClick(BottomActionDialog dialog, BottomActionDialog.ActionItemAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
                                dialog.dismiss();
                                deleteTiming(itemEntity.pkId);
                            }
                        }).show();
                break;
            case R.id.tv_start_date:
                showDateTimeSelectDialogFragment(TimingChangeDialogFragment.TYPE_CHANGE_START_TIME, itemEntity.startTime, 0);
                break;
            case R.id.start_time_tv:
                showDateTimeSelectDialogFragment(TimingChangeDialogFragment.TYPE_CHANGE_START_TIME, itemEntity.startTime, 0);
                break;
            case R.id.over_timing_close_ll:
                closeOverTimingRemind(true);
                break;
            case R.id.project_layout://所属项目
                showProjectSelectDialogFragment(itemEntity.matterPkId);
                break;
            case R.id.worktype_layout://工作类型
                //计时选择工作类别不需要判断是否选择项目    2017.8.1修改（暂时保留）
                /*if (TextUtils.isEmpty(itemEntity.matterPkId)) {
                    showTopSnackBar("请选择项目");
                    return;
                }*/
                showWorkTypeSelectDialogFragment(itemEntity.matterPkId, itemEntity.workTypeId);
                break;
            case R.id.task_layout://关联任务
                showTaskSelectDialogFragment(itemEntity.matterPkId, itemEntity.taskPkId);
                break;
            case R.id.stop_time_tv:
                itemEntity.state = TimeEntity.ItemEntity.TIMER_STATE_STOP;
                showLoadingDialog(null);
                MobclickAgent.onEvent(getContext(), UMMobClickAgent.stop_timer_click_id);
                TimerManager.getInstance().stopTimer(new SimpleCallBack<TimeEntity.ItemEntity>() {
                    @Override
                    public void onSuccess(Call<ResEntity<TimeEntity.ItemEntity>> call, Response<ResEntity<TimeEntity.ItemEntity>> response) {
                        dismissLoadingDialog();
                        if (response.body().result != null) {
                            itemEntity.endTime = response.body().result.endTime;
                            itemEntity.createTime = response.body().result.createTime;
                            itemEntity.useTime = response.body().result.useTime;
                            itemEntity.workTypeId = response.body().result.workTypeId;
                            itemEntity.taskPkId = response.body().result.taskPkId;
                        }
                        //TimerDetailActivity.launch(getContext(), response.body().result);
                        finish();
                    }

                    @Override
                    public void onFailure(Call<ResEntity<TimeEntity.ItemEntity>> call, Throwable t) {
                        dismissLoadingDialog();
                        super.onFailure(call, t);
                    }
                });
                break;
            default:
                super.onClick(view);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        saveTiming(false);
        super.onBackPressed();
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
     * 保存正在计时的修改内容
     *
     * @param isChangeStartTime 是否修改来开始时间
     */
    private void saveTiming(final boolean isChangeStartTime) {
        //实时保存
        if (itemEntity != null) {
            JsonObject jsonBody = null;
            try {
                jsonBody = JsonUtils.object2JsonObject(itemEntity);
            } catch (JsonParseException e) {
                e.printStackTrace();
            }
            if (jsonBody == null) return;
            if (isChangeStartTime) { //添加开始时间
                jsonBody.addProperty("startTime", selectedStartDate.getTimeInMillis());
            }
            if (jsonBody.has("matterName")) {
                jsonBody.remove("matterName");
            }
            if (jsonBody.has("timingCount")) {
                jsonBody.remove("timingCount");
            }
            if (jsonBody.has("workTypeName")) {
                jsonBody.remove("workTypeName");
            }
            if (jsonBody.has("endTime")) {
                jsonBody.remove("endTime");
            }
            AlphaUserInfo loginUserInfo = getLoginUserInfo();
            String clientId = "";
            if (loginUserInfo != null) {
                clientId = loginUserInfo.localUniqueId;
            }
            jsonBody.addProperty("clientId", clientId);
            jsonBody.addProperty("taskPkId", TextUtils.isEmpty(itemEntity.taskPkId) ? "" : itemEntity.taskPkId);
            jsonBody.addProperty("workTypeId", TextUtils.isEmpty(itemEntity.workTypeId) ? "" : itemEntity.workTypeId);
            showLoadingDialog(null);
            callEnqueue(
                    getApi().timingUpdate(RequestUtils.createJsonBody(jsonBody.toString())),
                    new SimpleCallBack<JsonElement>() {
                        @Override
                        public void onSuccess(Call<ResEntity<JsonElement>> call, Response<ResEntity<JsonElement>> response) {
                            dismissLoadingDialog();
                            if (isChangeStartTime) {//如果修改了开始时间，要让计时器同步一下服务器时间.
                                itemEntity.startTime = selectedStartDate.getTimeInMillis();
                                setDisplayTiming();
                                TimerManager.getInstance().timerQuerySync();
                            }
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
        boolean isChangeStartTime = false;
        if (fragment instanceof WorkTypeSelectDialogFragment && params != null) {
            Serializable serializable = params.getSerializable(BaseDialogFragment.KEY_FRAGMENT_RESULT);
            if (serializable instanceof WorkType) {
                itemEntity.workTypeId = ((WorkType) serializable).pkId;
                itemEntity.workTypeName = ((WorkType) serializable).name;
                worktypeNameTv.setText(((WorkType) serializable).name);
            }
        } else if (fragment instanceof TaskSelectDialogFragment && params != null) {
            Serializable serializable = params.getSerializable(BaseDialogFragment.KEY_FRAGMENT_RESULT);
            if (serializable instanceof TaskEntity.TaskItemEntity) {
                TaskEntity.TaskItemEntity item = ((TaskEntity.TaskItemEntity) serializable);
                itemEntity.taskPkId = item.id;
                itemEntity.taskName = item.name;
                taskNameTv.setText(itemEntity.taskName);
                if (item.matter != null) {
                    ProjectEntity projectEntity = item.matter.convert2Model();
                    if (!StringUtils.equalsIgnoreCase(itemEntity.matterPkId, projectEntity.pkId, false)) {
                        itemEntity.workTypeId = "";
                        itemEntity.workTypeName = "";
                        worktypeNameTv.setText(R.string.timing_not_select);
                    }
                    itemEntity.matterPkId = projectEntity.pkId;
                    itemEntity.matterName = projectEntity.name;
                    projectNameTv.setText(projectEntity.name);
                }
            }
        } else if (fragment instanceof ProjectSimpleSelectDialogFragment && params != null) {
            Serializable serializable = params.getSerializable(BaseDialogFragment.KEY_FRAGMENT_RESULT);
            if (serializable instanceof ProjectEntity) {
                ProjectEntity projectEntity = (ProjectEntity) serializable;
                if (projectEntity == null) return;
                if (!TextUtils.equals(itemEntity.matterPkId, projectEntity.pkId)) {
                    itemEntity.taskPkId = "";
                    itemEntity.taskName = "";
                    itemEntity.workTypeId = "";
                    itemEntity.workTypeName = "";
                    itemEntity.matterPkId = projectEntity.pkId;
                    worktypeNameTv.setText(R.string.timing_not_select);
                    taskNameTv.setText(R.string.timing_not_relevance);
                }
                itemEntity.matterPkId = projectEntity.pkId;
                itemEntity.matterName = projectEntity.name;
                projectNameTv.setText(projectEntity.name);
            }
        } else if (fragment instanceof TimingChangeDialogFragment && params != null) {
            isChangeStartTime = true;
            long timeMillis = params.getLong(TimingChangeDialogFragment.TIME_RESULT_MILLIS);
            selectedStartDate.clear();
            selectedStartDate.setTimeInMillis(timeMillis);
        }
        saveTiming(isChangeStartTime);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTimerEvent(TimingEvent event) {
        if (event == null) return;
        if (itemEntity == null) return;
        switch (event.action) {
            case TimingEvent.TIMING_UPDATE_PROGRESS:
                if (TextUtils.equals(event.timingId, itemEntity.pkId)) {
                    timingTv.setText(toTime(event.timingSecond));
                    if (itemEntity.noRemind == TimeEntity.ItemEntity.STATE_REMIND_ON) {
                        if (TimeUnit.SECONDS.toHours(event.timingSecond) >= 2) {//如果该计时超过两小时，显示超过2小时的提醒。
                            if (overTimingRemind.getVisibility() != View.VISIBLE) {
                                updateOverTimingRemindText(event.timingSecond);
                                overTimingRemind.setVisibility(View.VISIBLE);
                                viewMoveAnimation(true);
                            }
                            if (event.timingSecond % 3600 == 0) {
                                updateOverTimingRemindText(event.timingSecond);
                            }
                        }
                    } else if (overTimingRemind.getVisibility() != View.GONE) {
                        viewMoveAnimation(false);
                    }
                }
                break;
            case TimingEvent.TIMING_STOP:
                if (!isDelete) {
                    TimerDetailActivity.launch(getContext(), itemEntity);
                    finish();
                }
                break;
            case TimingEvent.TIMING_SYNC_SUCCESS://计时同步成功后的回调
                if (TimeUnit.SECONDS.toHours(TimerManager.getInstance().getTimingSeconds()) < 2) {//计时成功后的回调小于2小时，隐藏计时提醒的窗体
                    viewMoveAnimation(false);
                }
                break;
        }
    }

    private void updateOverTimingRemindText(long timingSecond) {
        String overTimingString = String.format(getContext().getResources().getString(R.string.timer_over_timing_remind_text), TimeUnit.SECONDS.toHours(timingSecond));
        overTimingTitleTv.setText(overTimingString);
    }

    /**
     * 显示超过两小时的任务的提醒
     *
     * @param isShow true：显示；false：不显示。
     */
    private void viewMoveAnimation(final boolean isShow) {
        final int durationTime = 300;

        ValueAnimator animator = null;
        if (isShow) {
            animator = ValueAnimator.ofInt(0, DensityUtil.dip2px(getContext(), 42));
        } else if (overTimingRemind.getVisibility() != View.GONE) {
            animator = ValueAnimator.ofInt(DensityUtil.dip2px(getContext(), 42), 0);
        }
        if (animator == null) return;
        animator.setDuration(durationTime).start();
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int height = (Integer) animation.getAnimatedValue();

                ViewGroup.LayoutParams params = overTimingRemind.getLayoutParams();
                params.height = height;
                overTimingRemind.setLayoutParams(params);
            }
        });
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (!isShow) {
                    overTimingRemind.setVisibility(View.GONE);
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }

    /**
     * 获取本地唯一id
     *
     * @return
     */
    private String getlocalUniqueId() {
        AlphaUserInfo loginUserInfo = LoginInfoUtils.getLoginUserInfo();
        if (loginUserInfo != null) {
            return loginUserInfo.localUniqueId;
        }
        return null;
    }

    public String createOverTimingRemindKey(String pkId) {
        return TimerTimingActivity.class.getSimpleName() + pkId;
    }

    public String toTime(long timesSecond) {
        long hour = timesSecond / 3600;
        long minute = timesSecond % 3600 / 60;
        long second = timesSecond % 60;
        return String.format("%02d:%02d:%02d", hour, minute, second);
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
