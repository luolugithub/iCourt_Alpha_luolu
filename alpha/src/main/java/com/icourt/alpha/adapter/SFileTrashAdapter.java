package com.icourt.alpha.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.entity.bean.FolderDocumentEntity;
import com.icourt.alpha.utils.DateUtils;
import com.icourt.alpha.utils.FileUtils;
import com.icourt.alpha.utils.IMUtils;

/**
 * Description  回收站里面的文件
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/8/22
 * version 2.1.0
 */
public class SFileTrashAdapter extends SeaFileImageBaseAdapter<FolderDocumentEntity> {

    boolean canRevert;

    public SFileTrashAdapter(boolean selectable, boolean canRevert) {
        super(selectable);
        this.canRevert = canRevert;
    }

    @Override
    public int bindView(int viewtype) {
        return R.layout.adapter_item_file_trash;
    }

    @Override
    public void onBindHoder(ViewHolder holder, FolderDocumentEntity folderDocumentEntity, int position) {
        ImageView document_type_iv = holder.obtainView(R.id.document_type_iv);
        TextView document_title_tv = holder.obtainView(R.id.document_title_tv);
        TextView document_desc_tv = holder.obtainView(R.id.document_desc_tv);
        ImageView document_restore_iv = holder.obtainView(R.id.document_restore_iv);
        holder.bindChildClick(document_restore_iv);
        document_restore_iv.setVisibility(canRevert ? View.VISIBLE : View.GONE);

        document_title_tv.setText(folderDocumentEntity.name);
        if (folderDocumentEntity.isDir()) {
            document_type_iv.setImageResource(R.mipmap.folder);
            document_desc_tv.setText(DateUtils.getStandardSimpleFormatTime(folderDocumentEntity.deleted_time));
        } else {
            document_desc_tv.setText(String.format("%s, %s",
                    FileUtils.bFormat(folderDocumentEntity.size),
                    DateUtils.getStandardSimpleFormatTime(folderDocumentEntity.deleted_time)));
            if (IMUtils.isPIC(folderDocumentEntity.name)) {
                loadSFileImage(folderDocumentEntity, document_type_iv);
            } else {
                document_type_iv.setImageResource(getSFileTypeIcon(folderDocumentEntity.name));
            }
        }
    }
}
