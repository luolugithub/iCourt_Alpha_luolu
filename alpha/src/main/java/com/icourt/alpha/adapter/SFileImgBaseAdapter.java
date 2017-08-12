package com.icourt.alpha.adapter;

import android.widget.ImageView;

import com.icourt.alpha.adapter.baseadapter.BaseArrayRecyclerAdapter;
import com.icourt.alpha.interfaces.ISeaFileImageLoader;

/**
 * Description  sfile图片加载适配器
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/8/12
 * version 2.1.0
 */
public abstract class SFileImgBaseAdapter<T> extends BaseArrayRecyclerAdapter<T> {

    protected ISeaFileImageLoader seaFileImageLoader;
    boolean selectable;

    public SFileImgBaseAdapter(ISeaFileImageLoader seaFileImageLoader, boolean selectable) {
        this.seaFileImageLoader = seaFileImageLoader;
        this.selectable = selectable;
    }

    public boolean isSelectable() {
        return selectable;
    }

    public void setSelectable(boolean selectable) {
        this.selectable = selectable;
    }

    /**
     * 记载sfile图片
     *
     * @param fileName
     * @param view
     * @param type
     * @param size
     */
    protected void loadSFileImage(String fileName, ImageView view, int type, int size) {
        if (seaFileImageLoader != null) {
            seaFileImageLoader.loadSFileImage(fileName, view, type, size);
        }
    }
}
