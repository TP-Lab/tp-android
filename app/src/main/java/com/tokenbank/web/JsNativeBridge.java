package com.tokenbank.web;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.JavascriptInterface;

import com.just.agentweb.AgentWeb;
import com.tokenbank.R;
import com.tokenbank.activity.ImportWalletActivity;
import com.tokenbank.base.BaseWalletUtil;
import com.tokenbank.base.BlockChainData;
import com.tokenbank.base.TBController;
import com.tokenbank.base.WCallback;
import com.tokenbank.base.WalletInfoManager;
import com.tokenbank.config.AppConfig;
import com.tokenbank.dialog.PwdDialog;
import com.tokenbank.utils.DeviceUtil;
import com.tokenbank.utils.FileUtil;
import com.tokenbank.utils.GsonUtil;
import com.tokenbank.utils.ToastUtil;
import com.zxing.activity.CaptureActivity;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import cn.sharesdk.onekeyshare.OnekeyShare;

import static com.tokenbank.activity.CreateWalletActivity.TAG;


/**
 * JS调用原生接口类
 */
public class JsNativeBridge {

    private final static String MSG_SUCCESS = "success";
    private final static String MSG_FAILED = "failed";
    private final static long FIFTEEN = 15 * 60 * 1000L;

    private AgentWeb mAgentWeb;
    private Context mContext;
    private WalletInfoManager mWalletManager;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private String version,name,address;
    private String mFrom, mTo, mValue, mToken, mIssuer, mGas, mMemo,mGasPrice;
    private IWebCallBack mWebCallBack;
    private BaseWalletUtil mWalletUtil;
    private WalletInfoManager.WData mCurrentWallet; //当前使用哪个钱包转账
    private BlockChainData.Block block;
    public JsNativeBridge(AgentWeb agent, Context context, IWebCallBack callback) {
        this.mAgentWeb = agent;
        this.mContext = context;
        this.mWebCallBack = callback;
        this.mWalletManager = WalletInfoManager.getInstance();
        this.mWalletUtil = TBController.getInstance().getWalletUtil(WalletInfoManager.getInstance().getWalletType());
    }

    @JavascriptInterface
    public void callMessage(String methodName, String params, final String callbackId) {
        mCurrentWallet = WalletInfoManager.getInstance().getCurrentWallet();
        block = BlockChainData.getInstance().getBolckByHid(WalletInfoManager.getInstance().getWalletType());
        GsonUtil data = new GsonUtil("{}");
        Log.d(TAG, "callMessage: "+methodName + " be called");
        switch (methodName) {
            case "getAppInfo":
                PackageManager packageManager = mContext.getPackageManager();
                PackageInfo packageInfo = null;

                try {
                    packageInfo = packageManager.getPackageInfo(mContext.getPackageName(), 0);
                    if (packageInfo != null) {
                        version = packageInfo.versionName;
                        name = mContext.getResources().getString(packageInfo.applicationInfo.labelRes);
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }

                data.putString("name", name);
                data.putString("system", "android");
                data.putString("version", version);
                data.putString("sys_version", Build.VERSION.SDK_INT + "");
                notifySuccessResult(data,callbackId);

                break;

            case "getWallets":
                List<WalletInfoManager.WData> wallets = mWalletManager.getAllWallet();

                for (int i = 0; i < wallets.size(); i++) {
                    GsonUtil wallet = new GsonUtil("{}");
                    address = wallets.get(i).waddress;
                    name = wallets.get(i).wname;
                    wallet.putString("name", name);
                    wallet.putString("address", address);
                    data.put(wallet);
                }

                notifySuccessResult(data,callbackId);

                break;

            case "getDeviceId":
                String deviceId = DeviceUtil.generateDeviceUniqueId();
                data.putString("device_id", deviceId);
                this.mAgentWeb.getJsAccessEntrace().callJs("javascript:" + callbackId + "('" + data.toString() + "')");
                break;

            case "shareNewsToSNS":
                GsonUtil tx = new GsonUtil(params);
                String mTitle = tx.getString("title", "");
                String mUrl = tx.getString("url", "").toUpperCase();
                String mText = tx.getString("text", "");
                String mImgUrl = tx.getString("imgUrl", "");
                OnekeyShare oks = new OnekeyShare();
                // title标题，微信、QQ和QQ空间等平台使用
                oks.setTitle(mTitle);
                // titleUrl QQ和QQ空间跳转链接
                oks.setTitleUrl(mUrl);
                // text是分享文本，所有平台都需要这个字段
                oks.setText(mText);
                // imagePath是图片的本地路径，确保SDcard下面存在此张图片
//                oks.setImagePath(mImgUrl);
                oks.setImageUrl(mImgUrl);
                // url在微信、Facebook等平台中使用
                oks.setUrl(mUrl);
                // 启动分享GUI
                oks.show(mContext);
                break;

            case "invokeQRScanner":
                CaptureActivity.startCaptureActivity(mContext, callbackId);
                break;

            case "getCurrentWallet":
                String walletName = mCurrentWallet.wname;
                data.putString("address", mCurrentWallet.waddress);
                data.putString("name",walletName);
                data.putString("blockchain","jingtum");

                notifySuccessResult(data,callbackId);
                break;

            case "sign":
                final GsonUtil SignParam = new GsonUtil(params);
                if(!mCurrentWallet.waddress.toLowerCase().equals(SignParam.getString("address","").toLowerCase())){
                    notifyFailedResult("非当前钱包",callbackId);
                    return;
                }
                SignParam.putString("secret",mCurrentWallet.wpk);
                AppConfig.postOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                    new PwdDialog(mContext, new PwdDialog.PwdResult() {
                        @Override
                        public void authPwd(String tag, boolean flag) {
                        if (TextUtils.equals(tag, "transaction")) {
                            if (flag) {
                                //执行
                                mWalletUtil.signedTransaction(SignParam, new WCallback() {
                                    @Override
                                    public void onGetWResult(int ret, GsonUtil extra) {
                                        String signature = extra.getString("signature","");
                                        if(signature.equals("")){
                                            notifyFailedResult(extra.getString("err",""),callbackId);
                                        } else {
                                            notifySuccessResult(signature,callbackId);
                                        }
                                    }
                                });
                            } else {
                                ToastUtil.toast(AppConfig.getContext(), AppConfig.getContext().getString(R.string.toast_order_password_incorrect));
                            }
                        }
                        }
                    }, mCurrentWallet.whash, "transaction").show();
                    }
                });

