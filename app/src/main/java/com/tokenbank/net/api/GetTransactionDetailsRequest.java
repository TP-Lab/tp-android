package com.tokenbank.net.api;

import com.android.volley.VolleyError;
import com.tokenbank.config.Constant;
import com.tokenbank.net.apirequest.BaseGetApiRequest;

/**
 * Created by Administrator on 2018/1/21.
 */

public class GetTransactionDetailsRequest extends BaseGetApiRequest {

    private String mHash;

    public GetTransactionDetailsRequest(String hash) {
        this.mHash = hash;
    }

    @Override
    public String initUrl() {
        return "";
    }

    @Override
    public void handleMessage(String response) {

    }

    @Override
    public void handleError(int code, VolleyError error) {

    }
}
