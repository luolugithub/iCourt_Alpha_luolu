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
import android.text.TextUtils;
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
import com.icourt.alpha.base.BaseActivity;
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
import com.icourt.alpha.utils.SystemUtils;
import com.icourt.alpha.utils.UMMobClickAgent;
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
 * Description
 * Company Beijing icourt
 * author  zhaolu  E-mail:zhaolu@icourt.cc
 * date createTime：2017/5/4
 * version 2.0.0
 */
public class TaskCreateActivity extends BaseActivity implements ProjectSelectDialogFragment.OnProjectTaskGroupSelectListener, OnFragmentCallBackListener, BaseRecyclerAdapter.OnItemClickListener {

    String content, startTime;
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
    String projectId, taskGroupId;
    long dueTime;
    TaskUsersAdapter usersAdapter;
    String projectName;
    @BindView(R.id.task_group_tv)
    TextView taskGroupTv;
    @BindView(R.id.task_group_layout)
    LinearLayout taskGroupLayout;

    TaskReminderEntity taskReminderEntity;

    public static void launch(@NonNull Context context, @NonNull String content, String startTime) {
        if (context == null) return;
        Intent intent = new Intent(context, TaskCreateActivity.class);
        intent.putExtra("content", content);
        intent.putExtra("startTime", startTime);
        context.startActivity(intent);
    }

    /**
     * 项目详情创建任务
     *
     * @param context
     * @param projectId
     * @param projectName
     */
    public static void launchFomProject(@NonNull Context context, @NonNull String projectId, @NonNull String projectName) {
        if (context == null) return;
        Intent intent = new Intent(context, TaskCreateActivity.class);
        intent.putExtra("projectId", projectId);
        intent.putExtra("projectName", projectName);
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
        setTitle("新建任务");
        content = getIntent().getStringExtra("content");
        startTime = getIntent().getStringExtra("startTime");
        projectId = getIntent().getStringExtra("projectId");
        projectName = getIntent().getStringExtra("projectName");
        if (!TextUtils.isEmpty(content)) {
            if (content.length() > 200) {
                taskNameEt.setText(content.substring(0, 200));
                taskDescEt.setText(content);
            } else {
                taskNameEt.setText(content);
            }
            taskNameEt.setSelection(taskNameEt.getText().length());
        }
        if (!TextUtils.isEmpty(startTime)) {
            taskDuetimeTv.setText(startTime);
        }
        if (!TextUtils.isEmpty(projectName)) {
            projectNameTv.setText(projectName);
        }
        if (!TextUtils.isEmpty(projectId)) {
            taskGroupLayout.setVisibility(View.VISIBLE);
        }
    }

