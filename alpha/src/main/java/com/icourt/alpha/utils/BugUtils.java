package com.icourt.alpha.utils;

import android.text.TextUtils;

import com.bugtags.library.Bugtags;
import com.icourt.alpha.entity.bean.AlphaUserInfo;

/**
 * Description  联系人详情界面
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/6/29
 * version 1.0.0
 */
public class BugUtils {

    /**
     * 同步bug到bugtags
     *
     * @param tag
     * @param log
     */
    public static final void bugSync(String tag, String log) {
        if (!TextUtils.isEmpty(tag) && !TextUtils.isEmpty(log)) {
            try {
                StringBuilder stringBuilder = new StringBuilder(tag);
                stringBuilder.append("\n");
                stringBuilder.append(log);
                stringBuilder.append("\n");
                AlphaUserInfo loginUserInfo = LoginInfoUtils.getLoginUserInfo();
                if (loginUserInfo != null) {
                    stringBuilder.append("loginUserInfo:" + loginUserInfo);
                } else {
                    stringBuilder.append("loginUserInfo null");
                }
                Bugtags.sendFeedback(stringBuilder.toString());
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 同步bug到bugtags
     *
     * @param tag
     * @param throwable
     */
    public static final void bugSync(String tag, Throwable throwable) {
        if (!TextUtils.isEmpty(tag) && throwable != null) {
            bugSync(tag, StringUtils.throwable2string(throwable));
        }
    }
}
