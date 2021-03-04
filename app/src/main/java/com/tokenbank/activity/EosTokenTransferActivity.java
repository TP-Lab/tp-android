
package com.tokenbank.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.tokenbank.R;
import com.tokenbank.base.BaseWalletUtil;
import com.tokenbank.base.TBController;
import com.tokenbank.base.WCallback;
import com.tokenbank.base.WalletInfoManager;
import com.tokenbank.dialog.EosOrderDetailDialog;
import com.tokenbank.dialog.PwdDialog;
import com.tokenbank.utils.GsonUtil;
import com.tokenbank.utils.TLog;
import com.tokenbank.utils.ToastUtil;
import com.tokenbank.utils.Util;
import com.tokenbank.utils.ViewUtil;
import com.tokenbank.view.TitleBar;

import java.text.DecimalFormat;


public class EosTokenTransferActivity extends BaseActivity implements View.OnClickListener {

    public final static String TAG = "EosTokenTransferActivity";
    private TitleBar mTitleBar;
    private TextView mTvToken;
    private EditText mEdtWalletAddress, mEdtTransferNum, mEdtTransferRemark;
    private Button mBtnNext;
    private BaseWalletUtil mWalletUtil;
    private WalletInfoManager.WData mWalletData; //当前使用哪个钱包转账
    private String mContractAddress;
    private String mOriginAddress;
    private String mReceiveAddress;
    private String mTokenSymbol;
    private double mAmount;
    private boolean defaultToken;
    private int mDecimal = 0;
    private int mBlockChain;

