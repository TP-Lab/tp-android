package com.tokenbank.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.WriterException;
import com.tokenbank.R;
import com.tokenbank.base.BaseWalletUtil;
import com.tokenbank.base.WalletInfoManager;
import com.tokenbank.base.WCallback;
import com.tokenbank.base.TBController;
import com.tokenbank.utils.GsonUtil;
import com.tokenbank.utils.QRUtils;
import com.tokenbank.utils.ToastUtil;
import com.tokenbank.utils.Util;
import com.tokenbank.view.TitleBar;


public class TransactionDetailsActivity extends BaseActivity implements View.OnClickListener {

    private TitleBar mTitleBar;
    private TextView mTvTransactionStatus;
    private TextView mTvCount;
    private TextView mTvSymbol;
    private TextView mTvSender;
    private TextView mTvReceiver;
    private TextView mTvGas;
    private TextView mTvInfo;
    private TextView mTvTransactionId;
    private TextView mTvBlockId;
    private TextView mTvTransactionTime;
    private TextView mTvCopyUrl;

    private ImageView mImgTransactionQrCode;
    private String mHash;
    private GsonUtil transactionData;
    private BaseWalletUtil mWalletUtil;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_details);
        if (getIntent() != null) {
            String data = getIntent().getStringExtra("ITEM");
            transactionData = new GsonUtil(data);
        }
        mHash = transactionData.getString("hash", "");
        if (TextUtils.isEmpty(mHash)) {
            ToastUtil.toast(TransactionDetailsActivity.this, "参数非法");
            this.finish();
            return;
        }

        mWalletUtil = TBController.getInstance().getWalletUtil(WalletInfoManager.getInstance().getWalletType());
        if (mWalletUtil == null) {
            this.finish();
            return;
        }

        initView();
    }

    private void initView() {

        mTitleBar = findViewById(R.id.title_bar);
        mTitleBar.setLeftDrawable(R.drawable.ic_back);
        mTitleBar.setTitle("交易详情");
        mTitleBar.setTitleBarClickListener(new TitleBar.TitleBarListener() {
            @Override
            public void onLeftClick(View view) {
                onBackPressed();
            }
        });

        mTvTransactionStatus = findViewById(R.id.tv_transaction_status);
        mTvCount = findViewById(R.id.tv_transaction_count);
        mTvSymbol = findViewById(R.id.tv_symbol);
        mTvSender = findViewById(R.id.tv_send_address);
        mTvSender.setOnClickListener(this);

        mTvReceiver = findViewById(R.id.tv_receive_address);
        mTvReceiver.setOnClickListener(this);

        mTvGas = findViewById(R.id.tv_gas);
        mTvInfo = findViewById(R.id.tv_info);
        mTvTransactionId = findViewById(R.id.tv_transaction_id);
        mTvTransactionId.setOnClickListener(this);
        mTvBlockId = findViewById(R.id.tv_block);
        mTvTransactionTime = findViewById(R.id.tv_transaction_time);
        mTvCopyUrl = findViewById(R.id.tv_copy_transaction_url);
        mTvCopyUrl.setOnClickListener(this);
        mImgTransactionQrCode = findViewById(R.id.img_transaction_qrcode);

        int type = WalletInfoManager.getInstance().getWalletType();
        if (type == TBController.SWT_INDEX) {
            loadData();
        }
    }

    private void updateData(GsonUtil transactionInfo) {
        double value = transactionInfo.getDouble("real_value", 0.0f);
        String toAddress = transactionInfo.getString("to", "");
        mTvSender.setText(transactionInfo.getString("from", ""));
        mTvReceiver.setText(toAddress);
        mTvGas.setText(transactionInfo.getString("fee", "0.0"));
        mTvInfo.setText(transactionInfo.getString("input", ""));
        mTvTransactionId.setText(transactionInfo.getString("hash", ""));
        mTvBlockId.setText(transactionInfo.getString("blockNumber", ""));
        mTvTransactionTime.setText(Util.formatTime(transactionInfo.getLong("timeStamp", 0l)));
        int status = transactionInfo.getInt("txreceipt_status", 5);
        if (status == 1) {
            //success
            mTvTransactionStatus.setText("交易成功");
        } else if (status == 2) {
            //pending
            mTvTransactionStatus.setText("交易打包中");
        } else if (status == 0) {
            //fail
            mTvTransactionStatus.setText("交易失败");
        } else {
            mTvTransactionStatus.setText("状态未知，请点击交易号查询");
        }
        mTvCount.setText(value + "");
        mTvSymbol.setText(transactionInfo.getString("tokenSymbol", ""));
        createQRCode(mWalletUtil.getTransactionSearchUrl(mTvTransactionId.getText().toString()));
    }

    private void loadData() {
        mWalletUtil.queryTransactionDetails(mHash, new WCallback() {
            @Override
            public void onGetWResult(int ret, GsonUtil extra) {
                if (ret == 0) {
                    updateData(extra.getObject("data", "{}"));
                } else {
                    ToastUtil.toast(TransactionDetailsActivity.this, "获取交易信息失败，请稍后重试");
                    TransactionDetailsActivity.this.finish();
                }
            }
        });
    }

    public static void startTransactionDetailActivity(Context context, GsonUtil data) {
        Intent intent = new Intent(context, TransactionDetailsActivity.class);
        intent.putExtra("ITEM", data.toString());
        intent.addFlags(context instanceof BaseActivity ? 0 : Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    private void createQRCode(String transactionUrl) {
        try {
            Bitmap bitmap = QRUtils.createQRCode(transactionUrl, getResources().getDimensionPixelSize(R.dimen.dimen_qr_width));
            mImgTransactionQrCode.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        if (v == mTvCopyUrl) {
            Util.clipboard(TransactionDetailsActivity.this, "",
                    mWalletUtil.getTransactionSearchUrl(mTvTransactionId.getText().toString()));
            ToastUtil.toast(TransactionDetailsActivity.this, "查询url已经复制到剪贴板");
        } else if (v == mTvSender) {
            Util.clipboard(TransactionDetailsActivity.this, "", mTvSender.getText().toString());
            ToastUtil.toast(TransactionDetailsActivity.this, "发送方地址已经复制到剪贴板");
        } else if (v == mTvReceiver) {
            Util.clipboard(TransactionDetailsActivity.this, "", mTvReceiver.getText().toString());
            ToastUtil.toast(TransactionDetailsActivity.this, "收款地址已经复制到剪贴板");
        } else if (v == mTvTransactionId) {
            WebBrowserActivity.startWebBrowserActivity(TransactionDetailsActivity.this, "交易查询",
                    mWalletUtil.getTransactionSearchUrl(mTvTransactionId.getText().toString()));
        }
    }
}
