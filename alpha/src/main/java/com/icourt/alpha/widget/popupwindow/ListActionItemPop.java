package com.icourt.alpha.widget.popupwindow;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.adapter.baseadapter.BaseArrayRecyclerAdapter;
import com.icourt.alpha.entity.bean.ItemsEntityImp;

import java.util.List;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/17
 * version 1.0.0
 */
public class ListActionItemPop<T extends ItemsEntityImp> extends BaseListActionItemPop<T> {

    public ListActionItemPop(@NonNull Context context, List<T> items) {
        super(context, new ListActionItemAdapter<T>());
        getAdapter().bindData(true, items);
        setAnimationStyle(R.style.SlideAnimBottom);
    }

    public ListActionItemPop(@NonNull Context context, @NonNull BaseArrayRecyclerAdapter<T> adapter) {
        super(context, adapter);
    }

    public static class ListActionItemAdapter<T extends ItemsEntityImp> extends BaseArrayRecyclerAdapter<T> {

        @Override
        public int bindView(int viewtype) {
            return R.layout.adapter_list_action_item_pop;
        }

        @Override
        public void onBindHoder(ViewHolder holder, T t, int position) {
            if (t == null) return;
            TextView item_title = holder.obtainView(R.id.item_title);
            item_title.setCompoundDrawablesWithIntrinsicBounds(0, t.getItemIconRes(), 0, 0);
            item_title.setText(t.getItemTitle());
        }
    }
}
