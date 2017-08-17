package com.icourt.alpha.adapter;

import android.widget.ImageView;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.adapter.baseadapter.BaseArrayRecyclerAdapter;
import com.icourt.alpha.entity.bean.FileChangedHistoryEntity;
import com.icourt.alpha.utils.DateUtils;
import com.icourt.alpha.utils.GlideUtils;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/8/17
 * version 2.1.0
 */
public class FileChangedHistoryAdapter extends BaseArrayRecyclerAdapter<FileChangedHistoryEntity> {
    @Override
    public int bindView(int viewtype) {
        return R.layout.adapter_item_file_change_history;
    }

    @Override
    public void onBindHoder(ViewHolder holder, FileChangedHistoryEntity fileChangedHistoryEntity, int position) {
        if (fileChangedHistoryEntity == null) return;
        ImageView file_action_user_icon_iv = holder.obtainView(R.id.file_action_user_icon_iv);
        ImageView file_restore_iv = holder.obtainView(R.id.file_restore_iv);
        TextView file_action_title_tv = holder.obtainView(R.id.file_action_title_tv);
        TextView file_title_tv = holder.obtainView(R.id.file_title_tv);
        TextView file_time_tv = holder.obtainView(R.id.file_time_tv);

        GlideUtils.loadUser(file_action_user_icon_iv.getContext(), fileChangedHistoryEntity.pic, file_action_user_icon_iv);
        file_action_title_tv.setText(
                String.format("%s %s 了文件",
                        fileChangedHistoryEntity.operator_name,
                        fileChangedHistoryEntity.op_type));

        file_title_tv.setText(fileChangedHistoryEntity.file_name);
        file_time_tv.setText(DateUtils.getFormatChatTime(fileChangedHistoryEntity.date));
        holder.bindChildClick(file_restore_iv);
    }
}
