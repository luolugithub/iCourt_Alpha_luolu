package com.icourt.alpha.adapter.baseadapter;

/**
 * @author xuanyouwu
 * @email xuanyouwu@163.com
 * @time 2016-08-30 09:49
 */

/**
 * 新版本 @see {@link BaseSelectableAdapter}
 * @param <T>
 */
@Deprecated
public abstract class SelectedRecyclerAdapter<T> extends BaseArrayRecyclerAdapter<T> {

    private static final int defaultSelectedPos = -1;

    private int selectedPos = defaultSelectedPos;

    public int getSelectedPos() {
        return selectedPos;
    }

    public void setSelectedPos(int selectedPos) {
        if (this.selectedPos != selectedPos) {
            this.selectedPos = selectedPos;
            this.notifyDataSetChanged();
        }
    }

    private boolean selectable;//是否可选

    /**
     * 设置可选择性
     *
     * @param selectable
     */
    public void setSelectable(boolean selectable) {
        if (this.selectable != selectable) {
            this.selectable = selectable;
            if (!isSelectable()) {//不可选择的 默认清除上次选中的 子类可以复写
                selectedPos = defaultSelectedPos;
            } else {
                this.notifyDataSetChanged();
            }
        }
    }

    /**
     * 清除选中的item
     */
    public void clearSelected() {
        if (selectedPos != defaultSelectedPos) {
            selectedPos = defaultSelectedPos;
            notifyDataSetChanged();
        }
    }

    public boolean isSelectable() {
        return selectable;
    }

    public SelectedRecyclerAdapter(boolean selectable) {
        this.selectable = selectable;
    }

    @Override
    public final void onBindHoder(ViewHolder holder, T t, int position) {
        onBindSelectableHoder(holder, t, position, position == getSelectedPos());
    }

    public abstract void onBindSelectableHoder(ViewHolder holder, T t, int position, boolean selected);
}
