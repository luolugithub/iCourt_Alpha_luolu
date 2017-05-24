package com.icourt.alpha.adapter;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.adapter.baseadapter.BaseArrayRecyclerAdapter;
import com.icourt.alpha.entity.bean.SearchPolymerizationEntity;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/5/24
 * version 1.0.0
 */
public class SearchPolymerizationAdapter extends BaseArrayRecyclerAdapter<SearchPolymerizationEntity> {
    @Override
    public int bindView(int viewtype) {
        return R.layout.adapter_search_polymerization;
    }

    @Override
    public void onBindHoder(ViewHolder holder, SearchPolymerizationEntity searchPolymerizationEntity, int position) {
        if (searchPolymerizationEntity == null) return;
        TextView search_title_tv = holder.obtainView(R.id.search_title_tv);
        TextView search_more_tv = holder.obtainView(R.id.search_more_tv);
        RecyclerView search_recyclerView = holder.obtainView(R.id.search_recyclerView);
        search_title_tv.setText(searchPolymerizationEntity.headerTitle);
        search_more_tv.setText(searchPolymerizationEntity.footerTitle);

        SearchItemAdapter searchItemAdapter;
        if (search_recyclerView.getLayoutManager() == null) {
            search_recyclerView.setLayoutManager(new LinearLayoutManager(search_recyclerView.getContext()));
            search_recyclerView.setAdapter(searchItemAdapter = new SearchItemAdapter(3));
        } else {
            searchItemAdapter = (SearchItemAdapter) search_recyclerView.getAdapter();
        }
        searchItemAdapter.bindData(true, searchPolymerizationEntity.data);
        search_more_tv.setVisibility(searchPolymerizationEntity.data.size() > 3 ? View.VISIBLE : View.GONE);
    }
}
