package com.icourt.alpha.entity.bean;

import java.io.Serializable;
import java.util.List;

/**
 * @data 创建时间:16/12/20
 *
 * @author 创建人:lu.zhao
 *
 * 个人联系人详情
 */

public class ContactDeatilBean implements Serializable{


    /**
     * pkid : E52F4046C66811E69FB200163E162ADD
     * contactType : P
     * name : 我还是姓名
     * abbr : null
     * officeId : 4d792e316a0511e6aa7600163e162add
     * isValid : 1
     * isHistory : 0
     * isPotential : 0
     * isCurrent : 0
     * isPause : 0
     * pinyin : wohuanshixingming,wohaishixingming
     * pinyinInitial : whsxm
     * logoUrl : null
     * sex : null
     * title : null
     * tycId : null
     * impression : null
     * crtTime : 1482206450000
     * crtUser : D7AE70BCBA9211E6834900163E001EAA
     * updTime : 1482206450000
     * updUser : D7AE70BCBA9211E6834900163E001EAA
     * crtUserName : 王江
     */

    private ContactBean contact;
    /**
     * pkid : E5303AD1C66811E69FB200163E162ADD
     * contactPkid : E52F4046C66811E69FB200163E162ADD
     * isValid : 1
     * isPrimary : 0
     * itemType : ADDRESS
     * crtTime : 1482206450000
     * crtUser : D7AE70BCBA9211E6834900163E001EAA
     * updTime : 1482206450000
     * updUser : D7AE70BCBA9211E6834900163E001EAA
     * itemSubType : 工作
     * itemValue : e1
     * resvTxt1 : null
     * resvTxt2 : null
     * resvTxt3 : null
     * officeId : 4d792e316a0511e6aa7600163e162add
     */

    private List<AddressesBean> addresses;
    /**
     * pkid : E532CC41C66811E69FB200163E162ADD
     * contactPkid : E52F4046C66811E69FB200163E162ADD
     * isValid : 1
     * isPrimary : 0
     * itemType : TEL
     * crtTime : 1482206450000
     * crtUser : D7AE70BCBA9211E6834900163E001EAA
     * updTime : 1482206450000
     * updUser : D7AE70BCBA9211E6834900163E001EAA
     * itemSubType : 主要
     * itemValue : 12313
     * resvTxt1 : null
     * resvTxt2 : null
     * resvTxt3 : null
     * officeId : 4d792e316a0511e6aa7600163e162add
     */

    private List<TelsBean> tels;
    /**
     * pkid : E53478D6C66811E69FB200163E162ADD
     * contactPkid : E52F4046C66811E69FB200163E162ADD
     * isValid : 1
     * isPrimary : 0
     * itemType : EMAIL
     * crtTime : 1482206450000
     * crtUser : D7AE70BCBA9211E6834900163E001EAA
     * updTime : 1482206450000
     * updUser : D7AE70BCBA9211E6834900163E001EAA
     * itemSubType : 主要
     * itemValue : 23@123.com
     * resvTxt1 : null
     * resvTxt2 : null
     * resvTxt3 : null
     * officeId : 4d792e316a0511e6aa7600163e162add
     */

