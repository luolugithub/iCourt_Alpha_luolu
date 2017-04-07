package com.icourt.alpha.entity.bean;

import java.io.Serializable;
import java.util.List;

/**
 * @data 创建时间:16/11/8
 *
 * @author 创建人:lu.zhao
 *
 *  第三方用户信息。
 */

public class WXUserInfo implements Serializable{


    /**
     * openid : oWURAv6KxKo00wv1ehhjFX42-KHo
     * nickname : 释别,
     * sex : 1
     * language : zh_CN
     * city :
     * province :
     * country : CN
     * headimgurl : http://wx.qlogo.cn/mmopen/ibMKXbzK99YOWdqbU3aZ5AQbAoGJPa2aO5uTffXCB1vTrOGePfiaVvWIpsc1JhYLU4oMTe1LySBotxcrdacWNG6TzV2LxzBNza/0
     * privilege : []
     * unionid : olLdkwdcRsKx2zTY9xbmbJfyZTxI
     */

    private String openid;
    private String nickname;
    private int sex;
    private String language;
    private String city;
    private String province;
    private String country;
    private String headimgurl;
    private String unionid;
    private List<?> privilege;

    private String alphaToken;

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getHeadimgurl() {
        return headimgurl;
    }

    public void setHeadimgurl(String headimgurl) {
        this.headimgurl = headimgurl;
    }

    public String getUnionid() {
        return unionid;
    }

    public void setUnionid(String unionid) {
        this.unionid = unionid;
    }

    public List<?> getPrivilege() {
        return privilege;
    }

    public void setPrivilege(List<?> privilege) {
        this.privilege = privilege;
    }

    public String getAlphaToken() {
        return alphaToken;
    }

    public void setAlphaToken(String alphaToken) {
        this.alphaToken = alphaToken;
    }
}
