package com.icourt.alpha.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.icourt.alpha.R;
import com.icourt.alpha.activity.MyAllotTaskActivity;
import com.icourt.alpha.activity.TaskCreateActivity;
import com.icourt.alpha.adapter.baseadapter.BaseFragmentAdapter;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.base.BaseFragment;
import com.icourt.alpha.entity.bean.FilterDropEntity;
import com.icourt.alpha.entity.bean.TaskMemberEntity;
import com.icourt.alpha.fragment.dialogfragment.TaskMemberSelectDialogFragment;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.interfaces.OnFragmentCallBackListener;
import com.icourt.alpha.utils.DensityUtil;
import com.icourt.alpha.utils.RAUtils;
import com.icourt.alpha.view.NoScrollViewPager;
import com.icourt.alpha.widget.dialog.BottomActionDialog;
import com.icourt.alpha.widget.popupwindow.TopMiddlePopup;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Description  任务tab页面
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/8
 * version 1.0.0
 */
public class TabTaskFragment extends BaseFragment implements OnFragmentCallBackListener, TopMiddlePopup.OnItemClickListener {

    public int select_position = 0;//选择的筛选选项的position：0，未完成；1，已完成；2，已删除。
    public boolean isAwayScroll = false; //切换时是否滚动，在'已完成和已删除'状态下，点击新任务提醒。
    public boolean isShowCalendar;//是否显示日历页面

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
    @BindView(R.id.titleAction2)
    ImageView titleAction2;
    @BindView(R.id.titleCalendar)
    ImageView titleCalendar;
    TaskListFragment2 attentionTaskFragment;
    TaskAllFragment alltaskFragment;
    TopMiddlePopup topMiddlePopup;
    List<FilterDropEntity> dropEntities = new ArrayList<>();
    FilterDropEntity doingEntity = new FilterDropEntity("未完成", "0", 0);//未完成
    FilterDropEntity doneEntity = new FilterDropEntity("已完成", "0", 1);//已完成
    FilterDropEntity deleteEntity = new FilterDropEntity("已删除", "0", 3);//已删除

    private Handler handler = new Handler();

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
        select_position = 0;
        baseFragmentAdapter = new BaseFragmentAdapter(getChildFragmentManager());
        viewPager.setNoScroll(false);
        viewPager.setAdapter(baseFragmentAdapter);
        tabLayout.setupWithViewPager(viewPager);
//        baseFragmentAdapter.bindTitle(true, Arrays.asList("全部", "我关注的"));
        baseFragmentAdapter.bindData(true,
                Arrays.asList(
                        alltaskFragment = TaskAllFragment.newInstance(),
                        attentionTaskFragment = TaskListFragment2.newInstance(TaskListFragment2.TYPE_MY_ATTENTION, 0)));

