package com.tokenbank.dialog;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.tokenbank.R;
import com.tokenbank.utils.FileUtil;
import com.tokenbank.utils.TLog;



public class PwdDialog extends Dialog implements View.OnClickListener {

    private final static String TAG = "PwdDialog";
    private EditText mEdtPw;
    private TextView mTvCancel;
    private TextView mTvOk;
    private String mPwdContent;
    private String mTag;

    private PwdResult mPwdResult;


    public interface PwdResult {
        void authPwd(String tag, boolean result);
    }

    public PwdDialog(@NonNull Context context, PwdResult authPwdListener, String pwdHash, String tag) {
        super(context, R.style.DialogStyle);
        this.mPwdResult = authPwdListener;
        this.mPwdContent = pwdHash;
        this.mTag = tag;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_dialog_authpwd);
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = -2;
        lp.height = -2;
        lp.x = 0;
        lp.y = 0;
        lp.gravity = Gravity.CENTER;
        getWindow().setAttributes(lp);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN | WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        initView();
    }

    @Override
    public void onClick(View view) {
        if (view == mTvCancel) {
            dismiss();
        } else if (view == mTvOk) {
            if (mPwdResult == null) {
                TLog.e(TAG, "回掉接口空");
                dismiss();
                return;
            }
            if (TextUtils.isEmpty(mEdtPw.getText().toString())) {
                mPwdResult.authPwd(mTag, false);
            } else {
                if (TextUtils.equals(mPwdContent, FileUtil.getStringContent(mEdtPw.getText().toString()))) {
                    mPwdResult.authPwd(mTag, true);
                } else {
                    mPwdResult.authPwd(mTag,false);
                }
            }

            dismiss();
        }
    }

    private void initView() {
        mTvCancel = (TextView) findViewById(R.id.tv_cancel);
        mTvCancel.setOnClickListener(this);
        mTvOk = (TextView) findViewById(R.id.tv_ok);
        mTvOk.setOnClickListener(this);
        mEdtPw = (EditText) findViewById(R.id.edt_dialog_pwd);
    }

}
