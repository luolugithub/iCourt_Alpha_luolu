package com.icourt.alpha.entity.bean;

import java.io.Serializable;

/**
 * @data 创建时间:16/11/30
 *
 * @author 创建人:lu.zhao
 *
 * 评论
 */

public class CommentBean implements Serializable{


    /**
     * id : 10F50E79B6C611E6992300163E162ADD
     * content : 测试测试
     * createUserId : null
     * createTime : 1480487248000
     * updateTime : 1480487248000
     * hostType : null
     * hostId : 0503A3E9B6C611E6992300163E162ADD
     * createUser : {"userId":"0480D584AD6311E6992300163E162ADD","userName":"赵潞","pic":"http://wx.qlogo.cn/mmopen/TLV402oJicT3zwHrmhjpvtd7jFSkT9RatMk7S0K8jg8ylr9o3ibNZBoHETZApnYbTvQfYLSfkUMLoyEG3kMGKfu2ibAkr3HEAjU/0"}
     */

    private String id;
    private String content;
    private Object createUserId;
    private long createTime;
    private long updateTime;
    private Object hostType;
    private String hostId;
    /**
     * userId : 0480D584AD6311E6992300163E162ADD
     * userName : 赵潞
     * pic : http://wx.qlogo.cn/mmopen/TLV402oJicT3zwHrmhjpvtd7jFSkT9RatMk7S0K8jg8ylr9o3ibNZBoHETZApnYbTvQfYLSfkUMLoyEG3kMGKfu2ibAkr3HEAjU/0
     */

    private CreateUserBean createUser;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Object getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(Object createUserId) {
        this.createUserId = createUserId;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public Object getHostType() {
        return hostType;
    }

    public void setHostType(Object hostType) {
        this.hostType = hostType;
    }

    public String getHostId() {
        return hostId;
    }

    public void setHostId(String hostId) {
        this.hostId = hostId;
    }

    public CreateUserBean getCreateUser() {
        return createUser;
    }

    public void setCreateUser(CreateUserBean createUser) {
        this.createUser = createUser;
    }

    public static class CreateUserBean {
        private String userId;
        private String userName;
        private String pic;

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getPic() {
            return pic;
        }

        public void setPic(String pic) {
            this.pic = pic;
        }
    }
}
