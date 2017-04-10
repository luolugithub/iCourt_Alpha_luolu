package com.icourt.alpha.widget.parser;

import com.icourt.alpha.constants.HelperNotifiConfig;
import com.icourt.alpha.entity.bean.CustomAttachment;
import com.icourt.alpha.entity.bean.MatterAttachment;
import com.icourt.alpha.entity.bean.TaskAttachment;
import com.icourt.alpha.utils.JsonUtils;
import com.netease.nimlib.sdk.msg.attachment.MsgAttachment;
import com.netease.nimlib.sdk.msg.attachment.MsgAttachmentParser;

import org.json.JSONObject;

/**
 * @author 创建人:lu.zhao
 *         <p>
 *         自定义消息的附件解析器
 * @data 创建时间:17/3/30
 */

public class CustomAttachParser implements MsgAttachmentParser {

    @Override
    public MsgAttachment parse(String attach) {
        CustomAttachment attachment = null;
        try {
            JSONObject jsonObject = JsonUtils.getJSONObject(attach);
            String object = JsonUtils.getString(jsonObject, "object");
            if (HelperNotifiConfig.TASK_OBJECT.equals(object)) {//任务
                attachment = new TaskAttachment();
            } else if (HelperNotifiConfig.MATTER_OBJECT.equals(object)) {//项目
                attachment = new MatterAttachment();
            } else if (HelperNotifiConfig.TGROUP_OBJECT.equals(object)) {//任务组

            }

            if (attachment != null) {
                attachment.fromJson(jsonObject);
            }
        } catch (Exception e) {

        }

        return attachment;
    }

    public static String packData(JSONObject data) {
        return data.toString();
    }
}
