package com.icourt.alpha.adapter;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.icourt.alpha.R;
import com.icourt.alpha.adapter.baseadapter.BaseArrayRecyclerAdapter;
import com.icourt.alpha.entity.bean.AlphaUserInfo;
import com.icourt.alpha.entity.bean.GroupContactBean;
import com.icourt.alpha.entity.bean.IMMessageCustomBody;
import com.icourt.alpha.utils.ActionConstants;
import com.icourt.alpha.utils.DateUtils;
import com.icourt.alpha.utils.FileUtils;
import com.icourt.alpha.utils.GlideUtils;
import com.icourt.alpha.utils.LoginInfoUtils;

import java.util.List;

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
 * Description 跟用户相关的适配器[我的文件消息 我收藏的消息]
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/17
 * version 1.0.0
 */
public class ImUserMessageAdapter extends BaseArrayRecyclerAdapter<IMMessageCustomBody> {
    private static final int VIEW_TYPE_TEXT = 0;
    private static final int VIEW_TYPE_FILE = 1;
    private static final int VIEW_TYPE_FILE_IMG = 2;
    private static final int VIEW_TYPE_DING = 3;
    private static final int VIEW_TYPE_AT = 4;
    private static final int VIEW_TYPE_SYS = 5;
    private static final int VIEW_TYPE_LINK = 6;

    private String loginToken;
    AlphaUserInfo alphaUserInfo;
    private List<GroupContactBean> contactBeanList;//本地联系人

    /**
     * 获取本地头像
     *
     * @param accid
     * @return
     */
    public String getUserIcon(String accid) {
        if (contactBeanList != null) {
            GroupContactBean groupContactBean = new GroupContactBean();
            groupContactBean.accid = accid;
            int indexOf = contactBeanList.indexOf(groupContactBean);
            if (indexOf >= 0) {
                groupContactBean = contactBeanList.get(indexOf);
                return groupContactBean.pic;
            }
        }
        return "";
    }

    public ImUserMessageAdapter(@NonNull List<GroupContactBean> contactBeanList) {
        alphaUserInfo = LoginInfoUtils.getLoginUserInfo();
        if (alphaUserInfo != null) {
            this.loginToken = alphaUserInfo.getToken();
        }
        this.contactBeanList = contactBeanList;
    }

    @Override
    public int bindView(int viewtype) {
        switch (viewtype) {
            case VIEW_TYPE_TEXT:
            case VIEW_TYPE_AT:
            case VIEW_TYPE_SYS:
            case VIEW_TYPE_DING:
                return R.layout.adapter_item_msg_text;
            case VIEW_TYPE_FILE_IMG:
                return R.layout.adapter_item_msg_img;
            case VIEW_TYPE_FILE:
                return R.layout.adapter_item_msg_file;
            case VIEW_TYPE_LINK:
                return R.layout.adapter_item_msg_link;
            default:
                return R.layout.adapter_item_msg_text;
        }
    }

