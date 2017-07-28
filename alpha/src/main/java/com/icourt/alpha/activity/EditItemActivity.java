package com.icourt.alpha.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.base.BaseActivity;
import com.icourt.alpha.utils.StringUtils;
import com.icourt.alpha.view.ClearEditText;

import butterknife.BindView;
import butterknife.ButterKnife;

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
    private static final String KEY_INPUT_MINI_LINE_NUM = "key_input_mini_line_num";
    private static final String KEY_INPUT_MAX_LINE_NUM = "key_input_max_line_num";
    private static final String KEY_IS_SHOW_LIMIT_NUM = " key_is_show_limit_num";
    private static final String KEY_LIMIT_NUM = "key_limit_num";
    private static final String KEY_IS_ALLOW_INPUT_EMPTY = "key_isAllowInputEmpty";
    @BindView(R.id.titleBack)
    CheckedTextView titleBack;
    @BindView(R.id.titleContent)
    TextView titleContent;
    @BindView(R.id.titleAction)
    CheckedTextView titleAction;
    @BindView(R.id.titleView)
    AppBarLayout titleView;
    @BindView(R.id.value_input_et)
    ClearEditText valueInputEt;
    @BindView(R.id.value_input_limit)
    TextView valueInputLimit;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);
        ButterKnife.bind(this);
        initView();
    }

    /**
     * @param context
     * @param title             页面标题
     * @param defaultValue      输入框的默认文案
     * @param reqCode
     * @param inputMiniLineNum  默认最小展示行数
     * @param inputMaxLineNum   默认展示几行
     * @param isShowLimitNum    是否展示数字显示提示
     * @param limitNum          数字限制长度
     * @param isAllowInputEmpty 是否允许输入为空
     */
    public static void launchForResult(@NonNull Activity context,
                                       @NonNull String title,
                                       @Nullable String defaultValue,
                                       int reqCode,
                                       int inputMiniLineNum,
                                       int inputMaxLineNum,
                                       boolean isShowLimitNum,
                                       int limitNum,
                                       boolean isAllowInputEmpty) {
        if (context == null) return;
        if (TextUtils.isEmpty(title)) return;
        if (inputMaxLineNum < 0) return;
        if (limitNum < 0) return;
        Intent intent = new Intent(context, EditItemActivity.class);
        intent.putExtra(KEY_TITLE, title);
        intent.putExtra(KEY_DEFAULT_VALUE, defaultValue);
        intent.putExtra(KEY_INPUT_MINI_LINE_NUM, inputMiniLineNum);
        intent.putExtra(KEY_INPUT_MAX_LINE_NUM, inputMaxLineNum);
        intent.putExtra(KEY_IS_SHOW_LIMIT_NUM, isShowLimitNum);
        intent.putExtra(KEY_LIMIT_NUM, limitNum);
        intent.putExtra(KEY_IS_ALLOW_INPUT_EMPTY, isAllowInputEmpty);
        context.startActivityForResult(intent, reqCode);
    }

    private boolean isShowLimitNum;
    private int limitNum;

    @Override
    protected void initView() {
        super.initView();
        isShowLimitNum = getIntent().getBooleanExtra(KEY_IS_SHOW_LIMIT_NUM, false);
        limitNum = getIntent().getIntExtra(KEY_LIMIT_NUM, 0);

        setTitle(getIntent().getStringExtra(KEY_TITLE));
        TextView titleActionTextView = getTitleActionTextView();
        if (titleActionTextView != null) {
            titleActionTextView.setText("确定");
        }
        if (getIntent().getIntExtra(KEY_INPUT_MAX_LINE_NUM, 1) <= 1) {
            valueInputEt.setSingleLine(true);
        } else {
            valueInputEt.setSingleLine(false);
            valueInputEt.setMinLines(getIntent().getIntExtra(KEY_INPUT_MINI_LINE_NUM, 1));
            valueInputEt.setMaxLines(getIntent().getIntExtra(KEY_INPUT_MAX_LINE_NUM, 1));
        }

        valueInputLimit.setVisibility(isShowLimitNum ? View.VISIBLE : View.GONE);
        valueInputEt.setFilters(new InputFilter[]{new InputFilter.LengthFilter(limitNum)});

        valueInputEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (isShowLimitNum) {
                    valueInputLimit.setText(TextUtils.isEmpty(s) ? String.format("%s/%s", 0, limitNum) : String.format("%s/%s", s.length(), limitNum));
                }
            }
        });
        valueInputEt.setText(getIntent().getStringExtra(KEY_DEFAULT_VALUE));
        valueInputEt.setSelection(getTextString(valueInputEt, "").length());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.titleAction:
                if (!getIntent().getBooleanExtra(KEY_IS_ALLOW_INPUT_EMPTY, false)
                        && StringUtils.isEmpty(valueInputEt.getText())) {
                    showTopSnackBar(String.format("%s不能为空", getIntent().getStringExtra(KEY_TITLE)));
                    return;
                }
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
