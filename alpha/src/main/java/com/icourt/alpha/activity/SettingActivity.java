package com.icourt.alpha.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.gson.JsonElement;
import com.icourt.alpha.R;
import com.icourt.alpha.base.BaseActivity;
import com.icourt.alpha.entity.bean.AlphaUserInfo;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.utils.GlideUtils;
import com.icourt.alpha.utils.LoginInfoUtils;
import com.icourt.alpha.utils.StringUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Description 设置
 * Company Beijing icourt
 * author  lu.zhao  E-mail:zhaolu@icourt.cc
 * date createTime：17/5/4
 * version 2.0.0
 */

public class SettingActivity extends BaseActivity {

    @BindView(R.id.photo_image)
    ImageView photoImage;
    @BindView(R.id.photo_layout)
    LinearLayout photoLayout;
    @BindView(R.id.phone_edittext)
    EditText phoneEdittext;
    @BindView(R.id.email_edittext)
    EditText emailEdittext;

    private AlphaUserInfo alphaUserInfo;

    public static void launch(@NonNull Context context) {
        if (context == null) return;
        Intent intent = new Intent(context, SettingActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void initView() {
        super.initView();
        alphaUserInfo = getLoginUserInfo();
        setTitle("个人设置");
        getTitleActionTextView().setText("完成");
        if (alphaUserInfo != null) {
            GlideUtils.loadUser(this, alphaUserInfo.getPic(), photoImage);
            phoneEdittext.setText(alphaUserInfo.getPhone());
            emailEdittext.setText(alphaUserInfo.getMail());
            phoneEdittext.setSelection(phoneEdittext.getText().length());
            // phoneEdittext.setSelection(alphaUserInfo.getPhone().length());
        }
    }

    @OnClick({R.id.titleAction, R.id.photo_layout})
    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.titleAction:
                updateInfo();
                break;
            case R.id.photo_layout:
                break;
        }
    }

    private void updateInfo() {
        final String phone = phoneEdittext.getText().toString().trim();
        final String email = emailEdittext.getText().toString().trim();

        //非必填
        /*if (!TextFormater.isMailNO(email)) {
            showTopSnackBar("请输入正确的邮箱格式");
            return;
        }*/
        if (!StringUtils.isMobileNO(phone)) {
            showTopSnackBar("请输入正确的手机格式");
            return;
        }
        showLoadingDialog(null);
        callEnqueue(
                getApi().updateUserInfo(alphaUserInfo.getUserId(), phone, email),
                new SimpleCallBack<JsonElement>() {
                    @Override
                    public void onSuccess(Call<ResEntity<JsonElement>> call, Response<ResEntity<JsonElement>> response) {
                        dismissLoadingDialog();
                        alphaUserInfo.setPhone(phone);
                        alphaUserInfo.setMail(email);
                        LoginInfoUtils.clearLoginUserInfo();
                        LoginInfoUtils.saveLoginUserInfo(alphaUserInfo);
                        SettingActivity.this.finish();
                    }

                    @Override
                    public void onFailure(Call<ResEntity<JsonElement>> call, Throwable t) {
                        super.onFailure(call, t);
                        dismissLoadingDialog();
                    }
                });
    }
}
