package com.tokenbank.net.api.jtrequest;

import com.android.volley.VolleyError;
import com.google.gson.JsonObject;
import com.tokenbank.config.Constant;
import com.tokenbank.net.apirequest.BasePostApiRequest;

/**
 */

public class JTTransactionRequest extends BasePostApiRequest {

    private String mSignedStr;

    public JTTransactionRequest(String signedStr) {
        this.mSignedStr = signedStr;
    }

    @Override
    public String initUrl() {
        return Constant.JC_EXCHANGE_SERVER + "/exchange/sign_payment";
    }

    @Override
    public String initRequest() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("sign", mSignedStr);
        return jsonObject.toString();
    }

    @Override
    public void handleMessage(String response) {

    }

    @Override
    public void handleError(int code, VolleyError error) {

    }

    @Override
    public String initContentType() {
        return "application/json";
    }
}
