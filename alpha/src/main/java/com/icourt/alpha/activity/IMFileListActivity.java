package com.icourt.alpha.activity;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.icourt.alpha.base.BaseActivity;

/**
 * Description  文件列表界面
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/15
 * version 1.0.0
 */
public class IMFileListActivity extends BaseActivity {

    public static void launch(@NonNull Context context) {
        if (context == null) return;
        Intent intent = new Intent(context, IMFileListActivity.class);
        context.startActivity(intent);
    }
}
