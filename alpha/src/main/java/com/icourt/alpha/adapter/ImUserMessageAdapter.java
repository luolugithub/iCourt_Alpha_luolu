package com.icourt.alpha.adapter;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.icourt.alpha.BuildConfig;
import com.icourt.alpha.R;
import com.icourt.alpha.adapter.baseadapter.BaseArrayRecyclerAdapter;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.constants.Const;
import com.icourt.alpha.entity.bean.IMMessageCustomBody;
import com.icourt.alpha.entity.bean.IMStringWrapEntity;
import com.icourt.alpha.utils.ActionConstants;
import com.icourt.alpha.utils.FileUtils;
import com.icourt.alpha.utils.GlideUtils;
import com.icourt.alpha.utils.IMUtils;

import static com.icourt.alpha.constants.Const.MSG_TYPE_ALPHA;
import static com.icourt.alpha.constants.Const.MSG_TYPE_AT;
import static com.icourt.alpha.constants.Const.MSG_TYPE_DING;
import static com.icourt.alpha.constants.Const.MSG_TYPE_FILE;
import static com.icourt.alpha.constants.Const.MSG_TYPE_LINK;
import static com.icourt.alpha.constants.Const.MSG_TYPE_SYS;
import static com.icourt.alpha.constants.Const.MSG_TYPE_TXT;
import static com.icourt.alpha.constants.Const.MSG_TYPE_VOICE;

/**
 * Description 更用户相关的适配器[我的文件消息 我收藏的消息]
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/17
 * version 1.0.0
 */
public class ImUserMessageAdapter extends BaseArrayRecyclerAdapter<IMMessageCustomBody> implements BaseRecyclerAdapter.OnItemClickListener {
    private static final int VIEW_TYPE_TEXT = 0;
    private static final int VIEW_TYPE_FILE = 1;
    private static final int VIEW_TYPE_FILE_IMG = 2;
    private static final int VIEW_TYPE_DING = 3;
    private static final int VIEW_TYPE_AT = 4;
    private static final int VIEW_TYPE_SYS = 5;

    private String loginToken;

    public ImUserMessageAdapter(String loginToken) {
        this.loginToken = loginToken;
        this.setOnItemClickListener(this);
    }

    @Override
    public int bindView(int viewtype) {
        switch (viewtype) {
            case VIEW_TYPE_TEXT:
            case VIEW_TYPE_AT:
            case VIEW_TYPE_SYS:
            case VIEW_TYPE_DING:
                return R.layout.adapter_item_text_msg;
            case VIEW_TYPE_FILE_IMG:
                return R.layout.adapter_item_file_type_img;
            case VIEW_TYPE_FILE:
                return R.layout.adapter_item_file_type_comm;
            default:
                return R.layout.adapter_item_text_msg;
        }
    }

    @Override
    public int getItemViewType(int position) {
        IMMessageCustomBody item = getItem(position);
        if (item != null) {
            switch (item.show_type) {
                case MSG_TYPE_TXT:
                    return VIEW_TYPE_TEXT;
                case MSG_TYPE_FILE:
                    return isPic(item.ext != null ? item.ext.path : "") ? VIEW_TYPE_FILE_IMG : VIEW_TYPE_FILE;
                case MSG_TYPE_DING:
                    return VIEW_TYPE_DING;
                case MSG_TYPE_AT:
                    return VIEW_TYPE_AT;
                case MSG_TYPE_SYS:
                    return VIEW_TYPE_SYS;
                case MSG_TYPE_LINK://TODO 处理链接消息
                case MSG_TYPE_ALPHA:
                case MSG_TYPE_VOICE:
                    break;
            }
        }
        return super.getItemViewType(position);
    }


    @Override
    public void onBindHoder(ViewHolder holder, IMMessageCustomBody imFileEntity, int position) {
   /*     if (imFileEntity == null) return;
        ImageView file_from_user_iv = holder.obtainView(R.id.file_from_user_iv);
        TextView file_from_user_tv = holder.obtainView(R.id.file_from_user_tv);
        TextView file_from_time_tv = holder.obtainView(R.id.file_from_time_tv);

        GlideUtils.loadUser(file_from_user_iv.getContext(), imFileEntity.pic, file_from_user_iv);
        file_from_user_tv.setText(imFileEntity.createName);
        file_from_time_tv.setText(DateUtils.getTimeShowString(imFileEntity.createDate, true));

        switch (holder.getItemViewType()) {
            case VIEW_TYPE_TEXT:
            case VIEW_TYPE_AT:
            case VIEW_TYPE_SYS:
            case VIEW_TYPE_DING:
                TextView item_text = holder.obtainView(R.id.item_text);
                item_text.setText(imFileEntity.content.content);
                break;
            case VIEW_TYPE_FILE_IMG:
                ImageView file_img = holder.obtainView(R.id.file_img);
                setViewTypeWithImg(file_img, imFileEntity);
                break;
            case VIEW_TYPE_FILE:
                setViewFileCommFile(holder, imFileEntity);
                break;
        }*/
    }

    /**
     * 初始化布局 普通文件
     *
     * @param holder
     * @param imFileEntity
     */
    private void setViewFileCommFile(ViewHolder holder, IMStringWrapEntity imFileEntity) {
        if (holder == null) return;
        if (imFileEntity == null) return;
        if (imFileEntity.content == null) return;
        ImageView file_type_iv = holder.obtainView(R.id.file_type_iv);
        TextView file_title_tv = holder.obtainView(R.id.file_title_tv);
        TextView file_size_tv = holder.obtainView(R.id.file_size_tv);
        file_type_iv.setImageResource(getFileIcon40(imFileEntity.content.file));
        file_title_tv.setText(imFileEntity.content.file);
        file_size_tv.setText(FileUtils.kbFromat(imFileEntity.content.size));
    }

    /**
     * 初始化布局 图片
     *
     * @param file_img
     * @param imFileEntity
     */
    private void setViewTypeWithImg(ImageView file_img, IMStringWrapEntity imFileEntity) {
        if (file_img == null) return;
        if (imFileEntity == null) return;
        if (GlideUtils.canLoadImage(file_img.getContext())) {
            Glide.with(file_img.getContext())
                    .load(getCombPicUrl((imFileEntity != null && imFileEntity.content != null)
                            ? imFileEntity.content.path : ""))
                    .placeholder(R.drawable.bg_round_rect_gray)
                    .into(file_img);
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
            if (holder.getItemViewType() == VIEW_TYPE_FILE_IMG) {
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
       /* IMStringWrapEntity item = getItem(getRealPos(position));
        FileDetailsActivity.launch(view.getContext(), item);*/
    }
}
