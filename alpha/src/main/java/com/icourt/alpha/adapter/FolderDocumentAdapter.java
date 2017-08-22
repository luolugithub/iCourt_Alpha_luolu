package com.icourt.alpha.adapter;

import android.text.TextUtils;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.constants.Const;
import com.icourt.alpha.constants.SFileConfig;
import com.icourt.alpha.entity.bean.FolderDocumentEntity;
import com.icourt.alpha.utils.DateUtils;
import com.icourt.alpha.utils.FileUtils;
import com.icourt.alpha.utils.IMUtils;

import java.util.Set;

import static com.icourt.alpha.constants.Const.VIEW_TYPE_GRID;
import static com.icourt.alpha.constants.Const.VIEW_TYPE_ITEM;
import static com.icourt.alpha.constants.SFileConfig.PERMISSION_RW;

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
                                 String seaFileRepoId, String seaFileDirPath,
                                 boolean selectable,
                                 Set<FolderDocumentEntity> selectedFolderDocuments) {
        super(seaFileRepoId, seaFileDirPath, selectable);
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


    @Override
    public void onBindHoder(ViewHolder holder, FolderDocumentEntity folderDocumentEntity, int position) {
        if (folderDocumentEntity == null) return;
        switch (holder.getItemViewType()) {
            case VIEW_TYPE_GRID:
                setGridItemData(holder, folderDocumentEntity, position);
                break;
            case VIEW_TYPE_ITEM:
                setItemData(holder, folderDocumentEntity, position);
                break;
        }
    }

    /**
     * 是否选中
     *
     * @param folderDocumentEntity
     * @return
     */

    private boolean isSelected(FolderDocumentEntity folderDocumentEntity) {
        return selectedFolderDocuments != null
                && selectedFolderDocuments.contains(folderDocumentEntity);
    }

    /**
     * 设置格子布局数据
     *
     * @param holder
     * @param folderDocumentEntity
     * @param position
     */
    private void setGridItemData(ViewHolder holder, FolderDocumentEntity folderDocumentEntity, int position) {
        CheckedTextView folder_document_ctv = holder.obtainView(R.id.folder_document_ctv);
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
                loadSFileImage(folderDocumentEntity.name, document_pic_iv);
            } else {
                document_pic_iv.setVisibility(View.GONE);
                document_type_iv.setImageResource(getSFileTypeIcon(folderDocumentEntity.name));
            }
        }
        folder_document_ctv.setVisibility(isSelectable() ? View.VISIBLE : View.GONE);
        folder_document_ctv.setChecked(isSelected(folderDocumentEntity));
    }

    private void setItemData(ViewHolder holder, FolderDocumentEntity folderDocumentEntity, int position) {
        CheckedTextView folder_document_ctv = holder.obtainView(R.id.folder_document_ctv);
        ImageView document_type_iv = holder.obtainView(R.id.document_type_iv);
        TextView document_title_tv = holder.obtainView(R.id.document_title_tv);
        TextView document_desc_tv = holder.obtainView(R.id.document_desc_tv);
        ImageView document_expand_iv = holder.obtainView(R.id.document_expand_iv);
        holder.bindChildClick(document_expand_iv);
        ImageView document_detail_iv = holder.obtainView(R.id.document_detail_iv);
        holder.bindChildClick(document_detail_iv);

        document_title_tv.setText(folderDocumentEntity.name);
        if (folderDocumentEntity.isDir()) {
            document_type_iv.setImageResource(R.mipmap.folder);
            document_desc_tv.setText(DateUtils.getFormatChatTimeSimple(folderDocumentEntity.mtime * 1_000));
        } else {
            document_desc_tv.setText(String.format("%s, %s", FileUtils.bFormat(folderDocumentEntity.size), DateUtils.getFormatChatTimeSimple(folderDocumentEntity.mtime * 1_000)));
            if (IMUtils.isPIC(folderDocumentEntity.name)) {
                loadSFileImage(folderDocumentEntity.name, document_type_iv);
            } else {
                document_type_iv.setImageResource(getSFileTypeIcon(folderDocumentEntity.name));
            }
        }
        String permission = SFileConfig.convert2filePermission(folderDocumentEntity.permission);
        boolean isShowActionView = !isSelectable() && TextUtils.equals(permission, PERMISSION_RW);
        boolean isShowDetailsView = !isSelectable() && !TextUtils.equals(permission, PERMISSION_RW);
        document_expand_iv.setVisibility(isShowActionView ? View.VISIBLE : View.GONE);
        document_detail_iv.setVisibility(isShowDetailsView ? View.VISIBLE : View.GONE);

        folder_document_ctv.setVisibility(isSelectable() ? View.VISIBLE : View.GONE);
        folder_document_ctv.setChecked(isSelected(folderDocumentEntity));
    }
}