                break;
            case "moacTokenTransfer":
                /*
                    from: '0xaaaaaaa',
                    to: '0xaaaaaab',
                    amount: '100',
                    gasLimit: 60000,
                    tokenName: 'MOAC',
                    decimal: 18,
                    contract: ''
                 */
                final GsonUtil MoacTx = new GsonUtil(params);
                if(!mCurrentWallet.waddress.toLowerCase().equals(MoacTx.getString("address","").toLowerCase())){
                    notifyFailedResult("非当前钱包",callbackId);
                    return;
                }
                MoacTx.putString("secret",mCurrentWallet.wpk);
                AppConfig.postOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        new PwdDialog(mContext, new PwdDialog.PwdResult() {
                            @Override
                            public void authPwd(String tag, boolean flag) {
                                if (TextUtils.equals(tag, "transaction")) {
                                    if (flag) {
                                        //执行
                                        mWalletUtil.signedTransaction(MoacTx, new WCallback() {
                                            @Override
                                            public void onGetWResult(int ret, GsonUtil extra) {
                                                String hash = extra.getString("transactionId","");
                                                if(hash.equals("")){
                                                    notifyFailedResult(extra.getString("err",""),callbackId);
                                                } else {
                                                    notifySuccessResult(hash,callbackId);
                                                }
                                            }
                                        });
                                    } else {
                                        ToastUtil.toast(AppConfig.getContext(), AppConfig.getContext().getString(R.string.toast_order_password_incorrect));
                                    }
                                }
                            }
                        }, mCurrentWallet.whash, "transaction").show();
                    }
                });
                break;
            case "back":
                if (mWebCallBack != null) {
                    mWebCallBack.onBack();
                }
                break;

            case "close":
                if (mWebCallBack != null) {
                    mWebCallBack.onClose();
                }
                break;

            case "fullScreen":
                if (mWebCallBack != null) {
                    mWebCallBack.switchFullScreen(params);
                }
                break;

            case "importWallet":
                ImportWalletActivity.startImportWalletActivity(mContext,0);
                break;

            case "setMenubar":
                //1 - open, 0 - close(default)
                if (mWebCallBack != null) {
                    final GsonUtil Show_flag = new GsonUtil(params);
                    switch (Show_flag.getInt("",0)){
                        case 1 :  mWebCallBack.setMenubar(true); break;
                        case 0 :  mWebCallBack.setMenubar(false); break;
                    }
                }
                break;
            case "signJingtumTransaction":
                //params = params.replace("TakerPays","Amount");
                //params = params.replace("Platform","Destination");
                final GsonUtil trans = new GsonUtil(params);
                if(!mCurrentWallet.waddress.toLowerCase().equals(trans.getString("Account","").toLowerCase())){
                    notifyFailedResult("非当前钱包",callbackId);
                    return;
                }
                trans.putInt("Flags", 0);
                GsonUtil SwtcTx = new GsonUtil("{}");
                SwtcTx.put("transaction", trans);
                SwtcTx.putString("secret", mCurrentWallet.wpk);
                AppConfig.postOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        new PwdDialog(mContext, new PwdDialog.PwdResult() {
                            @Override
                            public void authPwd(String tag, boolean flag) {
                                if (TextUtils.equals(tag, "transaction")) {
                                    if (flag) {
                                        Log.d(TAG, "authPwd: do it ++++");
                                        mWalletUtil.signedTransaction(SwtcTx, new WCallback() {
                                            @Override
                                            public void onGetWResult(int ret, GsonUtil extra) {
                                                if( ret == 0 ){
                                                    String signature = extra.getString("signature","");
                                                    Log.d(TAG, "onGetWResult: 签名 "+signature);
                                                    notifySuccessResult(signature,callbackId);
                                                } else {
                                                    Log.d(TAG, "onGetWResult: 签名错误!!!!!!");
                                                    notifyFailedResult("sign failed",callbackId);
                                                }
                                            }
                                        });
                                    } else {
                                        ToastUtil.toast(AppConfig.getContext(), AppConfig.getContext().getString(R.string.toast_order_password_incorrect));
                                    }
                                }
                            }
                        }, mCurrentWallet.whash, "transaction").show();
                    }
                });
                break;
            case "saveImage":
                Log.d(TAG, "callHandler: 开始保存图片 url = "+params);
                final GsonUtil picUrl = new GsonUtil(params);
                if(!picUrl.equals("")){
                    try {
                        //通过url获取图片
                        URL iconUrl=new URL(picUrl.getString("url",""));
                        URLConnection connection=iconUrl.openConnection();
                        HttpURLConnection httpURLConnection= (HttpURLConnection) connection;
                        int length = httpURLConnection.getContentLength();
                        connection.connect();
                        InputStream inputStream=connection.getInputStream();
                        BufferedInputStream bufferedInputStream=new BufferedInputStream(inputStream,length);
                        Bitmap mBitmap= BitmapFactory.decodeStream(bufferedInputStream);
                        bufferedInputStream.close();
                        inputStream.close();
                        //保存图片
                        FileUtil.saveBitmap(AppConfig.getContext(),mBitmap);
                        AppConfig.postOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtil.toast(AppConfig.getContext(), AppConfig.getContext().getString(R.string.picture_save_success)+"相册");
                            }
                        });
                    } catch (Exception e) {
                        Log.d(TAG, "callHandler: 保存失败");
                        AppConfig.postOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtil.toast(AppConfig.getContext(), AppConfig.getContext().getString(R.string.picture_save_false));
                            }
                        });
                        e.printStackTrace();
                    }
                }
                break;

            case "rollHorizontal":
                //横屏
                if (mWebCallBack != null) {
                    mWebCallBack.rollHorizontal();
                }
                break;

            case "popGestureRecognizerEnable":
                //苹果接口 安卓无效
                break;

            case "forwardNavigationGesturesEnable":

                break;
            case "signMoacTransaction":
                /*
                    tp 接口参数：

                    from: '0xaaaaaaa',
                    to: '0xaaaaaab',
                    gasPrice: 100000000,
                    gasLimit: 60000,
                    data: '0xaawefwefwefwefwefef',
                    value: 1000000000,
                    chainId: 99,
                    via: '',
                    shardingFlag: 0,
                 */
                final GsonUtil TX = new GsonUtil(params);
                if(!mCurrentWallet.waddress.toLowerCase().equals(TX.getString("from","").toLowerCase())){
                    notifyFailedResult("非当前钱包",callbackId);
                    return;
                }
                TX.putString("gas",TX.getString("gasPrice",""));
                TX.putString("privateKey", mCurrentWallet.wpk);
                TX.putString("senderAddress", TX.getString("from",""));
                TX.putString("receiverAddress", TX.getString("to",""));
                TX.putDouble("tokencount", TX.getDouble("value",0.0f));
                TX.putDouble("gas", TX.getDouble("gasLimit",0.0f));
                TX.putDouble("gasPrice", TX.getDouble("gasPrice",0.0f));
                if(!mCurrentWallet.waddress.toLowerCase().equals(TX.getString("from",""))){
                    notifyFailedResult("非当前钱包",callbackId);
                    return;
                }

                AppConfig.postOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        new PwdDialog(mContext, new PwdDialog.PwdResult() {
                            @Override
                            public void authPwd(String tag, boolean flag) {
                                if (TextUtils.equals(tag, "transaction")) {
                                    if (flag) {
                                        mWalletUtil.signedTransaction(TX, new WCallback() {
                                            @Override
                                            public void onGetWResult(int ret, GsonUtil extra) {
                                                String signature = extra.getString("signature","");
                                                if(signature.equals("")){
                                                    notifyFailedResult(extra.getString("err",""),callbackId);
                                                } else {
                                                    notifySuccessResult(signature,callbackId);
                                                }
                                            }
                                        });
                                    } else {
                                        ToastUtil.toast(AppConfig.getContext(), AppConfig.getContext().getString(R.string.toast_order_password_incorrect));
                                    }
                                }
                            }
                        }, mCurrentWallet.whash, "transaction").show();
                    }
                });
                break;

            case "getNodeUrl":
                String node = "";
                data.putString("blockchain","");
                data.putString("nodeUrl",node);
                notifySuccessResult(data,callbackId);
                break;

            case "sendMoacTransaction":
                /*
                    from: '0xaaaaaaa',
                    to: '0xaaaaaab',
                    gasPrice: 100000000,
                    gasLimit: 60000,
                    data: '0xaawefwefwefwefwefef',
                    value: 1000000000,
                    chainId: 99,
                    via: '',
                    shardingFlag: 0,
                 */
                final GsonUtil TransactionParam = new GsonUtil(params);
                if(!mCurrentWallet.waddress.toLowerCase().equals(TransactionParam.getString("from","").toLowerCase())){
                    notifyFailedResult("非当前钱包",callbackId);
                    return;
                }
                TransactionParam.putString("secret",mCurrentWallet.wpk);
                AppConfig.postOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        new PwdDialog(mContext, new PwdDialog.PwdResult() {
                            @Override
                            public void authPwd(String tag, boolean flag) {
                                if (TextUtils.equals(tag, "transaction")) {
                                    if (flag) {
                                        mWalletUtil.signedTransaction(TransactionParam, new WCallback() {
                                            @Override
                                            public void onGetWResult(int ret, GsonUtil extra) {
                                               if(ret == 0){
                                                   mWalletUtil.sendSignedTransaction(extra.getString("r", ""), new WCallback() {
                                                       @Override
                                                       public void onGetWResult(int ret, GsonUtil extra) {
                                                           String hash = extra.getString("hash","");
                                                           if(hash.equals("")){
                                                               notifyFailedResult(extra.getString("err",""),callbackId);
                                                           } else {
                                                               notifySuccessResult(hash,callbackId);
                                                           }
                                                       }
                                                   });
                                               }
                                            }
                                        });
                                    } else {
                                        ToastUtil.toast(AppConfig.getContext(), AppConfig.getContext().getString(R.string.toast_order_password_incorrect));
                                    }
                                }
                            }
                        }, mCurrentWallet.whash, "transaction").show();
                    }
                });
                break;
            default:
                Log.e(TAG, "callHandler: no such method : "+methodName);
                break;
        }
    }

    private void notifySuccessResult(GsonUtil data,String callbackId){
        GsonUtil result = new GsonUtil("{}");
        result.putBoolean("result", true);
        result.put("data", data);
        result.putString("msg", MSG_SUCCESS);
        this.mAgentWeb.getJsAccessEntrace().callJs("javascript:" + callbackId + "('" + result.toString() + "')");
    }

    private void notifyFailedResult(String data,String callbackId){
        GsonUtil result = new GsonUtil("{}");
        result.putBoolean("result", false);
        result.putString("err", data);
        result.putString("msg", MSG_FAILED);
        this.mAgentWeb.getJsAccessEntrace().callJs("javascript:" + callbackId + "('" + result.toString() + "')");
    }

    private void notifySuccessResult(String data,String callbackId){
        GsonUtil result = new GsonUtil("{}");
        result.putBoolean("result", true);
        result.putString("data", data);
        result.putString("msg", MSG_SUCCESS);
        this.mAgentWeb.getJsAccessEntrace().callJs("javascript:" + callbackId + "('" + result.toString() + "')");
    }


    private Spanned formatHtml() {
        String paysH = "<font color=\"#3B6CA6\">" + mValue + " </font>";
        String paysCurH = "<font color=\"#021E38\">" + mToken + " </font>";
        return Html.fromHtml(paysH.concat(paysCurH));
    }
}
