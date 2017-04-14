package com.icourt.alpha.utils;

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/14
 * version 1.0.0
 */
public class SimpleViewGestureListener implements View.OnTouchListener {

    GestureDetector tabGestureDetector;

    public SimpleViewGestureListener(View view, GestureDetector.OnGestureListener onGestureListener) {
        tabGestureDetector = new GestureDetector(view.getContext(), onGestureListener);
        view.setOnTouchListener(this);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return tabGestureDetector.onTouchEvent(event);
    }
}
