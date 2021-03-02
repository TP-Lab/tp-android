package com.tokenbank.web;

public interface IWebCallBack {
    void onBack();

    void onClose();

    void switchFullScreen(String status);

    void rollHorizontal();

    void setMenubar(boolean isShow);
}
