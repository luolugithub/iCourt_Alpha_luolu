package com.icourt.alpha.adapter.baseadapter;

import android.support.annotation.LayoutRes;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Description 封装 PagerAdapter 简化开发
 * Company Beijing guokeyuzhou
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：16/12/9
 * version
 * 现在viewpager逐渐被取代 替代recyclerView[横向]
 */

public abstract class BasePagerAdapter<T> extends PagerAdapter implements View.OnClickListener, View.OnLongClickListener {

    @Override
    public void onClick(View v) {
        if (onPagerItemClickListener != null) {
            onPagerItemClickListener.OnItemClick(BasePagerAdapter.this, v, viewIntegerHashMap.get(v));
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if (onPagerItemLongClickListener != null) {
            return onPagerItemLongClickListener.OnItemLongClick(BasePagerAdapter.this, v, viewIntegerHashMap.get(v));
        }
        return false;
    }

    public interface OnPagerItemClickListener {

        /**
         * @param adapter
         * @param v       点击的控件
         * @param pos     点击的位置[在adapter中]
         */
        void OnItemClick(BasePagerAdapter adapter, View v, int pos);
    }

    public interface OnPagerItemLongClickListener {

        /**
         * @param adapter
         * @param v       点击的控件
         * @param pos     点击的位置[在adapter中]
         */
        boolean OnItemLongClick(BasePagerAdapter adapter, View v, int pos);
    }


    private final List<T> datas = new ArrayList<>();
    private HashMap<View, Integer> viewIntegerHashMap = new HashMap<>();

    public void bindData(boolean isRefresh, List<T> data) {
        if (isRefresh) {
            datas.clear();
        }
        datas.addAll(data);
        notifyDataSetChanged();
    }

    private boolean isCanupdateItem;

    private OnPagerItemClickListener onPagerItemClickListener;

    private OnPagerItemLongClickListener onPagerItemLongClickListener;

    public void setOnPagerItemLongClickListener(OnPagerItemLongClickListener onPagerItemLongClickListener) {
        this.onPagerItemLongClickListener = onPagerItemLongClickListener;
    }

    public void setOnPagerItemClickListener(OnPagerItemClickListener onPagerItemClickListener) {
        this.onPagerItemClickListener = onPagerItemClickListener;
    }


    public boolean isCanupdateItem() {
        return isCanupdateItem;
    }

    public void setCanupdateItem(boolean canupdateItem) {
        this.isCanupdateItem = canupdateItem;
    }

    public BasePagerAdapter(boolean isCanupdateItem) {
        this.isCanupdateItem = isCanupdateItem;
    }

    public BasePagerAdapter() {
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    public T getItem(int pos) {
        if (pos >= 0 && pos < getCount()) {
            return datas.get(pos);
        }
        return null;
    }

    public void putItem(int pos, T t) {
        if (pos >= 0 && pos < getCount()) {
            datas.set(pos, t);
        }
    }


    /**
     * 绑定布局id
     *
     * @param pos
     * @return
     */
    @LayoutRes
    public abstract int bindView(int pos);

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View itemView = View.inflate(container.getContext(), bindView(position), null);
        itemView.setOnClickListener(this);
        itemView.setOnLongClickListener(this);
        container.addView(itemView);
        viewIntegerHashMap.put(itemView, position);
        bindDataToItem(getItem(position), container, itemView, position);
        return itemView;
    }

    public abstract void bindDataToItem(T t, ViewGroup container, View itemView, int pos);

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        viewIntegerHashMap.remove(object);
        container.removeView((View) object);
    }

    @Override
    public int getItemPosition(Object object) {
        return isCanupdateItem ? POSITION_NONE : super.getItemPosition(object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
}
