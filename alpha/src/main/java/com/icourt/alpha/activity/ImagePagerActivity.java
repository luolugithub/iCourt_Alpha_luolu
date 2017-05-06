package com.icourt.alpha.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.icourt.alpha.R;
import com.icourt.alpha.adapter.baseadapter.BasePagerAdapter;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.base.BaseUmengActivity;
import com.icourt.alpha.utils.ActionConstants;
import com.icourt.alpha.utils.FileUtils;
import com.icourt.alpha.utils.GlideUtils;
import com.icourt.alpha.utils.Md5Utils;
import com.icourt.alpha.utils.StringUtils;
import com.icourt.alpha.utils.UrlUtils;
import com.icourt.alpha.view.HackyViewPager;
import com.icourt.alpha.view.TouchImageView;
import com.icourt.alpha.widget.dialog.BottomActionDialog;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * @author xuanyouwu
 * @email xuanyouwu@163.com
 * @time 2016-08-26 10:46
 * <p>
 * 图片浏览器
 */

public class ImagePagerActivity extends BaseUmengActivity implements BasePagerAdapter.OnPagerItemClickListener, BasePagerAdapter.OnPagerItemLongClickListener {

    private static final int CODE_PERMISSION_FILE = 1009;

    private static final String KEY_URLS = "key_urls";
    private static final String KEY_POS = "key_pos";
    @BindView(R.id.imagePager)
    HackyViewPager imagePager;
    @BindView(R.id.tvPagerTitle)
    TextView tvPagerTitle;
    ImagePagerAdapter pagerAdapter;
    String[] urls;
    Handler handler = new Handler();
    int realPos;
    private FileDownloadListener picDownloadListener = new FileDownloadListener() {

        @Override
        protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
        }

