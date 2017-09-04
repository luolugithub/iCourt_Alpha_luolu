package com.icourt.alpha.adapter.baseadapter;

import android.support.annotation.CallSuper;

import java.util.ArrayList;
import java.util.List;

/**
 * ClassName BaseTaskRecyclerAdapter
 * Description
 * Company icourt
 * author  zhaodanyang  E-mail:zhaodanyang@icourt.cc
 * date createTime：2017/9/1 9:56
 * version
 * 这个Adapter是为了实现Adapter内部数据源的引用和外部是同一个数据源。
 */
public abstract class BaseTaskRecyclerAdapter<T> extends BaseRecyclerAdapter {
    private List<T> dataList = new ArrayList<>();

    public List<T> getData() {
        return dataList;
    }

    public T getData(int position) {
        if (position < 0 || position >= dataList.size()) return null;
        return dataList.get(position);
    }


    public boolean bindData(boolean isRefresh, List<T> datas) {
        if (datas == null) {
            dataList.clear();
        } else {
            dataList = datas;
        }
        notifyDataSetChanged();
        return true;
    }

    public void clearData() {
        dataList.clear();
        notifyDataSetChanged();
    }

    @Override
    public T getItem(int position) {
        if (position < 0 || position >= dataList.size()) return null;
        return dataList.get(position);
    }

    public boolean addItem(int position, T t) {
        if (t == null) return false;
        if (position < 0 || position > dataList.size()) return false;
        if (dataList.contains(t)) return false;
        dataList.add(position, t);
        notifyItemInserted(position);
        return true;
    }

    public boolean addItems(int pos, List<? extends T> datas) {
        if (datas == null) return false;
        if (datas.contains(datas)) return false;
        dataList.addAll(pos, datas);
        notifyItemRangeInserted(pos, datas.size());
        return true;
    }

    public boolean addItems(List<? extends T> datas) {
        if (datas == null) return false;
        if (datas.contains(datas)) return false;
        dataList.addAll(datas);
        notifyItemRangeInserted(getItemCount() - datas.size() >= 0 ? getItemCount() - datas.size() : 0, datas.size());
        return true;
    }

    public boolean addItem(T t) {
        if (t == null) return false;
        if (dataList.contains(t)) return false;
        boolean b = dataList.add(t);
        notifyItemInserted(dataList.size() - 1);
        return b;
    }


    public boolean updateItem(int position) {
        if (position < 0 || position >= dataList.size()) return false;
        notifyItemChanged(position);
        return true;
    }

    public boolean updateItem(T t) {
        if (t == null) return false;
        int index = dataList.indexOf(t);
        if (index >= 0 && index < dataList.size()) {
            dataList.set(index, t);
            notifyItemChanged(index);
            return true;
        }
        return false;
    }

    public boolean updateItem(int position, T t) {
        if (position < 0 || position >= dataList.size()) return false;
        if (t == null) return false;
        dataList.set(position, t);
        notifyItemChanged(position);
        return true;
    }

    public boolean removeItem(int position) {
        if (position < 0 || position >= dataList.size()) return false;
        dataList.remove(position);
        notifyItemRemoved(position);
        return true;
    }

    public boolean removeItems(int startPos, int itemCount) {
        if (startPos < 0 || startPos >= dataList.size()) return false;
        if (itemCount <= 0) return false;
        int endIndex = ((itemCount + startPos) >= dataList.size() ? dataList.size() : (itemCount + startPos));
        int realItemCount = (itemCount + startPos) < dataList.size() ? itemCount : dataList.size() - startPos;
        List<T> ts = new ArrayList<T>(dataList.subList(startPos, endIndex));
        boolean result = dataList.removeAll(ts);
        if (result) {
            notifyItemRangeRemoved(startPos, realItemCount);
        }
        return result;
    }

    public boolean removeItem(T t) {
        if (t == null) return false;
        int index = dataList.indexOf(t);
        if (index >= 0) {
            dataList.remove(index);
            notifyItemRemoved(index);
            return true;
        }
        return false;
    }


    @CallSuper
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        onBindHoder(holder, getData(position), position);
    }

    public abstract void onBindHoder(ViewHolder holder, T t, int position);

    @Override
    public int getItemCount() {
        return dataList.size();
    }

}