    private List<MailsBean> mails;
    /**
     * contact : {"pkid":"E52F4046C66811E69FB200163E162ADD","contactType":"P","name":"我还是姓名","abbr":null,"officeId":"4d792e316a0511e6aa7600163e162add","isValid":"1","isHistory":"0","isPotential":"0","isCurrent":"0","isPause":"0","pinyin":"wohuanshixingming,wohaishixingming","pinyinInitial":"whsxm","logoUrl":null,"sex":null,"title":null,"tycId":null,"impression":null,"crtTime":1482206450000,"crtUser":"D7AE70BCBA9211E6834900163E001EAA","updTime":1482206450000,"updUser":"D7AE70BCBA9211E6834900163E001EAA","crtUserName":"王江"}
     * addresses : [{"pkid":"E5303AD1C66811E69FB200163E162ADD","contactPkid":"E52F4046C66811E69FB200163E162ADD","isValid":"1","isPrimary":"0","itemType":"ADDRESS","crtTime":1482206450000,"crtUser":"D7AE70BCBA9211E6834900163E001EAA","updTime":1482206450000,"updUser":"D7AE70BCBA9211E6834900163E001EAA","itemSubType":"工作","itemValue":"e1","resvTxt1":null,"resvTxt2":null,"resvTxt3":null,"officeId":"4d792e316a0511e6aa7600163e162add"},{"pkid":"E531C6E3C66811E69FB200163E162ADD","contactPkid":"E52F4046C66811E69FB200163E162ADD","isValid":"1","isPrimary":"0","itemType":"ADDRESS","crtTime":1482206450000,"crtUser":"D7AE70BCBA9211E6834900163E001EAA","updTime":1482206450000,"updUser":"D7AE70BCBA9211E6834900163E001EAA","itemSubType":"家庭","itemValue":"sdf","resvTxt1":null,"resvTxt2":null,"resvTxt3":null,"officeId":"4d792e316a0511e6aa7600163e162add"}]
     * tels : [{"pkid":"E532CC41C66811E69FB200163E162ADD","contactPkid":"E52F4046C66811E69FB200163E162ADD","isValid":"1","isPrimary":"0","itemType":"TEL","crtTime":1482206450000,"crtUser":"D7AE70BCBA9211E6834900163E001EAA","updTime":1482206450000,"updUser":"D7AE70BCBA9211E6834900163E001EAA","itemSubType":"主要","itemValue":"12313","resvTxt1":null,"resvTxt2":null,"resvTxt3":null,"officeId":"4d792e316a0511e6aa7600163e162add"},{"pkid":"E533D42EC66811E69FB200163E162ADD","contactPkid":"E52F4046C66811E69FB200163E162ADD","isValid":"1","isPrimary":"0","itemType":"TEL","crtTime":1482206450000,"crtUser":"D7AE70BCBA9211E6834900163E001EAA","updTime":1482206450000,"updUser":"D7AE70BCBA9211E6834900163E001EAA","itemSubType":"家庭","itemValue":"weqe","resvTxt1":null,"resvTxt2":null,"resvTxt3":null,"officeId":"4d792e316a0511e6aa7600163e162add"}]
     * mails : [{"pkid":"E53478D6C66811E69FB200163E162ADD","contactPkid":"E52F4046C66811E69FB200163E162ADD","isValid":"1","isPrimary":"0","itemType":"EMAIL","crtTime":1482206450000,"crtUser":"D7AE70BCBA9211E6834900163E001EAA","updTime":1482206450000,"updUser":"D7AE70BCBA9211E6834900163E001EAA","itemSubType":"主要","itemValue":"23@123.com","resvTxt1":null,"resvTxt2":null,"resvTxt3":null,"officeId":"4d792e316a0511e6aa7600163e162add"},{"pkid":"E534DEC7C66811E69FB200163E162ADD","contactPkid":"E52F4046C66811E69FB200163E162ADD","isValid":"1","isPrimary":"0","itemType":"EMAIL","crtTime":1482206450000,"crtUser":"D7AE70BCBA9211E6834900163E001EAA","updTime":1482206450000,"updUser":"D7AE70BCBA9211E6834900163E001EAA","itemSubType":"工作","itemValue":"q3e@123.com","resvTxt1":null,"resvTxt2":null,"resvTxt3":null,"officeId":"4d792e316a0511e6aa7600163e162add"}]
     * dates : []
     * certificates : [{"pkid":"E535A62DC66811E69FB200163E162ADD","contactPkid":"E52F4046C66811E69FB200163E162ADD","isValid":"1","isPrimary":"0","itemType":"CERTIFICATE","crtTime":1482206450000,"crtUser":"D7AE70BCBA9211E6834900163E001EAA","updTime":1482206450000,"updUser":"D7AE70BCBA9211E6834900163E001EAA","itemSubType":"身份证","itemValue":"sdf","resvTxt1":null,"resvTxt2":null,"resvTxt3":null,"officeId":"4d792e316a0511e6aa7600163e162add"},{"pkid":"E536B704C66811E69FB200163E162ADD","contactPkid":"E52F4046C66811E69FB200163E162ADD","isValid":"1","isPrimary":"0","itemType":"CERTIFICATE","crtTime":1482206450000,"crtUser":"D7AE70BCBA9211E6834900163E001EAA","updTime":1482206450000,"updUser":"D7AE70BCBA9211E6834900163E001EAA","itemSubType":"护照","itemValue":"sadf","resvTxt1":null,"resvTxt2":null,"resvTxt3":null,"officeId":"4d792e316a0511e6aa7600163e162add"}]
     * ims : [{"pkid":"E5379414C66811E69FB200163E162ADD","contactPkid":"E52F4046C66811E69FB200163E162ADD","isValid":"1","isPrimary":"0","itemType":"IM","crtTime":1482206450000,"crtUser":"D7AE70BCBA9211E6834900163E001EAA","updTime":1482206450000,"updUser":"D7AE70BCBA9211E6834900163E001EAA","itemSubType":"微信","itemValue":"sadf","resvTxt1":null,"resvTxt2":null,"resvTxt3":null,"officeId":"4d792e316a0511e6aa7600163e162add"},{"pkid":"E537FE5CC66811E69FB200163E162ADD","contactPkid":"E52F4046C66811E69FB200163E162ADD","isValid":"1","isPrimary":"0","itemType":"IM","crtTime":1482206450000,"crtUser":"D7AE70BCBA9211E6834900163E001EAA","updTime":1482206450000,"updUser":"D7AE70BCBA9211E6834900163E001EAA","itemSubType":"QQ","itemValue":"asdf","resvTxt1":null,"resvTxt2":null,"resvTxt3":null,"officeId":"4d792e316a0511e6aa7600163e162add"}]
     * usedNames : [{"pkid":"E5386BD4C66811E69FB200163E162ADD","contactPkid":"E52F4046C66811E69FB200163E162ADD","isValid":"1","isPrimary":"0","itemType":"USERDNAME","crtTime":1482206450000,"crtUser":"D7AE70BCBA9211E6834900163E001EAA","updTime":1482206450000,"updUser":"D7AE70BCBA9211E6834900163E001EAA","itemSubType":"","itemValue":"曾用名","resvTxt1":null,"resvTxt2":null,"resvTxt3":null,"officeId":"4d792e316a0511e6aa7600163e162add"}]
     * companies : [{"pkid":"E5395721C66811E69FB200163E162ADD","contactPkid":"E52F4046C66811E69FB200163E162ADD","isValid":"1","isPrimary":"0","itemType":"COMPANY","crtTime":1482206450000,"crtUser":"D7AE70BCBA9211E6834900163E001EAA","updTime":1482206450000,"updUser":"D7AE70BCBA9211E6834900163E001EAA","itemSubType":null,"itemValue":null,"resvTxt1":null,"resvTxt2":null,"resvTxt3":null,"officeId":"4d792e316a0511e6aa7600163e162add"}]
     * groups : [{"groupId":"62F7E17BA02011E69A3800163E0020D1","groupName":"Alpha"},{"groupId":"10182C0FB2C111E6834900163E001EAA","groupName":"Alpha新橙"}]
     */

