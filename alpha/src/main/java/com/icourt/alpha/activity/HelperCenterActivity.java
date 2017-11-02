package com.icourt.alpha.activity;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.text.TextUtils;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.base.BaseActivity;
import com.icourt.alpha.view.ProgressLayout;
import com.kaopiz.kprogresshud.KProgressHUD;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * @author lu.zhao  E-mail:zhaolu@icourt.cc
 * @version 2.2.1
 * @Description
 * @Company Beijing icourt
 * @date createTime：17/11/2
 */

public class HelperCenterActivity extends BaseActivity {

    private static final String KEY_TITLE = "key_title";
    private static final String KEY_URL = "key_url";

    public static void launch(@NonNull Context context, String url) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
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
        if (TextUtils.isEmpty(url)) {
            return;
        }
        Intent intent = new Intent(context, HelperCenterActivity.class);
        intent.putExtra(KEY_URL, url);
        intent.putExtra(KEY_TITLE, title);
        context.startActivity(intent);
    }

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
        }

        @Override
        public void onPageCommitVisible(WebView view, String url) {
            super.onPageCommitVisible(view, url);
        }


        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
            return super.shouldInterceptRequest(view, url);
        }


        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            return super.shouldOverrideUrlLoading(view, request);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            if (progressLayout != null) {
                progressLayout.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            if (progressLayout != null) {
                progressLayout.setVisibility(View.GONE);
            }
            if (TextUtils.equals(title, getString(R.string.mine_helper_center))) {
                hideHelperCenterBtn(webView);
            }
        }


        @SuppressWarnings("deprecation")
        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
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
            return super.onConsoleMessage(consoleMessage);
        }

        @Override
        public void onConsoleMessage(String message, int lineNumber, String sourceID) {
            super.onConsoleMessage(message, lineNumber, sourceID);
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
        }

        @Override
        public void onReceivedTouchIconUrl(WebView view, String url, boolean precomposed) {
            super.onReceivedTouchIconUrl(view, url, precomposed);
        }


        @Override
        public void onProgressChanged(WebView view, int newProgress) {

            super.onProgressChanged(view, newProgress);
            if (progressLayout != null) {
                progressLayout.setCurrentProgress(newProgress);
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_helper_center_layout);
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
            titleActionImage.setVisibility(TextUtils.isEmpty(title) ? View.VISIBLE : View.INVISIBLE);
        }
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

    @Override
    public void onDestroy() {
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
}
