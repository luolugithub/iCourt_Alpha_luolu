package com.icourt.alpha.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.icourt.alpha.R;
import com.icourt.alpha.adapter.baseadapter.BasePagerAdapter;
import com.icourt.alpha.base.BaseActivity;
import com.icourt.alpha.utils.ActionConstants;
import com.icourt.alpha.utils.FileUtils;
import com.icourt.alpha.utils.GlideUtils;
import com.icourt.alpha.utils.Md5Utils;
import com.icourt.alpha.utils.StringUtils;
import com.icourt.alpha.view.HackyViewPager;
import com.icourt.alpha.view.TouchImageView;
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

public class ImagePagerActivity extends BaseActivity implements BasePagerAdapter.OnPagerItemClickListener, BasePagerAdapter.OnPagerItemLongClickListener {

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

    @OnClick({R.id.download_img})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.download_img:
                if (urls != null && urls.length > 0) {
                    if (checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        downloadFile(urls[imagePager.getCurrentItem()]);
                    } else {
                        reqPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, "下载文件需要文件写入权限!", CODE_PERMISSION_FILE);
                    }
                }
                break;
            default:
                super.onClick(v);
                break;
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

    /**
     * imageView 点击事件
     *
     * @param adapter
     * @param v       点击的控件
     * @param pos     点击的位置[在adapter中]
     */
    @Override
    public void OnItemClick(BasePagerAdapter adapter, View v, int pos) {

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
    public boolean OnItemLongClick(BasePagerAdapter adapter, View v, int pos) {
        return false;
    }

    class ImagePagerAdapter extends BasePagerAdapter<String> {

        @Override
        public int bindView(int pos) {
            return R.layout.adapter_item_image_pager;
        }

        @Override
        public void bindDataToItem(String s, ViewGroup container, View itemView, int pos) {
            TouchImageView touchImageView = (TouchImageView) itemView.findViewById(R.id.imageView);
            if (GlideUtils.canLoadImage(getContext())) {
                log("---------->load url:pos:" + pos + "  url:" + s);
                Glide.with(getContext())
                        .load(s)
                        .into(touchImageView);
            }
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
