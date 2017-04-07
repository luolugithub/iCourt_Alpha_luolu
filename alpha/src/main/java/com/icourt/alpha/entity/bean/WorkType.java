package com.icourt.alpha.entity.bean;

import java.io.Serializable;

/**
 * @author 创建人:lu.zhao
 *         <p>
 *         工作类别
 * @data 创建时间:16/12/26
 */


public class WorkType implements Serializable {


    /**
     * pkId : 3F754D3CC8CB11E6AAB000163E162ADD
     * name : 谈判协商
     * matterType : 2
     * officeId : 4d792e316a0511e6aa7600163e162add
     */

    private String pkId;
    private String name;
    private String matterType;
    private String officeId;

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

    public String getMatterType() {
        return matterType;
    }

    public void setMatterType(String matterType) {
        this.matterType = matterType;
    }

    public String getOfficeId() {
        return officeId;
    }

    public void setOfficeId(String officeId) {
        this.officeId = officeId;
    }
}