    private final static String CONTRACT_ADDRESS_KEY = "Contact_Address";
    private final static String RECEIVE_ADDRESS_KEY = "Receive_Address";
    private final static String TOKEN_SYMBOL_KEY = "Token_Symbol";
    private final static String TOKEN_DECIMAL = "Token_Decimal";
    private final static String TOEKN_AMOUNT = "Token_Amount";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.eos_activity_transfer_token);
        if (getIntent() != null) {
            mOriginAddress = getIntent().getStringExtra(RECEIVE_ADDRESS_KEY);
            mContractAddress = getIntent().getStringExtra(CONTRACT_ADDRESS_KEY);
            mTokenSymbol = getIntent().getStringExtra(TOKEN_SYMBOL_KEY);
            mDecimal = getIntent().getIntExtra(TOKEN_DECIMAL, 0);
            mAmount = getIntent().getDoubleExtra(TOEKN_AMOUNT, 0.0f);
        }

        mWalletData = WalletInfoManager.getInstance().getCurrentWallet();
        TLog.d(TAG, "Set-mWalletData = " + mWalletData);
        if (mWalletData == null) {
            this.finish();
            return;
        }
        mWalletUtil = TBController.getInstance().getWalletUtil(mWalletData.type);

        defaultToken = TextUtils.equals(mWalletUtil.getDefaultTokenSymbol(), mTokenSymbol);

        mBlockChain = WalletInfoManager.getInstance().getWalletType();
        initView();
    }

    private void initView() {
        mTitleBar = findViewById(R.id.title_bar);
        mTitleBar.setLeftDrawable(R.drawable.ic_back);
        mTitleBar.setTitle(getString(R.string.titleBar_transfer));
//        mTitleBar.setTitleBarClickListener(new TitleBar.TitleBarListener());
        mTitleBar.setTitleBarClickListener(new TitleBar.TitleBarListener() {
            @Override
            public void onLeftClick(View view) {
                EosTokenTransferActivity.this.finish();
            }
        });

        mTvToken = findViewById(R.id.tv_token_name);
        mTvToken.setOnClickListener(this);
        mTvToken.setText(TextUtils.isEmpty(mTokenSymbol) ? "" : mTokenSymbol);

        mEdtWalletAddress = findViewById(R.id.edt_wallet_address);

        mEdtTransferNum = findViewById(R.id.edt_transfer_num);

        mEdtWalletAddress.setText(mOriginAddress);

        DecimalFormat df = new DecimalFormat("0.0000");
        mEdtTransferNum.setText(mAmount > 0.0f ? df.format(mAmount).toString() : "");

        mEdtTransferRemark = findViewById(R.id.edt_transfer_remark);

        mBtnNext = findViewById(R.id.btn_next);

        mBtnNext.setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            mContractAddress = data.getStringExtra(CONTRACT_ADDRESS_KEY);
            mTokenSymbol = data.getStringExtra(TOKEN_SYMBOL_KEY);
            mDecimal = data.getIntExtra(TOKEN_DECIMAL, 0);
            mTvToken.setText(TextUtils.isEmpty(mTokenSymbol) ? "" : mTokenSymbol);
            defaultToken = TextUtils.equals(mWalletUtil.getDefaultTokenSymbol(), mTokenSymbol);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_next:
                if (paramCheck()) {
                    EosOrderDetailDialog orderDetailDialog = new EosOrderDetailDialog(EosTokenTransferActivity.this,
                            new EosOrderDetailDialog.onConfirmOrderListener() {
                                @Override
                                public void onConfirmOrder() {
                                    verifyPwd();
                                }
                            }, mWalletData.waddress, mEdtWalletAddress.getText().toString(),
                            Util.parseDouble(mEdtTransferNum.getText().toString()), mBlockChain, mTokenSymbol, defaultToken, mEdtTransferRemark.getText().toString());
                    orderDetailDialog.show();
                }
                break;
            case R.id.tv_token_name:
                Intent intent = new Intent(EosTokenTransferActivity.this, ChooseTokenTransferActivity.class);
                EosTokenTransferActivity.this.startActivityForResult(intent, 0);
        }
    }

    private void verifyPwd() {
        PwdDialog pwdDialog = new PwdDialog(EosTokenTransferActivity.this, new PwdDialog.PwdResult() {
            @Override
            public void authPwd(String tag, boolean result) {
                if (TextUtils.equals(tag, "transaction")) {
                    if (result) {
                        pwdRight();
                    } else {
                        ToastUtil.toast(EosTokenTransferActivity.this, getString(R.string.toast_order_password_incorrect));
                    }
                }
            }
        }, mWalletData.whash, "transaction");
        pwdDialog.show();
    }

    private void pwdRight() {
        updateBtnToTranferingState();

        tokenTransfer();

    }

    private void tokenTransfer() {

        signedEosTransaction(mWalletData.wpk, mTokenSymbol, mContractAddress, mWalletData.waddress,
                mEdtWalletAddress.getText().toString(),
                Util.parseDouble(mEdtTransferNum.getText().toString()), mEdtTransferRemark.getText().toString());

    }

    private void signedEosTransaction(String privateKey, String mTokenSymbol, String contactAddress, String senderAddress, String receiverAddress,
                                      double tokencount, String memo) {
        GsonUtil eosSigned = new GsonUtil("{}");
        eosSigned.putString("privateKey", privateKey);
        eosSigned.putString("contactAddress", contactAddress);
        eosSigned.putString("senderAddress", senderAddress);
        eosSigned.putString("receiverAddress", receiverAddress);
        eosSigned.putDouble("tokencount", tokencount);
        eosSigned.putString("symbol", mTokenSymbol);
        eosSigned.putString("memo", memo);
        mWalletUtil.signedTransaction(eosSigned, new WCallback() {
            @Override
            public void onGetWResult(int ret, GsonUtil extra) {
                if (ret == 0) {
                    resetTranferBtn();
                    ToastUtil.toast(EosTokenTransferActivity.this, getString(R.string.toast_transfer_success));

                    EosTokenTransferActivity.this.finish();
                } else {
                    resetTranferBtn();
                    ToastUtil.toast(EosTokenTransferActivity.this, getString(R.string.toast_transfer_failed) + 6);
                }
            }
        });
    }


    private void sendSignedTransaction(String rawTransaction) {
        if (TextUtils.isEmpty(rawTransaction)) {
            resetTranferBtn();
            ToastUtil.toast(EosTokenTransferActivity.this, getString(R.string.toast_transfer_failed) + 3);
            return;
        }
        mWalletUtil.sendSignedTransaction(rawTransaction, new WCallback() {
            @Override
            public void onGetWResult(int ret, GsonUtil extra) {
                if (ret == 0) {
                    resetTranferBtn();
                    ToastUtil.toast(EosTokenTransferActivity.this, getString(R.string.toast_transfer_success));
                    if (mBlockChain == TBController.ETH_INDEX) {
//                        new RequestPresenter().loadData(new TransactionRecordRequest());
                    }

                    EosTokenTransferActivity.this.finish();
                } else {
                    resetTranferBtn();
                    ToastUtil.toast(EosTokenTransferActivity.this, getString(R.string.toast_transfer_failed) + 4);
                }
            }
        });
    }

    private boolean paramCheck() {

        String address = mEdtWalletAddress.getText().toString();
        String num = mEdtTransferNum.getText().toString();

        if (TextUtils.isEmpty(mTvToken.getText().toString())) {
            ViewUtil.showSysAlertDialog(this, getString(R.string.dialog_content_choose_token), "OK");
            return false;
        }
        if (TextUtils.isEmpty(address)) {
            ViewUtil.showSysAlertDialog(this, getString(R.string.dialog_content_no_wallet_account), "OK");
            return false;
        }

        if (TextUtils.equals(address, mWalletData.waddress)) {
            ViewUtil.showSysAlertDialog(this, getString(R.string.dialog_content_receive_address_incorrect), "OK");
            return false;
        }

        if (!mWalletUtil.checkWalletAddress(address)) {
            ViewUtil.showSysAlertDialog(this, getString(R.string.dialog_content_address_format_incorrect), "OK");
            return false;
        }


        if ((TextUtils.isEmpty(num) || Util.parseDouble(num) <= 0.0f)) {
            ViewUtil.showSysAlertDialog(this, getString(R.string.dialog_content_amount_incorrect), "OK");
            return false;
        }
        return true;
    }

    private void updateBtnToTranferingState() {
        mBtnNext.setEnabled(false);
        mBtnNext.setText(getString(R.string.btn_transferring));
    }

    private void resetTranferBtn() {
        mBtnNext.setEnabled(true);
        mBtnNext.setText(getString(R.string.btn_next));
    }

    /**
     * 启动Activity
     *
     * @param context
     */
    public static void startTokenTransferActivity(Context context, String receiveAddress, String contactAddress,
                                                  double num, String tokenSymbol, int decimal) {
        Intent intent = new Intent(context, EosTokenTransferActivity.class);
        intent.putExtra(CONTRACT_ADDRESS_KEY, contactAddress);
        intent.putExtra(RECEIVE_ADDRESS_KEY, receiveAddress);
        intent.putExtra(TOKEN_SYMBOL_KEY, tokenSymbol);
        intent.putExtra(TOKEN_DECIMAL, decimal);
        intent.putExtra(TOEKN_AMOUNT, num);
        context.startActivity(intent);
    }

}
