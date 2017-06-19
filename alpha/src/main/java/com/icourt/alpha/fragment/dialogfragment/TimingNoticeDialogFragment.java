package com.icourt.alpha.fragment.dialogfragment;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
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
import com.icourt.alpha.entity.bean.TimeEntity;
import com.icourt.alpha.entity.event.TimingEvent;
import com.icourt.alpha.utils.DensityUtil;
import com.icourt.alpha.view.fittextview.AutofitTextView;
import com.icourt.alpha.widget.manager.TimerManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


/**
 * Description  计时悬浮框
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/5/17
 * version 1.0.0
 */
public class TimingNoticeDialogFragment extends BaseDialogFragment {


    @BindView(R.id.notice_timing_tv)
    AutofitTextView noticeTimingTv;
    @BindView(R.id.notice_timing_stop_iv)
    ImageView noticeTimingStopIv;
    @BindView(R.id.notice_timing_title_tv)
    TextView noticeTimingTitleTv;
    @BindView(R.id.timing_empty_view)
    View timingEmptyView;
    Unbinder unbinder;

    public static TimingNoticeDialogFragment newInstance(@NonNull TimeEntity.ItemEntity itemEntity) {
        TimingNoticeDialogFragment fragment = new TimingNoticeDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable("data", itemEntity);
        fragment.setArguments(args);
        return fragment;
    }

    TimeEntity.ItemEntity itemEntity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(R.layout.dialog_fragment_timing_notice, inflater, container, savedInstanceState);
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
        window.setLayout(DensityUtil.dip2px(getContext(), 260), DensityUtil.dip2px(getContext(), 198));
        window.setAttributes(windowParams);
    }

    @Override
    protected void initView() {
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.setCanceledOnTouchOutside(true);
            Window window = dialog.getWindow();
            if (window != null) {
                window.setGravity(Gravity.BOTTOM);
                window.setWindowAnimations(R.style.AppThemeSlideAnimation);
            }
        }
        Serializable data = getArguments().getSerializable("data");
        if (data instanceof TimeEntity.ItemEntity) {
            itemEntity = (TimeEntity.ItemEntity) data;
            noticeTimingTitleTv.setText(TextUtils.isEmpty(itemEntity.name) ? "未录入工作描述" : itemEntity.name);
        }
        EventBus.getDefault().register(this);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTimerEvent(TimingEvent event) {
        if (event == null) return;
        if (itemEntity == null) return;
        switch (event.action) {
            case TimingEvent.TIMING_UPDATE_PROGRESS:
                noticeTimingTv.setText(toTime(event.timingSecond));
                break;
        }
    }

    public String toTime(long times) {
        long hour = times / 3600;
        long minute = times % 3600 / 60;
        long second = times % 60;
        return String.format("%02d:%02d:%02d", hour, minute, second);
    }

    @OnClick({R.id.notice_timing_stop_iv,
            R.id.notice_timing_title_tv,
            R.id.timing_empty_view})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.notice_timing_stop_iv:
                TimerManager.getInstance().stopTimer();
                dismiss();
                break;
            case R.id.notice_timing_title_tv:
                TimerTimingActivity.launch(getContext(), TimerManager.getInstance().getTimer());
                dismiss();
                break;
            case R.id.timing_empty_view:
                dismiss();
                break;
            default:
                super.onClick(v);
                break;
        }

    }

    @Override
    public void onDestroyView() {
        EventBus.getDefault().unregister(this);
        super.onDestroyView();
        unbinder.unbind();
    }
}
