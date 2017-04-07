package com.icourt.alpha.utils;

import android.app.Activity;
import android.graphics.Color;
import android.support.annotation.UiThread;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.androidadvance.topsnackbar.TSnackbar;
import com.icourt.alpha.R;

/**
 * Description snackBar提示工具类
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：16/6/21
 * version
 */
public class SnackbarUtils {
    @UiThread
    private static Snackbar makeBottomSnackBar(View view, CharSequence snack, int duration) {
        Snackbar snackbar = Snackbar.make(view, snack, Snackbar.LENGTH_SHORT);
        snackbar.getView().setBackgroundColor(Color.GRAY);
        ((TextView) snackbar.getView().findViewById(android.support.design.R.id.snackbar_text)).setTextColor(Color.WHITE);
        return snackbar;
    }

    @UiThread
    public static void showBottomSnack(View view, CharSequence snack) {
        if (view == null) return;
        if (TextUtils.isEmpty(snack)) return;
        makeBottomSnackBar(view, snack, Snackbar.LENGTH_SHORT).show();
    }

    @UiThread
    public static void showBottomSnack(View view, int resId) {
        if (view == null) return;
        String resStr = view.getContext().getResources().getString(resId);
        if (TextUtils.isEmpty(resStr)) return;
        showBottomSnack(view, resStr);
    }
    @UiThread
    public static void showBottomSnack(Activity activity, CharSequence msg) {
        if (activity == null) return;
        if (TextUtils.isEmpty(msg)) return;
        showBottomSnack(activity.getWindow().getDecorView(), msg);
    }

    @UiThread
    public static void showLongBottomSnack(Activity activity, CharSequence msg) {
        if (activity == null) return;
        if (TextUtils.isEmpty(msg)) return;
        View view = activity.getWindow().getDecorView();
        if (view == null) return;
        makeBottomSnackBar(view, msg, Snackbar.LENGTH_LONG).show();
    }

    /******************
     * topSnackBar
     ******************/
    @UiThread
    public static void showTopSnackBar(Activity activity, CharSequence msg) {
        if (activity == null) return;
        showTopSnackBar(activity, msg, Color.WHITE, 0xFF17AF6C);
    }
    @UiThread
    public static void showTopSnackBar(View view, CharSequence msg) {
        if (view == null) return;
        showTopSnackBar(view, msg, Color.WHITE, 0xFF17AF6C);
    }

    /**
     * 比较严重错误 红色背景
     *
     * @param view
     * @param msg
     */
    @UiThread
    public static void showTopSnackBarWithError(View view, CharSequence msg) {
        if (view == null) return;
        showTopSnackBar(view, msg, Color.YELLOW, 0xFFCC00CC);
    }

    /**
     * 比较严重错误 红色背景
     *
     * @param activity
     * @param msg
     */
    @UiThread
    public static void showTopSnackBarWithError(Activity activity, CharSequence msg) {
        if (activity == null) return;
        View view = activity.getWindow().getDecorView();
        if (view == null) return;
        showTopSnackBar(view, msg, Color.YELLOW, 0xFFCC00CC);
    }

    public static void showTopSnackBar(Activity activity, CharSequence msg, int textColor, int backgroundColor) {
        View view = activity.getWindow().getDecorView();
        if (view == null) return;
        showTopSnackBar(view, msg, textColor, backgroundColor);
    }

    @UiThread
    public static void showTopSnackBar(View view, CharSequence msg, int textColor, int backgroundColor) {
        if (TextUtils.isEmpty(msg)) return;
        TSnackbar snackbar = TSnackbar
                .make(view, msg, TSnackbar.LENGTH_SHORT);
        snackbar.setActionTextColor(Color.WHITE);
        View snackbarView = snackbar.getView();
        if (view != null && view.getContext() != null) {
            int dp20 = DensityUtil.dip2px(view.getContext(), 20);
            snackbarView.setPadding(0, dp20, 0, 0);
        }
        snackbarView.setBackgroundColor(backgroundColor);
        TextView textView = (TextView) snackbarView.findViewById(R.id.snackbar_text);
        textView.setTextColor(textColor);
        snackbar.show();
    }
}