        topMiddlePopup = new TopMiddlePopup(getContext(), DensityUtil.getWidthInDp(getContext()), (int) (DensityUtil.getHeightInPx(getContext()) - DensityUtil.dip2px(getContext(), 75)), this);
        dropEntities.add(doingEntity);
        dropEntities.add(doneEntity);
        dropEntities.add(deleteEntity);
        topMiddlePopup.setMyItems(dropEntities);
        getTasksStateCount();
        for (int i = 0; i < baseFragmentAdapter.getCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            tab.setCustomView(R.layout.task_unfinish_tab_custom_view);
            TextView titleTv = tab.getCustomView().findViewById(R.id.tab_custom_title_tv);
            ImageView downIv = tab.getCustomView().findViewById(R.id.tab_custom_title_iv);
            switch (i) {
                case 0:
                    titleTv.setTextColor(0xFF313131);
                    titleTv.setText("未完成");
                    downIv.setVisibility(View.VISIBLE);
                    tab.getCustomView().setOnClickListener(new OnTabClickListener());
                    break;
                case 1:
                    titleTv.setTextColor(0xFF979797);
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
                if (tabLayout.getTabAt(0).getCustomView() != null && tabLayout.getTabAt(1).getCustomView() != null) {
                    TextView titleTv_0 = tabLayout.getTabAt(0).getCustomView().findViewById(R.id.tab_custom_title_tv);
                    TextView titleTv_1 = tabLayout.getTabAt(1).getCustomView().findViewById(R.id.tab_custom_title_tv);
                    switch (position) {
                        case 0:
                            titleTv_0.setTextColor(0xFF313131);
                            titleTv_1.setTextColor(0xFF979797);
                            titleCalendar.setVisibility(select_position == 0 ? View.VISIBLE : View.GONE);
                            if (topMiddlePopup != null && topMiddlePopup.getAdapter() != null) {
                                FilterDropEntity filterDropEntity = topMiddlePopup.getAdapter().getItem(select_position);
                                if (filterDropEntity != null) {
                                    setFirstTabText(filterDropEntity.name, select_position);
                                    updateListData(filterDropEntity.stateType);
                                }
                            }
                            break;
                        case 1://我关注的
                            titleTv_0.setTextColor(0xFF979797);
                            titleTv_1.setTextColor(0xFF313131);
                            setFirstTabImage(false);
                            Bundle bundle = new Bundle();
                            bundle.putInt(TaskListFragment2.STATE_TYPE, 0);
                            attentionTaskFragment.notifyFragmentUpdate(attentionTaskFragment, TaskListFragment2.TYPE_MY_ATTENTION, bundle);
                            break;
                    }
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
        if (select_position != position) {
            FilterDropEntity filterDropEntity = (FilterDropEntity) adapter.getItem(position);
            setFirstTabText(filterDropEntity.name, position);
            updateListData(filterDropEntity.stateType);
        }
        titleCalendar.setVisibility(position == 0 ? View.VISIBLE : View.GONE);
    }

    /**
     * 更新全部任务列表
     *
     * @param stateType
     */
    public void updateListData(int stateType) {
        Bundle bundle = new Bundle();
        bundle.putInt(TaskListFragment2.STATE_TYPE, stateType);
        int type = TaskAllFragment.TYPE_ALL_TASK;
        if (stateType == 0) {//说明该任务状态是全部的任务状态
            titleCalendar.setVisibility(View.VISIBLE);
            if (isShowCalendar) {
                type = TaskAllFragment.TYPE_ALL_TASK_CALENDAR;
                viewPager.setNoScroll(true);
                titleCalendar.setImageResource(R.mipmap.icon_calendar_selected);
            } else {
                viewPager.setNoScroll(false);
                titleCalendar.setImageResource(R.mipmap.ic_calendar);
            }
        }
        alltaskFragment.notifyFragmentUpdate(alltaskFragment, type, bundle);
    }

    private class OnTabClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            if (tabLayout.getTabAt(0) != null) {
                if (view.isSelected()) {
                    postDismissPop();
                    topMiddlePopup.show(titleView, dropEntities, select_position);
                    setFirstTabImage(true);
                    if (topMiddlePopup.isShowing()) {
                        getTasksStateCount();
                    }
                } else {
                    tabLayout.getTabAt(0).select();
                }
            }
        }
    }

