package com.icourt.alpha.adapter;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.adapter.baseadapter.BaseArrayRecyclerAdapter;
import com.icourt.alpha.constants.Const;
import com.icourt.alpha.entity.bean.FolderDocumentEntity;
import com.icourt.alpha.utils.ActionConstants;
import com.icourt.alpha.utils.DateUtils;
import com.icourt.alpha.utils.FileUtils;

import static com.icourt.alpha.constants.Const.VIEW_TYPE_GRID;
import static com.icourt.alpha.constants.Const.VIEW_TYPE_ITEM;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/8/10
 * version 2.1.0
 */
public class FolderDocumentAdapter extends BaseArrayRecyclerAdapter<FolderDocumentEntity> {

    @Const.AdapterViewType
    int adapterViewType;

    public FolderDocumentAdapter(@Const.AdapterViewType int adapterViewType) {
        this.adapterViewType = adapterViewType;
    }

    public FolderDocumentAdapter() {
        this.adapterViewType = VIEW_TYPE_ITEM;
    }

    @Override
    public int bindView(int viewtype) {
        switch (adapterViewType) {
            case VIEW_TYPE_ITEM:
                return R.layout.adapter_item_folder_document;
            case VIEW_TYPE_GRID:
                return R.layout.adapter_grid_folder_document;
        }
        return R.layout.adapter_item_folder_document;
    }

    @Override
    public void onBindHoder(ViewHolder holder, FolderDocumentEntity folderDocumentEntity, int position) {
        if (folderDocumentEntity == null) return;
        ImageView document_type_iv = holder.obtainView(R.id.document_type_iv);
        TextView document_title_tv = holder.obtainView(R.id.document_title_tv);
        TextView document_desc_tv = holder.obtainView(R.id.document_desc_tv);
        ImageView document_expand_iv = holder.obtainView(R.id.document_expand_iv);
        holder.bindChildClick(document_expand_iv);

        document_title_tv.setText(folderDocumentEntity.name);
        if (folderDocumentEntity.isDir()) {
            document_type_iv.setImageResource(R.mipmap.folder);
            document_desc_tv.setText(DateUtils.getFormatChatTimeSimple(folderDocumentEntity.mtime * 1_000));
        } else {
            document_desc_tv.setText(String.format("%s, %s", FileUtils.bFormat(folderDocumentEntity.size), DateUtils.getFormatChatTimeSimple(folderDocumentEntity.mtime * 1_000)));
            setFileTypeIcon(document_type_iv, folderDocumentEntity.name);
        }

        TextView folder_document_num_tv = holder.obtainView(R.id.folder_document_num_tv);
        if (position == getItemCount() - 1) {
            folder_document_num_tv.setVisibility(View.VISIBLE);
            int dirNum = 0, fileNum = 0;
            for (int i = 0; i < getItemCount(); i++) {
                FolderDocumentEntity item = getItem(i);
                if (item != null) {
                    if (item.isDir()) {
                        dirNum += 1;
                    } else {
                        fileNum += 1;
                    }
                }
            }
            folder_document_num_tv.setText(String.format("%s个文件夹, %s个文件", dirNum, fileNum));
        } else {
            folder_document_num_tv.setVisibility(View.GONE);
        }
    }

    /**
     * 设置文件类型的图标
     *
     * @param iv
     * @param fileName
     */
    private void setFileTypeIcon(ImageView iv, String fileName) {
        if (iv == null) return;
        iv.setImageResource(getFileIcon(fileName));
    }

    /**
     * 获取文件对应图标
     *
     * @param fileName
     * @return
     */
    public static int getFileIcon(String fileName) {
        if (!TextUtils.isEmpty(fileName) && fileName.length() > 0) {
            String type = fileName.substring(fileName.lastIndexOf(".") + 1);
            if (ActionConstants.resourcesDocumentIcon.containsKey(type)) {
                return ActionConstants.resourcesDocumentIcon.get(type);
            }
        }
        return R.mipmap.filetype_default;
    }
}
