package com.icourt.alpha.adapter.baseadapter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xuanyouwu
 * @email xuanyouwu@163.com
 * @time 2016-03-31 14:27
 */
public abstract class HFRecyclerAdapter<H, T, F> extends BaseRecyclerAdapter {
    private static final int HEADER_VIEW_TYPE = -2016;
    private static final int FOOTER_VIEW_TYPE = -2015;

    private final List<T> dataList = new ArrayList<T>();
    private final List<H> headerDataList = new ArrayList<H>();
    private final List<F> footerDataList = new ArrayList<F>();

    public List<T> getDataList() {
        return dataList;
    }

    public List<H> getHeaderDataList() {
        return headerDataList;
    }

    public List<F> getFooterDataList() {
        return footerDataList;
    }

    public boolean isHeader(int pos) {
        if (pos >= 0 && pos < getmHeaderCount())
            return true;
        return false;
    }

    public boolean isFooter(int pos) {
        if (pos >= getItemCount() - getmFooterCount() && pos < getItemCount())
            return true;
        return false;
    }

    public boolean isItem(int pos) {
        if (pos >= getmHeaderCount() && pos < getItemCount() - getmFooterCount())
            return true;
        return false;
    }

    @Override
    public T getItem(int position) {
        if (isItem(position)) {
            int realPos = position - getmHeaderCount();
            if (realPos >= 0 && realPos < dataList.size())
                return dataList.get(realPos);
        }
        return null;
    }

    public H getHeaderItem(int pos) {
        if (isHeader(pos)) {
            if (pos >= 0 && pos < headerDataList.size())
                return headerDataList.get(pos);
        }
        return null;
    }

    public F getFooterItem(int pos) {
        if (isFooter(pos)) {
            int realPos = getItemCount() - 1 - pos;
            if (realPos >= 0 && realPos < footerDataList.size())
                return footerDataList.get(realPos);
        }
        return null;
    }

    public boolean bindHeader(List<H> datas, boolean updateUI) {
        if (datas == null) return false;
        if (getHeaderDataList().containsAll(datas)) return false;
        getHeaderDataList().clear();
        boolean addSuc = getHeaderDataList().addAll(datas);
        if (updateUI) {
            this.notifyDataSetChanged();
        }
        return addSuc;
    }

    public boolean bindFooter(List<F> datas) {
        if (datas == null) return false;
        if (getFooterDataList().containsAll(datas)) return false;
        getFooterDataList().clear();
        boolean addSuc = getFooterDataList().addAll(datas);
        this.notifyItemRangeChanged(getItemCount() - getmFooterCount(), getItemCount());
        return addSuc;
    }

    public boolean bindData(boolean isRefresh, List<T> datas) {
        if (datas == null) return false;
        if (isRefresh) {
            getDataList().clear();
        }
        boolean addSuc = getDataList().addAll(datas);
        this.notifyDataSetChanged();
        return addSuc;
    }

    public boolean addItem(T t) {
        if (t == null) return false;
        if (getDataList().contains(t)) return false;
        boolean add = getDataList().add(t);
        this.notifyItemInserted(getmItemCount() + getmHeaderCount() - 1);
        return add;
    }

    public boolean addItem(T t, int pos) {
        if (t == null) return false;
        if (pos < getmHeaderCount() || pos > getmItemCount() + getmHeaderCount()) return false;
        if (getDataList().contains(t)) return false;
        getDataList().add(pos - getmHeaderCount(), t);
        this.notifyItemInserted(pos);
        return true;
    }


    public void updateItem(int position) {
        this.notifyItemChanged(position);
    }

    public boolean removeItem(int position) {
        if (isItem(position)) {
            int realPos = position - getmHeaderCount();
            getDataList().remove(realPos);
            this.notifyItemRemoved(position);
            return true;
        } else if (isHeader(position)) {
            getHeaderDataList().remove(position);
            this.notifyItemRemoved(position);
            return true;
        } else if (isFooter(position)) {
            getFooterDataList().remove(position - getmHeaderCount() - getmItemCount());
            this.notifyItemRemoved(position);
            return true;
        }
        return false;
    }

    public boolean removeItem(T t) {
        if (t == null) return false;
        int i = getDataList().indexOf(t);
        if (i >= 0) {
            return removeItem(i);
        }
        return false;
    }

    @Override
    public final int getItemViewType(int position) {
        if (isHeader(position)) {
            return getmHeaderViewType();
        } else if (isFooter(position)) {
            return getmFooterViewType();
        } else {
            return getmItemViewType(position);
        }
    }


    /**
     * 中间条目的viewtype
     *
     * @param pos
     * @return
     */
    public int getmItemViewType(int pos) {
        return 0;
    }

    /**
     * header item的viewType
     *
     * @return
     */
    public final int getmHeaderViewType() {
        return HEADER_VIEW_TYPE;
    }

    /**
     * footer item的viewType
     *
     * @return
     */
    public final int getmFooterViewType() {
        return FOOTER_VIEW_TYPE;
    }

    @Override
    public final int bindView(int viewtype) {
        if (viewtype == getmHeaderViewType()) {
            return bindHeaderView();
        } else if (viewtype == getmFooterViewType()) {
            return bindFooterView();
        } else {
            return bindItemView(viewtype);
        }
    }

    /**
     * bind center view
     *
     * @param itemViewType
     * @return
     */
    public abstract int bindItemView(int itemViewType);

    /**
     * bind header view
     *
     * @return
     */
    public abstract int bindHeaderView();

    /**
     * bind footer view
     *
     * @return
     */
    public abstract int bindFooterView();

    @Override
    public final void onBindViewHolder(ViewHolder holder, int position) {
        if (isHeader(position)) {
            onBindHeaderHolder(holder, getHeaderItem(position), position);
        } else if (isFooter(position)) {
            onBindFooterHolder(holder, getFooterItem(position), position);
        } else {
            onBindItemHolder(holder, getItem(position), position);
        }
    }

    /**
     * bind header
     *
     * @param holder
     * @param h
     * @param position
     */
    public abstract void onBindHeaderHolder(ViewHolder holder, H h, int position);

    /**
     * bind center
     *
     * @param holder
     * @param t
     * @param position
     */
    public abstract void onBindItemHolder(ViewHolder holder, T t, int position);

    /**
     * bind footer
     *
     * @param holder
     * @param f
     * @param position
     */
    public abstract void onBindFooterHolder(ViewHolder holder, F f, int position);

    @Override
    public final int getItemCount() {
        return getmHeaderCount() + getmItemCount() + getmFooterCount();
    }

    /**
     * header 条目数量
     *
     * @return
     */
    public int getmItemCount() {
        return dataList.size();
    }

    /**
     * 中间 条目数量
     *
     * @return
     */
    public int getmHeaderCount() {
        return headerDataList.size();
    }

    /**
     * footer 条目数量
     *
     * @return
     */
    public int getmFooterCount() {
        return footerDataList.size();
    }
}
