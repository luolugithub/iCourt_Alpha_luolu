package com.icourt.alpha.entity.event;

import com.icourt.alpha.constants.Const;

/**
 * Description  文件处理事件
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/8/14
 * version 2.1.0
 */
public class SeaFolderEvent {

    public String from_repo_id;
    public String from_parent_dir;

    @Const.FILE_ACTION_TYPE
    public int action_type;

    public SeaFolderEvent(@Const.FILE_ACTION_TYPE int action_type, String from_repo_id, String from_parent_dir) {
        this.action_type = action_type;
        this.from_repo_id = from_repo_id;
        this.from_parent_dir = from_parent_dir;
    }
}
