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

import com.andview.refreshview.XRefreshView;
import com.andview.refreshview.callback.IFooterCallBack;
import com.icourt.alpha.R;

/**
 * Description  自定义footer
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/6/9
 * version 1.0.0
 */
public class CustomerXRefreshViewFooter extends LinearLayout implements IFooterCallBack {
    private Context mContext;
    private boolean showing = true;
    private ViewGroup moreView;
    private ImageView footer_loadmore_arrow_iv;
    private ProgressBar footer_loadmore_progressbar;
    private TextView footer_loadmore_title_tv, footer_loadmore_desc_tv;

    public CustomerXRefreshViewFooter(Context context) {
        this(context, null);
    }

    public CustomerXRefreshViewFooter(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    @Override
    public void callWhenNotAutoLoadMore(final XRefreshView xRefreshView) {
        footer_loadmore_title_tv.setText(R.string.xrefreshview_footer_hint_click);
        setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                xRefreshView.notifyLoadMore();
            }
        });
    }

    @Override
    public void onStateReady() {
        footer_loadmore_arrow_iv.setVisibility(VISIBLE);
        footer_loadmore_progressbar.setVisibility(View.GONE);
        footer_loadmore_title_tv.setText(R.string.xrefreshview_footer_hint_ready);
    }

    public void setFooterLoadmoreDesc(CharSequence footerLoadmoreDesc) {
        footer_loadmore_desc_tv.setText(footerLoadmoreDesc);
    }

    public void setFooterLoadmoreTitle(CharSequence footerLoadmoreTitle) {
        footer_loadmore_title_tv.setText(footerLoadmoreTitle);
    }


    @Override
    public void onStateRefreshing() {
        footer_loadmore_arrow_iv.setVisibility(GONE);
        footer_loadmore_progressbar.setVisibility(View.VISIBLE);
        show(true);
    }

    @Override
    public void onReleaseToLoadMore() {
        footer_loadmore_arrow_iv.setVisibility(VISIBLE);
        footer_loadmore_progressbar.setVisibility(View.GONE);
        footer_loadmore_title_tv.setText(R.string.xrefreshview_footer_hint_ready);
    }

    @Override
    public void onStateFinish(boolean hideFooter) {
        footer_loadmore_arrow_iv.setVisibility(VISIBLE);
        footer_loadmore_progressbar.setVisibility(View.GONE);
    }

    @Override
    public void onStateComplete() {
        footer_loadmore_title_tv.setText(R.string.xrefreshview_footer_hint_complete);
        footer_loadmore_title_tv.setVisibility(View.VISIBLE);
        footer_loadmore_progressbar.setVisibility(View.GONE);
    }

    @Override
    public void show(final boolean show) {
        if (show == showing) {
            return;
        }
        showing = show;
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) moreView
                .getLayoutParams();
        lp.height = show ? LayoutParams.WRAP_CONTENT : 0;
        moreView.setLayoutParams(lp);
    }

    @Override
    public boolean isShowing() {
        return showing;
    }

    @Override
    public int getFooterHeight() {
        return getMeasuredHeight();
    }


    private void initView(Context context) {
        mContext = context;
        moreView = (ViewGroup) LayoutInflater.from(mContext).inflate(R.layout.footer_customer_xrefresh, this);
        moreView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        footer_loadmore_arrow_iv = (ImageView) moreView.findViewById(R.id.footer_loadmore_arrow_iv);
        footer_loadmore_progressbar = (ProgressBar) moreView.findViewById(R.id.footer_loadmore_progressbar);
        footer_loadmore_title_tv = (TextView) moreView.findViewById(R.id.footer_loadmore_title_tv);
        footer_loadmore_desc_tv = (TextView) moreView.findViewById(R.id.footer_loadmore_desc_tv);
    }
}
