package com.tokenbank.utils;

import android.text.TextUtils;
import android.util.Log;

import com.tokenbank.BuildConfig;

public class TLog {

    public static void d(String TAG, String log) {
        if(TextUtils.isEmpty(log)) {
            return;
        }
        if(BuildConfig.DEBUG) {
            Log.d(TAG, log);
        }
    }

    public static void e(String TAG, String log) {
        if(TextUtils.isEmpty(log)) {
            return;
        }
        if(BuildConfig.DEBUG) {
            Log.e(TAG, log);
        }
    }
}
