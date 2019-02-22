package com.tokenbank.net.api;

import android.text.TextUtils;

import com.android.volley.VolleyError;
import com.tokenbank.config.Constant;
import com.tokenbank.net.apirequest.BaseGetApiRequest;

/**
 * Created by Administrator on 2018/2/3.
 * 交易列表
 */

public class GetTransactionRecordRequest extends BaseGetApiRequest {

    private int mStart;
    private int mPageSize;
    private String mAddress;
    private String mSort;
    private String mContractAddress;

    public GetTransactionRecordRequest(int start, int pageSize, String address, String contractAddress) {
        this.mStart = start;
        this.mPageSize = pageSize;
        this.mAddress = address;
        this.mContractAddress = contractAddress;
    }

    public GetTransactionRecordRequest(int start, int pageSize, String address) {
        this.mStart = start;
        this.mPageSize = pageSize;
        this.mAddress = address;
    }

    public GetTransactionRecordRequest(int start, int pageSize, String address, String sort, String contractAddress) {
        this.mStart = start;
        this.mPageSize = pageSize;
        this.mAddress = address;
        this.mSort = sort;
        this.mContractAddress = contractAddress;
    }

    @Override
    public String initUrl() {

        //eth
        // https://api.etherscan.io/api?module=account&action=txlist&address=&page=1&offset=10&sort=desc&apikey=YourApiKeyToken

        // erc20
        // https://api.etherscan.io/api?module=account&action=tokentx&contractaddress=&address=&page=1&offset=100&sort=asc&apikey=YourApiKeyToken

        if (TextUtils.isEmpty(mContractAddress)) {
            return Constant.ETHERSCAN_SERVER + "/api?module=account&action=txlist&address=" + mAddress + "&page=" + mStart + "&offset=" + mPageSize + "&sort=desc&apikey=" + Constant.ETHERSCAN_API_TOKEN;
        }
        return Constant.ETHERSCAN_SERVER + "/api?module=account&action=tokentx&address=" + mAddress + "&contractaddress=" + mContractAddress + "&page=" + mStart + "&offset=" + mPageSize + "&sort=desc&apikey=" + Constant.ETHERSCAN_API_TOKEN;
    }

    @Override
    public void handleMessage(String response) {

    }

    @Override
    public void handleError(int code, VolleyError error) {

    }
}
