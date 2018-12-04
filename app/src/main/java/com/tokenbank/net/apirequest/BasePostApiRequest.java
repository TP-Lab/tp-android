package com.tokenbank.net.apirequest;

import com.android.volley.Request;


public abstract class BasePostApiRequest extends ApiRequest {
	@Override
	public byte[] initByteRequest() {
		return new byte[0];
	}

	@Override
	public int getMethod() {
		return Request.Method.POST;
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
		return "application/x-www-form-urlencoded;charset=utf-8";
	}
}
