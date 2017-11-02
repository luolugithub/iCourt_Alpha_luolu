package com.icourt.alpha.entity.bean;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;
import com.icourt.alpha.db.convertor.IConvertModel;
import com.icourt.alpha.db.dbmodel.ContactDbModel;
import com.icourt.alpha.view.recyclerviewDivider.ISuspensionInterface;
import com.icourt.alpha.widget.filter.IFilterEntity;

import java.io.Serializable;

/**
 * Description  联系人模型
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/10
 * version 1.0.0
 */
public class GroupContactBean
        implements IConvertModel<ContactDbModel>,
        Serializable,
        ISuspensionInterface,
        IFilterEntity, Parcelable {

    public static final int TYPE_ROBOT = 100;
    public static final int TYPE_ALL = -100;

    public String suspensionTag;

    public String accid;
    @SerializedName(value = "userId", alternate = "user_id")
    public String userId;
    public String name;
    @SerializedName(value = "nameCharacter", alternate = "name_character")
    public String nameCharacter;
    public String title;
    public String phone;
    public String email;
    public String pic;
    public int type;

    public GroupContactBean() {
    }

    public GroupContactBean(String accid, String userId, String name, String nameCharacter, String title, String phone, String email, String pic, int type) {
        this.accid = accid;
        this.userId = userId;
        this.name = name;
        this.nameCharacter = nameCharacter;
        this.title = title;
        this.phone = phone;
        this.email = email;
        this.pic = pic;
        this.type = type;
    }

    @Override
    public String toString() {
        return "GroupContactBean{" +
                "suspensionTag='" + suspensionTag + '\'' +
                ", accid='" + accid + '\'' +
                ", userId='" + userId + '\'' +
                ", name='" + name + '\'' +
                ", nameCharacter='" + nameCharacter + '\'' +
                ", title='" + title + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", pic='" + pic + '\'' +
                ", type=" + type +
                '}';
    }

    @Override
    public ContactDbModel convert2Model() {
        if (TextUtils.isEmpty(accid)) return null;
        return new ContactDbModel(accid,
                userId,
                null,
                name,
                nameCharacter,
                title,
                phone,
                email,
                pic,
                type);
    }

    @Override
    public boolean isShowSuspension() {
        return true;
    }

    @NonNull
    @Override
    public String getSuspensionTag() {
        return TextUtils.isEmpty(suspensionTag) ? "#" : suspensionTag;
    }

    @Override
    public String getTargetField() {
        return name;
    }

    @Override
    public void setSuspensionTag(@NonNull String suspensionTag) {
        this.suspensionTag = suspensionTag;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null) return false;
        if (getClass() != o.getClass())
            return false;
        final GroupContactBean other = (GroupContactBean) o;
        return TextUtils.equals(this.accid, other.accid);
    }


    @Override
    public boolean isFilter(int type) {
        return this.type == type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.suspensionTag);
        dest.writeString(this.accid);
        dest.writeString(this.userId);
        dest.writeString(this.name);
        dest.writeString(this.nameCharacter);
        dest.writeString(this.title);
        dest.writeString(this.phone);
        dest.writeString(this.email);
        dest.writeString(this.pic);
        dest.writeInt(this.type);
    }

    protected GroupContactBean(Parcel in) {
        this.suspensionTag = in.readString();
        this.accid = in.readString();
        this.userId = in.readString();
        this.name = in.readString();
        this.nameCharacter = in.readString();
        this.title = in.readString();
        this.phone = in.readString();
        this.email = in.readString();
        this.pic = in.readString();
        this.type = in.readInt();
    }

    public static final Parcelable.Creator<GroupContactBean> CREATOR = new Parcelable.Creator<GroupContactBean>() {
        @Override
        public GroupContactBean createFromParcel(Parcel source) {
            return new GroupContactBean(source);
        }

        @Override
        public GroupContactBean[] newArray(int size) {
            return new GroupContactBean[size];
        }
    };
}
