package com.icourt.alpha.widget.dialog;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.support.v7.app.AlertDialog;
import android.widget.ListView;

import com.icourt.alpha.utils.SystemUtils;

/**
 * Description  设置分割线
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/30
 * version 1.0.0
 */
public class AlertListDialog extends AlertDialog {

    protected AlertListDialog(@NonNull Context context) {
        super(context);
    }

    protected AlertListDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
    }

    protected AlertListDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }


    public static class ListBuilder extends AlertDialog.Builder {
        private Drawable divider;
        private int dividerHeight;

        /**
         * 设置分割线
         *
         * @param divider
         * @return
         */
        public ListBuilder setDivider(Drawable divider) {
            this.divider = divider;
            return this;
        }

        /**
         * 设置分割线颜色
         *
         * @param color
         * @return
         */
        public ListBuilder setDividerColorRes(@ColorRes int color) {
            this.divider = new ColorDrawable(SystemUtils.getColor(getContext(), color));
            return this;
        }

        /**
         * 设置分割线颜色
         *
         * @param color
         * @return
         */
        public ListBuilder setDivider(@ColorInt int color) {
            this.divider = new ColorDrawable(color);
            return this;
        }

        /**
         * 设置分割线高度
         *
         * @param dividerHeight
         * @return
         */
        public ListBuilder setDividerHeight(int dividerHeight) {
            this.dividerHeight = dividerHeight;
            return this;
        }


        /**
         * 设置分割线高度
         *
         * @param id
         * @return
         */
        public ListBuilder setDividerHeightRes(@DimenRes int id) {
            this.dividerHeight = (int) getContext().getResources().getDimension(id);
            return this;
        }

        public ListBuilder(@NonNull Context context) {
            super(context);
        }

        public ListBuilder(@NonNull Context context, @StyleRes int themeResId) {
            super(context, themeResId);
        }

        @Override
        public AlertDialog create() {
            AlertDialog alertListDialog = super.create();
            ListView listView = alertListDialog.getListView();
            if (listView != null && divider != null) {
                //注意 顺序不能交换
                listView.setDivider(divider);
                listView.setDividerHeight(dividerHeight);
            }
            return alertListDialog;
        }
    }
}
