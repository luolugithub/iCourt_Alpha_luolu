package com.icourt.alpha.activity;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.CheckResult;
import android.support.annotation.IntDef;
import android.support.annotation.IntRange;
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
import com.icourt.alpha.constants.Const;
import com.icourt.alpha.constants.DownloadConfig;
import com.icourt.alpha.entity.bean.AlphaUserInfo;
import com.icourt.alpha.entity.bean.ChatFileInfoEntity;
import com.icourt.alpha.entity.bean.IMMessageCustomBody;
import com.icourt.alpha.entity.bean.ISeaFile;
import com.icourt.alpha.fragment.dialogfragment.ContactShareDialogFragment;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.utils.FileUtils;
import com.icourt.alpha.utils.GlideUtils;
import com.icourt.alpha.utils.JsonUtils;
import com.icourt.alpha.utils.StringUtils;
import com.icourt.alpha.view.HackyViewPager;
import com.icourt.alpha.widget.dialog.BottomActionDialog;
import com.icourt.api.RequestUtils;
import com.liulishuo.filedownloader.BaseDownloadTask;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Response;

import static com.icourt.alpha.constants.Const.CHAT_TYPE_P2P;
import static com.icourt.alpha.constants.Const.CHAT_TYPE_TEAM;


/**
 * @author xuanyouwu
 * @email xuanyouwu@163.com
 * @time 2016-08-26 10:46
 * <p>
 * 图片浏览器
 */

public class ImagePagerActivity extends ImageViewBaseActivity implements BasePagerAdapter.OnPagerItemClickListener, BasePagerAdapter.OnPagerItemLongClickListener {
    //收藏的消息列表
    protected final Set<Long> msgCollectedIdsList = new HashSet<>();
    //钉的消息列表
    protected final Set<Long> msgDingedIdsList = new HashSet<>();


    private static final String KEY_S_FILE_INFO = "key_s_file_info";
    private static final String KEY_CHAT_TYPE = "key_chat_type";
    private static final String KEY_CHAT_ID = "key_chat_id";
    private static final String KEY_POS = "key_pos";
    private static final String KEY_IMAGE_FROM = "key_image_from";
    @BindView(R.id.imagePager)
    HackyViewPager imagePager;
    @BindView(R.id.tvPagerTitle)
    TextView tvPagerTitle;
    ImagePagerAdapter pagerAdapter;
    ArrayList<ChatFileInfoEntity> sFileImageInfoEntities;
    Handler handler = new Handler();
    int realPos;
    int imageFrom;

    public static final int IMAGE_FROM_CHAT_WINDOW = 1;
    public static final int IMAGE_FROM_CHAT_FILE = 2;
    public static final int IMAGE_FROM_DING = 3;
    public static final int IMAGE_FROM_COLLECT = 4;

    @IntDef({
            IMAGE_FROM_CHAT_WINDOW,
            IMAGE_FROM_CHAT_FILE,
            IMAGE_FROM_DING,
            IMAGE_FROM_COLLECT,
    })
    public @interface ImageFrom {
    }

    /**
     * 显示图片
     *
     * @param context
     * @param sFileImageInfoEntities
     * @param selectedPos
     * @param transitionView
     */

