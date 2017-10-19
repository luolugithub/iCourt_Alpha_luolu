package com.icourt.alpha.widget.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.support.v7.app.AlertDialog;
import android.view.View;

import com.icourt.alpha.R;
import com.icourt.alpha.view.ClearEditText;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/9/14
 * version 2.1.0
 */
public class RepoDecryptDialog extends AlertDialog implements View.OnClickListener {
    protected RepoDecryptDialog(@NonNull Context context) {
        super(context);
    }

    protected RepoDecryptDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
    }

    protected RepoDecryptDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    ClearEditText repoPwdEt;
    View cancelTv;
    View okTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_repo_decriypt);
        repoPwdEt = (ClearEditText) findViewById(R.id.repo_pwd_et);
        cancelTv = findViewById(R.id.cancel_tv);
        okTv = findViewById(R.id.ok_tv);
        cancelTv.setOnClickListener(this);
        okTv.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cancel_tv:
                dismiss();
                break;
            case R.id.ok_tv:
                break;
        }
    }
}
