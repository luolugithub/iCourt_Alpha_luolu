package com.icourt.alpha.adapter;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.icourt.alpha.BuildConfig;
import com.icourt.alpha.R;
import com.icourt.alpha.adapter.baseadapter.BaseArrayRecyclerAdapter;
import com.icourt.alpha.constants.Const;
import com.icourt.alpha.entity.bean.AlphaUserInfo;
import com.icourt.alpha.entity.bean.GroupContactBean;
import com.icourt.alpha.entity.bean.IMMessageCustomBody;
import com.icourt.alpha.utils.DateUtils;
import com.icourt.alpha.utils.GlideUtils;
import com.icourt.alpha.utils.LoginInfoUtils;
import com.icourt.alpha.utils.StringUtils;
import com.icourt.alpha.utils.transformations.FitHeightImgViewTarget;
import com.icourt.alpha.view.BubbleImageView;
import com.icourt.alpha.view.recyclerviewDivider.ITimeDividerInterface;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.icourt.alpha.constants.Const.MSG_TYPE_FILE;
import static com.icourt.alpha.constants.Const.MSG_TYPE_IMAGE;
import static com.icourt.alpha.constants.Const.MSG_TYPE_LINK;
import static com.icourt.alpha.constants.Const.MSG_TYPE_SYS;
import static com.icourt.alpha.constants.Const.MSG_TYPE_TXT;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/24
 * version 1.0.0
 */
public class ChatAdapter extends BaseArrayRecyclerAdapter<IMMessageCustomBody> implements ITimeDividerInterface {
    private Set<Long> timeShowArray = new HashSet<>();//时间分割线消息
    private final int TIME_DIVIDER = 5 * 60 * 1_000;
    private Comparator<Long> longComparator = new Comparator<Long>() {
        @Override
        public int compare(Long o1, Long o2) {
            if (o1 != null && o2 != null) {
                return o1.compareTo(o2);
            }
            return 0;
        }
    };

    //左边布局 类型
    private static final int TYPE_LEFT_TXT = 0;
    private static final int TYPE_LEFT_IMAGE = 1;
    private static final int TYPE_LEFT_FILE = 2;
    private static final int TYPE_LEFT_DING_TXT = 3;
    private static final int TYPE_LEFT_DING_IMAGE = 4;
    private static final int TYPE_LEFT_DING_FILE = 5;
    private static final int TYPE_LEFT_DING_LINK = 6;
    private static final int TYPE_LEFT_LINK = 7;


    //右边布局 类型
    private static final int TYPE_RIGHT_TXT = 100;
    private static final int TYPE_RIGHT_IMAGE = 101;
    private static final int TYPE_RIGHT_FILE = 102;
    private static final int TYPE_RIGHT_DING_TXT = 103;
    private static final int TYPE_RIGHT_DING_IMAGE = 104;
    private static final int TYPE_RIGHT_DING_FILE = 105;
    private static final int TYPE_RIGHT_DING_LINK = 106;
    private static final int TYPE_RIGHT_LINK = 107;


    private static final int TYPE_CENTER_SYS = 200;


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

    public ChatAdapter(List<GroupContactBean> contactBeanList) {
        this.contactBeanList = contactBeanList;
        alphaUserInfo = LoginInfoUtils.getLoginUserInfo();
        if (alphaUserInfo != null) {
            this.loginToken = alphaUserInfo.getToken();
        }
    }

    private String getLoginUid() {
        return alphaUserInfo == null ? null : alphaUserInfo.getUserId();
    }

