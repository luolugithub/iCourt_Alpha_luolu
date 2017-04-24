package com.icourt.alpha.adapter;

import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.icourt.alpha.R;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.adapter.baseadapter.MultiSelectRecyclerAdapter;
import com.icourt.alpha.entity.bean.LocalImageEntity;
import com.icourt.alpha.utils.GlideUtils;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/24
 * version 1.0.0
 */
public class PhotosListAdapter
        extends MultiSelectRecyclerAdapter<LocalImageEntity>
        implements BaseRecyclerAdapter.OnItemClickListener {

    public PhotosListAdapter() {
        this.setOnItemClickListener(this);
    }

    @Override
    public int bindView(int viewtype) {
        return R.layout.adapter_item_photo;
    }


    @Override
    public void onBindSelectableHolder(ViewHolder holder, LocalImageEntity localImageEntity, boolean selected, int position) {
        if (localImageEntity == null) return;
        ImageView photo_item_iv = holder.obtainView(R.id.photo_item_iv);
        CheckBox photo_item_cb = holder.obtainView(R.id.photo_item_cb);
        if (GlideUtils.canLoadImage(photo_item_iv.getContext())) {
            Glide.with(photo_item_iv.getContext())
                    .load(localImageEntity.thumbPath)
                    .into(photo_item_iv);
        }
        photo_item_cb.setChecked(selected);
    }

    @Override
    public void onItemClick(BaseRecyclerAdapter adapter, ViewHolder holder, View view, int position) {
        this.toggleSelected(position);
    }

    @Override
    public int getItemCount() {
        return 20;
       // return super.getItemCount();
    }
}
