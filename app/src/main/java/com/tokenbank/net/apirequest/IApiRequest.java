package com.tokenbank.net.apirequest;

import com.android.volley.VolleyError;

import java.util.Map;

public interface IApiRequest {
    String initUrl();		//初始化Url

    String initRequest();		//初始化Request对象，请求体

    byte[] initByteRequest();	// 初始化二进制Request对象，请求体

    void handleMessage(String response);//请求成功处理接口

    void handleError(int code, VolleyError error);		//请求失败处理接口


    int getMethod();

    Map<String, String> initHeader();//初始化Header

    void execute(); //执行http网络请求

    String initCookie();

    String iniEncoding();

    String initContentType();

}
