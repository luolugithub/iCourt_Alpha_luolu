package com.icourt.alpha.utils;

import android.content.Context;
import android.text.TextUtils;

import com.icourt.alpha.view.recyclerviewDivider.ISuspensionAction;
import com.icourt.alpha.view.recyclerviewDivider.ISuspensionInterface;
import com.pinyin4android.PinyinUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/23
 * version 1.0.0
 */
public class IndexUtils {

    /**
     * 为实体设置拼音首字母
     *
     * @param context
     * @param data
     * @return
     */
    public static List<? extends ISuspensionAction> setSuspensions(Context context, List<? extends ISuspensionAction> data) {
        if (context != null && data != null) {
            for (ISuspensionAction i : data) {
                if (i != null) {
                    String targetField = i.getTargetField();
                    //后期优化 首个char处理
                    if (!TextUtils.isEmpty(targetField)) {
                        String pinyin = PinyinUtil.toPinyin(context, targetField);
                        if (!TextUtils.isEmpty(pinyin)) {
                            i.setSuspensionTag(Character.toString(pinyin.toUpperCase().charAt(0)));
                        } else {
                            i.setSuspensionTag(null);
                        }
                    } else {
                        i.setSuspensionTag(null);
                    }
                }
            }
        }
        return data;
    }


    /**
     * 获取index 默认已经去重
     *
     * @param sortedData 建议先排序
     * @return
     */
    public static ArrayList<String> getSuspensions(List<? extends ISuspensionInterface> sortedData) {
        ArrayList<String> indexList = new ArrayList<>();
        if (sortedData != null) {
            for (ISuspensionInterface suspensionInterface : sortedData) {
                if (suspensionInterface != null && !indexList.contains(suspensionInterface.getSuspensionTag())) {
                    indexList.add(suspensionInterface.getSuspensionTag());
                }
            }
        }
        return indexList;
    }
}
