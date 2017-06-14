package com.icourt.alpha.adapter;

import android.graphics.Color;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.text.style.URLSpan;
import android.widget.ImageView;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.adapter.baseadapter.BaseArrayRecyclerAdapter;
import com.icourt.alpha.entity.bean.CommentEntity;
import com.icourt.alpha.utils.DateUtils;
import com.icourt.alpha.utils.GlideUtils;
import com.icourt.alpha.utils.UrlUtils;

/**
 * Description   评论列表适配器
 * Company Beijing icourt
 * author  lu.zhao  E-mail:zhaolu@icourt.cc
 * date createTime：17/5/12
 * version 2.0.0
 */

public class CommentListAdapter extends BaseArrayRecyclerAdapter<CommentEntity.CommentItemEntity> {


    @Override
    public int bindView(int viewtype) {
        return R.layout.adapter_item_comment_layout;
    }

    @Override
    public void onBindHoder(ViewHolder holder, CommentEntity.CommentItemEntity commentItemEntity, int position) {
        ImageView photoView = holder.obtainView(R.id.user_photo_image);
        TextView nameView = holder.obtainView(R.id.user_name_tv);
        TextView timeView = holder.obtainView(R.id.create_time_tv);
        TextView contentView = holder.obtainView(R.id.content_tv);
        if (commentItemEntity.createUser != null) {
            GlideUtils.loadUser(photoView.getContext(), commentItemEntity.createUser.pic, photoView);
            nameView.setText(commentItemEntity.createUser.userName);
        }
        timeView.setText(DateUtils.getTimeShowString(commentItemEntity.createTime, true));
        holder.bindChildClick(photoView);
        setCommentUrlView(contentView, commentItemEntity.content);

    }

    private void setCommentUrlView(TextView contentView, CharSequence content) {
        if (contentView == null) return;
        if (TextUtils.isEmpty(content)) return;
        if (UrlUtils.isHttpLink(content.toString())) {
            contentView.setText(Html.fromHtml("<a href=\"" + content + "\">" + content + "</a>"));
            contentView.setMovementMethod(LinkMovementMethod.getInstance());
            CharSequence text = contentView.getText();
            if (text instanceof Spannable) {
                int end = text.length();
                Spannable sp = (Spannable) contentView.getText();
                URLSpan[] urls = sp.getSpans(0, end, URLSpan.class);

                SpannableStringBuilder style = new SpannableStringBuilder(text);
                style.clearSpans(); // should clear old spans
                for (URLSpan url : urls) {
                    URLSpan myURLSpan = new URLSpan(url.getURL());
                    style.setSpan(myURLSpan, sp.getSpanStart(url), sp.getSpanEnd(url), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    style.setSpan(new ForegroundColorSpan(Color.RED), sp.getSpanStart(url), sp.getSpanEnd(url), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);//设置前景色为红色
                }
                contentView.setText(style);
            }
        } else {
            contentView.setText(content);
        }
    }

}
