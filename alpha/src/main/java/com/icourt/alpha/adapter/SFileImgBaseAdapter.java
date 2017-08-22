package com.icourt.alpha.adapter;

import android.support.annotation.DrawableRes;
import android.widget.ImageView;

import com.icourt.alpha.BuildConfig;
import com.icourt.alpha.adapter.baseadapter.BaseArrayRecyclerAdapter;
import com.icourt.alpha.utils.FileUtils;
import com.icourt.alpha.utils.GlideUtils;
import com.icourt.alpha.utils.SFileTokenUtils;

/**
 * Description  sfile图片加载适配器
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/8/12
 * version 2.1.0
 */
public abstract class SFileImgBaseAdapter<T> extends BaseArrayRecyclerAdapter<T> {

    boolean selectable;
    private String seaFileRepoId;
    private String seaFileDirPath;

    public String getSeaFileRepoId() {
        return seaFileRepoId;
    }

    public String getSeaFileDirPath() {
        return seaFileDirPath;
    }

    public SFileImgBaseAdapter(String seaFileRepoId, String seaFileDirPath, boolean selectable) {
        this.seaFileRepoId = seaFileRepoId;
        this.seaFileDirPath = seaFileDirPath;
        this.selectable = selectable;
    }

    public boolean isSelectable() {
        return selectable;
    }

    public void setSelectable(boolean selectable) {
        this.selectable = selectable;
    }

    /**
     * 加载图片
     *
     * @param fileName
     * @param view
     */
    protected void loadSFileImage(String fileName, ImageView view) {
        if (view == null) return;
        GlideUtils.loadSFilePic(view.getContext(), getSFileImageThumbUrl(fileName), view);
    }

    /**
     * 获取地址
     *
     * @param name
     * @return
     */
    protected String getSFileImageThumbUrl(String name) {
        return String.format("%silaw/api/v2/documents/thumbnailImage?repoId=%s&seafileToken=%s&p=%s&size=%s",
                BuildConfig.API_URL,
                seaFileRepoId,
                SFileTokenUtils.getSFileToken(),
                String.format("%s%s", seaFileDirPath, name),
                150);
    }

    /**
     * 获取文件对应图标
     *
     * @param fileName
     * @return
     */
    @DrawableRes
    public static int getSFileTypeIcon(String fileName) {
        return FileUtils.getSFileIcon(fileName);
    }
}
