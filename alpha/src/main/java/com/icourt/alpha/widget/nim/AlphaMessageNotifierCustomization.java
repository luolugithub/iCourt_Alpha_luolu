package com.icourt.alpha.widget.nim;

import android.text.TextUtils;

import com.google.gson.JsonParseException;
import com.icourt.alpha.entity.bean.IMMessageCustomBody;
import com.icourt.alpha.utils.IMUtils;
import com.icourt.alpha.utils.JsonUtils;
import com.icourt.alpha.utils.LogUtils;
import com.icourt.alpha.utils.LoginInfoUtils;
import com.icourt.alpha.utils.StringUtils;
import com.netease.nimlib.sdk.msg.MessageNotifierCustomization;
import com.netease.nimlib.sdk.msg.constant.MsgTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;

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
 * Description  享聊推送提示文案
 * <p>
 * （ SDK 1.8.0 及以上版本支持）本地定制的通知栏提醒文案，目前支持配置Ticker文案（通知栏弹框条显示内容）和通知内容文案（下拉通知栏显示的通知内容）
 * http://dev.netease.im/docs/product/IM%E5%8D%B3%E6%97%B6%E9%80%9A%E8%AE%AF/SDK%E5%BC%80%E5%8F%91%E9%9B%86%E6%88%90/Android%E5%BC%80%E5%8F%91%E9%9B%86%E6%88%90?#推送
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/10
 * version 1.0.0
 */
public class AlphaMessageNotifierCustomization
        implements MessageNotifierCustomization {

    /**
     * 组合文字 张三:图片
     *
     * @param nick
     * @param content
     * @return
     */
    private String getCombString(String nick, String content) {
        StringBuilder stringBuilder = new StringBuilder();
        if (!TextUtils.isEmpty(nick)) {
            stringBuilder.append(nick + ":");
        }
        if (!TextUtils.isEmpty(content)) {
            stringBuilder.append(content);
        } else {
            stringBuilder.append("您收到一条消息");
        }
        return stringBuilder.toString();
    }

    public String getMsgContent(String nick, IMMessage imMessage) {
        IMUtils.logIMMessage("----------->notify:\n" + nick, imMessage);
        if (imMessage.getMsgType() == MsgTypeEnum.text
                && imMessage.getAttachment() != null) {//机器人 alpha小助手
            IMMessageCustomBody imBody = getIMBody(imMessage.getAttachment().toJson(false));
            if (imBody != null) {
                return getCombString("Alpha小助手", imBody.content);
            }
        } else if (imMessage.getMsgType() == MsgTypeEnum.text) {
            IMMessageCustomBody imBody = getIMBody(imMessage.getContent());
            if (imBody != null) {
                switch (imBody.show_type) {
                    case MSG_TYPE_TXT:    //文本消息
                        return getCombString(imBody.name, imBody.content);
                    case MSG_TYPE_IMAGE:
                        return getCombString(imBody.name, "[ 图片 ]");
                    case MSG_TYPE_FILE:
                        return getCombString(imBody.name, "[ 文件 ]");
                    case MSG_TYPE_DING:   //钉消息
                        return getCombString(imBody.name, imBody.content);
                    case MSG_TYPE_AT://@消息
                        if (imBody.ext.is_all) {
                            return getCombString(imBody.name, "有人@了你");
                        } else if (imBody.ext.users != null
                                && StringUtils.containsIgnoreCase(imBody.ext.users, LoginInfoUtils.getLoginUserId())) {
                            return getCombString(imBody.name, "有人@了你");
                        } else {
                            return getCombString(imBody.name, imBody.content);
                        }
                    case MSG_TYPE_SYS:     //系统辅助消息
                        if (imBody.ext != null) {
                            return getCombString(imBody.name, imBody.ext.content);
                        } else {
                            return getCombString(imBody.name, imBody.content);
                        }
                    case MSG_TYPE_LINK://链接消息
                        if (imBody.ext != null) {
                            return getCombString(imBody.name, imBody.ext.url);
                        } else {
                            return getCombString(imBody.name, null);
                        }
                    case MSG_TYPE_ALPHA:   //alpha系统内业务消息 2.0.0暂时不处理
                    case MSG_TYPE_VOICE:
                }
            }
        }
        return getCombString(nick, null);
    }

    private final IMMessageCustomBody getIMBody(String content) {
        IMMessageCustomBody imBodyEntity = null;
        try {
            imBodyEntity = JsonUtils.Gson2Bean(content, IMMessageCustomBody.class);
        } catch (JsonParseException e) {
            e.printStackTrace();
        }
        return imBodyEntity;
    }

    @Override
    public String makeNotifyContent(String nick, IMMessage message) {
        String notifyContent = getMsgContent(nick, message);
        LogUtils.d("----------->makeNotifyContent notifyContent:" + notifyContent);
        return notifyContent; // 采用SDK默认文案
    }

    @Override
    public String makeTicker(String nick, IMMessage message) {
        String notifyContent = getMsgContent(nick, message);
        LogUtils.d("----------->makeTicker notifyContent:" + notifyContent);
        return notifyContent; // 采用SDK默认文案
    }
}
