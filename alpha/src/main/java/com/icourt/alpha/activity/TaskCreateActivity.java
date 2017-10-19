package com.icourt.alpha.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.icourt.alpha.R;
import com.icourt.alpha.adapter.TaskUsersAdapter;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.constants.TaskConfig;
import com.icourt.alpha.entity.bean.ProjectEntity;
import com.icourt.alpha.entity.bean.TaskEntity;
import com.icourt.alpha.entity.bean.TaskGroupEntity;
import com.icourt.alpha.entity.bean.TaskReminderEntity;
import com.icourt.alpha.entity.event.TaskActionEvent;
import com.icourt.alpha.fragment.dialogfragment.DateSelectDialogFragment;
import com.icourt.alpha.fragment.dialogfragment.ProjectSelectDialogFragment;
import com.icourt.alpha.fragment.dialogfragment.TaskAllotSelectDialogFragment;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.interfaces.OnFragmentCallBackListener;
import com.icourt.alpha.utils.DateUtils;
import com.icourt.alpha.utils.SpUtils;
import com.icourt.alpha.utils.StringUtils;
import com.icourt.alpha.utils.SystemUtils;
import com.icourt.alpha.utils.UMMobClickAgent;
import com.icourt.alpha.widget.filter.LengthListenFilter;
import com.icourt.api.RequestUtils;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Response;

import static com.icourt.alpha.base.BaseDialogFragment.KEY_FRAGMENT_RESULT;

/**
 * @author zhaolu  E-mail:zhaolu@icourt.cc
 * @version 2.0.0
 * @Description
 * @Company Beijing icourt
 * @date createTime：2017/5/4
 */
