package com.tokenbank.net.api.jtrequest;

import com.android.volley.VolleyError;
import com.tokenbank.base.WalletInfoManager;
import com.tokenbank.config.Constant;
import com.tokenbank.net.apirequest.BaseGetApiRequest;

/**
 * Created by Administrator on 2018/3/11.
 */

public class JTTransactionDetailsRequest extends BaseGetApiRequest {

    private String mHash;
    private String mJtAddress;

    public JTTransactionDetailsRequest(String hash) {
        this.mHash = hash;
        this.mJtAddress = WalletInfoManager.getInstance().getWAddress();

    }

    @Override
    public String initUrl() {
        return Constant.jt_base_url + "/v2/accounts/" + mJtAddress + "/payments/" + mHash;
    }

    @Override
    public void handleMessage(String response) {

    }

    @Override
    public void handleError(int code, VolleyError error) {

    }
}
