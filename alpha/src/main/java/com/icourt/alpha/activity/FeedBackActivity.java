package com.icourt.alpha.activity;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.base.BaseActivity;
import com.icourt.alpha.utils.SystemUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Description 意见反馈页面
 * Company Beijing icourt
 * author  lu.zhao  E-mail:zhaolu@icourt.cc
 * date createTime：17/9/6
 * version 2.0.0
 */

public class FeedBackActivity extends BaseActivity {

    @BindView(R.id.titleBack)
    ImageView titleBack;
    @BindView(R.id.titleContent)
    TextView titleContent;
    @BindView(R.id.titleView)
    AppBarLayout titleView;
    @BindView(R.id.feedback_wechat_tv)
    TextView feedbackWechatTv;
    @BindView(R.id.feedback_email_tv)
    TextView feedbackEmailTv;

    public static void launch(@NonNull Context context) {
        if (context == null) return;
        Intent intent = new Intent(context, FeedBackActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void initView() {
        super.initView();
        setTitle("意见反馈");
    }

    @OnClick({R.id.feedback_wechat_tv,
            R.id.feedback_email_tv})
    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.feedback_wechat_tv:
                showToast("微信");
                break;
            case R.id.feedback_email_tv:
                if (!TextUtils.isEmpty(feedbackEmailTv.getText())) {
                    try {
                        SystemUtils.sendEmail(getContext(), feedbackEmailTv.getText().toString());
                    } catch (ActivityNotFoundException e) {
                        e.printStackTrace();
                        showTopSnackBar("未找到邮件发送的app!");
                    }
                }
                break;
        }
    }
}