    @Override
    public long getItemId(int position) {
        IMMessageCustomBody item = getItem(position);
        return item != null ? item.id : super.getItemId(position);
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
                    return VIEW_TYPE_AT;
                case MSG_TYPE_SYS:
                    return VIEW_TYPE_SYS;
                case MSG_TYPE_LINK:
                    return VIEW_TYPE_LINK;
                case MSG_TYPE_ALPHA:
                case MSG_TYPE_VOICE:
                    break;
            }
        }
        return super.getItemViewType(position);
    }


    @Override
    public void onBindHoder(ViewHolder holder, IMMessageCustomBody imFileEntity, int position) {
        if (imFileEntity == null) return;
        setCommUserInfo(holder, imFileEntity);

        switch (holder.getItemViewType()) {
            case VIEW_TYPE_TEXT:
            case VIEW_TYPE_AT:
            case VIEW_TYPE_DING:
                TextView item_text = holder.obtainView(R.id.item_text);
                item_text.setText(imFileEntity.content);
                break;
            case VIEW_TYPE_SYS:
                TextView item_text_sys = holder.obtainView(R.id.item_text);
                if (imFileEntity.ext != null) {
                    item_text_sys.setText(imFileEntity.ext.content);
                } else {
                    item_text_sys.setText("服务器 系统消息 ext null");
                }
                break;
            case VIEW_TYPE_FILE_IMG:
                setViewTypeWithImg(holder, imFileEntity);
                break;
            case VIEW_TYPE_FILE:
                setViewFileCommFile(holder, imFileEntity);
                break;
            case VIEW_TYPE_LINK:
                setViewLink(holder, imFileEntity);
                break;
        }
    }

    /**
     * 初始化link消息
     *
     * @param holder
     * @param imFileEntity
     */
    private void setViewLink(ViewHolder holder, IMMessageCustomBody imFileEntity) {
        if (holder == null) return;
        if (imFileEntity == null) return;
        TextView msg_link_title_tv = holder.obtainView(R.id.msg_link_title_tv);
        ImageView msg_link_thumb_iv = holder.obtainView(R.id.msg_link_thumb_iv);
        TextView msg_link_url_tv = holder.obtainView(R.id.msg_link_url_tv);
        TextView msg_link_desc_tv = holder.obtainView(R.id.msg_link_desc_tv);
        if (imFileEntity.ext != null) {
            if (!TextUtils.isEmpty(imFileEntity.ext.title)) {
                msg_link_title_tv.setVisibility(View.VISIBLE);
                msg_link_title_tv.setText(imFileEntity.ext.title);
            } else {
                msg_link_title_tv.setVisibility(View.GONE);
            }
            msg_link_url_tv.setText(imFileEntity.ext.url);

            if (!TextUtils.isEmpty(imFileEntity.ext.thumb)) {
                msg_link_thumb_iv.setVisibility(View.VISIBLE);
                if (GlideUtils.canLoadImage(msg_link_thumb_iv.getContext())) {
                    Glide.with(msg_link_thumb_iv.getContext())
                            .load(imFileEntity.ext.thumb)
                            .into(msg_link_thumb_iv);
                }
            } else {
                msg_link_thumb_iv.setVisibility(View.GONE);
            }

            if (!TextUtils.isEmpty(imFileEntity.ext.desc)) {
                msg_link_desc_tv.setVisibility(View.VISIBLE);
                msg_link_desc_tv.setText(imFileEntity.ext.desc);
            } else {
                msg_link_desc_tv.setVisibility(View.GONE);
            }
        } else {
            msg_link_url_tv.setText("link ext null");
            msg_link_title_tv.setText("link ext null");
            msg_link_desc_tv.setText("link ext null");
        }
    }

    /**
     * 初始化基本信息
     *
     * @param holder
     * @param imFileEntity
     */
    private void setCommUserInfo(ViewHolder holder, IMMessageCustomBody imFileEntity) {
        if (holder == null) return;
        if (imFileEntity == null) return;
        ImageView file_from_user_iv = holder.obtainView(R.id.file_from_user_iv);
        TextView file_from_user_tv = holder.obtainView(R.id.file_from_user_tv);
        TextView file_from_time_tv = holder.obtainView(R.id.file_from_time_tv);

        GlideUtils.loadUser(file_from_user_iv.getContext(),
                getUserIcon(imFileEntity.from),
                file_from_user_iv);
        file_from_user_tv.setText(imFileEntity.name);
        file_from_time_tv.setText(DateUtils.getTimeShowString(imFileEntity.send_time, true));
    }

    /**
     * 初始化布局 普通文件
     *
     * @param holder
     * @param imFileEntity
     */
    private void setViewFileCommFile(ViewHolder holder, IMMessageCustomBody imFileEntity) {
        if (holder == null) return;
        if (imFileEntity == null) return;
        ImageView file_type_iv = holder.obtainView(R.id.file_type_iv);
        TextView file_title_tv = holder.obtainView(R.id.file_title_tv);
        TextView file_size_tv = holder.obtainView(R.id.file_size_tv);
        if (imFileEntity.ext != null) {
            file_type_iv.setImageResource(getFileIcon40(imFileEntity.ext.name));
            file_title_tv.setText(imFileEntity.ext.name);
            file_size_tv.setText(FileUtils.bFormat(imFileEntity.ext.size));
        } else {
            file_title_tv.setText("服务器 file ext null");
        }
    }

    /**
     * 初始化布局 图片
     *
     * @param holder
     * @param imFileEntity
     */
    private void setViewTypeWithImg(ViewHolder holder, IMMessageCustomBody imFileEntity) {
        ImageView file_img = holder.obtainView(R.id.file_img);
        if (file_img == null) return;
        if (imFileEntity == null) return;
        if (GlideUtils.canLoadImage(file_img.getContext())) {
            String picUrl = "";
            if (imFileEntity.ext != null) {
                picUrl = imFileEntity.ext.thumb;
            }
            GlideUtils.loadPic(file_img.getContext(), picUrl, file_img);
        }
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

}
