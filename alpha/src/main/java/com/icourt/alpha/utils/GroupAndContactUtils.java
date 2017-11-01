package com.icourt.alpha.utils;

import android.content.Context;
import android.text.TextUtils;

import com.icourt.alpha.entity.bean.GroupContactBean;
import com.pinyin4android.PinyinUtil;

import java.util.List;

/**
 * Description 与讨论组和用户相关的工具类
 * Company Beijing iCourt
 *
 * @author Junkang.Ding Email:dingjunkang@icourt.cc
 *         date createTime：2017/11/1
 *         version 2.2.1
 */
public final class GroupAndContactUtils {

    public static final String DEFAULT_PINYIN_GAP_STR = " ";

    private GroupAndContactUtils() {
    }

    /**
     * 服务器给的用户的nameCharacter不太准确，所以使用PinyinUtil做本地转换
     * <p>
     * !IMPORTANT: 有一定的性能消耗
     *
     * @param contactList 用户/组员列表
     */
    public static void pinyinContactNameCharacters(Context context, List<GroupContactBean> contactList) {
        if (context == null) {
            return;
        }
        if (contactList != null) {
            for (GroupContactBean contact : contactList) {
                contact.nameCharacter = PinyinUtil.toPinyin(context, contact.name);
            }
        }
    }

    /**
     * 获取拼音
     *
     * @param string 汉文或英文
     * @param gapStr 词之间的分割串
     */
    public static String makePinyin(Context context, String string, String gapStr) {
        if (context == null || TextUtils.isEmpty(string)) {
            return string;
        }
        // s的默认分割符是单个空格' ', DEFAULT_PINYIN_GAP_STR
        String s = PinyinUtil.toPinyin(context, string);
        if (gapStr == null) {
            return s;
        }
        if (DEFAULT_PINYIN_GAP_STR.equals(gapStr)) {
            return s;
        }
        return s.replace(DEFAULT_PINYIN_GAP_STR, gapStr);
    }
}
