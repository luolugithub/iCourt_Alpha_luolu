package com.icourt.alpha.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.icourt.alpha.R;
import com.icourt.alpha.activity.ProjectDetailActivity;
import com.icourt.alpha.activity.TaskDescChangeActivity;
import com.icourt.alpha.activity.TaskDetailActivity;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.base.BaseFragment;
import com.icourt.alpha.entity.bean.ProjectEntity;
import com.icourt.alpha.entity.bean.TaskEntity;
import com.icourt.alpha.entity.bean.TaskGroupEntity;
import com.icourt.alpha.entity.bean.TaskReminderEntity;
import com.icourt.alpha.entity.event.TaskActionEvent;
import com.icourt.alpha.fragment.dialogfragment.DateSelectDialogFragment;
import com.icourt.alpha.fragment.dialogfragment.ProjectSelectDialogFragment;
import com.icourt.alpha.fragment.dialogfragment.TaskGroupSelectFragment;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.interfaces.OnFragmentCallBackListener;
import com.icourt.alpha.utils.BeanUtils;
import com.icourt.alpha.utils.DateUtils;
import com.icourt.alpha.widget.dialog.BottomActionDialog;
import com.icourt.api.RequestUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.net.URLDecoder;
import java.util.Arrays;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Description  任务详情fragment
 * Company Beijing icourt
 * author  lu.zhao  E-mail:zhaolu@icourt.cc
 * date createTime：17/5/11
 * version 2.0.0
 */

public class TaskDetailFragment extends BaseFragment implements ProjectSelectDialogFragment.OnProjectTaskGroupSelectListener, OnFragmentCallBackListener {
    private static final String KEY_TASK_DETAIL = "key_task_detail";
    //修改任务详情requestcode
    private static final int UPDATE_DEST_REQUEST_CODE = 1;

    private static final int TIME_HOUR_23 = 23;
    private static final int TIME_MIN_59 = 59;

    Unbinder unbinder;
    @BindView(R.id.task_project_tv)
    TextView taskProjectTv;
    @BindView(R.id.task_project_layout)
    LinearLayout taskProjectLayout;
    @BindView(R.id.task_group_tv)
    TextView taskGroupTv;
    @BindView(R.id.task_group_layout)
    LinearLayout taskGroupLayout;
    @BindView(R.id.task_time_tv)
    TextView taskTimeTv;
    @BindView(R.id.task_time_layout)
    LinearLayout taskTimeLayout;
    @BindView(R.id.task_desc_tv)
    TextView taskDescTv;

    TaskEntity.TaskItemEntity taskItemEntity, cloneItemEntity;
    @BindView(R.id.task_desc_layout)
    LinearLayout taskDescLayout;
    TaskReminderEntity taskReminderEntity;
    @BindView(R.id.task_reminder_icon)
    ImageView taskReminderIcon;
    /**
     * 是否完成
     */
    boolean isFinish;
    /**
     * 是否有效   true：未删除   fale：已删除
     */
    boolean valid;
    @BindView(R.id.task_project_arrow_iv)
    ImageView taskProjectArrowIv;
    @BindView(R.id.task_group_arrow_iv)
    ImageView taskGroupArrowIv;
    @BindView(R.id.task_time_arrow_iv)
    ImageView taskTimeArrowIv;

