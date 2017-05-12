package com.icourt.alpha.adapter;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.icourt.alpha.R;
import com.icourt.alpha.activity.ImagePagerActivity;
import com.icourt.alpha.activity.WebViewActivity;
import com.icourt.alpha.adapter.baseadapter.BaseArrayRecyclerAdapter;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.entity.bean.GroupContactBean;
import com.icourt.alpha.entity.bean.IMMessageCustomBody;
import com.icourt.alpha.utils.ActionConstants;
import com.icourt.alpha.utils.DateUtils;
import com.icourt.alpha.utils.FileUtils;
import com.icourt.alpha.utils.GlideUtils;
import com.icourt.alpha.utils.SpannableUtils;
import com.icourt.alpha.utils.SystemUtils;

import java.util.List;

import static com.icourt.alpha.R.id.file_img;
import static com.icourt.alpha.constants.Const.MSG_TYPE_ALPHA;
import static com.icourt.alpha.constants.Const.MSG_TYPE_AT;
import static com.icourt.alpha.constants.Const.MSG_TYPE_DING;
import static com.icourt.alpha.constants.Const.MSG_TYPE_FILE;
import static com.icourt.alpha.constants.Const.MSG_TYPE_IMAGE;
import static com.icourt.alpha.constants.Const.MSG_TYPE_LINK;
import static com.icourt.alpha.constants.Const.MSG_TYPE_SYS;
import static com.icourt.alpha.constants.Const.MSG_TYPE_TXT;
import static com.icourt.alpha.constants.Const.MSG_TYPE_VOICE;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/17
 * version 1.0.0
 */
public class ImUserMessageDetailAdapter extends BaseArrayRecyclerAdapter<IMMessageCustomBody> implements BaseRecyclerAdapter.OnItemChildClickListener, BaseRecyclerAdapter.OnItemClickListener {
    private static final int VIEW_TYPE_TEXT = 0;
    private static final int VIEW_TYPE_FILE = 1;
    private static final int VIEW_TYPE_FILE_IMG = 2;
    private static final int VIEW_TYPE_DING = 3;
    private static final int VIEW_TYPE_AT = 4;
    private static final int VIEW_TYPE_SYS = 5;
    private static final int VIEW_TYPE_LINK = 6;


    private List<GroupContactBean> groupContactBeans;

    public ImUserMessageDetailAdapter(@NonNull List<GroupContactBean> contactBeanList) {
        this.setOnItemChildClickListener(this);
        this.setOnItemClickListener(this);
        this.groupContactBeans = contactBeanList;
    }

