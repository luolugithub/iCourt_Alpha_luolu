package com.icourt.alpha.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.andview.refreshview.callback.IHeaderCallBack;
import com.icourt.alpha.R;

/**
 * 计时列表 自定义刷新头部
 * Description
 * Company Beijing icourt
 * author  lu.zhao  E-mail:zhaolu@icourt.cc
 * date createTime：17/6/21
 * version 2.0.0
 */

public class CustomerXRefreshViewHeader extends LinearLayout implements IHeaderCallBack {
    private Context mContext;
    private ViewGroup headerView;
    private ImageView header_refresh_arrow_iv;
    private ProgressBar header_refresh_progressbar;
    private TextView header_refresh_title_tv, header_refresh_desc_tv;

    public CustomerXRefreshViewHeader(Context context) {
        this(context, null);
    }

    public CustomerXRefreshViewHeader(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        mContext = context;
        headerView = (ViewGroup) LayoutInflater.from(mContext).inflate(R.layout.header_customer_xrefresh, this);
        headerView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        header_refresh_arrow_iv = (ImageView) headerView.findViewById(R.id.header_refresh_arrow_iv);
        header_refresh_progressbar = (ProgressBar) headerView.findViewById(R.id.header_refresh_progressbar);
        header_refresh_title_tv = (TextView) headerView.findViewById(R.id.header_refresh_title_tv);
        header_refresh_desc_tv = (TextView) headerView.findViewById(R.id.header_refresh_desc_tv);
    }

    public void setHeaderRefreshDesc(CharSequence headerRefreshDesc) {
        header_refresh_desc_tv.setText(headerRefreshDesc);
    }

    public void setHeaderRefreshTitle(CharSequence headerRefreshTitle) {
        header_refresh_title_tv.setText(headerRefreshTitle);
    }

    @Override
    public void onStateNormal() {

    }

    @Override
    public void onStateReady() {

    }

    @Override
    public void onStateRefreshing() {
        header_refresh_arrow_iv.setVisibility(GONE);
        header_refresh_progressbar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onStateFinish(boolean b) {
        header_refresh_arrow_iv.setVisibility(VISIBLE);
        header_refresh_progressbar.setVisibility(View.GONE);
    }

    @Override
    public void onHeaderMove(double v, int i, int i1) {

    }

    @Override
    public void setRefreshTime(long l) {

    }

    @Override
    public void hide() {

    }

    @Override
    public void show() {

    }

    @Override
    public int getHeaderHeight() {
        return this.getMeasuredHeight();
    }
}
