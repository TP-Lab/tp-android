package com.tokenbank.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;


import com.tokenbank.R;
import com.tokenbank.config.AppConfig;

import java.util.ArrayList;

public class PermissionUtil {
    public interface CheckTipCallback2 {
        void onPermissionGranted();

        void onPermissionDenied(String[] var1);

        void onUserOnceDenied(String[] var1);
    }

    public interface CheckTipCallback {
        void onPermissionGranted();

        void onPermissionDenied(String var1);

        void onUserOnceDenied(String var1);
    }

    public interface CheckCallback2 {
        void onPermissionGranted();

        void onPermissionDenied(String[] var1);
    }

    public interface CheckCallback {
        void onPermissionGranted();

        void onPermissionDenied(String var1);
    }

    public static abstract class RequestPermissionsListener implements CheckCallback2 {
        public void onPermissionGranted() {
        }

        public void onPermissionDenied(String[] var1) {
        }

        public abstract void onPermissionRequestStart();

        public abstract void onPermissionRequestEnd();
    }

    private static PermissionUtil sInst;
    private static AlertDialog mAlertDialog;

    private SimpleCallback<CheckCallback2> mCheckCallback;
    private int mRequestCode;

    public static synchronized PermissionUtil getInstance() {
        if (sInst == null) {
            sInst = new PermissionUtil();
        }

        return sInst;
    }

    PermissionUtil() {
    }

    public void onRequestPermissionsResult(Activity activity, int requestCode, final String[] permissions, int[] grantResults) {
        if (requestCode == mRequestCode) {
            if (mCheckCallback != null) {
                boolean granted = true;
                for (int i = 0; i < permissions.length; i++) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        granted = false;
                    }
                }

                if (granted) {
                    mCheckCallback.firEvent(new SimpleCallback.ICallback<CheckCallback2>() {
                        @Override
                        public void onFireEvent(CheckCallback2 listener, Object... objects) {
                            listener.onPermissionGranted();
                        }
                    });
                } else {
                    mCheckCallback.firEvent(new SimpleCallback.ICallback<CheckCallback2>() {
                        @Override
                        public void onFireEvent(CheckCallback2 listener, Object... objects) {
                            listener.onPermissionDenied(permissions);
                        }
                    });
                }

                mCheckCallback.firEvent(new SimpleCallback.ICallback<CheckCallback2>() {
                    @Override
                    public void onFireEvent(CheckCallback2 listener, Object... objects) {
                        if (listener instanceof RequestPermissionsListener) {
                            ((RequestPermissionsListener) listener).onPermissionRequestEnd();
                        }
                    }
                });
            }
        }
    }

    public void requestPermissions(Activity activity, String... permissions) {
        mRequestCode = (int) SystemClock.elapsedRealtime();
        mRequestCode &= 0x0000FFFF;

        if (mCheckCallback != null) {
            mCheckCallback.firEvent(new SimpleCallback.ICallback<CheckCallback2>() {
                @Override
                public void onFireEvent(CheckCallback2 listener, Object... objects) {
                    if (listener instanceof RequestPermissionsListener) {
                        ((RequestPermissionsListener) listener).onPermissionRequestStart();
                    }
                }
            });
        }

        ActivityCompat.requestPermissions(activity, permissions, mRequestCode);
    }

    public void attachPermissionCheckCallback(CheckCallback2 checkCallback) {
        if (mCheckCallback == null) {
            mCheckCallback = new SimpleCallback<>();
        }

        mCheckCallback.addCallback(checkCallback);
    }

    public void detachPermissionCheckCallback(CheckCallback2 checkCallback) {
        if (mCheckCallback != null) {
            mCheckCallback.removeCallback(checkCallback);
        }
    }

    public static void startSelfDetailsSettings(Context context) {
        try {
            context.startActivity(new Intent("android.settings.APPLICATION_DETAILS_SETTINGS",
                    Uri.parse("package:" + context.getPackageName()))
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String[] lackPermissions(Context context, String... permissions) {
        ArrayList<String> ret = new ArrayList<>();
        for (int i = 0; i < permissions.length; ++i) {
            if (lackPermission(context, permissions[i])) {
                ret.add(permissions[i]);
            }
        }

        if (ret.size() == 0) {
            return null;
        } else {
            return ret.toArray(new String[ret.size()]);
        }
    }

    public static boolean lackPermission(Context context, String permission) {
        return ActivityCompat.checkSelfPermission(context, permission) != 0;
    }

    public static void doWithPermissionChecked(Context context, String permission, CheckCallback checkCallback) {
        if (lackPermission(context, permission)) {
            checkCallback.onPermissionDenied(permission);
        } else {
            checkCallback.onPermissionGranted();
        }
    }

    public static void doWithPermissionChecked(Context context, String[] permissions, CheckCallback2 checkCallback) {
        String[] var3 = lackPermissions(context, permissions);
        if (var3 != null) {
            checkCallback.onPermissionDenied(var3);
        } else {
            checkCallback.onPermissionGranted();
        }
    }

    public static void doWithPermissionChecked(Activity activity, String permission, CheckTipCallback checkCallback) {
        if (lackPermission(activity, permission)) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                checkCallback.onUserOnceDenied(permission);
            } else {
                checkCallback.onPermissionDenied(permission);
            }
        } else {
            checkCallback.onPermissionGranted();
        }
    }

    public static void doWithPermissionChecked(Activity activity, String[] permissions, CheckTipCallback2 checkCallback) {
        String[] ret = lackPermissions(activity, permissions);
        if (ret != null) {
            for (int i = 0; i < ret.length; ++i) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(activity, ret[i])) {
                    checkCallback.onUserOnceDenied(ret);
                    return;
                }
            }

            checkCallback.onPermissionDenied(ret);
        } else {
            checkCallback.onPermissionGranted();
        }
    }

    public static void showPermSetDialog(final Activity activity, final boolean needFinish, String... permissions) {
        StringBuilder tips = new StringBuilder();
        tips.append(activity.getString(R.string.str_require_permissions));

        for (int i = 0; i < permissions.length; ++i) {
            tips.append(getPermissionTip(activity, permissions[i]));
            if (i + 1 < permissions.length) {
                tips.append("、");
            }
        }

        ViewUtil.showSysAlertDialog(activity, activity.getString(R.string.str_prompt), tips.toString(), activity.getString(R.string.str_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                activity.finish();
            }
        }, activity.getString(R.string.str_setting), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                activity.finish();
                startSelfDetailsSettings(activity);
            }
        });
    }

    public static String getPermissionTip(Context context, String permission) {
        String var2 = "";
        if (TextUtils.isEmpty(permission)) {
            return var2;
        } else {
            if ("android.permission.CAMERA".equals(permission)) {
                var2 = context.getString(R.string.str_camera);
            }

            if ("android.permission.WRITE_EXTERNAL_STORAGE".equals(permission)) {
                var2 = context.getString(R.string.str_sd_card);
            }

            if (Manifest.permission.READ_PHONE_STATE.equals(permission)) {
                var2 = context.getString(R.string.str_phone_status);
            }

            return var2;
        }
    }

    public static boolean needCheckPermission() {
        return Build.VERSION.SDK_INT >= 23;
    }
}
