package com.icourt.alpha.adapter;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.icourt.alpha.BuildConfig;
import com.icourt.alpha.R;
import com.icourt.alpha.adapter.baseadapter.BaseArrayRecyclerAdapter;
import com.icourt.alpha.constants.Const;
import com.icourt.alpha.entity.bean.GroupContactBean;
import com.icourt.alpha.entity.bean.IMCustomerMessageEntity;
import com.icourt.alpha.utils.DateUtils;
import com.icourt.alpha.utils.GlideUtils;
import com.icourt.alpha.utils.IMUtils;
import com.icourt.alpha.view.recyclerviewDivider.ITimeDividerInterface;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.netease.nimlib.sdk.msg.constant.MsgDirectionEnum.In;
import static com.netease.nimlib.sdk.msg.constant.MsgDirectionEnum.Out;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/24
 * version 1.0.0
 */
public class ChatAdapter extends BaseArrayRecyclerAdapter<IMCustomerMessageEntity> implements ITimeDividerInterface {
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


    //右边布局 类型
    private static final int TYPE_RIGHT_TXT = 100;
    private static final int TYPE_RIGHT_IMAGE = 101;
    private static final int TYPE_RIGHT_FILE = 102;
    private static final int TYPE_RIGHT_DING_TXT = 103;
    private static final int TYPE_RIGHT_DING_IMAGE = 104;
    private static final int TYPE_RIGHT_DING_FILE = 105;