    @Override
    public int bindView(int viewtype) {
        switch (viewtype) {
            case TYPE_LEFT_TXT:
                return R.layout.adapter_item_chat_left_txt;
            case TYPE_LEFT_IMAGE:
                return R.layout.adapter_item_chat_left_image;
            case TYPE_LEFT_FILE:
                return R.layout.adapter_item_chat_left_file;

            case TYPE_LEFT_DING_TXT:
                return R.layout.adapter_item_chat_left_ding_txt;
            case TYPE_LEFT_DING_IMAGE:
                return R.layout.adapter_item_chat_left_ding_image;
            case TYPE_LEFT_DING_FILE:
                return R.layout.adapter_item_chat_left_ding_file;
            case TYPE_LEFT_DING_LINK:
                return R.layout.adapter_item_chat_left_ding_link;

            case TYPE_LEFT_LINK:
                return R.layout.adapter_item_chat_left_link;

            case TYPE_RIGHT_TXT:
                return R.layout.adapter_item_chat_right_txt;
            case TYPE_RIGHT_IMAGE:
                return R.layout.adapter_item_chat_right_image;
            case TYPE_RIGHT_FILE:
                return R.layout.adapter_item_chat_right_file;

            case TYPE_RIGHT_DING_TXT:
                return R.layout.adapter_item_chat_right_ding_txt;
            case TYPE_RIGHT_DING_IMAGE:
                return R.layout.adapter_item_chat_right_ding_image;
            case TYPE_RIGHT_DING_FILE:
                return R.layout.adapter_item_chat_right_ding_file;
            case TYPE_RIGHT_DING_LINK:
                return R.layout.adapter_item_chat_right_ding_link;

            case TYPE_RIGHT_LINK:
                return R.layout.adapter_item_chat_right_link;

            case TYPE_CENTER_SYS:
                return R.layout.adapter_item_chat_sys;
        }
        return R.layout.adapter_item_chat_left_txt;
    }

    /**
     * 是否是发出的消息
     *
     * @param from
     * @return
     */
    private boolean isSendMsg(String from) {
        return StringUtils.equalsIgnoreCase(from, getLoginUid(), false);
    }

    @Override
    public int getItemViewType(int position) {
        IMMessageCustomBody item = getItem(position);
        if (item != null) {
            //系统消息
            if (item.show_type == MSG_TYPE_SYS) {
                return TYPE_CENTER_SYS;
            } else if (!isSendMsg(item.from)) {
                switch (item.show_type) {
                    case Const.MSG_TYPE_TXT:
                        return TYPE_LEFT_TXT;
                    case Const.MSG_TYPE_FILE:
                        return TYPE_RIGHT_FILE;
                    case Const.MSG_TYPE_DING:
                        if (item.ext != null) {
                            //钉细分 文本 文件图片 链接
                            switch (item.ext.show_type) {
                                case MSG_TYPE_TXT:
                                    return TYPE_LEFT_DING_TXT;
                                case MSG_TYPE_FILE:
                                    return TYPE_LEFT_DING_FILE;
                                case MSG_TYPE_IMAGE:
                                    return TYPE_LEFT_DING_IMAGE;
                                case MSG_TYPE_LINK:
                                    return TYPE_LEFT_DING_LINK;
                            }
                        }
                        return TYPE_LEFT_DING_TXT;
                    case Const.MSG_TYPE_AT:
                        return TYPE_LEFT_TXT;
                    case MSG_TYPE_SYS:
                        return TYPE_LEFT_TXT;
                    case Const.MSG_TYPE_LINK:
                        return TYPE_LEFT_LINK;
                    case Const.MSG_TYPE_IMAGE:
                        return TYPE_LEFT_IMAGE;
                }
            } else {
                switch (item.show_type) {
                    case Const.MSG_TYPE_TXT:
                        return TYPE_RIGHT_TXT;
                    case Const.MSG_TYPE_FILE:
                        return TYPE_RIGHT_FILE;
                    case Const.MSG_TYPE_DING:
                        if (item.ext != null) {
                            //钉细分 文本 文件图片 链接
                            switch (item.ext.show_type) {
                                case MSG_TYPE_TXT:
                                    return TYPE_RIGHT_DING_TXT;
                                case MSG_TYPE_FILE:
                                    return TYPE_RIGHT_DING_FILE;
                                case MSG_TYPE_IMAGE:
                                    return TYPE_RIGHT_DING_IMAGE;
                                case MSG_TYPE_LINK:
                                    return TYPE_RIGHT_DING_LINK;
                            }
                        }
                        return TYPE_RIGHT_DING_TXT;
                    case Const.MSG_TYPE_AT:
                        return TYPE_RIGHT_TXT;
                    case MSG_TYPE_SYS:
                        return TYPE_RIGHT_TXT;
                    case Const.MSG_TYPE_LINK:
                        return TYPE_RIGHT_LINK;
                    case Const.MSG_TYPE_IMAGE:
                        return TYPE_RIGHT_IMAGE;
                }
            }
        }
        return TYPE_LEFT_TXT;
    }

