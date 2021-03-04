package com.tokenbank.net.api;

import com.android.volley.VolleyError;
import com.tokenbank.base.TBController;
import com.tokenbank.config.Constant;
import com.tokenbank.net.apirequest.BaseGetApiRequest;
import com.tokenbank.utils.TLog;

/**
 * Created by Administrator on 2018/1/16.
 */

public class GetWalletTokenList extends BaseGetApiRequest {

    public String mAddress;
    private int mType;

    public GetWalletTokenList(String address, int type) {
        this.mAddress = address;
        this.mType = type;
    }

    @Override
    public String initUrl() {
        if (this.mType == TBController.SWT_INDEX) {
            return Constant.JC_EXCHANGE_SERVER + "/exchange/balances/" + mAddress;
        } else if (this.mType == TBController.ETH_INDEX) {
            return Constant.ETHPLORER_SERVER + "/getAddressInfo/" + mAddress + "?apiKey=freekey";
        }
        return "";
    }

    @Override
    public void handleMessage(String response) {

    }

    @Override
    public void handleError(int code, VolleyError error) {

    }

}
