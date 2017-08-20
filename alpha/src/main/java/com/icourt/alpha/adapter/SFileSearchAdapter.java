package com.icourt.alpha.adapter;

import android.widget.ImageView;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.adapter.baseadapter.BaseArrayRecyclerAdapter;
import com.icourt.alpha.entity.bean.SFileSearchEntity;
import com.icourt.alpha.utils.DateUtils;
import com.icourt.alpha.utils.FileUtils;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/8/20
 * version 2.1.0
 */
public class SFileSearchAdapter extends BaseArrayRecyclerAdapter<SFileSearchEntity> {
    @Override
    public int bindView(int viewtype) {
        return R.layout.adapter_item_folder;
    }

    @Override
    public void onBindHoder(ViewHolder holder, SFileSearchEntity sFileSearchEntity, int position) {
        if (sFileSearchEntity == null) return;
        ImageView folder_type_iv = holder.obtainView(R.id.folder_type_iv);
        TextView document_title_tv = holder.obtainView(R.id.document_title_tv);
        TextView document_desc_tv = holder.obtainView(R.id.document_desc_tv);

        document_title_tv.setText(sFileSearchEntity.name);
        String timeFormatStr = DateUtils.getFormatChatTimeSimple(sFileSearchEntity.last_modified);
        if (sFileSearchEntity.is_dir) {
            document_desc_tv.setText(timeFormatStr);
            folder_type_iv.setImageResource(FileUtils.getSFileIcon(sFileSearchEntity.name));
        } else {
            folder_type_iv.setImageResource(R.mipmap.folder);
            document_desc_tv.setText(String.format("%s, %s",
                    FileUtils.bFormat(sFileSearchEntity.size),
                    timeFormatStr));
        }
    }
}
