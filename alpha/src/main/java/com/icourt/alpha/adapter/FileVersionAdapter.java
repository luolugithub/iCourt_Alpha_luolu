package com.icourt.alpha.adapter;

import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.asange.recyclerviewadapter.BaseViewHolder;
import com.icourt.alpha.R;
import com.icourt.alpha.adapter.baseadapter.BaseAdapter;
import com.icourt.alpha.entity.bean.FileVersionEntity;
import com.icourt.alpha.utils.DateUtils;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/8/15
 * version 2.1.0
 */
public class FileVersionAdapter extends BaseAdapter<FileVersionEntity> {
    boolean canRevert;

    public FileVersionAdapter(boolean canRevert) {
        this.canRevert = canRevert;
    }

    @Override
    public int bindView(int viewtype) {
        return R.layout.adapter_item_file_version;
    }

    @Override
    public void onBindHolder(BaseViewHolder holder, @Nullable FileVersionEntity fileVersionEntity, int i) {
        if (fileVersionEntity == null) {
            return;
        }
        TextView file_version_tv = holder.obtainView(R.id.file_version_tv);
        TextView file_title_tv = holder.obtainView(R.id.file_title_tv);
        TextView file_desc_tv = holder.obtainView(R.id.file_desc_tv);
        ImageView file_restore_iv = holder.obtainView(R.id.file_restore_iv);

        file_version_tv.setText(String.format("v%s", fileVersionEntity.version));
        file_title_tv.setText(DateUtils.getFormatDate(fileVersionEntity.ctime * 1_000, DateUtils.DATE_YYYYMMDD_HHMM_STYLE1));
        file_desc_tv.setText(fileVersionEntity.user_info != null ? fileVersionEntity.user_info.name : "");
        holder.bindChildClick(file_restore_iv);
        file_restore_iv.setVisibility(canRevert ? View.VISIBLE : View.GONE);
    }
}