    /**
     * 隐藏pop
     */
    private void postDismissPop() {
        handler.removeCallbacksAndMessages(null);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (topMiddlePopup != null) {
                    if (topMiddlePopup.isShowing()) {
                        if (!isVisible()) {
                            topMiddlePopup.dismiss();
                        }
                    }
                }
            }
        }, 100);
    }

    /**
     * 获取各个状态的任务数量
     */
    private void getTasksStateCount() {
        getApi().taskStateCountQuery().enqueue(new SimpleCallBack<JsonElement>() {
            @Override
            public void onSuccess(Call<ResEntity<JsonElement>> call, Response<ResEntity<JsonElement>> response) {
                JsonElement jsonElement = response.body().result;
                if (jsonElement != null) {
                    if (jsonElement.isJsonObject()) {
                        JsonObject jsonObject = jsonElement.getAsJsonObject();
                        if (jsonObject != null) {
                            doingEntity.count = jsonObject.get("doingCount").getAsString();
                            doneEntity.count = jsonObject.get("doneCount").getAsString();
                            deleteEntity.count = jsonObject.get("deletedCount").getAsString();
                            dropEntities.clear();
                            dropEntities.add(doingEntity);
                            dropEntities.add(doneEntity);
                            dropEntities.add(deleteEntity);
                            if (topMiddlePopup != null && topMiddlePopup.isShowing()) {
                                if (topMiddlePopup.getAdapter() != null) {
                                    topMiddlePopup.getAdapter().bindData(true, dropEntities);
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ResEntity<JsonElement>> call, Throwable t) {
                super.onFailure(call, t);
            }
        });
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
            R.id.titleAction2,
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
                            isShowCalendar = true;
                            viewPager.setNoScroll(true);
                            titleCalendar.setImageResource(R.mipmap.icon_calendar_selected);
                            taskAllFragment.notifyFragmentUpdate(taskAllFragment, TaskAllFragment.TYPE_ALL_TASK_CALENDAR, null);
                            break;
                        case TaskAllFragment.TYPE_ALL_TASK_CALENDAR:
                            isShowCalendar = false;
                            viewPager.setNoScroll(false);
                            titleCalendar.setImageResource(R.mipmap.ic_calendar);
                            if (topMiddlePopup.getAdapter() != null) {
                                FilterDropEntity filterDropEntity = topMiddlePopup.getAdapter().getItem(select_position);
                                int stateType = 0;
                                if (filterDropEntity != null) {
                                    stateType = filterDropEntity.stateType;
                                }
                                Bundle bundle = new Bundle();
                                bundle.putInt(TaskListFragment2.STATE_TYPE, stateType);
                                taskAllFragment.notifyFragmentUpdate(taskAllFragment, TaskAllFragment.TYPE_ALL_TASK, bundle);
                            }
                            break;
                    }
                }
                break;
            case R.id.titleAction:
                TaskCreateActivity.launch(getContext(), null, null);
                break;
            case R.id.titleAction2:
                List<String> titles = null;
                if (select_position != 2 || tabLayout.getSelectedTabPosition() == 1) {
                    titles = Arrays.asList("查看他人任务");
                } else {
                    titles = Arrays.asList("查看他人任务", "清空所有已删除任务");
                }
                new BottomActionDialog(getContext(),
                        null,
                        titles,
                        1,
                        0xFFFF0000,
                        new BottomActionDialog.OnActionItemClickListener() {
                            @Override
                            public void onItemClick(BottomActionDialog dialog, BottomActionDialog.ActionItemAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
                                dialog.dismiss();
                                switch (position) {
                                    case 0:
                                        showMemberSelectDialogFragment();
                                        break;
                                    case 1:
                                        showTwiceSureDialog();
                                        break;
                                }
                            }
                        }).show();
                break;
        }
    }

    /**
     * 显示二次确认对话框
     */
    private void showTwiceSureDialog() {
        new BottomActionDialog(getContext(),
                "该操作不可恢复，确定清空？",
                Arrays.asList("确定"),
                0,
                0xFFFF0000,
                new BottomActionDialog.OnActionItemClickListener() {
                    @Override
                    public void onItemClick(BottomActionDialog dialog, BottomActionDialog.ActionItemAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
                        dialog.dismiss();
                        switch (position) {
                            case 0:
                                clearAllDeletedTask();
                                break;
                        }
                    }
                }).show();
    }

    /**
     * 展示选择成员对话框
     */
    public void showMemberSelectDialogFragment() {
        String tag = TaskMemberSelectDialogFragment.class.getSimpleName();
        FragmentTransaction mFragTransaction = getChildFragmentManager().beginTransaction();
        Fragment fragment = getChildFragmentManager().findFragmentByTag(tag);
        if (fragment != null) {
            mFragTransaction.remove(fragment);
        }
        TaskMemberSelectDialogFragment.newInstance()
                .show(mFragTransaction, tag);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
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

    /**
     * 清空所有已删除的任务
     */
    private void clearAllDeletedTask() {
        if (select_position == 2) {
            if (alltaskFragment.currFragment instanceof TaskListFragment2) {
                TaskListFragment2 fragment = (TaskListFragment2) alltaskFragment.currFragment;
                fragment.clearAllDeletedTask();
            }
        }
    }

    /**
     * 判断'下一个'view是否显示
     *
     * @return
     */
    public boolean isShowingNextTaskView() {
        Fragment currFragment = alltaskFragment.currFragment;
        if (currFragment instanceof TaskListFragment2) {
            TaskListFragment2 taskListFragment = (TaskListFragment2) currFragment;
            int visibility = taskListFragment.nextTaskCardview.getVisibility();
            return (visibility == View.GONE || visibility == View.INVISIBLE);
        }
        return false;
    }
}
