package com.icourt.alpha.adapter;

import android.text.TextUtils;
import android.view.View;
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
    boolean canRevert;

    public FileChangedHistoryAdapter(boolean canRevert) {
        this.canRevert = canRevert;
    }

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
        file_action_title_tv.setText(getCombineActionTitle(fileChangedHistoryEntity));

        file_title_tv.setText(fileChangedHistoryEntity.file_name);
        file_time_tv.setText(DateUtils.getFormatChatTime(fileChangedHistoryEntity.date));
        holder.bindChildClick(file_restore_iv);
        file_restore_iv.setVisibility(canRevert && canRestore(fileChangedHistoryEntity) ? View.VISIBLE : View.GONE);

    }
    /**
     * 是否可以撤销
     *
     * @param fileChangedHistoryEntity
     * @return
     */
    private boolean canRestore(FileChangedHistoryEntity fileChangedHistoryEntity) {
        if (fileChangedHistoryEntity == null) return false;
        String actionTypeEnglish = fileChangedHistoryEntity.op_type;
        if (TextUtils.equals(actionTypeEnglish, "delete")) {
            return true;
        } else if (TextUtils.equals(actionTypeEnglish, "create")) {
            return true;
        } else if (TextUtils.equals(actionTypeEnglish, "move")) {
            return true;
        } else if (TextUtils.equals(actionTypeEnglish, "recover")) {
            return false;
        } else if (TextUtils.equals(actionTypeEnglish, "rename")) {
            return true;
        } else if (TextUtils.equals(actionTypeEnglish, "edit")) {
            return true;
        }
        return false;
    }

    /**
     * 获取组合的文件操作标题
     * eg. xx 重命名了文件
     * <p>
     * 1. 重命名 文件/文件夹
     * 2. 删除了文件／文件夹
     * 3. 恢复了文件(没有撤销按钮)
     * 4. 新增了文件／文件夹
     * 5. 移动文件/文件夹
     * 6. 编辑了了文件
     *
     * @param fileChangedHistoryEntity
     */
    private String getCombineActionTitle(FileChangedHistoryEntity fileChangedHistoryEntity) {
        if (fileChangedHistoryEntity != null) {
            //文件类型
            String fileType = null;
            if (TextUtils.equals(fileChangedHistoryEntity.obj_type, "file")) {
                fileType = "文件";
            } else {
                fileType = "文件夹";
            }


            //操作类型
            String actionType;
            String actionTypeEnglish = fileChangedHistoryEntity.op_type;
            if (TextUtils.equals(actionTypeEnglish, "delete")) {
                actionType = "删除";
            } else if (TextUtils.equals(actionTypeEnglish, "create")) {
                actionType = "新增";
            } else if (TextUtils.equals(actionTypeEnglish, "move")) {
                actionType = "移动";
            } else if (TextUtils.equals(actionTypeEnglish, "recover")) {
                actionType = "恢复";
            } else if (TextUtils.equals(actionTypeEnglish, "rename")) {
                actionType = "重命名";
            } else if (TextUtils.equals(actionTypeEnglish, "edit")) {
                actionType = "编辑";
            } else {
                actionType = "未命名动作";
            }
            String actionUserName;
            int lengthLimit = 12;
            if (!TextUtils.isEmpty(fileChangedHistoryEntity.operator_name)
                    && fileChangedHistoryEntity.operator_name.length() > lengthLimit) {
                actionUserName = fileChangedHistoryEntity.operator_name.substring(0, lengthLimit);
                actionUserName = actionUserName.concat("...");
            } else {
                actionUserName = fileChangedHistoryEntity.operator_name;
            }
            return String.format("%s  %s了%s", actionUserName, actionType, fileType);
        }
        return "";
    }
}
