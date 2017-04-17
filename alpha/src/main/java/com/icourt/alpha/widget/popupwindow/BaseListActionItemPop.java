package com.icourt.alpha.widget.popupwindow;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.icourt.alpha.R;
import com.icourt.alpha.adapter.baseadapter.BaseArrayRecyclerAdapter;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.utils.ItemDecorationUtils;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/17
 * version 1.0.0
 */
public class BaseListActionItemPop<T>
        extends PopupWindow
        implements BaseRecyclerAdapter.OnItemClickListener {

    @Override
    public final void onItemClick(BaseRecyclerAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
        if (listener != null) {
            listener.onItemClick(BaseListActionItemPop.this, adapter, holder, view, position);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(BaseListActionItemPop listActionItemPop, BaseRecyclerAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position);
    }

    private Context context;
    private RecyclerView recyclerView;
    private BaseArrayRecyclerAdapter<T> adapter;
    private OnItemClickListener listener;

    private BaseListActionItemPop() {
    }

    public BaseListActionItemPop(@NonNull Context context, @NonNull BaseArrayRecyclerAdapter<T> adapter) {
        super(View.inflate(context, R.layout.popupwindow_list, null));
        this.context = context;
        this.adapter = adapter;
        initView();
    }

    public final BaseListActionItemPop withOnItemClick(OnItemClickListener listener) {
        this.listener = listener;
        adapter.setOnItemClickListener(this);
        return this;
    }

    public Context getContext() {
        return context;
    }

    public RecyclerView getRecyclerView() {
        return recyclerView;
    }

    public BaseArrayRecyclerAdapter<T> getAdapter() {
        return adapter;
    }

    private void initView() {
        recyclerView = (RecyclerView) getContentView().findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setAutoMeasureEnabled(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(ItemDecorationUtils.getCommFullDivider(getContext(), false));
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(adapter);
        setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setFocusable(true);
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    public int getMeasureHeight() {
        getContentView().measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        return getContentView().getMeasuredHeight();
    }

    public int getMeasureWidth() {
        getContentView().measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        return getContentView().getMeasuredWidth();
    }

    /**
     * 展示在正上方
     *
     * @param anchor
     * @param yoff
     */
    public final void showUpCenter(View anchor, int yoff) {
        if (anchor == null) return;
        int popHeight = getContentView().getHeight();
        if (popHeight <= 0) {
            popHeight = getMeasureHeight();
        }
        popHeight += yoff;
        int popWidth = getContentView().getWidth();
        if (popWidth <= 0) {
            popWidth = getMeasureWidth();
        }
        showAsDropDown(anchor, -(popWidth - anchor.getWidth()) / 2, -(anchor.getHeight() + popHeight));
    }

    /**
     * 展示在正上方
     *
     * @param anchor
     */
    public final void showUpCenter(View anchor) {
        showUpCenter(anchor, 0);
    }

    private BaseListActionItemPop(int width, int height) {
        super(null, width, height);
    }

    private BaseListActionItemPop(View contentView, int width, int height) {
        super(contentView, width, height, false);
    }

    private BaseListActionItemPop(View contentView, int width, int height, boolean focusable) {
        super(contentView, width, height, focusable);
    }
}