    public static TaskDetailFragment newInstance(@NonNull TaskEntity.TaskItemEntity taskItemEntity) {
        TaskDetailFragment taskDetailFragment = new TaskDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_TASK_DETAIL, taskItemEntity);
        taskDetailFragment.setArguments(bundle);
        return taskDetailFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(R.layout.fragment_task_detail_layout, inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void initView() {
        EventBus.getDefault().register(this);
        taskItemEntity = (TaskEntity.TaskItemEntity) getArguments().getSerializable(KEY_TASK_DETAIL);
        if (taskItemEntity != null) {
            try {
                cloneItemEntity = (TaskEntity.TaskItemEntity) BeanUtils.cloneTo(taskItemEntity);
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
            setDataToView(taskItemEntity);
            //获取任务提醒数据
            getTaskReminder(taskItemEntity.id);
        }
    }

    private void setDataToView(TaskEntity.TaskItemEntity taskItemEntity) {
        if (taskItemEntity == null) {
            return;
        }
        if (taskProjectLayout == null) {
            return;
        }
        isFinish = taskItemEntity.state;
        valid = taskItemEntity.valid;
        if (taskItemEntity.matter != null) {
            taskProjectLayout.setVisibility(View.VISIBLE);
            taskGroupLayout.setVisibility(View.VISIBLE);
            taskProjectTv.setText(taskItemEntity.matter.name);
            if (taskItemEntity.parentFlow != null) {
                taskGroupTv.setText(taskItemEntity.parentFlow.name);
            } else {
                taskGroupTv.setText("");
                taskGroupTv.setHint((valid && !isFinish) ? getString(R.string.task_select_group) : getString(R.string.task_not_set_group));
            }
        } else {
            taskProjectLayout.setVisibility(View.VISIBLE);
            taskGroupLayout.setVisibility(View.GONE);
            taskProjectTv.setHint((valid && !isFinish) ? getString(R.string.task_select_project) : getString(R.string.task_not_set_project));
        }

        if (taskItemEntity.dueTime > 0) {
            taskTimeTv.setHint(DateUtils.get23Hour59Min(taskItemEntity.dueTime));
        } else {
            taskTimeTv.setHint((valid && !isFinish) ? getString(R.string.task_select_duetime) : getString(R.string.task_not_set_duetime));
        }

        if (!TextUtils.isEmpty(taskItemEntity.description)) {
            try {
                taskDescTv.setText(URLDecoder.decode(taskItemEntity.description, "utf-8"));
            } catch (Exception e) {
                taskDescTv.setText(taskItemEntity.description);
                e.printStackTrace();
                bugSync("任务详情转码失败", taskItemEntity.description);
            }
        } else {
            taskDescTv.setHint((valid && !isFinish) ? getString(R.string.task_add_desc) : getString(R.string.task_not_set_desc));
        }
        taskProjectArrowIv.setVisibility((valid && !isFinish) ? View.VISIBLE : View.GONE);
        taskGroupArrowIv.setVisibility((valid && !isFinish) ? View.VISIBLE : View.GONE);
        taskTimeArrowIv.setVisibility((valid && !isFinish) ? View.VISIBLE : View.GONE);
        boolean hasReminder = taskReminderEntity != null && (taskReminderEntity.ruleTime != null || taskReminderEntity.customTime != null);
        if (hasReminder) {
            taskReminderIcon.setVisibility(!isFinish ? View.VISIBLE : View.GONE);
        }
    }

    @OnClick({R.id.task_project_layout,
            R.id.task_group_layout,
            R.id.task_time_layout,
            R.id.task_desc_tv})
    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (!isFinish && valid) {
            if (hasTaskEditPermission()) {
                switch (v.getId()) {
                    //选择项目
                    case R.id.task_project_layout:
                        if (taskItemEntity != null) {
                            if (taskItemEntity.matter == null) {
                                showProjectSelectDialogFragment(null);
                            } else {
                                showBottomMenu();
                            }
                        }
                        break;
                    //选择任务组
                    case R.id.task_group_layout:
                        if (taskItemEntity.matter != null) {
                            if (!TextUtils.isEmpty(taskItemEntity.matter.id)) {
                                showProjectSelectDialogFragment(taskItemEntity.matter.id);
                            }
                        }
                        break;
                    //选择到期时间
                    case R.id.task_time_layout:
                        if (taskItemEntity != null) {
                            showDateSelectDialogFragment(taskItemEntity.dueTime, taskItemEntity.id);
                        }
                        break;
                    //添加任务详情
                    case R.id.task_desc_tv:
                        TaskDescChangeActivity.launch(
                                getContext(),
                                taskItemEntity);
                        break;
                    default:

                        break;
                }
            } else {
                showTopSnackBar(getString(R.string.task_not_permission_edit_task));
            }
        }
    }

    @Override
    public void notifyFragmentUpdate(Fragment targetFrgament, int type, Bundle bundle) {
        if (targetFrgament instanceof TaskDetailFragment) {
            if (bundle == null) {
                return;
            }
            isFinish = bundle.getBoolean(TaskDetailActivity.KEY_ISFINISH);
            valid = bundle.getBoolean(TaskDetailActivity.KEY_VALID);
            taskItemEntity = (TaskEntity.TaskItemEntity) bundle.getSerializable("taskItemEntity");
            try {
                cloneItemEntity = (TaskEntity.TaskItemEntity) BeanUtils.cloneTo(taskItemEntity);
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
            setDataToView(taskItemEntity);
        }
    }

    /**
     * 查询任务提醒
     *
     * @param taskId
     */
    private void getTaskReminder(String taskId) {
        callEnqueue(
                getApi().taskReminderQuery(taskId),
                new SimpleCallBack<TaskReminderEntity>() {
                    @Override
                    public void onSuccess(Call<ResEntity<TaskReminderEntity>> call, Response<ResEntity<TaskReminderEntity>> response) {
                        taskReminderEntity = response.body().result;
                        if (taskReminderIcon != null) {
                            if (taskReminderEntity != null) {
                                if (taskReminderEntity.ruleTime != null || taskReminderEntity.customTime != null) {
                                    taskReminderIcon.setVisibility(isFinish ? View.GONE : View.VISIBLE);
                                } else {
                                    taskReminderIcon.setVisibility(View.INVISIBLE);
                                }
                            } else {
                                taskReminderIcon.setVisibility(View.INVISIBLE);
                            }
                        }
                    }
                }
        );
    }

    /**
     * 是否有任务编辑权限
     */
    private boolean hasTaskEditPermission() {
        if (taskItemEntity != null && taskItemEntity.right != null) {
            return taskItemEntity.right.contains("MAT:matter.task:edit");
        }
        return false;
    }

    /**
     * 显示底部菜单
     */
    private void showBottomMenu() {
        new BottomActionDialog(getContext(),
                null,
                Arrays.asList(getString(R.string.task_sheet_select_project), getString(R.string.task_sheet_look_project)),
                new BottomActionDialog.OnActionItemClickListener() {
                    @Override
                    public void onItemClick(BottomActionDialog dialog, BottomActionDialog.ActionItemAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
                        dialog.dismiss();
                        switch (position) {
                            case 0:
                                showProjectSelectDialogFragment(null);
                                break;
                            case 1:
                                if (taskItemEntity != null && taskItemEntity.matter != null) {
                                    ProjectDetailActivity.launch(getContext(), taskItemEntity.matter.id, taskItemEntity.matter.name);
                                }
                                break;
                            default:

                                break;
                        }
                    }
                }).show();
    }

    /**
     * 展示选择项目对话框
     */
    public void showProjectSelectDialogFragment(String projectId) {
        String tag = ProjectSelectDialogFragment.class.getSimpleName();
        FragmentTransaction mFragTransaction = getChildFragmentManager().beginTransaction();
        Fragment fragment = getChildFragmentManager().findFragmentByTag(tag);
        if (fragment != null) {
            mFragTransaction.remove(fragment);
        }

        ProjectSelectDialogFragment.newInstance(projectId)
                .show(mFragTransaction, tag);
    }

    /**
     * 展示选择到期时间对话框
     */
    private void showDateSelectDialogFragment(long dueTime, String taskId) {
        String tag = DateSelectDialogFragment.class.getSimpleName();
        FragmentTransaction mFragTransaction = getChildFragmentManager().beginTransaction();
        Fragment fragment = getChildFragmentManager().findFragmentByTag(tag);
        if (fragment != null) {
            mFragTransaction.remove(fragment);
        }
        if (dueTime > 0) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(dueTime);
            DateSelectDialogFragment.newInstance(calendar, taskReminderEntity, taskId)
                    .show(mFragTransaction, tag);
        } else {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
            DateSelectDialogFragment.newInstance(calendar, taskReminderEntity, taskId)
                    .show(mFragTransaction, tag);
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }
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
            if (taskItemEntity != null) {
                if (taskItemEntity.attendeeUsers != null) {
                    taskItemEntity.attendeeUsers.clear();
                }
            }
        }
        if (taskGroupEntity == null) {
            taskGroupEntity = new TaskGroupEntity();
            taskGroupEntity.id = "";
        }
        selectedTaskGroup = taskGroupEntity;
        updateTask(taskItemEntity, projectEntity, taskGroupEntity);
    }

