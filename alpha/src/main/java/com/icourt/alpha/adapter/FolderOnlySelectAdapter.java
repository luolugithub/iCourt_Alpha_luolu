package com.icourt.alpha.adapter;

import android.view.View;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.constants.Const;
import com.icourt.alpha.entity.bean.FolderDocumentEntity;

import java.util.ArrayList;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/9/12
 * version 2.1.0
 */
public class FolderOnlySelectAdapter extends FolderDocumentAdapter {

    boolean fileSelectable;
    boolean folderSelectable;

    public boolean isFileSelectable() {
        return fileSelectable;
    }

    public boolean isFolderSelectable() {
        return folderSelectable;
    }

    /**
     * @param selectedFolderDocuments
     * @param fileSelectable          文件是否可以选择
     * @param folderSelectable        文件夹是否可以选择
     */
    public FolderOnlySelectAdapter(ArrayList<FolderDocumentEntity> selectedFolderDocuments,
                                   boolean fileSelectable,
                                   boolean folderSelectable) {
        super(Const.VIEW_TYPE_ITEM, fileSelectable || folderSelectable, selectedFolderDocuments);
        this.fileSelectable = fileSelectable;
        this.folderSelectable = folderSelectable;
    }

    @Override
    public void onBindHoder(ViewHolder holder, FolderDocumentEntity folderDocumentEntity, int position) {
        super.onBindHoder(holder, folderDocumentEntity, position);
        TextView document_desc_tv = holder.obtainView(R.id.document_desc_tv);
        ImageView document_expand_iv = holder.obtainView(R.id.document_expand_iv);
        CheckedTextView folder_document_ctv = holder.obtainView(R.id.folder_document_ctv);

        if (folder_document_ctv != null && folderDocumentEntity.isDir()) {
            folder_document_ctv.setVisibility(folderSelectable ? View.VISIBLE : View.INVISIBLE);
        } else if (folder_document_ctv != null && !folderDocumentEntity.isDir()) {
            folder_document_ctv.setVisibility(fileSelectable ? View.VISIBLE : View.INVISIBLE);
        }
        if (document_desc_tv != null) {
            document_desc_tv.setVisibility(isSelectable() ? View.GONE : View.VISIBLE);
        }
        if (document_expand_iv != null) {
            document_expand_iv.setVisibility(isSelectable() ? View.GONE : View.VISIBLE);
        }
    }
}
