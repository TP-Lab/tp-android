package com.tokenbank.net.api.jtrequest;

import com.android.volley.VolleyError;
import com.tokenbank.config.Constant;
import com.tokenbank.net.apirequest.BaseGetApiRequest;


public class JTBalanceRequest extends BaseGetApiRequest {
    private String mJtWalletAddress;

    public JTBalanceRequest(String address) {
        this.mJtWalletAddress = address;
    }
    @Override
    public String initUrl() {
        return Constant.jt_base_url +"/v2/accounts/" + this.mJtWalletAddress + "/balances";
    }

    @Override
    public void handleMessage(String response) {

    }

    @Override
    public void handleError(int code, VolleyError error) {

    }
}
