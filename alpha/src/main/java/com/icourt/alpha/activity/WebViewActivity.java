package com.icourt.alpha.activity;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.base.BaseActivity;
import com.icourt.alpha.base.BaseApplication;
import com.icourt.alpha.constants.DownloadConfig;
import com.icourt.alpha.utils.FileUtils;
import com.icourt.alpha.utils.NetUtils;
import com.icourt.alpha.utils.StringUtils;
import com.icourt.alpha.view.ProgressLayout;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;
import com.liulishuo.filedownloader.exception.FileDownloadHttpException;
import com.liulishuo.filedownloader.exception.FileDownloadOutOfSpaceException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/5/12
 * version 1.0.0
 */
public class WebViewActivity extends BaseActivity implements DownloadListener {
    @BindView(R.id.bottom_back_iv)
    ImageButton bottomBackIv;
    @BindView(R.id.bottom_forward_iv)
    ImageButton bottomForwardIv;
    @BindView(R.id.bottom_refresh_iv)
    ImageButton bottomRefreshIv;
    @BindView(R.id.bottom_share_iv)
    ImageButton bottomShareIv;
    private static final String KEY_TITLE = "key_title";
    private static final String KEY_URL = "key_url";

    public static void launch(@NonNull Context context, String url) {
        if (context == null) return;
        if (TextUtils.isEmpty(url)) return;
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra(KEY_URL, url);
        context.startActivity(intent);
    }

    @BindView(R.id.titleBack)
    ImageView titleBack;
    @BindView(R.id.titleContent)
    TextView titleContent;
    @BindView(R.id.titleAction)
    ImageView titleAction;
    @BindView(R.id.titleView)
    AppBarLayout titleView;
    String title;

    public static void launch(@NonNull Context context, String title, String url) {
        if (context == null) return;
        if (TextUtils.isEmpty(url)) return;
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra(KEY_URL, url);
        intent.putExtra(KEY_TITLE, title);
        context.startActivity(intent);
    }

    private static final int REQUEST_FILE_PERMISSION = 9999;
    @BindView(R.id.webView)
    WebView webView;
    @BindView(R.id.progressLayout)
    ProgressLayout progressLayout;
    Unbinder unbinder;
    KProgressHUD hud;

