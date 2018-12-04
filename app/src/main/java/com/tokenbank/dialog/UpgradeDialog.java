package com.tokenbank.dialog;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.tokenbank.R;
import com.tokenbank.config.AppConfig;
import com.tokenbank.utils.ToastUtil;
import com.tokenbank.utils.Util;



public class UpgradeDialog extends BaseDialog {

    private final static String TAG = "PKDialog";

    private TextView mTvUpgrade;
    private String mUpgradeUrl;

    public UpgradeDialog(@NonNull Context context, String upgradeUrl) {
        super(context, R.style.DialogStyle);
        this.mUpgradeUrl = upgradeUrl;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCanceledOnTouchOutside(false);
        setContentView(R.layout.layout_dialog_upgrade);
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = -2;
        lp.height = -2;
        lp.x = 0;
        lp.y = 0;
        lp.gravity = Gravity.CENTER;
        getWindow().setAttributes(lp);
        initView();
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }


    private void initView() {
        mTvUpgrade = (TextView) findViewById(R.id.tv_upgrade);
        mTvUpgrade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(mUpgradeUrl)) {
                    dismiss();
                    return;
                }
                Uri url = Uri.parse(mUpgradeUrl);
                Intent intent = new Intent(Intent.ACTION_VIEW, url);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getContext().startActivity(intent);

//                AppConfig.getContext().clearActivity();
//                android.os.Process.killProcess(android.os.Process.myPid());
//                System.exit(0);

            }
        });
    }
}
