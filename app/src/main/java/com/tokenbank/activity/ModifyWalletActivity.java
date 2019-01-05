package com.tokenbank.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tokenbank.R;
import com.tokenbank.base.WalletInfoManager;
import com.tokenbank.dialog.PKDialog;
import com.tokenbank.dialog.PwdDialog;
import com.tokenbank.utils.ToastUtil;
import com.tokenbank.utils.Util;
import com.tokenbank.utils.ViewUtil;
import com.tokenbank.view.TitleBar;

public class ModifyWalletActivity extends BaseActivity implements View.OnClickListener, TitleBar.TitleBarClickListener,
        PwdDialog.PwdResult {
    private final static String TAG = "ModifyWalletActivity";

    private TitleBar mTitleBar;

    private TextView mTvWalletAddress;

    private EditText mEdtWalletName;
    private RelativeLayout mLayoutModifyPwd;
    private RelativeLayout mLayoutExportPrivateKey;

    private TextView mTvDeleteWallet;
    private TextView mTvBak;

    private double mAsset = 0;
    private WalletInfoManager.WData mWalletData;
    private String mWalletAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_wallet);
        if (getIntent() != null) {
            mWalletAddress = getIntent().getStringExtra("Wallet_Address");
        }
        mWalletData = WalletInfoManager.getInstance().getWData(mWalletAddress);
        if (mWalletData == null) {
            finish();
            return;
        }
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mWalletData = WalletInfoManager.getInstance().getWData(mWalletAddress);
        if (mWalletData == null) {
            finish();
            return;
        }
    }

    @Override
    public void onClick(View view) {
        if (view == mLayoutModifyPwd) {
            gotoModifyPwd();
        } else if (view == mLayoutExportPrivateKey) {
            verifyPwd("exportprivatekey");
        } else if (view == mTvDeleteWallet) {
            verifyPwd("deletewallet");
        } else if (view == mTvBak) {
            gotoBak();
        } else if (view == mTvWalletAddress) {
            Util.clipboard(ModifyWalletActivity.this, "", mTvWalletAddress.getText().toString());
            ToastUtil.toast(ModifyWalletActivity.this, getString(R.string.toast_wallet_address_copied));
        }
    }

    @Override
    public void onLeftClick(View view) {
        finish();
    }

    @Override
    public void onRightClick(View view) {
        saveWalletInfo();
    }

    @Override
    public void onMiddleClick(View view) {

    }

    @Override
    public void authPwd(String tag, boolean result) {
        if (TextUtils.equals(tag, "exportprivatekey")) {
            if (result) {
                realExportPrivateKey();
            } else {
                ToastUtil.toast(this, getString(R.string.toast_password_incorrect));
            }
        } else if (TextUtils.equals(tag, "deletewallet")) {
            if (result) {
                deleteWallet();
            } else {
                ToastUtil.toast(this, getString(R.string.toast_password_incorrect));
            }
        }
    }

    public static void startModifyWalletActivity(Context context, String walletAddress) {
        Intent intent = new Intent(context, ModifyWalletActivity.class);
        intent.putExtra("Wallet_Address", walletAddress);
        intent.addFlags(context instanceof BaseActivity ? 0 : Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    private void initView() {
        mTitleBar = (TitleBar) findViewById(R.id.title_bar);
        mTitleBar.setLeftDrawable(R.drawable.ic_back);
        mTitleBar.setTitle(mWalletData.wname);
        mTitleBar.setRightText(getString(R.string.titleBar_save));
        mTitleBar.setRightTextColor(R.color.white);
        mTitleBar.setTitleBarClickListener(this);

        mTvWalletAddress = (TextView) findViewById(R.id.tv_wallet_address);
        mTvWalletAddress.setText(mWalletData.waddress);
        mTvWalletAddress.setOnClickListener(this);

        mEdtWalletName = (EditText) findViewById(R.id.edt_wallet_name);
        mEdtWalletName.setText(mWalletData.wname);

        mLayoutModifyPwd = (RelativeLayout) findViewById(R.id.layout_modify_pwd);
        mLayoutModifyPwd.setOnClickListener(this);

        mLayoutExportPrivateKey = (RelativeLayout) findViewById(R.id.layout_export_privatekey);
        mLayoutExportPrivateKey.setOnClickListener(this);

        mTvDeleteWallet = (TextView) findViewById(R.id.tv_delete_wallet);
        mTvBak = (TextView) findViewById(R.id.tv_bak);
        mTvDeleteWallet.setOnClickListener(this);
        mTvBak.setOnClickListener(this);

        if (!mWalletData.isBaked) {
            if (mWalletData.type == 1) {
                mTvBak.setVisibility(View.VISIBLE);
            } else if (mWalletData.type == 2) {
                mTvBak.setVisibility(View.GONE);
            }
        }

    }

    private void gotoModifyPwd() {
        ModifyPwdActivity.startModifyPwdActivity(ModifyWalletActivity.this, mWalletData.waddress);
    }

    private void verifyPwd(String tag) {
        PwdDialog pwdDialog = new PwdDialog(this, this, mWalletData.whash, tag);
        pwdDialog.show();
    }

    private void realExportPrivateKey() {
        PKDialog PKDialog = new PKDialog(this,
                mWalletData.wpk);
        PKDialog.show();
    }

    private void saveWalletInfo() {
        String newWalletName = mEdtWalletName.getText().toString();
        if (TextUtils.isEmpty(newWalletName)) {
            ViewUtil.showSysAlertDialog(this, getString(R.string.dialog_content_no_wallet_name), "Ok");
            return;
        }
        mWalletData.wname = newWalletName;
        WalletInfoManager.getInstance().updateWalletName(mWalletData.waddress, newWalletName);
        finish();
    }

    private void deleteWallet() {
        if (!mWalletData.isBaked) {
            if (mWalletData.type == 1) {
                ViewUtil.showSysAlertDialog(ModifyWalletActivity.this, getString(R.string.dialog_title_warning), getString(R.string.dialog_content_no_wallet_backup), getString(R.string.dialog_btn_backup), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        gotoBak();
                        dialog.dismiss();
                    }
                }, getString(R.string.dialog_btn_delete), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        WalletInfoManager.getInstance().deleteWallet(ModifyWalletActivity.this, mWalletData);
                        dialog.dismiss();
                        finish();
                    }
                });
            } else if (mWalletData.type == 2) {
                ViewUtil.showSysAlertDialog(ModifyWalletActivity.this, getString(R.string.dialog_title_warning), getString(R.string.dialog_content_no_key_backup), getString(R.string.dialog_btn_backup), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        verifyPwd("exportprivatekey");
                        dialog.dismiss();
                    }
                }, getString(R.string.dialog_btn_delete), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        WalletInfoManager.getInstance().deleteWallet(ModifyWalletActivity.this, mWalletData);
                        dialog.dismiss();
                        finish();
                    }
                });
            }
        } else {
            WalletInfoManager.getInstance().deleteWallet(ModifyWalletActivity.this, mWalletData);
            finish();
        }
    }

    private void gotoBak() {
        String[] words = null;
        words = mWalletData.words.split(" ");
        if (words == null || words.length < 12) {
            ToastUtil.toast(ModifyWalletActivity.this, getString(R.string.toast_cant_backup));
            return;
        }
        StartBakupActivity.startBakupWalletStartActivity(ModifyWalletActivity.this, mWalletData.waddress, 2);
    }

}
