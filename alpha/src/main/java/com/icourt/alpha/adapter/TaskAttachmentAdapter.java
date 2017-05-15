package com.icourt.alpha.adapter;

import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.adapter.baseadapter.BaseArrayRecyclerAdapter;
import com.icourt.alpha.entity.bean.TaskAttachmentEntity;
import com.icourt.alpha.utils.FileUtils;

/**
 * Description  任务附件适配器
 * Company Beijing icourt
 * author  lu.zhao  E-mail:zhaolu@icourt.cc
 * date createTime：17/5/15
 * version 2.0.0
 */

public class TaskAttachmentAdapter extends BaseArrayRecyclerAdapter<TaskAttachmentEntity> {
    @Override
    public int bindView(int viewtype) {
        return R.layout.adapter_item_task_attachment_layout;
    }

    @Override
    public void onBindHoder(ViewHolder holder, TaskAttachmentEntity taskAttachmentEntity, int position) {
        ImageView iconView = holder.obtainView(R.id.task_file_icon);
        TextView nameView = holder.obtainView(R.id.task_file_name_tv);
        TextView userView = holder.obtainView(R.id.task_file_user_tv);
        TextView sizeView = holder.obtainView(R.id.task_file_size_tv);

        if (taskAttachmentEntity.pathInfoVo != null) {
            iconView.setImageResource(FileUtils.getFileIcon40(taskAttachmentEntity.pathInfoVo.filePath));
            if (!TextUtils.isEmpty(taskAttachmentEntity.pathInfoVo.filePath) && taskAttachmentEntity.pathInfoVo.filePath.contains("/"))
                nameView.setText(taskAttachmentEntity.pathInfoVo.filePath.substring(taskAttachmentEntity.pathInfoVo.filePath.lastIndexOf("/") + 1));
        }
        sizeView.setText(FileUtils.bFormat(taskAttachmentEntity.fileSize));
    }
}
