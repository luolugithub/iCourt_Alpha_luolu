package com.icourt.alpha.entity.bean;

import com.netease.nimlib.sdk.msg.model.IMMessage;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/24
 * version 1.0.0
 */
@Deprecated
public class IMCustomerMessageEntity {
    public IMMessageCustomBody customIMBody;//自定义消息体 请提前解析
    public IMMessage imMessage;
}
