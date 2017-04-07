package com.icourt.alpha.entity.bean;

import java.io.Serializable;
import java.util.List;

/**
 * @author 创建人:lu.zhao
 *         <p>
 *         消息体
 * @data 创建时间:17/1/5
 */

public class IMBodyBean implements Serializable {


    /**
     * show_type : 2
     * isPining : 1
     * pinMsg : {"show_type":"0","name":"张陆","pic":"https://wx.qlogo.cn/mmopen/ajNVdqHZLLDaBwAxDupHs3yEodFbGwhSlElbVGaSPeH23k8CPUyicpicb5s84cwHjDtIDTD9MqUpWzJrP5Y7yZaQ/0","id":"2109","content":"blibli~"}
     * groupId : 83
     * name : 吕东东
     * id : 2121
     * pic : https://wx.qlogo.cn/mmopen/TLV402oJicT3zwHrmhjpvtaQ0g6g1icb8JTXu0BFu0QlQjkKQBiaGmiabMhB05ZyJiayMfIBRsV440iao3ACcB05ibN73JRaGawRJLD/0
     * tid : 15507002
     */

    private int show_type;// 0: 文本消息; 1:文件 ; 2: 钉消息;  3:@消息  4:通知消息

    /**
     * show_type : 0
     * name : 张陆
     * pic : https://wx.qlogo.cn/mmopen/ajNVdqHZLLDaBwAxDupHs3yEodFbGwhSlElbVGaSPeH23k8CPUyicpicb5s84cwHjDtIDTD9MqUpWzJrP5Y7yZaQ/0
     * id : 2109
     * content : blibli~
     */

    private PinMsgBean pinMsg;//被钉的消息
    private String groupId;//组id
    private String name;// 发消息人name
    private String id;//消息id
    private String pic;//发消息人头像
    private String tid;//云信组/对方 id
    private String fileName;//文件名称
    private String filePath;//文件地址
    private String fileSize;//文件大小
    private String content;
    private String route;//存放连接的url(通知需要)
    private int atAll; // atAll为“1”时为at所有人，atAll为“0”时按照atList来@人员
    private List<AtBean> atBeanList;//被at的人集合
    private long time;
    private boolean isRight;
    private int isStart;
    private String action_tid;
    private String action_groupId;

    public int getShow_type() {
        return show_type;
    }

    public void setShow_type(int show_type) {
        this.show_type = show_type;
    }


    public PinMsgBean getPinMsg() {
        return pinMsg;
    }

    public void setPinMsg(PinMsgBean pinMsg) {
        this.pinMsg = pinMsg;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getAtAll() {
        return atAll;
    }

    public void setAtAll(int atAll) {
        this.atAll = atAll;
    }

    public List<AtBean> getAtBeanList() {
        return atBeanList;
    }

    public void setAtBeanList(List<AtBean> atBeanList) {
        this.atBeanList = atBeanList;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public boolean isRight() {
        return isRight;
    }

    public void setRight(boolean right) {
        isRight = right;
    }

    public int getIsStart() {
        return isStart;
    }

    public void setIsStart(int isStart) {
        this.isStart = isStart;
    }

    public String getAction_tid() {
        return action_tid;
    }

    public void setAction_tid(String action_tid) {
        this.action_tid = action_tid;
    }

    public String getAction_groupId() {
        return action_groupId;
    }

    public void setAction_groupId(String action_groupId) {
        this.action_groupId = action_groupId;
    }

    public static class PinMsgBean {
        private int show_type;
        private String isPining; // 0:取消钉 ; 1:加钉
        private String name;
        private String pic;
        private String id;
        private String content;
        private String fileName;//文件名称
        private String filePath;//文件地址
        private String fileSize;//文件大小
        private int atAll; // atAll为“1”时为at所有人，atAll为“0”时按照atList来@人员
        private List<AtBean> atBeanList;//被at的人集合

        public int getShow_type() {
            return show_type;
        }

        public void setShow_type(int show_type) {
            this.show_type = show_type;
        }

        public String getIsPining() {
            return isPining;
        }

        public void setIsPining(String isPining) {
            this.isPining = isPining;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPic() {
            return pic;
        }

        public void setPic(String pic) {
            this.pic = pic;
        }

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

        public int getAtAll() {
            return atAll;
        }

        public void setAtAll(int atAll) {
            this.atAll = atAll;
        }

        public List<AtBean> getAtBeanList() {
            return atBeanList;
        }

        public void setAtBeanList(List<AtBean> atBeanList) {
            this.atBeanList = atBeanList;
        }
    }

    public static class AtBean {
        private String id;
        private String name;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
