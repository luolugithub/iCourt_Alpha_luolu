package com.icourt.alpha.adapter;

import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.adapter.baseadapter.BaseArrayRecyclerAdapter;
import com.icourt.alpha.entity.bean.ProjectEntity;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/5/11
 * version 1.0.0
 */
public class ProjectAdapter extends BaseArrayRecyclerAdapter<ProjectEntity> {
    @Override
    public int bindView(int viewtype) {
        return R.layout.adapter_item_projrct_title;
    }

    @Override
    public void onBindHoder(ViewHolder holder, ProjectEntity projectEntity, int position) {
        if (projectEntity == null) return;
        TextView project_title_tv = holder.obtainView(R.id.project_title_tv);
        project_title_tv.setText(projectEntity.name);
    }
}
