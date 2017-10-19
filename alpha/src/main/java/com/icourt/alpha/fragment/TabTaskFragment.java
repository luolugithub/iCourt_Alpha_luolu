package com.icourt.alpha.fragment;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.icourt.alpha.R;
import com.icourt.alpha.activity.TaskCreateActivity;
import com.icourt.alpha.activity.TaskOtherActivity;
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
import com.icourt.alpha.view.tab.AlphaTabLayout;
import com.icourt.alpha.view.tab.AlphaTitleNavigatorAdapter;
import com.icourt.alpha.view.tab.pagertitleview.ScaleTransitionPagerTitleView;
import com.icourt.alpha.widget.dialog.BottomActionDialog;
import com.icourt.alpha.widget.popupwindow.TopMiddlePopup;

import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;

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
 * @author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/8
 * version 1.0.0
 */
public class TabTaskFragment extends BaseFragment implements OnFragmentCallBackListener, TopMiddlePopup.OnItemClickListener {

    @BindView(R.id.tabLayout)
    AlphaTabLayout tabLayout;
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
    /**
     * 用来显示未完成、已完成、已删除的任务列表的碎片
     */
    TaskAllFragment alltaskFragment;
    /**
     * 我关注的任务列表
     */
    TaskListFragment attentionTaskFragment;
    /**
     * 用来显示顶部未完成、已完成、已删除的弹出窗
     */
    TopMiddlePopup topMiddlePopup;
    /**
     * 存储弹出窗所需要的数据的集合
     */
    List<FilterDropEntity> dropEntities = new ArrayList<>();
    /**
     * 第一个Tab，用来显示未完成、已完成、已删除的tab。
     */
    ScaleTransitionPagerTitleView firstTabView;
    /**
     * 选择的筛选选项的position：0，未完成；1，已完成；2，已删除。
     */
    public int selectPosition = 0;
    /**
     * 切换时是否滚动，在'已完成和已删除'状态下，点击新任务提醒。
     */
    public boolean isAwayScroll = false;
    /**
     * 是否显示日历页面
     */
    public boolean isShowCalendar;

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
        //初始化任务的pop所要显示的数据
        setTaskPopCount("0", "0", "0");
        topMiddlePopup = new TopMiddlePopup(getContext(), DensityUtil.getWidthInDp(getContext()), (int) (DensityUtil.getHeightInPx(getContext()) - DensityUtil.dip2px(getContext(), 75)), this);
        topMiddlePopup.setMyItems(dropEntities);
        //获取任务的pop所要显示的数据
        getTasksStateCount();
        //默认选中未完成的筛选器
        selectPosition = 0;
        baseFragmentAdapter = new BaseFragmentAdapter(getChildFragmentManager());
        viewPager.setNoScroll(false);
        viewPager.setAdapter(baseFragmentAdapter);

        baseFragmentAdapter.bindData(true,
                Arrays.asList(
                        alltaskFragment = TaskAllFragment.newInstance(),
                        attentionTaskFragment = TaskListFragment.newInstance(TaskListFragment.TYPE_MY_ATTENTION, 0)));
        baseFragmentAdapter.bindTitle(true, Arrays.asList(getString(R.string.task_unfinished), getString(R.string.task_my_attention)));

