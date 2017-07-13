package com.icourt.alpha.adapter;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.adapter.baseadapter.MultiSelectRecyclerAdapter;
import com.icourt.alpha.constants.Const;
import com.icourt.alpha.entity.bean.GroupContactBean;
import com.icourt.alpha.utils.GlideUtils;
import com.icourt.alpha.utils.SpannableUtils;
import com.icourt.alpha.utils.StringUtils;
import com.pinyin4android.PinyinUtil;

/**
 * Descriptionn  联系人适配器
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/11
 * version 1.0.0
 */
public class IMContactAdapter extends MultiSelectRecyclerAdapter<GroupContactBean> {
    int foregroundColor = 0xFFed6c00;
    @Const.AdapterViewType
    private int type;

    public IMContactAdapter() {
        this.type = Const.VIEW_TYPE_ITEM;
    }

    private String keyWord;

    public IMContactAdapter(String keyWord) {
        this.type = Const.VIEW_TYPE_ITEM;
        this.keyWord = keyWord;
    }

    public void setKeyWord(String keyWord) {
        this.keyWord = keyWord;
    }

    public IMContactAdapter(@Const.AdapterViewType int type) {
        this.type = type;
    }

    @Override
    public int bindView(int viewtype) {
        switch (type) {
            case Const.VIEW_TYPE_GRID:
                return R.layout.adapter_item_im_contact_grid;
            case Const.VIEW_TYPE_ITEM:
                return R.layout.adapter_item_im_contact;
            default:
                return R.layout.adapter_item_im_contact;
        }
    }

    @Override
    public void onBindSelectableHolder(ViewHolder holder, GroupContactBean groupContactBean, boolean selected, int position) {
        if (groupContactBean == null) return;
        ImageView iv_contact_icon = holder.obtainView(R.id.iv_contact_icon);
        TextView tv_contact_name = holder.obtainView(R.id.tv_contact_name);
        GlideUtils.loadUser(iv_contact_icon.getContext(), groupContactBean.pic, iv_contact_icon);
        if (!TextUtils.isEmpty(keyWord)) {
            String originalText = groupContactBean.name;
            SpannableString textForegroundColorSpan = null;
            if (StringUtils.containsIgnoreCase(originalText, keyWord)) {
                textForegroundColorSpan = SpannableUtils.getTextForegroundColorSpan(originalText, keyWord, foregroundColor);
            } else {//可能是汉字
                textForegroundColorSpan = new SpannableString(groupContactBean.name);
                try {
                    //用本地提取的 目前网络不准确
                    groupContactBean.nameCharacter = PinyinUtil.toPinyin(tv_contact_name.getContext(), groupContactBean.name);
                    String[] split = groupContactBean.nameCharacter.split(" ");
                    if (split != null) {
                        for (int i = 0; i < split.length; i++) {
                            String s = split[i];
                            if (StringUtils.containsIgnoreCase(s, keyWord)) {
                                textForegroundColorSpan.setSpan(new ForegroundColorSpan(foregroundColor),
                                        i, i + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    textForegroundColorSpan = SpannableUtils.getTextForegroundColorSpan(originalText, keyWord, foregroundColor);
                }
            }
            tv_contact_name.setText(textForegroundColorSpan);
        } else {
            tv_contact_name.setText(groupContactBean.name);
        }

        CheckedTextView ctv_contact = holder.obtainView(R.id.ctv_contact);
        if (ctv_contact != null) {
            if (isSelectable() && ctv_contact.getVisibility() != View.VISIBLE) {
                ctv_contact.setVisibility(View.VISIBLE);
            } else if (!isSelectable() && ctv_contact.getVisibility() != View.GONE) {
                ctv_contact.setVisibility(View.GONE);
            }
            ctv_contact.setBackgroundResource(selected ? R.mipmap.checkmark : 0);
        }
    }

}
