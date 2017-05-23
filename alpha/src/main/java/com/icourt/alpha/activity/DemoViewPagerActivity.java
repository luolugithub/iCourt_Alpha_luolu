package com.icourt.alpha.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;

import com.icourt.alpha.R;
import com.icourt.alpha.adapter.baseadapter.BaseFragmentAdapter;
import com.icourt.alpha.base.BaseActivity;
import com.icourt.alpha.fragment.TabMineFragment;
import com.icourt.alpha.fragment.TabNewsFragment;
import com.icourt.alpha.fragment.TabTaskFragment;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/9
 * version 1.0.0
 */
public class DemoViewPagerActivity extends BaseActivity {
    @BindView(R.id.viewPager)
    ViewPager viewPager;

    BaseFragmentAdapter baseFragmentAdapter;

    public static void launch(@NonNull Context context) {
        if (context == null) return;
        Intent intent = new Intent(context, DemoViewPagerActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo_viewpager);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void initView() {
        super.initView();
        baseFragmentAdapter = new BaseFragmentAdapter(getSupportFragmentManager());
        viewPager.setAdapter(baseFragmentAdapter);
        baseFragmentAdapter.bindData(true, Arrays.asList(TabTaskFragment.newInstance()
                , TabMineFragment.newInstance()
                , TabNewsFragment.newInstance()));
    }
}
