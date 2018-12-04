package com.tokenbank.utils;


import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.nostra13.universalimageloader.cache.disc.DiskCache;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.memory.impl.LRULimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.decode.BaseImageDecoder;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.utils.MemoryCacheUtils;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.nostra13.universalimageloader.utils.L;
import com.tokenbank.R;
import com.tokenbank.config.AppConfig;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class TokenImageLoader {

    private final static String TAG = "TokenImageLoader";

    static {
        ActivityManager am = (ActivityManager) AppConfig.getContext().getSystemService(Context.ACTIVITY_SERVICE);
        int memoryClass = am.getMemoryClass();
        int largeMemoryClass = am.getLargeMemoryClass();
        long runTimeMaxMemory = Runtime.getRuntime().maxMemory() / 1024 / 1024;
        int memoryCacheSize = 1024 * 1024 * largeMemoryClass / 8;
        TLog.d(TAG, "memoryClass=" + memoryClass + " largeMemoryClass=" + largeMemoryClass + " runTimeMaxMemory=" + runTimeMaxMemory);
        TLog.d(TAG, "memoryCacheSize=" + memoryCacheSize);

        File cacheDir = StorageUtils.getCacheDirectory(AppConfig.getContext());
        DiskCache diskCache;

        long diskCacheSize = DeviceUtil.getAvailableExternalMemorySize();
        TLog.d("XlImageLoader", "AvailableExternalMemorySize=" + diskCacheSize / 1024 / 1204 + "MB");
        diskCacheSize = diskCacheSize / 5;//可用空间的5分之一
        if (diskCacheSize < 100 * 1024 * 1024) diskCacheSize = 100 * 1024 * 1024;//至少100MB
        if (diskCacheSize > 1024 * 1024 * 1024) diskCacheSize = 1024 * 1024 * 1024;//至多1G
        diskCache = new UnlimitedDiscCache(cacheDir);

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(AppConfig.getContext())
                .threadPoolSize(4) //
                .threadPriority(Thread.NORM_PRIORITY) //
                .tasksProcessingOrder(QueueProcessingType.FIFO) //
                .denyCacheImageMultipleSizesInMemory()
                .memoryCache(new LRULimitedMemoryCache(memoryCacheSize))//默认值堆内存（或最大堆）8分之一
                .diskCacheSize((int) diskCacheSize)
                .diskCache(diskCache)
                .imageDecoder(new BaseImageDecoder(true)) // default
                .defaultDisplayImageOptions(DisplayImageOptions.createSimple()) // default
                .imageDownloader(new ImageLoaderDownloaderEx(AppConfig.getContext())) // 替换成自己的
                .build();

        ImageLoader.getInstance().init(config);
        L.disableLogging();
    }

    private static DisplayImageOptions defaultOption = new DisplayImageOptions.Builder()
            .showImageOnLoading(R.drawable.ic_images_common_loading)// 设置图片在下载期间显示的图片
            .showImageForEmptyUri(R.drawable.img_load_fail)// 设置图片Uri为空或是错误的时候显示的图片
            .showImageOnFail(R.drawable.img_load_fail)// 设置图片加载/解码过程中错误时候显示的图片
            .cacheInMemory(true)// 是否緩存都內存中
            .cacheOnDisk(true)// 是否緩存到sd卡上
            .bitmapConfig(Bitmap.Config.RGB_565)
//			.imageScaleType(ImageScaleType.EXACTLY)
            .resetViewBeforeLoading(true)
            .considerExifParams(true)
            .displayer(new FadeInBitmapDisplayer(800, true, true, false))
            .build();


    /**
     * 默认方法，适用大部分情况
     * <p>
     * 图片将会被加入内存缓存，并且缓存到磁盘上，在加载中或者失败会显示默认底图
     *
     * @param url       图片地址
     * @param imageView 将下载的图片显示在该imageView上
     */
    public static void displayImage(String url, ImageView imageView) {
        ImageLoader.getInstance().displayImage(url, imageView, defaultOption);
    }

    public static void displayImage(String url, ImageView imageView,
                                        DisplayImageOptions options) {
        ImageLoader.getInstance().displayImage(url, imageView, options);
    }

    public static void displayImage(String url, ImageView imageView, ImageLoadingListener listener) {
        ImageLoader.getInstance().displayImage(url, imageView, defaultOption, listener);
    }

    public static void displayImage(String uri, ImageView imageView, DisplayImageOptions options,
                                        ImageLoadingListener listener, ImageLoadingProgressListener progressListener) {
        ImageLoader.getInstance().displayImage(uri, imageView, options,
                listener, progressListener);
    }

    public static void displayImage(String uri, ImageView imageView, ImageLoadingListener listener, ImageLoadingProgressListener progressListener) {
        ImageLoader.getInstance().displayImage(uri, imageView, defaultOption, listener, progressListener);
    }


    ////////////////////////////////////以下是圆角图片方法///////////////////////////

    public static void displayImageRound(String url, ImageView imageView,
                                               int roundPixels) {
        DisplayImageOptions options;
        options = new DisplayImageOptions.Builder().cacheOnDisk(true)
                .cacheInMemory(true).bitmapConfig(Bitmap.Config.RGB_565)
                .displayer(new RoundedBitmapDisplayer(roundPixels)).build();
        ImageLoader.getInstance().displayImage(url, imageView, options);
    }


    public static void displayImageRound(String url, ImageView imageView,
                                               boolean cacheInMemory, boolean cacheOnDisk, int roundPixels) {
        DisplayImageOptions options;
        options = new DisplayImageOptions.Builder().cacheOnDisk(cacheOnDisk)
                .cacheInMemory(cacheInMemory)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .displayer(new RoundedBitmapDisplayer(roundPixels)).build();
        ImageLoader.getInstance().displayImage(url, imageView, options);
    }

    public static void displayImageRound(String url, ImageView imageView,
                                               int roundPixels, ImageLoadingListener listener) {
        DisplayImageOptions options;
        options = new DisplayImageOptions.Builder().cacheOnDisk(true)
                .cacheInMemory(true).bitmapConfig(Bitmap.Config.RGB_565)
                .displayer(new RoundedBitmapDisplayer(roundPixels)).build();
        ImageLoader.getInstance().displayImage(url, imageView, options,
                listener);
    }

    /////////////////////////////其他方法////////////////////////////////////

    public static String getCacheImagePath(String url) {
        ImageLoader imageLoader = ImageLoader.getInstance();
        DiskCache discCache = imageLoader.getDiskCache();
        File file = discCache.get(url);
        return file.getPath();
    }

    public static File getCacheImageFile(String url) {
        ImageLoader imageLoader = ImageLoader.getInstance();
        DiskCache discCache = imageLoader.getDiskCache();
        File file = discCache.get(url);
        return file;
    }

    public static boolean saveCache(String url, Bitmap bitmap) {
        ImageLoader imageLoader = ImageLoader.getInstance();
        DiskCache discCache = imageLoader.getDiskCache();
        try {
            return discCache.save(url, bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static Bitmap getBitmapCache(String url) {

        Bitmap b = null;
        List<Bitmap> values = MemoryCacheUtils.findCachedBitmapsForImageUri(url, ImageLoader.getInstance().getMemoryCache());
        if (values.size() > 0) {
            b = values.get(0);
        }
        return b;
    }

    public static DisplayImageOptions imageOption(int loadingDrawable) {
        return imageOption(loadingDrawable, R.drawable.img_load_fail, R.drawable.img_load_fail);
    }

    public static DisplayImageOptions imageOption(int loadingDrawable, int emptyUriDrawable, int failDrawable) {
        return new DisplayImageOptions.Builder()
                .showImageOnLoading(loadingDrawable)// 设置图片在下载期间显示的图片
                .showImageForEmptyUri(emptyUriDrawable)// 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(failDrawable)// 设置图片加载/解码过程中错误时候显示的图片
                .cacheInMemory(true)// 是否緩存都內存中
                .cacheOnDisk(true)// 是否緩存到sd卡上
                .bitmapConfig(Bitmap.Config.RGB_565)
                .resetViewBeforeLoading(true)
                .displayer(new FadeInBitmapDisplayer(800, true, true, false))
                .build();
    }
}
