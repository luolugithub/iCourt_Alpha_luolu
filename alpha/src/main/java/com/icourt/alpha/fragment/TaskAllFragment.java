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
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/7/8
 * version 1.0.0
 */
public class TaskAllFragment extends BaseFragment implements OnTasksChangeListener {
    public static final int TYPE_ALL_TASK = 1;
    public static final int TYPE_ALL_TASK_CALENDAR = 2;
    final ArrayList<TaskEntity.TaskItemEntity> taskItemEntityList = new ArrayList<>();

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

    @IntDef({TYPE_ALL_TASK, TYPE_ALL_TASK_CALENDAR})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ChildFragmentType {
    }


    @BindView(R.id.main_fl_content)
    FrameLayout mainFlContent;
    Unbinder unbinder;


    public static TaskAllFragment newInstance() {
        TaskAllFragment taskAllFragment = new TaskAllFragment();
        Bundle args = new Bundle();
        args.putInt("childFragment", TYPE_ALL_TASK);
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

    Fragment currFragment;
    final SparseArray<Fragment> fragmentSparseArray = new SparseArray<>();

    private Fragment getFragment(int type, int stateType) {
        if (type == TYPE_ALL_TASK) {
            return TaskListFragment.newInstance(0, stateType);
        }
        Fragment fragment = fragmentSparseArray.get(type);
        if (fragment == null) {
            switch (type) {
                case TYPE_ALL_TASK:
                    putFragment(type, TaskListFragment.newInstance(0, stateType));
                    break;
                case TYPE_ALL_TASK_CALENDAR:
                    // putFragment(type, TaskListCalendarFragment.newInstance(taskItemEntityList));
                    putFragment(type, TaskListCalendarFragment.newInstance(null));
                    break;
            }
        }
        return fragmentSparseArray.get(type);
    }

    private void putFragment(int type, Fragment fragment) {
        fragmentSparseArray.put(type, fragment);
    }

    @Override
    protected void initView() {
        currFragment = addOrShowFragment(getFragment(TYPE_ALL_TASK, 0), currFragment, R.id.main_fl_content);
    }


    @Override
    protected Fragment addOrShowFragment(@NonNull Fragment targetFragment, Fragment currentFragment, @IdRes int containerViewId) {
        return addOrShowFragmentAnim(targetFragment, currentFragment, containerViewId, true);
    }

    protected Fragment addOrShowFragmentAnim(@NonNull Fragment targetFragment, Fragment currentFragment, @IdRes int containerViewId, boolean isAnim) {
        if (targetFragment == null) return currentFragment;
        if (targetFragment == currentFragment) return currentFragment;
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

    @Override
    public void notifyFragmentUpdate(Fragment targetFrgament, @ChildFragmentType int type, Bundle bundle) {
        super.notifyFragmentUpdate(targetFrgament, type, bundle);
        getArguments().putInt("childFragment", type);
        int stateType = 0;
        if (bundle != null) {
            stateType = bundle.getInt("stateType");
        }
        currFragment = addOrShowFragmentAnim(
                getFragment(type, stateType),
                currFragment,
                R.id.main_fl_content,
                type == TYPE_ALL_TASK_CALENDAR);

        switch (type) {
            case TYPE_ALL_TASK_CALENDAR:
                updateCalendarRefresh();
                break;
            case TYPE_ALL_TASK:
//                BaseFragment fragment = (BaseFragment) getFragment(TYPE_ALL_TASK);
//                if (fragment != null) fragment.notifyFragmentUpdate(fragment, 100, bundle);
                break;
        }
    }

    /**
     * 更新日历刷新
     */
    private void updateCalendarRefresh() {
        Bundle args = new Bundle();
        args.putSerializable(KEY_FRAGMENT_RESULT, taskItemEntityList);
        Fragment fragment = getFragment(TYPE_ALL_TASK_CALENDAR, 0);
        ((INotifyFragment) fragment).notifyFragmentUpdate(fragment, 0, args);
    }

    @ChildFragmentType
    public int getChildFragmentType() {
        switch (getArguments().getInt("childFragment", TYPE_ALL_TASK)) {
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
