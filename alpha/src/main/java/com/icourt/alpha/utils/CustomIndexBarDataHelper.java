package com.icourt.alpha.utils;

import com.github.promeg.pinyinhelper.Pinyin;
import com.mcxtzhang.indexlib.IndexBar.bean.BaseIndexPinyinBean;
import com.mcxtzhang.indexlib.IndexBar.helper.IIndexBarDataHelper;
import com.mcxtzhang.indexlib.IndexBar.helper.IndexBarDataHelperImpl;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/11
 * version 1.0.0
 */
public class CustomIndexBarDataHelper  extends IndexBarDataHelperImpl {
    public IIndexBarDataHelper convert(List<? extends BaseIndexPinyinBean> datas) {
        if (null != datas && !datas.isEmpty()) {
            int size = datas.size();

            for (int i = 0; i < size; ++i) {
                BaseIndexPinyinBean indexPinyinBean = (BaseIndexPinyinBean) datas.get(i);
                StringBuilder pySb = new StringBuilder();
                if (indexPinyinBean.isNeedToPinyin()) {
                    String target = indexPinyinBean.getTarget();

                    for (int i1 = 0; i1 < target.length(); ++i1) {
                        pySb.append(Pinyin.toPinyin(target.charAt(i1)).toUpperCase());
                    }

                    indexPinyinBean.setBaseIndexPinyin(pySb.toString());
                }
            }

            return this;
        } else {
            return this;
        }
    }

    public IIndexBarDataHelper fillInexTag(List<? extends BaseIndexPinyinBean> datas) {
        if (null != datas && !datas.isEmpty()) {
            int size = datas.size();

            for (int i = 0; i < size; ++i) {
                BaseIndexPinyinBean indexPinyinBean = datas.get(i);
                if (indexPinyinBean.isNeedToPinyin()) {
                    String tagString = indexPinyinBean.getBaseIndexPinyin().substring(0, 1);
                    if (tagString.matches("[A-Z]")) {
                        indexPinyinBean.setBaseIndexTag(tagString);
                    } else {
                        indexPinyinBean.setBaseIndexTag("#");
                    }
                }
            }

            return this;
        } else {
            return this;
        }
    }

    public IIndexBarDataHelper sortSourceDatas(List<? extends BaseIndexPinyinBean> datas) {
        try {
            if (null != datas && !datas.isEmpty()) {
                this.convert(datas);
                this.fillInexTag(datas);
                final List<String> list = TextFormater.firstList();
                list.add(list.size(), "#");
                System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
                Collections.sort(datas, new Comparator<BaseIndexPinyinBean>() {
                    @Override
                    public int compare(BaseIndexPinyinBean baseIndexPinyinBean, BaseIndexPinyinBean t1) {
                        BaseIndexPinyinBean lhs = baseIndexPinyinBean;
                        BaseIndexPinyinBean rhs = t1;

                        if (baseIndexPinyinBean == null || t1 == null) {
                            return 1;
                        }

                        if (!lhs.isNeedToPinyin()) {
                            return 0;
                        } else if (!rhs.isNeedToPinyin()) {
                            return 0;
                        }
//                        else if (lhs.getBaseIndexTag().equals("#")) {
//                            return 1;
//                        } else if (rhs.getBaseIndexTag().equals("#")) {
//                            return 0;
//                        }
                        else {
                            int a = list.indexOf(lhs.getBaseIndexTag().toLowerCase());
                            int b = list.indexOf(rhs.getBaseIndexTag().toLowerCase());
//                            return lhs.getBaseIndexPinyin().compareTo(rhs.getBaseIndexPinyin());
                            long time = a - b;
                            return time == 0 ? 0 : (time < 0 ? -1 : 1);
                        }
                    }
                });
                return this;
            } else {
                return this;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }

    public IIndexBarDataHelper getSortedIndexDatas(List<? extends BaseIndexPinyinBean> sourceDatas, List<String> indexDatas) {
        if (null != sourceDatas && !sourceDatas.isEmpty()) {
            int size = sourceDatas.size();

            for (int i = 0; i < size; ++i) {
                String baseIndexTag = (sourceDatas.get(i)).getBaseIndexTag();
                if (!indexDatas.contains(baseIndexTag)) {
                    indexDatas.add(baseIndexTag);
                }
            }

            return this;
        } else {
            return this;
        }
    }
}
