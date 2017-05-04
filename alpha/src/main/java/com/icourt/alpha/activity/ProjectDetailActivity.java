package com.icourt.alpha.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.adapter.baseadapter.BaseFragmentAdapter;
import com.icourt.alpha.base.BaseActivity;
import com.icourt.alpha.fragment.MyProjectFragment;
import com.icourt.alpha.fragment.ProjectDetailFragment;
import com.icourt.alpha.fragment.ProjectTaskFragment;
import com.icourt.alpha.fragment.ProjectTimeFragment;
import com.icourt.alpha.utils.logger.Logger;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Description   项目详情页面
 * Company Beijing icourt
 * author  lu.zhao  E-mail:zhaolu@icourt.cc
 * date createTime：17/5/2
 * version 2.0.0
 */

public class ProjectDetailActivity extends BaseActivity {

    @BindView(R.id.titleBack)
    ImageView titleBack;
    @BindView(R.id.titleContent)
    TextView titleContent;
    @BindView(R.id.titleAction)
    ImageView titleAction;
    @BindView(R.id.titleAction2)
    ImageView titleAction2;
    @BindView(R.id.titleView)
    AppBarLayout titleView;
    @BindView(R.id.detail_tablayout)
    TabLayout detailTablayout;
    @BindView(R.id.detail_viewpager)
    ViewPager detailViewpager;

    String projectId, projectName;
    BaseFragmentAdapter baseFragmentAdapter;

    public static void launch(@NonNull Context context, @NonNull String projectId, @NonNull String proectName) {
        if (context == null) return;
        if (TextUtils.isEmpty(projectId)) return;
        Intent intent = new Intent(context, ProjectDetailActivity.class);
        intent.putExtra("projectId", projectId);
        intent.putExtra("projectName", proectName);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_detail_layout);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void initView() {
        super.initView();
        projectId = getIntent().getStringExtra("projectId");
        projectName = getIntent().getStringExtra("projectName");
        Logger.e("projectId --- " + projectId);
        if (!TextUtils.isEmpty(projectName)) {
            setTitle(projectName);
        }
        titleAction.setImageResource(R.mipmap.header_icon_star_line);
        titleAction2.setImageResource(R.mipmap.header_icon_more);
        baseFragmentAdapter = new BaseFragmentAdapter(getSupportFragmentManager());
        detailViewpager.setAdapter(baseFragmentAdapter);
        detailTablayout.setupWithViewPager(detailViewpager);
        baseFragmentAdapter.bindTitle(true, Arrays.asList(
//                "动态",
                "概览", "任务", "计时", "文档", "分析"));
        baseFragmentAdapter.bindData(true,
                Arrays.asList(
//                        ProjectDetailFragment.newInstance(projectId),
                        ProjectDetailFragment.newInstance(projectId),
                        ProjectTaskFragment.newInstance(projectId),
                        ProjectTimeFragment.newInstance(projectId),
                        MyProjectFragment.newInstance(MyProjectFragment.TYPE_MY_PARTIC_PROJECT),
                        MyProjectFragment.newInstance(MyProjectFragment.TYPE_MY_PARTIC_PROJECT)));
    }

    @OnClick({R.id.titleAction, R.id.titleAction2})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.titleAction:
                break;
            case R.id.titleAction2:
                break;
        }
    }

}
