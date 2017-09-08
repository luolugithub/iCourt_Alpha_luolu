package com.icourt.alpha.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.icourt.alpha.base.BaseActivity;
import com.icourt.alpha.utils.SFileTokenUtils;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/9/8
 * version 2.1.0
 */
public class FileBaseActivity extends BaseActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SFileTokenUtils.syncServerSFileToken();
    }
}