    public static void launch(Context context,
                              @ImageFrom int imageFrom,
                              @Nullable ArrayList<ChatFileInfoEntity> sFileImageInfoEntities,
                              @IntRange(from = 0, to = Integer.MAX_VALUE) int selectedPos,
                              int chatType,
                              String chatId,
                              @Nullable View transitionView) {
        if (sFileImageInfoEntities == null || sFileImageInfoEntities.isEmpty()) return;
        if (selectedPos >= sFileImageInfoEntities.size()) {
            selectedPos = sFileImageInfoEntities.size() - 1;
        }
        Intent intent = new Intent(context, ImagePagerActivity.class);
        intent.putExtra(KEY_IMAGE_FROM, imageFrom);
        intent.putExtra(KEY_POS, selectedPos);
        intent.putExtra(KEY_S_FILE_INFO, sFileImageInfoEntities);
        intent.putExtra(KEY_CHAT_TYPE, chatType);
        intent.putExtra(KEY_CHAT_ID, chatId);
        if (context instanceof Activity
                && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
                && transitionView != null) {
            String transitionName = sFileImageInfoEntities.get(selectedPos).getChatMiddlePic();
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
        imageFrom = getIntent().getIntExtra(KEY_IMAGE_FROM, 0);
        sFileImageInfoEntities = (ArrayList<ChatFileInfoEntity>) getIntent().getSerializableExtra(KEY_S_FILE_INFO);
        realPos = getIntent().getIntExtra(KEY_POS, 0);
        pagerAdapter = new ImagePagerAdapter();
        pagerAdapter.bindData(true, sFileImageInfoEntities);
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
        callEnqueue(
                getChatApi().msgQueryAllDingIds(getIMChatType(), getIMChatId()),
                new SimpleCallBack<List<Long>>() {
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
        callEnqueue(
                getChatApi().msgQueryAllCollectedIds(getIMChatType(), getIMChatId()),
                new SimpleCallBack<List<Long>>() {
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


    @OnClick({R.id.download_img})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.download_img:
                //下载原图
                final String originalImageUrl = pagerAdapter.getOriginalImageUrl(imagePager.getCurrentItem());
                final String picSavePath = getPicSavePath(getSFileImageInfoEntity(imagePager.getCurrentItem()));
                downloadFile(originalImageUrl, picSavePath);
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
            final ChatFileInfoEntity chatFileInfoEntity = getSFileImageInfoEntity(pos);
            ImageView imageView = (ImageView) v;
            final Drawable drawable = imageView.getDrawable();
            if (drawable == null) return false;
            final ChatFileInfoEntity finalChatFileInfoEntity = chatFileInfoEntity;

            final String originalImageUrl = pagerAdapter.getOriginalImageUrl(pos);
            final String picSavePath = getPicSavePath(getSFileImageInfoEntity(pos));

            if (finalChatFileInfoEntity != null) {
                final boolean isCollected = isCollected(chatFileInfoEntity.getChatMsgId());
                final boolean isDinged = isDinged(chatFileInfoEntity.getChatMsgId());
                ArrayList<String> menus = new ArrayList<>();
                switch (imageFrom) {
                    case IMAGE_FROM_CHAT_WINDOW:
                        menus.add("转发给同事");
                        menus.add("分享");
                        menus.add(isCollected ? "取消收藏" : "收藏");
                        menus.add(isDinged ? "取消钉" : "钉");
                        menus.add("保存图片");
                        menus.add("保存到文档");
                        break;
                    default:
                        menus.add("转发给同事");
                        menus.add("分享");
                        menus.add("保存图片");
                        menus.add("保存到文档");
                        break;
                }
                new BottomActionDialog(getContext(),
                        null,
                        menus,
                        new BottomActionDialog.OnActionItemClickListener() {
                            @Override
                            public void onItemClick(BottomActionDialog dialog, BottomActionDialog.ActionItemAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
                                dialog.dismiss();
                                String action = adapter.getItem(position);
                                if (TextUtils.equals(action, "转发给同事")) {
                                    showContactShareDialogFragment(finalChatFileInfoEntity.getChatMsgId());
                                } else if (TextUtils.equals(action, "分享")) {
                                    shareHttpFile(originalImageUrl, picSavePath);
                                } else if (TextUtils.equals(action, "取消收藏")) {
                                    msgActionCollectCancel(finalChatFileInfoEntity.getChatMsgId());
                                } else if (TextUtils.equals(action, "收藏")) {
                                    msgActionCollect(finalChatFileInfoEntity.getChatMsgId());
                                } else if (TextUtils.equals(action, "取消钉")) {
                                    msgActionDing(!isDinged, finalChatFileInfoEntity.getChatMsgId());
                                } else if (TextUtils.equals(action, "钉")) {
                                    msgActionDing(!isDinged, finalChatFileInfoEntity.getChatMsgId());
                                } else if (TextUtils.equals(action, "保存图片")) {
                                    downloadFile(originalImageUrl, picSavePath);
                                } else if (TextUtils.equals(action, "保存到文档")) {
                                    shareHttpFile2repo(originalImageUrl, picSavePath);
                                }
                            }
                        }).show();
            }
            return true;
        }
        return false;
    }

    /**
     * 展示联系人转发对话框
     * 软拷贝
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
        ContactShareDialogFragment.newInstance(id, true)
                .show(mFragTransaction, tag);
    }


    /**
     * 收藏消息
     *
     * @param msgId
     */

    protected final void msgActionCollect(final long msgId) {
        callEnqueue(
                getChatApi().msgCollect(msgId, getIMChatType(), getIMChatId()),
                new SimpleCallBack<Boolean>() {
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
        callEnqueue(
                getChatApi().msgCollectCancel(msgId, getIMChatType(), getIMChatId()),
                new SimpleCallBack<Boolean>() {
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
        callEnqueue(
                getChatApi().msgAdd(RequestUtils.createJsonBody(jsonBody)),
                new SimpleCallBack<IMMessageCustomBody>() {
                    @Override
                    public void onSuccess(Call<ResEntity<IMMessageCustomBody>> call, Response<ResEntity<IMMessageCustomBody>> response) {
                        dismissLoadingDialog();
                        getMsgDingedIds();
                        if (isDing) {
                            showTopSnackBar("钉成功");
                        } else {
                            showTopSnackBar("取消钉成功");
                        }
                    }

                    @Override
                    public void onFailure(Call<ResEntity<IMMessageCustomBody>> call, Throwable t) {
                        super.onFailure(call, t);
                        dismissLoadingDialog();
                        if (isDing) {
                            showTopSnackBar("钉失败");
                        } else {
                            showTopSnackBar("取消钉失败");
                        }
                    }
                });
    }


    /**
     * 获取保存路径
     *
     * @param iSeaFile
     * @return
     */
    private String getPicSavePath(ISeaFile iSeaFile) {
        return DownloadConfig.getSeaFileDownloadPath(getLoginUserId(), iSeaFile);
    }

//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            finishAfterTransition();
//        }
//    }

    @CheckResult
    private ChatFileInfoEntity getSFileImageInfoEntity(int pos) {
        return sFileImageInfoEntities.get(pos);
    }

    class ImagePagerAdapter extends BasePagerAdapter<ChatFileInfoEntity> {

        @Override
        public int bindView(int pos) {
            return R.layout.adapter_item_image_pager;
        }

        /**
         * 获取原图地址
         *
         * @param iSeaFile
         * @return
         */
        public String getOriginalImageUrl(ISeaFile iSeaFile) {
            if (iSeaFile == null) return null;
            StringBuilder bigUrlBuilder = new StringBuilder(BuildConfig.API_CHAT_URL);
            bigUrlBuilder.append("im/v1/msgs/files/download/refer");
            bigUrlBuilder.append("?");
            bigUrlBuilder.append("repo_id=");
            bigUrlBuilder.append(iSeaFile.getSeaFileRepoId());
            bigUrlBuilder.append("&path=");
            bigUrlBuilder.append(FileUtils.getFileParentDir(iSeaFile.getSeaFileFullPath()));
            bigUrlBuilder.append("&name=");
            bigUrlBuilder.append(FileUtils.getFileName(iSeaFile.getSeaFileFullPath()));
            bigUrlBuilder.append("&token=");
            bigUrlBuilder.append(getUserToken());
            return bigUrlBuilder.toString();
        }

        /**
         * 获取原图地址
         *
         * @param pos
         * @return
         */
        public String getOriginalImageUrl(int pos) {
            ChatFileInfoEntity item = getItem(pos);
            return getOriginalImageUrl(item);
        }

        @Override
        public void bindDataToItem(final ChatFileInfoEntity chatFileInfoEntity, ViewGroup container, View itemView, final int pos) {
            final ImageView touchImageView = itemView.findViewById(R.id.imageView);
            setTransitionView(touchImageView, chatFileInfoEntity.getChatMiddlePic());
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
            img_look_original_tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadOriginalImage(chatFileInfoEntity, touchImageView, img_look_original_tv);
                }
            });

            final String thumbUrl = chatFileInfoEntity.getChatThumbPic();
            final String middleUrl = chatFileInfoEntity.getChatMiddlePic();
            final String picSavePath = getPicSavePath(chatFileInfoEntity);

            //已经有原图
            if (FileUtils.isFileExists(picSavePath)) {
                GlideUtils.loadSFilePicWithoutPlaceholder(getContext(), picSavePath, touchImageView);
                return;
            }

            if (GlideUtils.canLoadImage(getContext())) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (!GlideUtils.canLoadImage(getContext())) return;
                        //1.先加载缩略图
                        //2.加载中等图片
                        //3.加载原图
                        Glide.with(getContext())
                                .load(thumbUrl)
                                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                                .listener(new RequestListener<String, GlideDrawable>() {
                                    @Override
                                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                        loadMiddleImage(img_look_original_tv,
                                                middleUrl,
                                                touchImageView);
                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                        loadMiddleImage(img_look_original_tv,
                                                middleUrl,
                                                touchImageView);
                                        return false;
                                    }
                                })
                                .dontAnimate()
                                .into(touchImageView);
                    }
                }, new Random().nextInt(50));
            }
        }

        /**
         * 加载中图
         *
         * @param img_look_original_tv
         * @param middleUrl
         * @param imageView
         */
        private void loadMiddleImage(final View img_look_original_tv,
                                     final String middleUrl,
                                     final ImageView imageView) {
            if (!GlideUtils.canLoadImage(getContext())) return;
            Glide.with(getContext())
                    .load(middleUrl)
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .error(R.mipmap.filetype_image)
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            img_look_original_tv.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            //有sfile原图 原图的分界线是高度>=800
                            if (resource != null
                                    && resource.getIntrinsicHeight() >= 800) {
                                img_look_original_tv.setVisibility(View.VISIBLE);
                            } else {
                                img_look_original_tv.setVisibility(View.GONE);
                            }
                            return false;
                        }
                    })
                    .error(R.mipmap.filetype_image)
                    .dontAnimate()
                    .into(imageView);
        }

        /**
         * 加载缩略图
         *
         * @param imageView
         * @param pos
         */


        /**
         * 加载原图
         *
         * @param iSeaFile
         * @param imageView
         * @param imgLookOriginalTv
         */
        private void loadOriginalImage(final ISeaFile iSeaFile, final ImageView imageView, final View imgLookOriginalTv) {
            imgLookOriginalTv.setVisibility(View.GONE);
            final String picSavePath = getPicSavePath(iSeaFile);
            String originalImageUrl = pagerAdapter.getOriginalImageUrl(iSeaFile);
            downloadFile(originalImageUrl, picSavePath, new LoadingDownloadListener() {
                @Override
                protected void completed(BaseDownloadTask task) {
                    super.completed(task);
                    GlideUtils.loadSFilePicWithoutPlaceholder(getContext(), picSavePath, imageView);
                }
            });
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
