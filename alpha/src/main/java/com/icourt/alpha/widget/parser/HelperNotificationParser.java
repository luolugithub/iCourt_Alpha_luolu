package com.icourt.alpha.widget.parser;

import com.icourt.alpha.entity.bean.HelperNotification;
import com.icourt.alpha.utils.JsonUtils;

import org.json.JSONObject;

/**
 * @author 创建人:lu.zhao
 *         <p>
 *         alpha助手通知解析
 * @data 创建时间:17/3/30
 */

public class HelperNotificationParser {

    public static HelperNotification getHelperNotification(String json) {
        HelperNotification helperNotification = null;
        JSONObject jsonObject = JsonUtils.getJSONObject(json);
        if (jsonObject != null) {
            helperNotification = new HelperNotification();
            helperNotification.setShowType(JsonUtils.getInt(jsonObject, "showType"));
            helperNotification.setContent(JsonUtils.getString(jsonObject, "content"));
            helperNotification.setId(JsonUtils.getString(jsonObject, "id"));
            helperNotification.setMatterName(JsonUtils.getString(jsonObject, "matterName"));
            helperNotification.setRoute(JsonUtils.getString(jsonObject, "route"));
            helperNotification.setObject(JsonUtils.getString(jsonObject, "object"));
            helperNotification.setType(JsonUtils.getString(jsonObject, "type"));
            helperNotification.setScene(JsonUtils.getString(jsonObject, "scene"));
            helperNotification.setTaskName(JsonUtils.getString(jsonObject, "taskName"));
            helperNotification.setPic(JsonUtils.getString(jsonObject, "pic"));
            helperNotification.setOperator(JsonUtils.getString(jsonObject, "operator"));

            if (jsonObject.has("dueTime")) {
                helperNotification.setDueTime(JsonUtils.getLong(jsonObject, "dueTime"));
            }
            if (jsonObject.has("fileName")) {
                helperNotification.setFileName(JsonUtils.getString(jsonObject, "fileName"));
            }
            if (jsonObject.has("reply")) {
                helperNotification.setReply(JsonUtils.getString(jsonObject, "reply"));
            }

            if (jsonObject.has("endDate")) {
                helperNotification.setEndDate(JsonUtils.getLong(jsonObject, "endDate"));
            }
            if (jsonObject.has("startDate")) {
                helperNotification.setStartDate(JsonUtils.getLong(jsonObject, "startDate"));
            }
            if (jsonObject.has("clientName")) {
                helperNotification.setClientName(JsonUtils.getString(jsonObject, "clientName"));
            }
            if (jsonObject.has("serveContent")) {
                helperNotification.setServeContent(JsonUtils.getString(jsonObject, "serveContent"));
            }
            if (jsonObject.has("caseProcess")) {
                helperNotification.setCaseProcess(JsonUtils.getString(jsonObject, "caseProcess"));
            }
            if (jsonObject.has("status")) {
                helperNotification.setStatus(JsonUtils.getString(jsonObject, "status"));
            }
        }
        return helperNotification;
    }

}
