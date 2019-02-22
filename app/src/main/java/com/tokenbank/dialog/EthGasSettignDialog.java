package com.tokenbank.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.tokenbank.R;
import com.tokenbank.base.TBController;
import com.tokenbank.utils.TLog;
import com.tokenbank.utils.Util;


public class EthGasSettignDialog extends BaseDialog implements View.OnClickListener {
    private final static String TAG = "EthGasSettignDialog";

    public interface OnSettingGasListener {
        void onSettingGas(double gasPrice, double gasInToken);
    }

    private ImageView mImgClose;
    private SeekBar mSeekBarGas;
    private TextView mTvGas;
    private TextView mTvToken;
    private TextView mTvOk;
    private OnSettingGasListener mOnsettingGasListener;
    private double mGas;
    private double mGasInToken;
    private long mBlockChain;
    private double mGasPrice = 8.0f;
    private boolean mDefaultToken;

    public EthGasSettignDialog(@NonNull Context context, OnSettingGasListener onSettingGasListener,  double gasPrice, boolean defaultToken) {
        super(context, R.style.DialogStyle);
        mOnsettingGasListener = onSettingGasListener;
        mBlockChain = TBController.ETH_INDEX;
        mGasPrice = gasPrice;
        mDefaultToken = defaultToken;
        mGas = Util.getRecommendGweiGas(mBlockChain, defaultToken);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_dialog_gas);
        setCanceledOnTouchOutside(true);
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
        if (view == mTvOk) {
            if (mOnsettingGasListener != null) {
                mGasInToken = Util.parseDouble(mTvGas.getText().toString());
                mOnsettingGasListener.onSettingGas(mGasPrice, mGasInToken);
                dismiss();
            }
        } else if (view == mImgClose) {
            dismiss();
        }
    }

    private void initView() {
        mImgClose = (ImageView) findViewById(R.id.img_close);
        mImgClose.setOnClickListener(this);
        mTvGas = (TextView) findViewById(R.id.tv_gascount_intoken);
        double totalGasInWei = Util.fromGweToWei(mBlockChain, mGasPrice) * Util.getRecommendGweiGas(mBlockChain, mDefaultToken);
        mTvGas.setText(Util.formatDoubleToStr(5, Util.fromWei(mBlockChain, totalGasInWei)));
        mSeekBarGas = (SeekBar) findViewById(R.id.seekbar_gas);
        mSeekBarGas.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                TLog.e(TAG, "progress" + progress);
                mGasPrice = 8.0f + progress;
                double totalGasInWei = Util.fromGweToWei(mBlockChain, mGasPrice) * Util.getRecommendGweiGas(mBlockChain, mDefaultToken);
                mTvGas.setText(Util.formatDoubleToStr(5, Util.fromWei(mBlockChain, totalGasInWei)));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        mTvToken = (TextView) findViewById(R.id.tv_token_name);
        mTvToken.setText(Util.getSymbolByBlockChain(mBlockChain));
        mSeekBarGas.setMax(92);
        mSeekBarGas.setProgress(0);
        mTvOk = (TextView) findViewById(R.id.tv_ok);
        mTvOk.setOnClickListener(this);
    }
}
