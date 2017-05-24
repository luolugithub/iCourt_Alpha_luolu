package com.icourt.alpha.entity.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Description
 * Company Beijing icourt
 * author  lu.zhao  E-mail:zhaolu@icourt.cc
 * date createTime：17/5/3
 * version 2.0.0
 */

public class ProjectDetailEntity implements Serializable {


    /**
     * pkId : 88EE3604C2DA11E69FB200163E162ADD
     * name : #1215非诉2
     * status : 2
     * openDate : null
     * closeDate : null
     * terminationDate : null
     * userId : 06162A12A02111E69A3800163E0020D1
     * userName : 陈克维
     * crtTime : 1481815453000
     * updCde : null
     * updCdeName : null
     * updTime : null
     * matterType : 1
     * matterCase : null
     * caseProcess : null
     * matterNumber : null
     * competentCourt : null
     * beginDate : 1480550400000
     * endDate : 1481932800000
     * lawField : 111
     * matterTypeName : 非诉项目
     * matterCaseName : null
     * caseProcessName : null
     * lawFieldName : null
     * statusName : 进行中
     * responsibleAttorneys : null
     * assistAttorneys : null
     * otherAttorneys : null
     * originatingAttorneys : null
     * attorneys : [{"pkId":"3F08509CC44B11E69FB200163E162ADD","attorneyPkid":"DE9B81AAA02511E69A3800163E0020D1","attorneyName":"明华","attorneyPic":"https://wx.qlogo.cn/mmopen/SHnMujzj2vibXicCcrpIAhxXjpzNkFTtLWLwpP6Q3IxIRCSWrCEdFqbNBdCpg7rgw7QHwsJxn5icjlkFCfZ5mWrD0PlhjcxgVF1/0","attorneyType":"S"}]
     * clients : [{"contactPkid":"5BE0F059C2DA11E69FB200163E162ADD","contactName":"深圳市大疆创新科技有限公司","customerPosition":"0","customerPositionName":null,"type":"C"}]
     * litigants : [{"contactPkid":"9BD98931C29011E69FB200163E162ADD","contactName":"从AC按时擦擦趣分期我s","customerPosition":null,"customerPositionName":null,"type":"L"}]
     * judges : null
     * clerks : null
     * arbitrators : null
     * secretaries : null
     * myStar : null
     * groups : [{"groupPkid":"62F7E17BA02011E69A3800163E0020D1","name":"Alpha"},{"groupPkid":"B88693D6C25B11E69FB200163E162ADD","name":"#测试吧"}]
     * allSee : null
     * members : [{"userId":"01CEB4E16D2411E6A5C200163E0020D1","userName":"王储","roleId":3594,"roleName":"项目管理员","pic":"https://wx.qlogo.cn/mmopen/EALWeEvfX7tAA2uISLHGrbwqoJO5GpkbfoYIS443frBzMrEoTnWu3VHXWU5DkP6XnMwmGldJq0dnoMpKgGia6VB2kffUjjYdz/0"},{"userId":"06162A12A02111E69A3800163E0020D1","userName":"陈克维","roleId":3594,"roleName":"项目管理员","pic":"https://wx.qlogo.cn/mmopen/TLV402oJicT3zwHrmhjpvta1j5RyGY9K6ictgiazKOUnCIzanHOIKSrBYF4a5ANVRSfxNakwVkqEQP2ujOYHLyILibpcuD4DgaWG/0"},{"userId":"071EE8B1ABF411E6834900163E001EAA","userName":"李鹏浩","roleId":3594,"roleName":"项目管理员","pic":"https://wx.qlogo.cn/mmopen/TLV402oJicT2xcMsHVyrPjlppoYfgNiaBP7vqq3FekEhY2k3b4m0vwpaV5NZKLKD2rfRGSLBTtY8RbdBHZM7ORlUnGBibheBtuN/0"},{"userId":"15C763D0C26C11E69FB200163E162ADD","userName":"赵小潞","roleId":3594,"roleName":"项目管理员","pic":"https://wx.qlogo.cn/mmopen/SHnMujzj2v8VXia4lzCEWl3AX3bqibjAWFArEjibEmq92wpHD2YKDPNNmM4zVww3t3e9EsXfZlyRhQricxib50nJnrSQeqgR1uNJt/0"},{"userId":"18DD6BDABA9211E6834900163E001EAA","userName":"韩琦华","roleId":3594,"roleName":"项目管理员","pic":"https://wx.qlogo.cn/mmopen/SHnMujzj2v8VXia4lzCEWl8gLTWHOJRia6We1GFia6RVibDMiaFyIp0gm1LKMOpf5C5qdicLwsb6SvzlFUoTmBZ0dkOAAPdmKQYS2A/0"},{"userId":"1E942F02A02111E69A3800163E0020D1","userName":"胡关荣","roleId":3594,"roleName":"项目管理员","pic":"https://wx.qlogo.cn/mmopen/ajNVdqHZLLAHjU7eRmeopKgODiawudjQZBxQicHRic73rhocDkPpeWPCeziaQ8xVw9rgs7ghDpQ3G8SiciceEEE7BtcQ/0"},{"userId":"21FD1448B2B611E6834900163E001EAA","userName":"万鸿鹄","roleId":3595,"roleName":"项目成员","pic":"https://wx.qlogo.cn/mmopen/EALWeEvfX7ujQibxpITwOyMoLUSsTczSQRaBtogDHaLF4ubE10kkoapRiaTQrhWpmSWQhhG3C9CUuXZF8LskP6Lh5nPGGDneAg/0"},{"userId":"273DC760A02511E69A3800163E0020D1","userName":"李子鹏","roleId":3594,"roleName":"项目管理员","pic":"https://wx.qlogo.cn/mmopen/EALWeEvfX7sAJgXE5BPbFlEOLHOyVhTyVVTjRHUftuUDUxibvGH1Wiav1GTy7laI958ZmzHI20oJecvBw77MYswDiaqdbZbYOFQ/0"},{"userId":"33216A90A02411E69A3800163E0020D1","userName":"A佳佳粉丝应援团","roleId":3594,"roleName":"项目管理员","pic":"https://wx.qlogo.cn/mmopen/O5IB5rptd1oOpz8Zic5ictvFudHF3w95Hb5Xa08VaE4wRia2cLL0IYPTfrAWN6Ppyv1jIAz4SS2hicJbUBdgkDTw2w/0"},{"userId":"36BD4961A03211E69A3800163E0020D1","userName":"Richer","roleId":3595,"roleName":"项目成员","pic":"https://wx.qlogo.cn/mmopen/ajNVdqHZLLBM0iadriaBOQj2LSV2XaJAV8LBcicnYt4233lL4yz29CZuv6IFw1NgThkfAQ159F7CP4FZYicOPThlibQ/0"},{"userId":"3FE2C478A1B611E6AD6300163E0020D1","userName":"李方明","roleId":3594,"roleName":"项目管理员","pic":"https://wx.qlogo.cn/mmopen/PiajxSqBRaEJhD9KMSKWCSjyFnkABTZ8juSds1ib4mkY1c7YITGoFtLAzOC78gj0aSgoqJBChibXkl0COAU4ghMgg/0"},{"userId":"48744E23C5A511E69FB200163E162ADD","userName":"蓝浩春","roleId":3594,"roleName":"项目管理员","pic":null},{"userId":"4BF5503FD7A911E6A9E000163E162ADD","userName":"何天成","roleId":3594,"roleName":"项目管理员","pic":"https://wx.qlogo.cn/mmopen/O5IB5rptd1ppbN8Gl6sSNUW8rQeQgStNeicBrMb6cNqkpIRpeXaOIVic5wFdTx05WsO2uSoU7HweUWEYTZ5bXFYvKtEmeNicTMW/0"},{"userId":"5E7569490D5311E79B4900163E30718E","userName":"王善伟","roleId":3594,"roleName":"项目管理员","pic":"https://wx.qlogo.cn/mmopen/O5IB5rptd1pOCIYYKfAGA3moHY38iaOSsAicCITmFGzIAHvKiaq69ibQtQh5hWHLrQ0PJ83aosAicEWibKttCdqJw5HQ/0"},{"userId":"64ACC77CCC3811E6A9E000163E162ADD","userName":"李韬","roleId":3594,"roleName":"项目管理员","pic":"https://wx.qlogo.cn/mmopen/TLV402oJicT3zwHrmhjpvtUL5NrkEhbEVOAxyuHpiaiaWaSGlsQXchdytLCweh9tmWicMqT6yy2mUXI1fd6OKbEz692JsXXCUzRO/0"},{"userId":"6BE5394EFF1D11E69B4900163E30718E","userName":"芳姐","roleId":3594,"roleName":"项目管理员","pic":null},{"userId":"6CD58A21A02211E69A3800163E0020D1","userName":"石玏","roleId":3594,"roleName":"项目管理员","pic":"https://wx.qlogo.cn/mmopen/TLV402oJicT1UPcfK725ZKmqq4FYd9vyKvZattaziatPymh03zpQJGM1L7KcRDtkn5KzXvbzNkIa26sWEataibOCeTZ8ypkP5Xib/0"},{"userId":"6DBA55ECA02611E69A3800163E0020D1","userName":"陈俊杰","roleId":3594,"roleName":"项目管理员","pic":"https://wx.qlogo.cn/mmopen/SHnMujzj2v8VXia4lzCEWl8ctic0mUHZ5arLCy3ibiaRITREDbloyfsCE2Vn0ctp0Rlg1IuA4FzpudrRTpXXnIRCoaqkSic9Ydicgy/0"},{"userId":"6F7E86B4A0C411E6AD6300163E0020D1","userName":"采那","roleId":3595,"roleName":"项目成员","pic":"https://wx.qlogo.cn/mmopen/SHnMujzj2v8VXia4lzCEWl3esTJibWz0rjicxQBib6zZVGT1PDMpT3SIy17f9WYeTKpW6iaBhjOyTIwYbTVCGITSiap2hw6EZWAuwS/0"},{"userId":"8523897EA02211E69A3800163E0020D1","userName":"郑玮","roleId":3594,"roleName":"项目管理员","pic":"https://wx.qlogo.cn/mmopen/PiajxSqBRaELvU06YDHeQktnY1S4wFHLb1adMe1IGyZAS9Ho3o3yJ2hNCd9mCZB1dqN0ea2ibwO8kEQgNHiaz3rdQ/0"},{"userId":"86BC662EA04B11E6AD6300163E0020D1","userName":"陈武松","roleId":3594,"roleName":"项目管理员","pic":"https://wx.qlogo.cn/mmopen/EALWeEvfX7sAJgXE5BPbFj1wLh0VZcYCRjTc7IJSOOTpd3iabRoVgctLRIpDMXR8jOcicIfmhXI2kWvw1TAwRyj6AAbGOboWC5/0"},{"userId":"9094192CC33611E69FB200163E162ADD","userName":"万贝贝很hdsgvjgdfhkakghdfakjafdkhguyouoqu","roleId":3594,"roleName":"项目管理员","pic":"https://wx.qlogo.cn/mmopen/TLV402oJicT0DLUXF80tJh5HMPXsyQejVgia26TfDTooLbc8etGo2OsmBZ4kHpnpmEm6vE23c1YBa70MxM4HoASrrCcianUtB1l/0"},{"userId":"9E450C60A02211E69A3800163E0020D1","userName":"张骞","roleId":3594,"roleName":"项目管理员","pic":"https://wx.qlogo.cn/mmopen/O5IB5rptd1oayACj9HxFLvVqia0QlW4icQNmv7rN1JUoWWUOQPCNqeMxp7BWJZWfjZp4EbOicSwK7uAngFszO5Hcw/0"},{"userId":"A2EFA3E2A02011E69A3800163E0020D1","userName":"胡谢进","roleId":3594,"roleName":"项目管理员","pic":"https://wx.qlogo.cn/mmopen/EALWeEvfX7sAJgXE5BPbFmxTP4RPAZqaXrXOjKznWZkXudMnEMfGfHvhD3LxcRVz4bOV0fPlPMlp7HGyb6SJwKmComGkjH3O/0"},{"userId":"A3EFC3CCA02411E69A3800163E0020D1","userName":"赵文宇","roleId":3594,"roleName":"项目管理员","pic":"https://wx.qlogo.cn/mmopen/PiajxSqBRaEI36eiaUUK7uCr3FtvoEoFwHh9s9wOYM8QaJdOECDq0GC5ibTChjWUKGQ2uTWFiaQicvlcBicG7wBB09icQ/0"},{"userId":"A5E35029C33A11E69FB200163E162ADD","userName":"王兴龙","roleId":3594,"roleName":"项目管理员","pic":"https://wx.qlogo.cn/mmopen/Q3auHgzwzM5EtZM5FfOEib2QTaumvRtNYckRTVyz4jfxB8Ob9QsWbiafEsJ0ILtn6ohSGsTRMQ6nTPWq0CtdNj6Q/0"},{"userId":"BE8AEB0AA02011E69A3800163E0020D1","userName":"陈利","roleId":3594,"roleName":"项目管理员","pic":"https://wx.qlogo.cn/mmopen/EALWeEvfX7tAA2uISLHGrQQsibrNicQQQyUIib0MiawNN5eef6ICgYODpiaANo6qN2oVedP7ichhKlarJ5XicQb3NtMnkKibgQ4o184C/0"},{"userId":"C2CE0BD1033211E79B4900163E30718E","userName":"邵伟","roleId":3594,"roleName":"项目管理员","pic":"https://wx.qlogo.cn/mmopen/SHnMujzj2v8VXia4lzCEWl4Z8Ko3gpaM4N24eMLX9ib5oW0ib7CUqPVIsNGfmFEnZndbYpvrx3wcJMfiawcwiakibXS54u3Bb3vghq/0"},{"userId":"C9D28062DC6811E6A9E000163E162ADD","userName":"兰洋","roleId":3594,"roleName":"项目管理员","pic":"https://wx.qlogo.cn/mmopen/Q3auHgzwzM6KfiartuzCYoUwvmcl4RZdxJgCBR8X2wd7R6Gh7KmW4KMDEbu47jSBGq7XJSV5ofgBvjFjB4wyCvGrS6FrOjqD7KsZMDNCPOF0/0"},{"userId":"CE352607B77111E6834900163E001EAA","userName":"蓝浩春","roleId":3594,"roleName":"项目管理员","pic":"https://wx.qlogo.cn/mmopen/TLV402oJicT1UPcfK725ZKvkTXr4WRrOvg6rsEoRbIfM3ccEtAoaVZ1pFAlZPEO2SPxrHKicbD4U3yspm3rwqquotsiafEkzcKy/0"},{"userId":"D779E9E8A02411E69A3800163E0020D1","userName":"杨帆","roleId":3594,"roleName":"项目管理员","pic":"https://wx.qlogo.cn/mmopen/icOZDW0kqwN6MgibK4iamqJWykEfN7iaO9FuGaYjKr9Ey6DaHIDoft7NJtibicDyLBLXPJu4XqFHibibpxHOc7pibqCicOmul2bk6olUNn/0"},{"userId":"DE9B81AAA02511E69A3800163E0020D1","userName":"明华","roleId":3594,"roleName":"项目管理员","pic":"https://wx.qlogo.cn/mmopen/SHnMujzj2vibXicCcrpIAhxXjpzNkFTtLWLwpP6Q3IxIRCSWrCEdFqbNBdCpg7rgw7QHwsJxn5icjlkFCfZ5mWrD0PlhjcxgVF1/0"},{"userId":"FBBA5D3BD7A711E6A9E000163E162ADD","userName":"胡嘉欣","roleId":3594,"roleName":"项目管理员","pic":null}]
     */

