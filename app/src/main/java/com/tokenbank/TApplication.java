package com.tokenbank;

import android.app.Application;
import android.content.Context;
import android.os.Build;

import com.android.jccdex.app.eos.EosWallet;
import com.android.jccdex.app.ethereum.EthereumWallet;
import com.android.jccdex.app.jingtum.JingtumWallet;
import com.android.jccdex.app.moac.MoacWallet;
import com.tokenbank.activity.BaseActivity;
import com.tokenbank.base.BlockChainData;
import com.tokenbank.base.WalletInfoManager;
import com.tokenbank.base.TBController;
import com.tokenbank.config.AppConfig;
import com.tokenbank.utils.LanguageUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class TApplication extends Application {

    private final static String TAG = "TApplication";
    private List<BaseActivity> mActivities = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();
        AppConfig.init(this);
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(getApplicationContext());
        BlockChainData.getInstance().init();
        TBController.getInstance().init();
        WalletInfoManager.getInstance().init();
        JingtumWallet.getInstance().init(this);
        EthereumWallet.getInstance().init(this);
        EthereumWallet.getInstance().initWeb3Provider("https://eth626892d.jccdex.cn");
        MoacWallet.getInstance().init(this);
        MoacWallet.getInstance().initChain3Provider("https://moac1ma17f1.jccdex.cn");
        EosWallet.getInstance().init(this);
        EosWallet.getInstance().initEosProvider("aca376f206b8fc25a6ed44dbdc66547c36c6c33e3a119ffbeaef943642f0e906", "http://openapi.eos.ren");
    }

    public void addActivity(BaseActivity activity) {
        mActivities.add(activity);
        AppConfig.setCurActivity(activity);
    }

    public void popActivity(BaseActivity activity) {
        mActivities.remove(activity);
        if (!activity.isFinishing()) {
            activity.finish();
        }
    }

    public void clearActivity() {
        for (BaseActivity activity : mActivities
        ) {
            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Locale locale = LanguageUtil.getUserLocale(base);
            super.attachBaseContext(LanguageUtil.updateLocale(base, locale));
        } else {
            super.attachBaseContext(base);
        }
    }
}
