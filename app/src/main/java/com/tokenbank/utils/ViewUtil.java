package com.tokenbank.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tokenbank.activity.StartBakupActivity;
import com.tokenbank.base.WalletInfoManager;
import com.tokenbank.dialog.PwdDialog;
import com.tokenbank.dialog.WarnDialog;


public class ViewUtil {

    public static View inflatView(Context context, ViewGroup parent, int id, boolean attach) {
        return LayoutInflater.from(context).inflate(id, parent, attach);
    }

    public static View inflatView(LayoutInflater inflater, ViewGroup parent, int id, boolean attach) {
        return inflater.inflate(id, parent, attach);
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }


    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static void showSysAlertDialog(Context context, String title, String btnTxt) {
        new AlertDialog.Builder(context).setTitle(title).setNegativeButton(btnTxt, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).show();
    }

    public static void showSysAlertDialog(Context context, String title, String message, String cancelTxt, DialogInterface.OnClickListener negListener, String positiveTxt, DialogInterface.OnClickListener listener) {
        new AlertDialog.Builder(context).setTitle(title).setMessage(message).setNegativeButton(cancelTxt, negListener).setPositiveButton(positiveTxt, listener).show();
    }

    public static void showBakupDialog(final Context context, final WalletInfoManager.WData walletData, boolean canCancel, final boolean needVerifyPwd, final String pwdHash) {
        final WarnDialog warnDialog = new WarnDialog(context, "为了您的钱包安全，请备份钱包", "立即备份", canCancel,
                new WarnDialog.OnConfirmClickListener() {
                    @Override
                    public void onConfirmClick(final Dialog dialog, View view) {

                        if (needVerifyPwd) {
                            PwdDialog pwdDialog = new PwdDialog(context, new PwdDialog.PwdResult() {
                                @Override
                                public void authPwd(String tag, boolean result) {
                                    if (result) {
                                        if (TextUtils.isEmpty(walletData.words)) {
                                            StartBakupActivity.startBakupWalletStartActivity(context, walletData.waddress,
                                                    1);
                                        } else {
                                            StartBakupActivity.startBakupWalletStartActivity(context, walletData.waddress,
                                                    2);
                                        }
                                        dialog.dismiss();
                                    } else {
                                        ToastUtil.toast(context, "密码错误");
                                    }
                                }
                            }, pwdHash, "");
                            pwdDialog.show();
                        } else {
                            if (TextUtils.isEmpty(walletData.words)) {
                                StartBakupActivity.startBakupWalletStartActivity(context, walletData.waddress,
                                        1);
                            } else {
                                StartBakupActivity.startBakupWalletStartActivity(context, walletData.waddress,
                                        2);
                            }
                            dialog.dismiss();
                        }
                    }
                });
        warnDialog.show();
    }

    public static void showBakupDialog(final Context context, final WalletInfoManager.WData walletData, boolean canCancel) {
        showBakupDialog(context, walletData, canCancel, false, "");
    }


}
