package com.icourt.alpha.adapter;

import android.graphics.Color;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.entity.bean.SFileSearchEntity;
import com.icourt.alpha.utils.BugUtils;
import com.icourt.alpha.utils.FileUtils;
import com.icourt.alpha.utils.IMUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/8/20
 * version 2.1.0
 */
public class SFileSearchAdapter extends SeaFileImageBaseAdapter<SFileSearchEntity> {


    @Override
    public int bindView(int viewtype) {
        return R.layout.adapter_item_sfile_search;
    }


    @Override
    public void onBindHoder(ViewHolder holder, SFileSearchEntity sFileSearchEntity, int position) {
        if (sFileSearchEntity == null) return;
        ImageView folder_type_iv = holder.obtainView(R.id.folder_type_iv);
        ImageView document_detail_iv = holder.obtainView(R.id.document_detail_iv);
        TextView document_title_tv = holder.obtainView(R.id.document_title_tv);
        TextView document_desc_tv = holder.obtainView(R.id.document_desc_tv);
        TextView document_desc_more_tv = holder.obtainView(R.id.document_desc_more_tv);


        holder.bindChildClick(document_detail_iv);
        document_detail_iv.setVisibility(sFileSearchEntity.is_dir ? View.GONE : View.VISIBLE);

        document_title_tv.setText(sFileSearchEntity.name);

        //显示文件类别图片
        if (sFileSearchEntity.is_dir) {
            folder_type_iv.setImageResource(R.mipmap.folder);
        } else {//文件是图片加载缩略图
            if (IMUtils.isPIC(sFileSearchEntity.name)) {
                loadSFileImage(sFileSearchEntity, folder_type_iv);
            } else {
                folder_type_iv.setImageResource(FileUtils.getSFileIcon(sFileSearchEntity.name));
            }
        }

        //资料库/目录路径  eg. "我的资料库/img/"
        String fileDir = sFileSearchEntity.fullpath;
        if (!sFileSearchEntity.is_dir) {
            if (!TextUtils.isEmpty(fileDir)) {
                int indexOf = fileDir.lastIndexOf("/");
                if (indexOf == 0) {
                    fileDir = "/";
                } else if (indexOf > 0) {
                    fileDir = fileDir.substring(0, indexOf);
                }
            }
        }
        document_desc_tv.setText(String.format("%s%s", sFileSearchEntity.repo_name, fileDir));

        if (!sFileSearchEntity.isSearchContent()) {
            document_desc_more_tv.setVisibility(View.GONE);
        } else {
            document_desc_more_tv.setText(getSpanForKeyWord(sFileSearchEntity.content_highlight));
        }
    }

    private SpannableStringBuilder getSpanForKeyWord(String content) {
        CharSequence originalText = TextUtils.isEmpty(content) ? "" : content;
        String targetText = "<b>.*?</b>";
        String targetStartStr = "<b>";
        String targetEndStr = "</b>";
        SpannableStringBuilder spannableString = new SpannableStringBuilder();
        try {
            String[] split = Pattern.compile(targetText, Pattern.CASE_INSENSITIVE).split(originalText);

            List<String> filterStrings = new ArrayList<>();
            Matcher matcher = Pattern.compile(targetText, Pattern.CASE_INSENSITIVE).matcher(originalText);
            while (matcher.find()) {
                String group = matcher.group();
                filterStrings.add(group.substring(targetStartStr.length(), group.length() - targetEndStr.length()));
            }

            for (int i = 0; i < split.length; i++) {
                String splitei = split[i];
                if (!TextUtils.isEmpty(splitei)) {
                    //需要转义 否则空格太多
                    splitei = Html.fromHtml(splitei).toString();
                }
                spannableString.append(splitei);
                int start = spannableString.length();
                String unitStr = i >= filterStrings.size() ? "" : filterStrings.get(i);
                spannableString.append(unitStr);
                int end = spannableString.length();
                spannableString.setSpan(
                        new ForegroundColorSpan(Color.RED),
                        start,
                        end,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        } catch (Throwable e) {
            e.printStackTrace();
            BugUtils.bugSync("文档搜索描红错误", e);

            return new SpannableStringBuilder(content);
        }
        return spannableString;
    }

}
