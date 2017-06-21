package com.icourt.alpha.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.icourt.alpha.R;
import com.icourt.alpha.activity.GroupCreateActivity;
import com.icourt.alpha.adapter.baseadapter.BaseFragmentAdapter;
import com.icourt.alpha.base.BaseFragment;
import com.icourt.alpha.entity.event.UnReadEvent;
import com.icourt.alpha.interfaces.OnFragmentCallBackListener;
import com.icourt.alpha.interfaces.OnPageFragmentCallBack;
import com.icourt.alpha.interfaces.OnTabDoubleClickListener;
import com.icourt.alpha.service.SyncDataService;
import com.icourt.alpha.widget.nim.GlobalMessageObserver;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.msg.MsgServiceObserve;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Description  享聊tab页面
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/8
 * version 1.0.0
 */
public class TabNewsFragment extends BaseFragment
        implements OnTabDoubleClickListener, OnFragmentCallBackListener, OnPageFragmentCallBack {

    Unbinder unbinder;
    @BindView(R.id.tabLayout)
    TabLayout tabLayout;
    @BindView(R.id.ivActionAdd)
    ImageView ivActionAdd;
    @BindView(R.id.viewPager)
    ViewPager viewPager;
    OnFragmentCallBackListener parentFragmentCallBackListener;

    private BaseFragmentAdapter baseFragmentAdapter;
    GlobalMessageObserver globalMessageObserver = new GlobalMessageObserver();

    public static TabNewsFragment newInstance() {
        return new TabNewsFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NIMClient.getService(MsgServiceObserve.class)
                .observeReceiveMessage(globalMessageObserver, true);
        SyncDataService.startSysnContact(getActivity());
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            parentFragmentCallBackListener = (OnFragmentCallBackListener) context;
        } catch (ClassCastException e) {
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        SyncDataService.startSysnContact(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(R.layout.fragment_tab_news, inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }


    @Override
    protected void initView() {
        EventBus.getDefault().register(this);
        viewPager.setAdapter(baseFragmentAdapter = new BaseFragmentAdapter(getChildFragmentManager()));
        tabLayout.setupWithViewPager(viewPager);
        baseFragmentAdapter.bindTitle(true, Arrays.asList("消息", "@我的", "通讯录"));
        baseFragmentAdapter.bindData(true,
                Arrays.asList(MessageListFragment.newInstance(),
                        AtMeFragment.newInstance(),
                        ContactListFragment.newInstance()));
    }

    @OnClick({R.id.ivActionAdd})
    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.ivActionAdd:
                GroupCreateActivity.launch(getContext());
                // TestActivity.launch(getContext());
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUnReadEvent(UnReadEvent event) {
        if (event == null) return;
        //动态修改tabLayout 指示器
        //方式1：
        // tabLayout.getTabAt(0).setText("未使用(%s)");

        //方式2:
        StringBuilder newsTabBuilder = new StringBuilder("消息");
        int unReadNum = event.unReadCount;
        if (unReadNum > 99) {
            newsTabBuilder.append("(...)");
        } else if (unReadNum > 0) {
            newsTabBuilder.append("(" + unReadNum + ")");
        }
        baseFragmentAdapter.bindTitle(true,
                Arrays.asList(newsTabBuilder.toString(), "@我的", "通讯录"));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onTabDoubleClick(Fragment targetFragment, View v, Bundle bundle) {
        if (targetFragment != TabNewsFragment.this) return;
        Fragment item = baseFragmentAdapter.getItem(0);
        if (item instanceof OnTabDoubleClickListener) {
            ((OnTabDoubleClickListener) item).onTabDoubleClick(item, v, bundle);
        }
    }

    @Override
    public void onFragmentCallBack(Fragment fragment, int type, Bundle params) {
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        NIMClient.getService(MsgServiceObserve.class)
                .observeReceiveMessage(globalMessageObserver, false);
    }

    @Override
    public void onRequest2NextPage(Fragment fragment, int type, Bundle bundle) {
        if (fragment instanceof MessageListFragment) {
            //进入联系人tab
            viewPager.setCurrentItem(2);
        }
    }

    @Override
    public void onRequest2LastPage(Fragment fragment, int type, Bundle bundle) {

    }

    @Override
    public void onRequest2Page(Fragment fragment, int type, int pagePos, Bundle bundle) {

    }

    @Override
    public boolean canGoNextFragment(Fragment fragment) {
        return false;
    }

    @Override
    public boolean canGoLastFragment(Fragment fragment) {
        return false;
    }
}