    private List<DateBean> dates;
    /**
     * pkid : E535A62DC66811E69FB200163E162ADD
     * contactPkid : E52F4046C66811E69FB200163E162ADD
     * isValid : 1
     * isPrimary : 0
     * itemType : CERTIFICATE
     * crtTime : 1482206450000
     * crtUser : D7AE70BCBA9211E6834900163E001EAA
     * updTime : 1482206450000
     * updUser : D7AE70BCBA9211E6834900163E001EAA
     * itemSubType : 身份证
     * itemValue : sdf
     * resvTxt1 : null
     * resvTxt2 : null
     * resvTxt3 : null
     * officeId : 4d792e316a0511e6aa7600163e162add
     */

    private List<CertificatesBean> certificates;
    /**
     * pkid : E5379414C66811E69FB200163E162ADD
     * contactPkid : E52F4046C66811E69FB200163E162ADD
     * isValid : 1
     * isPrimary : 0
     * itemType : IM
     * crtTime : 1482206450000
     * crtUser : D7AE70BCBA9211E6834900163E001EAA
     * updTime : 1482206450000
     * updUser : D7AE70BCBA9211E6834900163E001EAA
     * itemSubType : 微信
     * itemValue : sadf
     * resvTxt1 : null
     * resvTxt2 : null
     * resvTxt3 : null
     * officeId : 4d792e316a0511e6aa7600163e162add
     */

    private List<ImsBean> ims;
    /**
     * pkid : E5386BD4C66811E69FB200163E162ADD
     * contactPkid : E52F4046C66811E69FB200163E162ADD
     * isValid : 1
     * isPrimary : 0
     * itemType : USERDNAME
     * crtTime : 1482206450000
     * crtUser : D7AE70BCBA9211E6834900163E001EAA
     * updTime : 1482206450000
     * updUser : D7AE70BCBA9211E6834900163E001EAA
     * itemSubType :
     * itemValue : 曾用名
     * resvTxt1 : null
     * resvTxt2 : null
     * resvTxt3 : null
     * officeId : 4d792e316a0511e6aa7600163e162add
     */

    private List<UsedNamesBean> usedNames;
    /**
     * pkid : E5395721C66811E69FB200163E162ADD
     * contactPkid : E52F4046C66811E69FB200163E162ADD
     * isValid : 1
     * isPrimary : 0
     * itemType : COMPANY
     * crtTime : 1482206450000
     * crtUser : D7AE70BCBA9211E6834900163E001EAA
     * updTime : 1482206450000
     * updUser : D7AE70BCBA9211E6834900163E001EAA
     * itemSubType : null
     * itemValue : null
     * resvTxt1 : null
     * resvTxt2 : null
     * resvTxt3 : null
     * officeId : 4d792e316a0511e6aa7600163e162add
     */

    private List<CompaniesBean> companies;
    /**
     * groupId : 62F7E17BA02011E69A3800163E0020D1
     * groupName : Alpha
     */

    private List<GroupsBean> groups;

    public CompanyBean company;

    public ContactBean getContact() {
        return contact;
    }

    public void setContact(ContactBean contact) {
        this.contact = contact;
    }

