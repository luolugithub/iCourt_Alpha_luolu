package com.icourt.alpha.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.adapter.baseadapter.BaseArrayRecyclerAdapter;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.entity.bean.ItemsEntityImp;
import com.icourt.alpha.view.recyclerviewDivider.DividerItemDecoration;

import java.util.List;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/22
 * version 1.0.0
 */
public class CenterMenuDialog extends Dialog implements View.OnClickListener {
    TextView dialogTitle;
    RecyclerView dialogRecyclerView;
    Button closeBtn;
    List<? extends ItemsEntityImp> data;
    CharSequence title;
    View dialog_title_divider;
    BaseRecyclerAdapter.OnItemClickListener onItemClickListener;
    private MenuAdapter menuAdapter;

    public CenterMenuDialog(@NonNull Context context, CharSequence title, List<? extends ItemsEntityImp> data) {
        super(context);
        this.title = title;
        this.data = data;
    }

    private CenterMenuDialog(@NonNull Context context) {
        super(context);
    }

    private CenterMenuDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
    }

    private CenterMenuDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public BaseRecyclerAdapter.OnItemClickListener getOnItemClickListener() {
        return onItemClickListener;
    }

    public MenuAdapter getMenuAdapter() {
        return menuAdapter;
    }

    public CenterMenuDialog setOnItemClickListener(BaseRecyclerAdapter.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
        if (menuAdapter != null)
            menuAdapter.setOnItemClickListener(onItemClickListener);
        return this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_center_menu);
        Window win = getWindow();
        win.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        win.getDecorView().setPadding(0, 0, 0, 0);
        win.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        win.setGravity(Gravity.CENTER);
        win.getAttributes().dimAmount = 0.5f;
        setCanceledOnTouchOutside(true);
        win.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialogTitle = (TextView) findViewById(R.id.dialog_title);
        dialog_title_divider = findViewById(R.id.dialog_title_divider);
        if (!TextUtils.isEmpty(title)) {
            dialogTitle.setVisibility(View.VISIBLE);
            dialog_title_divider.setVisibility(View.VISIBLE);
            dialogTitle.setText(title);
        } else {
            dialogTitle.setVisibility(View.GONE);
            dialog_title_divider.setVisibility(View.GONE);
        }
        dialogRecyclerView = (RecyclerView) findViewById(R.id.dialog_recyclerView);
        closeBtn = (Button) findViewById(R.id.close_btn);
        closeBtn.setOnClickListener(this);
        dialogRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        DividerItemDecoration itemDecoration = new DividerItemDecoration();
        itemDecoration.setDividerLookup(new DividerItemDecoration.DividerLookup() {
            @Override
            public DividerItemDecoration.Divider getVerticalDivider(int position) {
                return new DividerItemDecoration.Divider.Builder()
                        .size(getContext().getResources().getDimensionPixelSize(R.dimen.dp8))
                        .color(Color.TRANSPARENT)
                        .build();
            }

            @Override
            public DividerItemDecoration.Divider getHorizontalDivider(int position) {
                return new DividerItemDecoration.Divider.Builder()
                        .size(getContext().getResources().getDimensionPixelSize(R.dimen.dp8))
                        .color(Color.TRANSPARENT)
                        .build();
            }
        });
        dialogRecyclerView.addItemDecoration(itemDecoration);
        dialogRecyclerView.setAdapter(menuAdapter = new MenuAdapter());
        menuAdapter.setOnItemClickListener(onItemClickListener);
        menuAdapter.bindData(true, data);
    }


    @Override
    public void onClick(View v) {
        if (v == closeBtn) {
            dismiss();
        }
    }

    public static class MenuAdapter<T extends ItemsEntityImp> extends BaseArrayRecyclerAdapter<T> {

        @Override
        public int bindView(int viewtype) {
            return R.layout.dialog_adapter_item_menu;
        }


        @Override
        public void onBindHoder(ViewHolder holder, T t, int position) {
            if (t == null) return;
            ImageView menu_icon_iv = holder.obtainView(R.id.menu_icon_iv);
            TextView menu_title_tv = holder.obtainView(R.id.menu_title_tv);
            menu_icon_iv.setImageResource(t.getItemIconRes());
            menu_title_tv.setText(t.getItemTitle());
        }
    }
}
