package com.icourt.alpha.interfaces;

/**
 * Description
 * Company Beijing icourt
 * author  lu.zhao  E-mail:zhaolu@icourt.cc
 * date createTime：17/6/5
 * version 2.0.0
 */

public interface OnUpdateTaskListener {

    /**
     * 修改检查项
     *
     * @param doneItemCount
     * @param totalCount
     */
    void onUpdateCheckItem(int doneItemCount, int totalCount);

    /**
     * 修改文档
     *
     * @param documentCount
     */
    void onUpdateDocument(int documentCount);

}
