package com.icourt.alpha.entity.bean;


import java.io.Serializable;

/**
 * @data 创建时间:17/2/27
 *
 * @author 创建人:lu.zhao
 *
 */
public class DisStickBean implements Serializable{


    /**
     * groupId : 1
     * updateDate : 1488176411000
     * memberId : 538BC9C8FCB311E6843370106FAECE2E
     * p2pId : null
     * tid : null
     */

    private String groupId;
    private String updateDate;
    private String memberId;
    private String p2pId;
    private String tid;

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getP2pId() {
        return p2pId;
    }

    public void setP2pId(String p2pId) {
        this.p2pId = p2pId;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }
}
