package com.tokenbank.config;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Looper;

import com.tokenbank.TApplication;

import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class AppConfig {
    private static TApplication application;//
    private static WeakReference<Activity> currentActivity;
    private static Handler handler;
    private static ExecutorService es;
    private static ExecutorService singleThreadEs;
    private static ScheduledExecutorService scheduleEs;
    public static WifiManager mWifiManager;


    public static void setCurActivity(Activity activity) {
        currentActivity = new WeakReference<>(activity);
    }

    public static void init(TApplication application) {
        es = Executors.newCachedThreadPool();
        singleThreadEs = Executors.newSingleThreadExecutor();
        scheduleEs = Executors.newScheduledThreadPool(2);
        AppConfig.application = application;
        handler = new Handler(Looper.getMainLooper());
        mWifiManager = (WifiManager) application.getSystemService(Context.WIFI_SERVICE);
    }

    public static TApplication getContext() {
        return application;
    }

    public static void postOnUiThread(Runnable task) {
        handler.post(task);
    }

    public static void postDelayOnUiThread(Runnable task, long delayMillis) {
        handler.postDelayed(task, delayMillis);
    }


    public static void execute(Runnable task) {
        if (es != null && !es.isShutdown()) {
            es.execute(task);
        }
    }


    public interface ERR_CODE {
        int OK = 0;
        int NETWORK_ERR = -99;
    }

}
