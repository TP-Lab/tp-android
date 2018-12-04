package com.tokenbank.base;

import com.tokenbank.utils.GsonUtil;



public interface WCallback {

    void onGetWResult(int ret, GsonUtil extra);
}
