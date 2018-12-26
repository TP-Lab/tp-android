package com.tokenbank.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.tokenbank.TApplication;
import com.tokenbank.utils.LanguageUtil;
import com.tokenbank.utils.PermissionUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Locale;


public class BaseActivity extends AppCompatActivity {

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionUtil.getInstance().onRequestPermissionsResult(BaseActivity.this, requestCode, permissions, grantResults);

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TApplication application = (TApplication) getApplication();
        application.addActivity(this);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        TApplication application = (TApplication) getApplication();
        application.popActivity(this);
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(String str) {
        switch (str) {
            case "EVENT_REFRESH_LANGUAGE":
                Locale locale = LanguageUtil.getUserLocale(this);
                LanguageUtil.updateLocale(this, locale);
//                recreate();//刷新界面
                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                overridePendingTransition(0, 0);
                break;
        }
    }

}