    public String pkId;//主键
    public String name;//项目名称
    public String status;//项目状态：[0:预立案 1:申请立案 2:开案（正在办理） 3:申请结案 4:结案5、申请归档 6:已归档 7:终止代理] ,
    public long openDate;//开案日期
    public long closeDate;//结案日期
    public long terminationDate;//终止日期
    public String userId;//创建人 id
    public String userName;//创建人姓名
    public long crtTime;//创建时间
    public String updCde;//修改人 id
    public String updCdeName;//修改人姓名
    public long updTime;//修改时间
    public int matterType;//项目模板(类型)(0争议解决，1非诉专项,2常年顾问，3所内事务)
    public String matterCase;//案由 id
    public String caseProcess;//程序 id
    public String matterNumber;//案号
    public String competentCourt;//管辖法院
    public long beginDate;//开始时间
    public long endDate;//结束时间
    public long sumTime;//累计计时
    public String lawField;//法律领域 id
    public String matterTypeName;//项目类型名称
    public String matterCaseName;//案由名称
    public String caseProcessName;//程序名称
    public String lawFieldName;//法律领域名称
    public String statusName;//项目状态名称
    public List<ResponsibleAttorneyBean> responsibleAttorneys;//主办律师
    public List<AssistAttorneBean> assistAttorneys;//协办律师
    public List<OtherAttorneyBean> otherAttorneys;//其它参与人
    public List<OriginatingAttorneyBean> originatingAttorneys;//案源律师
    public List<JudgeBean> judges;//法官
    public List<ClerkBean> clerks;//书记员
    public List<ArbitratorBean> arbitrators;//仲裁员
    public List<SecretarieBean> secretaries;//仲裁秘书
    public String myStar;//星标
    public String allSee;//全所可见:[0:否1：是]
    public List<AttorneysBean> attorneys;//律师
    public List<ClientsBean> clients;//客户
    public List<LitigantsBean> litigants;//其它当事人
    public List<GroupsBean> groups;//所属团队
    public List<MembersBean> participants;//项目成员
    public String logDescription;//日志描述


