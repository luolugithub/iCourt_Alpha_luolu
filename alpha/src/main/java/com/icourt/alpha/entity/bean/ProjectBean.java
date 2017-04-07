package com.icourt.alpha.entity.bean;

import java.io.Serializable;
import java.util.List;

/**
 * @data 创建时间:16/12/1
 *
 * @author 创建人:lu.zhao
 *
 * 项目
 */

public class ProjectBean implements Serializable {


    /**
     * pkId : 5EC208BAB0D311E6992300163E162ADD
     * name : 110902个人pppp等与北京合众思壮科技股份有限公司等劳动争议、人事争议一审
     * status : 0
     * pendingDate : 1479835929000
     * openDate : 1479835922000
     * closeDate : 1479835924000
     * terminationDate : 1479835926000
     * userId : CABE921084B111E6992300163E162ADD
     * crtTime : 1479833255000
     * updTime : 1479835929000
     * updCde : CABE921084B111E6992300163E162ADD
     * matterType : 0
     * matterSubType : 74502C15AFA111E6992300163E162ADD
     * matterCase : 01060
     * caseProcess : 1
     * matterNumber : null
     * competentCourt : null
     * beginDate : null
     * endDate : null
     * lawField : null
     * matterTypeName : 争议解决
     * matterCaseName : 劳动争议、人事争议
     * caseProcessName : 一审
     * lawFieldName : null
     * responsibleAttorneys : [{"attorneyPkid":"2","attorneyName":"测试账号","attorneyPic":"http://wx.qlogo.cn/mmopen/TLV402oJicT3zwHrmhjpvtaQ0g6g1icb8JTXu0BFu0QlQjkKQBiaGmiabMhB05ZyJiayMfIBRsV440iao3ACcB05ibN73JRaGawRJLD/0"}]
     * assistAttorneys : [{"attorneyPkid":"CABE921084B111E6992300163E162ADD","attorneyName":"克维","attorneyPic":"http://wx.qlogo.cn/mmopen/Q3auHgzwzM5LZ3RYldDOBiamerpEGhrAcS3xicico73ODyfh7MeEtoiaicP4ibiaejQTv2dibd18lqaLHBFf6CZ0CA4jUtibnXbybkYl50GlfQZYPFH4/0"}]
     * originatingAttorneys : []
     * otherAttorneys : [{"attorneyPkid":"500E05696D2411E6A5C200163E0020D1","attorneyName":"曹磊","attorneyPic":null},{"attorneyPkid":"2B3D466B6A1A11E6AA7600163E162ADD","attorneyName":"陈俊杰","attorneyPic":null},{"attorneyPkid":"8CCC667D824C11E6992300163E162ADD","attorneyName":"胡关荣","attorneyPic":"http://wx.qlogo.cn/mmopen/ajNVdqHZLLAHjU7eRmeopKgODiawudjQZBxQicHRic73rhocDkPpeWPCeziaQ8xVw9rgs7ghDpQ3G8SiciceEEE7BtcQ/0"},{"attorneyPkid":"CA4076029E5111E6992300163E162ADD","attorneyName":"小火龙龙龙龙龙","attorneyPic":null},{"attorneyPkid":"5064AC65AFC611E6992300163E162ADD","attorneyName":"赵潞潞","attorneyPic":"http://wx.qlogo.cn/mmopen/SHnMujzj2v8VXia4lzCEWl3AX3bqibjAWFArEjibEmq92wpHD2YKDPNNmM4zVww3t3e9EsXfZlyRhQricxib50nJnrSQeqgR1uNJt/0"}]
     * clients : [{"contactPkid":"76FAC55CA69011E6992300163E162ADD","contactName":"110902个人pppp","customerPosition":"1","customerPositionName":"原告人"}]
     * litigants : [{"contactPkid":"89097C8AA95711E6992300163E162ADD","contactName":"北京合众思壮科技股份有限公司","customerPosition":"3","customerPositionName":"第三人"},{"contactPkid":"64D014AC858311E6992300163E162ADD","contactName":"乐视投资管理（北京）有限公司","customerPosition":"3","customerPositionName":"第三人"}]
     * judges : null
     * clerks : null
     * arbitrators : null
     * secretaries : null
     * isView : 0
     * permission : null
     */

    private String pkId;
    private String name;
    private String status;
    private long pendingDate;
    private long openDate;
    private long closeDate;
    private long terminationDate;
    private String userId;
    private long crtTime;
    private long updTime;
    private String updCde;
    private String matterType;
    private String matterSubType;
    private String matterCase;
    private String caseProcess;
    private Object matterNumber;
    private Object competentCourt;
    private Object beginDate;
    private Object endDate;
    private Object lawField;
    private String matterTypeName;
    private String matterCaseName;
    private String caseProcessName;
    private Object lawFieldName;
    private Object judges;
    private Object clerks;
    private Object arbitrators;
    private Object secretaries;
    private String isView;
    private Object permission;
    /**
     * attorneyPkid : 2
     * attorneyName : 测试账号
     * attorneyPic : http://wx.qlogo.cn/mmopen/TLV402oJicT3zwHrmhjpvtaQ0g6g1icb8JTXu0BFu0QlQjkKQBiaGmiabMhB05ZyJiayMfIBRsV440iao3ACcB05ibN73JRaGawRJLD/0
     */

