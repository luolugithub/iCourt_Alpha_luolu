package com.icourt.alpha.widget.comparators;

import com.icourt.alpha.entity.bean.IMSessionEntity;

import java.util.Comparator;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/15
 * version 1.0.0
 */
public class IMSessionEntityComparator implements Comparator<IMSessionEntity> {
    private static final long RECENT_TAG_STICKY = 1; // 联系人置顶tag

    @Override
    public int compare(IMSessionEntity o1, IMSessionEntity o2) {
        if (o1 != null
                && o1.recentContact != null
                && o2 != null
                && o2.recentContact != null) {
            // 先比较置顶tag
            long sticky = (o1.recentContact.getTag() & RECENT_TAG_STICKY) - (o2.recentContact.getTag() & RECENT_TAG_STICKY);
            if (sticky != 0) {
                return sticky > 0 ? -1 : 1;
            } else {
                long time = o1.recentContact.getTime() - o2.recentContact.getTime();
                return time == 0 ? 0 : (time > 0 ? -1 : 1);
            }
        }
        return 0;
    }
}
