package com.icourt.alpha.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import Decoder.BASE64Encoder;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/6
 * version 1.0.0
 */
public class Md5Utils {

    /**
     * 使用MD5加密密码
     *
     * @param message
     * @return
     */
    public static String md5(String message) {
        return md5(message, null);
    }

    public static String md5(String message, String defaultResult) {
        try {
            MessageDigest md = MessageDigest.getInstance("md5");
            byte m5[] = md.digest(message.getBytes());
            BASE64Encoder encoder = new BASE64Encoder();
            return encoder.encode(m5);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return defaultResult;
    }
}
