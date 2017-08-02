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
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.icourt.alpha.R;
import com.icourt.alpha.base.BaseDialogFragment;
import com.icourt.alpha.entity.bean.AlphaUserInfo;
import com.icourt.alpha.entity.bean.ProjectEntity;
import com.icourt.alpha.entity.bean.TaskEntity;
import com.icourt.alpha.entity.bean.TimeEntity;
import com.icourt.alpha.entity.bean.WorkType;
import com.icourt.alpha.entity.event.ServerTimingEvent;
import com.icourt.alpha.entity.event.TimingEvent;
import com.icourt.alpha.fragment.dialogfragment.ProjectSimpleSelectDialogFragment;
import com.icourt.alpha.fragment.dialogfragment.TaskSelectDialogFragment;
import com.icourt.alpha.fragment.dialogfragment.WorkTypeSelectDialogFragment;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.interfaces.OnFragmentCallBackListener;
import com.icourt.alpha.utils.DateUtils;
import com.icourt.alpha.utils.JsonUtils;
import com.icourt.alpha.utils.LoginInfoUtils;
import com.icourt.alpha.utils.RAUtils;
import com.icourt.alpha.utils.StringUtils;
import com.icourt.alpha.utils.SystemUtils;
import com.icourt.alpha.widget.manager.TimerManager;
import com.icourt.api.RequestUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;

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

    private static final String KEY_TIME = "key_time";
    private static final int FINISH_TYPE = 1;//点击完成type
    private static final int SAVE_ONE_TYPE = 2;//选择项目／工作类别／关联任务type

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
    @BindView(R.id.root_view)
    LinearLayout rootView;


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
        titleAction.setVisibility(View.INVISIBLE);
        setTitle("计时详情");
        TextView titleActionTextView = getTitleActionTextView();
        if (titleActionTextView != null) {
            titleActionTextView.setText("完成");
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
        timeNameTv.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                return (event.getKeyCode() == KeyEvent.KEYCODE_ENTER);
            }
        });
    }

    private void initTimingView() {
        if (itemEntity != null) {
            timingTv.setText(toTime(itemEntity.useTime / 1000));
            startTimeTv.setText(DateUtils.getHHmm(itemEntity.startTime));
            timeNameTv.setText(itemEntity.name);
            timeNameTv.setSelection(timeNameTv.getText().length());
            projectNameTv.setText(TextUtils.isEmpty(itemEntity.matterName) ? "未设置" : itemEntity.matterName);
            worktypeNameTv.setText(TextUtils.isEmpty(itemEntity.workTypeName) ? "未设置" : itemEntity.workTypeName);
            taskNameTv.setText(TextUtils.isEmpty(itemEntity.taskName) ? "未关联" : itemEntity.taskName);
        }
    }


    @OnClick({
            R.id.project_layout,
            R.id.worktype_layout,
            R.id.task_layout,
            R.id.titleAction,
            R.id.stop_time_tv,
            R.id.root_view})
    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.root_view:
                if (itemEntity != null) {
                    itemEntity.name = timeNameTv.getText().toString();
                    saveTiming();
                }
                break;
            case R.id.titleAction:
                finish();
                break;
            case R.id.project_layout://所属项目
                showProjectSelectDialogFragment(itemEntity.matterPkId);
                break;
            case R.id.worktype_layout://工作类型
                //计时选择工作类别不需要判断是否选择项目    2017.8.1修改
//                if (TextUtils.isEmpty(itemEntity.matterPkId)) {
//                    showTopSnackBar("请选择项目");
//                    return;
//                }
                showWorkTypeSelectDialogFragment(itemEntity.matterPkId, itemEntity.workTypeId);
                break;
            case R.id.task_layout://关联任务
                showTaskSelectDialogFragment(itemEntity.matterPkId, itemEntity.taskPkId);
                break;
            case R.id.stop_time_tv:
                itemEntity.state = TimeEntity.ItemEntity.TIMER_STATE_STOP;
                showLoadingDialog(null);
                TimerManager.getInstance().stopTimer(new SimpleCallBack<TimeEntity.ItemEntity>() {
                    @Override
                    public void onSuccess(Call<ResEntity<TimeEntity.ItemEntity>> call, Response<ResEntity<TimeEntity.ItemEntity>> response) {
                        dismissLoadingDialog();
                        if (response.body().result != null) {

                            itemEntity.endTime = response.body().result.endTime;
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
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (!RAUtils.inRangeOfView(titleBack, ev)) {
                    if (itemEntity != null) {
                        itemEntity.name = timeNameTv.getText().toString();
                        saveTiming();
                    }
                }
                SystemUtils.hideSoftKeyBoard(getActivity(), true);
                break;
        }
        return super.dispatchTouchEvent(ev);
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
            if (jsonBody.has("endTime")) {
                jsonBody.remove("endTime");
            }
            AlphaUserInfo loginUserInfo = getLoginUserInfo();
            String clientId = "";
            if (loginUserInfo != null) {
                clientId = loginUserInfo.localUniqueId;
            }
            jsonBody.addProperty("clientId", clientId);
            jsonBody.addProperty("taskPkId", itemEntity.taskPkId);
            jsonBody.addProperty("workTypeId", itemEntity.workTypeId);
            showLoadingDialog(null);
            getApi().timingUpdate(RequestUtils.createJsonBody(jsonBody.toString()))
                    .enqueue(new SimpleCallBack<JsonElement>() {
                        @Override
                        public void onSuccess(Call<ResEntity<JsonElement>> call, Response<ResEntity<JsonElement>> response) {
                            dismissLoadingDialog();
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
                        worktypeNameTv.setText("未选择");
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
                    worktypeNameTv.setText("未选择");
                    taskNameTv.setText("未关联");
                }
                itemEntity.matterPkId = projectEntity.pkId;
                itemEntity.matterName = projectEntity.name;
                projectNameTv.setText(projectEntity.name);
            }
        }
        saveTiming();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTimerEvent(TimingEvent event) {
        if (event == null) return;
        if (itemEntity == null) return;
        switch (event.action) {
            case TimingEvent.TIMING_UPDATE_PROGRESS:
                if (TextUtils.equals(event.timingId, itemEntity.pkId)) {
                    timingTv.setText(toTime(event.timingSecond));
                }
                break;
            case TimingEvent.TIMING_STOP:
                TimerDetailActivity.launch(getContext(), itemEntity);
                finish();
                break;
        }
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

    /**
     * 产品确认：这个页面不做同步
     * @param event
     */
//    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onServerTimingEvent(ServerTimingEvent event) {
        if (event == null) return;
        if (itemEntity == null) return;
        if (TextUtils.equals(event.clientId, getlocalUniqueId())) return;
        if (event.isSyncObject() && event.isSyncTimingType()) {
            //信息更新发生变化
            if (TextUtils.equals(event.scene, ServerTimingEvent.TIMING_SYNC_EDIT)) {
                if (TextUtils.equals(event.pkId, itemEntity.pkId)) {
                    itemEntity = event;
                    initTimingView();
                }
            }
        }
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
