package com.icourt.alpha.adapter.recycleradapter;

/**
 * @author xuanyouwu
 * @email xuanyouwu@163.com
 * @time 2016-08-30 09:49
 */

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


    @Override
    public final void onBindHoder(ViewHolder holder, T t, int position) {
        onBindSelectableHoder(holder, t, position, position == getSelectedPos());
    }

    public abstract void onBindSelectableHoder(ViewHolder holder, T t, int position, boolean selected);
}
