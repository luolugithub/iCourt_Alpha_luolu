package com.icourt.alpha.fragment.dialogfragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.activity.TimerTimingActivity;
import com.icourt.alpha.base.BaseDialogFragment;
import com.icourt.alpha.entity.event.OverTimingRemindEvent;
import com.icourt.alpha.interfaces.callback.IOverTimingRemindCallBack;
import com.icourt.alpha.utils.DensityUtil;
import com.icourt.alpha.widget.manager.TimerManager;

import org.greenrobot.eventbus.EventBus;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Description
 * Company Beijing icourt
 * author  yanghepeng  E-mail:yanghepeng@icourt.cc
 * date createTime：2017/8/7
 * version 2.1.0
 */

public class OverTimingRemindDialogFragment extends BaseDialogFragment {
    public static final String USE_TIME = "useTime";

    @BindView(R.id.over_timing_title_tv)
    TextView overTimingTitleTv;
    @BindView(R.id.over_timing_close_iv)
    ImageView overTimingCloseIv;
    Unbinder unbinder;

    /**
     *
     * @param useTime 单位Second
     * @return
     */
    public static OverTimingRemindDialogFragment newInstance(@NonNull long useTime) {
        OverTimingRemindDialogFragment fragment = new OverTimingRemindDialogFragment();
        Bundle args = new Bundle();
        args.putLong(USE_TIME, useTime);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     *
     * @param useTime 单位Second
     * @return
     */
    public void show(@NonNull long useTime) {
        if (overTimingTitleTv != null && useTime != 0) {
            String overTimingString = String.format(getContext().getResources()
                    .getString(R.string.timer_over_timing_remind_text), TimeUnit.MILLISECONDS.toHours(useTime));
            overTimingTitleTv.setText(overTimingString);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(R.layout.dialog_fragment_over_timing_remind, inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Window window = getDialog().getWindow();
        window.setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams windowParams = window.getAttributes();
        windowParams.dimAmount = 0.0f;

        windowParams.y = DensityUtil.dip2px(getContext(), 50);
        window.setLayout(DensityUtil.dip2px(getContext(), 260), DensityUtil.dip2px(getContext(), 52.3f));
        window.setAttributes(windowParams);
    }

    @Override
    protected void initView() {
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.setCanceledOnTouchOutside(false);
            Window window = dialog.getWindow();
            if (window != null) {
                window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
                window.setWindowAnimations(R.style.AppThemeSlideAnimation);
            }
        }

        long useTime = getArguments().getLong(USE_TIME);
        if (useTime != 0) {
            String overTimingString = String.format(getContext().getResources()
                    .getString(R.string.timer_over_timing_remind_text), TimeUnit.MILLISECONDS.toHours(useTime));
            overTimingTitleTv.setText(overTimingString);
        }
    }

    @OnClick({R.id.over_timing_title_tv,
            R.id.over_timing_close_iv})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.over_timing_title_tv:
                TimerTimingActivity.launch(getContext(), TimerManager.getInstance().getTimer());
                dismiss();
                break;
            case R.id.over_timing_close_iv:
                dismiss();
                break;
            default:
                super.onClick(v);
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        EventBus.getDefault().post(new OverTimingRemindEvent(OverTimingRemindEvent.STATUS_TIMING_REMIND_CLOSE));
        unbinder.unbind();
    }
}