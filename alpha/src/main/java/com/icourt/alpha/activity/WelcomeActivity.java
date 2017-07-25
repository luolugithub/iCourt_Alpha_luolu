package com.icourt.alpha.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;

import com.icourt.alpha.R;
import com.icourt.alpha.base.BaseActivity;
import com.icourt.alpha.entity.bean.AlphaUserInfo;
import com.icourt.alpha.http.AlphaClient;
import com.icourt.alpha.service.LocalService;
import com.icourt.alpha.service.RemoteService;
import com.netease.nimlib.sdk.NimIntent;
import com.netease.nimlib.sdk.msg.model.IMMessage;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Description  spalash页面
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

        RemoteService.start(getContext());
        LocalService.start(getContext());

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
            AlphaUserInfo loginUserInfo = getLoginUserInfo();
            AlphaClient.setToken(loginUserInfo.getToken());
            AlphaClient.setOfficeId(loginUserInfo.getOfficeId());
            Intent intent = getIntent();
            if (intent.hasExtra(NimIntent.EXTRA_NOTIFY_CONTENT)) {
                ArrayList<IMMessage> messages = (ArrayList<IMMessage>) intent.getSerializableExtra(NimIntent.EXTRA_NOTIFY_CONTENT);
                if (messages == null) {
                    MainActivity.launch(getContext());
                    this.finish();
                } else {
                    MainActivity.launchByNotifaction(WelcomeActivity.this, messages.get(0));
                    this.finish();
                }
                // 最好将intent清掉，以免从堆栈恢复时又打开客服窗口
                setIntent(new Intent());
            }else{
                MainActivity.launch(getContext());
                this.finish();
            }
        } else {
            LoginSelectActivity.launch(getContext());
        }
        finish();
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        overridePendingTransition(R.anim.activity_fade_enter, R.anim.activity_zoom_fade_exit);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.activity_fade_enter, R.anim.activity_zoom_fade_exit);
    }
}