    //律师
    public static class AttorneysBean implements Serializable {
        /**
         * pkId : 3F08509CC44B11E69FB200163E162ADD
         * attorneyPkid : DE9B81AAA02511E69A3800163E0020D1
         * attorneyName : 明华
         * attorneyPic : https://wx.qlogo.cn/mmopen/SHnMujzj2vibXicCcrpIAhxXjpzNkFTtLWLwpP6Q3IxIRCSWrCEdFqbNBdCpg7rgw7QHwsJxn5icjlkFCfZ5mWrD0PlhjcxgVF1/0
         * attorneyType : S
         */

        public String pkId;
        public String attorneyPkid;
        public String attorneyName;
        public String attorneyPic;
        public String attorneyType;

    }

    //客户
    public static class ClientsBean implements Serializable {
        /**
         * contactPkid : 5BE0F059C2DA11E69FB200163E162ADD
         * contactName : 深圳市大疆创新科技有限公司
         * customerPosition : 0
         * customerPositionName : null
         * type : C
         */

        public String contactPkid;
        public String contactName;
        public String customerPosition;
        public String customerPositionName;
        public String type;

    }

    //其他当事人
    public static class LitigantsBean implements Serializable {
        /**
         * contactPkid : 9BD98931C29011E69FB200163E162ADD
         * contactName : 从AC按时擦擦趣分期我s
         * customerPosition : null
         * customerPositionName : null
         * type : L
         */

