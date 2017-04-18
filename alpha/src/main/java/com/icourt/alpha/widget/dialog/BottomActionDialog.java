package com.icourt.alpha.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.adapter.baseadapter.BaseArrayRecyclerAdapter;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.utils.ItemDecorationUtils;

import java.util.List;

/**
 * @author xuanyouwu
 * @email xuanyouwu@163.com
 * @time 2016-09-21 15:45
 * 模仿ios 下部菜单
 * 其实android 有更好的方案 但是设计非得抄袭ios study more  {@link BottomSheetDialog}
 */

public class BottomActionDialog extends Dialog {

    private BottomActionDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    private BottomActionDialog(Context context, int themeResId) {
        super(context, themeResId);
    }


    public BottomActionDialog(@NonNull Context context, @Nullable String title, @NonNull List<String> actionItems, OnActionItemClickListener onItemClickListener) {
        super(context, R.style.AnimBottomDialog);
        this.onItemClickListener = onItemClickListener;
        this.actionItems = actionItems;
        this.title = title;
    }

    TextView dialog_title, dialog_cancel;
    RecyclerView dialog_recyclerView;
    ActionItemAdapter actionItemAdapter;
    OnActionItemClickListener onItemClickListener;
    List<String> actionItems;
    String title;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_bottom_action);
        Window win = getWindow();
        win.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        win.getDecorView().setPadding(0, 0, 0, 0);
        win.setWindowAnimations(R.style.AnimBottomDialog);
        win.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        win.setGravity(Gravity.BOTTOM);
        win.getAttributes().dimAmount = 0.5f;
        setCanceledOnTouchOutside(true);
        win.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

        dialog_title = (TextView) findViewById(R.id.dialog_title);
        if (TextUtils.isEmpty(title)) {
            dialog_title.setVisibility(View.GONE);
        } else {
            dialog_title.setText(TextUtils.isEmpty(title) ? "提示" : title);
        }
        dialog_recyclerView = (RecyclerView) findViewById(R.id.dialog_recyclerView);
        dialog_recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        dialog_recyclerView.setAdapter(actionItemAdapter = new ActionItemAdapter());
        dialog_recyclerView.addItemDecoration(ItemDecorationUtils.getCommFull05Divider(getContext(), false));
        actionItemAdapter.bindData(true, actionItems);
        actionItemAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseRecyclerAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(BottomActionDialog.this, actionItemAdapter, holder, view, position);
                }
            }
        });
        dialog_cancel = (TextView) findViewById(R.id.dialog_cancel);
        dialog_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isShowing()) {
                    dismiss();
                }
            }
        });
    }


    public interface OnActionItemClickListener {

        /**
         * item 点击事件
         *
         * @param dialog
         * @param adapter
         * @param holder
         * @param view
         * @param position
         */
        void onItemClick(BottomActionDialog dialog, ActionItemAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position);
    }

    public static class ActionItemAdapter extends BaseArrayRecyclerAdapter<String> {

        @Override
        public void onBindHoder(ViewHolder holder, String s, int position) {
            TextView tv_action_bottom_dialog = holder.obtainView(R.id.tv_action_bottom_dialog);
            tv_action_bottom_dialog.setText(s);
        }

        @Override
        public int bindView(int viewtype) {
            return R.layout.item_dialog_bottom_action_;
        }
    }
}
