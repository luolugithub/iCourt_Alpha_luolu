package com.icourt.alpha.activity;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.gson.JsonParseException;
import com.icourt.alpha.BuildConfig;
import com.icourt.alpha.R;
import com.icourt.alpha.adapter.baseadapter.BasePagerAdapter;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.base.BaseUmengActivity;
import com.icourt.alpha.constants.Const;
import com.icourt.alpha.entity.bean.AlphaUserInfo;
import com.icourt.alpha.entity.bean.IMMessageCustomBody;
import com.icourt.alpha.entity.bean.SFileImageInfoEntity;
import com.icourt.alpha.fragment.dialogfragment.ContactShareDialogFragment;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.utils.ActionConstants;
import com.icourt.alpha.utils.FileUtils;
import com.icourt.alpha.utils.GlideUtils;
import com.icourt.alpha.utils.JsonUtils;
import com.icourt.alpha.utils.Md5Utils;
import com.icourt.alpha.utils.StringUtils;
import com.icourt.alpha.view.HackyViewPager;
import com.icourt.alpha.view.TouchImageView;
import com.icourt.alpha.widget.dialog.BottomActionDialog;
import com.icourt.api.RequestUtils;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Response;

import static com.icourt.alpha.R.id.imageView;
import static com.icourt.alpha.constants.Const.CHAT_TYPE_P2P;
import static com.icourt.alpha.constants.Const.CHAT_TYPE_TEAM;


/**
 * @author xuanyouwu
 * @email xuanyouwu@163.com
 * @time 2016-08-26 10:46
 * <p>
 * 图片浏览器
 */

public class ImagePagerActivity extends BaseUmengActivity implements BasePagerAdapter.OnPagerItemClickListener, BasePagerAdapter.OnPagerItemLongClickListener {
    //收藏的消息列表
    protected final Set<Long> msgCollectedIdsList = new HashSet<>();
    //钉的消息列表
    protected final Set<Long> msgDingedIdsList = new HashSet<>();


    private static final int CODE_PERMISSION_FILE = 1009;

    private static final String KEY_URLS = "key_urls";
    private static final String KEY_S_FILE_INFO = "key_s_file_info";
    private static final String KEY_POS = "key_pos";
    private static final String KEY_CHAT_TYPE = "key_chat_type";
    private static final String KEY_CHAT_ID = "key_chat_id";
    @BindView(R.id.imagePager)
    HackyViewPager imagePager;
    @BindView(R.id.tvPagerTitle)
    TextView tvPagerTitle;
    ImagePagerAdapter pagerAdapter;
    String[] urls;
    ArrayList<SFileImageInfoEntity> sFileImageInfoEntities;
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

    /**
     * 先展示小图 查看原图
     *
     * @param context
     * @param smallUrls
     * @param sFileImageInfoEntities sfile对应详细信息
     */
    public static void launch(Context context,
                              @NonNull List<String> smallUrls,
                              @Nullable ArrayList<SFileImageInfoEntity> sFileImageInfoEntities,
                              int pos,
                              @Nullable View transitionView,
                              @Const.CHAT_TYPE int chatType,
                              String chatId) {
        if (context == null) return;
        if (smallUrls == null) return;
        if (smallUrls.size() == 0) return;
        Intent intent = new Intent(context, ImagePagerActivity.class);
        String[] urlsArr = (String[]) smallUrls.toArray(new String[smallUrls.size()]);
        intent.putExtra(KEY_URLS, urlsArr);
        if (pos < 0) {
            pos = 0;
        } else if (pos >= smallUrls.size()) {
            pos = smallUrls.size() - 1;
        }
        intent.putExtra(KEY_POS, pos);
        intent.putExtra(KEY_CHAT_ID, chatId);
        intent.putExtra(KEY_CHAT_TYPE, chatType);
        if (sFileImageInfoEntities != null) {
            intent.putExtra(KEY_S_FILE_INFO, sFileImageInfoEntities);
        }
        if (context instanceof Activity
                && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
                && transitionView != null) {
            String transitionName = smallUrls.get(pos);
            if (TextUtils.isEmpty(transitionName)) {
                transitionName = "transitionView";
            }
            ViewCompat.setTransitionName(transitionView, transitionName);
            context.startActivity(intent,
                    ActivityOptions.makeSceneTransitionAnimation((Activity) context, transitionView, transitionName).toBundle());
        } else {
            context.startActivity(intent);
        }
    }