    @OnClick({R.id.titleAction, R.id.project_layout, R.id.task_group_layout, R.id.duetime_layout, R.id.ower_layout})
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.titleBack:
                SystemUtils.hideSoftKeyBoard(this);
                checkIsSave();
                break;
            case R.id.titleAction://保存
                createNewTask();
                break;
            case R.id.project_layout://选择项目
                showProjectSelectDialogFragment(null);
                break;
            case R.id.task_group_layout://选择任务组
                showProjectSelectDialogFragment(projectId);
                break;
            case R.id.duetime_layout://选择到期时间
                showDateSelectDialogFragment();
                break;
            case R.id.ower_layout://选择负责人
                if (!TextUtils.isEmpty(projectId))
                    showTaskAllotSelectDialogFragment(projectId);
                else
                    showTopSnackBar("请先选择项目");
                break;
            default:
                super.onClick(v);
                break;
        }
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
                taskGroupNameTv.setHint("选择任务组");
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
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(dueTime);
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);
                int second = calendar.get(Calendar.SECOND);
                if ((hour == 23 && minute == 59 && second == 59) || (hour == 0 && minute == 0)) {
                    taskDuetimeTv.setText(DateUtils.getMMMdd(dueTime) + "(" + DateUtils.getWeekOfDateFromZ(dueTime) + ")");
                } else {
                    taskDuetimeTv.setText(DateUtils.getMMMdd(dueTime) + "(" + DateUtils.getWeekOfDateFromZ(dueTime) + ") " + DateUtils.getHHmm(dueTime));
                }
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
     * 新建任务
     */
    private void createNewTask() {

        String bodyStr = getNewTaskJson();
        if (!TextUtils.isEmpty(bodyStr)) {
            MobclickAgent.onEvent(this, UMMobClickAgent.creat_task_click_id);
            showLoadingDialog(null);
            getApi().taskCreate(RequestUtils.createJsonBody(getNewTaskJson())).enqueue(new SimpleCallBack<TaskEntity.TaskItemEntity>() {
                @Override
                public void onSuccess(Call<ResEntity<TaskEntity.TaskItemEntity>> call, Response<ResEntity<TaskEntity.TaskItemEntity>> response) {
                    if (response.body().result != null) {
                        showToast("新建任务成功");
                        if (taskReminderEntity != null) {
                            addReminders(response.body().result, taskReminderEntity);
                        } else {
                            dismissLoadingDialog();
                            EventBus.getDefault().post(new TaskActionEvent(TaskActionEvent.TASK_REFRESG_ACTION));
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
            if (taskNameEt.getText().length() <= 200) {
                jsonObject.addProperty("name", taskNameEt.getText().toString());
            } else {
                showTopSnackBar("任务名称不能超过200个字符");
                return null;
            }
        } else {
            showTopSnackBar("请输入任务名称");
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
    private void addReminders(TaskEntity.TaskItemEntity taskItemEntity, final TaskReminderEntity taskReminderEntity) {
        if (taskReminderEntity == null) return;
        if (taskItemEntity == null) return;
        String json = getReminderJson(taskReminderEntity);
        if (TextUtils.isEmpty(json)) return;
        getApi().taskReminderAdd(taskItemEntity.id, RequestUtils.createJsonBody(json)).enqueue(new SimpleCallBack<TaskReminderEntity>() {
            @Override
            public void onSuccess(Call<ResEntity<TaskReminderEntity>> call, Response<ResEntity<TaskReminderEntity>> response) {
                dismissLoadingDialog();
                EventBus.getDefault().post(new TaskActionEvent(TaskActionEvent.TASK_REFRESG_ACTION));
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
            if (taskReminderEntity == null) return null;
            Gson gson = new Gson();
            return gson.toJson(taskReminderEntity);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onItemClick(BaseRecyclerAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
        if (!TextUtils.isEmpty(projectId))
            showTaskAllotSelectDialogFragment(projectId);
        else
            showTopSnackBar("请优先选择项目");
    }

    private void showSaveDialog(String message) {
        //先new出一个监听器，设置好监听
        DialogInterface.OnClickListener dialogOnclicListener = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                switch (which) {
                    case Dialog.BUTTON_POSITIVE://确定
                        TaskCreateActivity.this.finish();
                        break;
                    case Dialog.BUTTON_NEGATIVE://取消

                        break;
                }
            }
        };
        //dialog参数设置
        AlertDialog.Builder builder = new AlertDialog.Builder(this);  //先得到构造器
        builder.setTitle("提示"); //设置标题
        builder.setMessage(message); //设置内容
        builder.setPositiveButton("确认", dialogOnclicListener);
        builder.setNegativeButton("取消", dialogOnclicListener);
        builder.create().show();
    }

    public void checkIsSave() {
        if (!TextUtils.isEmpty(taskNameEt.getText().toString()) ||
                !TextUtils.isEmpty(projectId) ||
                !TextUtils.isEmpty(taskGroupId) ||
                !TextUtils.isEmpty(taskDescEt.getText().toString()) ||
                !TextUtils.isEmpty(taskDuetimeTv.getText().toString()))
            showSaveDialog(getString(R.string.timer_is_save_timer_text));
        else
            finish();

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            checkIsSave();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
