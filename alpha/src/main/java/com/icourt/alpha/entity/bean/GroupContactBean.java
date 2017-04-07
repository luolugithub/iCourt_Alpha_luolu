package com.icourt.alpha.entity.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.mcxtzhang.indexlib.IndexBar.bean.BaseIndexBean;
import com.mcxtzhang.indexlib.IndexBar.bean.BaseIndexPinyinBean;


import java.io.Serializable;

/**
 * @author 创建人:lu.zhao
 *         <p>
 *         团队联系人
 * @data 创建时间:16/12/15
 */
public class GroupContactBean extends BaseIndexPinyinBean implements Serializable, Parcelable {

    private boolean isTop;//是否是最上面的 不需要被转化成拼音的
    private static final long serialVersionUID = 4472917112042440681L;

    private Long id;

    /**
     * userId : 6F131BEFC1F311E69FB200163E162ADD
     * userName : 123@123.com
     * password :
     * name : 测试账户
     * lastLogin : null
     * ip : null
     * status : 1
     * birthday : null
     * phone : 123@123.com
     * startTime : null
     * endTime : null
     * years : null
     * number : null
     * email : 123@123.com
     * uniquedevice : null
     * officeId : 4d792e316a0511e6aa7600163e162add
     * createDate : 1481716208000
     * updateDate : 1481769753000
     * zhiWu : 12
     * level : null
     * jurisdiction : 0
     * type : 1
     * verifyCode : 61729a5fc19c4f5392f5769ee5e54de9
     * briefing : null
     * weixin : null
     * pic : null
     * groupName : null
     * groupId : null
     * gids : null
     * gnames : null
     * groupNameAndIds : null
     * goups : null
     * idNumber : null
     * charge : false
     */
    private String userId;
    private String userName;
    private String password;
    private String name;
    private String lastLogin;
    private String ip;
    private String status;
    private String birthday;
    private String phone;
    private String startTime;
    private String endTime;
    private String years;
    private String number;
    private String email;
    private String uniquedevice;
    private String officeId;
    private long createDate;
    private long updateDate;
    private String zhiWu;
    private String level;
    private int jurisdiction;
    private int type;
    private String verifyCode;
    private String briefing;
    private String weixin;
    private String pic;
    private String groupName;
    private String groupId;
    private String gids;
    private String gnames;
    private String groupNameAndIds;
    private String goups;
    private String idNumber;
    private String charge;
    private int isRobot = 0;

    public GroupContactBean(Long id, String userId, String userName, String password, String name, String lastLogin,
            String ip, String status, String birthday, String phone, String startTime, String endTime, String years,
            String number, String email, String uniquedevice, String officeId, long createDate, long updateDate,
            String zhiWu, String level, int jurisdiction, int type, String verifyCode, String briefing, String weixin,
            String pic, String groupName, String groupId, String gids, String gnames, String groupNameAndIds, String goups,
            String idNumber, String charge, int isRobot) {
        this.id = id;
        this.userId = userId;
        this.userName = userName;
        this.password = password;
        this.name = name;
        this.lastLogin = lastLogin;
        this.ip = ip;
        this.status = status;
        this.birthday = birthday;
        this.phone = phone;
        this.startTime = startTime;
        this.endTime = endTime;
        this.years = years;
        this.number = number;
        this.email = email;
        this.uniquedevice = uniquedevice;
        this.officeId = officeId;
        this.createDate = createDate;
        this.updateDate = updateDate;
        this.zhiWu = zhiWu;
        this.level = level;
        this.jurisdiction = jurisdiction;
        this.type = type;
        this.verifyCode = verifyCode;
        this.briefing = briefing;
        this.weixin = weixin;
        this.pic = pic;
        this.groupName = groupName;
        this.groupId = groupId;
        this.gids = gids;
        this.gnames = gnames;
        this.groupNameAndIds = groupNameAndIds;
        this.goups = goups;
        this.idNumber = idNumber;
        this.charge = charge;
        this.isRobot = isRobot;
    }

    public GroupContactBean() {
    }

    public GroupContactBean(String name) {
        this.name = name;
    }

