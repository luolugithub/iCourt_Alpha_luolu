package com.icourt.alpha.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.activity.MyAllotTaskActivity;
import com.icourt.alpha.activity.TaskCreateActivity;
import com.icourt.alpha.adapter.ListDropDownAdapter;
import com.icourt.alpha.adapter.baseadapter.BaseFragmentAdapter;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.base.BaseFragment;
import com.icourt.alpha.entity.bean.FilterDropEntity;
import com.icourt.alpha.entity.bean.TaskMemberEntity;
import com.icourt.alpha.fragment.dialogfragment.TaskMemberSelectDialogFragment;
import com.icourt.alpha.interfaces.OnFragmentCallBackListener;
import com.icourt.alpha.utils.DensityUtil;
import com.icourt.alpha.utils.RAUtils;
import com.icourt.alpha.view.NoScrollViewPager;
import com.icourt.alpha.widget.popupwindow.TopMiddlePopup;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
public class TabTaskFragment extends BaseFragment implements OnFragmentCallBackListener, TopMiddlePopup.OnItemClickListener {

    public static int select_position = 0;//选择的筛选选项

    @BindView(R.id.tabLayout)
    TabLayout tabLayout;
    @BindView(R.id.titleAction)
    ImageView titleAction;
    @BindView(R.id.titleView)
    AppBarLayout titleView;
    @BindView(R.id.viewPager)
    NoScrollViewPager viewPager;
    Unbinder unbinder;
    BaseFragmentAdapter baseFragmentAdapter;
    @BindView(R.id.titleCalendar)
    ImageView titleCalendar;
    TaskListFragment attentionTaskFragment;
    TaskAllFragment alltaskFragment;
    ListDropDownAdapter listDropDownAdapter;
    TopMiddlePopup topMiddlePopup;

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
        viewPager.setNoScroll(false);
        viewPager.setAdapter(baseFragmentAdapter);
        tabLayout.setupWithViewPager(viewPager);
//        baseFragmentAdapter.bindTitle(true, Arrays.asList("全部", "我关注的"));
        baseFragmentAdapter.bindData(true,
                Arrays.asList(
                        alltaskFragment = TaskAllFragment.newInstance(),
                        attentionTaskFragment = TaskListFragment.newInstance(1)));
        topMiddlePopup = new TopMiddlePopup(getContext(), DensityUtil.getWidthInDp(getContext()), (int) (DensityUtil.getHeightInPx(getContext()) - DensityUtil.dip2px(getContext(), 75)), this);

