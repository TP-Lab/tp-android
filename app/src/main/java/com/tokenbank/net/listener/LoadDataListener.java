package com.tokenbank.net.listener;

/**
 */
public interface LoadDataListener{

    void loadSuccess(String result);

    void loadFailed(Throwable throwable, int reqId);

    void loadFinish();

}
