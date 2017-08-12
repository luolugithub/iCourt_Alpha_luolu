package com.icourt.alpha.adapter;

import android.text.TextUtils;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.constants.Const;
import com.icourt.alpha.entity.bean.FolderDocumentEntity;
import com.icourt.alpha.interfaces.ISeaFileImageLoader;
import com.icourt.alpha.utils.ActionConstants;
import com.icourt.alpha.utils.DateUtils;
import com.icourt.alpha.utils.FileUtils;
import com.icourt.alpha.utils.IMUtils;

import java.util.Set;

import static com.icourt.alpha.constants.Const.VIEW_TYPE_GRID;
import static com.icourt.alpha.constants.Const.VIEW_TYPE_ITEM;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/8/10
 * version 2.1.0
 */
public class FolderDocumentAdapter extends SFileImgBaseAdapter<FolderDocumentEntity> {

    private Set<FolderDocumentEntity> selectedFolderDocuments;
    @Const.AdapterViewType
    int adapterViewType;

    public FolderDocumentAdapter(@Const.AdapterViewType int adapterViewType,
                                 ISeaFileImageLoader seaFileImageLoader,
                                 boolean selectable,
                                 Set<FolderDocumentEntity> selectedFolderDocuments) {
        super(seaFileImageLoader, selectable);
        this.adapterViewType = adapterViewType;
        this.selectedFolderDocuments = selectedFolderDocuments;
    }


    public
    @Const.AdapterViewType
    int getAdapterViewType() {
        return adapterViewType;
    }

    public void setAdapterViewType(@Const.AdapterViewType int adapterViewType) {
        if (this.adapterViewType != adapterViewType) {
            this.adapterViewType = adapterViewType;
            this.notifyDataSetChanged();
        }
    }

    @Override
    public int bindView(int viewtype) {
        switch (viewtype) {
            case VIEW_TYPE_ITEM:
                return R.layout.adapter_item_folder_document;
            case VIEW_TYPE_GRID:
                return R.layout.adapter_grid_folder_document;
        }
        return R.layout.adapter_item_folder_document;
    }

    @Override
    public int getItemViewType(int position) {
        return adapterViewType;
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

    @Override
    public void onBindHoder(ViewHolder holder, FolderDocumentEntity folderDocumentEntity, int position) {
        if (folderDocumentEntity == null) return;
        CheckedTextView folder_document_ctv = holder.obtainView(R.id.folder_document_ctv);
        switch (holder.getItemViewType()) {
            case VIEW_TYPE_GRID: {
                ImageView document_type_iv = holder.obtainView(R.id.document_type_iv);
                TextView document_title_tv = holder.obtainView(R.id.document_title_tv);
                ImageView document_pic_iv = holder.obtainView(R.id.document_pic_iv);
                document_title_tv.setText(folderDocumentEntity.name);
                if (folderDocumentEntity.isDir()) {
                    document_pic_iv.setVisibility(View.GONE);
                    document_type_iv.setImageResource(R.mipmap.folder);
                } else {
                    if (IMUtils.isPIC(folderDocumentEntity.name)) {
                        document_pic_iv.setVisibility(View.VISIBLE);
                        loadSFileImage(folderDocumentEntity.name, document_pic_iv, 0, 250);
                    } else {
                        document_pic_iv.setVisibility(View.GONE);
                        setFileTypeIcon(document_type_iv, folderDocumentEntity.name);
                    }
                }
            }
            break;
            case VIEW_TYPE_ITEM: {
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
                    if (IMUtils.isPIC(folderDocumentEntity.name)) {
                        loadSFileImage(folderDocumentEntity.name, document_type_iv, 0, 250);
                    } else {
                        setFileTypeIcon(document_type_iv, folderDocumentEntity.name);
                    }
                }
                document_expand_iv.setVisibility(isSelectable() ? View.GONE : View.VISIBLE);
            }
            break;
        }
        if (folder_document_ctv != null) {
            folder_document_ctv.setVisibility(isSelectable() ? View.VISIBLE : View.GONE);
            folder_document_ctv.setChecked(selectedFolderDocuments.contains(folderDocumentEntity));
        }
    }
}
