package com.tokenbank.net.apirequest;

import com.android.volley.Request;


public abstract class BaseGetApiRequest extends ApiRequest {


    @Override
    public int getMethod() {
        return Request.Method.GET;
    }

    @Override
    public String initRequest() {
        return null;
    }

    @Override
    public byte[] initByteRequest() {
        return new byte[0];
    }

    @Override
    public String initCookie() {
        return null;
    }

    @Override
    public String iniEncoding() {
        return null;
    }

    @Override
    public String initContentType() {
        return "application/json";
    }
}
