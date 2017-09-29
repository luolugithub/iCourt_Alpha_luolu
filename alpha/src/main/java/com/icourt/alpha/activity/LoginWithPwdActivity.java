package com.icourt.alpha.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.utils.SpUtils;
import com.icourt.alpha.utils.SystemUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Description  密码登陆界面
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/10
 * version 1.0.0
 */
public class LoginWithPwdActivity extends LoginBaseActivity {

    private static final String KEY_ACCOUNT = "key_account";
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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_with_pwd);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void initView() {
        super.initView();
        String account = SpUtils.getInstance().getStringData(KEY_ACCOUNT, "");
        if (!TextUtils.isEmpty(account)) {
            etMail.setText(account);
            etMail.setSelection(etMail.length());
        }
    }

    @OnClick({R.id.pwd_login_rootview, R.id.pwd_login_btn, R.id.wechat_login_text})
    @Override
    public void onClick(View v) {
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
                    SpUtils.getInstance().putData(KEY_ACCOUNT, etMail.getText().toString());
                    loginWithPwd(etMail.getText().toString(), etPwd.getText().toString());
                }
                break;
            case R.id.wechat_login_text:
                loginWithWeiXin();
                break;
            default:
                super.onClick(v);
                break;
        }
    }


}
