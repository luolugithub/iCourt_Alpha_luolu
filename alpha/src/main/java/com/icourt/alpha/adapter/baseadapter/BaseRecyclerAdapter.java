package com.icourt.alpha.adapter.baseadapter;

import android.app.Activity;
import android.content.Context;
import android.databinding.ViewDataBinding;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.icourt.alpha.http.ApiAlphaService;
import com.icourt.alpha.http.ApiChatService;
import com.icourt.alpha.http.ApiProjectService;
import com.icourt.alpha.http.RetrofitServiceFactory;
import com.icourt.alpha.utils.SnackbarUtils;
import com.kaopiz.kprogresshud.KProgressHUD;

/**
 * ClassName BaseRecyclerAdapter
 * Description
 * Company
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2015/9/10 10:05
 * version
 */
public abstract class BaseRecyclerAdapter extends RecyclerView.Adapter<BaseRecyclerAdapter.ViewHolder> {

    @LayoutRes
    public abstract int bindView(int viewtype);

    public Object getItem(int position) {
        return null;
    }

    private HeaderFooterAdapter parentHeaderFooterAdapter;//包裹的父adapter

    @Nullable
    public HeaderFooterAdapter getParentHeaderFooterAdapter() {
        return parentHeaderFooterAdapter;
    }

    /**
     * 获取真实位置
     *
     * @param adapterPos
     * @return
     */
    public int getRealPos(int adapterPos) {
        return adapterPos - (getParentHeaderFooterAdapter() == null
                ? 0 : getParentHeaderFooterAdapter().getHeaderCount());

    }

    public void setParentHeaderFooterAdapter(HeaderFooterAdapter parentHeaderFooterAdapter) {
        this.parentHeaderFooterAdapter = parentHeaderFooterAdapter;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewtype) {
        ViewHolder viewHolder = new ViewHolder(LayoutInflater.from(viewGroup.getContext())
                .inflate(bindView(viewtype), viewGroup, false));
        return viewHolder;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        private SparseArray<View> holder = null;
        private ViewDataBinding binding;

        @Nullable
        public ViewDataBinding getBinding() {
            return binding;
        }

        public void setBinding(ViewDataBinding binding) {
            this.binding = binding;
        }

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        /**
         * 获取子控件
         *
         * @param id
         * @param <T>
         * @return
         */
        @Nullable
        public <T extends View> T obtainView(@IdRes int id) {
            if (null == holder) holder = new SparseArray<>();
            View view = holder.get(id);
            if (null != view) return (T) view;
            view = itemView.findViewById(id);
            if (null == view) return null;
            holder.put(id, view);
            return (T) view;
        }

        @Nullable
        public <T> T obtainView(@IdRes int id, Class<T> viewClazz) {
            View view = obtainView(id);
            if (null == view) return null;
            return (T) view;
        }


        public ViewHolder bindChildClick(@IdRes int id) {
            View view = obtainView(id);
            if (view == null) return this;
            view.setOnClickListener(this);
            return this;
        }

        /**
         * 子控件绑定局部点击事件
         *
         * @param v
         * @return
         */
        public ViewHolder bindChildClick(View v) {
            if (v == null) return this;
            if (obtainView(v.getId()) == null)
                return this;
            v.setOnClickListener(this);
            return this;
        }


        public ViewHolder bindChildLongClick(@IdRes int id) {
            View view = obtainView(id);
            if (view == null) return this;
            view.setOnLongClickListener(this);
            return this;
        }

        public ViewHolder bindChildLongClick(View v) {
            if (v == null) return this;
            if (obtainView(v.getId()) == null)
                return this;
            v.setOnLongClickListener(this);
            return this;
        }

        /**
         * 文本控件赋值
         *
         * @param id
         * @param text
         */
        public ViewHolder setText(@IdRes int id, CharSequence text) {
            View view = obtainView(id);
            if (view instanceof TextView) {
                ((TextView) view).setText(text);
            }
            return this;
        }


        @Override
        public boolean onLongClick(View v) {
            if (onItemLongClickListener != null && v.getId() == this.itemView.getId()) {
                return onItemLongClickListener.onItemLongClick(BaseRecyclerAdapter.this, this, v, getAdapterPosition());
            } else if (onItemChildLongClickListener != null && v.getId() != this.itemView.getId()) {
                return onItemChildLongClickListener.onItemChildLongClick(BaseRecyclerAdapter.this, this, v, getAdapterPosition());
            }
            return false;
        }

        @Override
        public void onClick(View v) {
            if (onItemClickListener != null && v.getId() == this.itemView.getId()) {
                onItemClickListener.onItemClick(BaseRecyclerAdapter.this, this, v, getAdapterPosition());
            } else if (onItemChildClickListener != null && v.getId() != this.itemView.getId()) {
                onItemChildClickListener.onItemChildClick(BaseRecyclerAdapter.this, this, v, getAdapterPosition());
            }
        }


    }

