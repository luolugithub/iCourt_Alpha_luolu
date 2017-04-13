package com.icourt.alpha.base;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.CallSuper;
import android.support.annotation.CheckResult;

import com.icourt.alpha.R;
import com.icourt.alpha.utils.SpUtils;
import com.icourt.alpha.utils.StringUtils;
import com.icourt.alpha.utils.SystemUtils;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;

import java.io.File;
import java.util.Map;

/**
 * Description  uemeng sdk 登陆 分享
 * 防止微信等杀死http://dev.umeng.com/social/android/share-detail#7_1
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/5
 * version 1.0.0
 */
public class BaseUmengActivity extends BaseActivity implements UMAuthListener {
    private UMShareAPI mShareAPI;

    @CheckResult
    protected UMShareAPI getmShareAPI() {
        return mShareAPI;
    }

    @CallSuper
    @Override
    public void onStart(SHARE_MEDIA share_media) {
        showLoadingDialog(R.string.umeng_auth);
    }

    @CallSuper
    @Override
    public void onComplete(SHARE_MEDIA share_media, int i, Map<String, String> map) {
        dismissLoadingDialog();
        log("---------->onComplete share_media:" + share_media + ";i:" + i + ";map:" + map);
    }

    @CallSuper
    @Override
    public void onError(SHARE_MEDIA share_media, int i, Throwable throwable) {
        showTopSnackBar(throwable != null ? throwable.getLocalizedMessage() : "");
        dismissLoadingDialog();
        feedBackErrorLog(share_media, i, throwable);
    }

    @CallSuper
    @Override
    public void onCancel(SHARE_MEDIA share_media, int i) {
        dismissLoadingDialog();
        log("---------->onCancel share_media:" + share_media + ";i:" + i);
    }

    /**
     * 记录授权错误日志
     *
     * @param share_media
     * @param i
     * @param throwable
     */
    private void feedBackErrorLog(SHARE_MEDIA share_media, int i, Throwable throwable) {
        StringBuilder errorLogBuilder = new StringBuilder("授权错误:");
        errorLogBuilder.append("\nplat:" + share_media.toString());
        errorLogBuilder.append("\naction:" + i);
        errorLogBuilder.append("\nthrowable:" + StringUtils.throwable2string(throwable));
        //Bugtags.sendFeedback(errorLogBuilder.toString());
    }

    @CallSuper
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mShareAPI = UMShareAPI.get(this);
        // qq微信新浪授权防杀死 http://dev.umeng.com/social/android/share-detail#7_1
        //mShareAPI.fetchAuthResultWithBundle(getActivity(), savedInstanceState, this);
    }

    @CallSuper
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI umShareAPI = getmShareAPI();
        if (umShareAPI != null) {
            umShareAPI.onActivityResult(requestCode, resultCode, data);
        }
    }

    @CallSuper
    @Override
    protected void onDestroy() {
        super.onDestroy();
        UMShareAPI umShareAPI = getmShareAPI();
        if (umShareAPI != null) {
            umShareAPI.release();
        }
    }

    /**
     * 进行授权并获取用户信息 回调onComplete(SHARE_MEDIA share_media, int i, Map<String, String> map)
     * 可校验对应第三方平台是否安装 未安装对应的app 回调onError(SHARE_MEDIA share_media, int i, Throwable throwable)
     *
     * @param shareMedia
     */
    protected final void doOauth(SHARE_MEDIA shareMedia) {
        SystemUtils.hideSoftKeyBoard(getContext(), true);
        UMShareAPI umShareAPI = getmShareAPI();
        if (umShareAPI != null) {
            umShareAPI.getPlatformInfo(getContext(), shareMedia, this);
        }
    }

    /**
     * 删除授权
     *
     * @param shareMedia
     */
    protected void deleteOauth(SHARE_MEDIA shareMedia) {
        UMShareAPI umShareAPI = getmShareAPI();
        if (umShareAPI != null && umShareAPI.isAuthorize(getContext(), shareMedia)) {
            umShareAPI.deleteOauth(getContext(), shareMedia, null);
            switch (shareMedia) {
                case WEIXIN:
                    SpUtils.getInstance().putData("LOGIN_WX_USER_INFO", "");
                    break;
            }
        }
    }

    /**
     * 是否安装第三方客户端
     * 否则将会回调  onError(SHARE_MEDIA share_media, int i, Throwable throwable)
     *
     * @param shareMedia
     * @return
     */
    protected final boolean isInstall(SHARE_MEDIA shareMedia) {
        UMShareAPI umShareAPI = getmShareAPI();
        if (umShareAPI != null) {
            return umShareAPI.isInstall(getContext(), shareMedia);
        }
        return false;
    }


    protected void shareDemo() {
        try {
            String ROOTPATH = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;
            String apkPath = ROOTPATH + "test.txt";
            File file = new File(apkPath);
            shareFile2WeiXin(file);
        } catch (Exception e) {
            showTopSnackBar("error:" + e);
        }
    }

    /**
     * 注意文件大小不超过10MB
     *
     * @param file
     */
    protected void shareFile2WeiXin(File file) {
        if (file != null && file.exists()) {
            //  if(file.length()>10mb)
            UMImage umImage = new UMImage(this, "http://i.ce.cn/fashion/news/201704/11/W020170411297208606486.jpg");
            new ShareAction(getActivity())
                    .setPlatform(SHARE_MEDIA.WEIXIN)
                    .withMedia(umImage)
                 /*   .withSubject(file.getName())//文件名
                    .withFile(file)*/
                    .setCallback(new UMShareListener() {
                        @Override
                        public void onStart(SHARE_MEDIA share_media) {
                            log("------sta");
                        }

                        @Override
                        public void onResult(SHARE_MEDIA share_media) {
                            showTopSnackBar("分享成功");
                            log("------res");
                        }

                        @Override
                        public void onError(SHARE_MEDIA share_media, Throwable throwable) {
                            log("------erro:+" + throwable);
                            showTopSnackBar("分享失败:" + StringUtils.throwable2string(throwable));
                            //Bugtags.sendFeedback("分享失败:"+StringUtils.throwable2string(throwable));
                        }

                        @Override
                        public void onCancel(SHARE_MEDIA share_media) {
                        }
                    }).share();
        }
    }

}