        public String contactPkid;
        public String contactName;
        public String customerPosition;
        public String customerPositionName;
        public String type;

    }

    //所属团队
    public static class GroupsBean implements Serializable {
        /**
         * groupPkid : 62F7E17BA02011E69A3800163E0020D1
         * name : Alpha
         */

        public String groupPkid;
        public String name;

    }

    //项目成员
    public static class MembersBean implements Serializable {
        /**
         * userId : 01CEB4E16D2411E6A5C200163E0020D1
         * userName : 王储
         * roleId : 3594
         * roleName : 项目管理员
         * pic : https://wx.qlogo.cn/mmopen/EALWeEvfX7tAA2uISLHGrbwqoJO5GpkbfoYIS443frBzMrEoTnWu3VHXWU5DkP6XnMwmGldJq0dnoMpKgGia6VB2kffUjjYdz/0
         */

        public String userId;
        public String userName;
        public int roleId;
        public String roleName;
        public String pic;

    }

    //法官
    public static class JudgeBean implements Serializable {
        public String name;
        public String phone;
    }

    //书记员
    public static class ClerkBean implements Serializable {
        public String name;
        public String phone;
    }

    //仲裁员
    public static class ArbitratorBean implements Serializable {
        public String name;
        public String phone;
    }

    //仲裁秘书
    public static class SecretarieBean implements Serializable {
        public String name;
        public String phone;
    }

    //主办律师
    public static class ResponsibleAttorneyBean implements Serializable {

        public String pkId;
        public String attorneyPkid;
        public String attorneyName;
        public String attorneyPic;
        public String attorneyType;
    }

    //协办律师
    public static class AssistAttorneBean implements Serializable {

        public String pkId;
        public String attorneyPkid;
        public String attorneyName;
        public String attorneyPic;
        public String attorneyType;
    }

    //其他参与人
    public static class OtherAttorneyBean implements Serializable {

        public String pkId;
        public String attorneyPkid;
        public String attorneyName;
        public String attorneyPic;
        public String attorneyType;
    }

    //案源律师
    public static class OriginatingAttorneyBean implements Serializable {

        public String pkId;
        public String attorneyPkid;
        public String attorneyName;
        public String attorneyPic;
        public String attorneyType;
    }
}