    public List<AddressesBean> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<AddressesBean> addresses) {
        this.addresses = addresses;
    }

    public List<TelsBean> getTels() {
        return tels;
    }

    public void setTels(List<TelsBean> tels) {
        this.tels = tels;
    }

    public List<MailsBean> getMails() {
        return mails;
    }

    public void setMails(List<MailsBean> mails) {
        this.mails = mails;
    }

    public List<DateBean> getDates() {
        return dates;
    }

    public void setDates(List<DateBean> dates) {
        this.dates = dates;
    }

    public List<CertificatesBean> getCertificates() {
        return certificates;
    }

    public void setCertificates(List<CertificatesBean> certificates) {
        this.certificates = certificates;
    }

    public List<ImsBean> getIms() {
        return ims;
    }

    public void setIms(List<ImsBean> ims) {
        this.ims = ims;
    }

    public List<UsedNamesBean> getUsedNames() {
        return usedNames;
    }

    public void setUsedNames(List<UsedNamesBean> usedNames) {
        this.usedNames = usedNames;
    }

    public List<CompaniesBean> getCompanies() {
        return companies;
    }

    public void setCompanies(List<CompaniesBean> companies) {
        this.companies = companies;
    }

    public List<GroupsBean> getGroups() {
        return groups;
    }

    public void setGroups(List<GroupsBean> groups) {
        this.groups = groups;
    }

    public static class ContactBean implements Serializable{
        private String pkid;
        private String contactType;
        private String name;
        private String abbr;
        private String officeId;
        private String isValid;
        private String isHistory;
        private String isPotential;
        private String isCurrent;
        private String isPause;
        private String pinyin;
        private String pinyinInitial;
        private String logoUrl;
        private String sex;
        private String title;
        private String tycId;
        private String impression;
        private long crtTime;
        private String crtUser;
        private long updTime;
        private String updUser;
        private String crtUserName;
        private int isView;

        public String getPkid() {
            return pkid;
        }

        public void setPkid(String pkid) {
            this.pkid = pkid;
        }

        public String getContactType() {
            return contactType;
        }

        public void setContactType(String contactType) {
            this.contactType = contactType;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAbbr() {
            return abbr;
        }

        public void setAbbr(String abbr) {
            this.abbr = abbr;
        }

        public String getOfficeId() {
            return officeId;
        }

        public void setOfficeId(String officeId) {
            this.officeId = officeId;
        }

        public String getIsValid() {
            return isValid;
        }

        public void setIsValid(String isValid) {
            this.isValid = isValid;
        }

        public String getIsHistory() {
            return isHistory;
        }

        public void setIsHistory(String isHistory) {
            this.isHistory = isHistory;
        }

        public String getIsPotential() {
            return isPotential;
        }

        public void setIsPotential(String isPotential) {
            this.isPotential = isPotential;
        }

        public String getIsCurrent() {
            return isCurrent;
        }

        public void setIsCurrent(String isCurrent) {
            this.isCurrent = isCurrent;
        }

        public String getIsPause() {
            return isPause;
        }

        public void setIsPause(String isPause) {
            this.isPause = isPause;
        }

        public String getPinyin() {
            return pinyin;
        }

        public void setPinyin(String pinyin) {
            this.pinyin = pinyin;
        }

        public String getPinyinInitial() {
            return pinyinInitial;
        }

        public void setPinyinInitial(String pinyinInitial) {
            this.pinyinInitial = pinyinInitial;
        }

        public String getLogoUrl() {
            return logoUrl;
        }

        public void setLogoUrl(String logoUrl) {
            this.logoUrl = logoUrl;
        }

        public String getSex() {
            return sex;
        }

        public void setSex(String sex) {
            this.sex = sex;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getTycId() {
            return tycId;
        }

        public void setTycId(String tycId) {
            this.tycId = tycId;
        }

        public String getImpression() {
            return impression;
        }

        public void setImpression(String impression) {
            this.impression = impression;
        }

        public long getCrtTime() {
            return crtTime;
        }

        public void setCrtTime(long crtTime) {
            this.crtTime = crtTime;
        }

        public String getCrtUser() {
            return crtUser;
        }

        public void setCrtUser(String crtUser) {
            this.crtUser = crtUser;
        }

        public long getUpdTime() {
            return updTime;
        }

        public void setUpdTime(long updTime) {
            this.updTime = updTime;
        }

        public String getUpdUser() {
            return updUser;
        }

        public void setUpdUser(String updUser) {
            this.updUser = updUser;
        }

        public String getCrtUserName() {
            return crtUserName;
        }

        public void setCrtUserName(String crtUserName) {
            this.crtUserName = crtUserName;
        }

        public int getIsView() {
            return isView;
        }

        public void setIsView(int isView) {
            this.isView = isView;
        }
    }

    public static class AddressesBean implements Serializable{
        private String pkid;
        private String contactPkid;
        private String isValid;
        private String isPrimary;
        private String itemType;
        private long crtTime;
        private String crtUser;
        private long updTime;
        private String updUser;
        private String itemSubType;
        private String itemValue;
        private Object resvTxt1;
        private Object resvTxt2;
        private Object resvTxt3;
        private String officeId;

        public String getPkid() {
            return pkid;
        }

        public void setPkid(String pkid) {
            this.pkid = pkid;
        }

        public String getContactPkid() {
            return contactPkid;
        }

        public void setContactPkid(String contactPkid) {
            this.contactPkid = contactPkid;
        }

        public String getIsValid() {
            return isValid;
        }

        public void setIsValid(String isValid) {
            this.isValid = isValid;
        }

        public String getIsPrimary() {
            return isPrimary;
        }

        public void setIsPrimary(String isPrimary) {
            this.isPrimary = isPrimary;
        }

        public String getItemType() {
            return itemType;
        }

        public void setItemType(String itemType) {
            this.itemType = itemType;
        }

        public long getCrtTime() {
            return crtTime;
        }

        public void setCrtTime(long crtTime) {
            this.crtTime = crtTime;
        }

        public String getCrtUser() {
            return crtUser;
        }

        public void setCrtUser(String crtUser) {
            this.crtUser = crtUser;
        }

        public long getUpdTime() {
            return updTime;
        }

        public void setUpdTime(long updTime) {
            this.updTime = updTime;
        }

        public String getUpdUser() {
            return updUser;
        }

        public void setUpdUser(String updUser) {
            this.updUser = updUser;
        }

        public String getItemSubType() {
            return itemSubType;
        }

        public void setItemSubType(String itemSubType) {
            this.itemSubType = itemSubType;
        }

        public String getItemValue() {
            return itemValue;
        }

        public void setItemValue(String itemValue) {
            this.itemValue = itemValue;
        }

        public Object getResvTxt1() {
            return resvTxt1;
        }

        public void setResvTxt1(Object resvTxt1) {
            this.resvTxt1 = resvTxt1;
        }

        public Object getResvTxt2() {
            return resvTxt2;
        }

        public void setResvTxt2(Object resvTxt2) {
            this.resvTxt2 = resvTxt2;
        }

        public Object getResvTxt3() {
            return resvTxt3;
        }

        public void setResvTxt3(Object resvTxt3) {
            this.resvTxt3 = resvTxt3;
        }

        public String getOfficeId() {
            return officeId;
        }

        public void setOfficeId(String officeId) {
            this.officeId = officeId;
        }
    }

    public static class TelsBean implements Serializable{
        private String pkid;
        private String contactPkid;
        private String isValid;
        private String isPrimary;
        private String itemType;
        private long crtTime;
        private String crtUser;
        private long updTime;
        private String updUser;
        private String itemSubType;
        private String itemValue;
        private Object resvTxt1;
        private Object resvTxt2;
        private Object resvTxt3;
        private String officeId;

        public String getPkid() {
            return pkid;
        }

        public void setPkid(String pkid) {
            this.pkid = pkid;
        }

        public String getContactPkid() {
            return contactPkid;
        }

        public void setContactPkid(String contactPkid) {
            this.contactPkid = contactPkid;
        }

        public String getIsValid() {
            return isValid;
        }

        public void setIsValid(String isValid) {
            this.isValid = isValid;
        }

        public String getIsPrimary() {
            return isPrimary;
        }

        public void setIsPrimary(String isPrimary) {
            this.isPrimary = isPrimary;
        }

        public String getItemType() {
            return itemType;
        }

        public void setItemType(String itemType) {
            this.itemType = itemType;
        }

        public long getCrtTime() {
            return crtTime;
        }

        public void setCrtTime(long crtTime) {
            this.crtTime = crtTime;
        }

        public String getCrtUser() {
            return crtUser;
        }

        public void setCrtUser(String crtUser) {
            this.crtUser = crtUser;
        }

        public long getUpdTime() {
            return updTime;
        }

        public void setUpdTime(long updTime) {
            this.updTime = updTime;
        }

        public String getUpdUser() {
            return updUser;
        }

        public void setUpdUser(String updUser) {
            this.updUser = updUser;
        }

        public String getItemSubType() {
            return itemSubType;
        }

        public void setItemSubType(String itemSubType) {
            this.itemSubType = itemSubType;
        }

        public String getItemValue() {
            return itemValue;
        }

        public void setItemValue(String itemValue) {
            this.itemValue = itemValue;
        }

        public Object getResvTxt1() {
            return resvTxt1;
        }

        public void setResvTxt1(Object resvTxt1) {
            this.resvTxt1 = resvTxt1;
        }

        public Object getResvTxt2() {
            return resvTxt2;
        }

        public void setResvTxt2(Object resvTxt2) {
            this.resvTxt2 = resvTxt2;
        }

        public Object getResvTxt3() {
            return resvTxt3;
        }

        public void setResvTxt3(Object resvTxt3) {
            this.resvTxt3 = resvTxt3;
        }

        public String getOfficeId() {
            return officeId;
        }

        public void setOfficeId(String officeId) {
            this.officeId = officeId;
        }
    }

    public static class MailsBean implements Serializable{
        private String pkid;
        private String contactPkid;
        private String isValid;
        private String isPrimary;
        private String itemType;
        private long crtTime;
        private String crtUser;
        private long updTime;
        private String updUser;
        private String itemSubType;
        private String itemValue;
        private Object resvTxt1;
        private Object resvTxt2;
        private Object resvTxt3;
        private String officeId;

        public String getPkid() {
            return pkid;
        }

        public void setPkid(String pkid) {
            this.pkid = pkid;
        }

        public String getContactPkid() {
            return contactPkid;
        }

        public void setContactPkid(String contactPkid) {
            this.contactPkid = contactPkid;
        }

        public String getIsValid() {
            return isValid;
        }

        public void setIsValid(String isValid) {
            this.isValid = isValid;
        }

        public String getIsPrimary() {
            return isPrimary;
        }

        public void setIsPrimary(String isPrimary) {
            this.isPrimary = isPrimary;
        }

        public String getItemType() {
            return itemType;
        }

        public void setItemType(String itemType) {
            this.itemType = itemType;
        }

        public long getCrtTime() {
            return crtTime;
        }

        public void setCrtTime(long crtTime) {
            this.crtTime = crtTime;
        }

        public String getCrtUser() {
            return crtUser;
        }

        public void setCrtUser(String crtUser) {
            this.crtUser = crtUser;
        }

        public long getUpdTime() {
            return updTime;
        }

        public void setUpdTime(long updTime) {
            this.updTime = updTime;
        }

        public String getUpdUser() {
            return updUser;
        }

        public void setUpdUser(String updUser) {
            this.updUser = updUser;
        }

        public String getItemSubType() {
            return itemSubType;
        }

        public void setItemSubType(String itemSubType) {
            this.itemSubType = itemSubType;
        }

        public String getItemValue() {
            return itemValue;
        }

        public void setItemValue(String itemValue) {
            this.itemValue = itemValue;
        }

        public Object getResvTxt1() {
            return resvTxt1;
        }

        public void setResvTxt1(Object resvTxt1) {
            this.resvTxt1 = resvTxt1;
        }

        public Object getResvTxt2() {
            return resvTxt2;
        }

        public void setResvTxt2(Object resvTxt2) {
            this.resvTxt2 = resvTxt2;
        }

        public Object getResvTxt3() {
            return resvTxt3;
        }

        public void setResvTxt3(Object resvTxt3) {
            this.resvTxt3 = resvTxt3;
        }

        public String getOfficeId() {
            return officeId;
        }

        public void setOfficeId(String officeId) {
            this.officeId = officeId;
        }
    }

    public static class CertificatesBean implements Serializable{
        private String pkid;
        private String contactPkid;
        private String isValid;
        private String isPrimary;
        private String itemType;
        private long crtTime;
        private String crtUser;
        private long updTime;
        private String updUser;
        private String itemSubType;
        private String itemValue;
        private Object resvTxt1;
        private Object resvTxt2;
        private Object resvTxt3;
        private String officeId;

        public String getPkid() {
            return pkid;
        }

        public void setPkid(String pkid) {
            this.pkid = pkid;
        }

        public String getContactPkid() {
            return contactPkid;
        }

        public void setContactPkid(String contactPkid) {
            this.contactPkid = contactPkid;
        }

        public String getIsValid() {
            return isValid;
        }

        public void setIsValid(String isValid) {
            this.isValid = isValid;
        }

        public String getIsPrimary() {
            return isPrimary;
        }

        public void setIsPrimary(String isPrimary) {
            this.isPrimary = isPrimary;
        }

        public String getItemType() {
            return itemType;
        }

        public void setItemType(String itemType) {
            this.itemType = itemType;
        }

        public long getCrtTime() {
            return crtTime;
        }

        public void setCrtTime(long crtTime) {
            this.crtTime = crtTime;
        }

        public String getCrtUser() {
            return crtUser;
        }

        public void setCrtUser(String crtUser) {
            this.crtUser = crtUser;
        }

        public long getUpdTime() {
            return updTime;
        }

        public void setUpdTime(long updTime) {
            this.updTime = updTime;
        }

        public String getUpdUser() {
            return updUser;
        }

        public void setUpdUser(String updUser) {
            this.updUser = updUser;
        }

        public String getItemSubType() {
            return itemSubType;
        }

        public void setItemSubType(String itemSubType) {
            this.itemSubType = itemSubType;
        }

        public String getItemValue() {
            return itemValue;
        }

        public void setItemValue(String itemValue) {
            this.itemValue = itemValue;
        }

        public Object getResvTxt1() {
            return resvTxt1;
        }

        public void setResvTxt1(Object resvTxt1) {
            this.resvTxt1 = resvTxt1;
        }

        public Object getResvTxt2() {
            return resvTxt2;
        }

        public void setResvTxt2(Object resvTxt2) {
            this.resvTxt2 = resvTxt2;
        }

        public Object getResvTxt3() {
            return resvTxt3;
        }

        public void setResvTxt3(Object resvTxt3) {
            this.resvTxt3 = resvTxt3;
        }

        public String getOfficeId() {
            return officeId;
        }

        public void setOfficeId(String officeId) {
            this.officeId = officeId;
        }
    }

    public static class ImsBean implements Serializable{
        private String pkid;
        private String contactPkid;
        private String isValid;
        private String isPrimary;
        private String itemType;
        private long crtTime;
        private String crtUser;
        private long updTime;
        private String updUser;
        private String itemSubType;
        private String itemValue;
        private Object resvTxt1;
        private Object resvTxt2;
        private Object resvTxt3;
        private String officeId;

        public String getPkid() {
            return pkid;
        }

        public void setPkid(String pkid) {
            this.pkid = pkid;
        }

        public String getContactPkid() {
            return contactPkid;
        }

        public void setContactPkid(String contactPkid) {
            this.contactPkid = contactPkid;
        }

        public String getIsValid() {
            return isValid;
        }

        public void setIsValid(String isValid) {
            this.isValid = isValid;
        }

        public String getIsPrimary() {
            return isPrimary;
        }

        public void setIsPrimary(String isPrimary) {
            this.isPrimary = isPrimary;
        }

        public String getItemType() {
            return itemType;
        }

        public void setItemType(String itemType) {
            this.itemType = itemType;
        }

        public long getCrtTime() {
            return crtTime;
        }

        public void setCrtTime(long crtTime) {
            this.crtTime = crtTime;
        }

        public String getCrtUser() {
            return crtUser;
        }

        public void setCrtUser(String crtUser) {
            this.crtUser = crtUser;
        }

        public long getUpdTime() {
            return updTime;
        }

        public void setUpdTime(long updTime) {
            this.updTime = updTime;
        }

        public String getUpdUser() {
            return updUser;
        }

        public void setUpdUser(String updUser) {
            this.updUser = updUser;
        }

        public String getItemSubType() {
            return itemSubType;
        }

        public void setItemSubType(String itemSubType) {
            this.itemSubType = itemSubType;
        }

        public String getItemValue() {
            return itemValue;
        }

        public void setItemValue(String itemValue) {
            this.itemValue = itemValue;
        }

        public Object getResvTxt1() {
            return resvTxt1;
        }

        public void setResvTxt1(Object resvTxt1) {
            this.resvTxt1 = resvTxt1;
        }

        public Object getResvTxt2() {
            return resvTxt2;
        }

        public void setResvTxt2(Object resvTxt2) {
            this.resvTxt2 = resvTxt2;
        }

        public Object getResvTxt3() {
            return resvTxt3;
        }

        public void setResvTxt3(Object resvTxt3) {
            this.resvTxt3 = resvTxt3;
        }

        public String getOfficeId() {
            return officeId;
        }

        public void setOfficeId(String officeId) {
            this.officeId = officeId;
        }
    }

    public static class UsedNamesBean implements Serializable{
        private String pkid;
        private String contactPkid;
        private String isValid;
        private String isPrimary;
        private String itemType;
        private long crtTime;
        private String crtUser;
        private long updTime;
        private String updUser;
        private String itemSubType;
        private String itemValue;
        private Object resvTxt1;
        private Object resvTxt2;
        private Object resvTxt3;
        private String officeId;

        public String getPkid() {
            return pkid;
        }

        public void setPkid(String pkid) {
            this.pkid = pkid;
        }

        public String getContactPkid() {
            return contactPkid;
        }

        public void setContactPkid(String contactPkid) {
            this.contactPkid = contactPkid;
        }

        public String getIsValid() {
            return isValid;
        }

        public void setIsValid(String isValid) {
            this.isValid = isValid;
        }

        public String getIsPrimary() {
            return isPrimary;
        }

        public void setIsPrimary(String isPrimary) {
            this.isPrimary = isPrimary;
        }

        public String getItemType() {
            return itemType;
        }

        public void setItemType(String itemType) {
            this.itemType = itemType;
        }

        public long getCrtTime() {
            return crtTime;
        }

        public void setCrtTime(long crtTime) {
            this.crtTime = crtTime;
        }

        public String getCrtUser() {
            return crtUser;
        }

        public void setCrtUser(String crtUser) {
            this.crtUser = crtUser;
        }

        public long getUpdTime() {
            return updTime;
        }

        public void setUpdTime(long updTime) {
            this.updTime = updTime;
        }

        public String getUpdUser() {
            return updUser;
        }

        public void setUpdUser(String updUser) {
            this.updUser = updUser;
        }

        public String getItemSubType() {
            return itemSubType;
        }

        public void setItemSubType(String itemSubType) {
            this.itemSubType = itemSubType;
        }

        public String getItemValue() {
            return itemValue;
        }

        public void setItemValue(String itemValue) {
            this.itemValue = itemValue;
        }

        public Object getResvTxt1() {
            return resvTxt1;
        }

        public void setResvTxt1(Object resvTxt1) {
            this.resvTxt1 = resvTxt1;
        }

        public Object getResvTxt2() {
            return resvTxt2;
        }

        public void setResvTxt2(Object resvTxt2) {
            this.resvTxt2 = resvTxt2;
        }

        public Object getResvTxt3() {
            return resvTxt3;
        }

        public void setResvTxt3(Object resvTxt3) {
            this.resvTxt3 = resvTxt3;
        }

        public String getOfficeId() {
            return officeId;
        }

        public void setOfficeId(String officeId) {
            this.officeId = officeId;
        }
    }

    public static class CompaniesBean implements Serializable{
        private String pkid;
        private String contactPkid;
        private String isValid;
        private String isPrimary;
        private String itemType;
        private long crtTime;
        private String crtUser;
        private long updTime;
        private String updUser;
        private Object itemSubType;
        private Object itemValue;
        private Object resvTxt1;
        private Object resvTxt2;
        private Object resvTxt3;
        private String officeId;

        public String getPkid() {
            return pkid;
        }

        public void setPkid(String pkid) {
            this.pkid = pkid;
        }

        public String getContactPkid() {
            return contactPkid;
        }

        public void setContactPkid(String contactPkid) {
            this.contactPkid = contactPkid;
        }

        public String getIsValid() {
            return isValid;
        }

        public void setIsValid(String isValid) {
            this.isValid = isValid;
        }

        public String getIsPrimary() {
            return isPrimary;
        }

        public void setIsPrimary(String isPrimary) {
            this.isPrimary = isPrimary;
        }

        public String getItemType() {
            return itemType;
        }

        public void setItemType(String itemType) {
            this.itemType = itemType;
        }

        public long getCrtTime() {
            return crtTime;
        }

        public void setCrtTime(long crtTime) {
            this.crtTime = crtTime;
        }

        public String getCrtUser() {
            return crtUser;
        }

        public void setCrtUser(String crtUser) {
            this.crtUser = crtUser;
        }

        public long getUpdTime() {
            return updTime;
        }

        public void setUpdTime(long updTime) {
            this.updTime = updTime;
        }

        public String getUpdUser() {
            return updUser;
        }

        public void setUpdUser(String updUser) {
            this.updUser = updUser;
        }

        public Object getItemSubType() {
            return itemSubType;
        }

        public void setItemSubType(Object itemSubType) {
            this.itemSubType = itemSubType;
        }

        public Object getItemValue() {
            return itemValue;
        }

        public void setItemValue(Object itemValue) {
            this.itemValue = itemValue;
        }

        public Object getResvTxt1() {
            return resvTxt1;
        }

        public void setResvTxt1(Object resvTxt1) {
            this.resvTxt1 = resvTxt1;
        }

        public Object getResvTxt2() {
            return resvTxt2;
        }

        public void setResvTxt2(Object resvTxt2) {
            this.resvTxt2 = resvTxt2;
        }

        public Object getResvTxt3() {
            return resvTxt3;
        }

        public void setResvTxt3(Object resvTxt3) {
            this.resvTxt3 = resvTxt3;
        }

        public String getOfficeId() {
            return officeId;
        }

        public void setOfficeId(String officeId) {
            this.officeId = officeId;
        }
    }

    public static class GroupsBean implements Serializable{
        private String groupId;
        private String groupName;

        public String getGroupId() {
            return groupId;
        }

        public void setGroupId(String groupId) {
            this.groupId = groupId;
        }

        public String getGroupName() {
            return groupName;
        }

        public void setGroupName(String groupName) {
            this.groupName = groupName;
        }
    }

    public static class DateBean implements Serializable{
        private String pkid;
        private String contactPkid;
        private String isValid;
        private String isPrimary;
        private String itemType;
        private long crtTime;
        private String crtUser;
        private long updTime;
        private String updUser;
        private String itemSubType;
        private String itemValue;
        private Object resvTxt1;
        private Object resvTxt2;
        private Object resvTxt3;
        private String officeId;

        public String getPkid() {
            return pkid;
        }

        public void setPkid(String pkid) {
            this.pkid = pkid;
        }

        public String getContactPkid() {
            return contactPkid;
        }

        public void setContactPkid(String contactPkid) {
            this.contactPkid = contactPkid;
        }

        public String getIsValid() {
            return isValid;
        }

        public void setIsValid(String isValid) {
            this.isValid = isValid;
        }

        public String getIsPrimary() {
            return isPrimary;
        }

        public void setIsPrimary(String isPrimary) {
            this.isPrimary = isPrimary;
        }

        public String getItemType() {
            return itemType;
        }

        public void setItemType(String itemType) {
            this.itemType = itemType;
        }

        public long getCrtTime() {
            return crtTime;
        }

        public void setCrtTime(long crtTime) {
            this.crtTime = crtTime;
        }

        public String getCrtUser() {
            return crtUser;
        }

        public void setCrtUser(String crtUser) {
            this.crtUser = crtUser;
        }

        public long getUpdTime() {
            return updTime;
        }

        public void setUpdTime(long updTime) {
            this.updTime = updTime;
        }

        public String getUpdUser() {
            return updUser;
        }

        public void setUpdUser(String updUser) {
            this.updUser = updUser;
        }

        public String getItemSubType() {
            return itemSubType;
        }

        public void setItemSubType(String itemSubType) {
            this.itemSubType = itemSubType;
        }

        public String getItemValue() {
            return itemValue;
        }

        public void setItemValue(String itemValue) {
            this.itemValue = itemValue;
        }

        public Object getResvTxt1() {
            return resvTxt1;
        }

        public void setResvTxt1(Object resvTxt1) {
            this.resvTxt1 = resvTxt1;
        }

        public Object getResvTxt2() {
            return resvTxt2;
        }

        public void setResvTxt2(Object resvTxt2) {
            this.resvTxt2 = resvTxt2;
        }

        public Object getResvTxt3() {
            return resvTxt3;
        }

        public void setResvTxt3(Object resvTxt3) {
            this.resvTxt3 = resvTxt3;
        }

        public String getOfficeId() {
            return officeId;
        }

        public void setOfficeId(String officeId) {
            this.officeId = officeId;
        }
    }

    public static class CompanyBean implements Serializable{
        public ContactBean contact;
    }

}
