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
import com.icourt.alpha.adapter.baseadapter.BaseFragmentAdapter;
import com.icourt.alpha.base.BaseFragment;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/17
 * version 1.0.0
 */
public class TabFindProjectFragment extends BaseFragment {

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
    @BindView(R.id.titleAction2)
    ImageView titleAction2;

    public static TabFindProjectFragment newInstance() {
        return new TabFindProjectFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(R.layout.fragment_tab_find_project, inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void initView() {
        baseFragmentAdapter = new BaseFragmentAdapter(getChildFragmentManager());
        viewPager.setAdapter(baseFragmentAdapter);
        tabLayout.setupWithViewPager(viewPager);
        baseFragmentAdapter.bindTitle(true, Arrays.asList("全部", "我关注的", "我参与的"));
        baseFragmentAdapter.bindData(true,
                Arrays.asList(MyProjectFragment.newInstance(MyProjectFragment.TYPE_ALL_PROJECT),
                        MyProjectFragment.newInstance(MyProjectFragment.TYPE_MY_ATTENTION_PROJECT), MyProjectFragment.newInstance(MyProjectFragment.TYPE_MY_PARTIC_PROJECT)));
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
