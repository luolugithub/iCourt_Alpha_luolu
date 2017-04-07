package com.icourt.alpha.entity.bean;

import java.io.Serializable;
import java.util.List;

/**
 * @author 创建人:lu.zhao
 *         <p>
 *         钉消息bean
 * @data 创建时间:17/1/16
 */

public class IMDingMsgBean implements Serializable {


    /**
     * id : 7418
     * groupId : 83
     * open : 1
     * createId : BEC7F151A0AC11E6AD6300163E0020D1
     * content : {"name":"吕东东","pinMsg":{"content":"@测试账户 这是你","atList":[{"name":"测试账户","userId":"6F131BEFC1F311E69FB200163E162ADD"}],"name":"吕东东","atAll":"0","pic":"https://wx.qlogo.cn/mmopen/TLV402oJicT3zwHrmhjpvtaQ0g6g1icb8JTXu0BFu0QlQjkKQBiaGmiabMhB05ZyJiayMfIBRsV440iao3ACcB05ibN73JRaGawRJLD/0","show_type":"3"},"isPining":"1","show_type":"2"}
     * createDate : 1484565509000
     * updateDate : null
     * type : 0
     * createName : 吕东东
     * pic : https://wx.qlogo.cn/mmopen/TLV402oJicT3zwHrmhjpvtaQ0g6g1icb8JTXu0BFu0QlQjkKQBiaGmiabMhB05ZyJiayMfIBRsV440iao3ACcB05ibN73JRaGawRJLD/0
     * groupName : 测试聊天
     * tid : 15507002
     * star : 0
     * ding : 0
     * dingName : null
     * show_type : 2
     * searchContent : null
     * toid : null
     * pinMsg : {"content":"@测试账户 这是你","atList":[{"name":"测试账户","userId":"6F131BEFC1F311E69FB200163E162ADD"}],"name":"吕东东","atAll":"0","pic":"https://wx.qlogo.cn/mmopen/TLV402oJicT3zwHrmhjpvtaQ0g6g1icb8JTXu0BFu0QlQjkKQBiaGmiabMhB05ZyJiayMfIBRsV440iao3ACcB05ibN73JRaGawRJLD/0","show_type":"3"}
     */

    private int id;
    private int groupId;
    private int open;
    private String createId;
    private String content;
    private long createDate;
    private Object updateDate;
    private int type;
    private String createName;
    private String pic;
    private String groupName;
    private String tid;
    private int star;
    private int ding;
    private Object dingName;
    private String show_type;
    private Object searchContent;
    private Object toid;
    /**
     * content : @测试账户 这是你
     * atList : [{"name":"测试账户","userId":"6F131BEFC1F311E69FB200163E162ADD"}]
     * name : 吕东东
     * atAll : 0
     * pic : https://wx.qlogo.cn/mmopen/TLV402oJicT3zwHrmhjpvtaQ0g6g1icb8JTXu0BFu0QlQjkKQBiaGmiabMhB05ZyJiayMfIBRsV440iao3ACcB05ibN73JRaGawRJLD/0
     * show_type : 3
     */

    private PinMsgBean pinMsg;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public int getOpen() {
        return open;
    }

    public void setOpen(int open) {
        this.open = open;
    }

    public String getCreateId() {
        return createId;
    }

    public void setCreateId(String createId) {
        this.createId = createId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getCreateDate() {
        return createDate;
    }

    public void setCreateDate(long createDate) {
        this.createDate = createDate;
    }

    public Object getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Object updateDate) {
        this.updateDate = updateDate;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getCreateName() {
        return createName;
    }

    public void setCreateName(String createName) {
        this.createName = createName;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public int getStar() {
        return star;
    }

    public void setStar(int star) {
        this.star = star;
    }

    public int getDing() {
        return ding;
    }

    public void setDing(int ding) {
        this.ding = ding;
    }

    public Object getDingName() {
        return dingName;
    }

    public void setDingName(Object dingName) {
        this.dingName = dingName;
    }

    public String getShow_type() {
        return show_type;
    }

    public void setShow_type(String show_type) {
        this.show_type = show_type;
    }

    public Object getSearchContent() {
        return searchContent;
    }

    public void setSearchContent(Object searchContent) {
        this.searchContent = searchContent;
    }

    public Object getToid() {
        return toid;
    }

    public void setToid(Object toid) {
        this.toid = toid;
    }

    public PinMsgBean getPinMsg() {
        return pinMsg;
    }

    public void setPinMsg(PinMsgBean pinMsg) {
        this.pinMsg = pinMsg;
    }

    public static class PinMsgBean implements Serializable{
        private String content;
        private String name;
        private String pic;
        private String show_type;
        private String fileName;//文件名称
        private String filePath;//文件地址
        private String fileSize;//文件大小
        private String atAll; // atAll为“1”时为at所有人，atAll为“0”时按照atList来@人员
        private List<AtListBean> atBeanList;//被at的人集合
        /**
         * name : 测试账户
         * userId : 6F131BEFC1F311E69FB200163E162ADD
         */

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAtAll() {
            return atAll;
        }

        public void setAtAll(String atAll) {
            this.atAll = atAll;
        }

        public String getPic() {
            return pic;
        }

        public void setPic(String pic) {
            this.pic = pic;
        }

        public String getShow_type() {
            return show_type;
        }

        public void setShow_type(String show_type) {
            this.show_type = show_type;
        }
        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public String getFilePath() {
            return filePath;
        }

        public void setFilePath(String filePath) {
            this.filePath = filePath;
        }

        public String getFileSize() {
            return fileSize;
        }

        public void setFileSize(String fileSize) {
            this.fileSize = fileSize;
        }

        public List<AtListBean> getAtBeanList() {
            return atBeanList;
        }

        public void setAtBeanList(List<AtListBean> atBeanList) {
            this.atBeanList = atBeanList;
        }

        public static class AtListBean implements Serializable{
            private String name;
            private String userId;

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getUserId() {
                return userId;
            }

            public void setUserId(String userId) {
                this.userId = userId;
            }
        }
    }
}
