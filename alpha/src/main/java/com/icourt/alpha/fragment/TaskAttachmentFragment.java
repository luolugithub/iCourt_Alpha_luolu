package com.icourt.alpha.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.icourt.alpha.R;
import com.icourt.alpha.base.BaseFragment;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Description  任务附件列表
 * Company Beijing icourt
 * author  lu.zhao  E-mail:zhaolu@icourt.cc
 * date createTime：17/5/12
 * version 2.0.0
 */

public class TaskAttachmentFragment extends BaseFragment {
    private static final String KEY_TASK_ID = "key_task_id";
    Unbinder unbinder;

    public static TaskAttachmentFragment newInstance(@NonNull String taskId) {
        TaskAttachmentFragment taskAttachmentFragment = new TaskAttachmentFragment();
        Bundle bundle = new Bundle();
        bundle.putString(KEY_TASK_ID, taskId);
        taskAttachmentFragment.setArguments(bundle);
        return taskAttachmentFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(R.layout.fragment_task_check_item_layout, inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void initView() {

    }
}
