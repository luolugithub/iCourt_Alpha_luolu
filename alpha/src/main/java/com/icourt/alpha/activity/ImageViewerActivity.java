package com.icourt.alpha.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
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
import com.icourt.alpha.BuildConfig;
import com.icourt.alpha.R;
import com.icourt.alpha.adapter.baseadapter.BasePagerAdapter;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.constants.DownloadConfig;
import com.icourt.alpha.entity.bean.ISeaFile;
import com.icourt.alpha.utils.FileUtils;
import com.icourt.alpha.utils.GlideUtils;
import com.icourt.alpha.utils.SFileTokenUtils;
import com.icourt.alpha.utils.UrlUtils;
import com.icourt.alpha.widget.dialog.BottomActionDialog;

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
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static com.icourt.alpha.utils.GlideUtils.canLoadImage;

/**
 * Description  图片查看器 区别于娱乐模式的查看
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/9/1
 * version 2.1.0
 */
public class ImageViewerActivity extends ImageViewBaseActivity {
    private static final String KEY_SELECT_POS = "key_select_pos";//多个图片地址 跳转到某一条
    private static final String KEY_SEA_FILE_IMAGES = "key_sea_File_Images";//seafile图片
    @BindView(R.id.titleAction)
    ImageView titleAction;
    @BindView(R.id.main_content)
    LinearLayout mainContent;
    @BindView(R.id.download_img)
    ImageView downloadImg;


    public static void launch(@NonNull Context context,
                              ArrayList<? extends ISeaFile> seaFileImages,
                              int selectPos) {
        if (context == null) return;
        if (seaFileImages == null || seaFileImages.isEmpty()) return;
        if (selectPos < 0) {
            selectPos = 0;
        } else if (selectPos >= seaFileImages.size()) {
            selectPos = seaFileImages.size() - 1;
        }
        Intent intent = new Intent(context, ImageViewerActivity.class);
        intent.putExtra(KEY_SEA_FILE_IMAGES, seaFileImages);
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
    int selectPos;
    ImagePagerAdapter imagePagerAdapter;
    Handler mHandler = new Handler();
    ArrayList<ISeaFile> seaFileImages;

    /**
     * 获取图片缩略图
     *
     * @param seaFileRepoId
     * @param seaFileFullPath
     * @param size
     * @return
     */
    protected String getSFileImageUrl(String seaFileRepoId, String seaFileFullPath, int size) {
        return String.format("%silaw/api/v2/documents/thumbnailImage?repoId=%s&seafileToken=%s&size=%s&p=%s",
                BuildConfig.API_URL,
                seaFileRepoId,
                SFileTokenUtils.getSFileToken(),
                size,
                UrlUtils.encodeUrl(seaFileFullPath))
                .toString();
    }

    /**
     * 图片查看适配器
     */
    class ImagePagerAdapter extends BasePagerAdapter<ISeaFile> implements PhotoViewAttacher.OnViewTapListener {

        /**
         * 获取缩略图地址
         *
         * @param iSeaFile
         * @return
         */
        public String getThumbImageUrl(ISeaFile iSeaFile) {
            if (iSeaFile == null) return null;
            return getSFileImageUrl(iSeaFile.getSeaFileRepoId(), iSeaFile.getSeaFileFullPath(), 800);
        }

        /**
         * 获取原图地址
         *
         * @param pos
         * @return
         */
        public String getOriginalImageUrl(int pos) {
            ISeaFile iSeaFile = getItem(pos);
            return getOriginalImageUrl(iSeaFile);
        }

        /**
         * 获取原图地址
         *
         * @param iSeaFile
         * @return
         */
        public String getOriginalImageUrl(ISeaFile iSeaFile) {
            if (iSeaFile == null) return null;
            return getSFileImageUrl(iSeaFile.getSeaFileRepoId(), iSeaFile.getSeaFileFullPath(), Integer.MAX_VALUE);
        }


        @Override
        public void bindDataToItem(ISeaFile o, ViewGroup container, View itemView, int pos) {
            final PhotoView touchImageView = itemView.findViewById(R.id.imageView);
            touchImageView.setMaximumScale(5.0f);
            final View imgLookOriginalTv = itemView.findViewById(R.id.img_look_original_tv);
            final String bigUrl = getOriginalImageUrl(o);
            imgLookOriginalTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    loadBigImage(bigUrl, touchImageView, imgLookOriginalTv);
                }
            });
            touchImageView.setOnViewTapListener(this);

