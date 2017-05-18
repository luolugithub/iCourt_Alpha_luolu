package com.icourt.alpha.fragment.dialogfragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.adapter.TaskOwerListAdapter;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.entity.bean.TaskEntity;
import com.icourt.alpha.entity.bean.TaskOwerEntity;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.interfaces.OnFragmentCallBackListener;
import com.icourt.alpha.utils.DensityUtil;
import com.icourt.alpha.utils.ItemDecorationUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Description  选择任务分配人
 * Company Beijing icourt
 * author  lu.zhao  E-mail:zhaolu@icourt.cc
 * date createTime：17/5/16
 * version 2.0.0
 */

public class TaskAllotSelectDialogFragment extends BaseDialogFragment implements BaseRecyclerAdapter.OnItemClickListener {

    Unbinder unbinder;
    @BindView(R.id.bt_cancel)
    TextView btCancel;
    @BindView(R.id.bt_ok)
    TextView btOk;
    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;
    String projectId;
    TaskOwerListAdapter taskOwerListAdapter;
    List<TaskEntity.TaskItemEntity.AttendeeUserEntity> attendeeUserEntities;
    OnFragmentCallBackListener onFragmentCallBackListener;

    public static TaskAllotSelectDialogFragment newInstance(@NonNull String projectId, List<TaskEntity.TaskItemEntity.AttendeeUserEntity> attendeeUserEntities) {
        TaskAllotSelectDialogFragment taskAllotSelectDialogFragment = new TaskAllotSelectDialogFragment();
        Bundle args = new Bundle();
        args.putString("projectId", projectId);
        args.putSerializable("list", (Serializable) attendeeUserEntities);
        taskAllotSelectDialogFragment.setArguments(args);
        return taskAllotSelectDialogFragment;
    }

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
        View view = super.onCreateView(R.layout.dialog_fragment_task_select_layout, inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void initView() {
        Dialog dialog = getDialog();
        if (dialog != null) {
            Window window = dialog.getWindow();
            if (window != null) {
                window.setGravity(Gravity.BOTTOM);
                View decorView = window.getDecorView();
                if (decorView != null) {
                    int dp20 = DensityUtil.dip2px(getContext(), 20);
                    decorView.setPadding(dp20 / 2, dp20, dp20 / 2, dp20);
                }
            }
        }
        projectId = getArguments().getString("projectId");
        attendeeUserEntities = (List<TaskEntity.TaskItemEntity.AttendeeUserEntity>) getArguments().getSerializable("list");
        recyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerview.addItemDecoration(ItemDecorationUtils.getCommFull05Divider(getContext(), true));
        recyclerview.setAdapter(taskOwerListAdapter = new TaskOwerListAdapter());
        taskOwerListAdapter.setOnItemClickListener(this);
        getData(true);
    }

    @OnClick({R.id.bt_ok, R.id.bt_cancel})
    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.bt_ok:
                if (onFragmentCallBackListener != null) {
                    List<TaskEntity.TaskItemEntity.AttendeeUserEntity> attusers = getSelectUsers();
                    if (attusers != null) {
                        Bundle bunble = new Bundle();
                        bunble.putSerializable("list", (Serializable) attusers);
                        onFragmentCallBackListener.onFragmentCallBack(TaskAllotSelectDialogFragment.this, 0, bunble);
                    }
                }
                dismiss();
                break;
            case R.id.bt_cancel:
                dismiss();
                break;
        }
    }

    private List<TaskEntity.TaskItemEntity.AttendeeUserEntity> getSelectUsers() {
        List<TaskOwerEntity> owers = taskOwerListAdapter.getSelectedData();
        if (owers != null) {
            if (owers.size() > 0) {
                List<TaskEntity.TaskItemEntity.AttendeeUserEntity> attusers = new ArrayList<>();
                for (TaskOwerEntity ower : owers) {
                    TaskEntity.TaskItemEntity.AttendeeUserEntity att = new TaskEntity.TaskItemEntity.AttendeeUserEntity();
                    att.userId = ower.id;
                    att.userName = ower.name;
                    att.pic = ower.pic;
                    attusers.add(att);
                }
                return attusers;
            }
        }
        return null;
    }

    @Override
    protected void getData(boolean isRefresh) {
        super.getData(isRefresh);
        getApi().taskOwerListQuery(projectId).enqueue(new SimpleCallBack<List<TaskOwerEntity>>() {
            @Override
            public void onSuccess(Call<ResEntity<List<TaskOwerEntity>>> call, Response<ResEntity<List<TaskOwerEntity>>> response) {
                if (response.body().result != null) {
                    if (attendeeUserEntities != null) {
                        if (attendeeUserEntities.size() > 0) {
                            for (int i = 0; i < response.body().result.size(); i++) {
                                for (TaskEntity.TaskItemEntity.AttendeeUserEntity attendeeUserEntity : attendeeUserEntities) {
                                    if (TextUtils.equals(attendeeUserEntity.userId, response.body().result.get(i).id)) {
                                        taskOwerListAdapter.getSelectedArray().put(i, true);
                                    }
                                }
                            }
                        }
                    }
                    taskOwerListAdapter.bindData(true, response.body().result);
                }

            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onItemClick(BaseRecyclerAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
        taskOwerListAdapter.toggleSelected(position);
    }
}
