package com.icourt.alpha.adapter;

import android.widget.ImageView;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.adapter.baseadapter.BaseArrayRecyclerAdapter;
import com.icourt.alpha.entity.bean.FileVersionEntity;
import com.icourt.alpha.utils.DateUtils;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/8/15
 * version 2.1.0
 */
public class FileVersionAdapter extends BaseArrayRecyclerAdapter<FileVersionEntity> {
    @Override
    public int bindView(int viewtype) {
        return R.layout.adapter_item_file_version;
    }

    @Override
    public void onBindHoder(ViewHolder holder, FileVersionEntity fileVersionEntity, int position) {
        if (fileVersionEntity == null) return;
        TextView file_version_tv = holder.obtainView(R.id.file_version_tv);
        TextView file_title_tv = holder.obtainView(R.id.file_title_tv);
        TextView file_desc_tv = holder.obtainView(R.id.file_desc_tv);
        ImageView file_restore_iv = holder.obtainView(R.id.file_restore_iv);

        file_version_tv.setText(String.format("v%s", fileVersionEntity.version));
        file_title_tv.setText(DateUtils.getyyyyMMddHHmm(fileVersionEntity.ctime * 1_000));
        file_desc_tv.setText(fileVersionEntity.user_info != null ? fileVersionEntity.user_info.name : "");
        holder.bindChildClick(file_restore_iv);
    }
}
