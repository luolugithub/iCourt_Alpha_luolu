package com.icourt.alpha.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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

import com.icourt.alpha.R;
import com.icourt.alpha.base.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Description 编辑单条信息 模版
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/30
 * version 1.0.0
 */
public class EditItemActivity extends BaseActivity {
    private static final String KEY_TITLE = "key_title";
    private static final String KEY_DEFAULT_VALUE = "key_value";
    private static final String KEY_INPUT_LINE_NUM = "key_input_line_num";
    @BindView(R.id.titleBack)
    CheckedTextView titleBack;
    @BindView(R.id.titleContent)
    TextView titleContent;
    @BindView(R.id.titleAction)
    CheckedTextView titleAction;
    @BindView(R.id.titleView)
    AppBarLayout titleView;
    @BindView(R.id.value_input_et)
    EditText valueInputEt;
    @BindView(R.id.value_input_clear_iv)
    ImageView valueInputClearIv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);
        ButterKnife.bind(this);
        initView();
    }

    public static void launchForResult(@NonNull Activity context,
                                       @NonNull String title,
                                       @Nullable String defaultValue,
                                       int reqCode,
                                       int inputLineNum) {
        if (context == null) return;
        if (TextUtils.isEmpty(title)) return;
        if (inputLineNum < 0) return;
        Intent intent = new Intent(context, EditItemActivity.class);
        intent.putExtra(KEY_TITLE, title);
        intent.putExtra(KEY_DEFAULT_VALUE, defaultValue);
        intent.putExtra(KEY_INPUT_LINE_NUM, inputLineNum);
        context.startActivityForResult(intent, reqCode);
    }

    @Override
    protected void initView() {
        super.initView();
        setTitle(getIntent().getStringExtra(KEY_TITLE));
        setViewVisible(valueInputClearIv, false);
        TextView titleActionTextView = getTitleActionTextView();
        if (titleActionTextView != null) {
            titleActionTextView.setText("确定");
        }
        if (getIntent().getIntExtra(KEY_INPUT_LINE_NUM, 1) <= 1) {
            valueInputEt.setSingleLine(true);
        } else {
            valueInputEt.setSingleLine(false);
            valueInputEt.setMaxLines(getIntent().getIntExtra(KEY_INPUT_LINE_NUM, 1));
        }

        valueInputEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                valueInputClearIv.setVisibility(TextUtils.isEmpty(s) ? View.GONE : View.VISIBLE);
            }
        });
        valueInputEt.setText(getIntent().getStringExtra(KEY_DEFAULT_VALUE));
        valueInputEt.setSelection(getTextString(valueInputEt, "").length());
    }

    @OnClick({R.id.value_input_clear_iv})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.value_input_clear_iv:
                valueInputEt.setText("");
                break;
            case R.id.titleAction:
                Intent intent = getIntent();
                intent.putExtra(KEY_ACTIVITY_RESULT, getTextString(valueInputEt, ""));
                setResult(Activity.RESULT_OK, intent);
                finish();
                break;
            default:
                super.onClick(v);
                break;
        }
    }
}