    protected OnItemClickListener onItemClickListener;

    public OnItemClickListener getOnItemClickListener() {
        return onItemClickListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }


    protected OnItemLongClickListener onItemLongClickListener;

    public OnItemLongClickListener getOnItemLongClickListener() {
        return onItemLongClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }


    public interface OnItemClickListener {
        void onItemClick(BaseRecyclerAdapter adapter, ViewHolder holder, View view, int position);
    }

    public interface OnItemLongClickListener {
        boolean onItemLongClick(BaseRecyclerAdapter adapter, ViewHolder holder, View view, int position);
    }


    public interface OnItemChildClickListener {
        void onItemChildClick(BaseRecyclerAdapter adapter, ViewHolder holder, View view, int position);
    }

    public interface OnItemChildLongClickListener {
        boolean onItemChildLongClick(BaseRecyclerAdapter adapter, ViewHolder holder, View view, int position);
    }


    protected OnItemChildClickListener onItemChildClickListener;
    protected OnItemChildLongClickListener onItemChildLongClickListener;

    public OnItemChildLongClickListener getOnItemChildLongClickListener() {
        return onItemChildLongClickListener;
    }

    public void setOnItemChildLongClickListener(OnItemChildLongClickListener onItemChildLongClickListener) {
        this.onItemChildLongClickListener = onItemChildLongClickListener;
    }

    public OnItemChildClickListener getOnItemChildClickListener() {
        return onItemChildClickListener;
    }

    public void setOnItemChildClickListener(OnItemChildClickListener onItemChildClickListener) {
        this.onItemChildClickListener = onItemChildClickListener;
    }


    /**
     * Toast提示
     * 缺陷 有的rom 会禁用掉taost 比如huawei rom
     *
     * @param notice
     */
    @UiThread
    protected void showToast(@NonNull CharSequence notice) {
    }

    /**
     * 顶部的snackBar
     *
     * @param view   最好根布局,避免计算时间 如activity的decotor view
     * @param notice
     */
    @UiThread
    protected void showTopSnackBar(@NonNull View view, @NonNull CharSequence notice) {
        if (view != null && view.getContext() instanceof Activity) {
            SnackbarUtils.showTopSnackBar((Activity) view.getContext(), notice);
        } else {
            SnackbarUtils.showTopSnackBar(view, notice);
        }
    }

    /**
     * 底部的snackBar android默认在底部
     *
     * @param view   最好根布局,避免计算时间 如activity的decotor view
     * @param notice
     */
    @UiThread
    protected void showBottomSnackBar(@NonNull View view, @NonNull CharSequence notice) {
        SnackbarUtils.showBottomSnack(view, notice);
    }

    /**
     * 接口 http通信
     *
     * @return
     */
    @NonNull
    protected final ApiAlphaService getApi() {
        return RetrofitServiceFactory.getAlphaApiService();
    }

    /**
     * 接口 http通信
     *
     * @return
     */
    @NonNull
    protected final ApiChatService getChatApi() {
        return RetrofitServiceFactory.getChatApiService();
    }

    /**
     * 接口 http通信
     *
     * @return
     */
    @NonNull
    protected final ApiProjectService getProjectApi() {
        return RetrofitServiceFactory.getProjectApiService();
    }


    private KProgressHUD progressHUD;

    /**
     * 获取 菊花加载对话框
     *
     * @return
     */
    private KProgressHUD getSvProgressHUD(@NonNull Context context) {
        if (progressHUD == null) {
            progressHUD = KProgressHUD.create(context)
                    .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE);
        }
        return progressHUD;
    }

    /***
     *  展示加载对话框
     * @param notice
     */
    public void showLoadingDialog(@NonNull Context context, @Nullable String notice) {
        if (context == null) return;
        KProgressHUD currSVProgressHUD = getSvProgressHUD(context);
        currSVProgressHUD.setLabel(notice);
        if (!currSVProgressHUD.isShowing()) {
            currSVProgressHUD.show();
        }
    }

    /**
     * 取消加载对话框
     */
    public void dismissLoadingDialog() {
        if (isShowLoading()) {
            progressHUD.dismiss();
        }
    }

    /**
     * 加载对话框是否展示中
     *
     * @return
     */
    public boolean isShowLoading() {
        return progressHUD != null && progressHUD.isShowing();
    }

}

