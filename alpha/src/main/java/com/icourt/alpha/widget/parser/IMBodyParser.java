package com.icourt.alpha.widget.parser;

import android.text.TextUtils;

import com.icourt.alpha.entity.bean.IMBodyBean;
import com.icourt.alpha.utils.ActionConstants;
import com.icourt.alpha.utils.JsonUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 创建人:lu.zhao
 * @data 创建时间:17/1/6
 */

public class IMBodyParser {

    public static IMBodyBean getIMBodyBean(String json) {
        IMBodyBean imBodyBean = null;
        if (!TextUtils.isEmpty(json) && json.length() > 0) {
            JSONObject jsonObject = JsonUtils.getJSONObject(json);
            if (jsonObject != null) {
                int showType = JsonUtils.getInt(jsonObject, "show_type");
                imBodyBean = new IMBodyBean();
                switch (showType) {
                    case ActionConstants.IM_MESSAGE_TEXT_SHOWTYPE://文本
                        if (JsonUtils.has(jsonObject, "content")) {
                            imBodyBean.setContent(JsonUtils.getString(jsonObject, "content"));
                        }
                        imBodyBean.setName(JsonUtils.getString(jsonObject, "name"));
                        imBodyBean.setPic(JsonUtils.getString(jsonObject, "pic"));
                        imBodyBean.setGroupId(JsonUtils.getString(jsonObject, "groupId"));
                        imBodyBean.setId(JsonUtils.getString(jsonObject, "id"));
                        imBodyBean.setTid(JsonUtils.getString(jsonObject, "tid"));
                        imBodyBean.setRoute(JsonUtils.getString(jsonObject, "route"));
                        break;
                    case ActionConstants.IM_MESSAGE_FILE_SHOWTYPE://文件
                        imBodyBean.setFileName(JsonUtils.getString(jsonObject, "file"));
                        imBodyBean.setFilePath(JsonUtils.getString(jsonObject, "path"));
                        imBodyBean.setFileSize(JsonUtils.getString(jsonObject, "size"));
                        imBodyBean.setTime(JsonUtils.getLong(jsonObject, "time"));
                        imBodyBean.setName(JsonUtils.getString(jsonObject, "name"));
                        imBodyBean.setPic(JsonUtils.getString(jsonObject, "pic"));
                        imBodyBean.setGroupId(JsonUtils.getString(jsonObject, "groupId"));
                        imBodyBean.setId(JsonUtils.getString(jsonObject, "id"));
                        imBodyBean.setTid(JsonUtils.getString(jsonObject, "tid"));
                        imBodyBean.setRoute(JsonUtils.getString(jsonObject, "route"));
                        break;
                    case ActionConstants.IM_MESSAGE_PIN_SHOWTYPE://钉
                        if (JsonUtils.has(jsonObject, "pinMsg")) {
                            JSONObject pinMsgObject = JsonUtils.getJSONObject(jsonObject, "pinMsg");
                            IMBodyBean.PinMsgBean pinMsgBean = new IMBodyBean.PinMsgBean();
                            int showPinType = JsonUtils.getInt(pinMsgObject, "show_type");
                            pinMsgBean.setShow_type(showPinType);
                            pinMsgBean.setIsPining(JsonUtils.getString(jsonObject, "isPining"));
                            pinMsgBean.setId(JsonUtils.getString(pinMsgObject, "id"));
                            pinMsgBean.setName(JsonUtils.getString(pinMsgObject, "name"));
                            pinMsgBean.setPic(JsonUtils.getString(pinMsgObject, "pic"));
                            switch (showPinType) {
                                case ActionConstants.IM_MESSAGE_TEXT_SHOWTYPE://文本
                                    if (JsonUtils.has(pinMsgObject, "content")) {
                                        pinMsgBean.setContent(JsonUtils.getString(pinMsgObject, "content"));
                                    }
                                    break;
                                case ActionConstants.IM_MESSAGE_FILE_SHOWTYPE://文件
                                    pinMsgBean.setFileName(JsonUtils.getString(pinMsgObject, "file"));
                                    pinMsgBean.setFilePath(JsonUtils.getString(pinMsgObject, "path"));
                                    pinMsgBean.setFileSize(JsonUtils.getString(pinMsgObject, "size"));
                                    break;
                                case ActionConstants.IM_MESSAGE_AT_SHOWTYPE://@
                                    pinMsgBean.setAtAll(JsonUtils.getInt(pinMsgObject, "atAll"));
                                    JSONArray atArr = JsonUtils.getJSONArray(pinMsgObject, "atList");
                                    pinMsgBean.setContent(JsonUtils.getString(pinMsgObject, "content"));
                                    if (atArr.length() > 0) {
                                        List<IMBodyBean.AtBean> atBeanList = new ArrayList<>();
                                        for (int i = 0; i < atArr.length(); i++) {
                                            IMBodyBean.AtBean atbean = new IMBodyBean.AtBean();
                                            JSONObject itemObject = JsonUtils.getJSONObject(atArr, i);
                                            atbean.setId(JsonUtils.getString(itemObject, "userId"));
                                            atbean.setName(JsonUtils.getString(itemObject, "name"));
                                            atBeanList.add(atbean);
                                        }
                                        pinMsgBean.setAtBeanList(atBeanList);
                                    }
                                    break;
                                case ActionConstants.IM_MESSAGE_SYSTEM_SHOWTYPE://通知
                                    pinMsgBean.setContent(JsonUtils.getString(pinMsgObject, "content"));
                                    break;
                            }
                            imBodyBean.setPinMsg(pinMsgBean);
                        }
                        imBodyBean.setName(JsonUtils.getString(jsonObject, "name"));
                        imBodyBean.setPic(JsonUtils.getString(jsonObject, "pic"));
                        imBodyBean.setGroupId(JsonUtils.getString(jsonObject, "groupId"));
                        imBodyBean.setId(JsonUtils.getString(jsonObject, "id"));
                        imBodyBean.setTid(JsonUtils.getString(jsonObject, "tid"));
                        imBodyBean.setRoute(JsonUtils.getString(jsonObject, "route"));
                        break;
                    case ActionConstants.IM_MESSAGE_AT_SHOWTYPE://@
                        imBodyBean.setAtAll(JsonUtils.getInt(jsonObject, "atAll"));
                        JSONArray atArr = JsonUtils.getJSONArray(jsonObject, "atList");
                        imBodyBean.setContent(JsonUtils.getString(jsonObject, "content"));
                        if (atArr.length() > 0) {
                            List<IMBodyBean.AtBean> atBeanList = new ArrayList<>();
                            for (int i = 0; i < atArr.length(); i++) {
                                IMBodyBean.AtBean atbean = new IMBodyBean.AtBean();
                                JSONObject itemObject = JsonUtils.getJSONObject(atArr, i);
                                atbean.setId(JsonUtils.getString(itemObject, "userId"));
                                atbean.setName(JsonUtils.getString(itemObject, "name"));
                                atBeanList.add(atbean);
                            }
                            imBodyBean.setAtBeanList(atBeanList);
                        }
                        imBodyBean.setName(JsonUtils.getString(jsonObject, "name"));
                        imBodyBean.setPic(JsonUtils.getString(jsonObject, "pic"));
                        imBodyBean.setGroupId(JsonUtils.getString(jsonObject, "groupId"));
                        imBodyBean.setId(JsonUtils.getString(jsonObject, "id"));
                        imBodyBean.setTid(JsonUtils.getString(jsonObject, "tid"));
                        imBodyBean.setRoute(JsonUtils.getString(jsonObject, "route"));
                        break;
                    case ActionConstants.IM_MESSAGE_NEW_GROUP_SHOWTYPE://web端新建讨论组头部信息
                        imBodyBean.setContent("创建了讨论组");
                        break;
                    case ActionConstants.IM_MESSAGE_SYSTEM_SHOWTYPE://通知
                        imBodyBean.setContent(JsonUtils.getString(jsonObject, "content"));
                        imBodyBean.setGroupId(JsonUtils.getString(jsonObject, "groupId"));
                        break;
                    case ActionConstants.IM_MESSAGE_CONTACT_UPDATE_SHOWTYPE://联系人更新通知
                        imBodyBean.setTid(JsonUtils.getString(jsonObject, "tid"));
                        imBodyBean.setGroupId(JsonUtils.getString(jsonObject, "groupId"));
                        imBodyBean.setContent("更新联系人");
                        break;
                    case ActionConstants.IM_MESSAGE_SET_TOP_SHOWTYPE://设置置顶
                        imBodyBean.setGroupId(JsonUtils.getString(jsonObject, "groupId"));
                        imBodyBean.setIsStart(JsonUtils.getInt(jsonObject, "isStar"));
                        imBodyBean.setAction_groupId(JsonUtils.getString(jsonObject, "action_groupId"));
                        imBodyBean.setAction_tid(JsonUtils.getString(jsonObject, "action_tid"));
                        imBodyBean.setTid(JsonUtils.getString(jsonObject, "tid"));
                        imBodyBean.setContent("设置置顶");
                        break;
                    case ActionConstants.IM_MESSAGE_LEAVE_GROUP_SHOWTYPE://离开讨论组
                        imBodyBean.setTid(JsonUtils.getString(jsonObject, "tid"));
                        imBodyBean.setGroupId(JsonUtils.getString(jsonObject, "groupId"));
                        imBodyBean.setContent("离开讨论组");
                        break;
                }
                imBodyBean.setShow_type(showType);

            }
        }
        return imBodyBean;
    }

}
