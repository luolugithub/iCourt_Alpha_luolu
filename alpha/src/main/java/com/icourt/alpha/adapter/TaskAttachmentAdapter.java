package com.icourt.alpha.adapter;

import android.widget.ImageView;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.entity.bean.TaskAttachmentEntity;
import com.icourt.alpha.utils.FileUtils;
import com.icourt.alpha.utils.IMUtils;

/**
 * Description  任务附件适配器
 * Company Beijing icourt
 * author  lu.zhao  E-mail:zhaolu@icourt.cc
 * date createTime：17/5/15
 * version 2.0.0
 */

public class TaskAttachmentAdapter extends SeaFileImageBaseAdapter<TaskAttachmentEntity> {
    @Override
    public int bindView(int viewType) {
        return R.layout.adapter_item_task_attachment_layout;
    }

    @Override
    public void onBindHoder(ViewHolder holder, TaskAttachmentEntity taskAttachmentEntity, int position) {
        ImageView iconView = holder.obtainView(R.id.task_file_icon);
        TextView nameView = holder.obtainView(R.id.task_file_name_tv);
        TextView userView = holder.obtainView(R.id.task_file_user_tv);
        TextView sizeView = holder.obtainView(R.id.task_file_size_tv);
        if (IMUtils.isPIC(taskAttachmentEntity.getSeaFileFullPath())) {
            loadSFileImage(taskAttachmentEntity, iconView);
        } else {
            iconView.setImageResource(getSFileTypeIcon(taskAttachmentEntity.getSeaFileFullPath()));
        }
        nameView.setText(FileUtils.getFileName(taskAttachmentEntity.getSeaFileFullPath()));
        sizeView.setText(FileUtils.bFormat(taskAttachmentEntity.fileSize));
    }
}
