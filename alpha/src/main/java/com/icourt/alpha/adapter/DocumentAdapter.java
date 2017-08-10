package com.icourt.alpha.adapter;

import android.widget.ImageView;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.adapter.baseadapter.BaseArrayRecyclerAdapter;
import com.icourt.alpha.entity.bean.DocumentRootEntity;
import com.icourt.alpha.utils.DateUtils;
import com.icourt.alpha.utils.FileUtils;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/8/9
 * version 2.1.0
 */
public class DocumentAdapter extends BaseArrayRecyclerAdapter<DocumentRootEntity> {

    @Override
    public int bindView(int viewtype) {
        return R.layout.adapter_item_document;
    }

    @Override
    public void onBindHoder(ViewHolder holder, DocumentRootEntity documentRootEntity, int position) {
        if (documentRootEntity == null) return;
        TextView document_title_tv = holder.obtainView(R.id.document_title_tv);
        TextView document_desc_tv = holder.obtainView(R.id.document_desc_tv);
        ImageView document_expand_iv = holder.obtainView(R.id.document_expand_iv);
        holder.bindChildClick(document_expand_iv);

        document_title_tv.setText(documentRootEntity.repo_name);
        document_desc_tv.setText(String.format("%s, %s", FileUtils.bFormat(documentRootEntity.size), DateUtils.getFormatChatTimeSimple(documentRootEntity.last_modified)));
    }


}
