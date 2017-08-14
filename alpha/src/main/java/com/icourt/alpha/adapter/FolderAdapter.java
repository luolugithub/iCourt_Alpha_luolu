package com.icourt.alpha.adapter;

import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.adapter.baseadapter.BaseArrayRecyclerAdapter;
import com.icourt.alpha.entity.bean.FolderDocumentEntity;
import com.icourt.alpha.utils.DateUtils;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/8/14
 * version 2.1.0
 */
public class FolderAdapter extends BaseArrayRecyclerAdapter<FolderDocumentEntity> {
    @Override
    public int bindView(int viewtype) {
        return R.layout.adapter_item_folder;
    }

    @Override
    public void onBindHoder(ViewHolder holder, FolderDocumentEntity folderDocumentEntity, int position) {
        if (folderDocumentEntity == null) return;
        TextView document_title_tv = holder.obtainView(R.id.document_title_tv);
        TextView document_desc_tv = holder.obtainView(R.id.document_desc_tv);

        document_title_tv.setText(folderDocumentEntity.name);
        document_desc_tv.setText(DateUtils.getFormatChatTimeSimple(folderDocumentEntity.mtime * 1_000));
    }
}
