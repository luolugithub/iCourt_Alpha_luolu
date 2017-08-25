package com.icourt.alpha.adapter;

import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.icourt.alpha.BuildConfig;
import com.icourt.alpha.R;
import com.icourt.alpha.adapter.baseadapter.BaseArrayRecyclerAdapter;
import com.icourt.alpha.entity.bean.SFileSearchEntity;
import com.icourt.alpha.utils.FileUtils;
import com.icourt.alpha.utils.GlideUtils;
import com.icourt.alpha.utils.IMUtils;
import com.icourt.alpha.utils.SFileTokenUtils;

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
public class SFileSearchAdapter extends BaseArrayRecyclerAdapter<SFileSearchEntity> {

    private SparseBooleanArray expandArray = new SparseBooleanArray();


    @Override
    public boolean bindData(boolean isRefresh, List<SFileSearchEntity> datas) {
        expandArray.clear();
        return super.bindData(isRefresh, datas);
    }

    @Override
    public int bindView(int viewtype) {
        return R.layout.adapter_item_sfile_search;
    }

    /**
     * 是否展开模式
     *
     * @param position
     * @return
     */
    public boolean isItemExpand(int position) {
        return expandArray.get(position, false);
    }

    /**
     * 展开单个个item
     *
     * @param position
     */
    public void expandItem(int position) {
        expandArray.put(position, true);
        this.notifyDataSetChanged();
    }

    public void closeItem(int position) {
        expandArray.put(position, false);
        this.notifyDataSetChanged();
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
                loadSFileImage(sFileSearchEntity.repo_id, sFileSearchEntity.fullpath, folder_type_iv);
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
            holder.bindChildClick(document_desc_more_tv);
            document_desc_more_tv.setVisibility(expandArray.get(position, false) ? View.VISIBLE : View.GONE);
        }
    }

    private SpannableString getSpanForKeyWord(String content) {
        CharSequence originalText = content;
        String targetText = "<b>.*?</b>";
        String targetStartStr = "<b>";
        String targetEndStr = "</b>";
        SpannableString spannableString = new SpannableString(originalText);
        try {
            Pattern pattern = Pattern.compile(targetText, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(originalText);
            while (matcher.find()) {
                try {
                    int start = matcher.start() + targetStartStr.length();
                    int end = matcher.end() - targetEndStr.length();
                    //循环改变所有的颜色
                    spannableString.setSpan(
                            new ForegroundColorSpan(Color.RED),
                            start,
                            end,
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                } catch (Exception e) {
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return spannableString;
    }

    /**
     * 加载图片
     *
     * @param seaFileRepoId
     * @param view
     */
    protected void loadSFileImage(String seaFileRepoId, String fullPath, ImageView view) {
        if (view == null) return;
        GlideUtils.loadSFilePic(view.getContext(), getSFileImageThumbUrl(seaFileRepoId, fullPath), view);
    }

    /**
     * 获取地址
     *
     * @param fullPath 全路径
     * @return
     */
    protected String getSFileImageThumbUrl(String seaFileRepoId, String fullPath) {
        return String.format("%silaw/api/v2/documents/thumbnailImage?repoId=%s&seafileToken=%s&p=%s&size=%s",
                BuildConfig.API_URL,
                seaFileRepoId,
                SFileTokenUtils.getSFileToken(),
                fullPath,
                150);
    }
}
