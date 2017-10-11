package com.icourt.alpha.widget.filter;

import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

/**
 * Description  屏蔽会车
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/10/11
 * version 2.1.0
 */
public class InputActionNextFilter implements TextView.OnEditorActionListener {
    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        return (actionId == EditorInfo.IME_ACTION_NEXT) || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER);
    }
}
