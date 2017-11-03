package com.icourt.alpha.adapter;

import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.TextView;

import com.asange.recyclerviewadapter.BaseViewHolder;
import com.icourt.alpha.R;
import com.icourt.alpha.constants.Const;
import com.icourt.alpha.constants.SFileConfig;
import com.icourt.alpha.entity.bean.FolderDocumentEntity;
import com.icourt.alpha.utils.DateUtils;
import com.icourt.alpha.utils.FileUtils;
import com.icourt.alpha.utils.IMUtils;

import java.util.ArrayList;

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
public class FolderDocumentAdapter extends SeaFileImageBaseAdapter2<FolderDocumentEntity> {
    private ArrayList<FolderDocumentEntity> selectedFolderDocuments;
    @Const.AdapterViewType
    int adapterViewType;

    public FolderDocumentAdapter(@Const.AdapterViewType int adapterViewType,
                                 boolean selectable,
                                 ArrayList<FolderDocumentEntity> selectedFolderDocuments) {
        super(selectable);
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
    public void onBindHolder(BaseViewHolder holder, @Nullable FolderDocumentEntity folderDocumentEntity, int i) {
        if (folderDocumentEntity == null) {
            return;
        }
        switch (holder.getItemViewType()) {
            case VIEW_TYPE_GRID:
                setGridItemData(holder, folderDocumentEntity, i);
                break;
            case VIEW_TYPE_ITEM:
                setItemData(holder, folderDocumentEntity, i);
                break;
        }
    }

    @Override
    public int getViewType(int index) {
        return adapterViewType;
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
    protected void setGridItemData(BaseViewHolder holder, FolderDocumentEntity folderDocumentEntity, int position) {
        CheckedTextView folder_document_ctv = holder.obtainView(R.id.folder_document_ctv);
        ImageView document_type_iv = holder.obtainView(R.id.document_type_iv);
        TextView document_title_tv = holder.obtainView(R.id.document_title_tv);
        ImageView document_pic_iv = holder.obtainView(R.id.document_pic_iv);
        View folder_mask_view = holder.obtainView(R.id.folder_mask_view);

        document_title_tv.setText(folderDocumentEntity.name);
        if (folderDocumentEntity.isDir()) {
            document_pic_iv.setVisibility(View.GONE);
            document_type_iv.setImageResource(R.mipmap.folder);
        } else {
            if (IMUtils.isPIC(folderDocumentEntity.name)) {
                document_pic_iv.setVisibility(View.VISIBLE);
                loadSFileImage(folderDocumentEntity, document_pic_iv);
            } else {
                document_pic_iv.setVisibility(View.GONE);
                document_type_iv.setImageResource(getSFileTypeIcon(folderDocumentEntity.name));
            }
        }

        folder_document_ctv.setVisibility(isSelectable() ? View.VISIBLE : View.GONE);
        folder_document_ctv.setChecked(isSelected(folderDocumentEntity));
        folder_mask_view.setVisibility(isSelected(folderDocumentEntity) ? View.VISIBLE : View.GONE);
    }

    protected void setItemData(BaseViewHolder holder, FolderDocumentEntity folderDocumentEntity, int position) {
        if (folderDocumentEntity == null) {
            return;
        }
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
            document_desc_tv.setText(DateUtils.getStandardSimpleFormatTime(folderDocumentEntity.mtime * 1_000));
        } else {
            document_desc_tv.setText(String.format("%s, %s", FileUtils.bFormat(folderDocumentEntity.size), DateUtils.getStandardSimpleFormatTime(folderDocumentEntity.mtime * 1_000)));
            if (IMUtils.isPIC(folderDocumentEntity.name)) {
                loadSFileImage(folderDocumentEntity, document_type_iv);
            } else {
                document_type_iv.setImageResource(getSFileTypeIcon(folderDocumentEntity.name));
            }
        }

        if (!isSelectable()) {
            String permission = SFileConfig.convert2filePermission(folderDocumentEntity.permission);
            if (TextUtils.equals(permission, PERMISSION_RW)) {//可读写
                document_expand_iv.setVisibility(View.VISIBLE);
                document_detail_iv.setVisibility(View.GONE);
            } else {//可读
                if (folderDocumentEntity.isDir()) {
                    document_expand_iv.setVisibility(View.GONE);
                    document_detail_iv.setVisibility(View.GONE);
                } else {
                    document_expand_iv.setVisibility(View.GONE);
                    document_detail_iv.setVisibility(View.VISIBLE);
                }
            }
        } else {
            document_detail_iv.setVisibility(View.GONE);
            document_detail_iv.setVisibility(View.GONE);
        }

        folder_document_ctv.setVisibility(isSelectable() ? View.VISIBLE : View.GONE);
        folder_document_ctv.setChecked(isSelected(folderDocumentEntity));
    }
}
