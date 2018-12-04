package com.tokenbank.net.load;


import com.tokenbank.net.apirequest.ApiRequest;
import com.tokenbank.net.listener.LoadDataListener;

/**
 */
public interface ILoadData {

    void loadData(ApiRequest request, boolean shouldCache, LoadDataListener listener);

}
