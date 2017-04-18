package com.icourt.alpha.adapter;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.icourt.alpha.BuildConfig;
import com.icourt.alpha.R;
import com.icourt.alpha.activity.FileDetailsActivity;
import com.icourt.alpha.adapter.baseadapter.BaseArrayRecyclerAdapter;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.constants.Const;
import com.icourt.alpha.entity.bean.IMFileEntity;
import com.icourt.alpha.utils.ActionConstants;
import com.icourt.alpha.utils.DateUtils;
import com.icourt.alpha.utils.FileUtils;
import com.icourt.alpha.utils.GlideUtils;
import com.icourt.alpha.utils.IMUtils;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/17
 * version 1.0.0
 */
public class FileAdapter extends BaseArrayRecyclerAdapter<IMFileEntity> implements BaseRecyclerAdapter.OnItemClickListener {
    private final int TYPE_IMG = 1;

    private String loginToken;

    public FileAdapter(String loginToken) {
        this.loginToken = loginToken;
        this.setOnItemClickListener(this);
    }

    @Override
    public int bindView(int viewtype) {
        if (viewtype == TYPE_IMG) {
            return R.layout.adapter_item_file_type_img;
        }
        return R.layout.adapter_item_file_type_comm;
    }

    @Override
    public int getItemViewType(int position) {
        IMFileEntity item = getItem(position);
        if (item != null
                && item.content != null
                && isPic(item.content.file)) {
            return TYPE_IMG;
        }
        return super.getItemViewType(position);
    }


    @Override
    public void onBindHoder(ViewHolder holder, IMFileEntity imFileEntity, int position) {
        if (imFileEntity == null) return;
        ImageView file_from_user_iv = holder.obtainView(R.id.file_from_user_iv);
        TextView file_from_user_tv = holder.obtainView(R.id.file_from_user_tv);
        TextView file_from_time_tv = holder.obtainView(R.id.file_from_time_tv);

        GlideUtils.loadUser(file_from_user_iv.getContext(), imFileEntity.pic, file_from_user_iv);
        file_from_user_tv.setText(imFileEntity.createName);
        file_from_time_tv.setText(DateUtils.getTimeShowString(imFileEntity.createDate, true));

        if (holder.getItemViewType() == TYPE_IMG) {
            ImageView file_img = holder.obtainView(R.id.file_img);
            if (GlideUtils.canLoadImage(file_img.getContext())) {
                Glide.with(file_img.getContext())
                        .load(getCombPicUrl((imFileEntity != null && imFileEntity.content != null)
                                ? imFileEntity.content.path : ""))
                        .placeholder(R.drawable.bg_round_rect_gray)
                        .into(file_img);
            }
        } else {
            if (imFileEntity.content != null) {
                ImageView file_type_iv = holder.obtainView(R.id.file_type_iv);
                TextView file_title_tv = holder.obtainView(R.id.file_title_tv);
                TextView file_size_tv = holder.obtainView(R.id.file_size_tv);
                file_type_iv.setImageResource(getFileIcon40(imFileEntity.content.file));
                file_title_tv.setText(imFileEntity.content.file);
                file_size_tv.setText(FileUtils.kbFromat(imFileEntity.content.size));
            }
        }
    }

    /**
     * 是否是图片
     *
     * @param path
     * @return
     */
    public boolean isPic(String path) {
        return IMUtils.isPIC(path);
    }

    /**
     * 获取文件对应图标
     *
     * @param fileName
     * @return
     */
    public static int getFileIcon40(String fileName) {
        if (!TextUtils.isEmpty(fileName) && fileName.length() > 0) {
            String type = fileName.substring(fileName.lastIndexOf(".") + 1);
            if (ActionConstants.resourcesMap40.containsKey(type)) {
                return ActionConstants.resourcesMap40.get(type);
            }
        }
        return R.mipmap.filetype_default_40;
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        if (holder != null) {
            if (holder.getItemViewType() == TYPE_IMG) {
                ImageView file_img = holder.obtainView(R.id.file_img);
                try {
                    Glide.clear(file_img);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }
            }
        }
        super.onViewRecycled(holder);
    }

    /**
     * 获取组合拼接的图片原地址
     *
     * @param path
     * @return
     */
    private String getCombPicUrl(String path) {
        StringBuilder urlBuilder = new StringBuilder(BuildConfig.HOST_URL);
        urlBuilder.append(Const.HTTP_DOWNLOAD_FILE);
        urlBuilder.append("?sFileId=");
        urlBuilder.append(path);
        urlBuilder.append("&token=");
        urlBuilder.append(loginToken);
        urlBuilder.append("&width=");
        urlBuilder.append("480");
        return urlBuilder.toString();
    }

    @Override
    public void onItemClick(BaseRecyclerAdapter adapter, ViewHolder holder, View view, int position) {
        IMFileEntity item = getItem(getRealPos(position));
        FileDetailsActivity.launch(view.getContext(), item);
    }
}
