package com.tokenbank.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.tokenbank.R;
import com.tokenbank.utils.ViewUtil;



public class TBWebCore extends FrameLayout {

    public interface WebCoreListener {
        void onReceivedTitle(String title);

        void onPageStarted(String url, Bitmap favicon);

        void onPageFinished(String url);

        void onReceiveError(String url);
    }

    private TBWebView mWebView;
    private ProgressBar mProgressbarWebView;
    private TextView mTvErrorView;

    private WebCoreListener mWebCoreListener;

    private String mCurrentUrl;

    public TBWebCore(@NonNull Context context) {
        super(context);
    }

    public TBWebCore(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TBWebCore(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        View view = ViewUtil.inflatView(getContext(), this, R.layout.layout_view_webbrowser, true);
        initView(view);
    }

    public void loadUrl(String url) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        mCurrentUrl = url;
        mWebView.loadUrl(url);
    }

    public void refresh() {
        mWebView.loadUrl(mWebView.getUrl());
    }

    public void setWebCoreListener(WebCoreListener coreListener){
        this.mWebCoreListener = coreListener;
    }

    private void initView(View view) {
        mProgressbarWebView = (ProgressBar) view.findViewById(R.id.pb_webview);
        mTvErrorView = (TextView) view.findViewById(R.id.web_errorview);
        mWebView = (TBWebView) view.findViewById(R.id.webview);
        mWebView.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                if(mWebCoreListener != null) {
                    mWebCoreListener.onReceivedTitle(title);
                }
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                mProgressbarWebView.setProgress(newProgress);
            }

        });
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                if(mTvErrorView.getVisibility() == View.VISIBLE) {
                    mTvErrorView.setVisibility(View.GONE);
                }
                if (TextUtils.equals(url, mCurrentUrl) && mProgressbarWebView.getVisibility() == View.GONE) {
                    mProgressbarWebView.setVisibility(View.VISIBLE);
                    mProgressbarWebView.setProgress(0);
                    if(mWebCoreListener != null) {
                        mWebCoreListener.onPageStarted(url, favicon);
                    }
                }

            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (TextUtils.equals(url, mCurrentUrl) && mProgressbarWebView.getVisibility() == View.VISIBLE) {
                    mProgressbarWebView.setVisibility(View.GONE);
                    mProgressbarWebView.setProgress(0);
                    if(mWebCoreListener != null) {
                        mWebCoreListener.onPageFinished(url);
                    }
                }
            }

            @Override
            @TargetApi(21)
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                if(TextUtils.equals(mCurrentUrl +"/", request.getUrl().toString())) {
                    mTvErrorView.setVisibility(View.VISIBLE);
                    if(mWebCoreListener != null) {
                        mWebCoreListener.onReceiveError(mCurrentUrl);
                    }
                }
            }
        });
    }
}