    @Override
    public void onBindHoder(ViewHolder holder, IMMessageCustomBody imMessageCustomBody, int position) {
        if (imMessageCustomBody == null) return;

        //分割时间段
        addTimeDividerArray(imMessageCustomBody, position);

        //加载头像
        setCommonUserIcon(holder, imMessageCustomBody, position);

        //处理不同类型的布局
        switch (holder.getItemViewType()) {
            case TYPE_LEFT_TXT:
                setTypeLeftTxt(holder, imMessageCustomBody, position);
                break;
            case TYPE_LEFT_IMAGE:
                setTypeLeftImage(holder, imMessageCustomBody, position);
                break;
            case TYPE_LEFT_FILE:
                setTypeLeftFile(holder, imMessageCustomBody, position);
                break;
            case TYPE_LEFT_DING_TXT:
                setTypeLeftDingTxt(holder, imMessageCustomBody, position);
                break;
            case TYPE_LEFT_DING_IMAGE:
                setTypeLeftDingImage(holder, imMessageCustomBody, position);
                break;
            case TYPE_LEFT_DING_FILE:
                setTypeLeftDingFile(holder, imMessageCustomBody, position);
                break;
            case TYPE_LEFT_DING_LINK:
                setTypeLeftDingLink(holder, imMessageCustomBody, position);
                break;
            case TYPE_LEFT_LINK:
                setTypeLeftLink(holder, imMessageCustomBody, position);
                break;


            case TYPE_RIGHT_TXT:
                setTypeRightTxt(holder, imMessageCustomBody, position);
                break;
            case TYPE_RIGHT_IMAGE:
                setTypeRightImage(holder, imMessageCustomBody, position);
                break;
            case TYPE_RIGHT_FILE:
                setTypeRightFile(holder, imMessageCustomBody, position);
                break;
            case TYPE_RIGHT_DING_TXT:
                setTypeRightDingTxt(holder, imMessageCustomBody, position);
                break;
            case TYPE_RIGHT_DING_IMAGE:
                setTypeRightDingImage(holder, imMessageCustomBody, position);
                break;
            case TYPE_RIGHT_DING_FILE:
                setTypeRightDingFile(holder, imMessageCustomBody, position);
                break;
            case TYPE_RIGHT_DING_LINK:
                setTypeRightDingLink(holder, imMessageCustomBody, position);
                break;
            case TYPE_RIGHT_LINK:
                setTypeRightLink(holder, imMessageCustomBody, position);
                break;

            case TYPE_CENTER_SYS:
                setTypeCenterSys(holder, imMessageCustomBody, position);
                break;
        }
    }


    /**
     * 设置钉link 左边
     *
     * @param holder
     * @param imMessageCustomBody
     * @param position
     */
    private void setTypeLeftDingLink(ViewHolder holder, IMMessageCustomBody imMessageCustomBody, int position) {
        if (holder == null) return;
        if (imMessageCustomBody == null) return;
        TextView chat_ding_title_tv = holder.obtainView(R.id.chat_ding_title_tv);
        TextView chat_link_title_tv = holder.obtainView(R.id.chat_link_title_tv);
        ImageView chat_lin_thumb_iv = holder.obtainView(R.id.chat_lin_thumb_iv);
        TextView chat_link_url_tv = holder.obtainView(R.id.chat_link_url_tv);
        TextView chat_link_desc_tv = holder.obtainView(R.id.chat_link_desc_tv);
        chat_ding_title_tv.setText(TextUtils.isEmpty(imMessageCustomBody.content) ? "钉了一条消息" : imMessageCustomBody.content);
        if (imMessageCustomBody.ext != null
                && imMessageCustomBody.ext.ext != null) {
            if (!TextUtils.isEmpty(imMessageCustomBody.ext.ext.title)) {
                chat_link_title_tv.setVisibility(View.VISIBLE);
                chat_link_title_tv.setText(imMessageCustomBody.ext.ext.title);
            } else {
                chat_link_title_tv.setVisibility(View.GONE);
            }
            chat_link_url_tv.setText(imMessageCustomBody.ext.ext.url);

            if (!TextUtils.isEmpty(imMessageCustomBody.ext.ext.thumb)) {
                chat_lin_thumb_iv.setVisibility(View.VISIBLE);
                if (GlideUtils.canLoadImage(chat_lin_thumb_iv.getContext())) {
                    Glide.with(chat_lin_thumb_iv.getContext())
                            .load(imMessageCustomBody.ext.ext.thumb)
                            .into(chat_lin_thumb_iv);
                }
            } else {
                chat_lin_thumb_iv.setVisibility(View.GONE);
            }

            if (!TextUtils.isEmpty(imMessageCustomBody.ext.ext.desc)) {
                chat_link_desc_tv.setVisibility(View.VISIBLE);
                chat_link_desc_tv.setText(imMessageCustomBody.ext.ext.desc);
            } else {
                chat_link_desc_tv.setVisibility(View.GONE);
            }
        } else {
            chat_link_title_tv.setText("link ext null");
            chat_link_url_tv.setText("link ext null");
            chat_link_desc_tv.setText("link ext null");
        }
    }

