package com.icourt.alpha.fragment;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
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

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

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
public class TaskAllFragment extends BaseFragment {
    public static final int TYPE_ALL_TASK = 1;
    public static final int TYPE_ALL_TASK_CALENDAR = 2;

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

    private Fragment getFragment(int type) {
        Fragment fragment = fragmentSparseArray.get(type);
        if (fragment == null) {
            switch (type) {
                case TYPE_ALL_TASK:
                    putFragment(type, TaskListFragment.newInstance(0));
                    break;
                case TYPE_ALL_TASK_CALENDAR:
                    putFragment(type, TaskListCalendarFragment.newInstance());
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
        currFragment = addOrShowFragment(getFragment(TYPE_ALL_TASK), currFragment, R.id.main_fl_content);
    }

    @Override
    protected Fragment addOrShowFragment(@NonNull Fragment targetFragment, Fragment currentFragment, @IdRes int containerViewId) {
        if (targetFragment == null) return currentFragment;
        if (targetFragment == currentFragment) return currentFragment;
        FragmentManager fm = getChildFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        if (targetFragment instanceof TaskListCalendarFragment) {
            transaction.setCustomAnimations(
                    R.anim.fragment_slide_top_in, 0);
        } else {
            transaction.setCustomAnimations(0, R.anim.fragment_slide_top_out);
        }
        transaction.replace(containerViewId, targetFragment, String.valueOf(targetFragment.hashCode())).commitAllowingStateLoss();
        transaction.addToBackStack(String.valueOf(targetFragment.hashCode()));
        return targetFragment;
    }

    @Override
    public void notifyFragmentUpdate(Fragment targetFrgament, @ChildFragmentType int type, Bundle bundle) {
        super.notifyFragmentUpdate(targetFrgament, type, bundle);
        getArguments().putInt("childFragment", type);
        currFragment = addOrShowFragment(getFragment(type), currFragment, R.id.main_fl_content);
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
