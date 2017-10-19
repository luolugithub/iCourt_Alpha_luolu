package com.icourt.alpha.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.adapter.baseadapter.BaseArrayRecyclerAdapter;
import com.icourt.alpha.constants.SFileConfig;
import com.icourt.alpha.entity.bean.RepoEntity;
import com.icourt.alpha.utils.DateUtils;
import com.icourt.alpha.utils.FileUtils;

import static com.icourt.alpha.constants.SFileConfig.REPO_MINE;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/8/9
 * version 2.1.0
 */
public class RepoAdapter extends BaseArrayRecyclerAdapter<RepoEntity> {

    int type;

    /**
     * 0： "我的资料库",
     * 1： "共享给我的",
     * 2： "律所资料库",
     * 3： "项目资料库"
     *
     * @param type
     * @return
     */
    public RepoAdapter(@SFileConfig.REPO_TYPE int type) {
        this.type = type;
    }

    @Override
    public int bindView(int viewtype) {
        return R.layout.adapter_item_repo;
    }

    @Override
    public void onBindHoder(ViewHolder holder, RepoEntity repoEntity, int position) {
        if (repoEntity == null) return;
        ImageView document_type_iv = holder.obtainView(R.id.document_type_iv);
        TextView document_title_tv = holder.obtainView(R.id.document_title_tv);
        TextView document_desc_tv = holder.obtainView(R.id.document_desc_tv);
        ImageView document_expand_iv = holder.obtainView(R.id.document_expand_iv);
        ImageView document_detail_iv = holder.obtainView(R.id.document_detail_iv);
        holder.bindChildClick(document_expand_iv);
        holder.bindChildClick(document_detail_iv);
        switch (type) {
            case REPO_MINE:
                document_expand_iv.setVisibility(View.VISIBLE);
                document_detail_iv.setVisibility(View.GONE);
                break;
            default:
                document_expand_iv.setVisibility(View.GONE);
                document_detail_iv.setVisibility(View.VISIBLE);
                break;
        }

        document_expand_iv.setImageResource(type == 0 ? R.mipmap.ic_open_menu : R.mipmap.icon_about_16);
        document_title_tv.setText(repoEntity.repo_name);
        document_desc_tv.setText(String.format("%s, %s", FileUtils.bFormat(repoEntity.size), DateUtils.getStandardSimpleFormatTime(repoEntity.getUpdateTime())));

        if (!repoEntity.encrypted) {//非加密的
            document_type_iv.setImageResource(R.mipmap.ic_document);
        } else {
            if (repoEntity.isNeedDecrypt()) {//解密时间超时
                document_type_iv.setImageResource(R.mipmap.vault_locked);
                document_expand_iv.setVisibility(View.GONE);
                document_detail_iv.setVisibility(View.GONE);
            } else {
                document_type_iv.setImageResource(R.mipmap.vault_unlocked);
            }
        }
    }

}