    /**
     * 设置钉link 右边
     *
     * @param holder
     * @param imMessageCustomBody
     * @param position
     */
    private void setTypeRightDingLink(ViewHolder holder, IMMessageCustomBody imMessageCustomBody, int position) {
        setTypeLeftDingLink(holder, imMessageCustomBody, position);
    }

    /**
     * 设置系统消息
     *
     * @param holder
     * @param imMessageCustomBody
     * @param position
     */
    private void setTypeCenterSys(ViewHolder holder, IMMessageCustomBody imMessageCustomBody, int position) {
        if (holder == null) return;
        if (imMessageCustomBody == null) return;
        TextView chat_sys_tv = holder.obtainView(R.id.chat_sys_tv);
        if (imMessageCustomBody.ext != null) {
            chat_sys_tv.setText(imMessageCustomBody.ext.content);
        } else {
            chat_sys_tv.setText("系统消息ext null");
        }
    }


    /**
     * 处理时间分割线
     *
     * @param imMessageCustomBody
     * @param position
     */
    private void addTimeDividerArray(IMMessageCustomBody imMessageCustomBody, int position) {
        if (imMessageCustomBody == null) return;

        //消息时间本身已经有序

        if (timeShowArray.isEmpty()) {
            timeShowArray.add(imMessageCustomBody.send_time);
        } else {
            if (!timeShowArray.contains(imMessageCustomBody.send_time)) {
                if (imMessageCustomBody.send_time - Collections.max(timeShowArray, longComparator).longValue() >= TIME_DIVIDER) {
                    timeShowArray.add(imMessageCustomBody.send_time);
                } else if (Collections.min(timeShowArray, longComparator).longValue() - imMessageCustomBody.send_time >= TIME_DIVIDER) {
                    timeShowArray.add(imMessageCustomBody.send_time);
                }
            }
        }
    }


    /**
     * 设置头像 本地匹配头像
     *
     * @param holder
     * @param imMessageCustomBody
     * @param position
     */
    private void setCommonUserIcon(ViewHolder holder, IMMessageCustomBody imMessageCustomBody, int position) {
        ImageView chat_user_icon_iv = holder.obtainView(R.id.chat_user_icon_iv);
        if (chat_user_icon_iv != null && imMessageCustomBody != null) {
            String userHeadImg = getUserIcon(imMessageCustomBody.from);
            GlideUtils
                    .loadUser(chat_user_icon_iv.getContext(),
                            TextUtils.isEmpty(userHeadImg) ? "" : userHeadImg,
                            chat_user_icon_iv);
            holder.bindChildClick(chat_user_icon_iv);
        }
    }

    /**
     * 初始化左边 文本布局
     *
     * @param holder
     * @param imMessageCustomBody
     * @param position
     */
    private void setTypeLeftTxt(ViewHolder holder, IMMessageCustomBody imMessageCustomBody, int position) {
        TextView textView = holder.obtainView(R.id.chat_txt_tv);
        if (imMessageCustomBody == null) return;
        textView.setText(imMessageCustomBody.content);
    }

