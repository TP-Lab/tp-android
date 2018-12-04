
package com.tokenbank.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.WriterException;
import com.tokenbank.R;
import com.tokenbank.base.BaseWalletUtil;
import com.tokenbank.base.TBController;
import com.tokenbank.base.WalletInfoManager;
import com.tokenbank.base.WCallback;
import com.tokenbank.utils.GsonUtil;
import com.tokenbank.utils.QRUtils;
import com.tokenbank.utils.ToastUtil;
import com.tokenbank.utils.Util;
import com.tokenbank.view.TitleBar;



public class TokenReceiveActivity extends BaseActivity {

    public final static String TAG = "TokenTransferActivity";
    private TitleBar mTitleBar;

    private final static String TOKEN = "Token";
    private String mToken;

    private ImageView mImgQr;
    private ImageView mImgQrShadow;
    private EditText mEdtAmount;
    private TextView mTvAddress;
    private TextView mTvTokenName;
    private TextView mTvCopyAddress;
    private BaseWalletUtil mWalletUtil;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.token_receive_activity);
        if (getIntent() != null) {
            mToken = getIntent().getStringExtra(TOKEN);
        }
        if (TextUtils.isEmpty(mToken)) {
            finish();
            return;
        }
        mWalletUtil = TBController.getInstance().getWalletUtil(WalletInfoManager.getInstance().getWalletType());
        if (mWalletUtil == null) {
            this.finish();
            return;
        }
        initView();
        initData();
    }

    private void initView() {
        mTitleBar = findViewById(R.id.title_bar);
        mTitleBar.setLeftDrawable(R.drawable.ic_back);
        mTitleBar.setTitle("收款");
        mTitleBar.setTitleBarClickListener(new TitleBar.TitleBarListener() {
            @Override
            public void onLeftClick(View view) {
                onBackPressed();
            }
        });

        mImgQr = findViewById(R.id.receive_qr);
        mImgQrShadow = findViewById(R.id.img_qrcode_shadow);
        mImgQrShadow.setVisibility(View.GONE);
        mEdtAmount = findViewById(R.id.receive_amount);
        mTvAddress = findViewById(R.id.receive_address);
        mTvTokenName = findViewById(R.id.tv_token_name);
        mTvTokenName.setText(mToken);
        mTvCopyAddress = findViewById(R.id.tv_copy_address);
        mTvCopyAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.clipboard(TokenReceiveActivity.this, "", mTvAddress.getText().toString());
                ToastUtil.toast(TokenReceiveActivity.this, "钱包地址已经复制到剪贴板");
            }
        });
    }

    private void initData() {
        final String address = WalletInfoManager.getInstance().getWAddress();
        mTvAddress.setText(address);
        mEdtAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                double amount = Util.parseDouble(mEdtAmount.getText().toString());
                double tokenAmount = 0.0f;
                if (amount < 0) {
                    ToastUtil.toast(TokenReceiveActivity.this, "请输入正确数目");
                } else {
                    tokenAmount = amount;
                }

                generateAddress(WalletInfoManager.getInstance().getWAddress(),
                        tokenAmount, mToken);
            }
        });
        //刚开始就钱包地址
        generateAddress(WalletInfoManager.getInstance().getWAddress(), 0.0f, mToken);
    }

    private void generateAddress(String walletAddress, double amount, String token) {
        if (TextUtils.isEmpty(walletAddress) || amount < 0.0f || TextUtils.isEmpty(token)) {
            ToastUtil.toast(TokenReceiveActivity.this, "生成收款码错误, 请检查参数");
            mImgQrShadow.setVisibility(View.VISIBLE);
            return;
        }
        mWalletUtil.generateReceiveAddress(walletAddress, amount, token, new WCallback() {
            @Override
            public void onGetWResult(int ret, GsonUtil extra) {
                if (ret == 0) {
                    String receiveAddress = extra.getString("receiveAddress", ""); //不同体系生成的格式不同
                    if (!TextUtils.isEmpty(receiveAddress)) {
                        mImgQrShadow.setVisibility(View.GONE);
                        createQRCode(receiveAddress);
                    }
                } else {
                    ToastUtil.toast(TokenReceiveActivity.this, "生成收款码错误");
                    mImgQrShadow.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void createQRCode(String content) {
        if (TextUtils.isEmpty(content)) {
            mImgQrShadow.setVisibility(View.VISIBLE);
            return;
        }
        try {
            Bitmap bitmap = QRUtils.createQRCode(content, getResources().getDimensionPixelSize(R.dimen.dimen_qr_width));
            mImgQr.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    /**
     * 启动Activity
     *
     * @param context
     */
    public static void startTokenReceiveActivity(Context context, String token) {
        Intent intent = new Intent(context, TokenReceiveActivity.class);
        intent.putExtra(TOKEN, token);
        context.startActivity(intent);
    }
}