    public boolean isTop() {
        return isTop;
    }

    public GroupContactBean setTop(boolean top) {
        isTop = top;
        return this;
    }

    @Override
    public BaseIndexBean setBaseIndexTag(String baseIndexTag) {
        return super.setBaseIndexTag(baseIndexTag);
    }

    @Override
    public String getTarget() {
        return name;
    }

    @Override
    public boolean isNeedToPinyin() {
        return !isTop;
    }


    @Override
    public boolean isShowSuspension() {
        return !isTop;
    }

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastLogin() {
        return this.lastLogin;
    }

    public void setLastLogin(String lastLogin) {
        this.lastLogin = lastLogin;
    }

    public String getIp() {
        return this.ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBirthday() {
        return this.birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getPhone() {
        return this.phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getStartTime() {
        return this.startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return this.endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getYears() {
        return this.years;
    }

    public void setYears(String years) {
        this.years = years;
    }

    public String getNumber() {
        return this.number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUniquedevice() {
        return this.uniquedevice;
    }

    public void setUniquedevice(String uniquedevice) {
        this.uniquedevice = uniquedevice;
    }

    public String getOfficeId() {
        return this.officeId;
    }

    public void setOfficeId(String officeId) {
        this.officeId = officeId;
    }

    public long getCreateDate() {
        return this.createDate;
    }

    public void setCreateDate(long createDate) {
        this.createDate = createDate;
    }

    public long getUpdateDate() {
        return this.updateDate;
    }

    public void setUpdateDate(long updateDate) {
        this.updateDate = updateDate;
    }

    public String getZhiWu() {
        return this.zhiWu;
    }

    public void setZhiWu(String zhiWu) {
        this.zhiWu = zhiWu;
    }

    public String getLevel() {
        return this.level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public int getJurisdiction() {
        return this.jurisdiction;
    }

    public void setJurisdiction(int jurisdiction) {
        this.jurisdiction = jurisdiction;
    }

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getVerifyCode() {
        return this.verifyCode;
    }

    public void setVerifyCode(String verifyCode) {
        this.verifyCode = verifyCode;
    }

    public String getBriefing() {
        return this.briefing;
    }

    public void setBriefing(String briefing) {
        this.briefing = briefing;
    }

    public String getWeixin() {
        return this.weixin;
    }

    public void setWeixin(String weixin) {
        this.weixin = weixin;
    }

    public String getPic() {
        return this.pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getGroupName() {
        return this.groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupId() {
        return this.groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGids() {
        return this.gids;
    }

    public void setGids(String gids) {
        this.gids = gids;
    }

    public String getGnames() {
        return this.gnames;
    }

    public void setGnames(String gnames) {
        this.gnames = gnames;
    }

    public String getGroupNameAndIds() {
        return this.groupNameAndIds;
    }

    public void setGroupNameAndIds(String groupNameAndIds) {
        this.groupNameAndIds = groupNameAndIds;
    }

    public String getGoups() {
        return this.goups;
    }

    public void setGoups(String goups) {
        this.goups = goups;
    }

    public String getIdNumber() {
        return this.idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public String getCharge() {
        return this.charge;
    }

    public void setCharge(String charge) {
        this.charge = charge;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getIsRobot() {
        return isRobot;
    }

    public void setIsRobot(int isRobot) {
        this.isRobot = isRobot;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(this.isTop ? (byte) 1 : (byte) 0);
        dest.writeValue(this.id);
        dest.writeString(this.userId);
        dest.writeString(this.userName);
        dest.writeString(this.password);
        dest.writeString(this.name);
        dest.writeString(this.lastLogin);
        dest.writeString(this.ip);
        dest.writeString(this.status);
        dest.writeString(this.birthday);
        dest.writeString(this.phone);
        dest.writeString(this.startTime);
        dest.writeString(this.endTime);
        dest.writeString(this.years);
        dest.writeString(this.number);
        dest.writeString(this.email);
        dest.writeString(this.uniquedevice);
        dest.writeString(this.officeId);
        dest.writeLong(this.createDate);
        dest.writeLong(this.updateDate);
        dest.writeString(this.zhiWu);
        dest.writeString(this.level);
        dest.writeInt(this.jurisdiction);
        dest.writeInt(this.type);
        dest.writeString(this.verifyCode);
        dest.writeString(this.briefing);
        dest.writeString(this.weixin);
        dest.writeString(this.pic);
        dest.writeString(this.groupName);
        dest.writeString(this.groupId);
        dest.writeString(this.gids);
        dest.writeString(this.gnames);
        dest.writeString(this.groupNameAndIds);
        dest.writeString(this.goups);
        dest.writeString(this.idNumber);
        dest.writeString(this.charge);
        dest.writeInt(this.isRobot);
    }

    protected GroupContactBean(Parcel in) {
        this.isTop = in.readByte() != 0;
        this.id = (Long) in.readValue(Long.class.getClassLoader());
        this.userId = in.readString();
        this.userName = in.readString();
        this.password = in.readString();
        this.name = in.readString();
        this.lastLogin = in.readString();
        this.ip = in.readString();
        this.status = in.readString();
        this.birthday = in.readString();
        this.phone = in.readString();
        this.startTime = in.readString();
        this.endTime = in.readString();
        this.years = in.readString();
        this.number = in.readString();
        this.email = in.readString();
        this.uniquedevice = in.readString();
        this.officeId = in.readString();
        this.createDate = in.readLong();
        this.updateDate = in.readLong();
        this.zhiWu = in.readString();
        this.level = in.readString();
        this.jurisdiction = in.readInt();
        this.type = in.readInt();
        this.verifyCode = in.readString();
        this.briefing = in.readString();
        this.weixin = in.readString();
        this.pic = in.readString();
        this.groupName = in.readString();
        this.groupId = in.readString();
        this.gids = in.readString();
        this.gnames = in.readString();
        this.groupNameAndIds = in.readString();
        this.goups = in.readString();
        this.idNumber = in.readString();
        this.charge = in.readString();
        this.isRobot = in.readInt();
    }

    public static final Creator<GroupContactBean> CREATOR = new Creator<GroupContactBean>() {
        @Override
        public GroupContactBean createFromParcel(Parcel source) {
            return new GroupContactBean(source);
        }

        @Override
        public GroupContactBean[] newArray(int size) {
            return new GroupContactBean[size];
        }
    };

    public boolean equals(Object obj) {
        if (obj instanceof GroupContactBean) {
            GroupContactBean u = (GroupContactBean) obj;
            return this.userId.equals(u.userId);
        }
        return super.equals(obj);
    }

    @Override
    public String toString() {
        return "GroupContactBean{" +
                "isTop=" + isTop +
                ", id=" + id +
                ", userId='" + userId + '\'' +
                ", userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", name='" + name + '\'' +
                ", lastLogin='" + lastLogin + '\'' +
                ", ip='" + ip + '\'' +
                ", status='" + status + '\'' +
                ", birthday='" + birthday + '\'' +
                ", phone='" + phone + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", years='" + years + '\'' +
                ", number='" + number + '\'' +
                ", email='" + email + '\'' +
                ", uniquedevice='" + uniquedevice + '\'' +
                ", officeId='" + officeId + '\'' +
                ", createDate=" + createDate +
                ", updateDate=" + updateDate +
                ", zhiWu='" + zhiWu + '\'' +
                ", level='" + level + '\'' +
                ", jurisdiction=" + jurisdiction +
                ", type=" + type +
                ", verifyCode='" + verifyCode + '\'' +
                ", briefing='" + briefing + '\'' +
                ", weixin='" + weixin + '\'' +
                ", pic='" + pic + '\'' +
                ", groupName='" + groupName + '\'' +
                ", groupId='" + groupId + '\'' +
                ", gids='" + gids + '\'' +
                ", gnames='" + gnames + '\'' +
                ", groupNameAndIds='" + groupNameAndIds + '\'' +
                ", goups='" + goups + '\'' +
                ", idNumber='" + idNumber + '\'' +
                ", charge='" + charge + '\'' +
                ", isRobot=" + isRobot +
                '}';
    }
}
