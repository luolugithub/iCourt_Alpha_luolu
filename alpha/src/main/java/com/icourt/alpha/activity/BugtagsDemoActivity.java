package com.icourt.alpha.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.bugtags.library.Bugtags;
import com.icourt.alpha.R;
import com.icourt.alpha.base.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/7
 * version 1.0.0
 */
public class BugtagsDemoActivity extends BaseActivity {

    @BindView(R.id.editText)
    EditText editText;
    @BindView(R.id.button)
    Button button;

    public static void launch(@NonNull Context context) {
        if (context == null) return;
        Intent intent = new Intent(context, BugtagsDemoActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bugtags);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void initView() {
        super.initView();
    }

    @OnClick({R.id.button})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button:
                if (TextUtils.isEmpty(editText.getText())) {
                    showTopSnackBar("请输入bug");
                    return;
                }
                Bugtags.sendFeedback(editText.getText().toString());
                break;
            default:
                super.onClick(v);
                break;
        }

    }
}
