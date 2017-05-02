package com.icourt.alpha.adapter;

import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.adapter.baseadapter.BaseArrayRecyclerAdapter;
import com.icourt.alpha.db.dbmodel.SearchEngineModel;
import com.icourt.alpha.db.dbmodel.SearhHistoryModel;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/20
 * version 1.0.0
 */
public class SearchHistoryAdapter extends BaseArrayRecyclerAdapter<SearhHistoryModel> {

    private static final int TAG_MAX_NUM = 3;

    @Override
    public int bindView(int viewtype) {
        return R.layout.adapter_item_search_history;
    }


    @Override
    public void onBindHoder(ViewHolder holder, SearhHistoryModel searhHistoryModel, int position) {
        if (searhHistoryModel == null) return;
        TextView search_keyWord_tv = holder.obtainView(R.id.search_keyWord_tv);
        TextView search_engines_tv = holder.obtainView(R.id.search_engines_tv);

        StringBuilder stringBuilder = new StringBuilder();
        if (searhHistoryModel.searchEngines != null) {
            for (int i = 0; i < Math.min(TAG_MAX_NUM, searhHistoryModel.searchEngines.size()); i++) {
                SearchEngineModel searchEngineModel = searhHistoryModel.searchEngines.get(i);
                if (searchEngineModel != null) {
                    stringBuilder.append(searchEngineModel.name);
                    stringBuilder.append(" ");
                }
            }
            if (searhHistoryModel.searchEngines.size() > TAG_MAX_NUM) {
                stringBuilder.append("...");
            }
        }
        search_engines_tv.setText(stringBuilder.toString());
        search_keyWord_tv.setText(searhHistoryModel.keyWord);
    }
}
