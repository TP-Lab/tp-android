package com.tokenbank.net;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
//import android.net.http.AndroidHttpClient;
import android.os.Build;

import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.HurlStack;
import com.tokenbank.config.AppConfig;
import com.tokenbank.net.apirequest.ApiRequest;
import com.tokenbank.net.volleyext.BaseJsonRequest;


import java.io.File;


public class NetManager<T> implements INetInterface {
	private static NetManager mInstance;
	private RequestQueue mRequestQueue;
	private static Context mCtx;
	private Request<String> mRequest;
	private final static int THREAD_POOL_SIZE = 10;

	private NetManager() {
		mCtx = AppConfig.getContext();
		mRequestQueue = getRequestQueue();
	}

	public static synchronized NetManager getInstance() {
		if (mInstance == null) {
			mInstance = new NetManager();
		}
		return mInstance;
	}
	
	

	public static RequestQueue newRequestQueue(Context context, HttpStack stack, int maxDiskCacheBytes) {
        File cacheDir = new File(context.getCacheDir(), "volley");

        String userAgent = "volley/0";
        try {
            String packageName = context.getPackageName();
            PackageInfo info = context.getPackageManager().getPackageInfo(packageName, 0);
            userAgent = packageName + "/" + info.versionCode;
        } catch (NameNotFoundException e) {
        }

        if (stack == null) {
            if (Build.VERSION.SDK_INT >= 9) {
                stack = new HurlStack();
            } else {
                // Prior to Gingerbread, HttpUrlConnection was unreliable.
                // See: http://android-developers.blogspot.com/2011/09/androids-http-clients.html
//                stack = new HttpClientStack(AndroidHttpClient.newInstance(userAgent));
            }
        }

        Network network = new BasicNetwork(stack);
        RequestQueue queue;
        if (maxDiskCacheBytes <= -1)
        {
        	// No maximum size specified
        	queue = new RequestQueue(new DiskBasedCache(cacheDir), network, THREAD_POOL_SIZE);
        }
        else
        {
        	// Disk cache size specified
        	queue = new RequestQueue(new DiskBasedCache(cacheDir, maxDiskCacheBytes), network, THREAD_POOL_SIZE);
        }
        queue.start();
        return queue;
    }

	public RequestQueue getRequestQueue() {
		if (mRequestQueue == null) {
			mRequestQueue = newRequestQueue(mCtx.getApplicationContext(), null, -1);
		}
		return mRequestQueue;
	}

	public <T> void addToRequestQueue(Request<T> req) {
		getRequestQueue().add(req);
	}

	@SuppressWarnings("unchecked")
	// 同步锁 防止多线程交叉
	public synchronized void setRequestTask(
			final ApiRequest mAbstractRequestTask) {
		mRequest = new BaseJsonRequest<T>(mAbstractRequestTask.getMethod(),
				mAbstractRequestTask.initUrl(),
				mAbstractRequestTask.initHeader(),
				mAbstractRequestTask.initRequest(), new Listener<String>() {
					// 网络请求成功
					@Override
					public void onResponse(String response) {
						mAbstractRequestTask.handleMessage(response);
					}
				}, new ErrorListener() {
					// 网络请求失败
					@Override
					public void onErrorResponse(VolleyError error) {
						mAbstractRequestTask.handleError(AppConfig.ERR_CODE.NETWORK_ERR, error);
					}
				});
		mRequest.setShouldCache(false);
		start();

	}

	// 启动请求
	@Override
	public void start() {
		if (mRequest != null) {
			addToRequestQueue(mRequest);
		}
	}

	// 取消请求
	@Override
	public void cancel() {
		if (mRequest != null) {
			mRequest.cancel();
		}
	}
}
