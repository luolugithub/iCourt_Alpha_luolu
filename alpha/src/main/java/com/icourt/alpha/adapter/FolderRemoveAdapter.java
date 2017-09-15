package com.icourt.alpha.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.entity.bean.FolderDocumentEntity;
import com.icourt.alpha.utils.DateUtils;
import com.icourt.alpha.utils.FileUtils;
import com.icourt.alpha.utils.IMUtils;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/9/13
 * version 2.1.0
 */
public class FolderRemoveAdapter extends SeaFileImageBaseAdapter<FolderDocumentEntity> implements BaseRecyclerAdapter.OnItemChildClickListener {

    public FolderRemoveAdapter() {
        this.setOnItemChildClickListener(this);
    }

    @Override
    public int bindView(int viewtype) {
        return R.layout.adapter_item_folder_remove;
    }

    @Override
    public void onBindHoder(ViewHolder holder, FolderDocumentEntity folderDocumentEntity, int position) {
        if (folderDocumentEntity == null) return;
        ImageView document_type_iv = holder.obtainView(R.id.document_type_iv);
        TextView document_title_tv = holder.obtainView(R.id.document_title_tv);
        TextView document_desc_tv = holder.obtainView(R.id.document_desc_tv);
        ImageView document_delete_iv = holder.obtainView(R.id.document_delete_iv);
        holder.bindChildClick(document_delete_iv);
        document_title_tv.setText(folderDocumentEntity.name);
        if (folderDocumentEntity.isDir()) {
            document_type_iv.setImageResource(R.mipmap.folder);
            document_desc_tv.setText(DateUtils.getStandardSimpleFormatTime(folderDocumentEntity.mtime * 1_000));
        } else {
            document_desc_tv.setText(String.format("%s, %s", FileUtils.bFormat(folderDocumentEntity.size), DateUtils.getStandardSimpleFormatTime(folderDocumentEntity.mtime * 1_000)));
            if (IMUtils.isPIC(folderDocumentEntity.name)) {
                loadSFileImage(folderDocumentEntity, document_type_iv);
            } else {
                document_type_iv.setImageResource(getSFileTypeIcon(folderDocumentEntity.name));
            }
        }
    }

    @Override
    public void onItemChildClick(BaseRecyclerAdapter adapter, ViewHolder holder, View view, int position) {
        switch (view.getId()) {
            case R.id.document_delete_iv:
                removeItem(position);
                break;
        }
    }
}
