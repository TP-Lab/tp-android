package com.tokenbank.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import android.widget.ScrollView;
import android.widget.TextView;

import com.tokenbank.R;
import com.tokenbank.base.WalletInfoManager;
import com.tokenbank.utils.ViewUtil;
import com.tokenbank.view.FlowLayout;
import com.tokenbank.view.TagAdapter;
import com.tokenbank.view.TagFlowLayout;
import com.tokenbank.view.TitleBar;

import java.util.Arrays;
import java.util.List;

public class BWDInfoActivity extends BaseActivity implements View.OnClickListener {

    private static final String WALLET_ADDRESS = "Wallet_Address";
    private static final String BAKUP_TYPE = "Bakup_Type";

    private static final int PK_TYPE = 1;
    private static final int WORDS_TYPE = 2;
    private TitleBar mTitleBar;
    private TextView mTvBakupTitle;
    private TextView mTvBakupContent;
    private ScrollView mScrollViewWords;
    private TextView mTvPk;
    private TextView mTvNext;
    private TagFlowLayout mFlowLayout;
    private WordAdapter mAdapter;
    private WalletInfoManager.WData mWalletData;
    private List<String> mWords;
    private int mType = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bakup_wallet_info);
        if (getIntent() != null) {
            mType = getIntent().getIntExtra(BAKUP_TYPE, -1);
            String walletAddress = getIntent().getStringExtra(WALLET_ADDRESS);
            if (!TextUtils.isEmpty(walletAddress)) {
                mWalletData = WalletInfoManager.getInstance().getWData(walletAddress);
            }
        }
        verifyData();
        initView();
    }

    @Override
    public void onClick(View v) {
        if (v == mTvNext) {
            ConfirmWalletBakupInfoActivity.startConfirmWalletBakupInfoActivity(BWDInfoActivity.this,
                    mWalletData.waddress, mType);
            this.finish();
        }
    }

    private void initView() {
        mTitleBar = findViewById(R.id.title_bar);
        mTitleBar.setTitleTextColor(R.color.white);
        mTitleBar.setTitle(getString(R.string.titleBar_backup_wallet));
        mTvBakupTitle = findViewById(R.id.tv_bakup_title);
        mTvBakupContent = findViewById(R.id.tv_bakup_content);

        mScrollViewWords = findViewById(R.id.scrollview_words);
        mTvPk = findViewById(R.id.tv_wallet_pk);

        mTvNext = findViewById(R.id.tv_next);
        mTvNext.setOnClickListener(this);

        mFlowLayout = findViewById(R.id.flow_word);
        mAdapter = new WordAdapter(mWords);
        mFlowLayout.setAdapter(mAdapter);

        updateUIContent();
    }

    public static void startBakupWalletInfoActivity(Context from, String walletAddress, int bakupType) {
        Intent intent = new Intent(from, BWDInfoActivity.class);
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
            String[] words = mWalletData.words.split(" ");
            if (words == null || words.length < 12) {
                return false;
            }
            mWords = Arrays.asList(words);
        }

        return true;
    }

    private void updateUIContent() {
        if (mType == PK_TYPE) {
            mScrollViewWords.setVisibility(View.GONE);
            mTvPk.setVisibility(View.VISIBLE);
            mTvBakupTitle.setText(getString(R.string.title_backup_private_key));
            mTvBakupContent.setText(getString(R.string.content_backup_private_kye));
            mTvPk.setText(mWalletData.wpk);
        } else if (mType == WORDS_TYPE) {
            mScrollViewWords.setVisibility(View.VISIBLE);
            mTvPk.setVisibility(View.GONE);
            mTvBakupTitle.setText(getString(R.string.title_backup_mnemonic));
            mTvBakupContent.setText(getString(R.string.content_backup_mnemonic));
        }
    }

    public class WordAdapter extends TagAdapter<String> {
        public WordAdapter(List<String> datas) {
            super(datas);
        }

        @Override
        public View getView(FlowLayout parent, int position, String s) {
            TextView tv = (TextView) ViewUtil.inflatView(parent.getContext(), parent,
                    R.layout.layout_item_word, false);
            tv.setText(s);
            return tv;
        }
    }
}