    private List<ResponsibleAttorneysBean> responsibleAttorneys;
    /**
     * attorneyPkid : CABE921084B111E6992300163E162ADD
     * attorneyName : 克维
     * attorneyPic : http://wx.qlogo.cn/mmopen/Q3auHgzwzM5LZ3RYldDOBiamerpEGhrAcS3xicico73ODyfh7MeEtoiaicP4ibiaejQTv2dibd18lqaLHBFf6CZ0CA4jUtibnXbybkYl50GlfQZYPFH4/0
     */

    private List<AssistAttorneysBean> assistAttorneys;
    private List<?> originatingAttorneys;
    /**
     * attorneyPkid : 500E05696D2411E6A5C200163E0020D1
     * attorneyName : 曹磊
     * attorneyPic : null
     */

    private List<OtherAttorneysBean> otherAttorneys;
    /**
     * contactPkid : 76FAC55CA69011E6992300163E162ADD
     * contactName : 110902个人pppp
     * customerPosition : 1
     * customerPositionName : 原告人
     */

    private List<ClientsBean> clients;
    /**
     * contactPkid : 89097C8AA95711E6992300163E162ADD
     * contactName : 北京合众思壮科技股份有限公司
     * customerPosition : 3
     * customerPositionName : 第三人
     */

    private List<LitigantsBean> litigants;

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getPendingDate() {
        return pendingDate;
    }

    public void setPendingDate(long pendingDate) {
        this.pendingDate = pendingDate;
    }

    public long getOpenDate() {
        return openDate;
    }

    public void setOpenDate(long openDate) {
        this.openDate = openDate;
    }

    public long getCloseDate() {
        return closeDate;
    }

    public void setCloseDate(long closeDate) {
        this.closeDate = closeDate;
    }

    public long getTerminationDate() {
        return terminationDate;
    }

