package com.icourt.alpha.utils;

import java.security.MessageDigest;

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

    /**
     * 字符串 SHA 加密
     *
     * @return
     */
    private static final String SHA(final String strText, final String strType) {
        MessageDigest sha = null;
        try {
            sha = MessageDigest.getInstance(strType);
            sha.reset();
            byte[] srcBytes = strText.getBytes();
            return hexString(sha.digest(srcBytes));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static final String hexString(byte[] bytes) {
        StringBuffer hexValue = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            int val = ((int) bytes[i]) & 0xff;
            if (val < 16)
                hexValue.append("0");
            hexValue.append(Integer.toHexString(val));
        }
        return hexValue.toString();
    }

    public static final String sha256(String str) {
        return SHA(str, "SHA-256");
    }

}
