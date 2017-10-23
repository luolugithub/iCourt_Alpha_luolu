package com.icourt.alpha.activity;

import android.support.annotation.CallSuper;
import android.support.annotation.IntDef;
import android.view.KeyEvent;
import android.view.View;

import com.icourt.alpha.R;
import com.icourt.alpha.base.BaseActivity;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/10/10
 * version 2.1.0
 */
public abstract class ListenBackActivity extends BaseActivity {

    protected static final int FROM_BACK_KEY_EVENT = 0;
    protected static final int FROM_BACK_CLICK_EVENT = 1;

    @IntDef({FROM_BACK_KEY_EVENT,
            FROM_BACK_CLICK_EVENT})
    @Retention(RetentionPolicy.SOURCE)
    public @interface FROM_BACK {

    }

    @CallSuper
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.titleBack:
                if (!onPageBackClick(FROM_BACK_CLICK_EVENT)) {
                    onBackPressed();
                }
                break;
            default:
                super.onClick(v);
                break;
        }
    }
    /**
     * 页面返回 执行方法
     *
     * @param from
     * @return false 主动关闭页面
     */
    protected abstract boolean onPageBackClick(@FROM_BACK int from);

    @CallSuper
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (onPageBackClick(FROM_BACK_KEY_EVENT)) {
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