    public void setTerminationDate(long terminationDate) {
        this.terminationDate = terminationDate;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public long getCrtTime() {
        return crtTime;
    }

    public void setCrtTime(long crtTime) {
        this.crtTime = crtTime;
    }

    public long getUpdTime() {
        return updTime;
    }

    public void setUpdTime(long updTime) {
        this.updTime = updTime;
    }

    public String getUpdCde() {
        return updCde;
    }

    public void setUpdCde(String updCde) {
        this.updCde = updCde;
    }

    public String getMatterType() {
        return matterType;
    }

    public void setMatterType(String matterType) {
        this.matterType = matterType;
    }

    public String getMatterSubType() {
        return matterSubType;
    }

    public void setMatterSubType(String matterSubType) {
        this.matterSubType = matterSubType;
    }

    public String getMatterCase() {
        return matterCase;
    }

    public void setMatterCase(String matterCase) {
        this.matterCase = matterCase;
    }

    public String getCaseProcess() {
        return caseProcess;
    }

    public void setCaseProcess(String caseProcess) {
        this.caseProcess = caseProcess;
    }

    public Object getMatterNumber() {
        return matterNumber;
    }

    public void setMatterNumber(Object matterNumber) {
        this.matterNumber = matterNumber;
    }

    public Object getCompetentCourt() {
        return competentCourt;
    }

    public void setCompetentCourt(Object competentCourt) {
        this.competentCourt = competentCourt;
    }

    public Object getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(Object beginDate) {
        this.beginDate = beginDate;
    }

    public Object getEndDate() {
        return endDate;
    }

    public void setEndDate(Object endDate) {
        this.endDate = endDate;
    }

    public Object getLawField() {
        return lawField;
    }

    public void setLawField(Object lawField) {
        this.lawField = lawField;
    }

    public String getMatterTypeName() {
        return matterTypeName;
    }

    public void setMatterTypeName(String matterTypeName) {
        this.matterTypeName = matterTypeName;
    }

    public String getMatterCaseName() {
        return matterCaseName;
    }

    public void setMatterCaseName(String matterCaseName) {
        this.matterCaseName = matterCaseName;
    }

    public String getCaseProcessName() {
        return caseProcessName;
    }

    public void setCaseProcessName(String caseProcessName) {
        this.caseProcessName = caseProcessName;
    }

    public Object getLawFieldName() {
        return lawFieldName;
    }

    public void setLawFieldName(Object lawFieldName) {
        this.lawFieldName = lawFieldName;
    }

    public Object getJudges() {
        return judges;
    }

    public void setJudges(Object judges) {
        this.judges = judges;
    }

    public Object getClerks() {
        return clerks;
    }

    public void setClerks(Object clerks) {
        this.clerks = clerks;
    }

    public Object getArbitrators() {
        return arbitrators;
    }

    public void setArbitrators(Object arbitrators) {
        this.arbitrators = arbitrators;
    }

    public Object getSecretaries() {
        return secretaries;
    }

    public void setSecretaries(Object secretaries) {
        this.secretaries = secretaries;
    }

    public String getIsView() {
        return isView;
    }

    public void setIsView(String isView) {
        this.isView = isView;
    }

    public Object getPermission() {
        return permission;
    }

    public void setPermission(Object permission) {
        this.permission = permission;
    }

    public List<ResponsibleAttorneysBean> getResponsibleAttorneys() {
        return responsibleAttorneys;
    }

    public void setResponsibleAttorneys(List<ResponsibleAttorneysBean> responsibleAttorneys) {
        this.responsibleAttorneys = responsibleAttorneys;
    }

    public List<AssistAttorneysBean> getAssistAttorneys() {
        return assistAttorneys;
    }

    public void setAssistAttorneys(List<AssistAttorneysBean> assistAttorneys) {
        this.assistAttorneys = assistAttorneys;
    }

    public List<?> getOriginatingAttorneys() {
        return originatingAttorneys;
    }

    public void setOriginatingAttorneys(List<?> originatingAttorneys) {
        this.originatingAttorneys = originatingAttorneys;
    }

    public List<OtherAttorneysBean> getOtherAttorneys() {
        return otherAttorneys;
    }

    public void setOtherAttorneys(List<OtherAttorneysBean> otherAttorneys) {
        this.otherAttorneys = otherAttorneys;
    }

    public List<ClientsBean> getClients() {
        return clients;
    }

    public void setClients(List<ClientsBean> clients) {
        this.clients = clients;
    }

    public List<LitigantsBean> getLitigants() {
        return litigants;
    }

    public void setLitigants(List<LitigantsBean> litigants) {
        this.litigants = litigants;
    }

    public static class ResponsibleAttorneysBean {
        private String attorneyPkid;
        private String attorneyName;
        private String attorneyPic;

        public String getAttorneyPkid() {
            return attorneyPkid;
        }

        public void setAttorneyPkid(String attorneyPkid) {
            this.attorneyPkid = attorneyPkid;
        }

        public String getAttorneyName() {
            return attorneyName;
        }

        public void setAttorneyName(String attorneyName) {
            this.attorneyName = attorneyName;
        }

        public String getAttorneyPic() {
            return attorneyPic;
        }

        public void setAttorneyPic(String attorneyPic) {
            this.attorneyPic = attorneyPic;
        }
    }

    public static class AssistAttorneysBean {
        private String attorneyPkid;
        private String attorneyName;
        private String attorneyPic;

        public String getAttorneyPkid() {
            return attorneyPkid;
        }

        public void setAttorneyPkid(String attorneyPkid) {
            this.attorneyPkid = attorneyPkid;
        }

        public String getAttorneyName() {
            return attorneyName;
        }

        public void setAttorneyName(String attorneyName) {
            this.attorneyName = attorneyName;
        }

        public String getAttorneyPic() {
            return attorneyPic;
        }

        public void setAttorneyPic(String attorneyPic) {
            this.attorneyPic = attorneyPic;
        }
    }

    public static class OtherAttorneysBean {
        private String attorneyPkid;
        private String attorneyName;
        private Object attorneyPic;

        public String getAttorneyPkid() {
            return attorneyPkid;
        }

        public void setAttorneyPkid(String attorneyPkid) {
            this.attorneyPkid = attorneyPkid;
        }

        public String getAttorneyName() {
            return attorneyName;
        }

        public void setAttorneyName(String attorneyName) {
            this.attorneyName = attorneyName;
        }

        public Object getAttorneyPic() {
            return attorneyPic;
        }

        public void setAttorneyPic(Object attorneyPic) {
            this.attorneyPic = attorneyPic;
        }
    }

    public static class ClientsBean {
        private String contactPkid;
        private String contactName;
        private String customerPosition;
        private String customerPositionName;

        public String getContactPkid() {
            return contactPkid;
        }

        public void setContactPkid(String contactPkid) {
            this.contactPkid = contactPkid;
        }

        public String getContactName() {
            return contactName;
        }

        public void setContactName(String contactName) {
            this.contactName = contactName;
        }

        public String getCustomerPosition() {
            return customerPosition;
        }

        public void setCustomerPosition(String customerPosition) {
            this.customerPosition = customerPosition;
        }

        public String getCustomerPositionName() {
            return customerPositionName;
        }

        public void setCustomerPositionName(String customerPositionName) {
            this.customerPositionName = customerPositionName;
        }
    }

    public static class LitigantsBean {
        private String contactPkid;
        private String contactName;
        private String customerPosition;
        private String customerPositionName;

        public String getContactPkid() {
            return contactPkid;
        }

        public void setContactPkid(String contactPkid) {
            this.contactPkid = contactPkid;
        }

        public String getContactName() {
            return contactName;
        }

        public void setContactName(String contactName) {
            this.contactName = contactName;
        }

        public String getCustomerPosition() {
            return customerPosition;
        }

        public void setCustomerPosition(String customerPosition) {
            this.customerPosition = customerPosition;
        }

        public String getCustomerPositionName() {
            return customerPositionName;
        }

        public void setCustomerPositionName(String customerPositionName) {
            this.customerPositionName = customerPositionName;
        }
    }
}
