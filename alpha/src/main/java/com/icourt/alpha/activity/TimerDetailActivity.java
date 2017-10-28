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
import android.view.MotionEvent;
import android.view.View;
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
import com.icourt.alpha.fragment.dialogfragment.ProjectSimpleSelectDialogFragment;
import com.icourt.alpha.fragment.dialogfragment.TaskSelectDialogFragment;
import com.icourt.alpha.fragment.dialogfragment.TimingChangeDialogFragment;
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
import com.icourt.alpha.widget.filter.InputActionNextFilter;
import com.icourt.api.RequestUtils;

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
 *
 * @author lu.zhao  E-mail:zhaolu@icourt.ccdeleteTiming
 *         date createTime：17/5/10
 *         version 2.0.0
 */

public class TimerDetailActivity extends BaseTimerActivity
        implements
        OnFragmentCallBackListener {

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

    TimeEntity.ItemEntity mItemEntity;//用来记录从上个界面传递过来计时相关的参数。
    private final Calendar selectedStartDate = Calendar.getInstance();//选中的开始时间
    private final Calendar selectedEndDate = Calendar.getInstance();//选中的结束时间

    public static void launch(@NonNull Context context,
                              @NonNull TimeEntity.ItemEntity timeEntity) {
        if (context == null || timeEntity == null) {
            return;
        }
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
        setTitle(getString(R.string.timing_detail));
        ImageView titleActionImage = getTitleActionImage();
        if (titleActionImage != null) {
            titleActionImage.setImageResource(R.mipmap.header_icon_more);
        }
        mItemEntity = (TimeEntity.ItemEntity) getIntent().getSerializableExtra(KEY_TIME);
        //初始化相关数据
        if (mItemEntity != null) {
            //避免服务器小于1分钟
            if (mItemEntity.endTime - mItemEntity.startTime < TimeUnit.MINUTES.toMillis(1)) {
                mItemEntity.endTime = mItemEntity.startTime + TimeUnit.MINUTES.toMillis(1);
            }
            selectedStartDate.clear();
            selectedStartDate.setTimeInMillis(mItemEntity.startTime);
            selectedEndDate.clear();
            selectedEndDate.setTimeInMillis(mItemEntity.endTime);

            circleTimerView.setMiniTime(70);
            //初始化时间显示数据
            setTimeViewData();
            //初始化内容显示数据
            initContentView(mItemEntity);

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
                    if (selectedStartDate.getTimeInMillis() + time * 1000 > System.currentTimeMillis()) {
                        showTopSnackBar(getString(R.string.timing_donot_select_future_time));
                        selectedEndDate.clear();
                        selectedEndDate.setTimeInMillis(System.currentTimeMillis());
                    } else {
                        selectedEndDate.setTimeInMillis(selectedStartDate.getTimeInMillis() + time * 1000);
                    }
                    setTimeViewData();
                }

                /**
                 * 注意，在这个方法里不要调用setCurrentTime()方法，否则会导致死循环，切记切记。
                 * @param time 秒
                 */
                @Override
                public void onTimerSetValueChanged(long time) {
                }

                @Override
                public void onTimerSetValueChange(long time) {
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
                        mItemEntity.name = s.toString();
                    } else {
                        mItemEntity.name = "";
                    }
                }
            });
        }

        timeNameTv.setFilters(timingNameInputFilters);
        timeNameTv.setOnEditorActionListener(new InputActionNextFilter());
    }

    /**
     * 初始化内容显示数据
     *
     * @param itemEntity 所要显示的数据对象
     */
    private void initContentView(TimeEntity.ItemEntity itemEntity) {
        if (itemEntity == null) {
            return;
        }
        //计时标题
        timeNameTv.setText(itemEntity.name);
        if (!TextUtils.isEmpty(timeNameTv.getText())) {
            timeNameTv.setSelection(timeNameTv.getText().length());
        }
        projectNameTv.setText(TextUtils.isEmpty(itemEntity.matterName) ? getString(R.string.timing_not_set) : itemEntity.matterName);
        worktypeNameTv.setText(TextUtils.isEmpty(itemEntity.workTypeName) ? getString(R.string.timing_not_set) : itemEntity.workTypeName);
        taskNameTv.setText(TextUtils.isEmpty(itemEntity.taskName) ? getString(R.string.timing_not_relevance) : itemEntity.taskName);
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
            R.id.titleBack})
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.titleBack:
                saveTiming(mItemEntity, true);
                break;
            case R.id.titleAction:
                new BottomActionDialog(getContext(),
                        null,
                        Arrays.asList(getString(R.string.timing_delete)),
                        new BottomActionDialog.OnActionItemClickListener() {
                            @Override
                            public void onItemClick(BottomActionDialog dialog, BottomActionDialog.ActionItemAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
                                dialog.dismiss();
                                deleteTiming(mItemEntity.pkId);
                            }
                        }).show();
                break;
            case R.id.minus_time_image:
                //－时间，会有个最小值。
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
            case R.id.add_time_image:
                //＋时间，不能超过当前时间。
                long endTime = selectedEndDate.getTimeInMillis();
                if (endTime + TimeUnit.MINUTES.toMillis(15) <= System.currentTimeMillis()) {
                    endTime = endTime + TimeUnit.MINUTES.toMillis(15);
                    selectedEndDate.clear();
                    selectedEndDate.setTimeInMillis(endTime);
                    setTimeViewData();
                } else {
                    showTopSnackBar(R.string.timing_donot_select_future_time);
                }
                break;
            case R.id.use_time_date:
                //显示计时开始时间的日期
                showDateTimeSelectDialogFragment(
                        TimingChangeDialogFragment.TYPE_CHANGE_START_TIME,
                        selectedStartDate.getTimeInMillis(),
                        selectedEndDate.getTimeInMillis());
                break;
            case R.id.start_time_min_tv:
                //显示计时开始时间的时分
                showDateTimeSelectDialogFragment(TimingChangeDialogFragment.TYPE_CHANGE_START_TIME, selectedStartDate.getTimeInMillis(), selectedEndDate.getTimeInMillis());
                break;
            case R.id.stop_time_min_tv:
                //显示计时结束时间的时分
                showDateTimeSelectDialogFragment(TimingChangeDialogFragment.TYPE_CHANGE_END_TIME, selectedStartDate.getTimeInMillis(), selectedEndDate.getTimeInMillis());
                break;
            case R.id.project_layout:
                //所属项目
                if (mItemEntity != null) {
                    if (TextUtils.isEmpty(mItemEntity.matterPkId)) {
                        showProjectSelectDialogFragment(null);
                    } else {
                        showBottomMenu();
                    }
                }
                break;
            case R.id.worktype_layout:
                //工作类型
                showWorkTypeSelectDialogFragment(mItemEntity.matterPkId, mItemEntity.workTypeId);
                break;
            case R.id.task_layout:
                //关联任务
                showTaskSelectDialogFragment(mItemEntity.matterPkId, mItemEntity.taskPkId);
                break;
            default:
                super.onClick(view);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        saveTiming(mItemEntity, true);
    }

    /**
     * 显示底部菜单
     */
    private void showBottomMenu() {
        new BottomActionDialog(getContext(),
                null,
                Arrays.asList(getString(R.string.timing_select_project), getString(R.string.timing_check_project)),
                new BottomActionDialog.OnActionItemClickListener() {
                    @Override
                    public void onItemClick(BottomActionDialog dialog, BottomActionDialog.ActionItemAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
                        dialog.dismiss();
                        switch (position) {
                            case 0:
                                showProjectSelectDialogFragment(mItemEntity.matterPkId);
                                break;
                            case 1:
                                if (mItemEntity != null) {
                                    ProjectDetailActivity.launch(getContext(), mItemEntity.matterPkId, mItemEntity.matterName);
                                }
                                break;
                            default:
                                break;
                        }
                    }
                }).show();
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

        long rangeTime = selectedEndDate.getTimeInMillis() - selectedStartDate.getTimeInMillis();
        int time = (int) (rangeTime / 1000);
        circleTimerView.setCurrentTime(time);
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

    @Override
    public void onFragmentCallBack(Fragment fragment, int type, Bundle params) {
        TimeEntity.ItemEntity cloneEntity;
        try {
            cloneEntity = (TimeEntity.ItemEntity) mItemEntity.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            cloneEntity = new TimeEntity.ItemEntity();
        }
        if (fragment instanceof WorkTypeSelectDialogFragment && params != null) {
            Serializable serializable = params.getSerializable(BaseDialogFragment.KEY_FRAGMENT_RESULT);
            if (serializable instanceof WorkType) {
                cloneEntity.workTypeId = ((WorkType) serializable).pkId;
                cloneEntity.workTypeName = ((WorkType) serializable).name;
            }
        } else if (fragment instanceof TaskSelectDialogFragment && params != null) {
            Serializable serializable = params.getSerializable(BaseDialogFragment.KEY_FRAGMENT_RESULT);
            if (serializable instanceof TaskEntity.TaskItemEntity) {
                TaskEntity.TaskItemEntity item = ((TaskEntity.TaskItemEntity) serializable);
                cloneEntity.taskPkId = item.id;
                cloneEntity.taskName = item.name;

                if (item.matter != null) {
                    ProjectEntity projectEntity = item.matter.convert2Model();
                    if (!StringUtils.equalsIgnoreCase(mItemEntity.matterPkId, projectEntity.pkId, false)) {
                        cloneEntity.workTypeId = "";
                        cloneEntity.workTypeName = "";
                    }
                    cloneEntity.matterPkId = projectEntity.pkId;
                    cloneEntity.matterName = projectEntity.name;
                }
            }
        } else if (fragment instanceof ProjectSimpleSelectDialogFragment && params != null) {
            Serializable serializable = params.getSerializable(BaseDialogFragment.KEY_FRAGMENT_RESULT);
            if (serializable instanceof ProjectEntity) {
                ProjectEntity projectEntity = (ProjectEntity) serializable;
                if (!TextUtils.equals(this.mItemEntity.pkId, projectEntity.pkId)) {
                    cloneEntity.taskPkId = "";
                    cloneEntity.taskName = "";
                    cloneEntity.workTypeId = "";
                    cloneEntity.workTypeName = "";
                }
                cloneEntity.matterPkId = projectEntity.pkId;
                cloneEntity.matterName = projectEntity.name;
            }

        } else if (fragment instanceof TimingChangeDialogFragment) {
            long resultTime = params.getLong(TimingChangeDialogFragment.TIME_RESULT_MILLIS);
            if (type == TimingChangeDialogFragment.TYPE_CHANGE_START_TIME) {
                //修改开始时间
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
        }
        saveTiming(cloneEntity, false);
    }

    /**
     * 保存对计时的修改
     *
     * @param itemEntity 所要保存的对象
     * @param isFinish   保存成功之后是否销毁界面
     */
    private void saveTiming(final TimeEntity.ItemEntity itemEntity, final boolean isFinish) {
        //实时保存
        if (itemEntity != null) {
            JsonObject jsonBody = null;
            //工作日期
            final Calendar workDateCalendar = Calendar.getInstance();
            workDateCalendar.set(Calendar.DAY_OF_YEAR, selectedStartDate.get(Calendar.DAY_OF_YEAR));
            workDateCalendar.set(Calendar.HOUR_OF_DAY, 0);
            workDateCalendar.set(Calendar.MINUTE, 0);
            workDateCalendar.set(Calendar.SECOND, 0);
            workDateCalendar.set(Calendar.MILLISECOND, 0);
            //开始时间
            long startTime = selectedStartDate.getTimeInMillis();
            //结束时间
            long endTime = selectedEndDate.getTimeInMillis();
            //使用时间
            long useTime = selectedEndDate.getTimeInMillis() - selectedStartDate.getTimeInMillis();
            //工作日期
            long workDate = workDateCalendar.getTimeInMillis();

            try {
                jsonBody = JsonUtils.object2JsonObject(itemEntity);
            } catch (JsonParseException e) {
                e.printStackTrace();
            }
            if (jsonBody == null) {
                return;
            }
            //添加时间相关参数
            jsonBody.addProperty("startTime", startTime);
            jsonBody.addProperty("endTime", endTime);
            jsonBody.addProperty("useTime", useTime);
            jsonBody.addProperty("workDate", workDate);

            if (jsonBody.has("matterName")) {
                jsonBody.remove("matterName");
            }
            if (jsonBody.has("timingCount")) {
                jsonBody.remove("timingCount");
            }
            if (jsonBody.has("workTypeName")) {
                jsonBody.remove("workTypeName");
            }
            AlphaUserInfo loginUserInfo = getLoginUserInfo();
            String clientId = "";
            if (loginUserInfo != null) {
                clientId = loginUserInfo.localUniqueId;
            }
            jsonBody.addProperty("clientId", clientId);
            jsonBody.addProperty("taskPkId", itemEntity.taskPkId);
            jsonBody.addProperty("workTypeId", itemEntity.workTypeId);
            callEnqueue(
                    getApi().timingUpdate(RequestUtils.createJsonBody(jsonBody.toString())),
                    new SimpleCallBack<JsonElement>() {
                        @Override
                        public void onSuccess(Call<ResEntity<JsonElement>> call, Response<ResEntity<JsonElement>> response) {
                            //修改成功，将时间赋值给成员变量，并刷新界面
                            mItemEntity = itemEntity;
                            mItemEntity.startTime = selectedStartDate.getTimeInMillis();
                            mItemEntity.useTime = selectedEndDate.getTimeInMillis() - selectedStartDate.getTimeInMillis();
                            mItemEntity.workDate = workDateCalendar.getTimeInMillis();
                            mItemEntity.endTime = selectedEndDate.getTimeInMillis();
                            setTimeViewData();
                            initContentView(mItemEntity);
                            if (isFinish) {
                                finish();
                            }
                        }

                        @Override
                        public void defNotify(String noticeStr) {
                            showToast(noticeStr);
                            //修改失败，将成员变量的值重新赋值给开始和结束时间
                            selectedStartDate.clear();
                            selectedStartDate.setTimeInMillis(mItemEntity.startTime);
                            selectedEndDate.clear();
                            selectedEndDate.setTimeInMillis(mItemEntity.endTime);
                        }

                        @Override
                        public void onFailure(Call<ResEntity<JsonElement>> call, Throwable t) {
                            super.onFailure(call, t);
                            if (isFinish) {
                                finish();
                            }
                        }
                    });
        }
    }

}
