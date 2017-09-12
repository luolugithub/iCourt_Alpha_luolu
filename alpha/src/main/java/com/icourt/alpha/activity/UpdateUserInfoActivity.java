package com.icourt.alpha.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.JsonElement;
import com.icourt.alpha.R;
import com.icourt.alpha.base.BaseActivity;
import com.icourt.alpha.entity.bean.AlphaUserInfo;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.utils.LoginInfoUtils;
import com.icourt.alpha.utils.StringUtils;
import com.icourt.alpha.utils.SystemUtils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Description
 * Company Beijing icourt
 * author  lu.zhao  E-mail:zhaolu@icourt.cc
 * date createTime：17/4/8
 * version 2.0.0
 */

public class UpdateUserInfoActivity extends BaseActivity {

    public static final int UPDATE_PHONE_TYPE = 1;//修改电话
    public static final int UPDATE_EMAIL_TYPE = 2;//修改邮箱
    public static final int UPDATE_NAME_TYPE = 3;//修改姓名

    private static final String KEY_TYPE = "key_type";
    private static final String KEY_STRING_VALUE = "key_string_value";

    @BindView(R.id.titleBack)
    ImageView titleBack;
    @BindView(R.id.titleContent)
    TextView titleContent;
    @BindView(R.id.titleView)
    AppBarLayout titleView;
    @BindView(R.id.my_center_update_hint_text)
    TextView myCenterUpdateHintText;
    @BindView(R.id.my_center_update_edittext)
    EditText myCenterUpdateEdittext;
    @BindView(R.id.my_center_update_clear_view)
    ImageView myCenterUpdateClearView;
    @BindView(R.id.titleAction)
    CheckedTextView titleAction;

    AlphaUserInfo alphaUserInfo;

    @IntDef({UPDATE_PHONE_TYPE,
            UPDATE_EMAIL_TYPE,
            UPDATE_NAME_TYPE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface UPDATE_TYPE {
    }

    public static void launch(@NonNull Context context, @UPDATE_TYPE int type, String value) {
        if (context == null) return;
        Intent intent = new Intent(context, UpdateUserInfoActivity.class);
        intent.putExtra(KEY_TYPE, type);
        intent.putExtra(KEY_STRING_VALUE, value);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_phone_or_email_layout);
        ButterKnife.bind(this);
        initView();
    }

    /**
     * 获取type
     *
     * @return
     */
    private int getType() {
        return getIntent().getIntExtra(KEY_TYPE, -1);
    }

    /**
     * 获取value
     *
     * @return
     */
    private String getValue() {
        return getIntent().getStringExtra(KEY_STRING_VALUE);
    }

    @Override
    protected void initView() {
        super.initView();
        alphaUserInfo = getLoginUserInfo();
        switch (getType()) {
            case UPDATE_PHONE_TYPE:
                setTitle("电话号码");
                myCenterUpdateHintText.setText("电话号码");
                break;
            case UPDATE_EMAIL_TYPE:
                setTitle("邮箱地址");
                myCenterUpdateHintText.setText("邮箱地址");
                break;
            case UPDATE_NAME_TYPE:
                setTitle("姓名");
                myCenterUpdateHintText.setText("姓名");
                break;
        }
        String value = getValue();
        myCenterUpdateEdittext.setText(value);
        myCenterUpdateEdittext.setSelection(value.length());
        myCenterUpdateEdittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (s.toString().length() > 0) {
                    myCenterUpdateClearView.setVisibility(View.VISIBLE);
                } else {
                    myCenterUpdateClearView.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() > 0) {
                    myCenterUpdateClearView.setVisibility(View.VISIBLE);
                } else {
                    myCenterUpdateClearView.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    @OnClick({R.id.titleAction, R.id.my_center_update_clear_view})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.titleBack:
                SystemUtils.hideSoftKeyBoard(this);
                finish();
                break;
            case R.id.titleAction:
                updateInfo(alphaUserInfo.getUserId(), myCenterUpdateEdittext.getText().toString().trim());
                break;
            case R.id.my_center_update_clear_view:
                myCenterUpdateEdittext.setText("");
                myCenterUpdateClearView.setVisibility(View.INVISIBLE);
                break;
            default:
                super.onClick(v);
                break;
        }
    }


    private void updateInfo(String userId, String value) {

        if (TextUtils.isEmpty(userId)) return;
        if (TextUtils.isEmpty(value)) return;
        switch (getType()) {
            case UPDATE_PHONE_TYPE:
                upDatePhone(userId, value);
                break;
            case UPDATE_EMAIL_TYPE:
                upDateEmail(userId, value);
                break;
            case UPDATE_NAME_TYPE:
                upDateName(userId, value);
                break;
        }
    }

    /**
     * 修改邮箱
     *
     * @param userId
     * @param email
     */
    private void upDateEmail(String userId, final String email) {
        if (!StringUtils.isMailNO(email)) {
            showTopSnackBar("请输入正确的邮箱格式");
            return;
        }
        showLoadingDialog(null);
        getApi().updateUserEmail(userId, email).enqueue(new SimpleCallBack<JsonElement>() {
            @Override
            public void onSuccess(Call<ResEntity<JsonElement>> call, Response<ResEntity<JsonElement>> response) {
                dismissLoadingDialog();
                alphaUserInfo.setMail(email);
                LoginInfoUtils.clearLoginUserInfo();
                LoginInfoUtils.saveLoginUserInfo(alphaUserInfo);
                UpdateUserInfoActivity.this.finish();
            }

            @Override
            public void onFailure(Call<ResEntity<JsonElement>> call, Throwable t) {
                super.onFailure(call, t);
                dismissLoadingDialog();
            }
        });
    }

    /**
     * 修改电话
     *
     * @param userId
     * @param phone
     */
    private void upDatePhone(String userId, final String phone) {
        if (!StringUtils.isMobileNO(phone)) {
            showTopSnackBar("请输入正确的手机格式");
            return;
        }
        showLoadingDialog(null);
        getApi().updateUserPhone(userId, phone).enqueue(new SimpleCallBack<JsonElement>() {
            @Override
            public void onSuccess(Call<ResEntity<JsonElement>> call, Response<ResEntity<JsonElement>> response) {
                dismissLoadingDialog();
                alphaUserInfo.setPhone(phone);
                LoginInfoUtils.clearLoginUserInfo();
                LoginInfoUtils.saveLoginUserInfo(alphaUserInfo);
                UpdateUserInfoActivity.this.finish();
            }

            @Override
            public void onFailure(Call<ResEntity<JsonElement>> call, Throwable t) {
                super.onFailure(call, t);
                dismissLoadingDialog();
            }
        });
    }

    /**
     * 修改姓名
     *
     * @param userId
     * @param
     */
    private void upDateName(String userId, final String name) {
        showLoadingDialog(null);
        getApi().updateUserName(userId, name).enqueue(new SimpleCallBack<JsonElement>() {
            @Override
            public void onSuccess(Call<ResEntity<JsonElement>> call, Response<ResEntity<JsonElement>> response) {
                dismissLoadingDialog();
                alphaUserInfo.setName(name);
                LoginInfoUtils.clearLoginUserInfo();
                LoginInfoUtils.saveLoginUserInfo(alphaUserInfo);
                UpdateUserInfoActivity.this.finish();
            }

            @Override
            public void onFailure(Call<ResEntity<JsonElement>> call, Throwable t) {
                super.onFailure(call, t);
                dismissLoadingDialog();
            }
        });
    }
}
