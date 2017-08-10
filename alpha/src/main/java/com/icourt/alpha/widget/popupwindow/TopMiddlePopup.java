package com.icourt.alpha.widget.popupwindow;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.icourt.alpha.R;
import com.icourt.alpha.adapter.ListDropDownAdapter;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.entity.bean.FilterDropEntity;

import java.util.List;

/**
 * Description
 * Company Beijing icourt
 * author  lu.zhao  E-mail:zhaolu@icourt.cc
 * date createTime：17/8/4
 * version 2.0.0
 */

public class TopMiddlePopup extends PopupWindow implements BaseRecyclerAdapter.OnItemClickListener {

    private Context myContext;
    private RecyclerView recyclerView;
    private ListDropDownAdapter adapter;
    private OnItemClickListener listener;
    private List<FilterDropEntity> myItems;
    private int myWidth;
    private int myHeight;


    private LayoutInflater inflater = null;
    private View myMenuView;

    private LinearLayout popupLL;

    public TopMiddlePopup(Context context) {
    }

    public ListDropDownAdapter getAdapter() {
        return adapter;
    }

    @Override
    public final void onItemClick(BaseRecyclerAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
        if (listener != null) {
            listener.onItemClick(this, adapter, holder, view, position);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(TopMiddlePopup topMiddlePopup, BaseRecyclerAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position);
    }

    public TopMiddlePopup(Context context, int width, int height,
                          OnItemClickListener onItemClickListener) {

        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        myMenuView = inflater.inflate(R.layout.top_popup_filter_layout, null);

        this.myContext = context;
        this.listener = onItemClickListener;

        this.myWidth = width;
        this.myHeight = height;

        initWidget();

    }

    /**
     * 初始化控件
     */
    private void initWidget() {
        recyclerView = (RecyclerView) myMenuView.findViewById(R.id.popup_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(myContext));
        popupLL = (LinearLayout) myMenuView.findViewById(R.id.popup_layout);
        adapter = new ListDropDownAdapter(true);
        adapter.setOnItemClickListener(this);
        recyclerView.setAdapter(adapter);
        setPopup();
    }

    /**
     * 设置popup的样式
     */
    private void setPopup() {
        this.setContentView(myMenuView);
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        this.setHeight(myHeight);
        this.setFocusable(true);
//        this.setAnimationStyle(R.style.AnimTop);
        ColorDrawable dw = new ColorDrawable(0x33000000);
        this.setBackgroundDrawable(dw);

        myMenuView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                int height = popupLL.getBottom();
                int left = popupLL.getLeft();
                int right = popupLL.getRight();
                int y = (int) event.getY();
                int x = (int) event.getX();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (y > height || x < left || x > right) {
                        dismiss();
                        startLayoutAnimatorout();
                    }
                }
                return true;
            }
        });
    }

    private void startLayoutAnimatorin() {
        Animation animation = AnimationUtils.loadAnimation(myContext, R.anim.toppop_animation_in);
        popupLL.setAnimation(animation);
        animation.start();
    }

    private void startLayoutAnimatorout() {
        Animation animation = AnimationUtils.loadAnimation(myContext, R.anim.toppop_animation_out);
        popupLL.setAnimation(animation);
        animation.start();
    }

    /**
     * 显示弹窗界面
     *
     * @param view
     */
    public void show(View view, List<FilterDropEntity> items, int position) {
        adapter.bindData(true, items);
        adapter.setSelectedPos(position);
        startLayoutAnimatorin();
        showAsDropDown(view);
    }

}
