package com.icourt.alpha.entity.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.mcxtzhang.indexlib.IndexBar.bean.BaseIndexPinyinBean;

import java.io.Serializable;

/**
 * @author 创建人:lu.zhao
 *         <p>
 *         聊天讨论组bean
 * @data 创建时间:17/1/7
 */

public class IMGroupBean extends BaseIndexPinyinBean implements Serializable, Parcelable {

    private boolean isTop;//是否是最上面的 不需要被转化成拼音的
    /**
     * id : 83
     * name : 测试聊天噻
     * createId : BE8AEB0AA02011E69A3800163E0020D1
     * createName : 陈利
     * description : 加油～～～alpha ！``
     * <p>
     * <p>
     * createDate : 1482323855000
     * updateDate : 1483719078000
     * type : 0
     * tid : 15507002
     * announcement : blibli～
     * <p>
     * <p>
     * count : 21
     * isJoin : 1
     * characterName : null
     * createType : null
     */

    private int id;
    private String name;
    private String createId;
    private String createName;
    private String description;
    private long createDate;
    private long updateDate;
    private int type;
    private String tid;
    private String announcement;
    private int count;
    private int isJoin;
    private String characterName;
    private String createType;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreateId() {
        return createId;
    }

    public void setCreateId(String createId) {
        this.createId = createId;
    }

    public String getCreateName() {
        return createName;
    }

    public void setCreateName(String createName) {
        this.createName = createName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getCreateDate() {
        return createDate;
    }

    public void setCreateDate(long createDate) {
        this.createDate = createDate;
    }

    public long getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(long updateDate) {
        this.updateDate = updateDate;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public String getAnnouncement() {
        return announcement;
    }

    public void setAnnouncement(String announcement) {
        this.announcement = announcement;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getIsJoin() {
        return isJoin;
    }

    public void setIsJoin(int isJoin) {
        this.isJoin = isJoin;
    }

    public String getCharacterName() {
        return characterName;
    }

    public void setCharacterName(String characterName) {
        this.characterName = characterName;
    }

    public String getCreateType() {
        return createType;
    }

    public void setCreateType(String createType) {
        this.createType = createType;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.name);
        dest.writeString(this.createId);
        dest.writeString(this.createName);
        dest.writeString(this.description);
        dest.writeLong(this.createDate);
        dest.writeLong(this.updateDate);
        dest.writeInt(this.type);
        dest.writeString(this.tid);
        dest.writeString(this.announcement);
        dest.writeInt(this.count);
        dest.writeInt(this.isJoin);
        dest.writeString(this.characterName);
        dest.writeString(this.createType);
    }

    public IMGroupBean() {
    }

    public IMGroupBean(String name) {
        this.name = name;
    }

    public boolean isTop() {
        return isTop;
    }

    public IMGroupBean setTop(boolean top) {
        isTop = top;
        return this;
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

    protected IMGroupBean(Parcel in) {
        this.id = in.readInt();
        this.name = in.readString();
        this.createId = in.readString();
        this.createName = in.readString();
        this.description = in.readString();
        this.createDate = in.readLong();
        this.updateDate = in.readLong();
        this.type = in.readInt();
        this.tid = in.readString();
        this.announcement = in.readString();
        this.count = in.readInt();
        this.isJoin = in.readInt();
        this.characterName = in.readString();
        this.createType = in.readString();
    }

    public static final Creator<IMGroupBean> CREATOR = new Creator<IMGroupBean>() {
        @Override
        public IMGroupBean createFromParcel(Parcel source) {
            return new IMGroupBean(source);
        }

        @Override
        public IMGroupBean[] newArray(int size) {
            return new IMGroupBean[size];
        }
    };
}