    private static void setTransitionView(View transitionView, String transitionName) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
                && transitionView != null) {
            if (TextUtils.isEmpty(transitionName)) {
                transitionName = "transitionView";
            }
            ViewCompat.setTransitionName(transitionView, transitionName);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_pager);
        ButterKnife.bind(this);
        initView();
        getData(true);
    }

    @Override
    protected void initView() {
        super.initView();
        urls = getIntent().getStringArrayExtra(KEY_URLS);
        sFileImageInfoEntities = (ArrayList<SFileImageInfoEntity>) getIntent().getSerializableExtra(KEY_S_FILE_INFO);
        realPos = getIntent().getIntExtra(KEY_POS, 0);
        pagerAdapter = new ImagePagerAdapter();
        pagerAdapter.bindData(true, Arrays.asList(urls));
        pagerAdapter.setOnPagerItemClickListener(this);
        pagerAdapter.setOnPagerItemLongClickListener(this);
        tvPagerTitle.setVisibility(View.GONE);
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

    @Override
    protected void getData(boolean isRefresh) {
        super.getData(isRefresh);
        getMsgDingedIds();
        getMsgCollectedIds();
    }

    @Const.CHAT_TYPE
    protected int getIMChatType() {
        switch (getIntent().getIntExtra(KEY_CHAT_TYPE, 0)) {
            case CHAT_TYPE_P2P:
                return CHAT_TYPE_P2P;
            case CHAT_TYPE_TEAM:
                return CHAT_TYPE_TEAM;
            default:
                return CHAT_TYPE_TEAM;
        }
    }

    protected String getIMChatId() {
        return getIntent().getStringExtra(KEY_CHAT_ID);
    }

    /**
     * 获取被钉的ids列表
     */
    private void getMsgDingedIds() {
        getChatApi().msgQueryAllDingIds(getIMChatType(), getIMChatId())
                .enqueue(new SimpleCallBack<List<Long>>() {
                    @Override
                    public void onSuccess(Call<ResEntity<List<Long>>> call, Response<ResEntity<List<Long>>> response) {
                        if (response.body().result != null) {
                            msgDingedIdsList.clear();
                            msgDingedIdsList.addAll(response.body().result);
                        }
                    }

                    @Override
                    public void defNotify(String noticeStr) {
                        // super.defNotify(noticeStr);
                    }
                });
    }

    /**
     * 是否被钉过
     *
     * @param msgId
     * @return
     */
    private boolean isDinged(long msgId) {
        return msgDingedIdsList.contains(msgId);
    }

    /**
     * 是否收藏过
     *
     * @param msgId
     * @return
     */
    private boolean isCollected(long msgId) {
        return msgCollectedIdsList.contains(msgId);
    }

    /**
     * 获取已经收藏的id列表
     */
    private void getMsgCollectedIds() {
        getChatApi().msgQueryAllCollectedIds(getIMChatType(), getIMChatId())
                .enqueue(new SimpleCallBack<List<Long>>() {
                    @Override
                    public void onSuccess(Call<ResEntity<List<Long>>> call, Response<ResEntity<List<Long>>> response) {
                        if (response.body().result != null) {
                            msgCollectedIdsList.clear();
                            msgCollectedIdsList.addAll(response.body().result);
                        }

                    }

                    @Override
                    public void defNotify(String noticeStr) {
                        // super.defNotify(noticeStr);
                    }
                });
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
            SFileImageInfoEntity sFileImageInfoEntity = null;
            if (sFileImageInfoEntities != null
                    && pos < sFileImageInfoEntities.size()) {
                sFileImageInfoEntity = sFileImageInfoEntities.get(pos);
            }
            ImageView imageView = (ImageView) v;
            final Drawable drawable = imageView.getDrawable();
            if (drawable == null) return false;
            final SFileImageInfoEntity finalSFileImageInfoEntity = sFileImageInfoEntity;
            if (finalSFileImageInfoEntity != null) {
                final boolean isCollected = isCollected(sFileImageInfoEntity.chatMsgId);
                final boolean isDinged = isDinged(sFileImageInfoEntity.chatMsgId);
                new BottomActionDialog(getContext(),
                        null,
                        Arrays.asList("分享", "转发", isCollected ? "取消收藏" : "收藏", isDinged ? "取消钉" : "钉", "保存到项目"),
                        new BottomActionDialog.OnActionItemClickListener() {
                            @Override
                            public void onItemClick(BottomActionDialog dialog, BottomActionDialog.ActionItemAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
                                dialog.dismiss();
                                switch (position) {
                                    case 0:
                                        shareImage2WeiXin(drawable);
                                        break;
                                    case 1:
                                        showContactShareDialogFragment(finalSFileImageInfoEntity.chatMsgId);
                                        break;
                                    case 2:
                                        if (isCollected) {
                                            msgActionCollectCancel(finalSFileImageInfoEntity.chatMsgId);
                                        } else {
                                            msgActionCollect(finalSFileImageInfoEntity.chatMsgId);
                                        }
                                        break;
                                    case 3:
                                        msgActionDing(!isDinged, finalSFileImageInfoEntity.chatMsgId);
                                        break;
                                    case 4:
                                        if (checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                                            String fileName = getFileName(adapter.getItem(position));
                                            savedImport2Project(drawable, fileName);
                                        } else {
                                            reqPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, "下载文件需要文件写入权限!", CODE_PERMISSION_FILE);
                                        }
                                        break;
                                }
                            }
                        }).show();
            } else {
                new BottomActionDialog(getContext(),
                        null,
                        Arrays.asList("分享", "保存到项目"),
                        new BottomActionDialog.OnActionItemClickListener() {
                            @Override
                            public void onItemClick(BottomActionDialog dialog, BottomActionDialog.ActionItemAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
                                dialog.dismiss();
                                switch (position) {
                                    case 0:
                                        shareImage2WeiXin(drawable);
                                        break;
                                    case 1:
                                        if (checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                                            String fileName = getFileName(adapter.getItem(position));
                                            savedImport2Project(drawable, fileName);
                                        } else {
                                            reqPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, "下载文件需要文件写入权限!", CODE_PERMISSION_FILE);
                                        }
                                        break;
                                }
                            }
                        }).show();
            }
            return true;
        }
        return false;
    }

    /**
     * 收藏消息
     *
     * @param msgId
     */

    protected final void msgActionCollect(final long msgId) {
        getChatApi().msgCollect(msgId, getIMChatType(), getIMChatId())
                .enqueue(new SimpleCallBack<Boolean>() {
                    @Override
                    public void onSuccess(Call<ResEntity<Boolean>> call, Response<ResEntity<Boolean>> response) {
                        if (response.body().result != null && response.body().result.booleanValue()) {
                            msgCollectedIdsList.add(msgId);
                            showTopSnackBar("收藏成功");
                        } else {
                            showTopSnackBar("收藏失败");
                        }
                    }
                });
    }

    /**
     * 收藏消息 取消
     *
     * @param msgId
     */
    protected final void msgActionCollectCancel(final long msgId) {
        getChatApi().msgCollectCancel(msgId, getIMChatType(), getIMChatId())
                .enqueue(new SimpleCallBack<Boolean>() {
                    @Override
                    public void onSuccess(Call<ResEntity<Boolean>> call, Response<ResEntity<Boolean>> response) {
                        if (response.body().result != null && response.body().result.booleanValue()) {
                            showTopSnackBar("取消收藏成功");
                            msgCollectedIdsList.remove(msgId);
                        } else {
                            showTopSnackBar("取消收藏失败");
                        }
                    }
                });
    }

    /**
     * 钉消息
     *
     * @param isDing    钉 true 取消钉 false
     * @param dingMsgId
     */
    protected final void msgActionDing(final boolean isDing, final long dingMsgId) {
        AlphaUserInfo alphaUserInfo = getLoginUserInfo();
        String uid = StringUtils.toLowerCase(alphaUserInfo != null ? alphaUserInfo.getUserId() : "");
        final IMMessageCustomBody msgPostEntity = IMMessageCustomBody.createDingMsg(getIMChatType(),
                alphaUserInfo != null ? alphaUserInfo.getName() : "",
                uid,
                getIMChatId(),
                isDing,
                dingMsgId);
        String jsonBody = null;
        try {
            jsonBody = JsonUtils.Gson2String(msgPostEntity);
        } catch (JsonParseException e) {
            e.printStackTrace();
        }
        showLoadingDialog(null);
        getChatApi().msgAdd(RequestUtils.createJsonBody(jsonBody))
                .enqueue(new SimpleCallBack<IMMessageCustomBody>() {
                    @Override
                    public void onSuccess(Call<ResEntity<IMMessageCustomBody>> call, Response<ResEntity<IMMessageCustomBody>> response) {
                        dismissLoadingDialog();
                        getMsgDingedIds();
                    }

                    @Override
                    public void onFailure(Call<ResEntity<IMMessageCustomBody>> call, Throwable t) {
                        super.onFailure(call, t);
                        dismissLoadingDialog();
                    }
                });
    }

    /**
     * 展示联系人转发对话框
     *
     * @param id msgid
     */
    public void showContactShareDialogFragment(long id) {
        String tag = ContactShareDialogFragment.class.getSimpleName();
        FragmentTransaction mFragTransaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);
        if (fragment != null) {
            mFragTransaction.remove(fragment);
        }
        ContactShareDialogFragment.newInstance(id)
                .show(mFragTransaction, tag);
    }

    private String getFileName(String url) {
        if (!TextUtils.isEmpty(url)) {
            int indexOf = url.lastIndexOf("/");
            if (indexOf >= 0 && indexOf < url.length() - 1) {
                return url.substring(indexOf, url.length());
            }
        }
        return SystemClock.elapsedRealtime() + ".png";
    }

    /**
     * 分享到项目
     *
     * @param drawable
     * @param name
     */
    private void savedImport2Project(final Drawable drawable, final String name) {
        if (drawable == null) return;
        showLoadingDialog(null);
        Observable
                .create(new ObservableOnSubscribe<String>() {
                    @Override
                    public void subscribe(ObservableEmitter<String> e) throws Exception {
                        if (e.isDisposed()) return;
                        boolean b = FileUtils.saveBitmap(getContext(), name, FileUtils.drawableToBitmap(drawable));
                        String changeSendfilePath = FileUtils.dirFilePath + File.separator + name + ".png";
                        if (b) {
                            e.onNext(changeSendfilePath);
                        } else {
                            e.onError(new NullPointerException());
                        }
                        e.onComplete();
                    }
                })
                .compose(this.<String>bindToLifecycle())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        dismissLoadingDialog();
                        ProjectSelectActivity.launch(getContext(), null, null, s);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        dismissLoadingDialog();
                    }
                });
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            finishAfterTransition();
        }
    }

    class ImagePagerAdapter extends BasePagerAdapter<String> {

        @Override
        public int bindView(int pos) {
            return R.layout.adapter_item_image_pager;
        }

        @Override
        public void bindDataToItem(final String s, ViewGroup container, View itemView, final int pos) {
            final TouchImageView touchImageView = (TouchImageView) itemView.findViewById(imageView);
            setTransitionView(touchImageView, s);
            touchImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
            touchImageView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (onPagerItemLongClickListener != null) {
                        return onPagerItemLongClickListener.OnItemLongClick(ImagePagerAdapter.this, v, pos);
                    }
                    return false;
                }
            });
            final TextView img_look_original_tv = (TextView) itemView.findViewById(R.id.img_look_original_tv);
            img_look_original_tv.setVisibility(View.GONE);
            final String bigUrl = getBigUrl(pos);
            img_look_original_tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //putItem(pos, bigUrl);
                    loadOriginalPic(img_look_original_tv, bigUrl, touchImageView);
                }
            });
            img_look_original_tv.setVisibility(View.GONE);
            if (GlideUtils.canLoadImage(getContext())) {
                log("---------->load url:pos:" + pos + "  url:" + s);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(getContext())
                                .load(s)
                                .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                                .error(R.mipmap.default_img_failed)
                                .thumbnail(0.1f)
                                .listener(new RequestListener<String, GlideDrawable>() {
                                    @Override
                                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                        img_look_original_tv.setVisibility(View.GONE);
                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                        //有sfile原图 原图的分界线是高度>=800
                                        if (!TextUtils.isEmpty(bigUrl)
                                                && resource != null
                                                && resource.getIntrinsicHeight() >= 800) {
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
                                            }).compose(ImagePagerActivity.this.<Boolean>bindToLifecycle())
                                                    .subscribeOn(Schedulers.newThread())
                                                    .observeOn(AndroidSchedulers.mainThread())
                                                    .subscribe(new io.reactivex.functions.Consumer<Boolean>() {
                                                        @Override
                                                        public void accept(Boolean aBoolean) throws Exception {
                                                            if (aBoolean != null && aBoolean.booleanValue()) {
                                                                log("--------->yes");
                                                                loadOriginalPic2(img_look_original_tv, bigUrl, touchImageView);
                                                            } else {
                                                                log("--------->no");
                                                                img_look_original_tv.setVisibility(View.VISIBLE);
                                                            }
                                                        }
                                                    });
                                        } else {
                                            img_look_original_tv.setVisibility(View.GONE);
                                        }
                                        return false;
                                    }
                                })
                                .into(touchImageView);
                    }
                }, new Random().nextInt(50));
            }
        }

        /**
         * 加载原图
         *
         * @param img_look_original_tv
         * @param originalUrl
         * @param imageView
         */
        private void loadOriginalPic(final View img_look_original_tv, String originalUrl, ImageView imageView) {
            if (imageView == null) return;
            if (TextUtils.isEmpty(originalUrl)) return;
            if (img_look_original_tv == null) return;
            log("---------->load Original url:" + originalUrl);
            if (!GlideUtils.canLoadImage(getContext()))
                return;
            //加载原图
            showLoadingDialog(null);
            Glide.with(getContext())
                    .load(originalUrl)
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .placeholder(imageView.getDrawable())
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            dismissLoadingDialog();
                            img_look_original_tv.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            dismissLoadingDialog();
                            img_look_original_tv.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .into(imageView);
        }

        /**
         * @param img_look_original_tv
         * @param originalUrl
         * @param imageView
         */
        private void loadOriginalPic2(final View img_look_original_tv, String originalUrl, ImageView imageView) {
            if (imageView == null) return;
            if (TextUtils.isEmpty(originalUrl)) return;
            if (img_look_original_tv == null) return;
            log("---------->load Original url2:" + originalUrl);
            if (!GlideUtils.canLoadImage(getContext()))
                return;
            //加载原图
            img_look_original_tv.setVisibility(View.GONE);
            Glide.with(getContext())
                    .load(originalUrl)
                    .placeholder(imageView.getDrawable())
                    .into(imageView);
        }


        /**
         * 获取大图地址
         *
         * @param pos
         * @return
         */
        private String getBigUrl(int pos) {
            if (sFileImageInfoEntities != null
                    && sFileImageInfoEntities.size() > pos) {
                SFileImageInfoEntity sFileImageInfoEntity = sFileImageInfoEntities.get(pos);
                if (sFileImageInfoEntity != null && sFileImageInfoEntity.size > 500_000) {
                    StringBuilder bigUrlBuilder = new StringBuilder(BuildConfig.API_CHAT_URL);
                    bigUrlBuilder.append("im/v1/msgs/files/download/refer");
                    bigUrlBuilder.append("?");
                    bigUrlBuilder.append("repo_id=");
                    bigUrlBuilder.append(sFileImageInfoEntity.repo_id);
                    bigUrlBuilder.append("&path=");
                    bigUrlBuilder.append(sFileImageInfoEntity.path);
                    bigUrlBuilder.append("&name=");
                    bigUrlBuilder.append(sFileImageInfoEntity.name);
                    bigUrlBuilder.append("&token=");
                    bigUrlBuilder.append(getUserToken());
                    return bigUrlBuilder.toString();
                }
            }
            return null;
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