        for (int i = 0; i < baseFragmentAdapter.getCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            tab.setCustomView(R.layout.task_unfinish_tab_custom_view);
            TextView titleTv = tab.getCustomView().findViewById(R.id.tab_custom_title_tv);
            ImageView downIv = tab.getCustomView().findViewById(R.id.tab_custom_title_iv);
            switch (i) {
                case 0:
                    titleTv.setText("未完成");
                    downIv.setVisibility(View.VISIBLE);
                    tab.getCustomView().setOnClickListener(new OnTabClickListener());
                    break;
                case 1:
                    titleTv.setText("我关注的");
                    downIv.setVisibility(View.GONE);
                    break;
            }
        }
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                titleCalendar.setVisibility(View.GONE);
                switch (position) {
                    case 0:
                        titleCalendar.setVisibility(View.VISIBLE);
                        break;
                    case 1:
                        setFirstTabImage(false);
                        attentionTaskFragment.notifyFragmentUpdate(attentionTaskFragment, position, null);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        topMiddlePopup.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                setFirstTabImage(false);
            }
        });
    }

    /**
     * 设置第一个tab的文本内容
     *
     * @param content
     */
    public void setFirstTabText(String content, int position) {
        if (tabLayout == null) return;
        if (tabLayout.getTabAt(0) == null) return;
        if (tabLayout.getTabAt(0).getCustomView() == null) return;
        TextView titleTv = tabLayout.getTabAt(0).getCustomView().findViewById(R.id.tab_custom_title_tv);
        titleTv.setText(content);
        select_position = position;
        topMiddlePopup.getAdapter().setSelectedPos(select_position);
    }

    @Override
    public void onItemClick(TopMiddlePopup topMiddlePopup, BaseRecyclerAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
        topMiddlePopup.dismiss();
        FilterDropEntity filterDropEntity = (FilterDropEntity) adapter.getItem(position);
        setFirstTabText(filterDropEntity.name, select_position);
        Bundle bundle = new Bundle();
        bundle.putInt("stateType", filterDropEntity.stateType);
        alltaskFragment.notifyFragmentUpdate(alltaskFragment, TaskAllFragment.TYPE_ALL_TASK, bundle);
    }

    private class OnTabClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            if (tabLayout.getTabAt(0) != null) {
                if (view.isSelected()) {
                    topMiddlePopup.show(titleView, getItems(), select_position);
                    setFirstTabImage(true);
                } else {
                    tabLayout.getTabAt(0).select();
                }
            }
        }
    }

    private List<FilterDropEntity> getItems() {
        List<FilterDropEntity> dropEntities = new ArrayList<>();
        dropEntities.add(new FilterDropEntity("未完成", "22", 0));
        dropEntities.add(new FilterDropEntity("已完成", "51", 1));
        dropEntities.add(new FilterDropEntity("已删除", "9", 3));
        return dropEntities;
    }

    /**
     * 设置第一个tab的小图标
     *
     * @param isOpen
     */
    private void setFirstTabImage(boolean isOpen) {
        if (tabLayout != null)
            if (tabLayout.getTabAt(0) != null) {
                if (tabLayout.getTabAt(0).getCustomView() != null) {
                    ImageView iv = tabLayout.getTabAt(0).getCustomView().findViewById(R.id.tab_custom_title_iv);
                    iv.setImageResource(isOpen ? R.mipmap.task_dropup : R.mipmap.task_dropdown);
                }
            }

    }

    @OnClick({R.id.titleAction,
            R.id.titleCalendar})
    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.titleCalendar:
                if (!RAUtils.isLegal(RAUtils.DURATION_DEFAULT)) return;
                Fragment item = baseFragmentAdapter.getItem(viewPager.getCurrentItem());
                if (item instanceof TaskAllFragment) {
                    TaskAllFragment taskAllFragment = (TaskAllFragment) item;
                    switch (taskAllFragment.getChildFragmentType()) {
                        case TaskAllFragment.TYPE_ALL_TASK:
                            viewPager.setNoScroll(true);
                            titleCalendar.setImageResource(R.mipmap.icon_calendar_selected);
                            taskAllFragment.notifyFragmentUpdate(taskAllFragment, TaskAllFragment.TYPE_ALL_TASK_CALENDAR, null);
                            break;
                        case TaskAllFragment.TYPE_ALL_TASK_CALENDAR:
                            viewPager.setNoScroll(false);
                            titleCalendar.setImageResource(R.mipmap.ic_calendar);
                            taskAllFragment.notifyFragmentUpdate(taskAllFragment, TaskAllFragment.TYPE_ALL_TASK, null);
                            break;
                    }
                }
                break;
            case R.id.titleAction:
                TaskCreateActivity.launch(getContext(), null, null);
                break;
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    @Override
    public void onFragmentCallBack(Fragment fragment, int type, Bundle params) {
        if (fragment instanceof TaskMemberSelectDialogFragment && params != null) {
            Serializable serializable = params.getSerializable(KEY_FRAGMENT_RESULT);
            if (serializable instanceof TaskMemberEntity) {
                TaskMemberEntity taskMemberEntity = (TaskMemberEntity) serializable;
                ArrayList<String> ids = new ArrayList<>();
                ids.add(taskMemberEntity.userId);
                MyAllotTaskActivity.launch(getContext(), TaskOtherListFragment.SELECT_OTHER_TYPE, ids);
            }
        }
    }
}
