package com.icourt.alpha.adapter;

import android.widget.CheckedTextView;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.adapter.baseadapter.MultiSelectRecyclerAdapter;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/8/18
 * version 2.1.0
 */
public class FileSoryTypeAdapter extends MultiSelectRecyclerAdapter<String> {
    @Override
    public int bindView(int viewtype) {
        return R.layout.adapter_item_file_sort_type;
    }

    @Override
    public void onBindSelectableHolder(ViewHolder holder, String s, boolean selected, int position) {
        TextView textView = holder.obtainView(R.id.textView);
        textView.setText(s);
        CheckedTextView ctv_select = holder.obtainView(R.id.ctv_select);
        ctv_select.setBackgroundResource(selected?R.mipmap.checkmark:0);
    }
}
