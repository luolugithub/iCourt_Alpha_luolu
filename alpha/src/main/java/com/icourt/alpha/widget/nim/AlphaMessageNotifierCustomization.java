package com.icourt.alpha.widget.nim;

import android.app.Application;

import com.icourt.alpha.R;
import com.icourt.alpha.entity.bean.HelperNotification;
import com.icourt.alpha.entity.bean.IMBodyBean;
import com.icourt.alpha.utils.ActionConstants;
import com.icourt.alpha.widget.parser.HelperNotificationParser;
import com.icourt.alpha.widget.parser.IMBodyParser;
import com.netease.nimlib.sdk.msg.MessageNotifierCustomization;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/10
 * version 1.0.0
 */
public class AlphaMessageNotifierCustomization implements MessageNotifierCustomization {
    private Application application;

    public AlphaMessageNotifierCustomization(Application application) {
        this.application = application;
    }

    public String getMsgContent(String nick, IMMessage message) {
        String content = application.getString(R.string.receive_one_message);
        if (message.getAttachment() != null) {
            HelperNotification helperNotification = HelperNotificationParser.getHelperNotification(message.getAttachment().toJson(false));
            if (helperNotification != null) {
                content = helperNotification.getContent();
            }
        } else {
            IMBodyBean imBodyBean = IMBodyParser.getIMBodyBean(message.getContent());
            if (imBodyBean != null) {
                switch (imBodyBean.getShow_type()) {
                    case ActionConstants.IM_MESSAGE_TEXT_SHOWTYPE:
                        if (message.getSessionType() == SessionTypeEnum.P2P)
                            content = imBodyBean.getContent();
                        else
                            content = nick + ":" + imBodyBean.getContent();
                        break;
                    case ActionConstants.IM_MESSAGE_FILE_SHOWTYPE:
                        if (message.getSessionType() == SessionTypeEnum.P2P)
                            content = application.getString(R.string.receive_one_file_message);
                        else
                            content = nick + ":" + application.getString(R.string.receive_one_file_message);
                        break;
                    case ActionConstants.IM_MESSAGE_PIN_SHOWTYPE:
                        if (imBodyBean.getPinMsg() != null) {
                            if ("0".equals(imBodyBean.getPinMsg().getIsPining())) {
                                if (message.getSessionType() == SessionTypeEnum.P2P)
                                    content = application.getString(R.string.message_cancle_ding_one_msg_text);
                                else
                                    content = nick + ":" + application.getString(R.string.message_cancle_ding_one_msg_text);
                            } else if ("1".equals(imBodyBean.getPinMsg().getIsPining())) {
                                if (message.getSessionType() == SessionTypeEnum.P2P)
                                    content = application.getString(R.string.message_ding_one_msg_text);
                                else
                                    content = nick + ":" + application.getString(R.string.message_ding_one_msg_text);
                            }
                        }
                        break;
                    case ActionConstants.IM_MESSAGE_AT_SHOWTYPE:
                        if (message.getSessionType() == SessionTypeEnum.Team)
                            content = nick + ":" + application.getString(R.string.message_have_at_me_text);
                        break;
                }
            }
        }
        return content;
    }

    @Override
    public String makeNotifyContent(String nick, IMMessage message) {

        return getMsgContent(nick, message); // 采用SDK默认文案
    }

    @Override
    public String makeTicker(String nick, IMMessage message) {
        return getMsgContent(nick, message); // 采用SDK默认文案
    }
}
