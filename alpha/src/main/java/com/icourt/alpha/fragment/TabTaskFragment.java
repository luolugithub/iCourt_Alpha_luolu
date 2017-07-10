package com.icourt.alpha.fragment;

import android.os.Bundle;
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

import com.icourt.alpha.R;
import com.icourt.alpha.activity.MyAllotTaskActivity;
import com.icourt.alpha.activity.MyFinishTaskActivity;
import com.icourt.alpha.activity.TaskCreateActivity;
import com.icourt.alpha.adapter.baseadapter.BaseFragmentAdapter;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.base.BaseFragment;
import com.icourt.alpha.entity.bean.TaskEntity;
import com.icourt.alpha.entity.bean.TaskMemberEntity;
import com.icourt.alpha.fragment.dialogfragment.TaskMemberSelectDialogFragment;
import com.icourt.alpha.interfaces.OnFragmentCallBackListener;
import com.icourt.alpha.interfaces.OnTasksChangeListener;
import com.icourt.alpha.widget.dialog.BottomActionDialog;

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
public class TabTaskFragment extends BaseFragment implements OnFragmentCallBackListener, OnTasksChangeListener {
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
    OnCheckAllNewTaskListener onCheckAllNewTaskListener;
    @BindView(R.id.titleCalendar)
    ImageView titleCalendar;

    public static TabTaskFragment newInstance() {
        return new TabTaskFragment();
    }

    final ArrayList<TaskEntity.TaskItemEntity> taskItemEntityList = new ArrayList<>();

    public void setOnCheckAllNewTaskListener(OnCheckAllNewTaskListener onCheckAllNewTaskListener) {
        this.onCheckAllNewTaskListener = onCheckAllNewTaskListener;
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
        baseFragmentAdapter.bindTitle(true, Arrays.asList("全部", "新任务", "我关注的"));
        baseFragmentAdapter.bindData(true,
                Arrays.asList(
                        TaskAllFragment.newInstance(),
                        TaskListFragment.newInstance(1),
                        TaskListFragment.newInstance(2)));
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setTititleActionIcon(position);
                titleCalendar.setVisibility(View.GONE);
                if (position == 1) {
                    if (onCheckAllNewTaskListener != null)
                        onCheckAllNewTaskListener.onRefreshNewTask();
                } else if (position == 0) {
                    titleCalendar.setVisibility(taskItemEntityList.isEmpty() ? View.GONE : View.VISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        titleCalendar.setVisibility(View.GONE);
    }

    /**
     * 设置顶部icon
     *
     * @param position
     */
    private void setTititleActionIcon(int position) {
        switch (position) {
            case 0://全部
                titleAction.setImageResource(R.mipmap.header_icon_add);
                titleAction2.setImageResource(R.mipmap.header_icon_more);
                titleAction2.setVisibility(View.VISIBLE);
                break;
            case 1://新任务
                titleAction.setImageResource(R.mipmap.header_icon_add);
                titleAction2.setImageResource(R.mipmap.header_icon_checkall);
                titleAction2.setVisibility(View.GONE);
                break;
            case 2://我关注的
                titleAction.setImageResource(R.mipmap.header_icon_add);
                titleAction2.setImageResource(R.mipmap.header_icon_more);
                titleAction2.setVisibility(View.VISIBLE);
                break;
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
                Fragment item = baseFragmentAdapter.getItem(viewPager.getCurrentItem());
                if (item instanceof TaskAllFragment) {
                    TaskAllFragment taskAllFragment = (TaskAllFragment) item;
                    switch (taskAllFragment.getChildFragmentType()) {
                        case TaskAllFragment.TYPE_ALL_TASK:
                            titleCalendar.setImageResource(R.mipmap.ic_card_list);
                            taskAllFragment.notifyFragmentUpdate(taskAllFragment, TaskAllFragment.TYPE_ALL_TASK_CALENDAR, null);
                            break;
                        case TaskAllFragment.TYPE_ALL_TASK_CALENDAR:
                            titleCalendar.setImageResource(R.mipmap.ic_calendar);
                            taskAllFragment.notifyFragmentUpdate(taskAllFragment, TaskAllFragment.TYPE_ALL_TASK, null);
                            break;
                    }
                }
                break;
            case R.id.titleAction:
                TaskCreateActivity.launch(getContext(), null, null);
                break;
            case R.id.titleAction2:
                if (viewPager.getCurrentItem() != 1) {
//                new BottomActionDialog(getContext(), null, Arrays.asList("我分配的任务", "已完成的任务", "选择查看对象"), new BottomActionDialog.OnActionItemClickListener() {
                    new BottomActionDialog(getContext(), null, Arrays.asList("查看他人任务", "查看已完成的"), new BottomActionDialog.OnActionItemClickListener() {
                        @Override
                        public void onItemClick(BottomActionDialog dialog, BottomActionDialog.ActionItemAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
                            dialog.dismiss();
                            switch (position) {
//                            case 0:
//                                MyAllotTaskActivity.launch(getContext(), TaskOtherListFragment.MY_ALLOT_TYPE, null);
//                                break;
                                case 0:
                                    showMemberSelectDialogFragment();
                                    break;
                                case 1:
                                    MyFinishTaskActivity.launch(getContext());
                                    break;
                            }
                        }
                    }).show();
                } else {
                    if (onCheckAllNewTaskListener != null)
                        onCheckAllNewTaskListener.onCheckAll();
                }
                break;
        }
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

    @Override
    public void onTasksChanged(List<TaskEntity.TaskItemEntity> taskItemEntities) {
        if (taskItemEntities != null) {
            taskItemEntityList.clear();
            taskItemEntityList.addAll(taskItemEntities);

            if (viewPager.getCurrentItem() == 0) {
                titleCalendar.setVisibility(taskItemEntityList.isEmpty() ? View.GONE : View.VISIBLE);
            }
        }
    }

    @Override
    public void onTaskChanged(TaskEntity.TaskItemEntity taskItemEntity) {

    }


    public interface OnCheckAllNewTaskListener {
        void onCheckAll();

        void onRefreshNewTask();
    }

    /**
     * 显示隐藏'我知道了'按钮
     *
     * @param isShow
     */
    public void showOrHiddeTitleAction2(boolean isShow) {
        if (viewPager.getCurrentItem() == 1)
            titleAction2.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }
}
