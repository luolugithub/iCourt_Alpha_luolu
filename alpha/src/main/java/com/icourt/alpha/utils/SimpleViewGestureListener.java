package com.icourt.alpha.utils;

import android.support.annotation.NonNull;
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
public class SimpleViewGestureListener extends GestureDetector.SimpleOnGestureListener implements View.OnTouchListener {

    @Override
    public final boolean onDown(MotionEvent e) {
        return onViewGestureListener.onDown(view, e);
    }

    @Override
    public final void onShowPress(MotionEvent e) {
        onViewGestureListener.onShowPress(view, e);
    }

    @Override
    public final boolean onSingleTapUp(MotionEvent e) {
        return onViewGestureListener.onSingleTapUp(view, e);
    }

    @Override
    public final boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return onViewGestureListener.onScroll(view, e1, e2, distanceX, distanceY);
    }

    @Override
    public final void onLongPress(MotionEvent e) {
        onViewGestureListener.onLongPress(view, e);
    }

    @Override
    public final boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return onViewGestureListener.onFling(view, e1, e2, velocityX, velocityY);
    }

    @Override
    public final boolean onSingleTapConfirmed(MotionEvent e) {
        return onViewGestureListener.onSingleTapConfirmed(view, e);
    }

    @Override
    public final boolean onDoubleTap(MotionEvent e) {
        return onViewGestureListener.onDoubleTap(view, e);
    }

    @Override
    public final boolean onDoubleTapEvent(MotionEvent e) {
        return onViewGestureListener.onDoubleTapEvent(view, e);
    }


    public interface OnViewGestureListener {

        boolean onDown(View v, MotionEvent e);

        void onShowPress(View v, MotionEvent e);

        boolean onSingleTapUp(View v, MotionEvent e);

        boolean onScroll(View v, MotionEvent e1, MotionEvent e2, float distanceX, float distanceY);

        void onLongPress(View v, MotionEvent e);

        boolean onFling(View v, MotionEvent e1, MotionEvent e2, float velocityX, float velocityY);

        boolean onSingleTapConfirmed(View v, MotionEvent e);

        boolean onDoubleTap(View v, MotionEvent e);

        boolean onDoubleTapEvent(View v, MotionEvent e);
    }

    public static class OnSimpleViewGestureListener implements OnViewGestureListener {

        @Override
        public boolean onDown(View v, MotionEvent e) {
            return false;
        }

        @Override
        public void onShowPress(View v, MotionEvent e) {

        }

        @Override
        public boolean onSingleTapUp(View v, MotionEvent e) {
            return false;
        }

        @Override
        public boolean onScroll(View v, MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return false;
        }

        @Override
        public void onLongPress(View v, MotionEvent e) {

        }

        @Override
        public boolean onFling(View v, MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return false;
        }

        @Override
        public boolean onSingleTapConfirmed(View v, MotionEvent e) {
            return false;
        }

        @Override
        public boolean onDoubleTap(View v, MotionEvent e) {
            return false;
        }

        @Override
        public boolean onDoubleTapEvent(View v, MotionEvent e) {
            return false;
        }
    }


    private GestureDetector tabGestureDetector;
    private OnViewGestureListener onViewGestureListener;
    private View view;

    public SimpleViewGestureListener(@NonNull View view, @NonNull OnViewGestureListener l) {
        this.view = view;
        this.onViewGestureListener = l;
        tabGestureDetector = new GestureDetector(view.getContext(), this);
        view.setOnTouchListener(this);
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return tabGestureDetector.onTouchEvent(event);
    }
}
