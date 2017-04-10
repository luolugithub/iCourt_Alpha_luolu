package com.icourt.alpha.entity.bean;

import com.icourt.alpha.utils.JsonUtils;

import org.json.JSONObject;

/**
 * @author 创建人:lu.zhao
 *         <p>
 *         项目状态消息附件
 * @data 创建时间:17/3/30
 */

public class MatterAttachment extends CustomAttachment {

    private long endDate;
    private long startDate;
    private String clientName;
    private String serveContent;
    private String caseProcess;
    private String status;
    public MatterAttachment(int showType, String content, String id, String matterName, String route, String object, String type, String scene, String pic, String operator) {
        super(showType, content, id, matterName, route, object, type, scene,pic,operator);
    }

    public MatterAttachment() {

    }

    @Override
    protected void parseData(JSONObject data) {
        content = JsonUtils.getString(data, "content");
        id = JsonUtils.getString(data, "id");
        matterName = JsonUtils.getString(data, "matterName");
        object = JsonUtils.getString(data, "object");
        route = JsonUtils.getString(data, "route");
        scene = JsonUtils.getString(data, "scene");
        showType = JsonUtils.getInt(data, "showType");
        type = JsonUtils.getString(data, "type");
        pic = JsonUtils.getString(data,"pic");
        operator = JsonUtils.getString(data,"operator");

        clientName = JsonUtils.getString(data, "clientName");
        endDate = JsonUtils.getLong(data, "endDate");
        startDate = JsonUtils.getLong(data, "startDate");
        serveContent = JsonUtils.getString(data,"serveContent");
        caseProcess = JsonUtils.getString(data,"caseProcess");
        status = JsonUtils.getString(data,"status");

    }

    @Override
    protected JSONObject packData() {

        try {
            JSONObject data = new JSONObject();
            data.put("content", content);
            data.put("id", id);
            data.put("matterName", matterName);
            data.put("object", object);
            data.put("route", route);
            data.put("scene", scene);
            data.put("showType", showType);
            data.put("type", type);
            data.put("pic",pic);
            data.put("operator",operator);

            data.put("clientName", clientName);
            data.put("endDate", endDate);
            data.put("startDate", startDate);
            data.put("serveContent",serveContent);
            data.put("caseProcess",caseProcess);
            data.put("status",status);
            return data;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
