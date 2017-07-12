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

import com.bigkoo.pickerview.lib.WheelView;
import com.icourt.alpha.R;
import com.icourt.alpha.adapter.ReminderListAdapter;
import com.icourt.alpha.adapter.TimeWheelAdapter;
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

public class ReminderFragment extends BaseFragment implements BaseRecyclerAdapter.OnItemClickListener, BaseRecyclerAdapter.OnItemChildClickListener {
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
    @BindView(R.id.add_reminder_text)
    TextView addReminderText;
    int customPosition;//自定义的position
    LinearLayoutManager linearLayoutManager;

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
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public static ReminderFragment newInstance(TaskReminderEntity taskReminderEntity) {
        ReminderFragment reminderFragment = new ReminderFragment();
        Bundle args = new Bundle();
        args.putSerializable("taskReminder", taskReminderEntity);
        reminderFragment.setArguments(args);
        return reminderFragment;
    }

    OnFragmentCallBackListener onFragmentCallBackListener;


    @Override
    protected void initView() {

        titleContent.setText("提醒");
        taskReminderEntity = (TaskReminderEntity) getArguments().getSerializable("taskReminder");

        linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerview.setLayoutManager(linearLayoutManager);
        recyclerview.setHasFixedSize(true);
        recyclerview.addItemDecoration(ItemDecorationUtils.getCommFull05Divider(getContext(), true));
        recyclerview.setAdapter(reminderListAdapter = new ReminderListAdapter());
        if (taskReminderEntity != null)
            reminderListAdapter.setTaskReminderType(taskReminderEntity.taskReminderType);
        reminderListAdapter.setOnItemClickListener(this);
        reminderListAdapter.setOnItemChildClickListener(this);
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

            if (taskReminderEntity.customTime != null) {
                for (TaskReminderEntity.CustomTimeItemEntity customTimeItemEntity : taskReminderEntity.customTime) {
                    ReminderItemEntity reminderItemEntity = new ReminderItemEntity();
                    reminderItemEntity.customTimeItemEntity = customTimeItemEntity;
                    reminderItemEntities.add(reminderItemEntity);
                }
            }

            reminderListAdapter.bindData(true, reminderItemEntities);
            if (taskReminderEntity.ruleTime != null) {
                for (int i = 0; i < reminderItemEntities.size(); i++) {
                    if (taskReminderEntity.ruleTime.contains(reminderItemEntities.get(i).timeKey)) {
                        reminderListAdapter.setSelected(i, true);
                    }
                }
            }
            if (taskReminderEntity.customTime != null) {
                for (int i = 0; i < reminderListAdapter.getData().size(); i++) {
                    if (reminderListAdapter.getData().get(i) != null) {
                        if (reminderListAdapter.getData().get(i).customTimeItemEntity != null) {
                            reminderListAdapter.setSelected(i, true);
                        }
                    }
                }
            }
        }
    }

    @OnClick({R.id.titleBack,
            R.id.bt_clear_reminder,
            R.id.bt_ok,
            R.id.add_reminder_text})
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
                    TaskReminderEntity taskReminderEntity = getTaskReminderEntity();
                    bundle.putSerializable("taskReminder", taskReminderEntity);
                    onFragmentCallBackListener.onFragmentCallBack(ReminderFragment.this, DateSelectDialogFragment.SELECT_REMINDER_FINISH, bundle);
                }
                if (getParentFragment().getChildFragmentManager().getBackStackEntryCount() > 1) {
                    getParentFragment().getChildFragmentManager().popBackStack();
                }
                break;
            case R.id.add_reminder_text://添加自定义
                ReminderItemEntity itemEntity = new ReminderItemEntity();
                TaskReminderEntity.CustomTimeItemEntity ctie = new TaskReminderEntity.CustomTimeItemEntity();
                ctie.point = "09:00";
                ctie.unitNumber = "1";
                ctie.unit = "day";
                itemEntity.customTimeItemEntity = ctie;
                reminderListAdapter.addItem(itemEntity);
                customPosition = reminderListAdapter.getItemCount() - 1;
                reminderListAdapter.setSelected(customPosition, true);
                scrollToPosition(customPosition);
                break;
        }
    }

    private TaskReminderEntity getTaskReminderEntity() {
        TaskReminderEntity entity = new TaskReminderEntity();
        if (taskReminderEntity != null) {
            entity.taskReminderType = taskReminderEntity.taskReminderType;
        }
        if (reminderListAdapter != null) {
            if (reminderListAdapter.getSelectedData().size() > 0) {
                for (ReminderItemEntity reminderItemEntity : reminderListAdapter.getSelectedData()) {
                    if (!TextUtils.isEmpty(reminderItemEntity.timeKey)) {
                        if (entity.ruleTime == null) {
                            entity.ruleTime = new ArrayList<>();
                        }
                        entity.ruleTime.add(reminderItemEntity.timeKey);
                    }
                    if (reminderItemEntity.customTimeItemEntity != null) {
                        if (entity.customTime == null) {
                            entity.customTime = new ArrayList<>();
                        }
                        entity.customTime.add(reminderItemEntity.customTimeItemEntity);
                    }
                }
                return entity;
            }

        }
        return entity;
    }

    /**
     * 滚动到指定位置
     */
    private void scrollToPosition(int position) {
        if (linearLayoutManager != null && linearLayoutManager.getItemCount() > 0) {
            linearLayoutManager.scrollToPositionWithOffset(position, 50);
        }
    }

    @Override
    public void onItemClick(BaseRecyclerAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {

        reminderListAdapter.toggleSelected(position);
    }

    @Override
    public void onItemChildClick(BaseRecyclerAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
        customPosition = position;
        ((ReminderListAdapter) adapter).setCustomPosition(position);
        adapter.notifyDataSetChanged();
        scrollToPosition(position);
        switch (view.getId()) {
            case R.id.custom_point_text:
                ((ReminderListAdapter) adapter).setSelect_type(2);
                String poit = ((TextView) view).getText().toString();

                ((WheelView) holder.obtainView(R.id.hour_wheelView)).setAdapter(new TimeWheelAdapter(getTime24or60(24)));
                ((WheelView) holder.obtainView(R.id.minute_wheelView)).setAdapter(new TimeWheelAdapter(getTime24or60(60)));
                if (!TextUtils.isEmpty(poit)) {
                    if (poit.contains(":")) {
                        String[] da = poit.split(":");
                        if (da.length == 2) {
                            ((WheelView) holder.obtainView(R.id.hour_wheelView)).setCurrentItem(Integer.parseInt(da[0]) == 0 ? 0 : Integer.parseInt(da[0]) - 1);
                            ((WheelView) holder.obtainView(R.id.minute_wheelView)).setCurrentItem(Integer.parseInt(da[1]) == 0 ? 0 : Integer.parseInt(da[0]) - 1);
                        }
                    }
                }
                break;
            case R.id.custom_unit_number_text:
            case R.id.custom_unit_text:
                ((ReminderListAdapter) adapter).setSelect_type(1);
                break;
        }
    }

    /**
     * 获取时间：小时list
     *
     * @return
     */
    private List<String> getTime24or60(int num) {
        List<String> timeList = new ArrayList<>();
        for (int i = 1; i < num; i++) {
            timeList.add(String.valueOf(i));
        }
        return timeList;
    }
}
