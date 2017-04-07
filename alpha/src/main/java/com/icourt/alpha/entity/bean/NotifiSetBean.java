package com.icourt.alpha.entity.bean;

import java.io.Serializable;
import java.util.List;

/**
 * @data 创建时间:17/2/28
 *
 * @author 创建人:lu.zhao
 *
 * 通知开关类
 */

public class NotifiSetBean implements Serializable {


    /**
     * name : 项目提醒选择
     * items : [{"id":"2","parentId":"-1","type":"","status":null,"name":"项目类型选择","tagType":"1","memberId":"15C763D0C26C11E69FB200163E162ADD","officeId":null},{"id":"3","parentId":"-1","type":"","status":null,"name":"项目动态类型选择","tagType":"1","memberId":"15C763D0C26C11E69FB200163E162ADD","officeId":null}]
     */

    private String name;
    /**
     * id : 2
     * parentId : -1
     * type :
     * status : null
     * name : 项目类型选择
     * tagType : 1
     * memberId : 15C763D0C26C11E69FB200163E162ADD
     * officeId : null
     */

    private List<ItemsBean> items;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ItemsBean> getItems() {
        return items;
    }

    public void setItems(List<ItemsBean> items) {
        this.items = items;
    }

    public static class ItemsBean {
        private String id;
        private String parentId;
        private String type;
        private int status;
        private String name;
        private int tagType;
        private String memberId;
        private String officeId;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getParentId() {
            return parentId;
        }

        public void setParentId(String parentId) {
            this.parentId = parentId;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getTagType() {
            return tagType;
        }

        public void setTagType(int tagType) {
            this.tagType = tagType;
        }

        public String getMemberId() {
            return memberId;
        }

        public void setMemberId(String memberId) {
            this.memberId = memberId;
        }

        public String getOfficeId() {
            return officeId;
        }

        public void setOfficeId(String officeId) {
            this.officeId = officeId;
        }
    }
}
