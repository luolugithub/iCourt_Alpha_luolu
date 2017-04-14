package com.icourt.alpha.utils;

import android.text.TextUtils;
import android.widget.ImageView;

import com.icourt.alpha.view.TextDrawable;

import java.util.Arrays;
import java.util.List;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/13
 * version 1.0.0
 */
public class IMUtils {

    //team 背景 请勿轻易修改
    public static final int[] TEAM_ICON_BGS = {
            0xFFA3D9E3,
            0xFFF4E166,
            0XFFED7945,
            0XFFA53D4D,
            0XFF67405A,
            0XFF5A4A45,
            0XFF6A85BC
    };

    /**
     * 是否是图片
     *
     * @param url
     * @return
     */
    public static final boolean isPIC(String url) {
        if (!TextUtils.isEmpty(url)) {
            int pointIndex = url.lastIndexOf(".");
            if (pointIndex >= 0 && pointIndex < url.length()) {
                String fileSuffix = url.substring(pointIndex, url.length());
                return getPICSuffixs().contains(fileSuffix);
            }
        }
        return false;
    }

    /**
     * 图片后缀
     *
     * @return
     */
    public static final List<String> getPICSuffixs() {
        return Arrays.asList(".png", ".jpg", ".gif", ".jpeg", ".PNG", ".JPG", ".GIF", ".JPEG");
    }

    /**
     * 设置team头像
     *
     * @param teamName
     * @param ivSessionIcon
     */
    public static final void setTeamIcon(String teamName, ImageView ivSessionIcon) {
        if (TextUtils.isEmpty(teamName)) return;
        if (ivSessionIcon == null) return;
        char firstUpperLetter = PingYinUtil.getFirstUpperLetter(ivSessionIcon.getContext(), teamName);
        if (PingYinUtil.isUpperLetter(firstUpperLetter)) {//是字母
            //第一个字母是i
            if (TextUtils.equals("i", String.valueOf(firstUpperLetter))) {
                TextDrawable icon = TextDrawable.builder()
                        .buildRound(Character.toString(firstUpperLetter),
                                IMUtils.TEAM_ICON_BGS[2]);
                ivSessionIcon.setImageDrawable(icon);
            } else if (teamName.length() == 1) {//长度为1
                int indexOfUpper = PingYinUtil.indexOfUpperUpperLetter(firstUpperLetter);
                int realIndex = indexOfUpper / IMUtils.TEAM_ICON_BGS.length;
                TextDrawable icon = TextDrawable.builder()
                        .buildRound(Character.toString(firstUpperLetter),
                                IMUtils.TEAM_ICON_BGS[realIndex]);
                ivSessionIcon.setImageDrawable(icon);
            } else if (teamName.length() > 1) {//长度为2 两位
                int indexOfUpper = PingYinUtil.indexOfUpperUpperLetter(firstUpperLetter);
                char indexUpperLetter2 = PingYinUtil.getIndexUpperLetter(ivSessionIcon.getContext(), teamName, 1);
                int indexOfUpper2 = PingYinUtil.indexOfUpperUpperLetter(indexUpperLetter2);
                int realIndex = (indexOfUpper + indexOfUpper2) / IMUtils.TEAM_ICON_BGS.length;
                if (realIndex >= IMUtils.TEAM_ICON_BGS.length) {
                    realIndex = realIndex / IMUtils.TEAM_ICON_BGS.length;
                }
                if (realIndex >= 0) {
                    TextDrawable icon = TextDrawable.builder()
                            .buildRound(Character.toString(firstUpperLetter),
                                    IMUtils.TEAM_ICON_BGS[realIndex]);
                    ivSessionIcon.setImageDrawable(icon);
                }
            }
        } else {//非字母
            TextDrawable icon = TextDrawable.builder()
                    .buildRound("#", IMUtils.TEAM_ICON_BGS[0]);
            ivSessionIcon.setImageDrawable(icon);
        }

    }

}
