package com.icourt.alpha.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.icourt.alpha.R;
import com.icourt.alpha.activity.TaskCreateActivity;
import com.icourt.alpha.adapter.baseadapter.BaseFragmentAdapter;
import com.icourt.alpha.base.BaseFragment;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Description  任务tab页面
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/8
 * version 1.0.0
 */
public class TabTaskFragment extends BaseFragment {
    @BindView(R.id.tabLayout)
    TabLayout tabLayout;
    @BindView(R.id.titleAction)
    ImageView titleAction;
    @BindView(R.id.titleView)
    AppBarLayout titleView;
    @BindView(R.id.viewPager)
    ViewPager viewPager;
    Unbinder unbinder;
    BaseFragmentAdapter baseFragmentAdapter;

    public static TabTaskFragment newInstance() {
        return new TabTaskFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(R.layout.fragment_tab_task, inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void initView() {
        baseFragmentAdapter = new BaseFragmentAdapter(getChildFragmentManager());
        viewPager.setAdapter(baseFragmentAdapter);
        tabLayout.setupWithViewPager(viewPager);
        baseFragmentAdapter.bindTitle(true, Arrays.asList("全部", "新任务", "我关注的", "我部门的"));
        baseFragmentAdapter.bindData(true,
                Arrays.asList(
                        TaskListFragment.newInstance(0),
                        TaskListFragment.newInstance(1),
                        TaskListFragment.newInstance(2),
                        TaskListFragment.newInstance(3)));
    }

    @OnClick({R.id.titleAction})
    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.titleAction:
                TaskCreateActivity.launch(getContext(), null, null);
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

}
