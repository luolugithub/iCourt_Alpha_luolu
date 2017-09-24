package com.icourt.alpha.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.adapter.baseadapter.BaseFragmentAdapter;
import com.icourt.alpha.base.BaseActivity;
import com.icourt.alpha.fragment.FileListFragment;
import com.icourt.alpha.view.tab.AlphaTabLayout;
import com.icourt.alpha.view.tab.AlphaTitleNavigatorAdapter;

import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Description 我的文件 tab[所有人,我自己]
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/17
 * version 1.0.0
 */
public class MyFileTabActivity extends BaseActivity {
    @BindView(R.id.titleBack)
    ImageView titleBack;
    @BindView(R.id.titleContent)
    TextView titleContent;
    @BindView(R.id.titleView)
    AppBarLayout titleView;
    @BindView(R.id.tabLayout)
    AlphaTabLayout tabLayout;
    @BindView(R.id.viewPager)
    ViewPager viewPager;
    private BaseFragmentAdapter baseFragmentAdapter;

    public static void launch(@NonNull Context context) {
        if (context == null) return;
        Intent intent = new Intent(context, MyFileTabActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_file_tab);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void initView() {
        super.initView();
        setTitle("文件");
        viewPager.setAdapter(baseFragmentAdapter = new BaseFragmentAdapter(getSupportFragmentManager()));
        CommonNavigator commonNavigator = new CommonNavigator(getContext());
        commonNavigator.setAdapter(new AlphaTitleNavigatorAdapter(1.0f) {

            @Nullable
            @Override
            public CharSequence getTitle(int index) {
                return baseFragmentAdapter.getPageTitle(index);
            }

            @Override
            public int getCount() {
                return baseFragmentAdapter.getCount();
            }

            @Override
            public void onTabClick(View v, int pos) {
                viewPager.setCurrentItem(pos, true);
            }

        });
        tabLayout.setNavigator2(commonNavigator)
                .setupWithViewPager(viewPager);
        baseFragmentAdapter.bindTitle(true, Arrays.asList("所有人", "我自己"));
        baseFragmentAdapter.bindData(true, Arrays.asList(FileListFragment.newInstance(FileListFragment.TYPE_ALL_FILE),
                FileListFragment.newInstance(FileListFragment.TYPE_MY_FILE)));
    }
}
