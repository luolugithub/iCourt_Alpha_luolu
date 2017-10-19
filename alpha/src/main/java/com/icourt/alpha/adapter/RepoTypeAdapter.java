package com.icourt.alpha.adapter;

import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.adapter.baseadapter.BaseArrayRecyclerAdapter;
import com.icourt.alpha.entity.bean.RepoTypeEntity;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/9/9
 * version 2.1.0
 */
public class RepoTypeAdapter extends BaseArrayRecyclerAdapter<RepoTypeEntity> {
    @Override
    public int bindView(int viewtype) {
        return R.layout.adapter_item_repo_type;
    }

    @Override
    public void onBindHoder(ViewHolder holder, RepoTypeEntity repoTypeEntity, int position) {
        if (repoTypeEntity == null) return;
        TextView repo_name_tv = holder.obtainView(R.id.repo_name_tv);
        repo_name_tv.setText(repoTypeEntity.title);
    }
}
