package com.tokenbank.utils;


import android.content.Context;

import com.nostra13.universalimageloader.core.download.BaseImageDownloader;

import java.io.IOException;
import java.io.InputStream;


public class ImageLoaderDownloaderEx extends BaseImageDownloader {
    static private String TAG = "ImageLoaderDownloaderEx";

    public ImageLoaderDownloaderEx(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    public ImageLoaderDownloaderEx(Context context, int connectTimeout, int readTimeout) {
        super(context, connectTimeout, readTimeout);
    }

    @Override
    public InputStream getStream(String imageUri, Object extra) throws IOException {

        return super.getStream(imageUri, extra);
    }

    protected String readURLParamValue(String url, String param, String defvalue) {

        String key = param + "=";
        int posStart = url.indexOf(key);
        if (posStart < 0) {
            return defvalue;
        }

        String value = "";
        int posEnd = url.indexOf("&", posStart);
        if (posEnd < 0) {
            value = url.substring(posStart + key.length());
        } else {
            value = url.substring(posStart + key.length(), posEnd);
        }
        return value;
    }

}