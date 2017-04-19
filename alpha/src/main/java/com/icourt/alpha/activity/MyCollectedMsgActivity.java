package com.icourt.alpha.activity;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.icourt.alpha.base.BaseActivity;

/**
 * Description 我收藏的消息
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/19
 * version 1.0.0
 */
public class MyCollectedMsgActivity extends BaseActivity {
    public static void launch(@NonNull Context context) {
        if (context == null) return;
        Intent intent = new Intent(context, MyCollectedMsgActivity.class);
        context.startActivity(intent);
    }
}