        CommonNavigator commonNavigator = new CommonNavigator(getContext());
        AlphaTitleNavigatorAdapter indicatorAdapter = new AlphaTitleNavigatorAdapter() {

            @Override
            public IPagerTitleView getTitleView(Context context, int index) {
                IPagerTitleView titleView = super.getTitleView(context, index);
                if (index == 0 && titleView instanceof ScaleTransitionPagerTitleView) {
                    firstTabView = (ScaleTransitionPagerTitleView) titleView;
                    setFirstTabImage(false);
                    return firstTabView;
                }
                return titleView;
            }

            @Nullable
            @Override
            public CharSequence getTitle(int index) {
                if (index == 0 && topMiddlePopup != null && topMiddlePopup.getAdapter() != null) {
                    FilterDropEntity filterDropEntity = topMiddlePopup.getAdapter().getItem(selectPosition);
                    if (filterDropEntity != null) {
                        setFirstTabText(filterDropEntity.name, selectPosition);
                        return filterDropEntity.name;
                    }
                }
                return baseFragmentAdapter.getPageTitle(index);
            }

            @Override
            public int getCount() {
                return baseFragmentAdapter.getCount();
            }

            @Override
            public void onTabClick(View v, int pos) {
                //说明当前是第0个，并且点击了第0个，需要弹出筛选已完成、未完成、已删除的弹出窗。
                if (viewPager.getCurrentItem() == 0 && pos == 0) {
                    postDismissPop();
                    topMiddlePopup.show(titleView, dropEntities, selectPosition);
                    setFirstTabImage(true);
                    if (topMiddlePopup.isShowing()) {
                        getTasksStateCount();
                    }
                } else {
                    viewPager.setCurrentItem(pos, true);
                }
            }
        };
        commonNavigator.setAdapter(indicatorAdapter);
        tabLayout.setNavigator2(commonNavigator)
                .setupWithViewPager(viewPager);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                titleCalendar.setVisibility(View.GONE);
                //未完成、已完成、已删除
                if (position == 0) {
                    setFirstTabImage(false);
                    titleCalendar.setVisibility(selectPosition == 0 ? View.VISIBLE : View.GONE);
                } //我关注的
                else {
                    setFirstTabImage(false);
                    Bundle bundle = new Bundle();
                    bundle.putInt(TaskListFragment.STATE_TYPE, 0);
                    attentionTaskFragment.notifyFragmentUpdate(attentionTaskFragment, TaskListFragment.TYPE_MY_ATTENTION, bundle);
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
        if (tabLayout == null) {
            return;
        }
        if (firstTabView != null) {
            firstTabView.setText(content);
            selectPosition = position;
            topMiddlePopup.getAdapter().setSelectedPos(selectPosition);
        }
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
        //说明该任务状态是全部的任务状态
        if (stateType == 0) {
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
        callEnqueue(
                getApi().taskStateCountQuery(),
                new SimpleCallBack<TaskCountEntity>() {
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
     * @param isOpen 是否是打开状态
     */
    private void setFirstTabImage(boolean isOpen) {
        if (tabLayout != null) {
            if (firstTabView != null) {
                if (isOpen) {
                    Drawable drawable = ContextCompat.getDrawable(getActivity(), R.mipmap.task_dropup);
                    drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getMinimumHeight());
                    firstTabView.setCompoundDrawables(null, null, drawable, null);
                } else {
                    Drawable drawable = ContextCompat.getDrawable(getActivity(), R.mipmap.task_dropdown);
                    drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getMinimumHeight());
                    firstTabView.setCompoundDrawables(null, null, drawable, null);
                }
            }
        }
    }

    @OnClick({R.id.titleAction,
            R.id.titleAction2,
            R.id.titleCalendar})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.titleCalendar:
                if (!RAUtils.isLegal(RAUtils.DURATION_DEFAULT)) {
                    return;
                }
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
                        default:
                            break;
                    }
                }
                break;
            case R.id.titleAction:
                TaskCreateActivity.launch(getContext(), null, null);
                break;
            case R.id.titleAction2:
                List<String> titles = null;
                if (selectPosition != 2 || viewPager.getCurrentItem() == 1) {
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
                                    default:
                                        break;
                                }
                            }
                        }).show();
                break;
            default:
                super.onClick(v);
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
                            default:
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
        //从选择他人界面的回调
        if (fragment instanceof TaskMemberSelectDialogFragment && params != null) {
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
        //已删除的任务列表
        if (selectPosition == 2) {
            if (alltaskFragment != null
                    && alltaskFragment.currFragment != null
                    && alltaskFragment.currFragment instanceof TaskListFragment) {
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
        if (alltaskFragment != null) {
            Fragment currFragment = alltaskFragment.currFragment;
            if (currFragment != null && currFragment instanceof TaskListFragment) {
                TaskListFragment taskListFragment = (TaskListFragment) currFragment;
                int visibility = taskListFragment.nextTaskLayout.getVisibility();
                return visibility == View.VISIBLE;
            }
        }
        return false;
    }
}
