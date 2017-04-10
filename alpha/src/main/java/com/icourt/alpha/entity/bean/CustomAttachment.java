package com.icourt.alpha.entity.bean;

import com.icourt.alpha.widget.parser.CustomAttachParser;
import com.netease.nimlib.sdk.msg.attachment.MsgAttachment;

import org.json.JSONObject;

/**
 * @author 创建人:lu.zhao
 *         <p>
 *         自定义消息附件的基类
 * @data 创建时间:17/3/30
 */

public abstract class CustomAttachment implements MsgAttachment {

    protected int showType;
    protected String content;
    protected String id;
    protected String matterName;
    protected String route;
    protected String object;
    protected String type;
    protected String scene;
    protected String pic;
    protected String operator;


    public CustomAttachment(int showType, String content, String id, String matterName, String route, String object, String type, String scene, String pic, String operator) {
        this.showType = showType;
        this.content = content;
        this.id = id;
        this.matterName = matterName;
        this.route = route;
        this.object = object;
        this.type = type;
        this.scene = scene;
        this.pic = pic;
        this.operator = operator;
    }

    public CustomAttachment() {

    }


    // 解析附件内容。
    public void fromJson(JSONObject data) {
        if (data != null) {
            parseData(data);
        }
    }

    // 实现 MsgAttachment 的接口，封装公用字段，然后调用子类的封装函数。
    @Override
    public String toJson(boolean send) {
        return CustomAttachParser.packData(packData());
    }

    // 子类的解析和封装接口。
    protected abstract void parseData(JSONObject data);

    protected abstract JSONObject packData();

    public int getShowType() {
        return showType;
    }

    public void setShowType(int showType) {
        this.showType = showType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMatterName() {
        return matterName;
    }

    public void setMatterName(String matterName) {
        this.matterName = matterName;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getScene() {
        return scene;
    }

    public void setScene(String scene) {
        this.scene = scene;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }
}
