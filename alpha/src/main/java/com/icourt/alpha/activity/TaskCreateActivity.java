package com.icourt.alpha.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.icourt.alpha.R;
import com.icourt.alpha.adapter.TaskUsersAdapter;
import com.icourt.alpha.base.BaseActivity;
import com.icourt.alpha.entity.bean.ProjectEntity;
import com.icourt.alpha.entity.bean.TaskEntity;
import com.icourt.alpha.entity.bean.TaskGroupEntity;
import com.icourt.alpha.entity.event.TaskActionEvent;
import com.icourt.alpha.fragment.dialogfragment.DateSelectDialogFragment;
import com.icourt.alpha.fragment.dialogfragment.ProjectSelectDialogFragment;
import com.icourt.alpha.fragment.dialogfragment.TaskAllotSelectDialogFragment;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.interfaces.OnFragmentCallBackListener;
import com.icourt.alpha.utils.DateUtils;
import com.icourt.api.RequestUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Response;

import static com.icourt.alpha.fragment.dialogfragment.BaseDialogFragment.KEY_FRAGMENT_RESULT;

/**
 * Description
 * Company Beijing icourt
 * author  zhaolu  E-mail:zhaolu@icourt.cc
 * date createTime：2017/5/4
 * version 2.0.0
 */
public class TaskCreateActivity extends BaseActivity implements ProjectSelectDialogFragment.OnProjectTaskGroupSelectListener, OnFragmentCallBackListener {

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

    List<TaskEntity.TaskItemEntity.AttendeeUserEntity> attendeeUserEntities;
    String projectId, taskGroupId;
    long dueTime;
    TaskUsersAdapter usersAdapter;
    String projectName;

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
            taskDescEt.setText(content);
        }
        if (!TextUtils.isEmpty(startTime)) {
            taskDuetimeTv.setText(startTime);
        }
        if (!TextUtils.isEmpty(projectName)) {
            projectNameTv.setText(projectName);
        }
    }

    @OnClick({R.id.titleAction, R.id.project_layout, R.id.duetime_layout, R.id.ower_layout})
    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.titleAction://保存
                createNewTask();
                break;
            case R.id.project_layout://选择项目
                showProjectSelectDialogFragment();
                break;
            case R.id.duetime_layout://选择到期时间
                showDateSelectDialogFragment();
                break;
            case R.id.ower_layout://选择负责人
                if (!TextUtils.isEmpty(projectId))
                    showTaskAllotSelectDialogFragment(projectId);
                else
                    showTopSnackBar("请优先选择项目");
                break;

        }
    }

    /**
     * 展示选择项目对话框
     */
    public void showProjectSelectDialogFragment() {
        String tag = ProjectSelectDialogFragment.class.getSimpleName();
        FragmentTransaction mFragTransaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);
        if (fragment != null) {
            mFragTransaction.remove(fragment);
        }
        ProjectSelectDialogFragment.newInstance()
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
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        //默认当天23：59
        DateSelectDialogFragment.newInstance(calendar)
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
                taskDuetimeTv.setText(DateUtils.getTimeDateFormatMm(dueTime));
            } else if (fragment instanceof TaskAllotSelectDialogFragment) {
                attendeeUserEntities = (List<TaskEntity.TaskItemEntity.AttendeeUserEntity>) params.getSerializable("list");
                if (attendeeUserEntities != null) {
                    if (attendeeUserEntities.size() > 0) {
                        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
                        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                        layoutManager.setReverseLayout(true);
                        taskOwerRecyclerview.setLayoutManager(layoutManager);
                        taskOwerRecyclerview.setAdapter(usersAdapter = new TaskUsersAdapter());
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
            showLoadingDialog(null);
            getApi().taskCreate(RequestUtils.createJsonBody(getNewTaskJson())).enqueue(new SimpleCallBack<JsonElement>() {
                @Override
                public void onSuccess(Call<ResEntity<JsonElement>> call, Response<ResEntity<JsonElement>> response) {
                    dismissLoadingDialog();
                    EventBus.getDefault().post(new TaskActionEvent(TaskActionEvent.TASK_REFRESG_ACTION));
                    finish();
                }
            });
        }
    }

    private String getNewTaskJson() {
        JsonObject jsonObject = new JsonObject();
        if (!TextUtils.isEmpty(taskNameEt.getText().toString())) {
            jsonObject.addProperty("name", taskNameEt.getText().toString());
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
            }
        } else {
            jsonArray.add(getLoginUserId());
        }
        jsonObject.add("attendees", jsonArray);
        return jsonObject.toString();
    }
}