    /**
     * 修改任务
     *
     * @param itemEntity
     * @param projectEntity
     * @param taskGroupEntity
     */
    private void updateTask(final TaskEntity.TaskItemEntity itemEntity, final ProjectEntity projectEntity, final TaskGroupEntity taskGroupEntity) {
        showLoadingDialog(null);
        callEnqueue(
                getApi().taskUpdateNew(RequestUtils.createJsonBody(getTaskJson(itemEntity, projectEntity, taskGroupEntity))),
                new SimpleCallBack<TaskEntity.TaskItemEntity>() {
                    @Override
                    public void onSuccess(Call<ResEntity<TaskEntity.TaskItemEntity>> call, Response<ResEntity<TaskEntity.TaskItemEntity>> response) {
                        dismissLoadingDialog();
                        if (taskProjectTv != null) {
                            taskItemEntity = response.body().result;
                            try {
                                cloneItemEntity = (TaskEntity.TaskItemEntity) BeanUtils.cloneTo(taskItemEntity);
                            } catch (RuntimeException e) {
                                e.printStackTrace();
                            }
                            setDataToView(taskItemEntity);
                            addReminders(taskReminderEntity);
                            EventBus.getDefault().post(new TaskActionEvent(TaskActionEvent.TASK_REFRESG_ACTION, itemEntity.id, ""));
                        }
                    }

                    @Override
                    public void onFailure(Call<ResEntity<TaskEntity.TaskItemEntity>> call, Throwable t) {
                        super.onFailure(call, t);
                        dismissLoadingDialog();
                        setDataToView(cloneItemEntity);
                    }

                    @Override
                    public void defNotify(String noticeStr) {
                        showTopSnackBar(noticeStr);
                    }
                }
        );
    }


