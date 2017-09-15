package com.icourt.alpha.activity;

import android.os.Bundle;
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
 * Description  编辑页面的模版类
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/8/18
 * version 2.1.0
 */
public abstract class SFileEditBaseActivity
        extends BaseActivity implements TextWatcher {

    @BindView(R.id.titleBack)
    CheckedTextView titleBack;
    @BindView(R.id.titleContent)
    TextView titleContent;
    @BindView(R.id.titleAction)
    CheckedTextView titleAction;
    @BindView(R.id.titleView)
    AppBarLayout titleView;
    @BindView(R.id.input_type_iv)
    ImageView inputTypeIv;
    @BindView(R.id.input_name_et)
    EditText inputNameEt;
    @BindView(R.id.input_clear_iv)
    ImageView inputClearIv;
    @BindView(R.id.input_limit_num_tv)
    TextView inputLimitNumTv;

    @Override
    protected final void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sfile_edit_base);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void initView() {
        super.initView();
        titleAction.setEnabled(false);
        setTitleActionTextView("完成");
        inputNameEt.setFilters(new InputFilter[]{new InputFilter.LengthFilter(getMaxInputLimitNum())});
        inputNameEt.addTextChangedListener(this);
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

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
                onCancelSubmitInput(inputNameEt);
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return onCancelSubmitInput(inputNameEt);
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
     *
     * @param et
     */
    protected abstract boolean onCancelSubmitInput(EditText et);
}
