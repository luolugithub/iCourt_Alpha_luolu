package com.icourt.alpha.adapter.baseadapter;

import android.databinding.DataBindingUtil;
import android.view.ViewGroup;

/**
 * Description  适用于mvvm架构的adapter
 * Company Beijing guokeyuzhou
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：17/3/14
 * version
 */

public abstract class BaseDataBindingAdapter<T> extends BaseArrayRecyclerAdapter<T> {


    @Override
    public final ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewtype) {
        ViewHolder viewHolder = super.onCreateViewHolder(viewGroup, viewtype);
        viewHolder.setBinding(DataBindingUtil.bind(viewHolder.itemView));
        return viewHolder;
    }

    @Override
    public void onBindHoder(ViewHolder holder, T t, int position) {
        if (holder.getBinding() != null) {
            holder.getBinding().setVariable(bindVariableId(), t);
            holder.getBinding().executePendingBindings();
        }
    }

    public abstract int bindVariableId();

}
