package com.icourt.alpha.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.base.BaseActivity;
import com.icourt.alpha.fragment.ProjectEndTaskFragment;
import com.icourt.alpha.fragment.TaskListCalendarFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Description 项目详情：已完成任务页面
 * Company Beijing icourt
 * author  zhaodanyang  E-mail:zhaodanyang@icourt.cc
 * date createTime：2017/9/7
 * version 2.0.0
 */

public class ProjectEndTaskActivity extends BaseActivity {

    @BindView(R.id.titleBack)
    ImageView titleBack;
    @BindView(R.id.titleContent)
    TextView titleContent;
    @BindView(R.id.titleView)
    AppBarLayout titleView;
    @BindView(R.id.container)
    FrameLayout flContainer;

    String projectId;

    public static void launch(@NonNull Context context, @NonNull String projectId) {
        if (context == null) return;
        Intent intent = new Intent(context, ProjectEndTaskActivity.class);
        intent.putExtra(ProjectEndTaskFragment.KEY_PROJECT_ID, projectId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_end_task_layout);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void initView() {
        super.initView();
        setTitle("查看已完成任务");
        projectId = getIntent().getStringExtra(ProjectEndTaskFragment.KEY_PROJECT_ID);
        ProjectEndTaskFragment projectEndTaskFragment = ProjectEndTaskFragment.newInstance(getActivity(), projectId);
        addOrShowFragmentAnim(projectEndTaskFragment, R.id.container, true);
    }

    private void addOrShowFragmentAnim(@NonNull Fragment targetFragment, @IdRes int containerViewId, boolean isAnim) {
        if (targetFragment == null) return;
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        if (isAnim) {
            if (targetFragment instanceof TaskListCalendarFragment) {
                transaction.setCustomAnimations(
                        R.anim.fragment_slide_top_in, 0);
            } else {
                transaction.setCustomAnimations(0, R.anim.fragment_slide_top_out);
            }
        }
        transaction.add(containerViewId, targetFragment, String.valueOf(targetFragment.hashCode())).commitAllowingStateLoss();
        transaction.addToBackStack(null);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            default:
                super.onClick(v);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
