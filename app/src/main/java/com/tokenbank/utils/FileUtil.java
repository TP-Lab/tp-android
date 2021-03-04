package com.tokenbank.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.tokenbank.config.Constant;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.util.UUID;


public class FileUtil {
    private static MessageDigest md5 = null;

    public static void putStringToSp(Context context, String spName, String key, String value) {
        SharedPreferences sp = context.getSharedPreferences(spName, Context.MODE_PRIVATE);
        sp.edit().putString(key, value).commit();
    }

    public static String getStringFromSp(Context context, String spName, String key) {
        SharedPreferences sp = context.getSharedPreferences(spName, Context.MODE_PRIVATE);
        return sp.getString(key, "");
    }

    public static void putIntToSp(Context context, String spName, String key, int value) {
        SharedPreferences sp = context.getSharedPreferences(spName, Context.MODE_PRIVATE);
        sp.edit().putInt(key, value).commit();
    }

    public static int getIntFromSp(Context context, String spName, String key) {
        SharedPreferences sp = context.getSharedPreferences(spName, Context.MODE_PRIVATE);
        return sp.getInt(key, 0);
    }

    public static void putLongToSp(Context context, String spName, String key, long value) {
        SharedPreferences sp = context.getSharedPreferences(spName, Context.MODE_PRIVATE);
        sp.edit().putLong(key, value).commit();
    }

    public static long getLongFromSp(Context context, String spName, String key) {
        SharedPreferences sp = context.getSharedPreferences(spName, Context.MODE_PRIVATE);
        return sp.getLong(key, 0l);
    }

    public static void putBooleanToSp(Context context, String spName, String key, boolean value) {
        SharedPreferences sp = context.getSharedPreferences(spName, Context.MODE_PRIVATE);
        sp.edit().putBoolean(key, value).commit();
    }

    public static boolean getBooleanFromSp(Context context, String spName, String key) {
        SharedPreferences sp = context.getSharedPreferences(spName, Context.MODE_PRIVATE);
        return sp.getBoolean(key, false);
    }

    public static boolean getBooleanFromSp(Context context, String spName, String key, boolean defValue) {
        SharedPreferences sp = context.getSharedPreferences(spName, Context.MODE_PRIVATE);
        return sp.getBoolean(key, defValue);
    }

    public static String getStringContent(String originTxt) {
        return originTxt;
    }

    public static void deleteFile(String path) {
        File file = new File(path);
        if (file != null && file.exists()) {
            if (file.isFile()) {
                file.delete();
            } else if (file.isDirectory()) {
                File[] files = file.listFiles();
                if (files != null && files.length > 0) {
                    for (File f : files) {
                        deleteFile(f.getAbsolutePath());
                    }
                }
                file.delete();
            }
        }
    }

    public static String getSharedPrefDir(Context context) {
        return context.getFilesDir().getParent() + "/shared_prefs/";
    }

    public static String getSaveImgDir(Context context) {
        return context.getFilesDir().getParent() + "/image/";
    }

    public static String getConfigFile(Context context, String fileName) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            AssetManager assetManager = context.getAssets();
            BufferedReader bf = new BufferedReader(new InputStreamReader(
                    assetManager.open(fileName)));
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    public static void saveBitmap(Context context, Bitmap mBtimap){
        //保存在相册
        Log.d("FileUtil :","begin to save picture");
        String PhotoPath;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            //外部存储可用
           // PhotoPath = Environment.getExternalStorageDirectory().getAbsolutePath()+ Constant.photo_path;
            PhotoPath = Environment.getExternalStorageDirectory().toString()+ Constant.photo_path;
        }else {
            //外部存储不可用
            PhotoPath = context.getCacheDir().getPath()+ "/images/";
        }
        File file = new File(PhotoPath);
        if(!file.exists()){
            file.mkdirs();
        }
        try {
            File pictrueFile = new File(PhotoPath + UUID.randomUUID().toString() + ".jpg");
            Log.d("saveBitmap", "path: "+PhotoPath + UUID.randomUUID().toString() + ".jpg");
            if(!pictrueFile.exists()){
                pictrueFile.createNewFile();
            }
            FileOutputStream out = new FileOutputStream(pictrueFile);
            mBtimap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
            Log.d("FileUtil", "save picture success !");
            Uri uri = Uri.fromFile(file);
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
        } catch (Exception e) {
            e.printStackTrace();
        }

        /*
        //保存在本地
          String savePath;
            File filePic;
            savePath =  context.getExternalFilesDir(null)+"/1.JPEG";
            try {
                filePic = new File(savePath);
                if (!filePic.exists()) {
                    filePic.getParentFile().mkdirs();
                    filePic.createNewFile();
                }
                FileOutputStream fos = new FileOutputStream(filePic);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.flush();
                fos.close();
            } catch (IOException e) {
                Log.e("saveBitmap","saveBitmap" + e.getMessage());
                return;
            }
            Log.i( "saveBitmap","saveBitmap success: " + filePic.getAbsolutePath());
        }
         */
    }
}
