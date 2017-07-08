package com.icourt.alpha.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.adapter.ReminderListAdapter;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.base.BaseFragment;
import com.icourt.alpha.entity.bean.ReminderItemEntity;
import com.icourt.alpha.entity.bean.TaskReminderEntity;
import com.icourt.alpha.fragment.dialogfragment.DateSelectDialogFragment;
import com.icourt.alpha.interfaces.OnFragmentCallBackListener;
import com.icourt.alpha.utils.ItemDecorationUtils;
import com.icourt.alpha.utils.TaskReminderUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Description  提醒
 * Company Beijing icourt
 * author  lu.zhao  E-mail:zhaolu@icourt.cc
 * date createTime：17/7/8
 * version 2.0.0
 */

public class ReminderFragment extends BaseFragment implements BaseRecyclerAdapter.OnItemClickListener {
    Unbinder unbinder;
    @BindView(R.id.titleBack)
    ImageView titleBack;
    @BindView(R.id.titleContent)
    TextView titleContent;
    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;

    ReminderListAdapter reminderListAdapter;
    TaskReminderEntity taskReminderEntity;
    @BindView(R.id.bt_clear_reminder)
    TextView btClearReminder;
    @BindView(R.id.bt_ok)
    TextView btOk;

    public static ReminderFragment newInstance(TaskReminderEntity taskReminderEntity) {
        ReminderFragment reminderFragment = new ReminderFragment();
        Bundle args = new Bundle();
        args.putSerializable("taskReminder", taskReminderEntity);
        reminderFragment.setArguments(args);
        return reminderFragment;
    }

    OnFragmentCallBackListener onFragmentCallBackListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            onFragmentCallBackListener = (OnFragmentCallBackListener) context;
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(R.layout.fragment_reminder_layout, inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void initView() {
        titleContent.setText("提醒");
        taskReminderEntity = (TaskReminderEntity) getArguments().getSerializable("taskReminder");
        recyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerview.setHasFixedSize(true);
        recyclerview.addItemDecoration(ItemDecorationUtils.getCommFull05Divider(getContext(), true));
        recyclerview.setAdapter(reminderListAdapter = new ReminderListAdapter());
        reminderListAdapter.setOnItemClickListener(this);
        getData(true);
    }

    @Override
    protected void getData(boolean isRefresh) {
        super.getData(isRefresh);
        List<ReminderItemEntity> reminderItemEntities = new ArrayList<>();
        if (taskReminderEntity != null) {
            if (TextUtils.equals(TaskReminderEntity.ALL_DAY, taskReminderEntity.taskReminderType)) {
                for (Map.Entry<String, String> entry : TaskReminderUtils.alldayMap.entrySet()) {
                    ReminderItemEntity reminderItemEntity = new ReminderItemEntity();
                    reminderItemEntity.timeKey = entry.getKey();
                    reminderItemEntity.timeValue = entry.getValue();
                    reminderItemEntities.add(reminderItemEntity);
                }
            } else if (TextUtils.equals(TaskReminderEntity.PRECISE, taskReminderEntity.taskReminderType)) {
                for (Map.Entry<String, String> entry : TaskReminderUtils.preciseMap.entrySet()) {
                    ReminderItemEntity reminderItemEntity = new ReminderItemEntity();
                    reminderItemEntity.timeKey = entry.getKey();
                    reminderItemEntity.timeValue = entry.getValue();
                    reminderItemEntities.add(reminderItemEntity);
                }
            }
            reminderListAdapter.bindData(true, reminderItemEntities);
        }
    }

    @OnClick({R.id.titleBack,
            R.id.bt_clear_reminder,
            R.id.bt_ok})
    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.titleBack:
                if (getParentFragment().getChildFragmentManager().getBackStackEntryCount() > 1) {
                    getParentFragment().getChildFragmentManager().popBackStack();
                }
                break;
            case R.id.bt_clear_reminder://清除提醒
                reminderListAdapter.clearSelected();
                break;
            case R.id.bt_ok:
                if (getParentFragment() instanceof OnFragmentCallBackListener) {
                    onFragmentCallBackListener = (OnFragmentCallBackListener) getParentFragment();
                }
                if (onFragmentCallBackListener != null) {
                    Bundle bundle = new Bundle();
                    onFragmentCallBackListener.onFragmentCallBack(ReminderFragment.this, DateSelectDialogFragment.SELECT_REMINDER, bundle);
                }
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onItemClick(BaseRecyclerAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
        reminderListAdapter.toggleSelected(position);
    }
}
