package com.icourt.alpha.adapter;

import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.adapter.baseadapter.BaseArrayRecyclerAdapter;
import com.icourt.alpha.entity.bean.IMAtEntity;
import com.icourt.alpha.utils.DateUtils;
import com.icourt.alpha.utils.GlideUtils;
import com.icourt.alpha.utils.SpannableUtils;
import com.icourt.alpha.utils.SystemUtils;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/19
 * version 1.0.0
 */
public class MyAtedAdapter extends BaseArrayRecyclerAdapter<IMAtEntity> {
    @Override
    public int bindView(int viewtype) {
        return R.layout.adapter_my_ated;
    }


    @Override
    public void onBindHoder(ViewHolder holder, IMAtEntity imAtEntity, int position) {
        if (imAtEntity == null) return;
        ImageView at_user_iv = holder.obtainView(R.id.at_user_iv);
        TextView at_user_tv = holder.obtainView(R.id.at_user_tv);
        TextView at_time_tv = holder.obtainView(R.id.at_time_tv);
        TextView at_content_tv = holder.obtainView(R.id.at_content_tv);
        GlideUtils.loadUser(at_user_iv.getContext(), imAtEntity.pic, at_user_iv);
        at_user_tv.setText(imAtEntity.createName);
        at_time_tv.setText(DateUtils.getTimeShowString(imAtEntity.createDate, true));
        if (imAtEntity.content != null && !TextUtils.isEmpty(imAtEntity.content.content)) {
            String originalText = imAtEntity.content.content;
            String targetText = null;
            try {
                targetText = originalText.substring(originalText.indexOf("@"), originalText.indexOf(" "));
            } catch (IndexOutOfBoundsException e) {
            }
            if (TextUtils.isEmpty(targetText) && originalText.startsWith("@")) {
                targetText = originalText;
            }
            SpannableUtils.setTextForegroundColorSpan(at_content_tv,
                    originalText,
                    targetText,
                    SystemUtils.getColor(at_content_tv.getContext(), R.color.alpha_font_color_orange));
        }
    }
}
