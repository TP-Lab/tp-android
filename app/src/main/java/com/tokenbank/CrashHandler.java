package com.tokenbank;

import android.content.Context;
import android.os.Looper;

import com.tokenbank.config.AppConfig;

public class CrashHandler implements Thread.UncaughtExceptionHandler {
    private static final String TAG = "CrashHandler";

    // CrashHandler 实例
    private static CrashHandler INSTANCE = new CrashHandler();

    // 程序的 Context 对象
    private Context mContext;

    // 系统默认的 UncaughtException 处理类
    private Thread.UncaughtExceptionHandler mDefaultHandler;


    private CrashHandler() {
    }


    public static CrashHandler getInstance() {
        return INSTANCE;
    }


    public void init(Context context) {
        mContext = context;

        // 获取系统默认的 UncaughtException 处理器
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();

        // 设置该 CrashHandler 为程序的默认处理器
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    /**
     * 当 UncaughtException 发生时会转入该函数来处理
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        if (!handleException(ex) && mDefaultHandler != null) {
            // 如果用户没有处理则让系统默认的异常处理器来处理
            mDefaultHandler.uncaughtException(thread, ex);
        } else {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
            }
            //退出程序
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(0);

        }
    }


    private boolean handleException(Throwable ex) {
        if (ex == null) {
            return false;
        }

        // 异常的自定义处理
        // 日志书写之类的。。
        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                AppConfig.getContext().clearActivity();
                Looper.loop();
            }
        }.start();
        return true;
    }

}