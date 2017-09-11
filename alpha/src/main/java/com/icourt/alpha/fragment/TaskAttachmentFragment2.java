package com.icourt.alpha.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.adapter.TaskAttachmentAdapter;
import com.icourt.alpha.adapter.baseadapter.HeaderFooterAdapter;
import com.icourt.alpha.base.BaseFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Description  任务下的附件
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/9/11
 * version 2.1.0
 */
public class TaskAttachmentFragment2 extends BaseFragment {
    private static final String KEY_TASK_ID = "key_task_id";
    private static final String KEY_HAS_PERMISSION = "key_has_permission";

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    Unbinder unbinder;
    TaskAttachmentAdapter taskAttachmentAdapter;
    HeaderFooterAdapter<TaskAttachmentAdapter> headerFooterAdapter;

    public static TaskAttachmentFragment2 newInstance(@NonNull String taskId,
                                                      boolean hasPermission) {
        TaskAttachmentFragment2 taskAttachmentFragment = new TaskAttachmentFragment2();
        Bundle bundle = new Bundle();
        bundle.putString(KEY_TASK_ID, taskId);
        bundle.putBoolean(KEY_HAS_PERMISSION, hasPermission);
        taskAttachmentFragment.setArguments(bundle);
        return taskAttachmentFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(R.layout.fragment_task_attachment_layout2, inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void initView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        headerFooterAdapter = new HeaderFooterAdapter<>(taskAttachmentAdapter = new TaskAttachmentAdapter());

        View footerView = HeaderFooterAdapter.inflaterView(getContext(), R.layout.footer_add_attachment, recyclerView);
        TextView attachmentTv = footerView.findViewById(R.id.add_attachment_view);
        if (attachmentTv != null) {
            attachmentTv.setText(R.string.sfile_add_share_member);
        }
        registerClick(attachmentTv);
        headerFooterAdapter.addFooter(footerView);

        recyclerView.setAdapter(headerFooterAdapter);
    }

    @Override
    protected void getData(boolean isRefresh) {
        super.getData(isRefresh);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
