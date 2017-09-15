package com.icourt.alpha.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.base.BaseActivity;
import com.icourt.alpha.constants.Const;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.icourt.alpha.constants.Const.PROJECT_CASENUMBER_TYPE;
import static com.icourt.alpha.constants.Const.PROJECT_CASE_TYPE;
import static com.icourt.alpha.constants.Const.PROJECT_COMPETENT_TYPE;
import static com.icourt.alpha.constants.Const.PROJECT_NAME_TYPE;
import static com.icourt.alpha.constants.Const.PROJECT_NUMBER_TYPE;
import static com.icourt.alpha.constants.Const.PROJECT_REMARK_TYPE;
import static com.icourt.alpha.constants.Const.PROJECT_TYPE_TYPE;

/**
 * Description  项目概览：具体信息展示
 * Company Beijing icourt
 * author  lu.zhao  E-mail:zhaolu@icourt.cc
 * date createTime：17/8/18
 * version 2.0.0
 */

public class ProjectBasicTextInfoActivity extends BaseActivity {

    private static final String KEY_TEXT = "key_text";
    private static final String KEY_VALUE_TEXT = "key_value_text";
    private static final String KEY_TYPE = "key_type";
    String key, value;
    int type;
    @BindView(R.id.titleBack)
    ImageView titleBack;
    @BindView(R.id.titleContent)
    TextView titleContent;
    @BindView(R.id.titleView)
    AppBarLayout titleView;
    @BindView(R.id.desc_text)
    TextView descText;
    @BindView(R.id.content_length_tv)
    TextView contentLengthTv;

    public static void launch(@NonNull Context context, String key, @NonNull String value, @Const.PROJECT_INFO_TEXT_TYPE int type) {
        if (context == null) return;
        Intent intent = new Intent(context, ProjectBasicTextInfoActivity.class);
        intent.putExtra(KEY_TEXT, key);
        intent.putExtra(KEY_VALUE_TEXT, value);
        intent.putExtra(KEY_TYPE, type);
        context.startActivity(intent);
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_basic_text_layout);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void initView() {
        super.initView();
        key = getIntent().getStringExtra(KEY_TEXT);
        value = getIntent().getStringExtra(KEY_VALUE_TEXT);
        type = getIntent().getIntExtra(KEY_TYPE, -1);
        titleContent.setText(key);
        descText.setText(value);
    }

    /**
     * 设置头部信息
     *
     * @return
     */
    private String getTitleText() {
        switch (type) {
            case PROJECT_NAME_TYPE:
                return "项目名称";
            case PROJECT_TYPE_TYPE:
                return "项目类型";
            case PROJECT_NUMBER_TYPE:
                return "项目编号";
            case PROJECT_REMARK_TYPE:
                return "项目备注";
            case Const.PROJECT_CASEPROCESS_TYPE://程序名称
                return "程序名称";
            case Const.PROJECT_PRICE_TYPE:
                return "标的";
            case PROJECT_CASE_TYPE:
                return "案由";
            case PROJECT_CASENUMBER_TYPE:
                return "案号";
            case PROJECT_COMPETENT_TYPE:
                return "法院";
        }
        return "项目信息";
    }
}
