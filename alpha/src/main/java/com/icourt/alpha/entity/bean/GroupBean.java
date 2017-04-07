package com.icourt.alpha.entity.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.List;

/**
 * @author 创建人:lu.zhao
 *         <p>
 *         团队
 * @data 创建时间:16/12/9
 */

public class GroupBean implements Parcelable, Serializable {


    /**
     * pkId : 725BC4D9AE0211E6992300163E162ADD
     * name : 安卓开发
     * members : [{"memberPkId":"4F2685198E8B11E6992300163E162ADD","name":"张陆","isCharge":null,"pic":"http://wx.qlogo.cn/mmopen/ajNVdqHZLLDaBwAxDupHs3yEodFbGwhSlElbVGaSPeH23k8CPUyicpicb5s84cwHjDtIDTD9MqUpWzJrP5Y7yZaQ/0"},{"memberPkId":"FC798B079D9311E6992300163E162ADD","name":"测试1","isCharge":null,"pic":null},{"memberPkId":"B63DE3EF9DE511E6992300163E162ADD","name":"测试2","isCharge":null,"pic":null},{"memberPkId":"B1932FF09D0111E6992300163E162ADD","name":"测试10 28","isCharge":null,"pic":null},{"memberPkId":"8CCC667D824C11E6992300163E162ADD","name":"胡关荣","isCharge":null,"pic":"http://wx.qlogo.cn/mmopen/ajNVdqHZLLAHjU7eRmeopKgODiawudjQZBxQicHRic73rhocDkPpeWPCeziaQ8xVw9rgs7ghDpQ3G8SiciceEEE7BtcQ/0"},{"memberPkId":"A7372E0B92D311E69A3800163E0020D1","name":"李亚","isCharge":null,"pic":"http://wx.qlogo.cn/mmopen/SHnMujzj2vibyTYSbuKxly21KJSF0kj5YLXCTUPpQzIsadhKqLTHz4FtoJFc4BcOlMOV4DTJibTkjVibkATiblPINg/0"},{"memberPkId":"50C8BBFCB12211E6992300163E162ADD","name":"666666666666","isCharge":null,"pic":null},{"memberPkId":"6F39C6D06A1A11E6AA7600163E162ADD","name":"杨帆","isCharge":null,"pic":"http://wx.qlogo.cn/mmopen/icOZDW0kqwN6MgibK4iamqJWykEfN7iaO9FuGaYjKr9Ey6DaHIDoft7NJtibicDyLBLXPJu4XqFHibibpxHOc7pibqCicOmul2bk6olUNn/0"},{"memberPkId":"0480D584AD6311E6992300163E162ADD","name":"赵潞","isCharge":null,"pic":"http://wx.qlogo.cn/mmopen/TLV402oJicT3zwHrmhjpvtd7jFSkT9RatMk7S0K8jg8ylr9o3ibNZBoHETZApnYbTvQfYLSfkUMLoyEG3kMGKfu2ibAkr3HEAjU/0"},{"memberPkId":"5064AC65AFC611E6992300163E162ADD","name":"赵潞潞","isCharge":null,"pic":"http://wx.qlogo.cn/mmopen/SHnMujzj2v8VXia4lzCEWl3AX3bqibjAWFArEjibEmq92wpHD2YKDPNNmM4zVww3t3e9EsXfZlyRhQricxib50nJnrSQeqgR1uNJt/0"}]
     * chargeName : null
     * runType : 0
     * pic : null
     */

    private String pkId;
    private String name;
    private String chargeName;
    private String runType;
    private String pic;
    /**
     * memberPkId : 4F2685198E8B11E6992300163E162ADD
     * name : 张陆
     * isCharge : null
     * pic : http://wx.qlogo.cn/mmopen/ajNVdqHZLLDaBwAxDupHs3yEodFbGwhSlElbVGaSPeH23k8CPUyicpicb5s84cwHjDtIDTD9MqUpWzJrP5Y7yZaQ/0
     */

    private List<MembersBean> members;


    public GroupBean() {

    }

    protected GroupBean(Parcel in) {
        pkId = in.readString();
        name = in.readString();
        chargeName = in.readString();
        runType = in.readString();
        pic = in.readString();
    }

    public static final Creator<GroupBean> CREATOR = new Creator<GroupBean>() {
        @Override
        public GroupBean createFromParcel(Parcel in) {
            return new GroupBean(in);
        }

        @Override
        public GroupBean[] newArray(int size) {
            return new GroupBean[size];
        }
    };

    public String getPkId() {
        return pkId;
    }

    public void setPkId(String pkId) {
        this.pkId = pkId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getChargeName() {
        return chargeName;
    }

    public void setChargeName(String chargeName) {
        this.chargeName = chargeName;
    }

    public String getRunType() {
        return runType;
    }

    public void setRunType(String runType) {
        this.runType = runType;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public List<MembersBean> getMembers() {
        return members;
    }

    public void setMembers(List<MembersBean> members) {
        this.members = members;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(pkId);
        parcel.writeString(name);
        parcel.writeString(chargeName);
        parcel.writeString(runType);
        parcel.writeString(pic);
    }

    public static class MembersBean {
        private String memberPkId;
        private String name;
        private String isCharge;
        private String pic;

        public String getMemberPkId() {
            return memberPkId;
        }

        public void setMemberPkId(String memberPkId) {
            this.memberPkId = memberPkId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getIsCharge() {
            return isCharge;
        }

        public void setIsCharge(String isCharge) {
            this.isCharge = isCharge;
        }

        public String getPic() {
            return pic;
        }

        public void setPic(String pic) {
            this.pic = pic;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GroupBean groupBean = (GroupBean) o;

//        if (pkId != groupBean.pkId) return false;
//        if (pkId != null ? !pkId.equals(groupBean.pkId) : groupBean.pkId != null)
//            return false;
//        else
//            return true;
//        if (poster_url != null ? !poster_url.equals(program.poster_url) : program.poster_url != null)
//            return false;
        return !(pkId != null ? !pkId.equals(groupBean.pkId) : groupBean.pkId != null);

    }

    @Override
    public int hashCode() {
        int result = pkId != null ? pkId.hashCode() : 0;
//        result = 31 * result + (int) (timestamp ^ (timestamp >>> 32));
//        result = 31 * result + (poster_url != null ? poster_url.hashCode() : 0);
        result = 31 * result + (pkId != null ? pkId.hashCode() : 0);
        return result;
    }


}
