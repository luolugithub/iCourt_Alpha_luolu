package com.icourt.alpha.adapter;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.icourt.alpha.BuildConfig;
import com.icourt.alpha.R;
import com.icourt.alpha.activity.ImagePagerActivity;
import com.icourt.alpha.adapter.baseadapter.BaseArrayRecyclerAdapter;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.constants.Const;
import com.icourt.alpha.entity.bean.IMStringWrapEntity;
import com.icourt.alpha.utils.ActionConstants;
import com.icourt.alpha.utils.DateUtils;
import com.icourt.alpha.utils.FileUtils;
import com.icourt.alpha.utils.GlideUtils;
import com.icourt.alpha.utils.IMUtils;
import com.icourt.alpha.utils.SpannableUtils;
import com.icourt.alpha.utils.SystemUtils;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/17
 * version 1.0.0
 */
public class ImUserMessageDetailAdapter extends BaseArrayRecyclerAdapter<IMStringWrapEntity> implements BaseRecyclerAdapter.OnItemChildClickListener {
    private static final int VIEW_TYPE_TEXT = 0;
    private static final int VIEW_TYPE_FILE = 1;
    private static final int VIEW_TYPE_FILE_IMG = 2;
    private static final int VIEW_TYPE_DING = 3;
    private static final int VIEW_TYPE_AT = 4;
    private static final int VIEW_TYPE_SYS = 5;

    private String loginToken;

    public ImUserMessageDetailAdapter(String loginToken) {
        this.loginToken = loginToken;
        this.setOnItemChildClickListener(this);
    }

    @Override
    public int bindView(int viewtype) {
        switch (viewtype) {
            case VIEW_TYPE_TEXT:
            case VIEW_TYPE_AT:
            case VIEW_TYPE_SYS:
            case VIEW_TYPE_DING:
                return R.layout.adapter_user_message_detail_txt;
            case VIEW_TYPE_FILE_IMG:
                return R.layout.adapter_user_message_detail_img;
            case VIEW_TYPE_FILE:
                return R.layout.adapter_user_message_detail_file;
            default:
                return R.layout.adapter_user_message_detail_txt;
        }
    }

    @Override
    public int getItemViewType(int position) {
        IMStringWrapEntity item = getItem(position);
        if (item != null
                && item.content != null) {
            switch (item.content.show_type) {
                case IMStringWrapEntity.StringWrapIMContent.MSG_TYPE_TEXT:
                    return VIEW_TYPE_TEXT;
                case IMStringWrapEntity.StringWrapIMContent.MSG_TYPE_FILE:
                    return isPic(item.content.file) ? VIEW_TYPE_FILE_IMG : VIEW_TYPE_FILE;
                case IMStringWrapEntity.StringWrapIMContent.MSG_TYPE_DING:
                    return VIEW_TYPE_DING;
                case IMStringWrapEntity.StringWrapIMContent.MSG_TYPE_AT:
                    return VIEW_TYPE_AT;
                case IMStringWrapEntity.StringWrapIMContent.MSG_TYPE_SYS:
                    return VIEW_TYPE_SYS;
            }
        }
        return super.getItemViewType(position);
    }


    @Override
    public void onBindHoder(ViewHolder holder, IMStringWrapEntity imFileEntity, int position) {
        if (imFileEntity == null) return;
        ImageView file_from_user_iv = holder.obtainView(R.id.file_from_user_iv);
        TextView file_from_user_tv = holder.obtainView(R.id.file_from_user_tv);
        TextView file_upload_time = holder.obtainView(R.id.file_upload_time);
        TextView file_from_tv = holder.obtainView(R.id.file_from_tv);

        GlideUtils.loadUser(file_from_user_iv.getContext(), imFileEntity.pic, file_from_user_iv);
        file_from_user_tv.setText(imFileEntity.createName);
        file_upload_time.setText(DateUtils.getTimeShowString(imFileEntity.createDate, true));
        /**
         * Open==1 群组。group name
         * open==0 单聊 createName
         */
        String targetText = imFileEntity.open == 1 ? imFileEntity.groupName : imFileEntity.createName;
        String originalText = String.format("来自: %s", targetText);
        SpannableUtils.setTextForegroundColorSpan(file_from_tv, originalText, targetText, SystemUtils.getColor(file_from_tv.getContext(), R.color.alpha_font_color_black));


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
                holder.bindChildClick(file_img);
                setViewTypeWithImg(file_img, imFileEntity);
                break;
            case VIEW_TYPE_FILE:
                setViewFileCommFile(holder, imFileEntity);
                break;
        }
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
                            ? imFileEntity.content.path : "", "640"))
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
//                    try {-
//                        Glide.with(file_img.getContext())
//                                .load("")
//                                .downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
//                                .get();
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    } catch (ExecutionException e) {
//                        e.printStackTrace();
//                    }
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
    private String getCombPicUrl(String path, String width) {
        StringBuilder urlBuilder = new StringBuilder(BuildConfig.HOST_URL);
        urlBuilder.append(Const.HTTP_DOWNLOAD_FILE);
        urlBuilder.append("?sFileId=");
        urlBuilder.append(path);
        urlBuilder.append("&token=");
        urlBuilder.append(loginToken);
        urlBuilder.append("&width=");
        urlBuilder.append("640");
        return urlBuilder.toString();
    }


    @Override
    public void onItemChildClick(BaseRecyclerAdapter adapter, ViewHolder holder, View view, int position) {
        switch (view.getId()) {
            case R.id.file_img:
                IMStringWrapEntity item = getItem(getRealPos(position));
                if (item != null && item.content != null) {
                    ImagePagerActivity.launch(view.getContext(),
                            new String[]{getCombPicUrl(item.content.path, "1920")});
                }
                break;
        }
    }


}
