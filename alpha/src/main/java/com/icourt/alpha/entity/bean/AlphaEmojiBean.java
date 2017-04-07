package com.icourt.alpha.entity.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * @author 创建人:lu.zhao
 *         <p>
 *         表情bean
 * @data 创建时间:17/3/22
 */

public class AlphaEmojiBean implements Serializable {


    /**
     * keywords : ["face","smile","happy","joy",":D","grin"]
     * char : 😀
     * category : people
     */

    @SerializedName("char")
    private String charX;
    private String category;
    private List<String> keywords;

    public String getCharX() {
        return charX;
    }

    public void setCharX(String charX) {
        this.charX = charX;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }
}
