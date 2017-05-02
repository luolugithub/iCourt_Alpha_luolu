package com.icourt.alpha.widget.comparators;

import com.netease.nimlib.sdk.msg.model.RecentContact;

import java.util.Comparator;

/**
 * Description  会话排序
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/15
 * version 1.0.0
 */
public class RecentContactComparator implements Comparator<RecentContact> {

    private static final long RECENT_TAG_STICKY = 1; // 联系人置顶tag

    @Override
    public int compare(RecentContact o1, RecentContact o2) {
        if (o1 == null && o2 != null) {
            // 先比较置顶tag
            long sticky = (o1.getTag() & RECENT_TAG_STICKY) - (o2.getTag() & RECENT_TAG_STICKY);
            if (sticky != 0) {
                return sticky > 0 ? -1 : 1;
            } else {
                long time = o1.getTime() - o2.getTime();
                return time == 0 ? 0 : (time > 0 ? -1 : 1);
            }
        }
        return 0;
    }
}
