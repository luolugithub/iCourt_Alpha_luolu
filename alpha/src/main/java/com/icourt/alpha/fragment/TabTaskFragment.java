package com.icourt.alpha.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
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

import com.icourt.alpha.R;
import com.icourt.alpha.activity.TaskOtherActivity;
import com.icourt.alpha.activity.TaskCreateActivity;
import com.icourt.alpha.adapter.baseadapter.BaseFragmentAdapter;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.base.BaseFragment;
import com.icourt.alpha.entity.bean.FilterDropEntity;
import com.icourt.alpha.entity.bean.TaskCountEntity;
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

    @BindView(R.id.tabLayout)
    TabLayout tabLayout;
    @BindView(R.id.titleAction)
    ImageView titleAction;
    @BindView(R.id.titleView)
    AppBarLayout titleView;
    @BindView(R.id.viewPager)
    NoScrollViewPager viewPager;
    @BindView(R.id.titleAction2)
    ImageView titleAction2;
    @BindView(R.id.titleCalendar)
    ImageView titleCalendar;

    Unbinder unbinder;

    BaseFragmentAdapter baseFragmentAdapter;
    TaskAllFragment alltaskFragment; //用来显示未完成、已完成、已删除的任务列表的碎片
    TaskListFragment attentionTaskFragment; //我关注的任务列表
    TopMiddlePopup topMiddlePopup;//用来显示顶部未完成、已完成、已删除的弹出窗
    List<FilterDropEntity> dropEntities = new ArrayList<>();//存储弹出窗所需要的数据的集合

    public int selectPosition = 0;//选择的筛选选项的position：0，未完成；1，已完成；2，已删除。
    public boolean isAwayScroll = false; //切换时是否滚动，在'已完成和已删除'状态下，点击新任务提醒。
    public boolean isShowCalendar;//是否显示日历页面

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

        setTaskPopCount("0", "0", "0");//初始化任务的pop所要显示的数据
        topMiddlePopup = new TopMiddlePopup(getContext(), DensityUtil.getWidthInDp(getContext()), (int) (DensityUtil.getHeightInPx(getContext()) - DensityUtil.dip2px(getContext(), 75)), this);
        topMiddlePopup.setMyItems(dropEntities);
        getTasksStateCount();//获取任务的pop所要显示的数据
        selectPosition = 0;//默认选中未完成的筛选器

        baseFragmentAdapter = new BaseFragmentAdapter(getChildFragmentManager());
        viewPager.setNoScroll(false);
        viewPager.setAdapter(baseFragmentAdapter);
        tabLayout.setupWithViewPager(viewPager);
        baseFragmentAdapter.bindData(true,
                Arrays.asList(
                        alltaskFragment = TaskAllFragment.newInstance(),
                        attentionTaskFragment = TaskListFragment.newInstance(TaskListFragment.TYPE_MY_ATTENTION, 0)));


        for (int i = 0; i < baseFragmentAdapter.getCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            tab.setCustomView(R.layout.task_unfinish_tab_custom_view);
            TextView titleTv = tab.getCustomView().findViewById(R.id.tab_custom_title_tv);
            ImageView downIv = tab.getCustomView().findViewById(R.id.tab_custom_title_iv);
            switch (i) {
                case 0:
                    titleTv.setTextColor(0xFF313131);
                    titleTv.setText(getString(R.string.task_unfinished));
                    downIv.setVisibility(View.VISIBLE);
                    tab.getCustomView().setOnClickListener(new OnTabClickListener());
                    break;
                case 1:
                    titleTv.setTextColor(0xFF979797);
                    titleTv.setText(getString(R.string.task_my_attention));
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
                        case 0://未完成、已完成、已删除
                            titleTv_0.setTextColor(0xFF313131);
                            titleTv_1.setTextColor(0xFF979797);
                            titleCalendar.setVisibility(selectPosition == 0 ? View.VISIBLE : View.GONE);
                            if (topMiddlePopup != null && topMiddlePopup.getAdapter() != null) {
                                FilterDropEntity filterDropEntity = topMiddlePopup.getAdapter().getItem(selectPosition);
                                if (filterDropEntity != null) {
                                    setFirstTabText(filterDropEntity.name, selectPosition);
                                    updateListData(filterDropEntity.stateType);
                                }
                            }
                            break;
                        case 1://我关注的
                            titleTv_0.setTextColor(0xFF979797);
                            titleTv_1.setTextColor(0xFF313131);
                            setFirstTabImage(false);
                            Bundle bundle = new Bundle();
                            bundle.putInt(TaskListFragment.STATE_TYPE, 0);
                            attentionTaskFragment.notifyFragmentUpdate(attentionTaskFragment, TaskListFragment.TYPE_MY_ATTENTION, bundle);
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
     * 设置弹出切换任务列表的pop，并且显示对应的数量
     *
     * @param doingCount  未完成的任务的数量
     * @param doneCount   已完成的任务的数量
     * @param deleteCount 已删除的任务的数量
     */
    private void setTaskPopCount(@NonNull String doingCount, @NonNull String doneCount, @NonNull String deleteCount) {
        dropEntities.clear();
        FilterDropEntity doingEntity = new FilterDropEntity(getString(R.string.task_unfinished), doingCount, 0);
        FilterDropEntity doneEntity = new FilterDropEntity(getString(R.string.task_finished), doneCount, 1);
        FilterDropEntity deleteEntity = new FilterDropEntity(getString(R.string.task_deleted), deleteCount, 3);
        dropEntities.add(doingEntity);
        dropEntities.add(doneEntity);
        dropEntities.add(deleteEntity);
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
        selectPosition = position;
        topMiddlePopup.getAdapter().setSelectedPos(selectPosition);
    }

    @Override
    public void onItemClick(TopMiddlePopup topMiddlePopup, BaseRecyclerAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
        topMiddlePopup.dismiss();
        if (selectPosition != position) {
            FilterDropEntity filterDropEntity = (FilterDropEntity) adapter.getItem(position);
            setFirstTabText(filterDropEntity.name, position);
            updateListData(filterDropEntity.stateType);
        }
        titleCalendar.setVisibility(position == 0 ? View.VISIBLE : View.GONE);
    }

    /**
     * 更新全部任务列表
     *
     * @param stateType -1，全部任务；0，未完成；1，已完成；3，已删除。
     */
    public void updateListData(int stateType) {
        Bundle bundle = new Bundle();
        bundle.putInt(TaskListFragment.STATE_TYPE, stateType);
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
                    topMiddlePopup.show(titleView, dropEntities, selectPosition);
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
     * 获取各个状态的任务数量（未完成／已完成／已删除）
     */
    private void getTasksStateCount() {
        getApi().taskStateCountQuery().enqueue(new SimpleCallBack<TaskCountEntity>() {
            @Override
            public void onSuccess(Call<ResEntity<TaskCountEntity>> call, Response<ResEntity<TaskCountEntity>> response) {
                TaskCountEntity taskCountEntity = response.body().result;
                if (taskCountEntity != null) {
                    setTaskPopCount(taskCountEntity.doingCount, taskCountEntity.doneCount, taskCountEntity.deletedCount);
                    if (topMiddlePopup != null && topMiddlePopup.isShowing()) {
                        if (topMiddlePopup.getAdapter() != null) {
                            topMiddlePopup.getAdapter().bindData(true, dropEntities);
                        }
                    }
                }
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
                                FilterDropEntity filterDropEntity = topMiddlePopup.getAdapter().getItem(selectPosition);
                                int stateType = 0;
                                if (filterDropEntity != null) {
                                    stateType = filterDropEntity.stateType;
                                }
                                Bundle bundle = new Bundle();
                                bundle.putInt(TaskListFragment.STATE_TYPE, stateType);
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
                if (selectPosition != 2 || tabLayout.getSelectedTabPosition() == 1) {
                    titles = Arrays.asList(getString(R.string.task_look_others_task));
                } else {
                    titles = Arrays.asList(getString(R.string.task_look_others_task), getString(R.string.task_clear_deleted_task));
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
                getString(R.string.task_can_not_revert),
                Arrays.asList(getString(R.string.task_confirm)),
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

    /**
     * 跳转到其他选择碎片之后的回调，比如：选择查看其他人的任务所弹出的选择联系人的fragment。
     *
     * @param fragment
     * @param type
     * @param params
     */
    @Override
    public void onFragmentCallBack(Fragment fragment, int type, Bundle params) {
        if (fragment instanceof TaskMemberSelectDialogFragment && params != null) {//从选择他人界面的回调
            Serializable serializable = params.getSerializable(KEY_FRAGMENT_RESULT);
            if (serializable instanceof TaskMemberEntity) {
                TaskMemberEntity taskMemberEntity = (TaskMemberEntity) serializable;
                ArrayList<String> ids = new ArrayList<>();
                ids.add(taskMemberEntity.userId);
                TaskOtherActivity.launch(getContext(), TaskOtherListFragment.SELECT_OTHER_TYPE, ids);
            }
        }
    }

    /**
     * 清空所有已删除的任务
     */
    private void clearAllDeletedTask() {
        if (selectPosition == 2) {
            if (alltaskFragment.currFragment instanceof TaskListFragment) {
                TaskListFragment fragment = (TaskListFragment) alltaskFragment.currFragment;
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
        if (currFragment instanceof TaskListFragment) {
            TaskListFragment taskListFragment = (TaskListFragment) currFragment;
            int visibility = taskListFragment.nextTaskCardview.getVisibility();
            return (visibility == View.GONE || visibility == View.INVISIBLE);
        }
        return false;
    }
}
