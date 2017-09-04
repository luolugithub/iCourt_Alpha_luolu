package com.icourt.alpha.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.icourt.alpha.R;
import com.icourt.alpha.adapter.baseadapter.BasePagerAdapter;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.base.BaseUmengActivity;
import com.icourt.alpha.fragment.dialogfragment.ContactShareDialogFragment;
import com.icourt.alpha.fragment.dialogfragment.ProjectSaveFileDialogFragment;
import com.icourt.alpha.utils.FileUtils;
import com.icourt.alpha.utils.GlideUtils;
import com.icourt.alpha.utils.Md5Utils;
import com.icourt.alpha.utils.StringUtils;
import com.icourt.alpha.widget.dialog.BottomActionDialog;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.finalteam.galleryfinal.widget.zoonview.PhotoView;
import cn.finalteam.galleryfinal.widget.zoonview.PhotoViewAttacher;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.icourt.alpha.utils.FileUtils.isFileExists;

/**
 * Description  图片查看器 区别于娱乐模式的查看
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/9/1
 * version 2.1.0
 */
public class ImageViewerActivity extends BaseUmengActivity {
    private static final int CODE_PERMISSION_FILE = 1009;
    private static final String KEY_BIG_URLS = "key_big_urls";//多个大图地址
    private static final String KEY_SMALL_URLS = "key_small_urls";//多个小图地址
    private static final String KEY_SELECT_POS = "key_select_pos";//多个图片地址 跳转到某一条
    private static final String CACHE_DIR = FileUtils.dirFilePath;

    @BindView(R.id.titleAction)
    ImageView titleAction;
    @BindView(R.id.main_content)
    LinearLayout mainContent;
    @BindView(R.id.download_img)
    ImageView downloadImg;

    public static void launch(@NonNull Context context,
                              ArrayList<String> smallUrls,
                              ArrayList<String> bigUrls,
                              int selectPos) {
        if (context == null) return;
        if (smallUrls == null || smallUrls.isEmpty()) return;
        if (selectPos < 0) {
            selectPos = 0;
        } else if (selectPos >= smallUrls.size()) {
            selectPos = bigUrls.size() - 1;
        }
        Intent intent = new Intent(context, ImageViewerActivity.class);
        intent.putStringArrayListExtra(KEY_SMALL_URLS, smallUrls);
        intent.putStringArrayListExtra(KEY_BIG_URLS, bigUrls);
        intent.putExtra(KEY_SELECT_POS, selectPos);
        context.startActivity(intent);
    }

    @BindView(R.id.titleBack)
    ImageView titleBack;
    @BindView(R.id.titleContent)
    TextView titleContent;
    @BindView(R.id.titleView)
    AppBarLayout titleView;
    @BindView(R.id.viewPager)
    ViewPager viewPager;
    ArrayList<String> bigUrls;
    ArrayList<String> smallUrls;
    int selectPos;
    ImagePagerAdapter imagePagerAdapter;
    Handler mHandler = new Handler();
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
     * 图片查看适配器
     */
    class ImagePagerAdapter extends BasePagerAdapter<String> implements PhotoViewAttacher.OnViewTapListener {


        /**
         * 获取大图的地址
         *
         * @param pos
         * @return
         */
        private String getBigImageUrl(int pos) {
            if (bigUrls != null && pos < bigUrls.size()) {
                return bigUrls.get(pos);
            }
            return null;
        }

