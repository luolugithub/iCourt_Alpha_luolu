package com.icourt.alpha.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;

import com.icourt.alpha.R;
import com.icourt.alpha.base.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/10
 * version 1.0.0
 */
public class WelcomeActivity extends BaseActivity implements Animation.AnimationListener {
    @BindView(R.id.activity_welcome_view)
    ImageView activityWelcomeView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//全屏
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void initView() {
        super.initView();
        // 渐变动画
        AlphaAnimation alpha = new AlphaAnimation(1, 1);
        alpha.setDuration(2000);
        alpha.setFillAfter(true);
        alpha.setAnimationListener(this);
        activityWelcomeView.startAnimation(alpha);
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        if (isUserLogin()) {
            MainActivity.launch(getContext());
        } else {
            LoginSelectActivity.launch(getContext());
        }
        finish();
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
}