    /**
     * 初始化左边 图片布局
     *
     * @param holder
     * @param imMessageCustomBody
     * @param position
     */
    private void setTypeLeftImage(ViewHolder holder, IMMessageCustomBody imMessageCustomBody, int position) {
        BubbleImageView chat_image_iv = holder.obtainView(R.id.chat_image_iv);
        if (imMessageCustomBody == null) return;
        if (GlideUtils.canLoadImage(chat_image_iv.getContext())) {
            String picUrl = "";
            if (imMessageCustomBody.ext != null) {
                picUrl = imMessageCustomBody.ext.thumb;
            }
            GlideUtils.loadPic(chat_image_iv.getContext(), picUrl, chat_image_iv);
        }
    }

    /**
     * 初始化左边 文件布局
     *
     * @param holder
     * @param imMessageCustomBody
     * @param position
     */
    private void setTypeLeftFile(ViewHolder holder, IMMessageCustomBody imMessageCustomBody, int position) {

    }

    /**
     * 初始化左边 钉的文本布局
     *
     * @param holder
     * @param imMessageCustomBody
     * @param position
     */
    private void setTypeLeftDingTxt(ViewHolder holder, IMMessageCustomBody imMessageCustomBody, int position) {
        if (imMessageCustomBody == null) return;
        TextView chat_ding_title = holder.obtainView(R.id.chat_ding_title);
        TextView chat_ding_content_tv = holder.obtainView(R.id.chat_ding_content_tv);
        ImageView chat_ding_source_user_icon_iv = holder.obtainView(R.id.chat_ding_source_user_icon_iv);
        String userHeadImg = getUserIcon(imMessageCustomBody.ext != null ? imMessageCustomBody.ext.from : "");
        GlideUtils
                .loadUser(chat_ding_source_user_icon_iv.getContext(),
                        TextUtils.isEmpty(userHeadImg) ? "" : userHeadImg,
                        chat_ding_source_user_icon_iv);
        TextView chat_ding_source_user_name_tv = holder.obtainView(R.id.chat_ding_source_user_name_tv);
        chat_ding_title.setText(TextUtils.isEmpty(imMessageCustomBody.content) ? "钉了一条消息" : imMessageCustomBody.content);
        if (imMessageCustomBody.ext != null) {
            chat_ding_content_tv.setText(TextUtils.isEmpty(imMessageCustomBody.ext.content) ? "文本消息" : imMessageCustomBody.ext.content);
            chat_ding_source_user_name_tv.setText(imMessageCustomBody.ext.name);
        } else {
            chat_ding_content_tv.setText("文本消息");
            chat_ding_source_user_name_tv.setText("好友");
        }
    }


    /**
     * 初始化左边 钉的图片布局
     *
     * @param holder
     * @param imMessageCustomBody
     * @param position
     */
    private void setTypeLeftDingImage(ViewHolder holder, IMMessageCustomBody imMessageCustomBody, int position) {
        if (holder == null) return;
        if (imMessageCustomBody == null) return;
        TextView chat_ding_title_tv = holder.obtainView(R.id.chat_ding_title_tv);
        ImageView chat_ding_content_iamge_iv = holder.obtainView(R.id.chat_ding_content_iamge_iv);
        ImageView chat_ding_source_user_icon_iv = holder.obtainView(R.id.chat_ding_source_user_icon_iv);
        TextView chat_ding_source_user_name_tv = holder.obtainView(R.id.chat_ding_source_user_name_tv);
        chat_ding_title_tv.setText(imMessageCustomBody.content);
        if (imMessageCustomBody.ext != null) {
            if (GlideUtils.canLoadImage(chat_ding_content_iamge_iv.getContext())) {
                GlideUtils.loadPic(chat_ding_source_user_icon_iv.getContext(), imMessageCustomBody.ext.thumb, chat_ding_content_iamge_iv);
            }
            chat_ding_source_user_name_tv.setText(imMessageCustomBody.ext.from);
        }
    }


    /**
     * 初始化左边 钉的文件布局
     *
     * @param holder
     * @param imMessageCustomBody
     * @param position
     */
    private void setTypeLeftDingFile(ViewHolder holder, IMMessageCustomBody imMessageCustomBody, int position) {

    }

