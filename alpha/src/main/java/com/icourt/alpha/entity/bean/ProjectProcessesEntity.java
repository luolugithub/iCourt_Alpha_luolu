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
     * id : 0acd664a896f11e7bb2600163e0691a5
     * legalType : 1
     * processCode : 111
     * processName : 一审
     * priceStr : 10,000,000
     * price : 10000000
     * caseCodes : [{"code":"1010010010070","name":"触电人身损害赔偿纠纷"},{"code":"","name":"民事一审"}]
     * position : [{"partyCode":"114103","partyName":"张三","contactPkid":"C4C321B0B60911E6834900163E001EAA","contactName":null},{"partyCode":"117102","partyName":"李四","contactPkid":"B0DA184CCCA111E6A9E000163E162ADD","contactName":null}]
     * extra : [{"type":"court","name":"法院","values":[{"id":"10101","text":"朝阳人民法院","phone":""},{"id":"10102","text":"海淀法院","phone":""}]},{"type":"caseNo","name":"案由","values":[{"id":"","text":"第-1010993号","phone":""}]}]
     */

    public String id;
    public int legalType;// 程序类型，1：民事，2：刑事，3：行政
    public String processCode;
    public String legalName;
    public String processName;
    public String priceStr;
    public String price;
    public List<CaseCodesBean> caseCodes;
    public List<PositionBean> position;
    public List<ExtraBean> extra;


    public static class CaseCodesBean implements Serializable {
        /**
         * code : 1010010010070
         * name : 触电人身损害赔偿纠纷
         */

        public String code;
        public String name;

    }

    public static class PositionBean implements Serializable {
        /**
         * partyCode : 114103
         * partyName : 张三
         * contactPkid : C4C321B0B60911E6834900163E001EAA
         * contactName : null
         */

        public String partyCode;
        public String partyName;
        public String contactPkid;
        public String contactName;

    }

    public static class ExtraBean implements Serializable {
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
