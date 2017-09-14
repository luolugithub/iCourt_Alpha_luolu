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
    public String createUserId;//创建人Id
    public long gmtCreate;//创建时间
    public String modifyUserId;//修改人Id
    public long gmtModified;//修改时间
    public String appVersion;//版本号
    public String buildVersion;//build版本号
    public String effectVersion;//影响版本号，如果为空则影响所有版本
    public List<VersionDescBean> versionDesc;//升级描述
    public int upgradeStrategy;//升级策略：1、常规可选升级；2、强制更新
    public int osType;//终端类型：1、Android；2、iOS
    public String upgradeUrl;//升级包地址

    public class VersionDescBean implements Serializable {

        public String type;
        public String desc;
    }
}
