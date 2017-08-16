package com.icourt.alpha.entity.bean;

import android.text.TextUtils;

import com.icourt.alpha.BuildConfig;

/**
 * Description  注意:优先officeShareLink 如果officeShareLink==null 拼接:shareLinkId
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/8/15
 * version 2.1.0
 */
public class SFileLinkInfoEntity {

    /**
     * {
     * "resultCode": "1",
     * "resultMess": "获取共享链接信息成功",
     * "shareLinkId": null,
     * "password": null,
     * "openNum": null,
     * "useNum": null,
     * "expireTime": null,
     * "officeShareLink": null
     * }
     */


    public int resultCode;
    public String resultMess;
    public String shareLinkId;
    public String password;
    public int openNum;
    public int useNum;
    public long expireTime;
    public String officeShareLink;

    public boolean isNoLink() {
        return TextUtils.isEmpty(officeShareLink)
                && TextUtils.isEmpty(shareLinkId);
    }


    /**
     * 是否需要访问密码
     *
     * @return
     */
    public boolean isNeedAccessPwd() {
        return !TextUtils.isEmpty(password);
    }

    /**
     * 真正的外链
     *
     * @return
     */
    public String getRealShareLink() {
        if (!TextUtils.isEmpty(officeShareLink)) {
            return officeShareLink;
        } else {
            return BuildConfig.API_URL
                    .replace("ilaw/", "").concat("#withoutlo/sharelink/")
                    .concat(shareLinkId);
        }
    }
}
