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
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.icourt.alpha.R;
import com.icourt.alpha.base.BaseFragment;
import com.icourt.alpha.entity.bean.ProjectEntity;
import com.icourt.alpha.entity.bean.TaskEntity;
import com.icourt.alpha.entity.bean.TaskGroupEntity;
import com.icourt.alpha.entity.event.TaskActionEvent;
import com.icourt.alpha.fragment.dialogfragment.ProjectSelectDialogFragment;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.utils.DateUtils;
import com.icourt.api.RequestUtils;

import org.greenrobot.eventbus.EventBus;

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

public class TaskDetailFragment extends BaseFragment implements ProjectSelectDialogFragment.OnProjectTaskGroupSelectListener {
    private static final String KEY_TASK_DETAIL = "key_task_detail";
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
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
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
                taskProjectLayout.setVisibility(View.GONE);
                taskGroupLayout.setVisibility(View.GONE);
            }
            if (!TextUtils.isEmpty(taskItemEntity.description)) {
                taskDescLayout.setVisibility(View.VISIBLE);
                taskDescTv.setText(taskItemEntity.description);
            } else {
                taskDescLayout.setVisibility(View.GONE);
            }
        }
    }

    @OnClick({R.id.task_project_layout})
    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.task_project_layout://选择项目
                showProjectSelectDialogFragment();
                break;
        }
    }

    /**
     * 展示选择项目对话框
     */
    public void showProjectSelectDialogFragment() {
        String tag = "ProjectSelectDialogFragment";
        FragmentTransaction mFragTransaction = getChildFragmentManager().beginTransaction();
        Fragment fragment = getChildFragmentManager().findFragmentByTag(tag);
        if (fragment != null) {
            mFragTransaction.remove(fragment);
        }

        ProjectSelectDialogFragment.newInstance()
                .show(mFragTransaction, tag);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onProjectTaskGroupSelect(ProjectEntity projectEntity, TaskGroupEntity taskGroupEntity) {

        updateTask(taskItemEntity, projectEntity, taskGroupEntity);
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
                EventBus.getDefault().post(new TaskActionEvent(TaskActionEvent.TASK_REFRESG_ACTION));
                if (projectEntity != null) {
                    taskProjectTv.setText(projectEntity.name);
                }
                if (taskGroupEntity != null) {
                    taskGroupTv.setText(taskGroupEntity.name);
                }
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
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("id", itemEntity.id);
            jsonObject.addProperty("state", itemEntity.state);
            jsonObject.addProperty("valid", true);
            jsonObject.addProperty("updateTime", DateUtils.millis());
            if (projectEntity != null) {
                jsonObject.addProperty("matterId", projectEntity.pkId);
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
}
