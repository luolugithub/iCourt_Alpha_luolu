package com.icourt.alpha.view;

import android.content.Context;
import android.support.v7.widget.AppCompatCheckedTextView;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.icourt.alpha.utils.DensityUtil;

/**
 * Description  避免滑动点击问题
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/6/15
 * version 1.0.0
 */
public class ClickTouchTextView extends AppCompatCheckedTextView {
    public ClickTouchTextView(Context context) {
        super(context);
    }

    public ClickTouchTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ClickTouchTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    float startX;
    float endX;
    float startY;
    float endY;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = event.getRawX();
                startY = event.getRawY();
                break;
            case MotionEvent.ACTION_UP:
                endX = event.getRawX();
                endY = event.getRawY();
                int dp4 = DensityUtil.dip2px(getContext(), 4);
                if (Math.abs(startX - endX) > dp4 || Math.abs(startY - endY) > dp4) {
                    return true;
                }
                break;
        }
        return super.onTouchEvent(event);
    }
}
