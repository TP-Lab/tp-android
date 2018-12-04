package com.tokenbank.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.tokenbank.R;



public class WarnDialog extends BaseDialog {

    private TextView mTvContent;
    private TextView mTvConfirm;
    private String mContent;
    private String mConfirm;
    private OnConfirmClickListener mOnConfirmClickListener;
    private boolean canCancel = false;

    public interface OnConfirmClickListener{
        void onConfirmClick(Dialog dialog, View view);
    }

    public WarnDialog(@NonNull Context context, String content, String confirm, boolean canCancel, OnConfirmClickListener confirmClickListener) {
        super(context, R.style.DialogStyle);
        this.mOnConfirmClickListener = confirmClickListener;
        this.mContent = content;
        this.mConfirm = confirm;
        this.canCancel = canCancel;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCanceledOnTouchOutside(canCancel);
        setContentView(R.layout.layout_dialog_warn);

        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = -2;
        lp.height = -2;
        lp.x = 0;
        lp.y = 0;
        lp.gravity = Gravity.CENTER;
        getWindow().setAttributes(lp);
        initView();

    }

    @Override
    public void onBackPressed() {
        if(canCancel) {
           super.onBackPressed();
        }
    }

    private void initView() {
        mTvConfirm = findViewById(R.id.tv_confirm);
        mTvConfirm.setText(TextUtils.isEmpty(mConfirm) ? "" : mConfirm);
        mTvContent = findViewById(R.id.tv_content);
        mTvContent.setText(TextUtils.isEmpty(mContent) ? "" : mContent);
        mTvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnConfirmClickListener != null) {
                    mOnConfirmClickListener.onConfirmClick(WarnDialog.this, v);
                }
            }
        });
    }

}
