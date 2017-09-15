package com.icourt.alpha.adapter;

import android.support.annotation.DrawableRes;
import android.widget.ImageView;

import com.icourt.alpha.BuildConfig;
import com.icourt.alpha.adapter.baseadapter.BaseArrayRecyclerAdapter;
import com.icourt.alpha.entity.bean.ISeaFileImage;
import com.icourt.alpha.utils.FileUtils;
import com.icourt.alpha.utils.GlideUtils;
import com.icourt.alpha.utils.SFileTokenUtils;
import com.icourt.alpha.utils.UrlUtils;

/**
 * Description  sea file加载图片的适配器
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/9/12
 * version 2.1.0
 */
public abstract class SeaFileImageBaseAdapter<T extends ISeaFileImage> extends BaseArrayRecyclerAdapter<T> {
    boolean selectable;

    public SeaFileImageBaseAdapter(boolean selectable) {
        this.selectable = selectable;
    }
    public SeaFileImageBaseAdapter() {
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
     * @param iSeaFileImage
     * @param view
     */
    protected void loadSFileImage(ISeaFileImage iSeaFileImage, ImageView view) {
        if (iSeaFileImage == null) return;
        if (view == null) return;
        GlideUtils.loadSFilePic(view.getContext(), getSFileImageThumbUrl(iSeaFileImage), view);
    }

    /**
     * 获取图片地址
     *
     * @param iSeaFileImage
     * @return
     */
    protected String getSFileImageThumbUrl(ISeaFileImage iSeaFileImage) {
        return String.format("%silaw/api/v2/documents/thumbnailImage?repoId=%s&seafileToken=%s&p=%s&size=%s",
                BuildConfig.API_URL,
                iSeaFileImage.getSeaFileImageRepoId(),
                SFileTokenUtils.getSFileToken(),
                UrlUtils.encodeUrl(iSeaFileImage.getSeaFileImageFullPath()),
                150);
    }

    /**
     * 获取文件对应图标
     *
     * @param fileName
     * @return
     */
    @DrawableRes
    protected int getSFileTypeIcon(String fileName) {
        return FileUtils.getSFileIcon(fileName);
    }
}
