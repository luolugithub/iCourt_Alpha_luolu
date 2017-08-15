package com.icourt.alpha.fragment.dialogfragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.adapter.baseadapter.BaseFragmentAdapter;
import com.icourt.alpha.base.BaseDialogFragment;
import com.icourt.alpha.entity.bean.TaskEntity;
import com.icourt.alpha.fragment.FinishedTaskFragment;
import com.icourt.alpha.fragment.UnFinishTaskFragment;
import com.icourt.alpha.interfaces.OnFragmentCallBackListener;
import com.icourt.alpha.utils.DensityUtil;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/5/16
 * version 1.0.0
 */
public class TaskSelectDialogFragment
        extends BaseDialogFragment {


    Unbinder unbinder;

    @BindView(R.id.bt_cancel)
    TextView btCancel;
    @BindView(R.id.bt_ok)
    TextView btOk;
    @BindView(R.id.tablayout)
    TabLayout tablayout;
    @BindView(R.id.viewpager)
    ViewPager viewpager;
    private BaseFragmentAdapter baseFragmentAdapter;
    OnFragmentCallBackListener onFragmentCallBackListener;
    String projectId = null;
    String selectedTaskId;

    UnFinishTaskFragment unFinishTaskFragment;
    FinishedTaskFragment finishedTaskFragment;

    /**
     * @param projectId 为空 为我的任务
     * @return
     */
    public static TaskSelectDialogFragment newInstance(@Nullable String projectId, String selectedTaskId) {
        TaskSelectDialogFragment workTypeSelectDialogFragment = new TaskSelectDialogFragment();
        Bundle args = new Bundle();
        args.putString("projectId", projectId);
        args.putString("selectedTaskId", selectedTaskId);
        workTypeSelectDialogFragment.setArguments(args);
        return workTypeSelectDialogFragment;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            onFragmentCallBackListener = (OnFragmentCallBackListener) context;
        } catch (ClassCastException e) {
            e.printStackTrace();
            bugSync("选择任务：onFragmentCallBackListener", e);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Window window = getDialog().getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(R.layout.dialog_fragment_task, inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void initView() {
        projectId = getArguments().getString("projectId");
        selectedTaskId = getArguments().getString("selectedTaskId");
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
        viewpager.setAdapter(baseFragmentAdapter = new BaseFragmentAdapter(getChildFragmentManager()));
        tablayout.setupWithViewPager(viewpager);
        baseFragmentAdapter.bindTitle(true, Arrays.asList("未完成", "已完成"));
        baseFragmentAdapter.bindData(true,
                Arrays.asList(unFinishTaskFragment = UnFinishTaskFragment.newInstance(projectId, selectedTaskId),
                        finishedTaskFragment = FinishedTaskFragment.newInstance(projectId, selectedTaskId)));
    }


    @OnClick({R.id.bt_cancel,
            R.id.bt_ok})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_cancel:
                dismiss();
                break;
            case R.id.bt_ok:
                if (getParentFragment() instanceof OnFragmentCallBackListener) {
                    onFragmentCallBackListener = (OnFragmentCallBackListener) getParentFragment();
                }
                if (onFragmentCallBackListener != null) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(KEY_FRAGMENT_RESULT, getSelected());
                    onFragmentCallBackListener.onFragmentCallBack(TaskSelectDialogFragment.this, 0, bundle);
                }
                dismiss();
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    /**
     * 获取已选择的任务
     *
     * @return
     */
    private TaskEntity.TaskItemEntity getSelected() {
        TaskEntity.TaskItemEntity taskItemEntity = null;
        if (unFinishTaskFragment.getSelectedTask() != null) {
            taskItemEntity = unFinishTaskFragment.getSelectedTask();
        } else if (finishedTaskFragment.getSelectedTask() != null) {
            taskItemEntity = finishedTaskFragment.getSelectedTask();
        }
        return taskItemEntity;
    }

    /**
     * 未完成的清除已选中
     */
    public void unFinishClearSelected() {
        unFinishTaskFragment.clearSelected();
    }

    /**
     * 已完成的清除已选中
     */
    public void finishClearSelected() {
        finishedTaskFragment.clearSelected();
    }
}
