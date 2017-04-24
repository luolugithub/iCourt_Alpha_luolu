package com.icourt.alpha.adapter;

import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.icourt.alpha.BuildConfig;
import com.icourt.alpha.R;
import com.icourt.alpha.adapter.baseadapter.BaseArrayRecyclerAdapter;
import com.icourt.alpha.constants.Const;
import com.icourt.alpha.entity.bean.IMCustomerMessageEntity;
import com.icourt.alpha.utils.GlideUtils;
import com.icourt.alpha.utils.IMUtils;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/24
 * version 1.0.0
 */
public class ChatAdapter extends BaseArrayRecyclerAdapter<IMCustomerMessageEntity> {

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

    public ChatAdapter(String loginToken) {
        this.loginToken = loginToken;
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
        if (item != null && item.customIMBody != null) {
            if (IMUtils.isPIC(item.customIMBody.file)) {
                return TYPE_LEFT_IMAGE;
            }
        }
        return TYPE_LEFT_TXT;
        // return super.getItemViewType(position);
    }

    @Override
    public void onBindHoder(ViewHolder holder, IMCustomerMessageEntity imMessage, int position) {
        if (imMessage == null) return;
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


    private void setCommonUserIcon(ViewHolder holder, IMCustomerMessageEntity imMessage, int position) {
        ImageView chat_user_icon_iv = holder.obtainView(R.id.chat_user_icon_iv);
        if (chat_user_icon_iv != null) {
            GlideUtils
                    .loadUser(chat_user_icon_iv.getContext(),
                            imMessage.customIMBody != null ? imMessage.customIMBody.pic : "",
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
            textView.setText("" + imMessage.customIMBody.content);
        } else {
            textView.setText("" + position);
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
            Glide.with(chat_image_iv.getContext())
                    .load(imMessage.customIMBody != null ? getFileUrl(imMessage.customIMBody.path, 360) : "")
                    .into(chat_image_iv);
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
     * @param o
     * @param position
     */
    private void setTypeLeftDingTxt(ViewHolder holder, Object o, int position) {

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
     * @param o
     * @param position
     */
    private void setTypeRightTxt(ViewHolder holder, Object o, int position) {

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
     * @param o
     * @param position
     */
    private void setTypeRightDingTxt(ViewHolder holder, Object o, int position) {

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
}
