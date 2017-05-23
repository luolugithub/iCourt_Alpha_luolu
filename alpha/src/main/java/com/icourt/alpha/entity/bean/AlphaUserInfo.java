package com.icourt.alpha.entity.bean;

import com.icourt.alpha.http.httpmodel.ResEntity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by icourt on 16/11/8.
 * 注意这个接口 直接返回对象
 */

public class AlphaUserInfo extends ResEntity<String> implements Serializable {


    /**
     * groups : []
     * phone : null
     * mail : null
     * calendarPKid : null
     * resultCode : 1
     * photo : null
     * pic : http://wx.qlogo.cn/mmopen/SHnMujzj2v8VXia4lzCEWl3AX3bqibjAWFArEjibEmq92wpHD2YKDPNNmM4zVww3t3e9EsXfZlyRhQricxib50nJnrSQeqgR1uNJt/0
     * userId : 5064AC65AFC611E6992300163E162ADD
     * officeId : 4d792e316a0511e6aa7600163e162add
     * resultMess :
     * token : eyJhbGciOiJIUzI1NiJ9.eyJvZmZpY2VfbmFtZSI6ImlDb3VydCIsInVzZXJfbmFtZSI6Iui1tea9nua9niIsImV4cCI6MTQ4MDMyMjYwMTA5NSwiaXNzIjoiaUxhdy5jb20iLCJ1c2VyX2lkIjoiNTA2NEFDNjVBRkM2MTFFNjk5MjMwMDE2M0UxNjJBREQiLCJpYXQiOjE0Nzk3MTc4MDEwOTUsIm9mZmljZV9pZCI6IjRkNzkyZTMxNmEwNTExZTZhYTc2MDAxNjNlMTYyYWRkIn0.WBVCiNje_g-lvYk2HAlFhg2NNHyxuIVP28bM-ziHQ8s
     * refreshToken : eyJhbGciOiJIUzI1NiJ9.eyJvZmZpY2VfbmFtZSI6ImlDb3VydCIsInVzZXJfbmFtZSI6Iui1tea9nua9niIsImV4cCI6MTQ4MDYwNjgzMzgwMSwiaXNzIjoiaUxhdy5jb20iLCJ1c2VyX2lkIjoiNTA2NEFDNjVBRkM2MTFFNjk5MjMwMDE2M0UxNjJBREQiLCJpYXQiOjE0Nzk3MTc4MDEwOTcsIm9mZmljZV9pZCI6IjRkNzkyZTMxNmEwNTExZTZhYTc2MDAxNjNlMTYyYWRkIn0.mME5xSGBWzHdBb38CXTi8A7Dp-5WctKISMyE2KtJCDk
     * name : 赵潞潞
     */

    private String thirdpartId;
    private String chatToken;
    private Long loginTime;
    private String phone;
    private String mail;
    private String calendarPKid;
    private String photo;
    private String pic;
    private String userId;
    private String officeId;
    private String officename;
    private String token;
    private String refreshToken;
    private String name;
    private List<SelectGroupBean> groups;

    public String getThirdpartId() {
        return thirdpartId;
    }

    public void setThirdpartId(String thirdpartId) {
        this.thirdpartId = thirdpartId;
    }

    public String getChatToken() {
        return chatToken;
    }

    public void setChatToken(String chatToken) {
        this.chatToken = chatToken;
    }

    public Long getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(Long loginTime) {
        this.loginTime = loginTime;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getCalendarPKid() {
        return calendarPKid;
    }

    public void setCalendarPKid(String calendarPKid) {
        this.calendarPKid = calendarPKid;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getOfficeId() {
        return officeId;
    }

    public void setOfficeId(String officeId) {
        this.officeId = officeId;
    }

    public String getOfficename() {
        return officename;
    }

    public void setOfficename(String officename) {
        this.officename = officename;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<SelectGroupBean> getGroups() {
        return groups;
    }

    public void setGroups(List<SelectGroupBean> groups) {
        this.groups = groups;
    }

    @Override
    public String toString() {
        return "AlphaUserInfo{" +
                "thirdpartId='" + thirdpartId + '\'' +
                ", chatToken='" + chatToken + '\'' +
                ", loginTime=" + loginTime +
                ", phone='" + phone + '\'' +
                ", mail='" + mail + '\'' +
                ", calendarPKid='" + calendarPKid + '\'' +
                ", photo='" + photo + '\'' +
                ", pic='" + pic + '\'' +
                ", userId='" + userId + '\'' +
                ", officeId='" + officeId + '\'' +
                ", officename='" + officename + '\'' +
                ", token='" + token + '\'' +
                ", refreshToken='" + refreshToken + '\'' +
                ", name='" + name + '\'' +
                ", groups=" + groups +
                '}';
    }
}
