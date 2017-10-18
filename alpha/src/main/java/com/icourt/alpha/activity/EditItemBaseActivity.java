package com.icourt.alpha.activity;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.base.BaseActivity;
import com.icourt.alpha.utils.StringUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Description  统一的编辑输入的基类
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/9/27
 * version 2.1.0
 */
public abstract class EditItemBaseActivity extends BaseActivity implements TextWatcher {

    protected static final String KEY_TITLE = "key_title";
    protected static final String KEY_DEFAULT_VALUE = "key_value";
    protected static final String KEY_INPUT_MINI_LINE_NUM = "key_input_mini_line_num";
    protected static final String KEY_INPUT_MAX_LINE_NUM = "key_input_max_line_num";
    protected static final String KEY_IS_SHOW_LIMIT_NUM = " key_is_show_limit_num";
    protected static final String KEY_LIMIT_NUM = "key_limit_num";
    protected static final String KEY_IS_ALLOW_INPUT_EMPTY = "key_isAllowInputEmpty";
    @BindView(R.id.titleBack)
    ImageView titleBack;
    @BindView(R.id.titleContent)
    TextView titleContent;
    @BindView(R.id.titleAction)
    CheckedTextView titleAction;
    @BindView(R.id.titleView)
    AppBarLayout titleView;
    @BindView(R.id.input_name_et)
    EditText inputNameEt;
    @BindView(R.id.input_clear_iv)
    ImageView inputClearIv;
    @BindView(R.id.input_limit_num_tv)
    TextView inputLimitNumTv;


    /**
     * 子类不能再重写了
     *
     * @param savedInstanceState
     */
    @Override
    protected final void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item_base);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void initView() {
        super.initView();
        titleAction.setEnabled(false);
        inputNameEt.setFilters(new InputFilter[]{new InputFilter.LengthFilter(getMaxInputLimitNum())});
        inputNameEt.addTextChangedListener(this);
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @CallSuper
    @Override
    public void afterTextChanged(Editable editable) {
        if (editable != null) {
            inputLimitNumTv.setText(String.format("%s/%s", editable.length(), getMaxInputLimitNum()));
            inputLimitNumTv.setVisibility(editable.length() > 0 ? View.VISIBLE : View.GONE);
            inputClearIv.setVisibility(editable.length() > 0 ? View.VISIBLE : View.GONE);
            titleAction.setEnabled(!StringUtils.isEmpty(editable));
        } else {
            inputLimitNumTv.setVisibility(View.GONE);
            inputClearIv.setVisibility(View.GONE);
        }
    }

    @OnClick({R.id.input_clear_iv})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.titleBack:
                /**
                 * 默认关闭
                 */
                if (!onCancelSubmitInput(inputNameEt)) {
                    onBackPressed();
                }
                break;
            case R.id.titleAction:
                onSubmitInput(inputNameEt);
                break;
            case R.id.input_clear_iv:
                inputNameEt.setText("");
                break;
            default:
                super.onClick(v);
                break;
        }
    }

    @CallSuper
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (onCancelSubmitInput(inputNameEt)) {
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 最大输入长度限制
     *
     * @return
     */
    protected abstract int getMaxInputLimitNum();


    /**
     * 提交输入
     *
     * @param et
     */
    protected abstract void onSubmitInput(EditText et);


    /**
     * 取消提交输入
     * 返回true  代表自己处理
     *
     * @param et
     */
    protected abstract boolean onCancelSubmitInput(EditText et);
}