            //已经缓存了原图
            String picSavePath = getPicSavePath(o);
            if (FileUtils.isFileExists(picSavePath)) {
                imgLookOriginalTv.setVisibility(View.GONE);
                Glide.with(getContext())
                        .load(picSavePath)
                        .error(R.mipmap.filetype_image)
                        .into(touchImageView);
                return;
            }

            String thumbImageUrl = getThumbImageUrl(o);
            if (FileUtils.isGif(o.getSeaFileFullPath())) {
                if (GlideUtils.canLoadImage(getContext())) {
                    Glide.with(getContext())
                            .load(thumbImageUrl)
                            .error(R.mipmap.filetype_image)
                            .into(touchImageView);
                }
            } else {
                if (canLoadImage(getContext())) {
                    Glide.with(getContext())
                            .load(thumbImageUrl)
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
                            })
                            .dontAnimate()
                            .into(touchImageView);

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
                            if (!canLoadImage(getContext())) return;

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
            if (canLoadImage(getContext())) {
                Glide.with(getContext())
                        .load(bigUrl)
                        .placeholder(imageView.getDrawable())
                        .error(R.mipmap.filetype_image)
                        .dontAnimate()
                        .into(imageView);
            }
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
            } else {
                titleView.setVisibility(View.VISIBLE);
                mainContent.setBackgroundColor(Color.WHITE);
                downloadImg.setVisibility(View.GONE);
            }
            notifyDataSetChanged();
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
        seaFileImages = (ArrayList<ISeaFile>) getIntent().getSerializableExtra(KEY_SEA_FILE_IMAGES);
        selectPos = getIntent().getIntExtra(KEY_SELECT_POS, 0);
        viewPager.setAdapter(imagePagerAdapter = new ImagePagerAdapter());
        imagePagerAdapter.setCanupdateItem(true);
        imagePagerAdapter.bindData(true, seaFileImages);
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                setTitle(FileUtils.getFileName(seaFileImages.get(position).getSeaFileFullPath()));
            }
        });
        if (selectPos < imagePagerAdapter.getCount()) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    viewPager.setCurrentItem(selectPos, false);
                }
            }, 20);
        }
        setTitle(FileUtils.getFileName(FileUtils.getFileName(seaFileImages.get(0).getSeaFileFullPath())));
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
                //下载原图
                ISeaFile item = imagePagerAdapter.getItem(viewPager.getCurrentItem());
                if (item != null) {
                    String originalImageUrl = imagePagerAdapter.getOriginalImageUrl(item);
                    downloadFile(originalImageUrl, getPicSavePath(item));
                }
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

        ISeaFile item = imagePagerAdapter.getItem(viewPager.getCurrentItem());
        if (item == null) {
            return;
        }

        //使用原图
        final String originalImageUrl = imagePagerAdapter.getOriginalImageUrl(item);
        final String picSavePath = getPicSavePath(item);

        new BottomActionDialog(getContext(),
                null,
                Arrays.asList("保存图片", "转发给同事", "分享", "保存到项目资料库"),
                new BottomActionDialog.OnActionItemClickListener() {
                    @Override
                    public void onItemClick(BottomActionDialog dialog, BottomActionDialog.ActionItemAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
                        dialog.dismiss();
                        switch (position) {
                            case 0:
                                downloadFile(originalImageUrl, picSavePath);
                                break;
                            case 1:
                                shareHttpFile2Friends(originalImageUrl, picSavePath);
                                break;
                            case 2:
                                shareHttpFile(originalImageUrl, picSavePath);
                                break;
                            case 3:
                                shareHttpFile2repo(originalImageUrl, picSavePath);
                                break;
                        }
                    }
                })
                .show();
    }


    /**
     * 获取保存路径
     *
     * @param item
     * @return
     */
    private String getPicSavePath(@NonNull ISeaFile item) {
        return DownloadConfig.getSeaFileDownloadPath(getLoginUserId(), item);
    }


    @Override
    protected void onDestroy() {
        mHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }
}
