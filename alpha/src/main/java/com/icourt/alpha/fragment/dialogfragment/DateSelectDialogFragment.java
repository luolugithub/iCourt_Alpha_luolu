package com.icourt.alpha.fragment.dialogfragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.icourt.alpha.R;
import com.icourt.alpha.base.BaseDialogFragment;
import com.icourt.alpha.entity.bean.TaskReminderEntity;
import com.icourt.alpha.fragment.DateSelectFragment;
import com.icourt.alpha.fragment.ReminderFragment;
import com.icourt.alpha.interfaces.OnFragmentCallBackListener;
import com.icourt.alpha.utils.DensityUtil;

import java.util.Calendar;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Description   返回KEY_FRAGMENT_RESULT long时间戳
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/5/12
 * version 1.0.0
 */
public class DateSelectDialogFragment extends BaseDialogFragment implements OnFragmentCallBackListener, FragmentManager.OnBackStackChangedListener {

    public static final int SELECT_DATE_FINISH = 1;//选择到期日期完成
    public static final int SELECT_REMINDER = 2;//选择提醒
    public static final int SELECT_REMINDER_FINISH = 3;//选择提醒完成
    public static final int SELECT_RETASK = 4;//选择重复任务
    public static final int SELECT_RETASK_FINISH = 5;//选择重复任务完成

    Unbinder unbinder;
    Fragment currentFragment;
    Calendar selectedCalendar;
    TaskReminderEntity taskReminderEntity;
    String taskId;//任务id

    OnSelectReminderCallBlack onSelectReminderCallBlack;

    public static DateSelectDialogFragment newInstance(@Nullable Calendar calendar, TaskReminderEntity taskReminderEntity, String taskId) {
        DateSelectDialogFragment dateSelectDialogFragment = new DateSelectDialogFragment();
        Bundle args = new Bundle();
        args.putString("taskId", taskId);
        args.putSerializable("calendar", calendar);
        args.putSerializable("taskReminder", taskReminderEntity);
        dateSelectDialogFragment.setArguments(args);
        return dateSelectDialogFragment;
    }


    OnFragmentCallBackListener onFragmentCallBackListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            onFragmentCallBackListener = (OnFragmentCallBackListener) context;
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(R.layout.dialog_fragment_date_select, inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }


    @Override
    protected void initView() {
        Dialog dialog = getDialog();
        if (dialog != null) {
            Window window = dialog.getWindow();
            if (window != null) {
                window.setGravity(Gravity.BOTTOM);
                View decorView = window.getDecorView();
                if (decorView != null) {
                    int dp20 = DensityUtil.dip2px(getContext(), 20);
                    decorView.setPadding(dp20 / 2, 0, dp20 / 2, dp20);
                }
            }
        }
        selectedCalendar = (Calendar) getArguments().getSerializable("calendar");
        taskReminderEntity = (TaskReminderEntity) getArguments().getSerializable("taskReminder");
        taskId = getArguments().getString("taskId");
        getChildFragmentManager().addOnBackStackChangedListener(this);
        showFragment(DateSelectFragment.newInstance(selectedCalendar, taskReminderEntity, taskId));
    }

    private void showFragment(Fragment fragment) {
        currentFragment = addOrShowFragment(fragment, currentFragment, R.id.main_fl_content);
    }

    @Override
    protected Fragment addOrShowFragment(@NonNull Fragment targetFragment, Fragment currentFragment, @IdRes int containerViewId) {
        if (targetFragment == null) return currentFragment;
        if (targetFragment == currentFragment) return currentFragment;
        FragmentManager fm = getChildFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        if (fm.getBackStackEntryCount() > 0) {
            transaction.setCustomAnimations(
                    R.anim.fragment_slide_right_in, R.anim.fragment_slide_left_out,
                    R.anim.fragment_slide_left_in, R.anim.fragment_slide_right_out);
        }
        transaction.replace(containerViewId, targetFragment, String.valueOf(targetFragment.hashCode())).commitAllowingStateLoss();
        transaction.addToBackStack(String.valueOf(targetFragment.hashCode()));
        return targetFragment;
        // return super.addOrShowFragment(targetFragment, currentFragment, containerViewId);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onFragmentCallBack(Fragment fragment, int type, Bundle params) {
        if (params == null) return;
        if (getParentFragment() instanceof OnFragmentCallBackListener) {
            onFragmentCallBackListener = (OnFragmentCallBackListener) getParentFragment();
        }
        if (fragment instanceof DateSelectFragment) {
            if (type == SELECT_DATE_FINISH) {
                if (onFragmentCallBackListener != null) {
                    onFragmentCallBackListener.onFragmentCallBack(DateSelectDialogFragment.this, 0, params);
                    dismiss();
                }
            } else if (type == SELECT_REMINDER) {
                TaskReminderEntity taskReminderEntity = (TaskReminderEntity) params.getSerializable("taskReminder");
                long millis = params.getLong(KEY_FRAGMENT_RESULT);
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(millis);
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);
                int second = calendar.get(Calendar.SECOND);
                if (hour == 23 && minute == 59 && second == 59) {
                    showFragment(ReminderFragment.newInstance(taskReminderEntity, null));
                } else {
                    showFragment(ReminderFragment.newInstance(taskReminderEntity, calendar));
                }
            }
        } else if (fragment instanceof ReminderFragment) {
            if (type == SELECT_REMINDER_FINISH) {
                taskReminderEntity = (TaskReminderEntity) params.getSerializable("taskReminder");

                if (onSelectReminderCallBlack != null) {
                    onSelectReminderCallBlack.setReminderCallBlack(taskReminderEntity);
                }
            }
        }
    }

    @Override
    public void onBackStackChanged() {

    }

    public void setOnSelectReminderCallBlack(OnSelectReminderCallBlack onSelectReminderCallBlack) {
        this.onSelectReminderCallBlack = onSelectReminderCallBlack;
    }

    public interface OnSelectReminderCallBlack {
        void setReminderCallBlack(TaskReminderEntity taskReminderEntity);
    }

}
