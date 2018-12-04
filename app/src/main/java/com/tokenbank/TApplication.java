package com.tokenbank;

import android.app.Application;

import com.tokenbank.activity.BaseActivity;
import com.tokenbank.base.BlockChainData;
import com.tokenbank.base.JSUtil;
import com.tokenbank.base.WalletInfoManager;
import com.tokenbank.base.TBController;
import com.tokenbank.config.AppConfig;

import java.util.ArrayList;
import java.util.List;


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
        JSUtil.getInstance().init();
    }

    public void addActivity(BaseActivity activity) {
        mActivities.add(activity);
        AppConfig.setCurActivity(activity);
    }

    public void popActivity(BaseActivity activity) {
        mActivities.remove(activity);
        if(!activity.isFinishing()) {
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
}
