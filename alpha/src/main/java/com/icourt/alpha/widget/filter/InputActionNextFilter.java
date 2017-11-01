package com.icourt.alpha.widget.filter;

import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

/**
 * Description  屏蔽回车键
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/10/11
 * version 2.1.0
 */
public class InputActionNextFilter implements TextView.OnEditorActionListener {


    @Override
    public final boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        boolean isActionNext = (actionId == EditorInfo.IME_ACTION_NEXT)
                || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER);
        if (isActionNext) {
            return onInputActionNext(v);
        }
        return false;
    }

    /**
     * 回车键  默认返回true
     *
     * @param v
     * @return
     */
    public boolean onInputActionNext(TextView v) {
        return true;
    }
}