    /**
     * 获取任务json
     *
     * @param itemEntity
     * @return
     */
    private String getTaskJson(TaskEntity.TaskItemEntity itemEntity, ProjectEntity projectEntity, TaskGroupEntity taskGroupEntity) {
        try {
            if (itemEntity == null) {
                return null;
            }
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("id", itemEntity.id);
            jsonObject.addProperty("state", itemEntity.state);
            jsonObject.addProperty("name", itemEntity.name);
            jsonObject.addProperty("parentId", itemEntity.parentId);
            jsonObject.addProperty("dueTime", itemEntity.dueTime);
            jsonObject.addProperty("description", itemEntity.description);
            jsonObject.addProperty("valid", true);
            jsonObject.addProperty("updateTime", DateUtils.millis());
            JsonArray jsonarr = new JsonArray();
            if (projectEntity != null) {
                jsonObject.addProperty("matterId", projectEntity.pkId);
            }
            if (itemEntity.attendeeUsers != null) {
                if (itemEntity.attendeeUsers.size() > 0) {
                    for (TaskEntity.TaskItemEntity.AttendeeUserEntity attendeeUser : itemEntity.attendeeUsers) {
                        jsonarr.add(attendeeUser.userId);
                    }
                    jsonObject.add("attendees", jsonarr);
                }
            }
            if (taskGroupEntity != null) {
                jsonObject.addProperty("parentId", taskGroupEntity.id);
            }

            return jsonObject.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 添加任务提醒
     *
     * @param taskReminderEntity
     */
    private void addReminders(final TaskReminderEntity taskReminderEntity) {
        if (taskReminderEntity == null) {
            return;
        }
        if (taskItemEntity == null) {
            return;
        }
        if (TextUtils.isEmpty(taskReminderEntity.taskReminderType)) {
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
                        if (taskReminderIcon != null) {
                            boolean hasReminder = (taskReminderEntity.ruleTime != null && taskReminderEntity.ruleTime.size() > 0) ||
                                    (taskReminderEntity.customTime != null && taskReminderEntity.customTime.size() > 0);
                            if (hasReminder) {
                                taskReminderIcon.setVisibility(View.VISIBLE);
                            } else {
                                taskReminderIcon.setVisibility(View.INVISIBLE);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResEntity<TaskReminderEntity>> call, Throwable t) {
                        super.onFailure(call, t);
                        if (taskReminderIcon != null) {
                            taskReminderIcon.setVisibility(View.INVISIBLE);
                        }
                    }
                }
        );
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

    TaskGroupEntity selectedTaskGroup;

    @Override
    public void onFragmentCallBack(Fragment fragment, int type, Bundle params) {
        if (params != null) {
            //选择到期时间回调
            if (fragment instanceof DateSelectDialogFragment) {
                long millis = params.getLong(KEY_FRAGMENT_RESULT);
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(millis);
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);
                int second = calendar.get(Calendar.SECOND);
                if (hour == TIME_HOUR_23 && minute == TIME_MIN_59 && second == TIME_MIN_59) {
                    taskTimeTv.setText(DateUtils.getTimeDate(millis));
                } else {
                    taskTimeTv.setText(DateUtils.getTimeDateFormatMm(millis));
                }

                taskItemEntity.dueTime = millis;
                taskReminderEntity = (TaskReminderEntity) params.getSerializable("taskReminder");
                updateTask(taskItemEntity, null, null);
            } //选择任务组回调
            else if (fragment instanceof TaskGroupSelectFragment) {
                TaskGroupEntity taskGroupEntity = (TaskGroupEntity) params.getSerializable(KEY_FRAGMENT_RESULT);
                if (taskGroupEntity != null) {
                    selectedTaskGroup = taskGroupEntity;
                    taskGroupTv.setText(taskGroupEntity.name);
                    taskItemEntity.parentId = taskGroupEntity.id;
                    updateTask(taskItemEntity, null, taskGroupEntity);
                }
            }
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateTaskDescEvent(TaskActionEvent event) {
        if (event == null) {
            return;
        }
        //修改任务描述
        if (event.action == TaskActionEvent.TASK_UPDATE_DESC_ACTION) {
            if (getActivity() instanceof TaskDetailActivity) {
                taskItemEntity = ((TaskDetailActivity) getActivity()).getTaskItemEntity();
            }
            taskItemEntity.description = event.desc;
            updateTask(taskItemEntity, null, null);
        }
    }
}
