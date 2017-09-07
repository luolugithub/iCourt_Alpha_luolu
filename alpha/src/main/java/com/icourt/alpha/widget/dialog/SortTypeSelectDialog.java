package com.icourt.alpha.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.adapter.FileSoryTypeAdapter;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.utils.ItemDecorationUtils;
import com.icourt.alpha.widget.comparators.FileSortComparator;

import java.util.Arrays;
import java.util.List;

import static com.icourt.alpha.widget.comparators.FileSortComparator.FILE_SORT_TYPE_DEFAULT;
import static com.icourt.alpha.widget.comparators.FileSortComparator.FILE_SORT_TYPE_NAME;
import static com.icourt.alpha.widget.comparators.FileSortComparator.FILE_SORT_TYPE_SIZE_ASC;
import static com.icourt.alpha.widget.comparators.FileSortComparator.FILE_SORT_TYPE_SIZE_DESC;
import static com.icourt.alpha.widget.comparators.FileSortComparator.FILE_SORT_TYPE_UPDATE;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/8/18
 * version 2.1.0
 */
public class SortTypeSelectDialog extends Dialog implements View.OnClickListener {
    public interface OnSortTypeChangeListener {
        void onSortTypeSelected(@FileSortComparator.FileSortType int sortType);
    }


    RecyclerView dialogRecyclerView;
    TextView btCancel;
    TextView btOk;
    OnSortTypeChangeListener onSortTypeChangeListener;
    @FileSortComparator.FileSortType
    int selectedSortType;

    public SortTypeSelectDialog(@NonNull Context context,
                                @FileSortComparator.FileSortType int selectedSortType,
                                OnSortTypeChangeListener onSortTypeChangeListener) {
        super(context);
        this.onSortTypeChangeListener = onSortTypeChangeListener;
        this.selectedSortType = selectedSortType;
    }

    FileSoryTypeAdapter fileSoryTypeAdapter;
    List<String> menus = Arrays.asList(
            "默认排序",
            "按名称排序",
            "按文件由大到小排序",
            "按文件由小到大排序",
            "按最后更新时间排序");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_sort_type_select);

        Window win = getWindow();
        win.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        win.getDecorView().setPadding(0, 0, 0, 0);
        win.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        win.setGravity(Gravity.BOTTOM);
        win.getAttributes().dimAmount = 0.5f;
        win.getAttributes().windowAnimations = R.style.SlideAnimBottom;
        setCanceledOnTouchOutside(true);
        win.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);


        dialogRecyclerView = findViewById(R.id.dialog_recyclerView);
        btCancel = findViewById(R.id.bt_cancel);
        btOk = findViewById(R.id.bt_ok);

        dialogRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        dialogRecyclerView.addItemDecoration(ItemDecorationUtils.getCommFullDivider(getContext(), false));
        dialogRecyclerView.setAdapter(fileSoryTypeAdapter = new FileSoryTypeAdapter());
        fileSoryTypeAdapter.bindData(true, menus);
        fileSoryTypeAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseRecyclerAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
                fileSoryTypeAdapter.clearSelected();
                fileSoryTypeAdapter.setSelected(position, true);
                selectedSortType = convert2SortType(position);
            }
        });
        fileSoryTypeAdapter.setSelected(convert2SortType(selectedSortType), true);
        btCancel.setOnClickListener(this);
        btOk.setOnClickListener(this);
    }

    @FileSortComparator.FileSortType
    private int convert2SortType(int pos) {
        switch (pos) {
            case FILE_SORT_TYPE_DEFAULT:
                return FILE_SORT_TYPE_DEFAULT;
            case FILE_SORT_TYPE_NAME:
                return FILE_SORT_TYPE_NAME;
            case FILE_SORT_TYPE_SIZE_DESC:
                return FILE_SORT_TYPE_SIZE_DESC;
            case FILE_SORT_TYPE_SIZE_ASC:
                return FILE_SORT_TYPE_SIZE_ASC;
            case FILE_SORT_TYPE_UPDATE:
                return FILE_SORT_TYPE_UPDATE;
        }
        return FILE_SORT_TYPE_DEFAULT;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_cancel:
                dismiss();
                break;
            case R.id.bt_ok:
                if (onSortTypeChangeListener != null) {
                    onSortTypeChangeListener.onSortTypeSelected(selectedSortType);
                }
                dismiss();
                break;
        }
    }
}
