package com.tokenbank.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.tokenbank.R;
import com.tokenbank.base.WalletInfoManager;
import com.tokenbank.utils.FileUtil;
import com.tokenbank.utils.ToastUtil;
import com.tokenbank.view.TitleBar;


public class ModifyPwdActivity extends BaseActivity implements TitleBar.TitleBarClickListener, View.OnClickListener {


    private TitleBar mTitleBar;

    private EditText mEdtOldPwd;
    private EditText mEdtNewPwd;
    private EditText mEdtReaptNewPwd;

    private TextView mTvForgetPwdTips;
    private WalletInfoManager.WData mWalletData;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_pwd);
        if (getIntent() != null) {
            String walletAddress = getIntent().getStringExtra("Wallet_Address");
            if (!TextUtils.isEmpty(walletAddress)) {
                mWalletData = WalletInfoManager.getInstance().getWData(walletAddress);
            }
        }
        if (mWalletData == null) {
            finish();
            return;
        }
        initView();
    }

    @Override
    public void onLeftClick(View view) {
        finish();
    }

    @Override
    public void onRightClick(View view) {
        if (TextUtils.isEmpty(mEdtOldPwd.getText().toString())) {
            showTipAlertDialog("旧密码不能为空");
            return;
        }
        if (TextUtils.isEmpty(mEdtNewPwd.getText().toString())) {
            showTipAlertDialog("新密码不能为空");
            return;
        }

        if (TextUtils.isEmpty(mEdtReaptNewPwd.getText().toString())) {
            showTipAlertDialog("新密码确认不能为空");
            return;
        }
        if (!TextUtils.equals(mEdtReaptNewPwd.getText().toString(), mEdtNewPwd.getText().toString())) {
            showTipAlertDialog("两次输入的新密码不同，请确认");
            return;
        }
        if (mEdtNewPwd.getText().toString().length() < 8) {
            showTipAlertDialog("密码长度不能少于8位");
            return;
        }
        if (TextUtils.equals(mEdtNewPwd.getText().toString(), mEdtOldPwd.getText().toString())) {
            showTipAlertDialog("新旧密码不能一样，请重新填写");
            return;
        }
        String oldHash = FileUtil.getStringContent(mEdtOldPwd.getText().toString());
        if (!TextUtils.equals(oldHash, mWalletData.whash)) {
            showTipAlertDialog("旧密码错误，请重新输入旧密码");
            mEdtOldPwd.setText("");
            return;
        }
        ToastUtil.toast(ModifyPwdActivity.this, "修改密码成功");
        WalletInfoManager.getInstance().updateWalletHash(mWalletData.waddress, FileUtil.getStringContent(mEdtNewPwd.getText().toString()));
        this.finish();
    }

    @Override
    public void onMiddleClick(View view) {

    }

    @Override
    public void onClick(View view) {
        if (view == mTvForgetPwdTips) {
            gotoImportPrivateKeyFragment();
        }
    }


    public static void startModifyPwdActivity(Context context, String walletAddress) {
        Intent intent = new Intent(context, ModifyPwdActivity.class);
        intent.putExtra("Wallet_Address", walletAddress);
        intent.addFlags(context instanceof BaseActivity ? 0 : Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    private void initView() {
        mTitleBar = (TitleBar) findViewById(R.id.title_bar);
        mTitleBar.setLeftDrawable(R.drawable.ic_back);

        mTitleBar.setTitle("更改密码");

        mTitleBar.setRightText("完成");
        mTitleBar.setRightTextColor(R.color.white);
        mTitleBar.setTitleBarClickListener(this);

        mEdtOldPwd =  findViewById(R.id.edt_old_pwd);
        mEdtNewPwd =  findViewById(R.id.edt_new_pwd);
        mEdtReaptNewPwd =  findViewById(R.id.edt_new_repeat_pwd);
        mTvForgetPwdTips =  findViewById(R.id.tv_forgetpwd_tip);
        mTvForgetPwdTips.setText(Html.fromHtml("<font color='#929da6'>忘记密码? 导入助记词或私钥可重置密码。</font><font color='#2890fe'>立即导入?</font>"));
        mTvForgetPwdTips.setOnClickListener(this);
    }

    private void showTipAlertDialog(String tips) {
        new AlertDialog.Builder(ModifyPwdActivity.this).setTitle(tips).setNegativeButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).show();
    }

    private void gotoImportPrivateKeyFragment() {
        ImportWalletActivity.startImportWalletActivity(ModifyPwdActivity.this);
    }
}
