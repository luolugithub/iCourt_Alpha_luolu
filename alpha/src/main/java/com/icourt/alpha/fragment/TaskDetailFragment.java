package com.icourt.alpha.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.base.BaseFragment;
import com.icourt.alpha.entity.bean.TaskEntity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Description  任务详情fragment
 * Company Beijing icourt
 * author  lu.zhao  E-mail:zhaolu@icourt.cc
 * date createTime：17/5/11
 * version 2.0.0
 */

public class TaskDetailFragment extends BaseFragment {
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
                taskGroupTv.setText(taskItemEntity.parentName);
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
