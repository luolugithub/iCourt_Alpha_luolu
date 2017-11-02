package com.icourt.alpha.entity.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Description  新版本实体类  文档参考 https://fir.im/docs/version_detection
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/5
 * version 1.0.0
 */
public class AppVersionEntity implements Serializable {

    /**
     * id : 10
     * osType : 1
     * appVersion : 3.0
     * buildVersion : b-3.0
     * versionDescs : [{"id":11,"appVersionId":10,"type":"新增","versionDesc":"新增...什么鬼","createUserId":"01CEB4E16D2411E6A5C200163E0020D1","gmtCreate":1505378752000,"modifyUserId":"01CEB4E16D2411E6A5C200163E0020D1","gmtModified":1505378752000},{"id":12,"appVersionId":10,"type":"修复","versionDesc":"修复了...什么鬼","createUserId":"01CEB4E16D2411E6A5C200163E0020D1","gmtCreate":1505378752000,"modifyUserId":"01CEB4E16D2411E6A5C200163E0020D1","gmtModified":1505378752000},{"id":13,"appVersionId":10,"type":"其他","versionDesc":"其他...什么鬼","createUserId":"01CEB4E16D2411E6A5C200163E0020D1","gmtCreate":1505378752000,"modifyUserId":"01CEB4E16D2411E6A5C200163E0020D1","gmtModified":1505378752000}]
     * upgradeStrategy : 2
     * upgradeUrl : http://www.google.com.hk
     * createUserId : 01CEB4E16D2411E6A5C200163E0020D1
     * gmtCreate : 1505376773000
     * modifyUserId : 01CEB4E16D2411E6A5C200163E0020D1
     * gmtModified : 1505378752000
     */

    public int id;
    public String osType;//终端类型：1代表Android；2代表IOS
    public String appVersion;//版本 ,
    public String buildVersion;//Build版本
    public int upgradeStrategy;//升级策略  1、常规可选升级；2、强制更新
    public String upgradeUrl;//升级包地址
    public String createUserId;//创建人ID
    public long gmtCreate;//创建时间
    public String modifyUserId;//修改人ID
    public long gmtModified;//修改时间
    public List<VersionDescsBean> versionDescs;//升级描述


    public static class VersionDescsBean {
        /**
         * id : 11
         * appVersionId : 10
         * type : 新增
         * versionDesc : 新增...什么鬼
         * createUserId : 01CEB4E16D2411E6A5C200163E0020D1
         * gmtCreate : 1505378752000
         * modifyUserId : 01CEB4E16D2411E6A5C200163E0020D1
         * gmtModified : 1505378752000
         */

        public int id;
        public int appVersionId;
        public String type;//描述类型
        public String versionDesc;//描述内容
        public String createUserId;
        public long gmtCreate;
        public String modifyUserId;
        public long gmtModified;

    }
}
