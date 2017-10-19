package com.icourt.alpha.widget.comparators;

import android.support.annotation.IntDef;

import com.icourt.alpha.entity.bean.FolderDocumentEntity;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Comparator;

/**
 * Description  文件排序
 * 默认排序，同 Web：
 * 文件夹优先于文件
 * 文件夹之间或文件之间按名称正序
 * 可选排序
 * 按名称排序：正序
 * 按文件由大到小排序：
 * 文件夹优先于文件
 * 文件夹按名称正序，文件按从大到小
 * 按文件由小到大排序：
 * 文件优先于文件夹
 * 文件按从大到小，文件夹按名称正序
 * 按最后更新时间排序：最后更新的排在最前面
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/8/18
 * version 2.1.0
 */
public class FileSortComparator implements Comparator<FolderDocumentEntity> {

    public static final int FILE_SORT_TYPE_DEFAULT = 0;
    public static final int FILE_SORT_TYPE_NAME = 1;
    public static final int FILE_SORT_TYPE_SIZE_DESC = 2;
    public static final int FILE_SORT_TYPE_SIZE_ASC = 3;
    public static final int FILE_SORT_TYPE_UPDATE = 4;

    @IntDef({FILE_SORT_TYPE_DEFAULT,
            FILE_SORT_TYPE_NAME,
            FILE_SORT_TYPE_SIZE_DESC,
            FILE_SORT_TYPE_SIZE_ASC,
            FILE_SORT_TYPE_UPDATE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface FileSortType {

    }

    @FileSortType
    int fileSortType;
    PinyinComparator<FolderDocumentEntity> pinyinComparator = new PinyinComparator<>();
    LongComparator longComparatorDESC = new LongComparator(ORDER.DESC);

    public FileSortComparator(@FileSortType int fileSortType) {
        this.fileSortType = fileSortType;
    }

    @Override
    public int compare(FolderDocumentEntity t0, FolderDocumentEntity t1) {
        if (t0 != null && t1 != null) {
            switch (fileSortType) {
                case FILE_SORT_TYPE_DEFAULT:
                    /**
                     * 默认排序，同 Web：
                     文件夹优先于文件
                     文件夹之间或文件之间按名称正序
                     */
                    if (t0.isDir() && t1.isDir()) {
                        return pinyinComparator.compare(t0, t1);
                    } else if (t0.isDir()) {
                        return -1;
                    } else if (t1.isDir()) {
                        return 1;
                    }
                    return pinyinComparator.compare(t0, t1);
                case FILE_SORT_TYPE_NAME:
                    /**
                     * 按名称排序：正序
                     */
                    return pinyinComparator.compare(t0, t1);
                case FILE_SORT_TYPE_SIZE_ASC:
                    /**
                     * 按文件由小到大排序：
                     文件优先于文件夹
                     文件按从大到小，文件夹按名称正序
                     */
                    if (t0.isDir() && t1.isDir()) {
                        return pinyinComparator.compare(t0, t1);
                    } else if (t0.isDir()) {
                        return 1;
                    } else if (t1.isDir()) {
                        return -1;
                    }
                    return longComparatorDESC.compare(t1.size, t0.size);
                case FILE_SORT_TYPE_SIZE_DESC:
                    /**
                     *按文件由大到小排序：
                     文件夹优先于文件
                     文件夹按名称正序，文件按从大到小
                     */
                    if (t0.isDir() && t1.isDir()) {
                        return pinyinComparator.compare(t0, t1);
                    } else if (t0.isDir()) {
                        return -1;
                    } else if (t1.isDir()) {
                        return 1;
                    }
                    return longComparatorDESC.compare(t0.size, t1.size);
                case FILE_SORT_TYPE_UPDATE:
                    /**
                     * 按最后更新时间排序：最后更新的排在最前面
                     */
                    return longComparatorDESC.compare(t0.mtime, t1.mtime);
            }
        }
        return 0;
    }
}
