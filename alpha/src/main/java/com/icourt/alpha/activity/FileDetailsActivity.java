package com.icourt.alpha.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.icourt.alpha.BuildConfig;
import com.icourt.alpha.R;
import com.icourt.alpha.base.BaseActivity;
import com.icourt.alpha.constants.Const;
import com.icourt.alpha.entity.bean.IMFileEntity;
import com.icourt.alpha.utils.ActionConstants;
import com.icourt.alpha.utils.DateUtils;
import com.icourt.alpha.utils.FileUtils;
import com.icourt.alpha.utils.GlideUtils;
import com.icourt.alpha.utils.IMUtils;
import com.icourt.alpha.utils.SpannableUtils;

import java.io.Serializable;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/18
 * version 1.0.0
 */
public class FileDetailsActivity extends BaseActivity {

    private static final String KEY_FILE_INFO = "key_file_info";
    IMFileEntity item;
    @BindView(R.id.titleBack)
    ImageView titleBack;
    @BindView(R.id.titleContent)
    TextView titleContent;
    @BindView(R.id.titleAction)
    CheckedTextView titleAction;
    @BindView(R.id.titleView)
    AppBarLayout titleView;
    @BindView(R.id.file_from_user_iv)
    ImageView fileFromUserIv;
    @BindView(R.id.file_from_user_tv)
    TextView fileFromUserTv;
    @BindView(R.id.file_img)
    ImageView fileImg;
    @BindView(R.id.file_type_iv)
    ImageView fileTypeIv;
    @BindView(R.id.file_title_tv)
    TextView fileTitleTv;
    @BindView(R.id.file_size_tv)
    TextView fileSizeTv;
    @BindView(R.id.file_type_comm_ll)
    LinearLayout fileTypeCommLl;
    @BindView(R.id.file_upload_time)
    TextView fileUploadTime;
    @BindView(R.id.file_from_tv)
    TextView fileFromTv;

    public static void launch(@NonNull Context context, IMFileEntity imFileEntity) {
        if (context == null) return;
        if (imFileEntity == null) return;
        Intent intent = new Intent(context, FileDetailsActivity.class);
        intent.putExtra(KEY_FILE_INFO, imFileEntity);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_details);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void initView() {
        super.initView();
        setTitle("详情");
        TextView titleActionTextView = getTitleActionTextView();
        if (titleActionTextView != null) {
            titleActionTextView.setText("跳转");
        }
        Serializable serializableExtra = getIntent().getSerializableExtra(KEY_FILE_INFO);
        if (serializableExtra instanceof IMFileEntity) {
            item = (IMFileEntity) serializableExtra;
        }
        if (item != null) {
            GlideUtils.loadUser(getContext(), item.pic, fileFromUserIv);
            /**
             * Open==1 群组。group name
             * open==0 单聊 createName
             */
            String targetText = item.open == 1 ? item.groupName : item.createName;
            String originalText = String.format("来自: %s", targetText);
            SpannableUtils.setTextForegroundColorSpan(fileFromTv, originalText, targetText, getContextColor(R.color.alpha_font_color_black));
            fileFromUserTv.setText(item.createName);
            fileUploadTime.setText(DateUtils.getTimeShowString(item.createDate, true));
        }
        //纯图片
        if (isPic()) {
            fileTypeCommLl.setVisibility(View.GONE);
            fileImg.setVisibility(View.VISIBLE);
            if (GlideUtils.canLoadImage(getContext())) {
                Glide.with(getContext())
                        .load(getCombPicUrl((item != null && item.content != null)
                                ? item.content.path : ""))
                        .placeholder(R.drawable.bg_round_rect_gray)
                        .into(fileImg);
            }
        } else {
            fileTypeCommLl.setVisibility(View.VISIBLE);
            fileImg.setVisibility(View.GONE);
            if (item != null && item.content != null) {
                fileTypeIv.setImageResource(getFileIcon40(item.content.file));
                fileTitleTv.setText(item.content.file);
                fileSizeTv.setText(FileUtils.kbFromat(item.content.size));
            }
        }
    }

    /**
     * 获取组合拼接的图片原地址
     *
     * @param path
     * @return
     */
    private String getCombPicUrl(String path) {
        StringBuilder urlBuilder = new StringBuilder(BuildConfig.HOST_URL);
        urlBuilder.append(Const.HTTP_DOWNLOAD_FILE);
        urlBuilder.append("?sFileId=");
        urlBuilder.append(path);
        urlBuilder.append("&token=");
        urlBuilder.append(getUserToken());
        urlBuilder.append("&width=");
        urlBuilder.append("480");
        return urlBuilder.toString();
    }

    /**
     * 获取原图地址
     *
     * @param path
     * @return
     */
    private String getOriginalPicUrl(String path) {
        StringBuilder urlBuilder = new StringBuilder(BuildConfig.HOST_URL);
        urlBuilder.append(Const.HTTP_DOWNLOAD_FILE);
        urlBuilder.append("?sFileId=");
        urlBuilder.append(path);
        urlBuilder.append("&token=");
        urlBuilder.append(getUserToken());
        return urlBuilder.toString();
    }

    /**
     * 是否是图片
     *
     * @return
     */
    public boolean isPic() {
        return (item != null
                && item.content != null
                && IMUtils.isPIC(item.content.file));
    }

    /**
     * 获取文件对应图标
     *
     * @param fileName
     * @return
     */
    public static int getFileIcon40(String fileName) {
        if (!TextUtils.isEmpty(fileName) && fileName.length() > 0) {
            String type = fileName.substring(fileName.lastIndexOf(".") + 1);
            if (ActionConstants.resourcesMap40.containsKey(type)) {
                return ActionConstants.resourcesMap40.get(type);
            }
        }
        return R.mipmap.filetype_default_40;
    }

    @OnClick({R.id.file_img, R.id.file_type_comm_ll})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.titleAction:
                //TODO 跳转到聊天
                showTopSnackBar("未完成");
                break;
            case R.id.file_img:
                if (item != null && item.content != null) {
                    ImagePagerActivity.launch(getContext(),
                            new String[]{getOriginalPicUrl(item.content.path)});
                }
                break;
            case R.id.file_type_comm_ll:
                //TODO 文件详情
                showTopSnackBar("未完成");
                break;
            default:
                super.onClick(v);
                break;
        }
    }
}
