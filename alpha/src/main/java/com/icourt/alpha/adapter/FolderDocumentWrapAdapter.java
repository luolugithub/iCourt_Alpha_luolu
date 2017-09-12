package com.icourt.alpha.adapter;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.icourt.alpha.R;
import com.icourt.alpha.constants.Const;
import com.icourt.alpha.entity.bean.FolderDocumentEntity;

import java.util.ArrayList;
import java.util.List;

import static com.icourt.alpha.constants.Const.VIEW_TYPE_ITEM;

/**
 * Description  嵌套 4个
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/8/10
 * version 2.1.0
 */
public class FolderDocumentWrapAdapter
        extends SFileImgBaseAdapter<List<FolderDocumentEntity>> {
    @Const.AdapterViewType
    int adapterViewType;

    private ArrayList<FolderDocumentEntity> selectedFolderDocuments;

    public FolderDocumentWrapAdapter(@Const.AdapterViewType int adapterViewType,
                                     String seaFileRepoId, String seaFileDirPath,
                                     boolean selectable,
                                     ArrayList<FolderDocumentEntity> selectedFolderDocuments) {
        super(seaFileRepoId,seaFileDirPath, selectable);
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
        return R.layout.adpater_item_folder_document_wrap;
    }

    @Override
    public void onBindHoder(ViewHolder holder, List<FolderDocumentEntity> folderDocumentEntities, int position) {
        if (folderDocumentEntities == null) return;
        RecyclerView recyclerView = holder.obtainView(R.id.recyclerView);
        recyclerView.setNestedScrollingEnabled(false);
        if (adapterViewType == VIEW_TYPE_ITEM) {
            if (recyclerView.getLayoutManager() == null
                    || recyclerView.getLayoutManager() instanceof GridLayoutManager) {
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(recyclerView.getContext());
                linearLayoutManager.setAutoMeasureEnabled(true);
                recyclerView.setLayoutManager(linearLayoutManager);
            }
        } else {
            if (recyclerView.getLayoutManager() == null
                    || recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
                GridLayoutManager gridLayoutManager = new GridLayoutManager(recyclerView.getContext(), 4);
                gridLayoutManager.setAutoMeasureEnabled(true);
                recyclerView.setLayoutManager(gridLayoutManager);
            }
        }
        FolderDocumentAdapter folderDocumentAdapter = (FolderDocumentAdapter) recyclerView.getAdapter();
        if (folderDocumentAdapter == null) {
            recyclerView.setAdapter(
                    folderDocumentAdapter = new FolderDocumentAdapter(adapterViewType,
                            getSeaFileRepoId(),
                            getSeaFileDirPath(),
                            isSelectable(),
                            selectedFolderDocuments));
        } else {
            folderDocumentAdapter.setAdapterViewType(adapterViewType);
            folderDocumentAdapter.setSelectable(isSelectable());
        }
        folderDocumentAdapter.setOnItemLongClickListener(super.onItemLongClickListener);
        folderDocumentAdapter.setOnItemClickListener(super.onItemClickListener);
        folderDocumentAdapter.setOnItemChildClickListener(super.onItemChildClickListener);
        folderDocumentAdapter.bindData(true, folderDocumentEntities);
    }
}
