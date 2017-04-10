package com.icourt.alpha.entity.bean;

import com.icourt.alpha.utils.JsonUtils;

import org.json.JSONObject;

/**
 * @author 创建人:lu.zhao
 *         <p>
 *         任务状态消息附件
 * @data 创建时间:17/3/30
 */

public class TaskAttachment extends CustomAttachment {

    private long dueTime;
    private String fileName;
    private String reply;
    private String taskName;
    public TaskAttachment(int showType, String content, String id, String matterName, String route, String object, String type, String scene,String pic,String operator) {
        super(showType, content, id, matterName, route, object, type, scene,pic,operator);
    }

    public TaskAttachment() {

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

        taskName = JsonUtils.getString(data, "taskName");
        dueTime = JsonUtils.getLong(data, "dueTime");
        fileName = JsonUtils.getString(data,"fileName");
        reply = JsonUtils.getString(data,"reply");

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

            data.put("taskName", taskName);
            data.put("dueTime", dueTime);
            data.put("fileName",fileName);
            data.put("reply",reply);
            return data;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
