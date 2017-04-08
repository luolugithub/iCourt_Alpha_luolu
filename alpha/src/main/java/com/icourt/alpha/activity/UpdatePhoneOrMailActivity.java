package com.icourt.alpha.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.base.BaseActivity;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.httpmodel.ResEntity;

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

public class UpdatePhoneOrMailActivity extends BaseActivity {

    public static final int UPDATE_PHONE_TYPE = 1;//修改电话
    public static final int UPDATE_EMAIL_TYPE = 2;//修改邮箱

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

    @IntDef({UPDATE_PHONE_TYPE, UPDATE_EMAIL_TYPE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface UPDATE_TYPE {
    }

    public static void launch(@NonNull Context context, @UPDATE_TYPE int type, String phoneOrEmail) {
        if (context == null) return;
        Intent intent = new Intent(context, UpdatePhoneOrMailActivity.class);
        intent.putExtra("type", type);
        intent.putExtra("phoneOrEmail", phoneOrEmail);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_phone_or_email_layout);
        ButterKnife.bind(this);
        initView();
    }

    @UPDATE_TYPE
    int getType() {
        if (getIntent().getIntExtra("type", -1) == UPDATE_EMAIL_TYPE) {
            return UPDATE_EMAIL_TYPE;
        }
        return UPDATE_PHONE_TYPE;
    }

    private String phoneOrEmail;

    @Override
    protected void initView() {
        super.initView();

        switch (getType()) {
            case UPDATE_PHONE_TYPE:
                setTitle("电话号码");
                myCenterUpdateHintText.setText("电话号码");
                break;
            case UPDATE_EMAIL_TYPE:
                setTitle("邮箱地址");
                myCenterUpdateHintText.setText("邮箱地址");
                break;
        }
        phoneOrEmail = getIntent().getStringExtra("phoneOrEmail");
        myCenterUpdateEdittext.setText(phoneOrEmail);
        myCenterUpdateEdittext.setSelection(phoneOrEmail.length());
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

    @OnClick({R.id.titleBack, R.id.my_center_update_clear_view})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.titleBack:
                if (getType() == UPDATE_EMAIL_TYPE) {
                    upDateEmail(myCenterUpdateEdittext.getText().toString().trim());
                } else if (getType() == UPDATE_PHONE_TYPE) {
                    upDatePhone(myCenterUpdateEdittext.getText().toString().trim());
                }
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

    /**
     * 修改电话信息
     *
     * @param phone
     */
    private void upDatePhone(String phone) {
        getApi().updateUserEmail(phone).enqueue(new SimpleCallBack<String>() {
            @Override
            public void onSuccess(Call<ResEntity<String>> call, Response<ResEntity<String>> response) {
                UpdatePhoneOrMailActivity.this.finish();
            }
        });
    }

    /**
     * 修改邮箱信息
     *
     * @param email
     */
    private void upDateEmail(String email) {
        getApi().updateUserEmail(email).enqueue(new SimpleCallBack<String>() {
            @Override
            public void onSuccess(Call<ResEntity<String>> call, Response<ResEntity<String>> response) {
                UpdatePhoneOrMailActivity.this.finish();
            }
        });
    }
}
