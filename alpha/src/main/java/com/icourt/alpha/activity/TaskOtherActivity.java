package com.icourt.alpha.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.widget.ImageView;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.adapter.baseadapter.BaseFragmentAdapter;
import com.icourt.alpha.base.BaseActivity;
import com.icourt.alpha.fragment.TaskOtherListFragment;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Description 我分配的/查看他人的任务列表
 * Company Beijing icourt
 * author  lu.zhao  E-mail:zhaolu@icourt.cc
 * date createTime：17/5/19
 * version 2.0.0
 */

public class TaskOtherActivity extends BaseActivity {

    @BindView(R.id.titleBack)
    ImageView titleBack;
    @BindView(R.id.titleContent)
    TextView titleContent;
    @BindView(R.id.titleView)
    AppBarLayout titleView;
    @BindView(R.id.tablayout)
    TabLayout tablayout;
    @BindView(R.id.viewpager)
    ViewPager viewpager;

    BaseFragmentAdapter baseFragmentAdapter;

    public static void launch(@NonNull Context context,
                              @TaskOtherListFragment.START_TYPE int startType,
                              @Nullable ArrayList<String> uids) {
        if (context == null) return;
        Intent intent = new Intent(context, TaskOtherActivity.class);
        intent.putExtra(TaskOtherListFragment.TAG_START_TYPE, startType);
        intent.putExtra(TaskOtherListFragment.TAG_IDS, uids);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myallot_task_layout);
        ButterKnife.bind(this);
        initView();
    }


    @Override
    protected void initView() {
        super.initView();
        if (getQueryType() == TaskOtherListFragment.MY_ALLOT_TYPE) {
            setTitle(R.string.task_my_allot_task);
        } else if (getQueryType() == TaskOtherListFragment.SELECT_OTHER_TYPE) {
            setTitle(getString(R.string.task_look_others_task));
        }
        baseFragmentAdapter = new BaseFragmentAdapter(getSupportFragmentManager());
        viewpager.setAdapter(baseFragmentAdapter);
        tablayout.setupWithViewPager(viewpager);
        ArrayList<String> uids = (ArrayList<String>) getIntent().getSerializableExtra(TaskOtherListFragment.TAG_IDS);
        baseFragmentAdapter.bindTitle(true, Arrays.asList(getString(R.string.task_unfinished), getString(R.string.task_finished)));
        baseFragmentAdapter.bindData(true,
                Arrays.asList(
                        TaskOtherListFragment.newInstance(getQueryType(), TaskOtherListFragment.UNFINISH_TYPE, uids),
                        TaskOtherListFragment.newInstance(getQueryType(), TaskOtherListFragment.FINISH_TYPE, uids)));
    }

    /**
     * 获取查询的类型：我分配的／查看他人的
     *
     * @return START_TYPE枚举中所定义的两种类型。
     */
    @TaskOtherListFragment.START_TYPE
    private int getQueryType() {
        switch (getIntent().getIntExtra(TaskOtherListFragment.TAG_START_TYPE, 0)) {
            case TaskOtherListFragment.MY_ALLOT_TYPE:
                return TaskOtherListFragment.MY_ALLOT_TYPE;
            case TaskOtherListFragment.SELECT_OTHER_TYPE:
                return TaskOtherListFragment.SELECT_OTHER_TYPE;
            default:
                return TaskOtherListFragment.MY_ALLOT_TYPE;
        }
    }
}
