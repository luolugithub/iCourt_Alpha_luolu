package com.icourt.alpha.entity.bean;

import java.io.Serializable;

/**
 * @author 创建人:lu.zhao
 *         <p>
 *         团队成员
 * @data 创建时间:16/12/1
 */

public class MemberBean implements Serializable {


    /**
     * pkId : 1ef1d7956e3d4bf0bcaf037f28034cfa
     * memberId : 0480D584AD6311E6992300163E162ADD
     * createUserId : 5064AC65AFC611E6992300163E162ADD
     * createTime : 1480596835000
     * createUserName : null
     * memberName : 赵潞
     * memberPic : http://wx.qlogo.cn/mmopen/TLV402oJicT3zwHrmhjpvtd7jFSkT9RatMk7S0K8jg8ylr9o3ibNZBoHETZApnYbTvQfYLSfkUMLoyEG3kMGKfu2ibAkr3HEAjU/0
     */

    private String pkId;
    private String memberId;
    private String createUserId;
    private long createTime;
    private Object createUserName;
    private String memberName;
    private String memberPic;

    public String getPkId() {
        return pkId;
    }

    public void setPkId(String pkId) {
        this.pkId = pkId;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(String createUserId) {
        this.createUserId = createUserId;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public Object getCreateUserName() {
        return createUserName;
    }

    public void setCreateUserName(Object createUserName) {
        this.createUserName = createUserName;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public String getMemberPic() {
        return memberPic;
    }

    public void setMemberPic(String memberPic) {
        this.memberPic = memberPic;
    }
}
