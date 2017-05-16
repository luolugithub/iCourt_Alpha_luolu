package com.icourt.alpha.adapter;

import android.widget.ImageView;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.adapter.baseadapter.SelectedRecyclerAdapter;
import com.icourt.alpha.entity.bean.WorkType;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/5/16
 * version 1.0.0
 */
public class WorkTypeAdapter extends SelectedRecyclerAdapter<WorkType> {
    public WorkTypeAdapter(boolean selectable) {
        super(selectable);
    }

    @Override
    public int bindView(int viewtype) {
        return R.layout.adapter_item_work_type;
    }

    @Override
    public void onBindSelectableHoder(ViewHolder holder, WorkType workType, int position, boolean selected) {
        if (workType == null) return;
        TextView work_type_title_tv = holder.obtainView(R.id.work_type_title_tv);
        work_type_title_tv.setText(workType.name);
        ImageView work_type_arrow_iv = holder.obtainView(R.id.work_type_arrow_iv);
        work_type_arrow_iv.setImageResource(selected ? R.mipmap.checkmark : 0);
    }
}
