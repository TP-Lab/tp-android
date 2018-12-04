package com.tokenbank.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.tokenbank.TApplication;
import com.tokenbank.utils.PermissionUtil;


public class BaseActivity extends AppCompatActivity {

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionUtil.getInstance().onRequestPermissionsResult(BaseActivity.this, requestCode, permissions, grantResults);

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TApplication application = (TApplication)getApplication();
        application.addActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        TApplication application = (TApplication)getApplication();
        application.popActivity(this);
    }
}
