package com.icourt.alpha.adapter.baseadapter;

import android.util.SparseBooleanArray;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xuanyouwu
 * @email xuanyouwu@163.com
 * @time 2016-10-01 11:47
 * <p>
 * 公共多选适配器
 */

public abstract class MultiSelectRecyclerAdapter<T> extends BaseArrayRecyclerAdapter<T> {

    private final SparseBooleanArray selectedArray = new SparseBooleanArray();

    /**
     * 获取选中状态的列表
     *
     * @return
     */
    public SparseBooleanArray getSelectedArray() {
        return selectedArray;
    }

    private boolean selectable;//是否可选

    public MultiSelectRecyclerAdapter(boolean selectable) {
        this.selectable = selectable;
    }

    public MultiSelectRecyclerAdapter() {
    }

    public boolean isSelectable() {
        return selectable;
    }

    /**
     * 设置可选择性
     *
     * @param selectable
     */
    public void setSelectable(boolean selectable) {
        if (this.selectable != selectable) {
            this.selectable = selectable;
            if (!isSelectable()) {//不可选择的 默认清除上次选中的 子类可以复写
                clearSelected();
            } else {
                this.notifyDataSetChanged();
            }
        }
    }

    /**
     * 清除选中的
     */
    public void clearSelected() {
        selectedArray.clear();
        this.notifyDataSetChanged();
    }

    /**
     * 选择全部
     */
    public void selectAll() {
        selectedArray.clear();
        for (int i = 0; i < getItemCount(); i++) {
            selectedArray.put(i, true);
        }
        this.notifyDataSetChanged();
    }

    /**
     * 获取选中的items
     *
     * @return
     */
    public List<Integer> getSelectedData() {
        List<Integer> selected = new ArrayList<>();
        for (int i = 0; i < getItemCount(); i++) {
            boolean itemSelected = selectedArray.get(i, false);
            if (itemSelected) {
                selected.add(i);
            }
        }
        return selected;
    }


    /**
     * 选中和取消
     *
     * @param pos
     * @param selected
     * @return
     */
    public boolean setSelected(int pos, boolean selected) {
        if (pos >= 0 && pos < getItemCount()) {
            boolean itemSelected = selectedArray.get(pos, false);
            if (selected != itemSelected) {
                selectedArray.put(pos, selected);
                this.notifyItemChanged(pos);
                return true;
            }
        }
        return false;
    }

    /**
     * 反选
     *
     * @param pos
     * @return
     */
    public boolean toggleSelected(int pos) {
        if (pos >= 0 && pos < getItemCount()) {
            boolean itemSelected = selectedArray.get(pos, false);
            selectedArray.put(pos, !itemSelected);
            this.notifyItemChanged(pos);
            return true;
        }
        return false;
    }


    /**
     * 反选  不更新UI
     *
     * @param pos
     * @return
     */
    public boolean toggleSelectedWithoutUpdateUI(int pos) {
        if (pos >= 0 && pos < getItemCount()) {
            boolean itemSelected = selectedArray.get(pos, false);
            selectedArray.put(pos, !itemSelected);
            return true;
        }
        return false;
    }


    /**
     * 是否选中
     *
     * @param pos
     * @return
     */
    public boolean isSelected(int pos) {
        if (pos >= 0 && pos < getItemCount()) {
            return selectedArray.get(pos, false);
        }
        return false;
    }


    /**
     * 是否全部选中
     *
     * @return
     */
    public boolean isAllSelected() {
        if (selectedArray.size() < getItemCount()) {
            return false;
        } else {
            for (int i = 0; i < getItemCount(); i++) {
                boolean itemSelected = selectedArray.get(i, false);
                if (!itemSelected) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public final void onBindHoder(ViewHolder holder, T t, int position) {
        onBindSelectableHolder(holder, t, isSelected(position), position);
    }

    public abstract void onBindSelectableHolder(ViewHolder holder, T t, boolean selected, int position);
}
