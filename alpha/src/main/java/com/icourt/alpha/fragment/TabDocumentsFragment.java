package com.icourt.alpha.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.icourt.alpha.R;
import com.icourt.alpha.activity.RepoCreateActivity;
import com.icourt.alpha.adapter.baseadapter.BaseFragmentAdapter;
import com.icourt.alpha.base.BaseFragment;
import com.icourt.alpha.constants.SFileConfig;
import com.icourt.alpha.view.tab.AlphaTabLayout;
import com.icourt.alpha.view.tab.AlphaTitleNavigatorAdapter;

import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Description  文档模块tab页面
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/8/9
 * version 2.1.0
 */
public class TabDocumentsFragment extends BaseFragment {

    Unbinder unbinder;
    BaseFragmentAdapter baseFragmentAdapter;
    @BindView(R.id.tabLayout)
    AlphaTabLayout tabLayout;
    @BindView(R.id.titleAction)
    ImageView titleAction;
    @BindView(R.id.titleView)
    AppBarLayout titleView;
    @BindView(R.id.viewPager)
    ViewPager viewPager;

    public static TabDocumentsFragment newInstance() {
        return new TabDocumentsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(R.layout.fragment_tab_documents, inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void initView() {
        viewPager.setAdapter(baseFragmentAdapter = new BaseFragmentAdapter(getChildFragmentManager()));
        baseFragmentAdapter.bindTitle(true,
                Arrays.asList(getResources().getStringArray(R.array.repo_type_array)));
        baseFragmentAdapter.bindData(true,
                Arrays.asList(
                        RepoListFragment.newInstance(SFileConfig.REPO_MINE),
                        RepoListFragment.newInstance(SFileConfig.REPO_SHARED_ME),
                        RepoListFragment.newInstance(SFileConfig.REPO_LAWFIRM),
                        RepoListFragment.newInstance(SFileConfig.REPO_PROJECT)));
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                titleAction.setVisibility(position > 0 ? View.GONE : View.VISIBLE);
            }
        });

        CommonNavigator commonNavigator = new CommonNavigator(getContext());
        commonNavigator.setAdapter(new AlphaTitleNavigatorAdapter() {
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
    }

    @OnClick({R.id.titleAction})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.titleAction:
                RepoCreateActivity.launch(getContext());
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
