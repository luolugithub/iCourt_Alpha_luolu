package com.icourt.alpha.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.umeng.socialize.bean.SHARE_MEDIA;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/10
 * version 1.0.0
 */
public class LoginSelectActivity extends LoginBaseActivity {

    @BindView(R.id.loginWeixinBtn)
    TextView loginWeixinBtn;
    @BindView(R.id.actionLoginWithPwd)
    TextView actionLoginWithPwd;

    public static void launch(Context context) {
        if (context == null) return;
        Intent intent = new Intent(context, LoginSelectActivity.class);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_selelct);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.loginWeixinBtn, R.id.actionLoginWithPwd})
    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.loginWeixinBtn:
                doOauth(SHARE_MEDIA.WEIXIN);
                break;
            case R.id.actionLoginWithPwd:
                LoginWithPwdActivity.launch(getContext());
                break;
        }
    }
}