public class TaskCreateActivity extends ListenBackActivity
        implements ProjectSelectDialogFragment.OnProjectTaskGroupSelectListener, OnFragmentCallBackListener, BaseRecyclerAdapter.OnItemClickListener {

    private static final String KEY_CONTENT = "content";
    private static final String KEY_STARTTIME = "startTime";
    private static final String KEY_PROJECTID = "projectId";
    private static final String KEY_PROJECTNAME = "projectName";
    private static final int CONTENT_MAX_LENGTH = 200;

    String content, startTime;

    private static final String KEY_TASK_TITLE = "taskTitle";
    private static final String KEY_TASK_DUE_TIME = "taskDueTime";
    private static final String KEY_TASK_DESC = "taskDesc";
    private static final String KEY_PROJECT_ID = "projectId";
    private static final String KEY_PROJECT_NAME = "projectName";

    private static final String KEY_CACHE_TITLE = String.format("%s_%s", TaskCreateActivity.class.getSimpleName(), "cacheTitle");
    private static final String KEY_CACHE_DESC = String.format("%s_%s", TaskCreateActivity.class.getSimpleName(), "cacheDesc");

    private static final String ACTION_FROM_PROJECT = "fromProject";

    @BindView(R.id.titleBack)
    CheckedTextView titleBack;
    @BindView(R.id.titleContent)
    TextView titleContent;
    @BindView(R.id.titleAction)
    CheckedTextView titleAction;
    @BindView(R.id.titleView)
    AppBarLayout titleView;
    @BindView(R.id.task_name_et)
    EditText taskNameEt;
    @BindView(R.id.task_desc_et)
    EditText taskDescEt;
    @BindView(R.id.project_name_tv)
    TextView projectNameTv;
    @BindView(R.id.task_group_name_tv)
    TextView taskGroupNameTv;
    @BindView(R.id.project_layout)
    LinearLayout projectLayout;
    @BindView(R.id.task_duetime_tv)
    TextView taskDuetimeTv;
    @BindView(R.id.duetime_layout)
    LinearLayout duetimeLayout;
    @BindView(R.id.task_ower_recyclerview)
    RecyclerView taskOwerRecyclerview;
    @BindView(R.id.ower_layout)
    LinearLayout owerLayout;

    List<TaskEntity.TaskItemEntity.AttendeeUserEntity> attendeeUserEntities = new ArrayList<>();
    String taskTitle, taskDesc;
    String projectId, taskGroupId;
    long dueTime;
    TaskUsersAdapter usersAdapter;
    String projectName;
    @BindView(R.id.task_group_tv)
    TextView taskGroupTv;
    @BindView(R.id.task_group_layout)
    LinearLayout taskGroupLayout;

    TaskReminderEntity taskReminderEntity;


    /**
     * 如果name  desc 为空 从缓存中拿
     *
     * @param context
     * @param taskTitle
     * @param dueTime
     */
    public static void launch(@NonNull Context context,
                              @NonNull String taskTitle,
                              long dueTime) {
        if (context == null) return;
        Intent intent = new Intent(context, TaskCreateActivity.class);
        intent.putExtra(KEY_TASK_TITLE, TextUtils.isEmpty(taskTitle) ? SpUtils.getTemporaryCache().getStringData(KEY_CACHE_TITLE, "") : taskTitle);
        intent.putExtra(KEY_TASK_DUE_TIME, dueTime);
        //标题超过200以内,整个内容作为详情
        intent.putExtra(KEY_TASK_DESC, StringUtils.length(taskTitle) > TaskConfig.TASK_NAME_MAX_LENGTH ? taskTitle : SpUtils.getTemporaryCache().getStringData(KEY_CACHE_DESC, ""));
        context.startActivity(intent);
    }

    /**
     * 项目详情创建任务
     * name  desc 不从缓存中拿
     *
     * @param context
     * @param projectId
     * @param projectName
     */
    public static void launchFomProject(@NonNull Context context,
                                        @NonNull String projectId,
                                        @NonNull String projectName) {
        if (context == null) return;
        if (TextUtils.isEmpty(projectId)) return;
        Intent intent = new Intent(context, TaskCreateActivity.class);
        intent.putExtra(KEY_PROJECT_ID, projectId);
        intent.putExtra(KEY_PROJECT_NAME, projectName);
        intent.setAction(ACTION_FROM_PROJECT);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_create);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void initView() {
        super.initView();
        setTitle(R.string.task_create_task);

        taskNameEt.setFilters(LengthListenFilter.createSingleInputFilter(new LengthListenFilter(TaskConfig.TASK_NAME_MAX_LENGTH) {
            @Override
            public void onInputOverLength(int maxLength) {
                showToast(getString(R.string.task_name_limit_format, String.valueOf(maxLength)));
            }
        }));

        setTitle(R.string.task_new);
        content = getIntent().getStringExtra(KEY_CONTENT);
        startTime = getIntent().getStringExtra(KEY_STARTTIME);
        projectId = getIntent().getStringExtra(KEY_PROJECTID);
        projectName = getIntent().getStringExtra(KEY_PROJECTNAME);
        if (!TextUtils.isEmpty(content)) {
            if (content.length() > CONTENT_MAX_LENGTH) {
                taskNameEt.setText(content.substring(0, CONTENT_MAX_LENGTH));
                taskDescEt.setText(content);
            } else {
                taskNameEt.setText(content);
            }
        }
        taskDescEt.setFilters(LengthListenFilter.createSingleInputFilter(new LengthListenFilter(TaskConfig.TASK_DESC_MAX_LENGTH) {
            @Override
            public void onInputOverLength(int maxLength) {
                showToast(getString(R.string.task_desc_limit_format, String.valueOf(maxLength)));
            }
        }));
        titleAction.setEnabled(!StringUtils.isEmpty(taskNameEt.getText()));
        taskNameEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                titleAction.setEnabled(!StringUtils.isEmpty(s));
            }
        });

        if (TextUtils.equals(getIntent().getAction(), ACTION_FROM_PROJECT)) {
            projectId = getIntent().getStringExtra(KEY_PROJECT_ID);
            projectName = getIntent().getStringExtra(KEY_PROJECT_NAME);

            projectNameTv.setText(projectName);
            taskGroupLayout.setVisibility(View.VISIBLE);
        } else {
            taskTitle = getIntent().getStringExtra(KEY_TASK_TITLE);
            taskDesc = getIntent().getStringExtra(KEY_TASK_DESC);
            dueTime = getIntent().getLongExtra(KEY_TASK_DUE_TIME, 0);

            //如果标题超过200 就把标题截取200以内
            if (StringUtils.length(taskTitle) > TaskConfig.TASK_NAME_MAX_LENGTH) {
                taskTitle = taskTitle.substring(0, 200);
            }
            taskNameEt.setText(taskTitle);
            taskNameEt.setSelection(StringUtils.length(taskNameEt.getText()));
            taskDescEt.setText(taskDesc);
        }

        if (dueTime > 0) {
            setDueTime();
        }
    }

    @OnClick({
            R.id.titleAction,
            R.id.project_layout,
            R.id.task_group_layout,
            R.id.duetime_layout,
            R.id.ower_layout})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //保存
            case R.id.titleAction:
                createNewTask();
                break;
            //选择项目
            case R.id.project_layout:
                showProjectSelectDialogFragment(null);
                break;
            //选择任务组
            case R.id.task_group_layout:
                showProjectSelectDialogFragment(projectId);
                break;
            //选择到期时间
            case R.id.duetime_layout:
                showDateSelectDialogFragment();
                break;
            //选择负责人
            case R.id.ower_layout:
                if (!TextUtils.isEmpty(projectId)) {
                    showTaskAllotSelectDialogFragment(projectId);
                } else {
                    showTopSnackBar(R.string.task_please_check_project);
                }
                break;
            default:
                super.onClick(v);
                break;
        }
    }


    @Override
    protected boolean onPageBackClick(@FROM_BACK int from) {
        //记录输入历史:名字和详情
        SpUtils.getTemporaryCache().putData(KEY_CACHE_TITLE, getTextString(taskNameEt, ""));
        SpUtils.getTemporaryCache().putData(KEY_CACHE_DESC, getTextString(taskDescEt, ""));
        return false;
    }

    /**
     * 展示选择项目对话框
     */
    public void showProjectSelectDialogFragment(String projectId) {
        String tag = ProjectSelectDialogFragment.class.getSimpleName();
        FragmentTransaction mFragTransaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);
        if (fragment != null) {
            mFragTransaction.remove(fragment);
        }
        ProjectSelectDialogFragment.newInstance(projectId)
                .show(mFragTransaction, tag);
    }

    /**
     * 展示选择到期时间对话框
     */
    private void showDateSelectDialogFragment() {
        String tag = DateSelectDialogFragment.class.getSimpleName();
        FragmentTransaction mFragTransaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);
        if (fragment != null) {
            mFragTransaction.remove(fragment);
        }
        Calendar calendar = Calendar.getInstance();
        if (dueTime <= 0) {
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
        } else {
            calendar.setTimeInMillis(dueTime);
        }

        //默认当天23：59
        DateSelectDialogFragment.newInstance(calendar, taskReminderEntity, null)
                .show(mFragTransaction, tag);
    }

    /**
     * 展示选择负责人对话框
     */
    public void showTaskAllotSelectDialogFragment(String projectId) {
        String tag = TaskAllotSelectDialogFragment.class.getSimpleName();
        FragmentTransaction mFragTransaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);
        if (fragment != null) {
            mFragTransaction.remove(fragment);
        }
        TaskAllotSelectDialogFragment.newInstance(projectId, attendeeUserEntities)
                .show(mFragTransaction, tag);
    }

    /**
     * 选择项目回调
     *
     * @param projectEntity
     * @param taskGroupEntity
     */
    @Override
    public void onProjectTaskGroupSelect(ProjectEntity projectEntity, TaskGroupEntity taskGroupEntity) {
        if (projectEntity != null) {
            projectNameTv.setText(projectEntity.name);
            projectId = projectEntity.pkId;
            taskGroupLayout.setVisibility(View.VISIBLE);
            if (taskGroupEntity != null) {
                taskGroupNameTv.setText(taskGroupEntity.name);
                taskGroupId = taskGroupEntity.id;
            } else {
                taskGroupNameTv.setText("");
                taskGroupNameTv.setHint(getString(R.string.task_select_group));
                taskGroupId = null;
            }
            if (attendeeUserEntities != null) {
                attendeeUserEntities.clear();
                if (usersAdapter != null) {
                    usersAdapter.clearData();
                }
            }
        }
        if (taskGroupEntity != null) {
            taskGroupNameTv.setText(taskGroupEntity.name);
            taskGroupId = taskGroupEntity.id;
        }
    }

    /**
     * 选择到期时间、负责人回调
     *
     * @param fragment
     * @param type
     * @param params
     */
    @Override
    public void onFragmentCallBack(Fragment fragment, int type, Bundle params) {
        if (params != null) {
            if (fragment instanceof DateSelectDialogFragment) {
                dueTime = params.getLong(KEY_FRAGMENT_RESULT);
                setDueTime();
                taskReminderEntity = (TaskReminderEntity) params.getSerializable("taskReminder");

            } else if (fragment instanceof TaskAllotSelectDialogFragment) {
                attendeeUserEntities = (List<TaskEntity.TaskItemEntity.AttendeeUserEntity>) params.getSerializable("list");
                if (attendeeUserEntities != null) {
                    if (attendeeUserEntities.size() > 0) {
                        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
                        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                        layoutManager.setReverseLayout(true);
                        taskOwerRecyclerview.setLayoutManager(layoutManager);
                        taskOwerRecyclerview.setAdapter(usersAdapter = new TaskUsersAdapter(this));
                        usersAdapter.setOnItemClickListener(this);
                        usersAdapter.bindData(false, attendeeUserEntities);
                    }
                }
            }
        }
    }

    /**
     * 设置到期时间
     */
    private void setDueTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(dueTime);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        if ((hour == 23 && minute == 59 && second == 59) || (hour == 0 && minute == 0)) {
            taskDuetimeTv.setText(String.format("%s(%s)", DateUtils.getMMMdd(dueTime), DateUtils.getWeekOfDateFromZ(dueTime)));
        } else {
            taskDuetimeTv.setText(String.format("%s(%s)%s", DateUtils.getMMMdd(dueTime), DateUtils.getWeekOfDateFromZ(dueTime), DateUtils.getHHmm(dueTime)));
        }
    }

    /**
     * 新建任务
     */
    private void createNewTask() {
        String bodyStr = getNewTaskJson();
        if (!TextUtils.isEmpty(bodyStr)) {
            MobclickAgent.onEvent(this, UMMobClickAgent.creat_task_click_id);
            showLoadingDialog(null);
            callEnqueue(
                    getApi().taskCreate(RequestUtils.createJsonBody(getNewTaskJson())),
                    new SimpleCallBack<TaskEntity.TaskItemEntity>() {
                        @Override
                        public void onSuccess(Call<ResEntity<TaskEntity.TaskItemEntity>> call, Response<ResEntity<TaskEntity.TaskItemEntity>> response) {
                            if (response.body().result != null) {
                                showToast(R.string.task_new_succee);

                                //1 清除输入记录
                                SpUtils.getTemporaryCache().remove(KEY_CACHE_TITLE);
                                SpUtils.getTemporaryCache().remove(KEY_CACHE_DESC);

                                //2 体检提醒
                                if (taskReminderEntity != null) {
                                    addReminders(response.body().result, taskReminderEntity);
                                } else {
                                    dismissLoadingDialog();
                                    EventBus.getDefault().post(new TaskActionEvent(TaskActionEvent.TASK_REFRESG_ACTION));
                                    TaskDetailActivity.launchTabSelectCheckItem(TaskCreateActivity.this, response.body().result.id, true);
                                    finish();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<ResEntity<TaskEntity.TaskItemEntity>> call, Throwable t) {
                            super.onFailure(call, t);
                            dismissLoadingDialog();
                        }
                    });
        }
    }

    private String getNewTaskJson() {
        JsonObject jsonObject = new JsonObject();
        String name = taskNameEt.getText().toString().trim();
        if (!TextUtils.isEmpty(name)) {
            if (taskNameEt.getText().length() <= CONTENT_MAX_LENGTH) {
                jsonObject.addProperty("name", taskNameEt.getText().toString());
            } else {
                showTopSnackBar(R.string.task_name_not_exceed_max);
                return null;
            }
        } else {
            showTopSnackBar(R.string.task_input_name);
            return null;
        }
        jsonObject.addProperty("description", taskDescEt.getText().toString());
        if (!TextUtils.isEmpty(projectId)) {
            jsonObject.addProperty("matterId", projectId);
        }
        if (!TextUtils.isEmpty(taskGroupId)) {
            jsonObject.addProperty("parentId", taskGroupId);
        }
        if (dueTime > 0) {
            jsonObject.addProperty("dueTime", dueTime);
        }
        jsonObject.addProperty("state", 0);
        jsonObject.addProperty("type", 0);
        jsonObject.addProperty("valid", 1);

        JsonArray jsonArray = new JsonArray();
        if (attendeeUserEntities != null) {
            if (attendeeUserEntities.size() > 0) {
                for (TaskEntity.TaskItemEntity.AttendeeUserEntity attendeeUserEntity : attendeeUserEntities) {
                    jsonArray.add(attendeeUserEntity.userId);
                }
            } else {
                jsonArray.add(getLoginUserId());
            }
        } else {
            jsonArray.add(getLoginUserId());
        }
        jsonObject.add("attendees", jsonArray);
        return jsonObject.toString();
    }

    /**
     * 添加任务提醒
     *
     * @param taskItemEntity
     * @param taskReminderEntity
     */
    private void addReminders(final TaskEntity.TaskItemEntity taskItemEntity, final TaskReminderEntity taskReminderEntity) {
        if (taskReminderEntity == null) {
            return;
        }
        if (taskItemEntity == null) {
            return;
        }
        String json = getReminderJson(taskReminderEntity);
        if (TextUtils.isEmpty(json)) {
            return;
        }
        callEnqueue(
                getApi().taskReminderAdd(taskItemEntity.id, RequestUtils.createJsonBody(json)),
                new SimpleCallBack<TaskReminderEntity>() {
                    @Override
                    public void onSuccess(Call<ResEntity<TaskReminderEntity>> call, Response<ResEntity<TaskReminderEntity>> response) {
                        dismissLoadingDialog();
                        EventBus.getDefault().post(new TaskActionEvent(TaskActionEvent.TASK_REFRESG_ACTION));
                        TaskDetailActivity.launchTabSelectCheckItem(TaskCreateActivity.this, taskItemEntity.id, true);
                        finish();
                    }

                    @Override
                    public void onFailure(Call<ResEntity<TaskReminderEntity>> call, Throwable t) {
                        super.onFailure(call, t);
                        finish();
                    }
                });
    }

    /**
     * 获取提醒json
     *
     * @param taskReminderEntity
     * @return
     */
    private String getReminderJson(TaskReminderEntity taskReminderEntity) {
        try {
            if (taskReminderEntity == null) {
                return null;
            }
            Gson gson = new Gson();
            return gson.toJson(taskReminderEntity);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onItemClick(BaseRecyclerAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
        if (!TextUtils.isEmpty(projectId)) {
            showTaskAllotSelectDialogFragment(projectId);
        } else {
            showTopSnackBar(R.string.task_please_check_project);
        }
    }
}
