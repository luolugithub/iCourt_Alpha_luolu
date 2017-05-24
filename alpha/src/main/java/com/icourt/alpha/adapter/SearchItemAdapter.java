package com.icourt.alpha.adapter;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.adapter.baseadapter.BaseArrayRecyclerAdapter;
import com.icourt.alpha.entity.bean.ISearchItemEntity;
import com.icourt.alpha.utils.GlideUtils;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/5/24
 * version 1.0.0
 */
public class SearchItemAdapter<T extends ISearchItemEntity> extends BaseArrayRecyclerAdapter<T> {
    public int maxItemCount = 3;

    public SearchItemAdapter(int maxItemCount) {
        this.maxItemCount = maxItemCount;
    }

    @Override
    public int bindView(int viewtype) {
        return R.layout.adapter_item_search;
    }

    @Override
    public void onBindHoder(ViewHolder holder, T t, int position) {
        if (t == null) return;
        ImageView serach_iv = holder.obtainView(R.id.serach_iv);
        TextView serach_title_tv = holder.obtainView(R.id.serach_title_tv);
        TextView serach_content_tv = holder.obtainView(R.id.serach_content_tv);
        GlideUtils.loadUser(serach_iv.getContext(), t.getIcon(), serach_iv);
        serach_title_tv.setText(t.getTitle());
        serach_content_tv.setText(t.getContent());
        serach_content_tv.setVisibility(TextUtils.isEmpty(t.getContent()) ? View.GONE : View.VISIBLE);
    }

    @Override
    public int getItemCount() {
        int itemCount = super.getItemCount();
        return Math.min(itemCount, maxItemCount);
    }

}
