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

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Description 我分配的任务列表
 * Company Beijing icourt
 * author  lu.zhao  E-mail:zhaolu@icourt.cc
 * date createTime：17/5/19
 * version 2.0.0
 */

public class MyAllotTaskActivity extends BaseActivity {

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

    public static void launch(@NonNull Context context) {
        if (context == null) return;
        Intent intent = new Intent(context, MyAllotTaskActivity.class);
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
        setTitle("我分配的任务");
        baseFragmentAdapter = new BaseFragmentAdapter(getSupportFragmentManager());
        viewpager.setAdapter(baseFragmentAdapter);
        tablayout.setupWithViewPager(viewpager);
        baseFragmentAdapter.bindTitle(true, Arrays.asList("未完成", "已完成"));
        baseFragmentAdapter.bindData(true,
                Arrays.asList(
                        TaskOtherListFragment.newInstance(TaskOtherListFragment.MY_ALLOT_TYPE, TaskOtherListFragment.UNFINISH_TYPE, null),
                        TaskOtherListFragment.newInstance(TaskOtherListFragment.MY_ALLOT_TYPE, TaskOtherListFragment.FINISH_TYPE, null)));
    }
}
