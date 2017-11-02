package com.icourt.alpha.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.NumberKeyListener;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
//TODO 这个类用多态与继承实现 三种功能
public class UpdateUserInfoActivity extends BaseActivity {

    public static final int UPDATE_PHONE_TYPE = 1;//修改电话
    public static final int UPDATE_EMAIL_TYPE = 2;//修改邮箱
    public static final int UPDATE_NAME_TYPE = 3;//修改姓名

    private static final int UPDATE_NAME_MAX_LENGTH = 64;//修改姓名时 最大长度
    private static final int UPDATE_PHONE_MAX_LENGTH = 15;//修改电话时 最大长度

    private static final String KEY_TYPE = "key_type";
    private static final String KEY_STRING_VALUE = "key_string_value";
    char[] numberChars = {'1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '+'};
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
    @BindView(R.id.my_center_update_state_view)
    ImageView myCenterUpdateStateView;
    @BindView(R.id.update_state_layout)
    LinearLayout updateStateLayout;
    @BindView(R.id.update_right_layout)
    LinearLayout updateRightLayout;
    @BindView(R.id.my_center_update_error_hint_text)
    TextView myCenterUpdateErrorHintText;
    @BindView(R.id.my_center_update_name_hint_text)
    TextView myCenterUpdateNameHintText;
    @BindView(R.id.left_image_view)
    ImageView leftImageView;

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
                setTitle(R.string.mine_phone);
                myCenterUpdateHintText.setText(R.string.mine_phone);
                leftImageView.setImageResource(R.mipmap.setting_phone);
                updateStateLayout.setVisibility(View.VISIBLE);
                myCenterUpdateEdittext.setFilters(new InputFilter[]{new InputFilter.LengthFilter(UPDATE_PHONE_MAX_LENGTH)});
                myCenterUpdateEdittext.setKeyListener(new NumberKeyListener() {
                    @Override
                    protected char[] getAcceptedChars() {
                        return numberChars;
                    }

                    @Override
                    public int getInputType() {
                        return InputType.TYPE_CLASS_PHONE;
                    }
                });
                break;
            case UPDATE_EMAIL_TYPE:
                setTitle(R.string.mine_mail_address);
                myCenterUpdateHintText.setText(R.string.mine_mail_address);
                leftImageView.setImageResource(R.mipmap.setting_email);
                updateStateLayout.setVisibility(View.VISIBLE);
                myCenterUpdateEdittext.setInputType(InputType.TYPE_TEXT_VARIATION_WEB_EMAIL_ADDRESS);
                break;
            case UPDATE_NAME_TYPE:
                setTitle(R.string.mine_name);
                myCenterUpdateHintText.setText(R.string.mine_name);
                leftImageView.setImageResource(R.mipmap.setting_user_name);
                updateStateLayout.setVisibility(View.GONE);
                myCenterUpdateEdittext.setInputType(InputType.TYPE_CLASS_TEXT);
                myCenterUpdateEdittext.setFilters(new InputFilter[]{new InputFilter.LengthFilter(UPDATE_NAME_MAX_LENGTH)});
                myCenterUpdateEdittext.setGravity(Gravity.TOP);
                myCenterUpdateEdittext.setSingleLine(false);
                myCenterUpdateEdittext.setHorizontallyScrolling(false);
                break;
            default:
                break;
        }
        String value = getValue();
        myCenterUpdateEdittext.setText(value.trim());
        setRightLayoutVisible(value);
        setNameEditMaxLength();
        myCenterUpdateEdittext.setSelection(value.length());
        myCenterUpdateEdittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                setRightLayoutVisible(myCenterUpdateEdittext.getText());
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (getType() == UPDATE_PHONE_TYPE) {
                    formatPhoneTextChanged(myCenterUpdateEdittext.getText(), count);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                setRightLayoutVisible(myCenterUpdateEdittext.getText());
                setNameEditMaxLength();
            }
        });
    }

    /**
     * 设置修改姓名最大长度
     */
    private void setNameEditMaxLength() {
        if (getType() == UPDATE_NAME_TYPE) {
            if (myCenterUpdateEdittext.getText().length() >= 50) {
                myCenterUpdateNameHintText.setVisibility(View.VISIBLE);
                myCenterUpdateNameHintText.setText(String.format("%s/%s", myCenterUpdateEdittext.getText().length(), UPDATE_NAME_MAX_LENGTH));
            } else {
                myCenterUpdateNameHintText.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 设置输入框右侧按钮显示
     *
     * @param content
     */
    private void setRightLayoutVisible(CharSequence content) {
        if (!TextUtils.isEmpty(content)) {
            String info = content.toString().trim();
            if (TextUtils.isEmpty(info)) {
                setSaveViewState(false);
                updateRightLayout.setVisibility(View.INVISIBLE);
                return;
            }
            updateRightLayout.setVisibility(View.VISIBLE);
            myCenterUpdateClearView.setVisibility(View.VISIBLE);
            switch (getType()) {
                case UPDATE_EMAIL_TYPE:
                    updateStateLayout.setVisibility(View.VISIBLE);
                    boolean isMail = StringUtils.isMailNO(content.toString());
                    myCenterUpdateErrorHintText.setText(R.string.mine_use_true_mail);
                    setStatreViewImage(isMail);
                    setSaveViewState(isMail);
                    break;
                case UPDATE_PHONE_TYPE:
                    updateStateLayout.setVisibility(View.VISIBLE);
                    boolean isMobile = StringUtils.isMobileNO86(content.toString());
                    myCenterUpdateErrorHintText.setText(R.string.mine_use_true_phone);
                    setStatreViewImage(isMobile);
                    setSaveViewState(isMobile);
                    break;
                case UPDATE_NAME_TYPE:
                    updateStateLayout.setVisibility(View.GONE);
                    setSaveViewState(true);
                    break;
                default:
                    break;
            }
        } else {
            setSaveViewState(false);
            updateRightLayout.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * 设置状态按钮图标、错误提示是否隐藏
     *
     * @param isTrue
     */
    private void setStatreViewImage(boolean isTrue) {
        myCenterUpdateStateView.setImageResource(isTrue ? R.mipmap.verify_ok : R.mipmap.verify_no);
        myCenterUpdateErrorHintText.setVisibility(isTrue ? View.GONE : View.VISIBLE);
    }

    /**
     * 设置保存按钮是否可以点击
     *
     * @param isSave
     */
    private void setSaveViewState(boolean isSave) {
        titleAction.setTextColor(isSave ? SystemUtils.getColor(this, R.color.alpha_font_color_orange) : SystemUtils.getColor(this, R.color.alpha_font_color_gray));
        titleAction.setClickable(isSave);
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
                if (!TextUtils.isEmpty(myCenterUpdateEdittext.getText())) {
                    updateInfo(alphaUserInfo.getUserId(), myCenterUpdateEdittext.getText().toString().trim());
                }
                break;
            case R.id.my_center_update_clear_view:
                myCenterUpdateEdittext.setText("");
                updateRightLayout.setVisibility(View.INVISIBLE);
                setSaveViewState(false);
                break;
            default:
                super.onClick(v);
                break;
        }
    }

    /**
     * 替换本地保持user信息
     */
    private void clearAndSaveUser() {
        LoginInfoUtils.clearLoginUserInfo();
        LoginInfoUtils.saveLoginUserInfo(alphaUserInfo);
        UpdateUserInfoActivity.this.finish();
    }

    /**
     * 修改信息
     *
     * @param userId
     * @param value
     */
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
            default:
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
        showLoadingDialog(null);
        callEnqueue(
                getApi().updateUserEmail(userId, email),
                new SimpleCallBack<JsonElement>() {
                    @Override
                    public void onSuccess(Call<ResEntity<JsonElement>> call, Response<ResEntity<JsonElement>> response) {
                        dismissLoadingDialog();
                        alphaUserInfo.setMail(email);
                        clearAndSaveUser();
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
        showLoadingDialog(null);
        callEnqueue(
                getApi().updateUserPhone(userId, phone),
                new SimpleCallBack<JsonElement>() {
                    @Override
                    public void onSuccess(Call<ResEntity<JsonElement>> call, Response<ResEntity<JsonElement>> response) {
                        dismissLoadingDialog();
                        alphaUserInfo.setPhone(phone);
                        clearAndSaveUser();
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
        callEnqueue(
                getApi().updateUserName(userId, name),
                new SimpleCallBack<JsonElement>() {
                    @Override
                    public void onSuccess(Call<ResEntity<JsonElement>> call, Response<ResEntity<JsonElement>> response) {
                        dismissLoadingDialog();
                        alphaUserInfo.setName(name);
                        clearAndSaveUser();
                    }

                    @Override
                    public void onFailure(Call<ResEntity<JsonElement>> call, Throwable t) {
                        super.onFailure(call, t);
                        dismissLoadingDialog();
                    }
                });
    }

    /**
     * 处理输入、删除电话号码时输入框内容
     *
     * @param s
     * @param count
     */
    private void formatPhoneTextChanged(CharSequence s, int count) {
        if (TextUtils.isEmpty(s)) return;
        int length = s.toString().length();
        String firstChar = String.valueOf(s.charAt(0));
        if (TextUtils.equals(firstChar, "+")) {
            if (count == 0) { //删除数字
                if (length == 4) {
                    myCenterUpdateEdittext.setText(s.subSequence(0, 3));
                    myCenterUpdateEdittext.setSelection(myCenterUpdateEdittext.getText().length());
                }
            } else if (count == 1) {//添加数字
                if (length == 4 || (length == 14 && !s.toString().contains(" "))) {
                    String part1 = s.subSequence(0, 3).toString();
                    String part2 = s.subSequence(3, length).toString();
                    myCenterUpdateEdittext.setText(part1 + " " + part2);
                    myCenterUpdateEdittext.setSelection(myCenterUpdateEdittext.getText().length());
                }
            }
        }
        if (length > 0) {
            updateRightLayout.setVisibility(View.VISIBLE);
        }
    }
}