    WebViewClient mWebViewClient = new WebViewClient() {
        @Override
        public void onLoadResource(WebView view, String url) {
            super.onLoadResource(view, url);
            log("------->onLoadResource:" + url);
        }

        @Override
        public void onPageCommitVisible(WebView view, String url) {
            super.onPageCommitVisible(view, url);
            log("------->onPageCommitVisible:" + url);
        }


        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
            log("------->shouldInterceptRequest:" + url);
            return super.shouldInterceptRequest(view, url);
        }


        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            return super.shouldOverrideUrlLoading(view, request);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            log("-------->onPageStarted url:" + url);
            if (progressLayout != null) {
                progressLayout.setVisibility(View.VISIBLE);
            }
            setBackForwardBtn();
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            log("-------->onPageFinished url:" + url);
            if (progressLayout != null) {
                progressLayout.setVisibility(View.GONE);
            }
            if (TextUtils.equals(title, getString(R.string.mine_helper_center))) {
                hideHelperCenterBtn(webView);
            }
            setBackForwardBtn();
        }


        @SuppressWarnings("deprecation")
        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            log("------------->onReceivedError:errorCode:" + errorCode + ";description:" + description + ";failingUrl:" + failingUrl);
        }

        @TargetApi(Build.VERSION_CODES.M)
        @Override
        public void onReceivedError(WebView view, WebResourceRequest req, WebResourceError rerr) {
            // Redirect to deprecated method, so you can use it in all SDK versions
            onReceivedError(view, rerr.getErrorCode(), rerr.getDescription().toString(), req.getUrl().toString());
        }
    };

    WebChromeClient mWebChromeClient = new WebChromeClient() {
        @Override
        public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
            log("---------->onConsoleMessage:consoleMessage:" + consoleMessage);
            return super.onConsoleMessage(consoleMessage);
        }

        @Override
        public void onConsoleMessage(String message, int lineNumber, String sourceID) {
            log("---------->onConsoleMessage:message:" + message + ";lineNumber:" + lineNumber + ";sourceID:" + sourceID);
            super.onConsoleMessage(message, lineNumber, sourceID);
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            setTitle(title);
        }

        @Override
        public void onReceivedTouchIconUrl(WebView view, String url, boolean precomposed) {
            super.onReceivedTouchIconUrl(view, url, precomposed);
            log("--------->onReceivedTouchIconUrl:" + url);
        }


        @Override
        public void onProgressChanged(WebView view, int newProgress) {

            super.onProgressChanged(view, newProgress);
            if (progressLayout != null) {
                progressLayout.setCurrentProgress(newProgress);
            }
        }


    };
    private FileDownloadListener fileDownloadListener = new FileDownloadListener() {

        @Override
        protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
            log("----------->下载开始:" + soFarBytes + "  totalBytes:" + totalBytes);
            if (hud != null) {
                hud.show();
            }
        }

        @Override
        protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
            log("----------->下载进度:" + soFarBytes + ";totalBytes:" + totalBytes);
            if (hud != null) {
                if (totalBytes > 0 && totalBytes > soFarBytes) {
                    hud.setProgress((int) ((soFarBytes * 1.0f / totalBytes * 1.0f) * 100));
                }
            }
        }

        @Override
        protected void completed(BaseDownloadTask task) {
            if (hud != null) {
                hud.dismiss();
            }
            showTopSnackBar(String.format("已下载至目录%s", task.getTargetFilePath()));
        }

        @Override
        protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
        }

        @Override
        protected void error(BaseDownloadTask task, Throwable e) {
            if (hud != null) {
                hud.dismiss();
            }
            if (NetUtils.hasNetwork(BaseApplication.getApplication())) {
                bugSync("web 文件下载失败", e);
            }
            if (e instanceof FileDownloadHttpException) {
                int code = ((FileDownloadHttpException) e).getCode();
                showTopSnackBar(String.format("%s:%s", code, "下载异常!"));
            } else if (e instanceof FileDownloadOutOfSpaceException) {
                new AlertDialog.Builder(getActivity())
                        .setTitle("提示")
                        .setMessage("存储空间严重不足,去清理?")
                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
            } else {
                showTopSnackBar(String.format("下载异常!" + StringUtils.throwable2string(e)));
            }
        }

        @Override
        protected void warn(BaseDownloadTask task) {
            log("--------->warn");
        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (webView != null) {
            webView.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (webView != null) {
            webView.onPause();
        }
    }

    @Override
    public void onDestroy() {
        pauseDownload();
        try {
            if (webView != null) {
                webView.clearHistory();
                webView.clearFormData();
                webView.clearMatches();
                webView.removeAllViews();
                try {
                    webView.destroy();
                } catch (Throwable t) {
                }
                webView = null;
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }


    @Override
    protected void initView() {
        super.initView();

        hud = KProgressHUD.create(getContext())
                .setStyle(KProgressHUD.Style.PIE_DETERMINATE)
                .setMaxProgress(100)
                .setLabel("下载中...");

        title = getIntent().getStringExtra(KEY_TITLE);
        setTitle(TextUtils.isEmpty(title) ? "Alpha" : title);
        ImageView titleActionImage = getTitleActionImage();
        if (titleActionImage != null) {
            titleActionImage.setImageResource(R.mipmap.browser_open);
        }
        titleActionImage.setVisibility(TextUtils.isEmpty(title) ? View.VISIBLE : View.INVISIBLE);
        WebSettings webSettings = webView.getSettings();
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSettings.setAppCacheEnabled(false);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setDatabaseEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webView.setWebViewClient(mWebViewClient);
        webView.setWebChromeClient(mWebChromeClient);
        webView.setDownloadListener(this);
        progressLayout.setMaxProgress(100);
        webView.loadUrl(getIntent().getStringExtra(KEY_URL));
    }

    /**
     * 隐藏帮助中心中的按钮（'提交问题'、'查看问题'、'退出'）
     *
     * @param view
     */
    private void hideHelperCenterBtn(final WebView view) {
        //编写 javaScript方法
        final String javascript = "javascript:function hideOther() {" +
//                        "document.getElementsByClassName(\'flex-box\')[0].style.backgroundColor = \'red\'; " +//头部颜色
                "document.getElementsByClassName(\'nav-list bottom\')[0].style.visibility = \'hidden\'; " +//退出
                "document.getElementsByClassName(\'nav-list top\')[0].children[1].style.visibility = \'hidden\'; " +//提交问题
                "document.getElementsByClassName(\'nav-list top\')[0].children[2].style.visibility = \'hidden\'; " +//查看问题
                "document.getElementsByClassName(\'slideout-menu\')[0].children[0].style.visibility = \'hidden\'; }";//头像
        //创建方法
        view.loadUrl("javascript:" + javascript);
        view.loadUrl("javascript:hideOther();");
    }

    private void pauseDownload() {
        if (fileDownloadListener != null) {
            try {
                FileDownloader
                        .getImpl()
                        .pause(fileDownloadListener);
            } catch (Exception e) {
            }
        }
    }


    @OnClick({R.id.bottom_back_iv,
            R.id.bottom_forward_iv,
            R.id.bottom_refresh_iv,
            R.id.bottom_share_iv})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bottom_back_iv:
                if (webView.canGoBack()) {
                    webView.goBack();
                }
                break;
            case R.id.bottom_forward_iv:
                if (webView.canGoForward()) {
                    webView.goForward();
                }
                break;
            case R.id.bottom_refresh_iv:
                webView.reload();
                break;
            case R.id.bottom_share_iv:
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_TEXT, webView.getUrl());
                shareIntent.setType("text/plain");
                startActivity(Intent.createChooser(shareIntent, "分享到"));    //设置分享列表的标题，并且每次都显示分享列表
                break;
            case R.id.titleAction:
                openWithOtherApp();
                break;
            default:
                super.onClick(v);
                break;
        }
    }

    private void setBackForwardBtn() {
        try {
            if (bottomBackIv != null) {
                int unableColor = 0XFFF3F3F3;
                if (webView.canGoBack()) {
                    bottomBackIv.setColorFilter(0);
                } else {
                    bottomBackIv.setColorFilter(unableColor);
                }

                if (webView.canGoForward()) {
                    bottomForwardIv.setColorFilter(0);
                } else {
                    bottomForwardIv.setColorFilter(unableColor);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 用其它app打开网页
     */
    private void openWithOtherApp() {
        String url = getIntent().getStringExtra(KEY_URL);
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        Uri content_url = Uri.parse(url);
        intent.setData(content_url);
        startActivity(intent);
    }


    /**
     * 文件下载
     *
     * @param url
     * @param userAgent
     * @param contentDisposition
     * @param mimetype
     * @param contentLength
     */
    @Override
    public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
        //跳转到浏览器下载
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);

        //暂时不用自己做下载

        /*if (TextUtils.isEmpty(url)) {
            showTopSnackBar("下载地址为空!");
            return;
        }
        if (checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            startDownload(url);
        } else {
            reqPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, "我们需要文件写入权限!", REQUEST_FILE_PERMISSION);
        }*/
    }

    /**
     * 执行下载
     *
     * @param url
     */
    private void startDownload(String url) {
        log("----------->startDownload:" + url);
        if (Environment.isExternalStorageEmulated()) {
            FileDownloader
                    .getImpl()
                    .create(url)
                    .setPath(DownloadConfig.getCommFileDownloadPath(getLoginUserId(), FileUtils.getFileName(url)))
                    .setListener(fileDownloadListener)
                    .start();
        } else {
            showTopSnackBar(R.string.str_sd_unavailable);
        }
    }

    @CallSuper
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_FILE_PERMISSION:
                if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    showTopSnackBar("文件写入权限被拒绝！");
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
        }
    }
}
