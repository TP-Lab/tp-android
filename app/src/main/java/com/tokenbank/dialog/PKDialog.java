package com.tokenbank.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.tokenbank.R;
import com.tokenbank.utils.ToastUtil;
import com.tokenbank.utils.Util;


public class PKDialog extends BaseDialog implements View.OnClickListener {

    private final static String TAG = "PKDialog";

    private TextView mTvCopyPrivateKey;
    private TextView mTvPrivateKey;

    private String mPrivateKey;

    public PKDialog(@NonNull Context context, String privateKey) {
        super(context, R.style.DialogStyle);
        this.mPrivateKey = privateKey;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_dialog_exportprivatekey);
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
    public void onClick(View view) {
        if (view == mTvCopyPrivateKey) {
            copyPrivateKey();
            dismiss();
        }
    }

    private void initView() {
        mTvPrivateKey = (TextView) findViewById(R.id.tv_privatekey);
        mTvPrivateKey.setText(mPrivateKey);

        mTvCopyPrivateKey = (TextView) findViewById(R.id.tv_copy_privatekey);
        mTvCopyPrivateKey.setOnClickListener(this);

    }

    private void copyPrivateKey() {
        Util.clipboard(getContext(), "", mTvPrivateKey.getText().toString());
        ToastUtil.toast(getContext(), getContext().getString(R.string.str_private_key_copy));
    }
}