    private String loginToken;
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
        return null;
    }

    public ChatAdapter(String loginToken, List<GroupContactBean> contactBeanList) {
        this.loginToken = loginToken;
        this.contactBeanList = contactBeanList;
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
        }
        return R.layout.adapter_item_chat_left_txt;
    }

    @Override
    public int getItemViewType(int position) {
        IMCustomerMessageEntity item = getItem(position);
        if (item != null && item.imMessage != null && item.customIMBody != null) {
            if (item.imMessage.getDirect() == In) {
                switch (item.customIMBody.show_type) {
                    case Const.MSG_TYPE_TXT:
                        return TYPE_LEFT_TXT;
                    case Const.MSG_TYPE_FILE:
                        if (item.customIMBody.ext != null && IMUtils.isPIC(item.customIMBody.ext.name)) {
                            return TYPE_RIGHT_IMAGE;
                        } else {
                            return TYPE_RIGHT_FILE;
                        }
                    case Const.MSG_TYPE_DING:
                        //TODO 钉 即将细分 文本 文件图片
                        return TYPE_LEFT_DING_TXT;
                    case Const.MSG_TYPE_AT:
                        return TYPE_LEFT_TXT;
                    case Const.MSG_TYPE_SYS:
                        return TYPE_LEFT_TXT;
                    case Const.MSG_TYPE_LINK:
                        return TYPE_LEFT_TXT;
                }
            } else if (item.imMessage.getDirect() == Out) {
                switch (item.customIMBody.show_type) {
                    case Const.MSG_TYPE_TXT:
                        return TYPE_RIGHT_TXT;
                    case Const.MSG_TYPE_FILE:
                        if (item.customIMBody.ext != null && IMUtils.isPIC(item.customIMBody.ext.name)) {
                            return TYPE_RIGHT_IMAGE;
                        } else {
                            return TYPE_RIGHT_FILE;
                        }
                    case Const.MSG_TYPE_DING:
                        //TODO 钉 即将细分 文本 文件图片
                        return TYPE_RIGHT_DING_TXT;
                    case Const.MSG_TYPE_AT:
                        return TYPE_RIGHT_TXT;
                    case Const.MSG_TYPE_SYS:
                        return TYPE_RIGHT_TXT;
                    case Const.MSG_TYPE_LINK:
                        return TYPE_RIGHT_TXT;
                }
            }
        }
        return TYPE_LEFT_TXT;
    }

    @Override
    public void onBindHoder(ViewHolder holder, IMCustomerMessageEntity imMessage, int position) {
        if (imMessage == null) return;

        //分割时间段
        addTimeDividerArray(imMessage, position);

        //加载头像
        setCommonUserIcon(holder, imMessage, position);

        //处理不同类型的布局
        switch (holder.getItemViewType()) {
            case TYPE_LEFT_TXT:
                setTypeLeftTxt(holder, imMessage, position);
                break;
            case TYPE_LEFT_IMAGE:
                setTypeLeftImage(holder, imMessage, position);
                break;
            case TYPE_LEFT_FILE:
                setTypeLeftFile(holder, imMessage, position);
                break;
            case TYPE_LEFT_DING_TXT:
                setTypeLeftDingTxt(holder, imMessage, position);
                break;
            case TYPE_LEFT_DING_IMAGE:
                setTypeLeftDingImage(holder, imMessage, position);
                break;
            case TYPE_LEFT_DING_FILE:
                setTypeLeftDingFile(holder, imMessage, position);
                break;

            case TYPE_RIGHT_TXT:
                setTypeRightTxt(holder, imMessage, position);
                break;
            case TYPE_RIGHT_IMAGE:
                setTypeRightImage(holder, imMessage, position);
                break;
            case TYPE_RIGHT_FILE:
                setTypeRightFile(holder, imMessage, position);
                break;
            case TYPE_RIGHT_DING_TXT:
                setTypeRightDingTxt(holder, imMessage, position);
                break;
            case TYPE_RIGHT_DING_IMAGE:
                setTypeRightDingImage(holder, imMessage, position);
                break;
            case TYPE_RIGHT_DING_FILE:
                setTypeRightDingFile(holder, imMessage, position);
                break;
        }
    }

    /**
     * 处理时间分割线
     *
     * @param imMessage
     * @param position
     */
    private void addTimeDividerArray(IMCustomerMessageEntity imMessage, int position) {
        if (imMessage == null) return;
        if (imMessage.imMessage == null) return;

        //消息时间本身已经有序

        //用云信的时间
        if (timeShowArray.isEmpty()) {
            timeShowArray.add(imMessage.imMessage.getTime());
        } else {
            if (!timeShowArray.contains(imMessage.imMessage.getTime())) {
                if (imMessage.imMessage.getTime() - Collections.max(timeShowArray, longComparator).longValue() >= TIME_DIVIDER) {
                    timeShowArray.add(imMessage.imMessage.getTime());
                } else if (Collections.min(timeShowArray, longComparator).longValue() - imMessage.imMessage.getTime() >= TIME_DIVIDER) {
                    timeShowArray.add(imMessage.imMessage.getTime());
                }
            }
        }
    }


    /**
     * 设置头像 本地匹配头像
     *
     * @param holder
     * @param imMessage
     * @param position
     */
    private void setCommonUserIcon(ViewHolder holder, IMCustomerMessageEntity imMessage, int position) {
        ImageView chat_user_icon_iv = holder.obtainView(R.id.chat_user_icon_iv);
        if (chat_user_icon_iv != null && imMessage != null && imMessage.customIMBody != null) {
            String userHeadImg = getUserIcon(imMessage.customIMBody.from);
            GlideUtils
                    .loadUser(chat_user_icon_iv.getContext(),
                            TextUtils.isEmpty(userHeadImg) ? "" : userHeadImg,
                            chat_user_icon_iv);
        }
    }

    /**
     * 初始化左边 文本布局
     *
     * @param holder
     * @param imMessage
     * @param position
     */
    private void setTypeLeftTxt(ViewHolder holder, IMCustomerMessageEntity imMessage, int position) {
        TextView textView = holder.obtainView(R.id.chat_txt_tv);
        if (imMessage.customIMBody != null) {
            textView.setText(imMessage.customIMBody.content);
        } else {
            textView.setText("null");
        }
    }

    /**
     * 初始化左边 图片布局
     *
     * @param holder
     * @param imMessage
     * @param position
     */
    private void setTypeLeftImage(ViewHolder holder, IMCustomerMessageEntity imMessage, int position) {
        ImageView chat_image_iv = holder.obtainView(R.id.chat_image_iv);
        if (GlideUtils.canLoadImage(chat_image_iv.getContext())) {
            if (imMessage.customIMBody != null && imMessage.customIMBody.ext != null) {
                Glide.with(chat_image_iv.getContext())
                        .load(getFileUrl(imMessage.customIMBody.ext.path, 360))
                        .into(chat_image_iv);
            } else {
                //TODO 加载失败的图片
            }

        }
    }

    /**
     * 初始化左边 文件布局
     *
     * @param holder
     * @param imMessage
     * @param position
     */
    private void setTypeLeftFile(ViewHolder holder, IMCustomerMessageEntity imMessage, int position) {

    }

    /**
     * 初始化左边 钉的文本布局
     *
     * @param holder
     * @param imMessage
     * @param position
     */
    private void setTypeLeftDingTxt(ViewHolder holder, IMCustomerMessageEntity imMessage, int position) {
        if (imMessage == null) return;
        if (imMessage.customIMBody == null) return;
        TextView chat_ding_title = holder.obtainView(R.id.chat_ding_title);
        TextView chat_ding_content_tv = holder.obtainView(R.id.chat_ding_content_tv);
        ImageView chat_ding_source_user_icon_iv = holder.obtainView(R.id.chat_ding_source_user_icon_iv);
        String userHeadImg = getUserIcon(imMessage.customIMBody.ext != null ? imMessage.customIMBody.ext.from : "");
        GlideUtils
                .loadUser(chat_ding_source_user_icon_iv.getContext(),
                        TextUtils.isEmpty(userHeadImg) ? "" : userHeadImg,
                        chat_ding_source_user_icon_iv);
        TextView chat_ding_source_user_name_tv = holder.obtainView(R.id.chat_ding_source_user_name_tv);
        chat_ding_title.setText(TextUtils.isEmpty(imMessage.customIMBody.content) ? "钉了一条消息" : imMessage.customIMBody.content);
        if (imMessage.customIMBody.ext != null) {
            chat_ding_content_tv.setText(TextUtils.isEmpty(imMessage.customIMBody.ext.content) ? "文本消息" : imMessage.customIMBody.ext.content);
            chat_ding_source_user_name_tv.setText(imMessage.customIMBody.ext.name);
        } else {
            chat_ding_content_tv.setText("文本消息");
            chat_ding_source_user_name_tv.setText("好友");
        }
    }


    /**
     * 初始化左边 钉的图片布局
     *
     * @param holder
     * @param o
     * @param position
     */
    private void setTypeLeftDingImage(ViewHolder holder, Object o, int position) {

    }


    /**
     * 初始化左边 钉的文件布局
     *
     * @param holder
     * @param o
     * @param position
     */
    private void setTypeLeftDingFile(ViewHolder holder, Object o, int position) {

    }


    /**
     * 初始化右边 文本布局
     *
     * @param holder
     * @param customIMBody
     * @param position
     */
    private void setTypeRightTxt(ViewHolder holder, IMCustomerMessageEntity customIMBody, int position) {
        TextView textView = holder.obtainView(R.id.chat_txt_tv);
        if (customIMBody != null && customIMBody.customIMBody != null) {
            textView.setText(customIMBody.customIMBody.content);
        } else {
            textView.setText("null");
        }
    }

    /**
     * 初始化右边 图片布局
     *
     * @param holder
     * @param o
     * @param position
     */
    private void setTypeRightImage(ViewHolder holder, Object o, int position) {

    }

    /**
     * 初始化右边 文件布局
     *
     * @param holder
     * @param o
     * @param position
     */
    private void setTypeRightFile(ViewHolder holder, Object o, int position) {

    }

    /**
     * 初始化右边 钉的文本布局
     *
     * @param holder
     * @param imMessage
     * @param position
     */
    private void setTypeRightDingTxt(ViewHolder holder, IMCustomerMessageEntity imMessage, int position) {
        if (imMessage == null) return;
        if (imMessage.customIMBody == null) return;
        TextView chat_ding_title = holder.obtainView(R.id.chat_ding_title);
        TextView chat_ding_content_tv = holder.obtainView(R.id.chat_ding_content_tv);
        ImageView chat_ding_source_user_icon_iv = holder.obtainView(R.id.chat_ding_source_user_icon_iv);
        String userHeadImg = getUserIcon(imMessage.customIMBody.ext != null ? imMessage.customIMBody.ext.from : "");
        GlideUtils
                .loadUser(chat_ding_source_user_icon_iv.getContext(),
                        TextUtils.isEmpty(userHeadImg) ? "" : userHeadImg,
                        chat_ding_source_user_icon_iv);
        TextView chat_ding_source_user_name_tv = holder.obtainView(R.id.chat_ding_source_user_name_tv);
        chat_ding_title.setText(TextUtils.isEmpty(imMessage.customIMBody.content) ? "钉了一条消息" : imMessage.customIMBody.content);
        if (imMessage.customIMBody.ext != null) {
            chat_ding_content_tv.setText(TextUtils.isEmpty(imMessage.customIMBody.ext.content) ? "文本消息" : imMessage.customIMBody.ext.content);
            chat_ding_source_user_name_tv.setText(imMessage.customIMBody.ext.name);
        } else {
            chat_ding_content_tv.setText("文本消息");
            chat_ding_source_user_name_tv.setText("好友");
        }
    }


    /**
     * 初始化右边 钉的图片布局
     *
     * @param holder
     * @param o
     * @param position
     */
    private void setTypeRightDingImage(ViewHolder holder, Object o, int position) {

    }


    /**
     * 初始化右边 钉的文件布局
     *
     * @param holder
     * @param o
     * @param position
     */
    private void setTypeRightDingFile(ViewHolder holder, Object o, int position) {

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
        IMCustomerMessageEntity item = getItem(pos);
        return item != null && item.imMessage != null && timeShowArray.contains(item.imMessage.getTime());
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
        IMCustomerMessageEntity item = getItem(pos);
        return item != null && item.imMessage != null ?
                DateUtils.getTimeShowString(item.imMessage.getTime(), true) : "null";
    }
}
