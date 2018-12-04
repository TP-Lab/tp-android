package com.tokenbank.net.volleyext;


import android.preference.PreferenceManager;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;
import com.tokenbank.config.AppConfig;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.zip.GZIPInputStream;


public class BaseJsonRequest<T> extends JsonRequest<String> {
	private final static int TIME_OUT=10*1000;
	private Map<String, String> mHeader;
	private String mContentType;
	public BaseJsonRequest(int method, String url, Map<String, String> header , String requestBody,
						   Listener<String> listener, ErrorListener errorListener) {
		super(method, url, requestBody, listener, errorListener);
		this.mHeader = header;
		if(mHeader != null && mHeader.containsKey("Content-Type"))
		{
			mContentType = mHeader.get("Content-Type");
			mHeader.remove("Content-Type");
		}
		setRetryPolicy(new DefaultRetryPolicy(
				TIME_OUT, 
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
	}

	@Override
	protected Response<String> parseNetworkResponse(NetworkResponse response) {
		try {
			if(response.headers.containsKey("Content-Encoding"))
			{
				if(response.headers.get("Content-Encoding").equals("gzip"))
				{
					String jsonString = getRealString(response.data);
					return Response.success(jsonString,
							HttpHeaderParser.parseCacheHeaders(response));
				}
			}
			if (response.headers.containsKey("Set-Cookie")) {
				String rawCookies = response.headers.get("Set-Cookie");
				if (rawCookies.toUpperCase().contains("SESSIONID")) {
					PreferenceManager.getDefaultSharedPreferences(AppConfig.getContext()).edit().putString("Cookie", rawCookies).commit();
				}
			}
			String jsonString = new String(response.data,
					parseCharset(response.headers));
			return Response.success(jsonString,
					HttpHeaderParser.parseCacheHeaders(response));
		} catch (UnsupportedEncodingException e) {
			return Response.error(new ParseError(e));
		}
	}

	@Override
	public Map<String, String> getHeaders() throws AuthFailureError {
		return mHeader;
	}

	 @Override
	 public String getBodyContentType() {
		 if(mContentType != null)
		 {
			 return mContentType;
		 }
		 else
		 {
			 return super.getBodyContentType();
		 }
	 }

	private int getShort(byte[] data) {
		return ((data[0] << 8) | data[1] & 0xFF);
	}

	private String getRealString(byte[] data) {
		byte[] h = new byte[2];
		h[0] = (data)[0];
		h[1] = (data)[1];
		int head = getShort(h);
		boolean t = head == 0x1f8b;
		InputStream in;
		StringBuilder sb = new StringBuilder();
		try {
			ByteArrayInputStream bis = new ByteArrayInputStream(data);
			if (t) {
				in = new GZIPInputStream(bis);
			} else {
				in = bis;
			}
			BufferedReader r = new BufferedReader(new InputStreamReader(in),
					1000);
			for (String line = r.readLine(); line != null; line = r.readLine()) {
				sb.append(line);
			}
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sb.toString();
	}

	public static String parseCharset(Map<String, String> headers, String defaultCharset) {
		String contentType = (String)headers.get("Content-Type");
		if(contentType != null) {
			if(contentType.equals("application/json"))
			{
				return "utf-8";
			}
			String[] params = contentType.split(";");

			for(int i = 1; i < params.length; ++i) {
				String[] pair = params[i].trim().split("=");
				if(pair.length == 2 && pair[0].equals("charset")) {
					return pair[1];
				}
			}
		}

		return defaultCharset;
	}

	public static String parseCharset(Map<String, String> headers) {
//		return parseCharset(headers, "ISO-8859-1");
		return parseCharset(headers, "utf-8");
	}
}
