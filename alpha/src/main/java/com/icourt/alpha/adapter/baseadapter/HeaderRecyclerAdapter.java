package com.icourt.alpha.adapter.baseadapter;

/**
 * @author xuanyouwu
 * @email xuanyouwu@163.com
 * @time 2016-03-31 13:53
 */
public abstract class HeaderRecyclerAdapter<H, T, F> extends HFRecyclerAdapter<H, T, F> {

    @Override
    public int bindFooterView() {
        return 0;
    }

    @Override
    public void onBindFooterHolder(ViewHolder holder, F f, int position) {

    }
}
