package com.icourt.alpha.entity.bean;

/**
 * Description  新版本实体类  文档参考 https://fir.im/docs/version_detection
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/5
 * version 1.0.0
 */
public class AppVersionEntity {
    /**
     * id : 2
     * createUserId : 01CEB4E16D2411E6A5C200163E0020D1
     * gmtCreate : 1504875962000
     * modifyUserId : 01CEB4E16D2411E6A5C200163E0020D1
     * gmtModified : 1504768865000
     * appVersion : 1.1.0
     * buildVersion : b-1.1.0
     * effectVersion : ALL
     * versionDesc : 我们杀了一个产品经理祭天
     * upgradeStrategy : 1
     * osType : 1
     * upgradeUrl : http://www.baidu.com
     */

    public String id;
    public String createUserId;
    public long gmtCreate;
    public String modifyUserId;
    public long gmtModified;
    public String appVersion;
    public String buildVersion;
    public String effectVersion;
    public String versionDesc;
    public int upgradeStrategy;
    public int osType;
    public String upgradeUrl;

}
