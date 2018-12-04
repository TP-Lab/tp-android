package com.tokenbank.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;

import com.tokenbank.R;
import com.tokenbank.utils.TLog;
import com.tokenbank.view.TBWebCore;
import com.tokenbank.view.TitleBar;



public class WebBrowserActivity extends BaseActivity implements TitleBar.TitleBarClickListener {

    private final static String TAG = "WebBrowserActivity";

    private String mUrl = "";
    private String mTitle = "";

    private TBWebCore mWebCore;
    private TitleBar mTitleBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webbrowser);

        if (getIntent() != null) {
            mUrl = getIntent().getStringExtra("URL");
            mTitle = getIntent().getStringExtra("TITLE");
            TLog.d(TAG, "mUrl:" + mUrl);
        }
        mWebCore =  findViewById(R.id.web_core);
        mTitleBar =  findViewById(R.id.title_bar);
        mTitleBar.setTitle(mTitle);
        mTitleBar.setTitleTextColor(R.color.white);
        mTitleBar.setLeftDrawable(R.drawable.ic_close);
        mTitleBar.setRightDrawable(R.drawable.ic_refresh);
        mTitleBar.setTitleBarClickListener(this);

        if (!TextUtils.isEmpty(mUrl)) {
            mWebCore.loadUrl(mUrl);
        }
    }

    @Override
    public void onLeftClick(View view) {
        this.finish();
    }

    @Override
    public void onRightClick(View view) {
        mWebCore.refresh();
    }

    @Override
    public void onMiddleClick(View view) {

    }

    public static void startWebBrowserActivity(Context from, String title, String url) {
        Intent intent = new Intent(from, WebBrowserActivity.class);
        intent.putExtra("URL", url);
        intent.putExtra("TITLE", title);
        from.startActivity(intent);
    }

    public static void startWebBrowserActivity(Context from, String url) {
        startWebBrowserActivity(from, "", url);
    }
}
