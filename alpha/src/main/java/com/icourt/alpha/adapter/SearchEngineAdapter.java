package com.icourt.alpha.adapter;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.adapter.baseadapter.MultiSelectRecyclerAdapter;
import com.icourt.alpha.entity.bean.SearchEngineEntity;
import com.icourt.alpha.utils.DateUtils;
import com.icourt.alpha.utils.SystemUtils;
import com.icourt.alpha.widget.comparators.LongFieldEntityComparator;

import java.util.ArrayList;
import java.util.Collections;


/**
 * Description  搜索引擎tag适配器
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/20
 * version 1.0.0
 */
public class SearchEngineAdapter extends MultiSelectRecyclerAdapter<SearchEngineEntity> implements BaseRecyclerAdapter.OnItemClickListener {

    private LongFieldEntityComparator<SearchEngineEntity> longFieldEntityComparator = new LongFieldEntityComparator<>(LongFieldEntityComparator.ORDER.ASC);

    public SearchEngineAdapter() {
        this.setOnItemClickListener(this);
    }

    @Override
    public int bindView(int viewtype) {
        return R.layout.adapter_item_search_engine;
    }

    @NonNull
    @Override
    public ArrayList<SearchEngineEntity> getSelectedData() {
        //按时间升序排列  先选中的在前面
        ArrayList<SearchEngineEntity> selectedData = super.getSelectedData();
        if (selectedData != null) {
            try {
                Collections.sort(selectedData, longFieldEntityComparator);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        return selectedData;
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
        toggleSelected(position);
        SearchEngineEntity item = getItem(position);
        if (item != null) {
            //设置选中的时间
            item.checkedTime = DateUtils.millis();
        }
    }
}
