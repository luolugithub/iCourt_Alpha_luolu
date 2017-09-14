package com.icourt.alpha.activity;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/5/12
 * version 1.0.0
 */
public class WebViewActivity extends BaseActivity {
    private static final String KEY_TITLE = "key_title";
    private static final String KEY_URL = "key_url";
    @BindView(R.id.titleBack)
    ImageView titleBack;
    @BindView(R.id.titleContent)
    TextView titleContent;
    @BindView(R.id.titleAction)
    ImageView titleAction;
    @BindView(R.id.titleView)
    AppBarLayout titleView;

    public static void launch(@NonNull Context context, String url) {
        if (context == null) return;
        if (TextUtils.isEmpty(url)) return;
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra(KEY_URL, url);
        context.startActivity(intent);
    }

    public static void launch(@NonNull Context context, String title, String url) {
        if (context == null) return;
        if (TextUtils.isEmpty(url)) return;
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra(KEY_URL, url);
        intent.putExtra(KEY_TITLE, title);
        context.startActivity(intent);
    }

    @BindView(R.id.webView)
    WebView webView;
    @BindView(R.id.progressLayout)
    ProgressLayout progressLayout;
    Unbinder unbinder;

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
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            log("-------->onPageFinished url:" + url);
            if (progressLayout != null) {
                progressLayout.setVisibility(View.GONE);
            }
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void initView() {
        super.initView();
        String title = getIntent().getStringExtra(KEY_TITLE);
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
        webSettings.setAllowFileAccess(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setDatabaseEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webView.setWebViewClient(mWebViewClient);
        webView.setWebChromeClient(mWebChromeClient);
        progressLayout.setMaxProgress(100);
        webView.loadUrl(getIntent().getStringExtra(KEY_URL));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.titleAction:
                openWithOtherApp();
                break;
            default:
                super.onClick(v);
                break;
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
        super.onDestroy();
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
    }
}
