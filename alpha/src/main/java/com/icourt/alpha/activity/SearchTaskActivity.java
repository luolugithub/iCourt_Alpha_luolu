package com.icourt.alpha.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.icourt.alpha.R;
import com.icourt.alpha.base.BaseActivity;
import com.icourt.alpha.fragment.SearchTaskFragment;
import com.icourt.alpha.fragment.TaskListCalendarFragment;

import butterknife.ButterKnife;

/**
 * Description 搜索任务
 * Company Beijing icourt
 * author  zhaodanyang  E-mail:zhaodanyang@icourt.cc
 * date createTime：2017/9/7
 * version 2.0.0
 */

public class SearchTaskActivity extends BaseActivity {

    int searchTaskType;//搜索任务type
    int taskStatuType;//搜索任务状态type
    String projectId;//项目的id
    String assignTos;//负责人的id的集合

    /**
     * ·
     * 搜索未完成（全部、新任务、我关注的）的任务
     *
     * @param context
     * @param searchTaskType 0:全部；1：新任务；2：我关注的；3我部门的
     */
    public static void launchTask(@NonNull Context context, String assignTos, int searchTaskType) {
        if (context == null) return;
        Intent intent = new Intent(context, SearchTaskActivity.class);
        intent.putExtra(SearchTaskFragment.KEY_SEARCH_TASK_TYPE, searchTaskType);
        intent.putExtra(SearchTaskFragment.KEY_ASSIGN_TOS, assignTos);
        context.startActivity(intent);
    }

    /**
     * 搜索已完成的全部任务
     *
     * @param context
     * @param taskStatuType  0:未完成；1：已完成；2：已删除
     * @param searchTaskType 0:全部；1：新任务；2：我关注的；3我部门的
     * @param projectId      项目id
     */
    public static void launchFinishTask(@NonNull Context context, String assignTos, int searchTaskType, int taskStatuType, String projectId) {
        if (context == null) return;
        Intent intent = new Intent(context, SearchTaskActivity.class);
        intent.putExtra(SearchTaskFragment.KEY_SEARCH_TASK_TYPE, searchTaskType);
        intent.putExtra(SearchTaskFragment.KEY_SEARCH_TASK_STATUS_TYPE, taskStatuType);
        intent.putExtra(SearchTaskFragment.KEY_PROJECT_ID, projectId);
        intent.putExtra(SearchTaskFragment.KEY_ASSIGN_TOS, assignTos);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_task);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void initView() {
        super.initView();
        searchTaskType = getIntent().getIntExtra(SearchTaskFragment.KEY_SEARCH_TASK_TYPE, -1);
        taskStatuType = getIntent().getIntExtra(SearchTaskFragment.KEY_SEARCH_TASK_STATUS_TYPE, -1);
        projectId = getIntent().getStringExtra(SearchTaskFragment.KEY_PROJECT_ID);
        assignTos = getIntent().getStringExtra(SearchTaskFragment.KEY_ASSIGN_TOS);

        SearchTaskFragment searchTaskFragment = SearchTaskFragment.newInstance(getActivity(), assignTos, searchTaskType, taskStatuType, projectId);
        addOrShowFragmentAnim(searchTaskFragment, R.id.container, true);
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
    public void onBackPressed() {
        finish();
    }
}
