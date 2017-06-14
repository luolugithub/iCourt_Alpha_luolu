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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.icourt.alpha.R;
import com.icourt.alpha.activity.ProjectDetailActivity;
import com.icourt.alpha.activity.TaskDescUpdateActivity;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.base.BaseFragment;
import com.icourt.alpha.entity.bean.ProjectEntity;
import com.icourt.alpha.entity.bean.TaskEntity;
import com.icourt.alpha.entity.bean.TaskGroupEntity;
import com.icourt.alpha.entity.event.TaskActionEvent;
import com.icourt.alpha.fragment.dialogfragment.DateSelectDialogFragment;
import com.icourt.alpha.fragment.dialogfragment.ProjectSelectDialogFragment;
import com.icourt.alpha.fragment.dialogfragment.TaskGroupSelectFragment;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.interfaces.OnFragmentCallBackListener;
import com.icourt.alpha.utils.DateUtils;
import com.icourt.alpha.widget.dialog.BottomActionDialog;
import com.icourt.api.RequestUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

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
    private static final int UPDATE_DEST_REQUEST_CODE = 1;//修改任务详情requestcode
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

    TaskEntity.TaskItemEntity taskItemEntity;
    @BindView(R.id.task_desc_layout)
    LinearLayout taskDescLayout;

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
            if (taskItemEntity.matter != null) {
                taskProjectLayout.setVisibility(View.VISIBLE);
                taskGroupLayout.setVisibility(View.VISIBLE);
                taskProjectTv.setText(taskItemEntity.matter.name);
                if (taskItemEntity.parentFlow != null) {
                    taskGroupTv.setText(taskItemEntity.parentFlow.name);
                }
            } else {
                taskProjectLayout.setVisibility(View.VISIBLE);
                taskGroupLayout.setVisibility(View.GONE);
                taskProjectTv.setText("选择所属项目");
            }

            if (taskItemEntity.dueTime > 0) {
                taskTimeTv.setText(DateUtils.get23Hour59Min(taskItemEntity.dueTime));
            } else {
                taskTimeTv.setText("选择到期时间");
            }

            if (!TextUtils.isEmpty(taskItemEntity.description)) {
                taskDescTv.setText(taskItemEntity.description);
            }
        }
    }

    @OnClick({R.id.task_project_layout, R.id.task_group_layout, R.id.task_time_layout, R.id.task_desc_tv})
    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (hasTaskEditPermission()) {
            switch (v.getId()) {
                case R.id.task_project_layout://选择项目
                    if (taskItemEntity != null) {
                        if (taskItemEntity.matter == null) {
                            showProjectSelectDialogFragment(null);
                        } else {
                            showBottomMeau();
                        }
                    }
                    break;
                case R.id.task_group_layout://选择任务组
                    if (taskItemEntity.matter != null) {
                        if (!TextUtils.isEmpty(taskItemEntity.matter.id)) {
                            showProjectSelectDialogFragment(taskItemEntity.matter.id);
                        }
                    }
                    break;
                case R.id.task_time_layout://选择到期时间
                    showDateSelectDialogFragment(taskItemEntity.dueTime);
                    break;
                case R.id.task_desc_tv://添加任务详情
                    TaskDescUpdateActivity.launch(getContext(), taskDescTv.getText().toString(), TaskDescUpdateActivity.UPDATE_TASK_DESC);
                    break;
            }
        } else {
            showTopSnackBar("您没有编辑任务的权限");
        }
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
                                showProjectSelectDialogFragment(null);
                                break;
                            case 1:
                                if (taskItemEntity != null)
                                    if (taskItemEntity.matter != null)
                                        ProjectDetailActivity.launch(getContext(), taskItemEntity.matter.id, taskItemEntity.matter.name);
                                break;

                        }
                    }
                }).show();
    }

    /**
     * 展示选择项目对话框
     */
    public void showProjectSelectDialogFragment(String projectId) {
        String tag = "ProjectSelectDialogFragment";
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
    private void showDateSelectDialogFragment(long dueTime) {
        String tag = DateSelectDialogFragment.class.getSimpleName();
        FragmentTransaction mFragTransaction = getChildFragmentManager().beginTransaction();
        Fragment fragment = getChildFragmentManager().findFragmentByTag(tag);
        if (fragment != null) {
            mFragTransaction.remove(fragment);
        }
        Calendar calendar = Calendar.getInstance();
        if (dueTime <= 0) {
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
        } else {
            calendar.setTimeInMillis(dueTime);
        }
        DateSelectDialogFragment.newInstance(calendar)
                .show(mFragTransaction, tag);
    }

    /**
     * 展示选择任务组对话框
     */
    private void showTaskGroupSelectFragment(String projectId) {
        String tag = TaskGroupSelectFragment.class.getSimpleName();
        FragmentTransaction mFragTransaction = getChildFragmentManager().beginTransaction();
        Fragment fragment = getChildFragmentManager().findFragmentByTag(tag);
        if (fragment != null) {
            mFragTransaction.remove(fragment);
        }
        TaskGroupSelectFragment.newInstance(projectId)
                .show(mFragTransaction, tag);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
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
                updateTask(taskItemEntity, projectEntity, taskGroupEntity);
            }
        }
    }

    /**
     * 修改任务
     *
     * @param itemEntity
     * @param projectEntity
     * @param taskGroupEntity
     */
    private void updateTask(TaskEntity.TaskItemEntity itemEntity, final ProjectEntity projectEntity, final TaskGroupEntity taskGroupEntity) {
        showLoadingDialog(null);
        getApi().taskUpdate(RequestUtils.createJsonBody(getTaskJson(itemEntity, projectEntity, taskGroupEntity))).enqueue(new SimpleCallBack<JsonElement>() {
            @Override
            public void onSuccess(Call<ResEntity<JsonElement>> call, Response<ResEntity<JsonElement>> response) {
                dismissLoadingDialog();

                if (projectEntity != null) {
                    taskProjectTv.setText(projectEntity.name);
                    taskGroupLayout.setVisibility(View.VISIBLE);
                    EventBus.getDefault().post(new TaskActionEvent(TaskActionEvent.TASK_UPDATE_PROJECT_ACTION, projectEntity.pkId));
                    if (taskItemEntity != null) {
                        if (taskItemEntity.matter != null) {
                            taskItemEntity.matter.id = projectEntity.pkId;
                            taskItemEntity.matter.name = projectEntity.name;
                        } else {
                            TaskEntity.TaskItemEntity.MatterEntity matterEntity = new TaskEntity.TaskItemEntity.MatterEntity();
                            matterEntity.id = projectEntity.pkId;
                            matterEntity.name = projectEntity.name;
                            taskItemEntity.matter = matterEntity;
                        }
                    }
                    if (taskGroupEntity != null) {
                        taskGroupTv.setText(taskGroupEntity.name);
                    } else {
                        taskGroupTv.setText("");
                    }
                } else {
                    if (taskGroupEntity != null) {
                        taskGroupTv.setText(taskGroupEntity.name);
                    } else {
                        taskGroupTv.setText(taskItemEntity != null ? taskItemEntity.parentFlow != null ? taskItemEntity.parentFlow.name : "" : "");
                    }
                }

                EventBus.getDefault().post(new TaskActionEvent(TaskActionEvent.TASK_REFRESG_ACTION));
            }

            @Override
            public void onFailure(Call<ResEntity<JsonElement>> call, Throwable t) {
                super.onFailure(call, t);
                dismissLoadingDialog();
            }
        });
    }

    /**
     * 获取任务json
     *
     * @param itemEntity
     * @return
     */
    private String getTaskJson(TaskEntity.TaskItemEntity itemEntity, ProjectEntity projectEntity, TaskGroupEntity taskGroupEntity) {
        try {
            if (itemEntity == null) return null;
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("id", itemEntity.id);
            jsonObject.addProperty("state", itemEntity.state);
            jsonObject.addProperty("dueTime", itemEntity.dueTime);
            jsonObject.addProperty("description", itemEntity.description);
            jsonObject.addProperty("valid", true);
            jsonObject.addProperty("updateTime", DateUtils.millis());
            if (projectEntity != null) {
                jsonObject.addProperty("matterId", projectEntity.pkId);
            }
            if (taskGroupEntity != null) {
                jsonObject.addProperty("parentId", taskGroupEntity.id);
            } else {
                jsonObject.addProperty("parentId", "");
            }
            JsonArray jsonarr = new JsonArray();
            if (itemEntity.attendeeUsers != null) {
                if (itemEntity.attendeeUsers.size() > 0) {
                    for (TaskEntity.TaskItemEntity.AttendeeUserEntity attendeeUser : itemEntity.attendeeUsers) {
                        jsonarr.add(attendeeUser.userId);
                    }
                } else {
                    jsonarr.add(getLoginUserId());
                }
            }
            jsonObject.add("attendees", jsonarr);
            return jsonObject.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onFragmentCallBack(Fragment fragment, int type, Bundle params) {
        if (params != null) {
            if (fragment instanceof DateSelectDialogFragment) {//选择到期时间回调
                long millis = params.getLong(KEY_FRAGMENT_RESULT);
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(millis);
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);
                int second = calendar.get(Calendar.SECOND);
                if (hour == 23 && minute == 59 && second == 59) {
                    taskTimeTv.setText(DateUtils.getTimeDate(millis));
                } else {
                    taskTimeTv.setText(DateUtils.getTimeDateFormatMm(millis));
                }

                taskItemEntity.dueTime = millis;
                updateTask(taskItemEntity, null, null);
            } else if (fragment instanceof TaskGroupSelectFragment) {//选择任务组回调
                TaskGroupEntity taskGroupEntity = (TaskGroupEntity) params.getSerializable(KEY_FRAGMENT_RESULT);
                if (taskGroupEntity != null) {
                    taskGroupTv.setText(taskGroupEntity.name);
                    taskItemEntity.parentId = taskGroupEntity.id;
                    updateTask(taskItemEntity, null, taskGroupEntity);
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateTaskDescEvent(TaskActionEvent event) {
        if (event == null) return;
        if (event.action == TaskActionEvent.TASK_UPDATE_DESC_ACTION) {//修改任务描述
            taskDescTv.setText(event.desc);
            taskItemEntity.description = event.desc;
            updateTask(taskItemEntity, null, null);
        }
    }
}
