package com.tokenbank.base;

import android.graphics.Bitmap;
import android.text.TextUtils;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.tokenbank.config.AppConfig;
import com.tokenbank.config.Constant;
import com.tokenbank.utils.GsonUtil;
import com.tokenbank.utils.TLog;
import com.tokenbank.view.TBWebView;

import java.util.HashMap;


public class JSUtil {

    private final static String TAG = "JSUtil";

    private TBWebView mWebView;
    private boolean isInit = false;
    private int mCallID = 0;
    private HashMap<Integer, WCallback> mCallbackHashMap = new HashMap<>();
    private static JSUtil instance = new JSUtil();

    private JSUtil() {

    }

    public static JSUtil getInstance() {
        return instance;
    }

    public void init() {
        mWebView = new TBWebView(AppConfig.getContext());
        mWebView.addJavascriptInterface(this, "client");
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                if (TextUtils.equals(Constant.base_web3_url, url)) {
                    isInit = false;
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (TextUtils.equals(Constant.base_web3_url, url)) {
                    isInit = true;
                }
            }
        });
        loadJs();
    }


    public void callJS(String optCallback, GsonUtil json, WCallback walletOptCallback) {
        if (optCallback == null || optCallback.length() <= 0 || json == null) {
            return;
        }

        if(!isInit) {
            return;
        }

        mCallID++;
        json.putInt("callid", mCallID);
        mCallbackHashMap.put(mCallID, walletOptCallback);

        String jsonParams = json.toString();

        try {
            mWebView.loadUrl("javascript:void(function(){"
                    + optCallback + "('" + jsonParams + "');"
                    + "}"+"())");
        } catch (Throwable e) {
            TLog.e(TAG, "操作失败");
        }
    }


    public boolean checkInit(WCallback callback) {
        if (!isInit) {
            if (callback != null) {
                GsonUtil reason = new GsonUtil("{}");
                reason.putString("reason", "page not init, make sure call loadJs and page loadData success");
                callback.onGetWResult(-1, reason);
            }
            return false;
        } else {
            return true;
        }
    }

    @JavascriptInterface
    public void notify(final String result) {
        AppConfig.postOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    GsonUtil json = new GsonUtil(result);
                    WCallback web3ResultCallback = mCallbackHashMap.get(json.getInt("callid", -1));
                    mCallbackHashMap.remove(json.getInt("callid", -1));
                    if (web3ResultCallback != null) {
                        web3ResultCallback.onGetWResult(json.getInt("ret", -1), json.getObject("extra", "{}"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }


    private void loadJs() {
        mWebView.loadUrl(Constant.base_web3_url);
    }
}
