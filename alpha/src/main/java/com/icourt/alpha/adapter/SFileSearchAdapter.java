package com.icourt.alpha.adapter;

import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.icourt.alpha.BuildConfig;
import com.icourt.alpha.R;
import com.icourt.alpha.adapter.baseadapter.BaseArrayRecyclerAdapter;
import com.icourt.alpha.entity.bean.SFileSearchEntity;
import com.icourt.alpha.utils.FileUtils;
import com.icourt.alpha.utils.GlideUtils;
import com.icourt.alpha.utils.IMUtils;
import com.icourt.alpha.utils.SFileTokenUtils;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/8/20
 * version 2.1.0
 */
public class SFileSearchAdapter extends BaseArrayRecyclerAdapter<SFileSearchEntity> {
    @Override
    public int bindView(int viewtype) {
        return R.layout.adapter_item_folder;
    }

    @Override
    public void onBindHoder(ViewHolder holder, SFileSearchEntity sFileSearchEntity, int position) {
        if (sFileSearchEntity == null) return;
        ImageView folder_type_iv = holder.obtainView(R.id.folder_type_iv);
        TextView document_title_tv = holder.obtainView(R.id.document_title_tv);
        TextView document_desc_tv = holder.obtainView(R.id.document_desc_tv);

        document_title_tv.setText(sFileSearchEntity.name);

        //显示文件类别图片
        if (sFileSearchEntity.is_dir) {
            folder_type_iv.setImageResource(R.mipmap.folder);
        } else {//文件是图片加载缩略图
            if (IMUtils.isPIC(sFileSearchEntity.name)) {
                loadSFileImage(sFileSearchEntity.repo_id, sFileSearchEntity.fullpath, folder_type_iv);
            } else {
                folder_type_iv.setImageResource(FileUtils.getSFileIcon(sFileSearchEntity.name));
            }
        }

        //资料库/目录路径  eg. "我的资料库/img/"
        String fileDir = sFileSearchEntity.fullpath;
        if (!sFileSearchEntity.is_dir) {
            if (!TextUtils.isEmpty(fileDir)) {
                int indexOf = fileDir.lastIndexOf("/");
                if (indexOf == 0) {
                    fileDir = "/";
                } else if (indexOf > 0) {
                    fileDir = fileDir.substring(0, indexOf);
                }
            }
        }
        document_desc_tv.setText(String.format("%s%s", sFileSearchEntity.repo_name, fileDir));
    }

    /**
     * 加载图片
     *
     * @param seaFileRepoId
     * @param view
     */
    protected void loadSFileImage(String seaFileRepoId, String fullPath, ImageView view) {
        if (view == null) return;
        GlideUtils.loadSFilePic(view.getContext(), getSFileImageThumbUrl(seaFileRepoId, fullPath), view);
    }

    /**
     * 获取地址
     *
     * @param fullPath 全路径
     * @return
     */
    protected String getSFileImageThumbUrl(String seaFileRepoId, String fullPath) {
        return String.format("%silaw/api/v2/documents/thumbnailImage?repoId=%s&seafileToken=%s&p=%s&size=%s",
                BuildConfig.API_URL,
                seaFileRepoId,
                SFileTokenUtils.getSFileToken(),
                fullPath,
                150);
    }
}
