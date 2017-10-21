package com.icourt.alpha.fragment;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.icourt.alpha.R;
import com.icourt.alpha.base.BaseFragment;
import com.icourt.alpha.constants.TaskConfig;
import com.icourt.alpha.entity.bean.TaskEntity;
import com.icourt.alpha.interfaces.INotifyFragment;
import com.icourt.alpha.interfaces.OnTasksChangeListener;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Description  所有任务tab页面
 * Company Beijing icourt
 *
 * @author youxuan  E-mail:xuanyouwu@163.com
 *         date createTime：2017/7/8
 *         version 1.0.0
 */
public class TaskAllFragment extends BaseFragment implements OnTasksChangeListener {
    /**
     * 用来传递子Fragment的tag。
     */
    private static final String CHILD_FRAGMENT = "childFragment";
    /**
     * 任务列表
     */
    public static final int TYPE_ALL_TASK = 1;
    /**
     * 带日历的任务列表
     */
    public static final int TYPE_ALL_TASK_CALENDAR = 2;

    @IntDef({TYPE_ALL_TASK, TYPE_ALL_TASK_CALENDAR})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ChildFragmentType {
    }

    Unbinder unbinder;

    @BindView(R.id.main_fl_content)
    FrameLayout mainFlContent;
    /**
     * 用来记录当前显示的Fragment的任务列表数据。
     */
    ArrayList<TaskEntity.TaskItemEntity> taskItemEntityList = new ArrayList<>();
    /**
     * 当前的Fragment
     */
    Fragment currFragment;
    /**
     * 用来缓存Fragment的集合。
     */
    SparseArray<Fragment> fragmentSparseArray = new SparseArray<>();

    public static TaskAllFragment newInstance() {
        TaskAllFragment taskAllFragment = new TaskAllFragment();
        Bundle args = new Bundle();
        //默认初始化是任务列表
        args.putInt(CHILD_FRAGMENT, TYPE_ALL_TASK);
        taskAllFragment.setArguments(args);
        return taskAllFragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(R.layout.fragment_task_all, inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void initView() {
        currFragment = addOrShowFragment(getFragment(TYPE_ALL_TASK, TaskConfig.TASK_STATETYPE_UNFINISH), currFragment, R.id.main_fl_content);
    }

    /**
     * 获取要显示的子Fragment
     *
     * @param type      获取的子Fragment的type
     * @param stateType 子Fragment的状态 -1，全部任务；0，未完成；1，已完成；3，已删除。
     * @return
     */
    private Fragment getFragment(@ChildFragmentType int type, @TaskConfig.TaskStateType int stateType) {
        //如果是任务列表的话，每次都刷新。
        if (type == TYPE_ALL_TASK) {
            return TaskListFragment.newInstance(TaskListFragment.TYPE_ALL, stateType);
        }
        Fragment fragment = fragmentSparseArray.get(type);
        if (fragment == null) {
            switch (type) {
                case TYPE_ALL_TASK:
                    putFragment(type, TaskListFragment.newInstance(TaskListFragment.TYPE_ALL, stateType));
                    break;
                case TYPE_ALL_TASK_CALENDAR:
                    putFragment(type, TaskListCalendarFragment.newInstance(null));
                    break;
                default:
                    break;
            }
        }
        return fragmentSparseArray.get(type);
    }

    /**
     * 将Fragment缓存
     *
     * @param type
     * @param fragment
     */
    private void putFragment(int type, Fragment fragment) {
        fragmentSparseArray.put(type, fragment);
    }

    /**
     * 刷新任务列表，如果当前显示的列表是未完成／已完成／已删除的任务，刷新列表
     */
    public void upDateTaskList() {
        if (currFragment != null && currFragment instanceof TaskListFragment) {
            ((TaskListFragment) currFragment).getData(true);
        }
    }

    /**
     * 添加/显示Fragment
     *
     * @param targetFragment  将要添加／显示的fragment
     * @param currentFragment 正在显示的fragment
     * @param containerViewId 替换的viewid
     * @return
     */
    @Override
    protected Fragment addOrShowFragment(@NonNull Fragment targetFragment, Fragment currentFragment, @IdRes int containerViewId) {
        return addOrShowFragmentAnim(targetFragment, currentFragment, containerViewId, true);
    }

    protected Fragment addOrShowFragmentAnim(@NonNull Fragment targetFragment, Fragment currentFragment, @IdRes int containerViewId, boolean isAnim) {
        if (targetFragment == null) {
            return currentFragment;
        }
        if (targetFragment == currentFragment) {
            return currentFragment;
        }
        FragmentManager fm = getChildFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        if (isAnim) {
            if (targetFragment instanceof TaskListCalendarFragment) {
                transaction.setCustomAnimations(
                        R.anim.fragment_slide_top_in, 0);
            } else {
                transaction.setCustomAnimations(0, R.anim.fragment_slide_top_out);
            }
        }
        transaction.replace(containerViewId, targetFragment, String.valueOf(targetFragment.hashCode())).commitAllowingStateLoss();
        transaction.addToBackStack(null);
        return targetFragment;
    }

    /**
     * 更新Fragment
     *
     * @param targetFrgament
     * @param type
     * @param bundle
     */
    @Override
    public void notifyFragmentUpdate(Fragment targetFrgament, @ChildFragmentType int type, Bundle bundle) {
        super.notifyFragmentUpdate(targetFrgament, type, bundle);
        getArguments().putInt(CHILD_FRAGMENT, type);
        int stateType = TaskConfig.TASK_STATETYPE_UNFINISH;
        if (bundle != null) {
            stateType = TaskConfig.convert2TaskStateType(bundle.getInt(TaskListFragment.STATE_TYPE));
        }
        currFragment = addOrShowFragmentAnim(
                getFragment(type, stateType),
                currFragment,
                R.id.main_fl_content,
                type == TYPE_ALL_TASK_CALENDAR);

        switch (type) {
            //说明是更新日历的任务列表
            case TYPE_ALL_TASK_CALENDAR:
                updateCalendarRefresh();
                break;
            //说明是更新未完成／已完成／已取消的任务列表
            case TYPE_ALL_TASK:
                break;
            default:
                break;
        }
    }

    @Override
    public void onTasksChanged(List<TaskEntity.TaskItemEntity> taskItemEntities) {
        if (taskItemEntities != null) {
            //数据发生改变 替换
            if (taskItemEntities.hashCode() != taskItemEntityList.hashCode()) {
                taskItemEntityList.clear();
                taskItemEntityList.addAll(taskItemEntities);
                updateCalendarRefresh();
            }
        }
    }

    @Override
    public void onTaskChanged(TaskEntity.TaskItemEntity taskItemEntity) {

    }

    /**
     * 更新日历刷新
     */
    private void updateCalendarRefresh() {
        Bundle args = new Bundle();
        args.putSerializable(KEY_FRAGMENT_RESULT, taskItemEntityList);
        Fragment fragment = getFragment(TYPE_ALL_TASK_CALENDAR, TaskConfig.TASK_STATETYPE_UNFINISH);
        ((INotifyFragment) fragment).notifyFragmentUpdate(fragment, 0, args);
    }

    /**
     * 获取当前Fragment的type
     *
     * @return
     */
    @ChildFragmentType
    public int getChildFragmentType() {
        switch (getArguments().getInt(CHILD_FRAGMENT, TYPE_ALL_TASK)) {
            case TYPE_ALL_TASK:
                return TYPE_ALL_TASK;
            case TYPE_ALL_TASK_CALENDAR:
                return TYPE_ALL_TASK_CALENDAR;
            default:
                return TYPE_ALL_TASK;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