    /**
     * 初始化左边 链接布局
     *
     * @param holder
     * @param imMessageCustomBody
     * @param position
     */
    private void setTypeLeftLink(ViewHolder holder, IMMessageCustomBody imMessageCustomBody, int position) {
        if (holder == null) return;
        if (imMessageCustomBody == null) return;
        if (imMessageCustomBody.ext == null) return;
        TextView chat_link_title_tv = holder.obtainView(R.id.chat_link_title_tv);
        ImageView chat_lin_thumb_iv = holder.obtainView(R.id.chat_lin_thumb_iv);
        TextView chat_link_url_tv = holder.obtainView(R.id.chat_link_url_tv);
        TextView chat_link_desc_tv = holder.obtainView(R.id.chat_link_desc_tv);
        String thumb = imMessageCustomBody.ext.thumb;
        if (!TextUtils.isEmpty(thumb) && thumb.startsWith("http")) {
            chat_lin_thumb_iv.setVisibility(View.VISIBLE);
            Glide.with(chat_lin_thumb_iv.getContext())
                    .load(thumb)
                    .into(chat_lin_thumb_iv);
        } else {
            chat_lin_thumb_iv.setVisibility(View.GONE);
        }
        chat_link_title_tv.setText(imMessageCustomBody.ext.title);
        chat_link_title_tv.setVisibility(TextUtils.isEmpty(imMessageCustomBody.ext.title) ? View.GONE : View.VISIBLE);

        chat_link_url_tv.setText(imMessageCustomBody.ext.url);
        chat_link_desc_tv.setText(imMessageCustomBody.ext.desc);
    }

    /**
     * 初始化右边 文本布局
     *
     * @param holder
     * @param imMessageCustomBody
     * @param position
     */
    private void setTypeRightTxt(ViewHolder holder, IMMessageCustomBody imMessageCustomBody, int position) {
        TextView textView = holder.obtainView(R.id.chat_txt_tv);
        if (imMessageCustomBody != null) {
            textView.setText(imMessageCustomBody.content);
        } else {
            textView.setText("null");
        }
    }

    /**
     * 初始化右边 图片布局
     *
     * @param holder
     * @param imMessageCustomBody
     * @param position
     */
    private void setTypeRightImage(ViewHolder holder, IMMessageCustomBody imMessageCustomBody, int position) {
        BubbleImageView chat_image_iv = holder.obtainView(R.id.chat_image_iv);
        if (imMessageCustomBody == null) return;
        holder.bindChildClick(chat_image_iv);
        holder.bindChildLongClick(chat_image_iv);
        if (GlideUtils.canLoadImage(chat_image_iv.getContext())) {
            String picUrl = "";
            if (imMessageCustomBody.ext != null) {
                picUrl = imMessageCustomBody.ext.thumb;
            }
            Glide.with(chat_image_iv.getContext())
                    .load(picUrl)
                    .asBitmap()
                    .into(new FitHeightImgViewTarget(chat_image_iv));
            // GlideUtils.loadPic(chat_image_iv.getContext(), picUrl, chat_image_iv);
        }
    }

    /**
     * 初始化右边 文件布局
     *
     * @param holder
     * @param imMessageCustomBody
     * @param position
     */
    private void setTypeRightFile(ViewHolder holder, IMMessageCustomBody imMessageCustomBody, int position) {

    }

    /**
     * 初始化右边 钉的文本布局
     *
     * @param holder
     * @param imMessageCustomBody
     * @param position
     */
    private void setTypeRightDingTxt(ViewHolder holder, IMMessageCustomBody imMessageCustomBody, int position) {
        if (imMessageCustomBody == null) return;
        TextView chat_ding_title = holder.obtainView(R.id.chat_ding_title);
        TextView chat_ding_content_tv = holder.obtainView(R.id.chat_ding_content_tv);
        ImageView chat_ding_source_user_icon_iv = holder.obtainView(R.id.chat_ding_source_user_icon_iv);
        String userHeadImg = getUserIcon(imMessageCustomBody.ext != null ? imMessageCustomBody.ext.from : "");
        GlideUtils
                .loadUser(chat_ding_source_user_icon_iv.getContext(),
                        TextUtils.isEmpty(userHeadImg) ? "" : userHeadImg,
                        chat_ding_source_user_icon_iv);
        TextView chat_ding_source_user_name_tv = holder.obtainView(R.id.chat_ding_source_user_name_tv);
        chat_ding_title.setText(TextUtils.isEmpty(imMessageCustomBody.content) ? "钉了一条消息" : imMessageCustomBody.content);
        if (imMessageCustomBody.ext != null) {
            chat_ding_content_tv.setText(TextUtils.isEmpty(imMessageCustomBody.ext.content) ? "文本消息" : imMessageCustomBody.ext.content);
            chat_ding_source_user_name_tv.setText(imMessageCustomBody.ext.name);
        } else {
            chat_ding_content_tv.setText("文本消息");
            chat_ding_source_user_name_tv.setText("好友");
        }
    }


