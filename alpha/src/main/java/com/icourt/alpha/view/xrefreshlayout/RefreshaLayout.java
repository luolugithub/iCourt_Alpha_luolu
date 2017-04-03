package com.icourt.alpha.view.xrefreshlayout;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.andview.refreshview.XRefreshView;
import com.icourt.alpha.R;

/**
 * Description 自定义xrefresh  @{@link com.andview.refreshview.XRefreshView}
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/2
 * version 1.0.0
 */
public class RefreshaLayout extends XRefreshView {

    public RefreshaLayout(Context context) {
        this(context, null);
    }

    private View defalutContentEmptyView;
    private TextView contentEmptyText;

    public RefreshaLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        defalutContentEmptyView = View.inflate(context, R.layout.refresh_empty_view, null);
        setEmptyView(defalutContentEmptyView);
        contentEmptyText = (TextView) defalutContentEmptyView.findViewById(R.id.contentEmptyText);
    }

    public View getDefalutContentEmptyView() {
        return defalutContentEmptyView;
    }

    public TextView getContentEmptyText() {
        return contentEmptyText;
    }

    /**
     * 设置提示空的图片
     *
     * @param id
     */
    public void setNoticeEmptyImage(@DrawableRes int id) {
        contentEmptyText.setCompoundDrawablesWithIntrinsicBounds(0, id, 0, 0);
    }


    /**
     * 设置提示空的文字
     *
     * @param resId
     */
    public void setNoticeEmptyText(@StringRes int resId) {
        contentEmptyText.setText(resId);
    }

    /**
     * 设置提示空的文字
     *
     * @param text
     */
    public void setNoticeEmptyText(CharSequence text) {
        contentEmptyText.setText(text);
    }

    /**
     * 设置提示内容为空的图片与文字
     *
     * @param id
     * @param resId
     */
    public void setNoticeEmpty(@DrawableRes int id, @StringRes int resId) {
        this.setNoticeEmptyImage(id);
        this.setNoticeEmptyText(resId);
    }

    /**
     * 设置提示内容为空的图片与文字
     *
     * @param id
     * @param text
     */
    public void setNoticeEmpty(@DrawableRes int id, CharSequence text) {
        this.setNoticeEmptyImage(id);
        this.setNoticeEmptyText(text);
    }


    @Override
    public void enableEmptyView(boolean enable) {
        super.enableEmptyView(enable);
    }

    /**
     * 动态依据adapter 展示内容为空的布局
     *
     * @param adapter
     */
    public void enableEmptyViewWithAdapter(@NonNull RecyclerView.Adapter adapter) {
        if (adapter == null) return;
        if (adapter.getItemCount() > 0 && isEmptyViewShowing()) {
            enableEmptyView(false);
        } else if (adapter.getItemCount() <= 0 && !isEmptyViewShowing()) {
            enableEmptyView(true);
        }
    }
}
