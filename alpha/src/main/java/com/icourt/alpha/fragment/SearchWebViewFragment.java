package com.icourt.alpha.fragment;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.icourt.alpha.R;
import com.icourt.alpha.base.BaseFragment;
import com.icourt.alpha.interfaces.IWebViewPage;
import com.icourt.alpha.interfaces.OnWebViewFragmentListener;
import com.icourt.alpha.view.ProgressLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/20
 * version 1.0.0
 */
public class SearchWebViewFragment extends BaseFragment implements IWebViewPage {

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
            if (onWebViewFragmentListener != null) {
                onWebViewFragmentListener.onWebViewStarted(SearchWebViewFragment.this, 0, null);
            }
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            log("-------->onPageFinished url:" + url);
            if (progressLayout != null) {
                progressLayout.setVisibility(View.GONE);
            }
            if (onWebViewFragmentListener != null) {
                onWebViewFragmentListener.onWebViewFinished(SearchWebViewFragment.this, 0, null);
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

    OnWebViewFragmentListener onWebViewFragmentListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (getParentFragment() instanceof OnWebViewFragmentListener) {
            onWebViewFragmentListener = (OnWebViewFragmentListener) getParentFragment();
        } else if (context instanceof OnWebViewFragmentListener) {
            onWebViewFragmentListener = (OnWebViewFragmentListener) context;
        }
    }

    /**
     * @param url
     * @return
     */
    public static SearchWebViewFragment newInstance(String url) {
        SearchWebViewFragment searchWebViewFragment = new SearchWebViewFragment();
        Bundle args = new Bundle();
        args.putString("url", url);
        searchWebViewFragment.setArguments(args);
        return searchWebViewFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(R.layout.fragment_search_webview, inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void initView() {
        WebSettings webSettings = webView.getSettings();
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSettings.setAppCacheEnabled(false);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setDatabaseEnabled(true);
        webSettings.setDomStorageEnabled(true);

        webSettings.setSupportZoom(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webSettings.setLoadWithOverviewMode(true);

        webView.setWebViewClient(mWebViewClient);
        webView.setWebChromeClient(mWebChromeClient);
        progressLayout.setMaxProgress(100);
        webView.loadUrl(getArguments().getString("url", ""));
    }

    @Override
    public void notifyFragmentUpdate(Fragment targetFrgament, int type, Bundle bundle) {
        super.notifyFragmentUpdate(targetFrgament, type, bundle);
        if (targetFrgament == SearchWebViewFragment.this) {
            if (webView != null) {
                switch (type) {
                    case 1://后退
                        if (webView.canGoBack()) {
                            if (onWebViewFragmentListener != null) {
                                onWebViewFragmentListener.onWebViewGoBack(SearchWebViewFragment.this, 0, null);
                            }
                            webView.goBack();
                        }
                        break;
                    case 2://前进
                        if (webView.canGoForward()) {
                            if (onWebViewFragmentListener != null) {
                                onWebViewFragmentListener.onWebViewGoForward(SearchWebViewFragment.this, 0, null);
                            }
                            webView.goForward();
                        }
                        break;
                    case 3://刷新
                        webView.reload();
                        break;
                    case 4://分享
                        Intent shareIntent = new Intent();
                        shareIntent.setAction(Intent.ACTION_SEND);
                        shareIntent.putExtra(Intent.EXTRA_TEXT, webView.getUrl());
                        shareIntent.setType("text/plain");
                        startActivity(Intent.createChooser(shareIntent, "分享到"));    //设置分享列表的标题，并且每次都显示分享列表
                        break;
                }
            }
        }
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
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
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

    @Override
    public WebView getPageWebView() {
        return webView;
    }
}
