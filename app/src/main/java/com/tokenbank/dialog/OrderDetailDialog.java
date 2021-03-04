package com.tokenbank.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.tokenbank.R;
import com.tokenbank.base.BaseWalletUtil;
import com.tokenbank.base.TBController;
import com.tokenbank.base.WCallback;
import com.tokenbank.utils.GsonUtil;
import com.tokenbank.utils.Util;


public class OrderDetailDialog extends BaseDialog implements View.OnClickListener {
    private final static String TAG = "PKDialog";

    public interface onConfirmOrderListener {
        void onConfirmOrder();
    }

    private onConfirmOrderListener mOnConfirmOrderListener;

    private ImageView mImgClose;
    private TextView mTvReceiverAddress;
    private TextView mTvSenderAddress;
    private TextView mTvGasInToken;
    private TextView mTvGasInfo;
    private TextView mTvTokenCount;
    private TextView mTvTokenName;
    private TextView mTvConfirm;

    private String mSenderAddress;
    private String mReceiverAddress;
    private double mGasPrice;
    private String mTokenName;
    private double mGas;
    private double mTokenCount;
    private long mBlockChain;
    private boolean isDefaultToken;
    private BaseWalletUtil mWalletUtil;


    public OrderDetailDialog(@NonNull Context context, onConfirmOrderListener onConfirmOrderListener, String senderAddress,
                             String receiverAddress, double gasPrice, double gas, double tokencount, long blockChain, String tokenName, boolean defaultToken) {
        super(context, R.style.DialogStyle);
        mOnConfirmOrderListener = onConfirmOrderListener;
        this.mSenderAddress = senderAddress;
        this.mReceiverAddress = receiverAddress;
        this.mGasPrice = gasPrice;
        this.mTokenName = tokenName;
        this.mGas = gas;
        this.mTokenCount = tokencount;
        mBlockChain = blockChain;
        mWalletUtil = TBController.getInstance().getWalletUtil((int) mBlockChain);
        isDefaultToken = defaultToken;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCanceledOnTouchOutside(true);
        setContentView(R.layout.layout_dialog_confirmorder);
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = -1;
        lp.height = -2;
        lp.x = 0;
        lp.y = 0;
        lp.gravity = Gravity.BOTTOM;
        getWindow().setAttributes(lp);
        initView();
    }

    @Override
    public void onClick(View view) {
        if (view == mTvConfirm) {
            if (mOnConfirmOrderListener != null) {
                mOnConfirmOrderListener.onConfirmOrder();
                dismiss();
            }
        } else if (view == mImgClose) {
            dismiss();
        }
    }

    private void initView() {
        mImgClose = findViewById(R.id.img_close);
        mImgClose.setOnClickListener(this);
        mTvReceiverAddress = findViewById(R.id.tv_receiver_address);
        mTvReceiverAddress.setText(mReceiverAddress);
        mTvSenderAddress = findViewById(R.id.tv_sender_address);
        mTvSenderAddress.setText(mSenderAddress);
        mTvGasInToken = findViewById(R.id.tv_gas_intoken);
        mWalletUtil.calculateGasInToken(mGas, mGasPrice, isDefaultToken, new WCallback() {
            @Override
            public void onGetWResult(int ret, GsonUtil extra) {
                mTvGasInToken.setText(extra.getString("gas", ""));

            }
        });
        mTvGasInfo = findViewById(R.id.tv_gas_info);
        mTvGasInfo.setText(generateGasInfoByGas());
        mTvTokenCount = findViewById(R.id.tv_token_count);
        mTvTokenCount.setText(Util.formatDoubleToStr(5, mTokenCount));
        mTvTokenName = findViewById(R.id.tv_token_name);
        mTvTokenName.setText(mTokenName);
        mTvConfirm = findViewById(R.id.tv_confirm);
        mTvConfirm.setOnClickListener(this);
    }

    private String generateGasInfoByGas() {
        if (mBlockChain == TBController.ETH_INDEX || mBlockChain == TBController.MOAC_INDEX) {
            return "≈ " + Util.fromGweToWei(mBlockChain, mGasPrice) + " * " + mGas;

        } else if (mBlockChain == TBController.SWT_INDEX) {
            return "≈ " + mGasPrice + " * " + mGas;
        }
        return "";
    }
}
