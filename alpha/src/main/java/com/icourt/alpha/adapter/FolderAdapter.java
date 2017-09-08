package com.icourt.alpha.adapter;

import android.widget.ImageView;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.entity.bean.FolderDocumentEntity;
import com.icourt.alpha.utils.DateUtils;
import com.icourt.alpha.utils.FileUtils;
import com.icourt.alpha.utils.IMUtils;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/8/14
 * version 2.1.0
 */
public class FolderAdapter extends SFileImgBaseAdapter<FolderDocumentEntity> {
    public FolderAdapter() {
        super("", "", false);
    }

    public FolderAdapter(String seaFileRepoId, String seaFileDirPath, boolean selectable) {
        super(seaFileRepoId, seaFileDirPath, selectable);
    }

    @Override
    public int bindView(int viewtype) {
        return R.layout.adapter_item_folder;
    }

    @Override
    public void onBindHoder(ViewHolder holder, FolderDocumentEntity folderDocumentEntity, int position) {
        if (folderDocumentEntity == null) return;
        TextView document_title_tv = holder.obtainView(R.id.document_title_tv);
        ImageView folder_type_iv = holder.obtainView(R.id.folder_type_iv);
        TextView document_desc_tv = holder.obtainView(R.id.document_desc_tv);

        document_title_tv.setText(folderDocumentEntity.name);
        if (folderDocumentEntity.isDir()) {
            folder_type_iv.setImageResource(R.mipmap.folder);
            document_desc_tv.setText(DateUtils.getStandardSimpleFormatTime(folderDocumentEntity.mtime * 1_000));
        } else {
            document_desc_tv.setText(String.format("%s, %s",
                    FileUtils.bFormat(folderDocumentEntity.size), DateUtils.getStandardSimpleFormatTime(folderDocumentEntity.mtime * 1_000)));
            if (IMUtils.isPIC(folderDocumentEntity.name)) {
                loadSFileImage(folderDocumentEntity.name, folder_type_iv);
            } else {
                folder_type_iv.setImageResource(getSFileTypeIcon(folderDocumentEntity.name));
            }
        }
    }
}
