package com.icourt.alpha.entity.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author 创建人:lu.zhao
 *         <p>
 *         搜索标签
 * @data 创建时间:16/11/12
 */
public class SearchTag implements Parcelable {
    private static final long serialVersionUID = 2684657309332033242L;
//    @Transient
    private Long tag_id;
//    @Id
    private Long s_id;

    private int backgroundResId;
    private int id;
    private boolean isChecked;
    private int leftDrawableResId;
    private int rightDrawableResId;

    private String title;
    private String url;

    public SearchTag() {

    }

    public SearchTag(int paramInt, String paramString) {
        this.id = paramInt;
        this.title = paramString;
    }

    protected SearchTag(Parcel in) {
        backgroundResId = in.readInt();
        id = in.readInt();
        isChecked = in.readByte() != 0;
        leftDrawableResId = in.readInt();
        rightDrawableResId = in.readInt();
        title = in.readString();
        url = in.readString();
    }

    public SearchTag(Long tag_id, Long s_id, String title, String url) {
        this.tag_id = tag_id;
        this.s_id = s_id;
        this.title = title;
        this.url = url;
    }


    public static final Creator<SearchTag> CREATOR = new Creator<SearchTag>() {
        @Override
        public SearchTag createFromParcel(Parcel in) {
            return new SearchTag(in);
        }

        @Override
        public SearchTag[] newArray(int size) {
            return new SearchTag[size];
        }
    };

    public int getBackgroundResId() {
        return this.backgroundResId;
    }

    public int getId() {
        return this.id;
    }

    public int getLeftDrawableResId() {
        return this.leftDrawableResId;
    }

    public int getRightDrawableResId() {
        return this.rightDrawableResId;
    }

    public String getTitle() {
        return this.title;
    }

    public boolean isChecked() {
        return this.isChecked;
    }

    public void setBackgroundResId(int paramInt) {
        this.backgroundResId = paramInt;
    }

    public void setChecked(boolean paramBoolean) {
        this.isChecked = paramBoolean;
    }

    public void setId(int paramInt) {
        this.id = paramInt;
    }

    public void setLeftDrawableResId(int paramInt) {
        this.leftDrawableResId = paramInt;
    }

    public void setRightDrawableResId(int paramInt) {
        this.rightDrawableResId = paramInt;
    }

    public void setTitle(String paramString) {
        this.title = paramString;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(backgroundResId);
        parcel.writeInt(id);
        parcel.writeByte((byte) (isChecked ? 1 : 0));
        parcel.writeInt(leftDrawableResId);
        parcel.writeInt(rightDrawableResId);
        parcel.writeString(title);
        parcel.writeString(url);
    }

    public Long getTag_id() {
        return this.tag_id;
    }

    public void setTag_id(Long tag_id) {
        this.tag_id = tag_id;
    }

    public Long getS_id() {
        return this.s_id;
    }

    public void setS_id(Long s_id) {
        this.s_id = s_id;
    }

}
