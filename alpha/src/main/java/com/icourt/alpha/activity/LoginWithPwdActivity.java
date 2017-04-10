package com.icourt.alpha.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.base.BaseActivity;
import com.icourt.alpha.utils.SystemUtils;

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
public class LoginWithPwdActivity extends BaseActivity {

    @BindView(R.id.et_mail)
    EditText etMail;
    @BindView(R.id.et_pwd)
    EditText etPwd;
    @BindView(R.id.pwd_login_btn)
    TextView pwdLoginBtn;
    @BindView(R.id.wechat_login_text)
    TextView wechatLoginText;
    @BindView(R.id.pwd_login_rootview)
    LinearLayout pwdLoginRootview;

    public static void launch(Context context) {
        if (context == null) return;
        Intent intent = new Intent(context, LoginWithPwdActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//全屏
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_with_pwd);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.pwd_login_rootview, R.id.pwd_login_btn, R.id.wechat_login_text})
    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.pwd_login_rootview:
                SystemUtils.hideSoftKeyBoard(getActivity());
                break;
            case R.id.pwd_login_btn:
                if (TextUtils.isEmpty(etMail.getText())) {
                    showTopSnackBar(getString(R.string.input_mail_text));
                } else if (TextUtils.isEmpty(etPwd.getText())) {
                    showTopSnackBar(getString(R.string.input_password_text));
                } else {
                    pwdLogin();
                }
                break;
            case R.id.wechat_login_text:
                break;
        }
    }

    private void pwdLogin() {

    }
}
