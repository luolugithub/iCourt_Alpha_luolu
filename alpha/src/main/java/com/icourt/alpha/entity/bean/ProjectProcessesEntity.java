package com.icourt.alpha.entity.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Description   项目程序model
 * Company Beijing icourt
 * author  lu.zhao  E-mail:zhaolu@icourt.cc
 * date createTime：17/8/28
 * version 2.0.0
 */

public class ProjectProcessesEntity implements Serializable {


    /**
     * processName : 一审
     * priceStr : 10,000,000
     * caseCode : ["1010010010070"]
     * caseName : ["民事一审"]
     * position : [{"partyCode":"114103","partyName":"张三","contactPkid":"C4C321B0B60911E6834900163E001EAA"},{"partyCode":"117102","partyName":"李四","contactPkid":"B0DA184CCCA111E6A9E000163E162ADD"}]
     * acceptance : [{"type":"court","name":"法院","values":[{"id":"10101","text":"朝阳人民法院","phone":""},{"id":"10102","text":"海淀法院","phone":""}]},{"type":"caseNo","name":"案由","values":[{"id":"","text":"第-1010993号","phone":""}]}]
     * bailTime : 2017-08-25 16:26
     */

    public String processName;//程序名称
    public String priceStr; //标的额，long类型，精确到分
    public String bailTime;
    public List<String> caseCode;//案由代码
    public List<String> caseName;//案由名称，多个会已英文逗号分隔
    public List<PositionBean> position;//当事人地位信息
    public List<AcceptanceBean> acceptance;//受理信息，不同的程序内部字段不同


    public static class PositionBean implements Serializable {
        /**
         * partyCode : 114103
         * partyName : 张三
         * contactPkid : C4C321B0B60911E6834900163E001EAA
         */

        public String partyCode;
        public String partyName;
        public String contactPkid;

    }

    public static class AcceptanceBean implements Serializable {
        /**
         * type : court
         * name : 法院
         * values : [{"id":"10101","text":"朝阳人民法院","phone":""},{"id":"10102","text":"海淀法院","phone":""}]
         */

        public String type;
        public String name;
        public List<ValuesBean> values;


        public static class ValuesBean implements Serializable {
            /**
             * id : 10101
             * text : 朝阳人民法院
             * phone :
             */

            public String id;
            public String text;
            public String phone;
        }
    }
}
