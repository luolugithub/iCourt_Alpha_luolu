package com.icourt.alpha.adapter;

import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.adapter.baseadapter.MultiSelectRecyclerAdapter;
import com.icourt.alpha.entity.bean.SearchEngineEntity;
import com.icourt.alpha.utils.SystemUtils;


/**
 * Description  搜索引擎tag适配器
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/20
 * version 1.0.0
 */
public class SearchEngineAdapter extends MultiSelectRecyclerAdapter<SearchEngineEntity> implements BaseRecyclerAdapter.OnItemClickListener {

    public SearchEngineAdapter() {
        this.setOnItemClickListener(this);
    }

    @Override
    public int bindView(int viewtype) {
        return R.layout.adapter_item_search_engine;
    }

    @Override
    public void onBindSelectableHolder(ViewHolder holder, SearchEngineEntity searchEngineEntity, boolean selected, int position) {
        if (searchEngineEntity == null) return;
        TextView item_text = holder.obtainView(R.id.item_text);
        item_text.setText(searchEngineEntity.name);
        item_text.setBackgroundResource(!selected ? R.drawable.flowlayout_item_tagview_unselected_bg : R.drawable.flowlayout_item_tagview_selected_bg);
        item_text.setTextColor(selected ? Color.WHITE : SystemUtils.getColor(item_text.getContext(), R.color.alpha_font_color_gray));
    }

    @Override
    public void onItemClick(BaseRecyclerAdapter adapter, ViewHolder holder, View view, int position) {
        toggleSelected(getRealPos(position));
    }
}
