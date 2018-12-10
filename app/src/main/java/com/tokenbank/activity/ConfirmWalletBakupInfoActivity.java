package com.tokenbank.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tokenbank.R;
import com.tokenbank.base.WalletInfoManager;
import com.tokenbank.utils.ToastUtil;
import com.tokenbank.utils.ViewUtil;
import com.tokenbank.view.FlowLayout;
import com.tokenbank.view.TagAdapter;
import com.tokenbank.view.TagFlowLayout;
import com.tokenbank.view.TitleBar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class ConfirmWalletBakupInfoActivity extends BaseActivity implements View.OnClickListener {


    private static final String WALLET_ADDRESS = "Wallet_Address";
    private static final String BAKUP_TYPE = "Bakup_Type";

    private static final int PK_TYPE = 1;
    private static final int WORDS_TYPE = 2;

    private TitleBar mTitleBar;
    private TextView mTvFinish;
    private TextView mTvBakupTitle;
    private TextView mTvBakupContent;

    //私钥备份view
    private EditText mEdtPk;

    //助记词相关view
    private LinearLayout mLayoutWords;
    private TagFlowLayout mFlowResult;
    private TagFlowLayout mFlowSource;
    private WordAdapter mAdapterResult;
    private WordAdapter mAdapterSource;
    private String[] mWords;

    private WalletInfoManager.WData mWalletData;
    private int mType;

    private List<String> mBaseList = new ArrayList<>();
    private List<String> mResultList = new ArrayList<>();
    private List<String> mResortedList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_walletinfo);
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
        if (mType == WORDS_TYPE) {
            mBaseList = Arrays.asList(mWords);
            //将助记词顺序打乱
            resortWords();
        }

        initView();
    }


    @Override
    public void onClick(View v) {
        if (v == mTvFinish) {
            if (mType == PK_TYPE) {
                if (TextUtils.equals(mEdtPk.getText().toString(), mWalletData.wpk)) {
                    WalletInfoManager.getInstance().updateWalletBaked(mWalletData.waddress, true);
                    gotoMainActivity();
                } else {
                    ToastUtil.toast(ConfirmWalletBakupInfoActivity.this, getString(R.string.toast_private_key_incorrect));
                }
            } else if (mType == WORDS_TYPE) {
                if (verifyWords()) {
                    WalletInfoManager.getInstance().updateWalletWords(mWalletData.waddress, "");
                    WalletInfoManager.getInstance().updateWalletBaked(mWalletData.waddress, true);
                    gotoMainActivity();
                } else {
                    ToastUtil.toast(ConfirmWalletBakupInfoActivity.this, getString(R.string.toast_mnemonic_incorrect))
                    ;
                }
            }
        }
    }

    private void resortWords() {
        Random random = new Random();
        int len = mWords.length;
        while (mResortedList.size() < len) {
            int index = random.nextInt(len);
            if (mResortedList.contains(mWords[index])) {
                continue;
            } else {
                mResortedList.add(mWords[index]);
            }
        }
    }

    private void initView() {
        mTitleBar = findViewById(R.id.title_bar);
        mTitleBar.setTitle(getString(R.string.titleBar_verify));
        mTitleBar.setTitleBarClickListener(new TitleBar.TitleBarListener() {
            @Override
            public void onLeftClick(View view) {
                onBackPressed();
            }
        });

        mTvBakupTitle = findViewById(R.id.tv_bakup_title);
        mTvBakupContent = findViewById(R.id.tv_bakup_content);

        mLayoutWords = findViewById(R.id.layout_words);
        mEdtPk = findViewById(R.id.edt_wallet_pk);


        mTvFinish = findViewById(R.id.tv_finish);
        mTvFinish.setOnClickListener(this);

        mFlowResult = findViewById(R.id.flow_word_result);
        mAdapterResult = new WordAdapter(mResultList);
        mFlowResult.setAdapter(mAdapterResult);
        mFlowResult.setOnTagClickListener(new TagFlowLayout.OnTagClickListener() {
            @Override
            public boolean onTagClick(View view, int position, FlowLayout parent) {
                String item = mResultList.get(position);
                mResultList.remove(item);
                mFlowResult.setAdapter(mAdapterResult);
                mResortedList.add(item);
                mFlowSource.setAdapter(mAdapterSource);
                return true;
            }
        });

        mFlowSource = findViewById(R.id.flow_word_source);
        mFlowSource.setOnTagClickListener(new TagFlowLayout.OnTagClickListener() {
            @Override
            public boolean onTagClick(View view, int position, FlowLayout parent) {
                String item = mResortedList.get(position);
                if (!mResultList.contains(item)) {
                    mResortedList.remove(item);
                    mFlowSource.setAdapter(mAdapterSource);
                    mResultList.add(item);
                    mFlowResult.setAdapter(mAdapterResult);
                }
                return true;
            }
        });
        mAdapterSource = new WordAdapter(mResortedList);
        mFlowSource.setAdapter(mAdapterSource);

        updateUIContent();
    }

    private boolean verifyWords() {
        if (mResultList.size() != mBaseList.size()) {
            return false;
        }
        int len = mBaseList.size();
        for (int i = 0; i < len; i++) {
            if (!TextUtils.equals(mBaseList.get(i), mResultList.get(i))) {
                return false;
            }
        }
        return true;
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

    private void updateUIContent() {
        if (mType == PK_TYPE) {
            mLayoutWords.setVisibility(View.GONE);
            mEdtPk.setVisibility(View.VISIBLE);
            mTvBakupTitle.setText(getString(R.string.title_verify_private_key));
            mTvBakupContent.setText(getString(R.string.content_verify_private_key));
        } else if (mType == WORDS_TYPE) {
            mLayoutWords.setVisibility(View.VISIBLE);
            mEdtPk.setVisibility(View.GONE);
            mTvBakupTitle.setText(getString(R.string.title_verify_mnemonic));
            mTvBakupContent.setText(getString(R.string.content_verify_mnemonic));
        }
    }


    private void gotoMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        this.finish();
    }

    public static void startConfirmWalletBakupInfoActivity(Context from, String walletAddress, int bakupType) {
        Intent intent = new Intent(from, ConfirmWalletBakupInfoActivity.class);
        intent.addFlags(from instanceof BaseActivity ? 0 : Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(WALLET_ADDRESS, walletAddress);
        intent.putExtra(BAKUP_TYPE, bakupType);
        from.startActivity(intent);
    }

    public class WordAdapter extends TagAdapter<String> {
        public WordAdapter(List<String> datas) {
            super(datas);
        }

        @Override
        public View getView(FlowLayout parent, int position, String s) {
            TextView tv = (TextView) ViewUtil.inflatView(parent.getContext(), parent, R.layout.layout_item_word,
                    false);
            tv.setText(s);
            return tv;
        }
    }
}