        @Override
        protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
        }

        @Override
        protected void completed(BaseDownloadTask task) {
            showTopSnackBar("保存成功!");
            if (task != null && !TextUtils.isEmpty(task.getPath())) {
                try {
                    sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(task.getPath()))));
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
        }

        @Override
        protected void error(BaseDownloadTask task, Throwable e) {
            log("----------->图片下载异常:" + StringUtils.throwable2string(e));
            showTopSnackBar(String.format("下载异常!" + StringUtils.throwable2string(e)));
        }

        @Override
        protected void warn(BaseDownloadTask task) {

        }
    };

    /**
     * @param context
     * @param urls
     * @param pos     从0开始
     */
    public static void launch(Context context, String[] urls, int pos) {
        if (context == null) return;
        if (urls == null) return;
        if (urls.length == 0) return;
        if (pos >= 0 && pos < urls.length) {
            Intent intent = new Intent(context, ImagePagerActivity.class);
            intent.putExtra(KEY_URLS, urls);
            intent.putExtra(KEY_POS, pos);
            context.startActivity(intent);
        }
    }

    /**
     * @param context
     * @param urls
     */
    public static void launch(Context context, String[] urls) {
        launch(context, urls, 0);
    }

    /**
     * @param context
     * @param urls
     */
    public static void launch(Context context, List<String> urls) {
        launch(context, urls, 0);
    }

    /**
     * @param context
     * @param urls
     * @param pos     从0开始
     */
    public static void launch(Context context, List<String> urls, int pos) {
        if (context == null) return;
        if (urls == null) return;
        if (urls.size() == 0) return;
        String[] urlsArr = (String[]) urls.toArray(new String[urls.size()]);
        launch(context, urlsArr, pos);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_pager);
        ButterKnife.bind(this);
        initView();

    }

    @Override
    protected void initView() {
        super.initView();
        urls = getIntent().getStringArrayExtra(KEY_URLS);
        realPos = getIntent().getIntExtra(KEY_POS, 0);
        pagerAdapter = new ImagePagerAdapter();
        pagerAdapter.bindData(true, Arrays.asList(urls));
        pagerAdapter.setOnPagerItemClickListener(this);
        pagerAdapter.setOnPagerItemLongClickListener(this);
        imagePager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                tvPagerTitle.setText(String.format("%d/%d", position + 1, pagerAdapter.getCount()));
            }
        });
        imagePager.setAdapter(pagerAdapter);
        tvPagerTitle.setText(String.format("%d/%d", realPos + 1, pagerAdapter.getCount()));
        if (realPos >= 1) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    imagePager.setCurrentItem(realPos, false);
                }
            }, 50);
        }
    }

    /**
     * 获取当前image url
     *
     * @return
     */
    private String getCurrImageUrl() {
        if (urls != null && urls.length > 0) {
            return urls[imagePager.getCurrentItem()];
        }
        return null;
    }

    @OnClick({R.id.download_img})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.download_img:
                checkPermissionOrDownload();
                break;
            default:
                super.onClick(v);
                break;
        }
    }

    /**
     * imageView 点击事件
     *
     * @param adapter
     * @param v       点击的控件
     * @param pos     点击的位置[在adapter中]
     */
    @Override
    public void OnItemClick(BasePagerAdapter adapter, View v, int pos) {
        finish();
    }

    /**
     * imageView 长按事件
     *
     * @param adapter
     * @param v       点击的控件
     * @param pos     点击的位置[在adapter中]
     * @return
     */
    @Override
    public boolean OnItemLongClick(BasePagerAdapter adapter, final View v, final int pos) {
        if (v instanceof ImageView) {
            ImageView imageView = (ImageView) v;
            final Drawable drawable = imageView.getDrawable();
            if (drawable != null) {
                new BottomActionDialog(getContext(),
                        null,
                        Arrays.asList("分享", "转发", "保存到项目"),
                        new BottomActionDialog.OnActionItemClickListener() {
                            @Override
                            public void onItemClick(BottomActionDialog dialog, BottomActionDialog.ActionItemAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
                                dialog.dismiss();
                                switch (position) {
                                    case 0:
                                        shareImage2WeiXin(drawable);
                                        break;
                                    case 1:
                                        //TODO  转发到享聊
                                        showTopSnackBar("未完成");
                                        break;
                                    case 2:
                                        //TODO  保存到项目
                                        showTopSnackBar("未完成");
                                        break;
                                }
                            }
                        }).show();
                return true;
            }
        }
        return false;
    }


    /**
     * 检查权限或者下载
     */
    private void checkPermissionOrDownload() {
        if (checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            downloadFile(getCurrImageUrl());
        } else {
            reqPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, "下载文件需要文件写入权限!", CODE_PERMISSION_FILE);
        }
    }

    /**
     * 图片下载
     *
     * @param url
     */
    private void downloadFile(String url) {
        if (TextUtils.isEmpty(url)) {
            showTopSnackBar("下载地址为null");
            return;
        }
        if (!FileUtils.sdAvailable()) {
            showTopSnackBar("sd卡不可用!");
            return;
        }
        if (isFileExists(url)) {
            showTopSnackBar("文件已保存");
            return;
        }
        FileDownloader
                .getImpl()
                .create(url)
                .setPath(getPicSavePath(url))
                .setListener(picDownloadListener).start();
    }

    private String getPicSavePath(String url) {
        StringBuilder pathBuilder = new StringBuilder(Environment.getExternalStorageDirectory().getAbsolutePath());
        pathBuilder.append(File.separator);
        pathBuilder.append(ActionConstants.FILE_DOWNLOAD_PATH);
        pathBuilder.append(File.separator);
        pathBuilder.append(Md5Utils.md5(url, url));
        pathBuilder.append(".png");
        return pathBuilder.toString();
    }

    /**
     * 是否文件已经存在
     *
     * @param url
     * @return
     */
    private boolean isFileExists(String url) {
        return FileUtils.isFileExists(getPicSavePath(url));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case CODE_PERMISSION_FILE:
                if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    showTopSnackBar("文件写入权限被拒绝!");
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
        }

    }


    class ImagePagerAdapter extends BasePagerAdapter<String> {

        @Override
        public int bindView(int pos) {
            return R.layout.adapter_item_image_pager;
        }

        @Override
        public void bindDataToItem(final String s, ViewGroup container, View itemView, final int pos) {
            final TouchImageView touchImageView = (TouchImageView) itemView.findViewById(R.id.imageView);
            final TextView img_look_original_tv = (TextView) itemView.findViewById(R.id.img_look_original_tv);
            img_look_original_tv.setVisibility(isLoadOriginalPicUrl(s) ? View.GONE : View.VISIBLE);
            img_look_original_tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String originalPicUrl = getOriginalPicUrl(s);
                    putItem(pos, originalPicUrl);
                    if (GlideUtils.canLoadImage(getContext())) {
                        img_look_original_tv.setVisibility(View.GONE);
                        log("---------->load Original url:pos:" + pos + "  url:" + s);
                        Glide.with(getContext())
                                .load(originalPicUrl)
                                .thumbnail(0.5f)//先拿一半
                                .into(touchImageView);
                    }
                }
            });
            if (GlideUtils.canLoadImage(getContext())) {
                log("---------->load url:pos:" + pos + "  url:" + s);
                Glide.with(getContext())
                        .load(s)
                        .thumbnail(0.5f)//先拿一半
                        .into(touchImageView);
            }
        }

        /**
         * 是否加载的高清原图 包含sfile 并且不含width
         * https://alphalawyer.cn/ilaw/api/v2/file/download?sFileId=64880&token=xxx&width=480;
         *
         * @return
         */
        private boolean isLoadOriginalPicUrl(String url) {
            if (!TextUtils.isEmpty(url)) {
                if (!TextUtils.isEmpty(UrlUtils.getParam("sFileId", url))
                        && !TextUtils.isEmpty(UrlUtils.getParam("width", url))) {
                    return false;
                }
            }
            return true;
        }

        /**
         * 获取原图地址
         *
         * @param url
         * @return
         */
        private String getOriginalPicUrl(String url) {
            return UrlUtils.removeParam("width", url);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            if (GlideUtils.canLoadImage(getContext())) {
                try {
                    Glide.clear((View) object);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }
            }
            super.destroyItem(container, position, object);
        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }
}
