package com.tokenbank.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;

import com.tokenbank.config.AppConfig;
import com.tokenbank.config.Constant;

import java.io.File;
import java.util.Locale;


public final class DeviceUtil {

    //Memeroy
    public static long getAvailableExternalMemorySize() {
        File path = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return availableBlocks * blockSize;
    }


    public static String generateDeviceUniqueId() {
        try {
            String deviceUniqueId = FileUtil.getStringFromSp(AppConfig.getContext(), Constant.sys_prefs, Constant.init_keys);
            if (TextUtils.isEmpty(deviceUniqueId)) {
                String id = "123456";
                String deviceId = FileUtil.getStringContent(id);
                FileUtil.putStringToSp(AppConfig.getContext(), Constant.sys_prefs, Constant.init_keys, deviceId);
                return deviceId;
            } else {
                return deviceUniqueId;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getDeviceUniqueId() {
        String deviceUniqueId = FileUtil.getStringFromSp(AppConfig.getContext(), Constant.sys_prefs, Constant.init_keys);
        return deviceUniqueId;
    }


    public static String getVersionName(Context context) {
        String version = null;
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            version = info.versionName;
        } catch (Exception e) {
            TLog.d("AndroidConfig", "getVersionName error " + e.getMessage());
        }
        return version;
    }

    public static String getVersionName() {
        return getVersionName(AppConfig.getContext());
    }


    public static String getLanguage() {
        return Locale.getDefault().getLanguage();
    }

}
