package com.tokenbank.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.tokenbank.R;
import com.tokenbank.base.WalletInfoManager;
import com.tokenbank.view.TitleBar;


public class StartBakupActivity extends BaseActivity implements View.OnClickListener {

    private static final String WALLET_ADDRESS = "Wallet_Address";
    private static final String BAKUP_TYPE = "Bakup_Type";

    private static final int PK_TYPE = 1;
    private static final int WORDS_TYPE = 2;

    private TitleBar mTitleBar;
    private TextView mTvBakupTitle;
    private TextView mTvBakupContent;
    private TextView mTvStartBakWallet;


    private WalletInfoManager.WData mWalletData;
    private String[] mWords;
    private int mType = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bakup_wallet_start);
        if (getIntent() != null) {
            mType = getIntent().getIntExtra(BAKUP_TYPE, -1);
            String walletAddress = getIntent().getStringExtra(WALLET_ADDRESS);
            if (!TextUtils.isEmpty(walletAddress)) {
                mWalletData = WalletInfoManager.getInstance().getWData(walletAddress);
            }
        }
        if (!verifyData()) {
            this.finish();
            return;
        }
        initView();
    }

    @Override
    public void onClick(View v) {
        if (v == mTvStartBakWallet) {
            BWDInfoActivity.startBakupWalletInfoActivity(StartBakupActivity.this,
                    mWalletData.waddress, mType);
            this.finish();
        }
    }

    public static void startBakupWalletStartActivity(Context from, String walletAddress, int bakupType) {
        Intent intent = new Intent(from, StartBakupActivity.class);
        intent.addFlags(from instanceof BaseActivity ? 0 : Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(WALLET_ADDRESS, walletAddress);
        intent.putExtra(BAKUP_TYPE, bakupType);
        from.startActivity(intent);
    }

    private boolean verifyData() {
        if (mWalletData == null) {
            return false;
        }
        if (mType != PK_TYPE && mType != WORDS_TYPE) {
            return false;
        }
        if (mType == WORDS_TYPE) {
            if (TextUtils.isEmpty(mWalletData.words)) {
                return false;
            }
            mWords = mWalletData.words.split(" ");
            if (mWords == null || mWords.length < 12) {
                return false;
            }
        }

        return true;
    }

    private void initView() {
        mTitleBar = findViewById(R.id.title_bar);
        mTitleBar.setTitle(getString(R.string.title_backup_wallet));

        mTvBakupTitle = findViewById(R.id.tv_bakup_title);
        mTvBakupContent = findViewById(R.id.tv_bakup_content);
        mTvStartBakWallet = findViewById(R.id.tv_start_bakwallet);
        mTvStartBakWallet.setOnClickListener(this);

        updateUIContent();
    }

    private void updateUIContent() {
        if (mType == PK_TYPE) {
            mTvBakupTitle.setText(getString(R.string.title_backup_private_key));
            mTvBakupContent.setText(getString(R.string.str_backup_content));
        } else if (mType == WORDS_TYPE) {
            mTvBakupTitle.setText(getString(R.string.title_backup_mnemonic));
            mTvBakupContent.setText(getString(R.string.str_backup_mnemonic_content));
        }
    }
}