    @Nullable
    @CheckResult
    private GroupContactBean getUser(String accid) {
        if (groupContactBeans != null && !TextUtils.isEmpty(accid)) {
            GroupContactBean groupContactBean = new GroupContactBean();
            groupContactBean.accid = accid.toLowerCase();
            int indexOf = groupContactBeans.indexOf(groupContactBean);
            if (indexOf >= 0) {
                return groupContactBeans.get(indexOf);
            }
        }
        return null;
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
            case VIEW_TYPE_LINK:
                return R.layout.adapter_user_message_detail_link;
            default:
                return R.layout.adapter_user_message_detail_txt;
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
                    return VIEW_TYPE_FILE;
                case MSG_TYPE_IMAGE:
                    return VIEW_TYPE_FILE_IMG;
                case MSG_TYPE_DING:
                    return VIEW_TYPE_DING;
                case MSG_TYPE_AT:
                    return VIEW_TYPE_TEXT;
                case MSG_TYPE_SYS:
                    return VIEW_TYPE_TEXT;
                case MSG_TYPE_LINK:
                    return VIEW_TYPE_LINK;
                case MSG_TYPE_ALPHA:
                    return VIEW_TYPE_TEXT;
                case MSG_TYPE_VOICE:
                    return VIEW_TYPE_TEXT;
            }
        }
        return super.getItemViewType(position);
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

    /**
     * 初始化布局 普通文件
     *
     * @param holder
     * @param imMessageCustomBody
     */
    private void setViewFileCommFile(ViewHolder holder, IMMessageCustomBody imMessageCustomBody) {
        if (holder == null) return;
        if (imMessageCustomBody == null) return;
        if (imMessageCustomBody.ext == null) return;
        ImageView file_type_iv = holder.obtainView(R.id.file_type_iv);
        TextView file_title_tv = holder.obtainView(R.id.file_title_tv);
        TextView file_size_tv = holder.obtainView(R.id.file_size_tv);
        file_type_iv.setImageResource(getFileIcon40(imMessageCustomBody.ext.name));
        file_title_tv.setText(imMessageCustomBody.ext.name);
        file_size_tv.setText(FileUtils.kbFromat(imMessageCustomBody.ext.size));
    }


    /**
     * 初始化布局 图片
     */
    private void setViewTypeWithImg(ViewHolder holder, IMMessageCustomBody imMessageCustomBody) {
        ImageView file_img = holder.obtainView(R.id.file_img);
        holder.bindChildClick(file_img);
        if (imMessageCustomBody == null) return;
        if (imMessageCustomBody.ext == null) return;
        if (GlideUtils.canLoadImage(file_img.getContext())) {
            Glide.with(file_img.getContext())
                    .load(imMessageCustomBody.ext.thumb)
                    .placeholder(R.drawable.bg_round_rect_gray)
                    .into(file_img);
        }
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


    @Override
    public void onItemChildClick(BaseRecyclerAdapter adapter, ViewHolder holder, View view, int position) {
        switch (view.getId()) {
            case file_img:
                IMMessageCustomBody item = getItem(getRealPos(position));
                if (item != null && item.ext != null) {
                    ImagePagerActivity.launch(view.getContext(),
                            new String[]{item.ext.thumb});
                }
                break;
        }
    }


    @Override
    public void onBindHoder(ViewHolder holder, IMMessageCustomBody imMessageCustomBody, int position) {
        if (imMessageCustomBody == null) return;

        setCommUserInfo(holder, imMessageCustomBody, position);
        switch (holder.getItemViewType()) {
            case VIEW_TYPE_TEXT:
            case VIEW_TYPE_AT:
            case VIEW_TYPE_SYS:
            case VIEW_TYPE_DING:
                TextView item_text = holder.obtainView(R.id.item_text);
                item_text.setText(imMessageCustomBody.content);
                break;
            case VIEW_TYPE_FILE_IMG:
                setViewTypeWithImg(holder, imMessageCustomBody);
                break;
            case VIEW_TYPE_FILE:
                setViewFileCommFile(holder, imMessageCustomBody);
                break;
            case VIEW_TYPE_LINK:
                setViewLink(holder, imMessageCustomBody);
                break;
        }
    }

    private void setViewLink(ViewHolder holder, IMMessageCustomBody imMessageCustomBody) {
        if (imMessageCustomBody == null) return;
        if (holder == null) return;
        if (imMessageCustomBody == null) return;
        TextView msg_link_title_tv = holder.obtainView(R.id.msg_link_title_tv);
        ImageView msg_link_thumb_iv = holder.obtainView(R.id.msg_link_thumb_iv);
        TextView msg_link_url_tv = holder.obtainView(R.id.msg_link_url_tv);
        TextView msg_link_desc_tv = holder.obtainView(R.id.msg_link_desc_tv);
        if (imMessageCustomBody.ext != null) {
            if (!TextUtils.isEmpty(imMessageCustomBody.ext.title)) {
                msg_link_title_tv.setVisibility(View.VISIBLE);
                msg_link_title_tv.setText(imMessageCustomBody.ext.title);
            } else {
                msg_link_title_tv.setVisibility(View.GONE);
            }
            msg_link_url_tv.setText(imMessageCustomBody.ext.url);

            if (!TextUtils.isEmpty(imMessageCustomBody.ext.thumb)) {
                msg_link_thumb_iv.setVisibility(View.VISIBLE);
                if (GlideUtils.canLoadImage(msg_link_thumb_iv.getContext())) {
                    Glide.with(msg_link_thumb_iv.getContext())
                            .load(imMessageCustomBody.ext.thumb)
                            .into(msg_link_thumb_iv);
                }
            } else {
                msg_link_thumb_iv.setVisibility(View.GONE);
            }

            if (!TextUtils.isEmpty(imMessageCustomBody.ext.desc)) {
                msg_link_desc_tv.setVisibility(View.VISIBLE);
                msg_link_desc_tv.setText(imMessageCustomBody.ext.desc);
            } else {
                msg_link_desc_tv.setVisibility(View.GONE);
            }
        } else {
            msg_link_url_tv.setText("link ext null");
            msg_link_title_tv.setText("link ext null");
            msg_link_desc_tv.setText("link ext null");
        }
    }

    private void setCommUserInfo(ViewHolder holder, IMMessageCustomBody imMessageCustomBody, int position) {
        if (imMessageCustomBody == null) return;
        ImageView file_from_user_iv = holder.obtainView(R.id.file_from_user_iv);
        TextView file_from_user_tv = holder.obtainView(R.id.file_from_user_tv);
        TextView file_upload_time = holder.obtainView(R.id.file_upload_time);
        TextView file_from_tv = holder.obtainView(R.id.file_from_tv);

        GroupContactBean user = getUser(imMessageCustomBody.from);
        if (user != null) {
            GlideUtils.loadUser(file_from_user_iv.getContext(), user.pic, file_from_user_iv);
            file_from_user_tv.setText(user.name);

            String targetText = user.name;
            String originalText = String.format("来自: %s", user.name);
            SpannableUtils.setTextForegroundColorSpan(file_from_tv, originalText, targetText, SystemUtils.getColor(file_from_tv.getContext(), R.color.alpha_font_color_black));

        }
        file_upload_time.setText(DateUtils.getTimeShowString(imMessageCustomBody.send_time, true));
    }

    @Override
    public void onItemClick(BaseRecyclerAdapter adapter, ViewHolder holder, View view, int position) {
        IMMessageCustomBody item = getItem(adapter.getRealPos(position));
        if (item == null) return;
        switch (item.show_type) {
            case MSG_TYPE_LINK:
                if (item.ext != null) {
                    WebViewActivity.launch(view.getContext(), item.ext.url);
                }
                break;
        }
    }
}
