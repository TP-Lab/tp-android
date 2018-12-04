package com.tokenbank.net.api.jtrequest;

import android.text.TextUtils;

import com.android.volley.VolleyError;
import com.tokenbank.base.WalletInfoManager;
import com.tokenbank.config.Constant;
import com.tokenbank.net.apirequest.BaseGetApiRequest;


public class JTTransactionListRequest extends BaseGetApiRequest {

    private int mPageSize;
    private String mJtAddress;
    private String mToken;
    private String mMarker;

    public JTTransactionListRequest(int pagesize, String token, String marker) {
        this.mPageSize = pagesize;
        this.mJtAddress = WalletInfoManager.getInstance().getWAddress();
        this.mToken = token;
        this.mMarker = marker;
    }

    public JTTransactionListRequest(int pagesize, String marker) {
        this(pagesize, "", marker);

    }

    @Override
    public String initUrl() {
        return Constant.jt_base_url + "/v2/accounts/" + mJtAddress + "/payments?results_per_page=" + mPageSize +
                (TextUtils.isEmpty(mToken) ? "" : "&currency=" + mToken) + (TextUtils.isEmpty(mMarker) ? "" : "&marker=" + mMarker);
    }

    @Override
    public void handleMessage(String response) {

    }

    @Override
    public void handleError(int code, VolleyError error) {

    }
}
