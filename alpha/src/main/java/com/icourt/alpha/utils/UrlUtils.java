package com.icourt.alpha.utils;

import android.text.TextUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/27
 * version 1.0.0
 */
public class UrlUtils {

    /**
     * 获取url 参数
     * https://alphalawyer.cn/ilaw/api/v2/file/download?sFileId=64880&token=xxx&width=480;
     *
     * @param key
     * @return
     */
    public static final String getParam(String key, String url) {
        if (!TextUtils.isEmpty(key)) {
            if (!TextUtils.isEmpty(url)) {
                String urlKey = key + "=";
                int startParamIndex = url.lastIndexOf(urlKey);
                if (startParamIndex > 0) {
                    int end = url.indexOf("&", startParamIndex);
                    if (end > 0) {
                        return url.substring(startParamIndex + urlKey.length(), end);
                    } else {//最后一个
                        return url.substring(startParamIndex + urlKey.length());
                    }
                }
            }
        }
        return null;
    }

    /**
     * 移除某个参数
     *
     * @param key
     * @param url
     * @return
     */
    public static final String removeParam(String key, String url) {
        String value = getParam(key, url);
        if (!TextUtils.isEmpty(value)) {
            return url.replace(String.format("%s=%s", key, value), "");
        }
        return url;
    }

    /**
     * 追加参数
     *
     * @param key
     * @param value
     */
    public static final String appendParam(String url, String key, String value) {
        if (!TextUtils.isEmpty(url)) {
            if (url.contains("?")) {
                if (!url.contains(String.format("%s=", key))) {
                    return url.concat(String.format("&%s=%s", key, value));
                }
            } else {
                return url.concat(String.format("?%s=%s", key, value));
            }
        }
        return url;
    }

    /**
     * 获取html中的指定标签的值
     *
     * @param htmlString
     * @param labelName
     * @return
     */
    public static final String getHtmlLabel(String htmlString, String labelName) {
        if (!TextUtils.isEmpty(htmlString)
                && !TextUtils.isEmpty(labelName)) {
            Pattern pa = Pattern.compile(String.format("<%s>.*?</%s>", labelName, labelName));//源码中标题正则表达式
            Matcher ma = pa.matcher(htmlString);
            while (ma.find()) {
                return ma.group();
            }
        }
        return null;
    }

    /**
     * 文本是否是http链接
     *
     * @param text
     * @return
     */
    public static final boolean isHttpLink(String text) {
        if (TextUtils.isEmpty(text)) return false;
        String pattern = "(http|ftp|https):\\/\\/[\\w\\-_]+(\\.[\\w\\-_]+)+([\\w\\-\\.,@?^=%&amp;:/~\\+#]*[\\w\\-\\@?^=%&amp;/~\\+#])?";
        Pattern pa = Pattern.compile(pattern);
        Matcher ma = pa.matcher(text.trim());
        return ma.find();
    }


    /**
     * 获取html keywords <meta name="keywords" content="正则表达式,html"/>
     * 后期优化
     *
     * @param htmlString
     * @return
     */
    public static final String getHtmlKeywordslabel(String htmlString) {
        if (!TextUtils.isEmpty(htmlString)) {
            String keywordsLabel = "name=\"keywords\" content=\"";
            int keywordsStart = htmlString.indexOf(keywordsLabel);
            if (keywordsStart >= 0) {
                int contentEnd = htmlString.indexOf("\"", keywordsStart + 1 + keywordsLabel.length());
                if (contentEnd >= 0) {
                    return htmlString.substring(keywordsStart + 1 + keywordsLabel.length(), contentEnd);
                }
            }
        }
        return null;
    }

    /**
     * 获取html keywords <meta name="description" content="正则表达式,html"/>
     * 后期优化
     *
     * @param htmlString
     * @return
     */
    public static final String getHtmlDescriptionlabel(String htmlString) {
        if (!TextUtils.isEmpty(htmlString)) {
            String descriptionLabel = "name=\"description\" content=\"";
            int descriptionStart = htmlString.indexOf(descriptionLabel);
            if (descriptionStart >= 0) {
                int contentEnd = htmlString.indexOf("\"", descriptionStart + 1 + descriptionLabel.length());
                if (contentEnd >= 0) {
                    return htmlString.substring(descriptionStart + 1 + descriptionLabel.length(), contentEnd);
                }
            }
        }
        return null;
    }

    /**
     * <img alt="迪力热巴要离开杨幂单飞了，你们怎么看？" src="http://p3.pstatp.com/large/1dc30001ef7b5f2486e2" />
     * 获取html第一张图片
     *
     * @return
     */

    public static String getHtmlFirstImage(String htmlString) {
        try {
            if (!TextUtils.isEmpty(htmlString)) {
                int imgIndexOf = htmlString.indexOf("<img ");
                if (imgIndexOf >= 0) {
                    String srcString = "src=\"";
                    int srcIndex = htmlString.indexOf(srcString, imgIndexOf);
                    if (srcIndex >= 0) {
                        String imgUrl = htmlString.substring(
                                srcIndex + srcString.length(),
                                htmlString.indexOf("\"", srcIndex + srcString.length())
                        );
                        if (imgUrl.startsWith("http")) {
                            return imgUrl;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param url
     * @return
     */
    public static final String encodeUrl(String url) {
        try {
            if (!TextUtils.isEmpty(url)) {
                return URLEncoder.encode(url, "utf-8");
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return url;
    }
}
