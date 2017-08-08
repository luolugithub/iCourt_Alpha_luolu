package com.icourt.alpha.adapter.baseadapter;

import android.support.v4.app.FragmentManager;


/**
 * Description  可以刷新的的适配器
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/7/18
 * version 2.0.0
 */
public class BaseRefreshFragmentAdapter extends BaseFragmentAdapter {
    private String TAG = "";

    public BaseRefreshFragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    /**
     * 刷新 重载
     */
    public void notifyRefresh() {
        TAG = String.valueOf(System.currentTimeMillis());
        this.notifyDataSetChanged();
    }


    @Override
    public long getItemId(int position) {
        String id = String.format("%s_%s", TAG, position);
        return id.hashCode();
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}