        @Override
        public void bindDataToItem(String s, ViewGroup container, final View itemView, int pos) {
            final PhotoView touchImageView = itemView.findViewById(R.id.imageView);
            final View imgLookOriginalTv = itemView.findViewById(R.id.img_look_original_tv);
            final String bigUrl = getBigImageUrl(pos);
            imgLookOriginalTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    loadBigImage(bigUrl, touchImageView, imgLookOriginalTv);
                }
            });
            touchImageView.setOnViewTapListener(this);
            if (FileUtils.isGif(s)) {
                GlideUtils.loadSFilePic(getContext(), s, touchImageView);
            } else {
                if (GlideUtils.canLoadImage(getContext())) {
                    Glide.with(getContext())
                            .load(s)
                            .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                            .error(R.mipmap.filetype_image)
                            .listener(new RequestListener<String, GlideDrawable>() {
                                @Override
                                public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                    //有原图
                                    if (!TextUtils.isEmpty(bigUrl)
                                            && resource != null
                                            && (resource.getIntrinsicHeight() >= 800 || resource.getIntrinsicWidth() >= 800)) {
                                        checkAndLoadBigImage(bigUrl, touchImageView, imgLookOriginalTv);
                                    } else {
                                        imgLookOriginalTv.setVisibility(View.GONE);
                                    }
                                    return false;
                                }
                            }).into(touchImageView);

                }
            }
        }

        /**
         * 检查和加载原图
         *
         * @param bigUrl
         * @param imageView
         */
        private void checkAndLoadBigImage(final String bigUrl, final ImageView imageView, final View imgLookOriginalTv) {
            Observable.create(new ObservableOnSubscribe<Boolean>() {
                @Override
                public void subscribe(ObservableEmitter<Boolean> e) throws Exception {
                    if (e.isDisposed()) return;
                    File file = null;
                    try {
                        file = Glide.with(getContext())
                                .load(bigUrl)
                                .downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                                .get(200, TimeUnit.MILLISECONDS);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    e.onNext(file != null && file.exists());
                    e.onComplete();
                }
            }).compose(ImageViewerActivity.this.<Boolean>bindToLifecycle())
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new io.reactivex.functions.Consumer<Boolean>() {
                        @Override
                        public void accept(Boolean aBoolean) throws Exception {
                            if (!GlideUtils.canLoadImage(getContext())) return;

                            //大图 已经缓存
                            if (aBoolean != null && aBoolean.booleanValue()) {
                                loadBigImage(bigUrl, imageView, imgLookOriginalTv);
                            } else {
                                if (isFullScreenMode()) {
                                    imgLookOriginalTv.setVisibility(View.VISIBLE);
                                } else {
                                    imgLookOriginalTv.setVisibility(View.GONE);
                                }
                            }
                        }
                    });
        }

        /**
         * 加载大图
         *
         * @param bigUrl
         * @param imageView
         * @param imgLookOriginalTv
         */
        private void loadBigImage(final String bigUrl, ImageView imageView, final View imgLookOriginalTv) {
            imgLookOriginalTv.setVisibility(View.GONE);
            GlideUtils.loadSFilePic(getContext(), bigUrl, imageView);
        }

        @Override
        public int bindView(int pos) {
            return R.layout.adapter_item_image_pager;
        }


        /**
         * 是全屏模式
         *
         * @return
         */
        private boolean isFullScreenMode() {
            return titleView.getVisibility() != View.VISIBLE;
        }

        @Override
        public void onViewTap(View view, float v, float v1) {
            if (!isFullScreenMode()) {
                titleView.setVisibility(View.GONE);
                mainContent.setBackgroundColor(Color.BLACK);
                downloadImg.setVisibility(View.VISIBLE);
                notifyDataSetChanged();
            } else {
                titleView.setVisibility(View.VISIBLE);
                mainContent.setBackgroundColor(Color.WHITE);
                downloadImg.setVisibility(View.GONE);
                notifyDataSetChanged();
            }
        }
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewer);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void initView() {
        super.initView();
        titleAction.setImageResource(R.mipmap.header_icon_more);
        smallUrls = getIntent().getStringArrayListExtra(KEY_SMALL_URLS);
        bigUrls = getIntent().getStringArrayListExtra(KEY_BIG_URLS);
        selectPos = getIntent().getIntExtra(KEY_SELECT_POS, 0);
        viewPager.setAdapter(imagePagerAdapter = new ImagePagerAdapter());
        imagePagerAdapter.bindData(true, smallUrls);
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                setTitle(FileUtils.getFileName(smallUrls.get(position)));
            }
        });
        if (selectPos < imagePagerAdapter.getCount()) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    viewPager.setCurrentItem(selectPos, false);
                }
            }, 1_00);
        }
        setTitle(FileUtils.getFileName(smallUrls.get(0)));
    }

    @OnClick({R.id.titleAction,
            R.id.download_img})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.titleAction:
                showBottomMenuDialog();
                break;
            case R.id.download_img:
                checkPermissionOrDownload();
                break;
            default:
                super.onClick(v);
                break;
        }
    }

    private void showBottomMenuDialog() {
        View itemView = imagePagerAdapter.getItemView(viewPager.getCurrentItem());
        final PhotoView imageView = itemView.findViewById(R.id.imageView);
        final Drawable drawable = imageView.getDrawable();
        if (drawable == null) return;
        new BottomActionDialog(getContext(),
                null,
                Arrays.asList("保存图片", "转发给同事", "分享", "保存到项目资料库"),
                new BottomActionDialog.OnActionItemClickListener() {
                    @Override
                    public void onItemClick(BottomActionDialog dialog, BottomActionDialog.ActionItemAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
                        dialog.dismiss();
                        switch (position) {
                            case 0:
                                checkPermissionOrDownload();
                                break;
                            case 1:
                                transformFriends(drawable);
                                break;
                            case 2:
                                shareImage2WeiXin(drawable);
                                break;
                            case 3:
                                savedImport2Project(drawable);
                                break;
                        }
                    }
                })
                .show();
    }

    protected void shareImage2WeiXin(@NonNull Drawable drawable) {
        if (drawable == null) return;
        shareImage2WeiXin(FileUtils.drawableToBitmap(drawable));
    }

    /**
     * 转发给同时
     */
    private void transformFriends(Drawable drawable) {
        if (drawable == null) return;
        if (!checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            reqPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, "我们需要文件写入权限!", CODE_PERMISSION_FILE);
            return;
        }
        //先保存
        String url = imagePagerAdapter.getItem(viewPager.getCurrentItem());
        saveDrawableIntoSD(drawable,
                getEncodeName(url),
                new Observer<String>() {
                    @Override
                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable disposable) {

                    }

                    @Override
                    public void onNext(@io.reactivex.annotations.NonNull String s) {
                        showContactShareDialogFragment(s);
                    }

                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable throwable) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    /**
     * 展示联系人转发对话框
     *
     * @param filePath
     */
    public void showContactShareDialogFragment(String filePath) {
        String tag = ContactShareDialogFragment.class.getSimpleName();
        FragmentTransaction mFragTransaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);
        if (fragment != null) {
            mFragTransaction.remove(fragment);
        }
        ContactShareDialogFragment.newInstanceFile(filePath, true)
                .show(mFragTransaction, tag);
    }

    /**
     * 检查权限或者下载
     */
    private void checkPermissionOrDownload() {
        if (checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            downloadFile(imagePagerAdapter.getItem(viewPager.getCurrentItem()));
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
        if (url.startsWith("http")) {
            StringBuilder pathBuilder = new StringBuilder(CACHE_DIR);
            pathBuilder.append(File.separator);
            pathBuilder.append(getEncodeName(url));
            return pathBuilder.toString();
        } else {
            return url;
        }
    }

    /**
     * 获取加密的图片名称
     *
     * @param url
     * @return
     */
    private String getEncodeName(String url) {
        String fileName = FileUtils.getFileName(url);
        String fileNameWithoutSuffix = FileUtils.getFileNameWithoutSuffix(fileName);
        String fileSuffix = FileUtils.getFileSuffix(fileName);
        return String.format("%s_%s%s", fileNameWithoutSuffix, Md5Utils.md5(url, url), fileSuffix);
    }


    /**
     * 分享到项目
     *
     * @param drawable
     */
    private void savedImport2Project(final Drawable drawable) {
        if (drawable == null) return;
        if (!checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            reqPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, "我们需要文件写入权限!", CODE_PERMISSION_FILE);
            return;
        }
        showLoadingDialog(null);
        String url = imagePagerAdapter.getItem(viewPager.getCurrentItem());
        saveDrawableIntoSD(drawable, getEncodeName(url), new Observer<String>() {
            @Override
            public void onSubscribe(@io.reactivex.annotations.NonNull Disposable disposable) {

            }

            @Override
            public void onNext(@io.reactivex.annotations.NonNull String s) {
                showProjectSaveFileDialogFragment(s);
            }

            @Override
            public void onError(@io.reactivex.annotations.NonNull Throwable throwable) {
                dismissLoadingDialog();
            }

            @Override
            public void onComplete() {
                dismissLoadingDialog();
            }
        });
    }

    /**
     * 保存图片到sd
     *
     * @param drawable
     * @param fileName
     * @param observer
     */
    private void saveDrawableIntoSD(final Drawable drawable,
                                    final String fileName,
                                    Observer<String> observer) {
        Observable
                .create(new ObservableOnSubscribe<String>() {
                    @Override
                    public void subscribe(ObservableEmitter<String> e) throws Exception {
                        if (e.isDisposed()) return;
                        String path = String.format("%s/%s", CACHE_DIR, fileName);
                        if (FileUtils.isFileExists(fileName)) {
                            e.onNext(path);
                        } else {
                            boolean b = FileUtils.saveBitmap(getContext(), CACHE_DIR, fileName, FileUtils.drawableToBitmap(drawable));
                            if (b) {
                                e.onNext(path);
                            } else {
                                e.onError(new NullPointerException());
                            }

                        }
                        e.onComplete();
                    }
                })
                .compose(this.<String>bindToLifecycle())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    /**
     * 展示项目转发对话框
     *
     * @param filePath
     */
    public void showProjectSaveFileDialogFragment(String filePath) {
        String tag = ProjectSaveFileDialogFragment.class.getSimpleName();
        FragmentTransaction mFragTransaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);
        if (fragment != null) {
            mFragTransaction.remove(fragment);
        }
        ProjectSaveFileDialogFragment.newInstance(filePath, ProjectSaveFileDialogFragment.ALPHA_TYPE)
                .show(mFragTransaction, tag);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults == null) return;
        if (grantResults.length <= 0) return;
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

    @Override
    protected void onDestroy() {
        mHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }
}