    /**
     * 初始化右边 钉的图片布局
     *
     * @param holder
     * @param imMessageCustomBody
     * @param position
     */
    private void setTypeRightDingImage(ViewHolder holder, IMMessageCustomBody imMessageCustomBody, int position) {
        if (holder == null) return;
        if (imMessageCustomBody == null) return;
        TextView chat_ding_title_tv = holder.obtainView(R.id.chat_ding_title_tv);
        ImageView chat_ding_content_iamge_iv = holder.obtainView(R.id.chat_ding_content_iamge_iv);
        ImageView chat_ding_source_user_icon_iv = holder.obtainView(R.id.chat_ding_source_user_icon_iv);
        TextView chat_ding_source_user_name_tv = holder.obtainView(R.id.chat_ding_source_user_name_tv);
        chat_ding_title_tv.setText(imMessageCustomBody.content);
        if (imMessageCustomBody.ext.ext != null) {
            if (GlideUtils.canLoadImage(chat_ding_content_iamge_iv.getContext())) {
                GlideUtils.loadPic(chat_ding_content_iamge_iv.getContext(), imMessageCustomBody.ext.ext.thumb, chat_ding_content_iamge_iv);
                GlideUtils.loadUser(chat_ding_source_user_icon_iv.getContext(), getUserIcon(imMessageCustomBody.ext.from), chat_ding_source_user_icon_iv);
            }
            chat_ding_source_user_name_tv.setText(imMessageCustomBody.ext.name);
        } else {
            chat_ding_source_user_name_tv.setText("ext ext null");
        }
    }


    /**
     * 初始化右边 钉的文件布局
     *
     * @param holder
     * @param imMessageCustomBody
     * @param position
     */
    private void setTypeRightDingFile(ViewHolder holder, IMMessageCustomBody imMessageCustomBody, int position) {

    }

    /**
     * 初始化 右边链接布局
     *
     * @param holder
     * @param imMessageCustomBody
     * @param position
     */
    private void setTypeRightLink(ViewHolder holder, IMMessageCustomBody imMessageCustomBody, int position) {
        setTypeLeftLink(holder, imMessageCustomBody, position);
    }

    /**
     * 获取图片地址
     *
     * @param fileId
     * @param width
     */
    private String getFileUrl(String fileId, int width) {
        StringBuilder urlBuilder = new StringBuilder(BuildConfig.HOST_URL);
        urlBuilder.append(Const.HTTP_DOWNLOAD_FILE);
        urlBuilder.append("?sFileId=");
        urlBuilder.append(fileId);
        urlBuilder.append("&token=");
        urlBuilder.append(loginToken);
        urlBuilder.append("&width=");
        urlBuilder.append(width);
        return urlBuilder.toString();
    }


    /**
     * 是否显示时间 时间间隔5分钟
     *
     * @param pos
     * @return
     */
    @Override
    public boolean isShowTimeDivider(int pos) {
        IMMessageCustomBody item = getItem(pos);
        return item != null && timeShowArray.contains(item.send_time);
    }

    /**
     * 显示的时间字符串 isShowTimeDivider=true 不可以返回null
     *
     * @param pos
     * @return
     */
    @NonNull
    @Override
    public String getShowTime(int pos) {
        IMMessageCustomBody item = getItem(pos);
        return item != null ?
                DateUtils.getTimeShowString(item.send_time, true) : "null";
    }


}
